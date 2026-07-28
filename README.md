# 📝 Smart Notes

> A modern full-stack cloud-based note management application built with Spring Boot, MySQL, JWT Authentication, and Vanilla JavaScript.

<p align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.2-green?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-Aiven-blue?style=for-the-badge)
![JWT](https://img.shields.io/badge/Auth-JWT-red?style=for-the-badge)
![Render](https://img.shields.io/badge/Backend-Render-purple?style=for-the-badge)
![Vercel](https://img.shields.io/badge/Frontend-Vercel-black?style=for-the-badge)

</p>

---

# 📖 Overview

Smart Notes is a cloud-based full-stack note management application that allows users to securely manage personal notes with JWT authentication.

The application is built using Spring Boot for the backend, MySQL (Aiven) as the cloud database, and a responsive HTML/CSS/JavaScript frontend deployed on Vercel.

---

# ✨ Features

✅ User Authentication

- Register
- Login
- JWT Authentication

✅ Notes

- Create Note
- Read Notes
- Update Note
- Delete Note

✅ Search

- Search notes instantly

✅ Categories

- Personal
- Work
- Ideas
- Others

✅ Dashboard

- Total Notes
- Recent Notes
- Category Statistics

✅ Cloud Deployment

- Frontend → Vercel
- Backend → Render
- Database → Aiven MySQL

---

# 🛠 Tech Stack

| Technology | Usage |
|------------|-------|
| Java 17 | Backend |
| Spring Boot | REST API |
| Spring Data JPA | ORM |
| Spring Security | Authentication |
| JWT | Authorization |
| MySQL | Database |
| Aiven | Cloud Database |
| HTML | Frontend |
| CSS | Styling |
| JavaScript | Client-side |
| Render | Backend Hosting |
| Vercel | Frontend Hosting |

---

# 🏗 Architecture

```text
        User
          │
          ▼
    Vercel Frontend
          │
 REST API (HTTPS)
          │
          ▼
 Spring Boot (Render)
          │
 Hibernate + JPA
          │
          ▼
  Aiven MySQL Database
```

---

# 📂 Project Structure

```
Smart-Notes
│
├── backend
│   ├── controller
│   ├── service
│   ├── repository
│   ├── model
│   ├── security
│   └── config
│
├── frontend
│   ├── css
│   ├── js
│   ├── images
│   └── index.html
│
└── README.md
```

---

# 🚀 Installation

## Clone Repository

```bash
git clone [https://github.com/manikanta-pasupuleti/Smart-Notes]
```

## Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

## Frontend

Simply open

```
frontend/index.html
```

or use Live Server.

--- [https://vercel.com/manikanta-pasupuletis-projects/smart-notes-frontend]

# 🔐 Environment Variables

```properties
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

JWT_SECRET=
```

---

# 🌐 API Endpoints

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /api/auth/register | Register |
| POST | /api/auth/login | Login |
| GET | /api/notes | Get Notes |
| POST | /api/notes | Create Note |
| PUT | /api/notes/{id} | Update Note |
| DELETE | /api/notes/{id} | Delete Note |

---

# 🗄 Database

### Users

- id
- username
- email
- password
- created_at
- updated_at

### Notes

- id
- title
- content
- category
- created_at
- updated_at

---

# 🚀 Deployment

| Service | Platform |
|----------|----------|
| Frontend | Vercel |
| Backend | Render |
| Database | Aiven MySQL |

---

# 📸 Screenshots

> Add screenshots here

- Login Page <img width="1897" height="923" alt="image" src="https://github.com/user-attachments/assets/74d650e3-2cf0-4d5c-bf5a-f048951d1f7c" />

- Dashboard  <img width="338" height="833" alt="image" src="https://github.com/user-attachments/assets/20d9396b-ca42-4666-9a0b-19e7bcec2233" />


- Notes    <img width="1747" height="902" alt="image" src="https://github.com/user-attachments/assets/776a1458-b9b2-42a1-a0b2-491da3bf7fb5" />
- Search   <img width="1731" height="122" alt="image" src="https://github.com/user-attachments/assets/de9a5d09-59a0-4faf-80fe-a8cb5b36ed40" />

- Statistics   

---

# 🔮 Future Improvements

- Rich Text Editor
- File Attachments
- Note Sharing
- Archive Notes
- Favorite Notes
- Email Verification
- Password Reset
- Docker Support

---

# 👨‍💻 Author

**Manikanta Pasupuleti**

GitHub:
https://github.com/manikanta-pasupuleti/Smart-Notes

---

# ⭐ If you like this project

Please consider giving it a ⭐ on GitHub.
