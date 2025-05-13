# 💸 Finance Control

A personal finance management system built with Java and Spring Boot.

## 🧹 Features

* ✅ User registration and login with JWT authentication
* ✅ Create, read, update, and delete revenues and expenses
* ✅ Automatic balance calculation
* ✅ Protected routes using Spring Security
* 🔒 Token validation

## ⚙️ Technologies

* Java 17+
* Spring Boot
* Spring Security
* JWT (Auth0)
* Lombok
* PostgreSQL
* Maven
* Git

## 🚀 How to Run

### Prerequisites

* Java 17+
* PostgreSQL
* Maven

### Steps

1. Clone the repository:

```bash
git clone https://github.com/Alt3rs/finance-control.git
cd finance-control
```

2. Configure `application.properties` or `application.yml` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance-control
spring.datasource.username=your_username
spring.datasource.password=your_password

api.security.token.secret=your_secret_key
```

3. Run the project:

```bash
./mvnw spring-boot:run
```

## 🧪 API Endpoints (e.g., for Postman)

* `POST /auth/register`: Register a new user
* `POST /auth/login`: User login
* `POST /auth/validate`: Validate token
* `POST /activities`: Create activity (revenue or expense)
* `GET /activities?userId=ID`: List activities by user
* `GET /activities/balance?userId=ID`: Get balance
* `PUT /activities/{id}`: Update activity
* `DELETE /activities/{id}?userId=ID`: Delete activity

## 🔒 Security

Routes are protected using JWT. Include the token in the request header:

```http
Authorization: Bearer YOUR_TOKEN
```

## 👨‍💼 Author

* Name: Pedro Henrique Rios de Souza
* GitHub: [@Alt3rs](https://github.com/Alt3rs)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/Alt3rs/finance-control/blob/main/LICENSE) file for details.
