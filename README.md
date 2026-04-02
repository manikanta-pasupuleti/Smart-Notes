# Smart Notes 📝

A full-stack note-taking web application built with **Spring Boot** and **Vanilla JavaScript**. Smart Notes lets users register, log in, and manage personal notes with rich organization features — all backed by a RESTful API with JWT-based authentication.

---

## Features

- **Authentication** – Register and log in with JWT-based session management
- **CRUD Operations** – Create, read, update, and delete notes
- **Categories** – Organize notes into General, Study, Personal, and Work
- **Color Labels** – Tag notes with one of seven colors for quick visual scanning
- **Pin Notes** – Keep important notes at the top
- **Trash & Restore** – Soft-delete notes to the trash bin; restore or permanently delete them
- **Duplicate Notes** – Clone any note with a single click
- **Full-Text Search** – Search notes by title or content in real time
- **Sort & Filter** – Sort by newest, oldest, or title; filter by category
- **Statistics** – See counts of total, pinned, trashed, and per-category notes
- **JSON Export** – Download all active notes as a `smart-notes-export.json` file
- **Dark / Light Mode** – Toggle themes, persisted in `localStorage`
- **Responsive Design** – Works on desktop and mobile
- **Keyboard Shortcuts** – `Ctrl+N` (new note), `Ctrl+F` (search), `Esc` (close modal)

---

## Intended Users

Anyone who wants a lightweight, self-hosted note-taking app — students, developers, or knowledge workers who prefer owning their data without relying on a third-party SaaS tool.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2.0 |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL (tested with [Aiven](https://aiven.io/) hosted MySQL) |
| Authentication | JWT (JJWT 0.12.3, HS512) |
| Password Hashing | SHA-256 with salt (10,000 iterations) |
| Frontend | Vanilla HTML5, CSS3, JavaScript (ES6+) |
| Build | Apache Maven 3.8+ |
| Containerization | Docker (multi-stage build) |

---

## Project Structure

```
Smart-Notes/
├── Dockerfile                   # Multi-stage Docker build
├── backend/
│   ├── pom.xml                  # Maven project descriptor
│   └── src/main/
│       ├── java/com/smartnotes/
│       │   ├── SmartNotesApplication.java   # Entry point
│       │   ├── controller/
│       │   │   ├── AuthController.java      # /api/auth/*
│       │   │   └── NoteController.java      # /api/notes/*
│       │   ├── model/
│       │   │   ├── Note.java
│       │   │   └── User.java
│       │   ├── repository/
│       │   │   ├── NoteRepository.java
│       │   │   └── UserRepository.java
│       │   ├── service/
│       │   │   └── NoteService.java
│       │   ├── security/
│       │   │   ├── JwtTokenProvider.java
│       │   │   └── PasswordUtil.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   └── DataInitializer.java         # Seeds sample notes on first run
│       └── resources/
│           └── application.properties.example
└── frontend/
    ├── index.html               # Main notes UI
    ├── login.html               # Login / Register UI
    ├── app.js                   # Frontend application logic
    └── style.css                # Theming and responsive styles
```

---

## Prerequisites

### Without Docker

| Requirement | Minimum version |
|---|---|
| JDK | 17 |
| Apache Maven | 3.8 |
| MySQL | 8.0 (or a cloud-hosted instance, e.g., Aiven) |

### With Docker

| Requirement | Notes |
|---|---|
| Docker Engine | 20.10+ |
| A running MySQL instance | Docker does **not** bundle a database; see step 2 below |

---

## Configuration

The backend reads its configuration from `backend/src/main/resources/application.properties`.  
A template is provided at `application.properties.example`.

**Steps:**

1. Copy the example file:
   ```bash
   cp backend/src/main/resources/application.properties.example \
      backend/src/main/resources/application.properties
   ```

2. Fill in your MySQL connection details:

   ```properties
   # Server
   server.port=8080

   # MySQL connection (Aiven or any MySQL 8 instance)
   spring.datasource.url=jdbc:mysql://<HOST>:<PORT>/defaultdb?sslMode=REQUIRED
   spring.datasource.username=<DB_USER>
   spring.datasource.password=<DB_PASSWORD>
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

   # JPA / Hibernate
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true

   # App name
   spring.application.name=smart-notes
   ```

3. *(Optional)* Override JWT settings via the same file or environment variables:

   | Property | Default | Description |
   |---|---|---|
   | `jwt.secret` | `your-super-secret-key-change-this-in-production-please` | Signing secret – **change this in production** |
   | `jwt.expiration` | `86400000` | Token lifetime in milliseconds (default: 24 hours) |

> **Security note:** Never commit `application.properties` with real credentials. The file is already listed in `.gitignore`.

---

## Local Run (without Docker)

1. **Clone the repository**
   ```bash
   git clone https://github.com/manikanta-pasupuleti/Smart-Notes.git
   cd Smart-Notes
   ```

2. **Configure the application** – follow the [Configuration](#configuration) section above.

3. **Build the backend JAR**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

4. **Start the backend**
   ```bash
   java -jar target/smart-notes-1.0.0.jar
   ```
   The API is now available at `http://localhost:8080`.

5. **Open the frontend**  
   Open `frontend/login.html` in your browser (e.g., via the file system or a simple static server).  
   The frontend calls the API at `http://localhost:8080` by default — no separate build step is required.

   To serve the frontend with Python's built-in HTTP server:
   ```bash
   cd frontend
   python3 -m http.server 3000
   ```
   Then visit `http://localhost:3000/login.html`.

---

## Docker Run

The provided `Dockerfile` builds the Spring Boot backend only. You still need an external MySQL instance (see [Configuration](#configuration)).

1. **Build the Docker image** (from the repository root):
   ```bash
   docker build -t smart-notes-backend .
   ```

2. **Run the container**, passing your database credentials as environment variables:
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL="jdbc:mysql://<HOST>:<PORT>/defaultdb?sslMode=REQUIRED" \
     -e SPRING_DATASOURCE_USERNAME="<DB_USER>" \
     -e SPRING_DATASOURCE_PASSWORD="<DB_PASSWORD>" \
     -e JWT_SECRET="<your-strong-secret>" \
     smart-notes-backend
   ```

3. **Open the frontend** as described in step 5 of the local run section above (the frontend is a set of static files and is not containerized).

---

## API Reference

All endpoints are prefixed with `/api`.

### Authentication – `/api/auth`

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Log in and receive a JWT |
| `GET` | `/api/auth/validate` | Validate a Bearer token |

### Notes – `/api/notes`

All note endpoints require an `Authorization: Bearer <token>` header.

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/notes` | List active notes (`?sort=newest`, `oldest`, or `title`) |
| `POST` | `/api/notes` | Create a note |
| `GET` | `/api/notes/{id}` | Get a single note |
| `PUT` | `/api/notes/{id}` | Update a note |
| `DELETE` | `/api/notes/{id}` | Soft-delete (move to trash) |
| `GET` | `/api/notes/trash` | List trashed notes |
| `PATCH` | `/api/notes/{id}/restore` | Restore from trash |
| `DELETE` | `/api/notes/{id}/permanent` | Permanently delete |
| `POST` | `/api/notes/{id}/duplicate` | Duplicate a note |
| `PATCH` | `/api/notes/{id}/pin` | Toggle pin |
| `GET` | `/api/notes/search?keyword=` | Full-text search |
| `GET` | `/api/notes/category/{category}` | Filter by category |
| `GET` | `/api/notes/stats` | Get statistics |
| `GET` | `/api/notes/export` | Download notes as JSON |

---

## Build & Test

### Build

```bash
cd backend
mvn clean package          # compiles, runs tests, packages JAR
mvn clean package -DskipTests  # skip tests for a faster build
```

The JAR is output to `backend/target/smart-notes-1.0.0.jar`.

### Tests

No automated tests are currently included in the repository. The Spring Boot test dependency (`spring-boot-starter-test`) is present in `pom.xml`, so JUnit 5 / Mockito tests can be added under `backend/src/test/`.

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| `java.sql.SQLException: Access denied` | Wrong DB credentials | Double-check `spring.datasource.username` and `spring.datasource.password` in `application.properties` |
| `Communications link failure` | DB host/port unreachable | Verify `spring.datasource.url`, firewall rules, and that MySQL is running |
| `SSL connection error` | SSL mode mismatch | Ensure `sslMode=REQUIRED` is set when connecting to Aiven; remove it for local MySQL without SSL |
| `401 Unauthorized` on API calls | Expired or missing JWT | Log out and log in again; ensure the `Authorization: Bearer <token>` header is present |
| `Port 8080 already in use` | Another process on port 8080 | Change `server.port` in `application.properties`, or stop the conflicting process |
| Frontend shows blank / CORS error | Backend not running | Start the backend first and confirm it is reachable at `http://localhost:8080` |
| Docker build fails at `mvn dependency:go-offline` | Network issue inside build | Ensure Docker has internet access, or run the build behind a Maven mirror |

---

## License

No license file is present in this repository. All rights are reserved by the author unless otherwise stated.
