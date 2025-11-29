# 📚 Library Management System

A comprehensive RESTful API built with Spring Boot for managing library operations including books, borrowers, and borrowing records with PostgreSQL database.

## 🚀 Features

### Core Features
- **Book Management**: CRUD operations for books with inventory tracking
- **Borrower Management**: Manage library members with membership types
- **Borrowing System**: Issue and return books with due date tracking
- **Inventory Management**: Real-time stock monitoring and low-stock alerts
- **Fine Calculation**: Automatic fine calculation for overdue books
- **Overdue Tracking**: Monitor and manage overdue books

### Advanced Features
- **Search Functionality**: Search books by title, author, or category
- **Membership Types**: Regular, Premium, and Student memberships
- **Borrow Limits**: Maximum 5 books per borrower
- **Status Tracking**: Track book status (Available, Out of Stock, Maintenance)
- **Audit Trail**: Automatic timestamp tracking for all entities
- **Exception Handling**: Comprehensive error handling with meaningful messages
- **API Documentation**: Interactive Swagger UI for API testing

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Lombok**
- **Maven**
- **Docker & Docker Compose**
- **Swagger/OpenAPI 3.0**

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+ (or use Docker)
- Docker & Docker Compose (optional)

## 🔧 Installation & Setup

### Option 1: Using Docker Compose (Recommended)

1. **Clone the repository**
```bash
git clone <your-repo-url>
cd library-management
```

2. **Build and run with Docker Compose**
```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`

### Option 2: Manual Setup

1. **Install PostgreSQL**
```bash
# On Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# On macOS with Homebrew
brew install postgresql
```

2. **Create Database**
```sql
psql -U postgres
CREATE DATABASE library_db;
```

3. **Update application.properties**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. **Build the application**
```bash
mvn clean install
```

5. **Run the application**
```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/library-management-1.0.0.jar
```

## 📚 API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints Overview

#### Books API (`/api/books`)
- `POST /api/books` - Create a new book
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `GET /api/books/search/title?title={title}` - Search by title
- `GET /api/books/search/author?author={author}` - Search by author
- `GET /api/books/category/{category}` - Get books by category
- `GET /api/books/inventory/low-stock` - Get low stock books
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

#### Borrowers API (`/api/borrowers`)
- `POST /api/borrowers` - Create a new borrower
- `GET /api/borrowers` - Get all borrowers
- `GET /api/borrowers/{id}` - Get borrower by ID
- `GET /api/borrowers/active` - Get active borrowers
- `PUT /api/borrowers/{id}` - Update borrower
- `PATCH /api/borrowers/{id}/deactivate` - Deactivate borrower
- `PATCH /api/borrowers/{id}/activate` - Activate borrower
- `DELETE /api/borrowers/{id}` - Delete borrower

#### Borrow Records API (`/api/borrow`)
- `POST /api/borrow` - Borrow a book
- `POST /api/borrow/return` - Return a book
- `GET /api/borrow/borrower/{borrowerId}` - Get borrower's records
- `GET /api/borrow/book/{bookId}` - Get book's borrow history
- `GET /api/borrow/overdue` - Get overdue records
- `GET /api/borrow/active` - Get active borrows
- `PATCH /api/borrow/{recordId}/mark-lost` - Mark book as lost

## 📝 Sample API Requests

### Create a Book
```json
POST /api/books
Content-Type: application/json

{
"title": "Clean Code",
"author": "Robert C. Martin",
"isbn": "978-0132350884",
"category": "Programming",
"totalCopies": 5,
"publisher": "Prentice Hall",
"publishYear": 2008,
"description": "A handbook of agile software craftsmanship"
}
```

### Create a Borrower
```json
POST /api/borrowers
Content-Type: application/json

{
"name": "John Doe",
"email": "john.doe@example.com",
"phone": "1234567890",
"membershipType": "PREMIUM"
}
```

### Borrow a Book
```json
POST /api/borrow
Content-Type: application/json

{
"bookId": 1,
"borrowerId": 1,
"borrowDays": 14
}
```

### Return a Book
```json
POST /api/borrow/return
Content-Type: application/json

{
"recordId": 1,
"notes": "Book returned in good condition"
}
```

## 🗄️ Database Schema

The application uses the following main tables:

- **books**: Stores book information and inventory
- **borrowers**: Stores borrower/member information
- **borrow_records**: Tracks all borrowing transactions

All tables include audit fields (created_at, updated_at) automatically managed by Spring Data JPA.

## ⚙️ Configuration

Key configuration options in `application.properties`:

```properties
# Server port
server.port=8080

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 📊 Business Rules

- **Maximum Borrow Limit**: 5 books per borrower
- **Default Borrow Period**: 14 days (configurable per transaction)
- **Maximum Borrow Period**: 90 days
- **Fine Rate**: $2.00 per day for overdue books
- **Lost Book Fine**: $100.00

## 🔒 Exception Handling

The application handles various exceptions:
- `ResourceNotFoundException` - When entities are not found
- `BookNotAvailableException` - When trying to borrow unavailable books
- `DuplicateResourceException` - When creating duplicate resources
- `InvalidOperationException` - When performing invalid operations
- `BorrowerNotActiveException` - When inactive borrower tries to borrow

All exceptions return proper HTTP status codes and detailed error messages.

## 🚀 Deployment

### Deploy to AWS/Azure/GCP

1. **Build the JAR**
```bash
mvn clean package
```

2. **Upload to cloud platform**
3. **Set environment variables**
4. **Configure database connection**
5. **Run the application**

### Deploy to Heroku

1. **Create Heroku app**
```bash
heroku create library-management-app
```

2. **Add PostgreSQL addon**
```bash
heroku addons:create heroku-postgresql:hobby-dev
```

3. **Deploy**
```bash
git push heroku main
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the MIT License.

## 📧 Contact

Your Name - [LinkedIn Profile]
Project Link: [GitHub Repository]

## 🎯 Future Enhancements

- [ ] User authentication and authorization with Spring Security
- [ ] Email notifications for due dates and overdue books
- [ ] Book reservation system
- [ ] Advanced search with filters
- [ ] Reports and analytics dashboard
- [ ] Mobile app integration
- [ ] Payment gateway for fines
- [ ] Book recommendations based on borrowing history

---

**Built with ❤️ using Spring Boot**