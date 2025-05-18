# AfyaQuik Healthcare Management System

AfyaQuik is a comprehensive healthcare management system designed to streamline operations in healthcare facilities. The system provides specialized interfaces for different roles including doctors, nurses, receptionists, and administrators, enabling efficient patient management, appointment scheduling, billing, and reporting.

## Features

- **Patient Management**: Register, update, and manage patient information
- **Appointment Scheduling**: Schedule and manage patient appointments
- **Triage System**: Prioritize patients based on medical urgency
- **Visit Tracking**: Record and track patient visits and medical notes
- **Billing Management**: Generate and manage patient bills
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

### Frontend
- React
- Redux Toolkit
- React Router
- Bootstrap 5
- SASS
- PapaParse (for CSV parsing)

### DevOps
- Docker
- Maven

## Project Structure

The project follows a modular architecture with separate backend services and frontend applications:

### Backend Modules
- **afyaquik-core**: Core functionality and shared components
- **afyaquik-users**: User management and authentication
- **afyaquik-patients**: Patient management
- **afyaquik-appointments**: Appointment scheduling
- **afyaquik-billing**: Billing management
- **afyaquik-reports**: Reporting functionality
- **afyaquik-communication**: Communication services
- **afyaquik-admin**: Administrative functions
- **afyaquik-doctor**: Doctor-specific functionality
- **afyaquik-receptionist**: Receptionist-specific functionality
- **afyaquik-web**: Web interface components
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
- Node.js 16 or higher
- npm 8 or higher
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

3. Configure the database connection in `application.properties` or `application.yml` files of each module.

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
   - Backend API: http://localhost:8080/api
   - Admin Frontend: http://localhost:8080/client/admin/index.html
   - User Management Frontend: http://localhost:8080/client/auth/index.html
   - Doctor Frontend: http://localhost:8080/client/doctor/index.html
   - Receptionist Frontend: http://localhost:8080/client/receptionist/index.html
   - Reports Frontend: http://localhost:8080/client/reports/index.html

## API Documentation

API documentation is available via SpringDoc OpenAPI. After starting the backend services, access the API documentation at:

```
http://localhost:8080/swagger-ui.html
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
