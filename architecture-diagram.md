# AfyaQuik System Architecture

## 1. System Overview

AfyaQuik is a healthcare management system with a modular architecture consisting of:
- A monolithic Spring Boot backend API
- Multiple specialized frontend applications for different user roles
- A shared component library for frontend code reuse

## 2. Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Client Browsers                                │
└───────────────┬───────────────┬───────────────┬───────────────┬─────────┘
                │               │               │               │
                ▼               ▼               ▼               ▼
┌───────────────────┐ ┌─────────────────┐ ┌─────────────┐ ┌─────────────────┐
│  Admin Frontend   │ │ Doctor Frontend │ │ Receptionist│ │ Reports Frontend│
│    (Port 3001)    │ │   (Port 3003)   │ │ (Port 3004) │ │   (Port 3005)   │
└─────────┬─────────┘ └────────┬────────┘ └──────┬──────┘ └────────┬────────┘
          │                    │                 │                  │
          │                    │                 │                  │
          │         ┌──────────▼─────────────────▼──────────┐      │
          │         │        Shared Components              │      │
          │         │  (React components, utilities, etc.)  │      │
          │         └──────────▲─────────────────▲──────────┘      │
          │                    │                 │                  │
          └────────────────────┼─────────────────┼──────────────────┘
                               │                 │
                               ▼                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                                                                         │
│                       AfyaQuik Backend API (Port 8080)                  │
│                                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │ Appointments│  │    Doctor   │  │   Patients  │  │    Users    │    │
│  │   Module    │  │    Module   │  │    Module   │  │    Module   │    │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │
│                                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │Communication│  │   Reports   │  │    Utils    │  │     Web     │    │
│  │   Module    │  │    Module   │  │    Module   │  │    Module   │    │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      Spring Framework                            │   │
│  │  (Spring Boot, Spring Security, Spring Data JPA, Spring Web)     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │
                                    ▼
                          ┌───────────────────┐
                          │   PostgreSQL      │
                          │    Database       │
                          └───────────────────┘
```

## 3. Component Details

### 3.1 Frontend Architecture

The frontend is divided into multiple specialized applications:

- **Admin Frontend**: Management interface for system administrators
- **Doctor Frontend**: Interface for doctors to manage patient visits and observations
- **Receptionist Frontend**: Interface for managing appointments and patient registration
- **Reports Frontend**: Interface for generating and viewing reports

All frontends share common components through the shared library, which includes:
- Reusable UI components
- Utility functions
- Common interfaces and types

**Technologies**:
- React
- Redux (with Redux Toolkit)
- React Router
- Bootstrap for styling

### 3.2 Backend Architecture

The backend follows a domain-driven design approach with modules organized by business domains:

- **Appointments Module**: Manages patient appointments
- **Doctor Module**: Handles doctor-specific functionality (observations, reports)
- **Patients Module**: Manages patient information
- **Users Module**: Handles user authentication and authorization
- **Communication Module**: Manages notifications and messaging
- **Reports Module**: Generates and manages reports
- **Utils Module**: Contains utility classes and helpers
- **Web Module**: Handles web-specific configurations

**Technologies**:
- Spring Boot
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL database
- Liquibase for database migrations
- MapStruct for object mapping
- Springdoc OpenAPI for API documentation

## 4. Data Flow

1. Users access the appropriate frontend application based on their role
2. Frontend applications communicate with the backend API via RESTful endpoints
3. Backend processes requests, interacts with the database, and returns responses
4. Shared components are used across frontends to maintain consistency

## 5. Security Architecture

- JWT-based authentication
- Role-based access control
- Spring Security for securing API endpoints
- Separate frontend applications to isolate functionality by user role

## 6. Deployment Architecture

The system is containerized using Docker:
- Each frontend application runs in its own container
- The backend API runs in a separate container
- PostgreSQL database runs in its own container

This architecture allows for independent scaling and deployment of components.
