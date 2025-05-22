# Supply Chain Management System

A Java Enterprise Edition (JEE) web application for managing supply chain operations.

## Project Structure

The project follows a standard Maven JEE project structure:

```
supply-chain-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── scm/
│   │   │           ├── controller/    # Servlet controllers
│   │   │           ├── model/         # Entity classes
│   │   │           ├── service/       # Business logic
│   │   │           ├── dao/           # Data access objects
│   │   │           └── util/          # Utility classes
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml    # JPA configuration
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml            # Web application config
│   │       ├── resources/
│   │       │   ├── css/               # CSS stylesheets
│   │       │   ├── js/                # JavaScript files
│   │       │   └── images/            # Image assets
│   │       ├── jsp/
│   │       │   ├── common/            # Common JSP pages
│   │       │   ├── admin/             # Admin JSP pages
│   │       │   ├── inventory/         # Inventory JSP pages
│   │       │   ├── supplier/          # Supplier JSP pages
│   │       │   ├── order/             # Order JSP pages
│   │       │   └── reports/           # Reports JSP pages
│   │       └── index.jsp              # Main entry point
├── pom.xml                            # Maven configuration
└── README.md                          # This file
```

## Technologies Used

- Java EE 8
- Servlet API 4.0
- JSP & JSTL
- JPA for database persistence
- EJB for business logic
- MySQL as the database
- HikariCP for connection pooling
- Maven for dependency management

## System Requirements

- JDK 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or compatible database
- Java EE 8 compatible application server (e.g., WildFly, GlassFish, or TomEE)

## Setup Instructions

1. **Clone the repository**

2. **Configure database**
   - Create a MySQL database named `scm_db`
   - Create a user `scm_user` with password `scm_password` (or update the persistence.xml with your credentials)
   - Grant the user all privileges on the `scm_db` database

3. **Build the project**
   ```bash
   mvn clean package
   ```

4. **Deploy the application**
   - Copy the generated WAR file from `target/scm.war` to your application server's deployment directory

5. **Access the application**
   - Open a web browser and navigate to `http://localhost:8080/scm`

## Features

- Inventory Management
- Supplier Management
- Order Processing
- Reporting and Analytics
- User Authentication and Authorization

## Security

The application implements form-based authentication with the following roles:
- `admin`: Full access to all features
- `manager`: Access to reports and operational features
- `user`: Limited access to basic operations

## License

This project is licensed under the MIT License.

## Contact

For any inquiries, please contact the development team. 