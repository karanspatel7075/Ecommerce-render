# 🛒 Cartify — E-Commerce Backend System

[![Java](https://img.shields.io/badge/Java-21-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen)]()
[![MySQL](https://img.shields.io/badge/MySQL-Aiven-blue)]()
[![Stripe](https://img.shields.io/badge/Payments-Stripe-635BFF)]()
[![Deploy](https://img.shields.io/badge/Deploy-Render-46E3B7)]()

<img width="1536" height="1024" alt="ChatGPT Image Aug 7, 2026, 02_00_37 AM" src="https://github.com/user-attachments/assets/274fd3c0-00b1-484b-b8c8-14859be58df5" />

**Cartify** is a full-featured e-commerce backend built with Spring Boot and server-rendered Thymeleaf views. It supports a complete shopping flow — browse, cart, checkout — for users, and a full catalog/order management dashboard for admins, with dual payment paths (Cash on Delivery and Stripe) and email notifications at every order-status change.

This project focuses on the parts of e-commerce backends that are easy to get subtly wrong: **role separation, pagination at scale, order-state transitions, and keeping payment state consistent with order state.**

🔗 **Live Demo:** [ecommerce-render-2.onrender.com](https://ecommerce-render-2.onrender.com)

---

## 📑 Table of Contents

- [Problem Statement](#-problem-statement)
- [System Design](#-system-design)
- [Core Design Decisions](#-core-design-decisions)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Data Model](#-data-model)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [What This Project Demonstrates](#-what-this-project-demonstrates)
- [Roadmap](#-roadmap)
- [Author](#-author)

---

## 🎯 Problem Statement

An e-commerce platform needs to serve two very different users from one codebase:

1. **Shoppers**, who need a fast, simple browse → cart → checkout flow, with confidence that their order and payment actually went through.
2. **Admins**, who need to manage a growing product catalog, moderate accounts, and track orders through their lifecycle — without the catalog or order tables becoming unusable as they grow.

The harder problem underneath both: **keeping order state and payment state honest.** An order shouldn't be silently lost if a payment fails, and a customer shouldn't be charged for an order that was never actually recorded. Cartify's design is built around getting that sequencing right, and being explicit about where it currently isn't (see [Roadmap](#-roadmap)).

---

## 🏗 System Design

### Layered MVC, role-routed

```
Client Browser (User storefront / Admin panel)
        │  HTTP + session-based auth
        ▼
Spring Security  →  routes by role (ROLE_USER / ROLE_ADMIN)
        │
        ▼
Controllers (Home / User / Admin / Payment Gateway)
        │
        ▼
Service Layer (Product, Category, Cart, Order, Stripe, Email)
        │
        ▼
Repository Layer (Spring Data JPA)
        │
        ▼
MySQL (Aiven) — Users, Categories, Products, Cart, Orders
```

### Checkout Flow

```
User places order (COD or Online)
        │
        ▼
ProductOrder saved in MySQL  ◄── order record created before payment is confirmed
        │
        ├── COD ──────────────────────────────► Cart cleared → confirmation email → success page
        │
        └── Online ──► StripeService creates a checkout session
                              │
                              ▼
                    User redirected to Stripe's hosted payment page
```

### Admin Order-Status Flow

```
Admin updates order status (Placed → Shipped → Delivered)
        │
        ▼
OrderService updates ProductOrder.status
        │
        ▼
CommonUtil sends a status-update email via JavaMailSender
        │
        ▼
User sees updated status next time they check "My Orders"
```

---

## 🧩 Core Design Decisions

| Decision | Why |
|---|---|
| **Server-rendered Thymeleaf over a separate SPA + API** | For a project this size, a unified session-based auth model across both the user storefront and admin panel is simpler to reason about and ship than maintaining a separate frontend, a token refresh flow, and CORS configuration. |
| **Pagination on every admin listing (`Page<T>`)** | Categories, products, and orders are all fetched via `Page<T>` repository methods rather than loading full lists into memory. This matters the moment the catalog grows past a few hundred rows — the code was written for the scale it will actually reach, not just the scale it launched at. |
| **Role-based routing at the Spring Security layer, not inside controllers** | `/admin/**` is gated by role before a request ever reaches `AdminController`. Keeping authorization at the security-filter layer (rather than `if (user.isAdmin())` checks scattered through methods) means a missed check in one controller method can't accidentally expose admin functionality. |
| **Order persisted before payment confirmation** | This was a deliberate simplification: the order record exists as soon as the user commits to checkout, so nothing is lost if Stripe's redirect fails midway. The trade-off — and the honest gap — is that there's currently no reconciliation step if the Stripe session is abandoned or fails; see [Roadmap](#-roadmap). |
| **`@ModelAttribute`-based shared data loading** | Cart count and category list, needed on nearly every page, are loaded once via a `@ModelAttribute` method rather than repeated in every controller action. Keeps the "what does every page need" logic in one place per controller. |

---

## 🚀 Features

### 👤 User Storefront
- Secure registration & login (Spring Security)
- Product browsing with pagination and search
- Cart management — add, update quantity, remove
- Checkout via **Cash on Delivery** or **Stripe**
- Order history and status tracking
- Email confirmation on order placement
- Forgot-password flow with tokenized reset links

### 🛠 Admin Panel
- Role-based access control (Admin / User)
- Category management (create, update, delete, activate/deactivate)
- Product management with image upload and discount pricing
- Order management dashboard with pagination
- Order status lifecycle: **Placed → Shipped → Delivered**
- Automatic email notification to the user on every status change
- User account management (activate/deactivate, view by role)

### 💳 Payments
- Stripe-hosted checkout session for online payments
- Cash-on-delivery as a first-class alternative path

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Language / Runtime** | Java 21 |
| **Framework** | Spring Boot 3.4.2, Spring MVC |
| **Security** | Spring Security, BCrypt password encoding |
| **Persistence** | Spring Data JPA, Hibernate |
| **Database** | MySQL (Aiven Cloud) |
| **Payments** | Stripe Java SDK |
| **Email** | Spring Boot Starter Mail (JavaMailSender) |
| **Frontend (server-rendered)** | Thymeleaf, HTML/CSS, Bootstrap |
| **Tooling** | Lombok, Maven |
| **Deployment** | Render (app) + Aiven (database) |

---

## 🗃 Data Model

```
UserDtl (id, name, email, password, role, address, city, state,
         pincode, mobileNumber, profileImage, resetToken, isActive)

Category (id, name, imageName, isActive)

AddProduct (id, name, price, discount, discountedPrice,
            category, image, isActive)

Cart (id, product, user, quantity, totalPrice)

ProductOrder (id, orderId, user, status[Placed|Shipped|Delivered|...],
              createdAt)
```

Key relationships: `Cart` is scoped per `UserDtl`; `ProductOrder` is created from the cart contents at checkout time, independent of whether payment (Stripe) has actually succeeded yet — see the checkout-flow note above.

---

## 📂 Project Structure

```
src/main/java/com/E_commerce/Shopping_Cart
├── Controller       # HomeController, UserController, AdminController, Payment_Gateway
├── model            # UserDtl, Category, AddProduct, Cart, ProductOrder
├── repository        # Spring Data JPA repositories
├── service            # Product, Category, User, Cart, Order, Stripe services
└── util               # CommonUtil (email, URL generation), OrderStatus enum
```

---

## ⚙️ Getting Started

### Prerequisites
- Java 21, Maven
- MySQL instance (or Aiven cloud MySQL)
- Stripe account (test API keys)
- SMTP credentials for JavaMailSender

### Environment Configuration

Set the following via `application.properties` or environment variables:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

stripe.api.key=${STRIPE_SECRET_KEY}
```

### Run

```bash
./mvnw clean package -DskipTests
java -jar target/Shopping_Cart-0.0.1-SNAPSHOT.jar
```

---

## 🧠 What This Project Demonstrates

- Role-based access control enforced at the security-filter layer
- Pagination-first design for admin data views (categories, products, orders)
- A real checkout flow with two distinct payment paths, not just a single happy path
- Order lifecycle management with user-facing email notifications at each transition
- Session-scoped cart and shared page data via `@ModelAttribute`

---

## 📈 Roadmap

- **Reconcile Stripe payment status with the order record** — currently the order is saved before Stripe confirms payment, with no webhook to mark it failed/abandoned if the user never completes checkout. A Stripe webhook updating `ProductOrder.status` would close this gap.
- **Move image storage off local disk** — product, category, and profile images are currently saved to the app's local filesystem. On Render (and most PaaS platforms), local disk is ephemeral — uploaded images can be lost on redeploy. Moving to Cloudinary or S3 (as done in a later project, GNNS) is the fix.
- **Clear session flash attributes after read** — `Success`/`errorMsg` session attributes are currently set but never explicitly removed after the page reads them, which can let a stale message resurface on a later page load. Spring's `RedirectAttributes` (flash attributes) handle this automatically and would be a clean swap.
- **Extract payment-method branching into a strategy** — COD vs. Stripe is currently an if/else inside the order controller; pulling it into a pluggable strategy (same idea used for driver-matching in a later ride-sharing project) would make adding a third payment method a lot less invasive.
- **Add a proper `OrderStatus` state machine** — status transitions are currently driven by a raw string parameter; validating legal transitions (e.g., can't go from `Delivered` back to `Placed`) would catch bad admin input at the service layer instead of trusting the request.

---

## 👨‍💻 Author

**Karan Patel**
Backend Developer | Java · Spring Boot · System Design

GitHub: [github.com/karanspatel7075](https://github.com/karanspatel7075)

---

### ⭐ If you found this useful, consider starring the repo!
