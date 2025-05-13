🛍️ E-Commerce Web Application ( cartify )
Built with Spring Boot, Thymeleaf, HTML/CSS, and Bootstrap

A robust and full-featured e-commerce platform that enables users to browse and purchase products with ease, while offering admins powerful tools to manage the catalog, orders, and users. Integrated with Stripe for secure payments and JavaMailSender for order notifications.

🚀 Features Overview

------- 👨‍💼 User Panel -------

🔐 User Registration & Login (Secure with Spring Security)

🔍 Search & Paginate Products

🛒 Manage Cart (Add / Update / Remove)

📦 Track Orders

📧 Email Confirmation on Order Placement


💳 Place Orders:

~ Cash On Delivery (COD)

~ Online Payment via Stripe



------- 🛠️ Admin Panel -------

🧑‍💼 Role-based Access Control (Admin / User)

🗂️ Category Management (with discount support)

📦 Product Management (Create / Update / Delete)


📋 View & Manage All Orders

~ ✅ Update Order Status (Placed → Shipped → Delivered)

~ 📧 Auto Email Notification to Users



🌐 Live Demo
🔗 Visit the App on Render

<sub> https://ecommerce-render-2.onrender.com </sub>


📸 Screenshots
User Dashboard
Admin Dashboard
"C:\Users\KARAN PATEL\Pictures\Screenshots\Screenshot 2025-05-13 214652.png"
Product Page


💻 Tech Stack

⚙️ Layer	                            🔧 Technologies Used

👨‍🎨 Frontend                             Thymeleaf,	HTML, CSS, Bootstrap

👨‍💻 Backend	                            Java 17, Spring Boot, Spring Data JPA

🔒 Security                            	Spring Security (Role-Based Auth)

🗄️ Database	                            MySQL (Cloud Hosted via Aiven)

💰 Payment	                            Stripe API

📬 Email	                              JavaMailSender

🚀 Deployment	                          Render (for backend) + Aiven (for DB)



📦 Ecommerce-render/
├── 📁 backend/              # Spring Boot application
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   └── service/
├── 📁 frontend/             # HTML, CSS, JS, Bootstrap views
│   ├── index.html
│   ├── product-list.html
│   ├── cart.html
│   └── admin-dashboard.html
├── 📄 application.properties
└── 📄 README.md
