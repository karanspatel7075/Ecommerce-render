# 🛍️ Cartify – E-Commerce Web Application

Cartify is a **full-featured e-commerce web application** built using **Spring Boot, Thymeleaf, HTML/CSS, and Bootstrap**.  
It provides a smooth shopping experience for users and a powerful admin panel for managing products, categories, orders, and users.

The platform supports **secure authentication**, **online payments via Stripe**, **order notifications via email**, and a **scalable backend architecture**.

---

## 🚀 Features Overview

---

### 👨‍💼 User Panel

🔐 Secure User Registration & Login (Spring Security)  
🔍 Product Search with Pagination  
🛒 Cart Management (Add / Update / Remove Products)  
📦 Order Placement & Tracking  
📧 Email Confirmation on Order Placement  

💳 Payment Options:
- Cash On Delivery (COD)
- Online Payment via Stripe

---

### 🛠️ Admin Panel

🧑‍💼 Role-Based Access Control (Admin / User)  
🗂️ Category Management (Create, Update, Delete)  
📦 Product Management (CRUD Operations)  
📋 Order Management Dashboard  

Order Lifecycle Management:
- ✅ Placed  
- 🚚 Shipped  
- 📦 Delivered  

📧 Automatic Email Notifications to Users on Order Updates

---

## 🌐 Live Demo

🔗 **Visit the Application:**  
https://ecommerce-render-2.onrender.com

---

## 💻 Tech Stack

| Layer | Technologies Used |
|------|------------------|
| 🎨 Frontend | Thymeleaf, HTML, CSS, Bootstrap |
| 💻 Backend | Java 17, Spring Boot |
| 🧩 ORM | Spring Data JPA, Hibernate |
| 🔒 Security | Spring Security (Role-Based Authentication) |
| 🗄️ Database | MySQL (Cloud Hosted via Aiven) |
| 💰 Payments | Stripe API |
| 📬 Email | JavaMailSender |
| 🚀 Deployment | Render (Backend), Aiven (Database) |

---

## 🔑 Backend Features & Core Concepts

- **Spring Boot** – Rapid application development & auto-configuration  
- **Spring IOC Container** – Bean lifecycle and dependency management  
- **Dependency Injection** – Loose coupling and scalable design  
- **Spring MVC Architecture** – Controller → Service → Repository pattern  
- **Spring Data JPA** – Repository-based database interactions  
- **Hibernate ORM** – Object-relational mapping  
- **Lombok** – Reduced boilerplate code using annotations  

---

## 🛡️ Security & Authentication

- Spring Security for API protection  
- Role-Based Access Control (ADMIN / USER)  
- Encrypted passwords using **BCrypt**  
- Secure login & session management  

---

## 📧 Email Notifications

- Order placement confirmation  
- Order status updates  
- JavaMailSender integration  

---

## 🗄️ Project Structure

```bash
Cartify/
├── 📁 backend/
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── 📁 frontend/
│   ├── index.html
│   ├── product-list.html
│   ├── cart.html
│   └── admin-dashboard.html
│
├── 📄 application.properties
└── 📄 README.md
