# Setup and Run Guide

This repository is a Spring Boot application with Thymeleaf templates, MySQL, Spring Security, and MoMo payment integration.

## 1) Prerequisites

Install these first:

- Java 21
- Maven 3.9+
- MySQL 8+
- Git
- Optional: MySQL Workbench for importing the seed data

## 2) Prepare the database

Create the database used by the app:

```sql
CREATE DATABASE tinhnguyenxanh CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Import the seed data from `sql/seed_all_tables.sql`.

Use the command that matches your shell:

```bat
# cmd
mysql -u root -p tinhnguyenxanh < sql\seed_all_tables.sql
```

```powershell
# PowerShell
cmd /c "mysql -u root -p tinhnguyenxanh < sql\seed_all_tables.sql"
```

```bash
# Bash, Git Bash, or WSL
mysql -u root -p tinhnguyenxanh < sql/seed_all_tables.sql
```

If you use MySQL Workbench, open `sql/seed_all_tables.sql` and run it against the `tinhnguyenxanh` database.

## 3) Configure application settings

Edit `src/main/resources/application.properties` and confirm these values match your environment:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.mail.username`
- `spring.mail.password`
- MoMo settings if you plan to test payments

The default config expects MySQL on `localhost:3306` with database `tinhnguyenxanh`.

## 4) Run the backend

```bash
mvn compile
```

From the project root, start the app with Maven:

```bash
mvn spring-boot:run
```

Or build first, then run the jar:

```bash
mvn clean package
java -jar target/tinhnguyenxanh-1.0.0.jar
```

The application runs on:

- `http://localhost:8080`
- If `8080` is busy, the app automatically falls back to the next free port, such as `8081`
- When the server finishes starting, it prints `Server running on http://localhost:xxxx` in the terminal

## 5) Main pages to test

After startup, check these routes:

- `/`
- `/events`
- `/organizations`
- `/login`
- `/register`
- `/admin/dashboard`
- `/organizer/dashboard`
- `/auth/profile`
- `/events/my-registrations`
- `/events/my-favorites`

## 6) Front-end note

The `nodejs/public` folder contains React source files used as a sample/reference UI, but this repository does not include a `package.json` or a complete npm/Vite setup.

That means:

- the Spring Boot app is runnable as-is
- the React sample is not directly runnable until you add the missing Node.js project files

If you want to run a React frontend later, you will need to add a Vite or CRA setup around `nodejs/public` first.

## 7) Troubleshooting

- If the app cannot connect to MySQL, verify the database name, username, and password in `application.properties`.
- If the app fails on port 8080, stop the process already using that port or change `server.port`.
- If uploaded files are not saved, make sure the `uploads/` directory exists and is writable.
- If email or MoMo features fail, recheck the credentials and callback URLs in `application.properties`.

## 8) Quick startup checklist

1. Install Java 21, Maven, and MySQL.
2. Create the `tinhnguyenxanh` database.
3. Import `sql/seed_all_tables.sql`.
4. Update `src/main/resources/application.properties`.
5. Run `mvn spring-boot:run`.
6. Open `http://localhost:8080`.
