# Operations Service

AMS Group 4 — Operations, Maintenance, and Work Orders
University of Kelaniya | Software Architecture and Process Models

## Overview

The Operations Service handles maintenance request management, work order creation, technician assignment, and work order progress tracking.

* **Application port:** `8084`
* **Database:** MySQL 8
* **Database name:** `operations_db`
* **Docker database service:** `operations-db`

## Prerequisites

For Docker-based local testing:

* Docker Desktop
* Git

For running the application without Docker:

* Java 21
* Maven 3.9+
* MySQL 8

## Local Database Configuration

The Operations Service uses an isolated MySQL database for local development and testing.

```text
Operations Service
        |
        v
operations-db
        |
        v
MySQL 8
        |
        v
operations_db
```

The database runs in a separate Docker container and has its own Docker volume. This keeps the Operations Service database separate from the database used by the Community Service.

### Database Details

| Setting              | Value           |
| -------------------- | --------------- |
| Database type        | MySQL 8         |
| Database name        | `operations_db` |
| Docker service       | `operations-db` |
| MySQL container port | `3306`          |
| MySQL host port      | `3309`          |
| Application port     | `8084`          |

The application connects to MySQL using the Docker Compose service name:

```text
jdbc:mysql://operations-db:3306/operations_db
```

The application should use the container port `3306` when connecting from another Docker container. The host port `3309` is used when accessing the database directly from the host machine.

## Environment Variables

Database credentials are stored in a local `.env` file and are not committed to Git.

Create a `.env` file in the project root:

```env
MYSQL_ROOT_PASSWORD=<your-local-password>
```

Do not commit the `.env` file or add real database passwords to this README.

The `.gitignore` file includes:

```text
.env
```

## Docker Compose Setup

The local environment includes:

```text
+-----------------------+
| operations-service    |
| Port: 8084            |
+-----------+-----------+
            |
            | Docker network
            |
+-----------v-----------+
| operations-db        |
| MySQL 8              |
| Container port: 3306 |
+-----------------------+
            |
            v
   operations_db
```

The database uses a separate Docker volume so that the Operations Service database remains isolated from other service databases.

### Start the Environment

From the `operations-service` directory, run:

```bash
docker compose up --build
```

Docker Compose starts:

1. `operations-db`
2. MySQL database
3. `operations-service`

The Operations Service waits for the database health check before starting.

### Check Running Containers

Run:

```bash
docker compose ps
```

The database should show a **healthy** status and the Operations Service should show as running.

### Stop the Environment

To stop the containers:

```bash
docker compose down
```

To remove the database volume as well:

```bash
docker compose down -v
```

**Warning:** Removing the volume deletes the local database data.

## Application Configuration

The Docker Compose configuration provides the database connection settings to the Spring Boot application through environment variables.

The application uses:

```text
SPRING_PROFILES_ACTIVE=default
```

and connects to:

```text
jdbc:mysql://operations-db:3306/operations_db
```

The database password comes from:

```text
MYSQL_ROOT_PASSWORD
```

This keeps database credentials outside the committed source code.

## Manual MySQL Setup

If Docker is not used, the Operations Service can also be configured to use a local MySQL installation.

Create the database:

```sql
CREATE DATABASE operations_db;
```

Configure the database connection using the required local MySQL credentials.

For team testing, the Docker Compose setup is preferred because it provides an isolated and repeatable database environment.

## Health Check

When the application is running, the health endpoint is:

```text
http://localhost:8084/health
```

Swagger UI is available at:

```text
http://localhost:8084/swagger-ui.html
```

## Database Isolation

The Operations Service does not share its MySQL database container with the Community Service.

```text
Operations Service
       |
       +---- operations-db
       |         |
       |         +---- operations_db
       |
       |
Community Service
       |
       +---- community-db
                 |
                 +---- community_service_db
```

This separation helps prevent testing activities in one service from directly affecting the database of another service.
