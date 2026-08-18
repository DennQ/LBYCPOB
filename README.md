# SocialNet

A student social network and event management system built with Spring Boot. SocialNet gives students one place to manage their profile, discover campus organizations, and find and register for events.

## Overview

Students often struggle to keep track of campus organizations and events because information is spread across Facebook groups, bulletin boards, email announcements, and word of mouth. SocialNet solves this by bringing profiles, organizations, and events together in a single, easy to use web application.

## Features

- **Student Profile Management:** Create, view, look up, update, and delete profiles with academic details such as student ID, course, and year level.
- **Profile Search:** Search for a student by name or student ID.
- **Friends System:** Add or remove friends, with mutual friendships.
- **Organization Management:** Create, view, update, and delete organizations, each with a description, category, and logo.
- **Organization Members:** Add or remove members and assign roles, such as member or admin.
- **Event Management:** Create events under an organization with a date, venue, capacity, and description.
- **Event Discovery:** Browse events with filters for All, Upcoming, or Next 2 Weeks.
- **Event Registration:** Register for events, with duplicate prevention and a live registration count.
- **Image Upload:** Upload a profile picture, automatically compressed to WebP format.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| Frontend | Thymeleaf 3.5, Bootstrap 5.3 |
| Database | PostgreSQL (via Supabase) |
| Cloud Storage | Supabase Storage |
| Build Tool | Maven 3.9.16 |
| Validation | Jakarta Validation |

## Prerequisites

Before running the project, make sure you have:

- Java 25 or later (JDK)
- Maven 3.9 or later
- A Supabase account with a PostgreSQL database and a storage bucket
- Git

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-org/socialnet.git
cd socialnet
```

### 2. Configure the database and storage

Create an `application.properties` (or `application.yml`) file in `src/main/resources` and add your Supabase connection details:

```properties
# Database connection (use the session pooler on port 6543)
spring.datasource.url=jdbc:postgresql://<your-supabase-host>:6543/postgres
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>

# Supabase storage
supabase.url=<your-supabase-project-url>
supabase.api-key=<your-supabase-api-key>
supabase.storage-bucket=<your-bucket-name>
```

Note: use port 6543 (the session pooler) instead of the default port 5432. This is required due to network restrictions with direct connections.

### 3. Build the project

```bash
mvn clean install
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

## Project Structure

```
src/main/java/com/profilemanager/
├── controller/     Web and REST controllers
├── service/        Business logic
├── repository/     Data access (Spring Data JPA)
├── model/          Entity classes
├── dto/            Data transfer objects
└── exception/      Global exception handling
```

## Usage

1. Go to the home page and add a student profile with a name, student ID, course, and year level.
2. Go to the Organizations page to create an organization and add members.
3. Go to the Events page to create an event under an organization, then browse and register for events.
4. View a profile to see the organizations it belongs to.

## Team

| Member | Role |
|--------|------|
| Julio Ramos | Student profile management, backend development, database schema design |
| Angela Que | Organizations and events management, full stack integration, git administration |
| Gian Ticzon | Event discovery and registration, UI/UX design, frontend development, testing |

## License

This project was built for academic purposes as part of the LBYCPOB course requirements.
