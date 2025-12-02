# Card Tokenization API

A secure and scalable card tokenization service built with **Spring Boot 3.2.6**, **Spring Security**, and **JPA**.  
This API allows hashing, storing, validating, and retrieving tokenized card information using HMAC-SHA256.

---

## 🚀 Features

- Tokenization using **HMAC-SHA256**
- Secure lookup key from environment variables
- Validation endpoint for card existence + caching with redis
- Retrieval of card ID by hash
- Spring Boot 3.2.6 modern architecture
- JPA + MySQL
- Clear separation of **Controller**, **Service**, and **Repository**

---

## 📂 Project Structure

```
src/main/java/br.com.hyperativa
 ├── config
 ├── controller
 ├── exception
 ├── model
 ├── repository
 ├── service
 └── util
    
```

---

## 🔧 Technologies

- Java 17+
- Spring Boot 3.2.6
- Spring Web
- Spring Data JPA
- Spring Security (minimal)
- HMAC-SHA256 (Java Crypto)
- Redis
- MySql

---

## 🔑 Environment Variables

This project uses environment variables to manage sensitive information like database credentials, JWT secrets, and encryption keys. This ensures that no sensitive data is hardcoded in the application.properties file.

Create a .env file in the root of the project with the following variables:

| Variable      | Value                                                                     |
|---------------|---------------------------------------------------------------------------|
| `DB_URL`      | jdbc:mysql://localhost:3306/challangetest?useSSL=false&serverTimezone=UTC |
| `DB_USER`     | root                                                                      |
| `DB_PASSWORD` | root                                                                      |
| `SERVER_PORT` | 8081                                                                      |
| `JWT_SECRET`  | jwt_secret_here                                                           |
| `JWT_EXP_MS`  | 3600000                                                                   |
| `KEY_LOOKUP`  | _lookup_key_here                                                          |
| `KEY_ENC`     | your_encryption_key_here                                                  |

> **Important:**  This `.env` file should never be committed to version control.

---

## ▶️ How to Run

### **1. Clone the repository**
```bash
git https://github.com/jonathandenisads/challange-hyperativa.git
cd challange-hyperativa
```
## ⚙️ Setup Database
Before running the project, you need to create the database.

2. Create a MySQL database with the following credentials:

    - **Database Name:** `challangetest`
    - **Username:** `root`
    - **Password:** `root`


### **2. Run with Maven**
```bash
mvn spring-boot:run
```
```
 Default port: 8081
```
---

## 📘 Endpoints

### **Authenticator JWT Login**
1. Call the login endpoint:

`POST /auth/login`

`Content-Type: application/json`

Body:
```json
{
  "username": "admin",
  "password": "password"
}
```

Response:
```json
{
  "hash": "a94c...f02"
}
```
>Note: This token must be included in the Authorization header for all protected endpoints:
Authorization: Bearer <JWT_TOKEN_HERE>
---

### **Insert Manual Card**
`POST /cards` - Insert a manual Card (requires JWT token)

`Content-Type: application/json`

`Authorization: Bearer <JWT_TOKEN_HERE>`

Body:
```json
{
  "cardNumber": "4456897000000014"
}
```
Response:
```json
{
  "id": "9607a653-4e4d-4fe1-b2dd-4900470245fe"
}
```
>Without a valid token, requests return 401 Unauthorized or 403 Forbidden.
---
 ### **Upload Card File**
`POST /cards` -  Upload a card file (requires JWT token)

`Content-Type: multipart/form-data`

`Form-Data Key: file`

`Form-Data Value: .txt file`


Response:
```json
{
  "rejected": 8,
  "inserted": 1000
}
```
>Without a valid token, requests return 401 Unauthorized or 403 Forbidden.

---

### **Check if Card Exists**
`POST /cards/exists`- Check if Card Exists (requires JWT token)
`Authorization: Bearer <JWT_TOKEN_HERE>`

`Content-Type: multipart/form-data`

Body:
```json
{
  "cardNumber": "445689799999999656"
}
```
Response:
```json
{
  "exists": true,
  "id": "9607a653-4e4d-4fe1-b2dd-4900470245fe"
}
```
>Without a valid token, requests return 401 Unauthorized or 403 Forbidden.
> 
> 🛡️ Note:
Although this endpoint performs a lookup, it uses POST instead of GET to avoid exposing sensitive card numbers in URLs, logs, browser history, and monitoring tools.

---

## 🧪 Tests

Run:

```bash
mvn test
```

---
## 🧪 Redis Setup

Option 1 — Using Docker (recommended)
Run:

```bash
docker run --name redis-hyperativa -p 6379:6379 -d redis
```

Option 2 — Local installation (Linux/Mac)
Run:

```bash
sudo apt update
sudo apt install redis-server
redis-server
```
>Redis is used for caching card validations to improve performance.
---

## 📄 License

MIT License — Feel free to modify and use.

---

## ✨ Author

Developed by **Jonathan Denis Rufino da Costa**
