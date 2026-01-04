# 🏦 Banking Application – Spring Boot

A secure **Banking REST API** built using **Spring Boot**, **JWT Authentication**, **Refresh Token**, and **Role-Based Access Control**.

---

## 🚀 Features
- User Registration & Login
- JWT Access Token
- Refresh Token with Rotation
- Role-based Authorization (USER, ADMIN)
- Account Ownership Validation
- Deposit, Withdraw, Transfer Money
- Transaction History (View & Download)
- Secure Logout
- Swagger API Docs

---

## 🧱 Tech Stack
- Java 17
- Spring Boot
- Spring Security
- JWT (Access + Refresh Token)
- Spring Data JPA / Hibernate
- MySQL
- Maven

---

## 🔐 Authentication
- **Access Token** → short-lived JWT  
- **Refresh Token** → stored in DB (1 per user)  
- **Token Rotation** → old refresh token deleted on refresh  

---

## 📦 API Endpoints

### Public APIs
- `POST /bankingapp/register`
- `POST /bankingapp/login`
- `POST /bankingapp/refresh-token`

### Authenticated APIs
- `GET /bankingapp/me`
- `GET /bankingapp/get_account_details_by_account_number/{accountNumber}`
- `PUT /bankingapp/deposit_money/{accountNumber}`
- `PUT /bankingapp/withdraw_money/{accountNumber}`
- `POST /bankingapp/transfer_money/{toAccount}`
- `GET /bankingapp/show_transaction_history/{accountNumber}`
- `GET /bankingapp/download_transaction_history/{accountNumber}`
- `POST /bankingapp/logout`

### Admin APIs
- `POST /bankingapp/create_account`
- `PUT /bankingapp/update_account/{accountNumber}`
- `DELETE /bankingapp/delete_account/{accountNumber}`
- `PUT /bankingapp/admin/assign-role`

---

## 🛡️ Security Rules
- USER → access only own account
- ADMIN → access all accounts
- Ownership enforced using `@PreAuthorize`

---

## 📘 Swagger
http://localhost:9091/swagger-ui.html


---

## 🗄️ Database Tables
- register_user
- register_user_roles
- accounts
- transactions
- refresh_token

---

## 👨‍💻 Author
**Kabita Kumari** – Java Backend Engineer
