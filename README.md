# AfyaQuik Healthcare Management System

AfyaQuik is a comprehensive healthcare management system designed to streamline operations in healthcare facilities. The system provides specialized interfaces for different roles including doctors, nurses, receptionists, and administrators, enabling efficient patient management, appointment scheduling, billing, and reporting.

## Features

- **Patient Management**: Register, update, and manage patient information
- **Appointment Scheduling**: Schedule and manage patient appointments
- **Visit Tracking**: Record and track patient visits and medical notes
- **Reporting**: Generate various reports for administrative and clinical purposes
- **User Management**: Manage system users with role-based access control
- **Multi-interface Support**: Specialized interfaces for doctors, nurses, receptionists, and administrators

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.4.5
- Spring Data JPA
- Spring Security
- PostgreSQL
- Liquibase (for database migrations)
- Lombok
- MapStruct
- JUnit Jupiter (for testing)
- SpringDoc OpenAPI (for API documentation)
- JWT Authentication

### Frontend
- React
- React Router
- Bootstrap
- SASS

### DevOps
- Docker
- Maven

## Project Structure

The project follows a modular architecture with separate backend services and frontend applications:

### Backend Modules
- **appointments**: Appointment scheduling functionality
- **patients**: Patient management
- **reports**: Reporting functionality
- **users**: User management and authentication
- **utils**: Utility classes and helpers
- **web**: Web interface components
- **dtos**: Data Transfer Objects shared across modules

### Frontend Applications
- **admin**: Administrative interface
- **auth**: Authentication interface
- **doctor**: Doctor interface
- **nurse**: Nurse interface
- **receptionist**: Receptionist interface
- **reports**: Reporting interface
- **shared**: Shared components and utilities

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Node.js 18 or higher
- npm 9 or higher
- PostgreSQL 14 or higher
- Docker and Docker Compose (optional, for containerized deployment)

### Backend Setup
1. Clone the repository:
   ```
   git clone https://github.com/yourusername/afyaquik.git
   cd afyaquik
   ```

2. Build the backend services:
   ```
   mvn clean install
   ```

3. Configure the database connection in `application.properties` or `application.yml`.

4. Run the backend services:
   ```
   mvn spring-boot:run
   ```

### Frontend Setup
1. Navigate to the frontend directory:
   ```
   cd afyaquik-frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Build all frontend applications:
   ```
   npm run build:all
   ```

4. Copy built files to their respective backend modules (optional):
   ```
   npm run copy:all
   ```

### Docker Deployment
1. Build and start all services using Docker Compose:
   ```
   docker-compose up -d
   ```

2. Access the services at:
   - Backend API: http://localhost:8080
   - Admin Frontend: http://localhost:3001
   - Auth Frontend: http://localhost:3002
   - Doctor Frontend: http://localhost:3003
   - Receptionist Frontend: http://localhost:3004
   - Reports Frontend: http://localhost:3005

## API Documentation

API documentation is available via SpringDoc OpenAPI. After starting the backend services, access the API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
