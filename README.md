# 🔐 Сервис аутентификации пользователей

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring](https://img.shields.io/badge/Spring-6.x-green.svg)](https://spring.io/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.x-blue.svg)](https://hibernate.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7.x-red.svg)](https://redis.io/)

**Сервис аутентификации пользователей** — это приложение для управления пользователями и их аутентификацией с использованием JWT (JSON Web Tokens). Проект реализован на Java 17 с использованием Spring Boot, Hibernate, PostgreSQL и Redis.

---

## 🚀 Основные функции

- **Регистрация пользователей**:
  - Локальная регистрация (email, username, password).
  - Регистрация через OAuth (email, username, access token).
- **Аутентификация пользователей**: Генерация JWT-токенов для авторизованных пользователей.
- **Хранение токенов**: JWT-токены хранятся в Redis для быстрого доступа и проверки.
- **Управление пользователями**: Получение информации о пользователях, обновление данных и удаление учетных записей.
- **Роли пользователей**: Поддержка различных ролей (например, USER, ADMIN).

---

## 🛠️ Стек технологий

- **Язык программирования**: Java 17
- **Фреймворк**: Spring Boot 3.x
- **ORM**: Hibernate 6.x
- **База данных**: PostgreSQL 15
- **Кэширование**: Redis 7.x
- **Библиотека для JWT**: jjwt
- **Сборка**: Gradle

---

## 📚 API Документация

### Регистрация пользователя

#### Локальная регистрация

**Запрос**:
```http
POST /register/local
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "username": "john_doe",
  "password": "password123",
  "number": "+1234567890"
}
```

**Ответ**:
```json
{
  "message": "Local user registered successfully"
}
```

#### Регистрация через OAuth

**Запрос**:
```http
POST /register/oauth
Content-Type: application/json

{
  "nameProvider": "google",
  "email": "john.doe@example.com",
  "username": "john_doe",
  "accessToken": "ya29.a0AfH6SMC..."
}
```

**Ответ**:
```json
{
  "message": "OAuth user registered successfully"
}
```

### Аутентификация пользователя

**Запрос**:
```http
POST /login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Ответ**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Выход из системы

**Запрос**:
```http
POST /login/logout
Content-Type: application/json

{
  "username": "john_doe"
}
```

**Ответ**:
```json
{
  "message": "User logged out successfully"
}
```

### Получение токена

**Запрос**:
```http
GET /login/token?username=john_doe
```

**Ответ**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
---

## 👨‍💻 Автор

- **YouRB1T**
- GitHub: [YourB1T](https://github.com/YourB1T)

---

Если у вас есть вопросы или предложения, создайте [issue](https://github.com/YourB1T/authentication-service/issues) или свяжитесь со мной.
