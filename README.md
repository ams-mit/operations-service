# Operations Service

AMS Group 4 — Operations, Maintenance, and Work Orders  
University of Kelaniya | Software Architecture and Process Models

## Overview

Handles maintenance request management, work order creation and technician assignment, and work order progress tracking.

- **Port (local):** `8084`
- **Database:** `operations_db` (MySQL 8)
- **Health check:** `http://localhost:8084/health`

## Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8

## Setup

### 1. Create the database

```sql
CREATE DATABASE operations_db;