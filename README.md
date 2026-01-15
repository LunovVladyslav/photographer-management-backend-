# Photographer Management Backend

A comprehensive Spring Boot backend service for managing photography sessions, client portfolios, and photo delivery. This system enables photographers to upload, organize, and share photos with clients while managing their business operations through an admin panel.

## Features

### 📸 Session Management
- Create and manage photography sessions
- Organize photos by session
- Track session metadata (date, location, content type, access type)
- Search and filter sessions using advanced criteria
- Generate unique access codes for client access

### 👥 Client Management
- Client database with contact information
- Track client sessions and photo history
- Automated client notifications via email
- Secure client authentication using access codes

### 🖼️ Photo Management
- Upload and store photos using MinIO object storage
- Automatic thumbnail generation
- Bulk photo upload support
- Download photos individually or as ZIP archives
- Photo metadata tracking (filename, size, upload date)

### 📧 Notification System
- Email notifications to clients when photos are ready
- Automated access code delivery
- Integration with RabbitMQ for asynchronous messaging
- Configurable email templates

### 🎨 Portfolio Management
- Manage public portfolio photos on website
- Separate portfolio section from client sessions
- Easy photo organization and display

## Tech Stack

- **Framework:** Spring Boot 3.5.6
- **Language:** Java 21
- **Database:** PostgreSQL
- **Object Storage:** MinIO
- **Message Queue:** RabbitMQ (AMQP)
- **Email:** Spring Mail
- **Image Processing:** Thumbnailator
- **API Documentation:** SpringDoc OpenAPI (Swagger)
- **Mapping:** MapStruct
- **Build Tool:** Gradle
- **Testing:** JUnit 5, Testcontainers

## Architecture

```
photographer-backend/
├── config/           # Configuration classes (Security, MinIO, RabbitMQ, Mail)
├── controller/       # REST API endpoints
├── model/
│   ├── dto/         # Data Transfer Objects
│   ├── entity/      # JPA Entities (Client, Session, Photo)
│   ├── exception/   # Custom exceptions
│   └── validation/  # Custom validators
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic layer
├── specification/   # JPA Specifications for dynamic queries
└── util/           # Utilities and mappers
```

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL database
- MinIO server
- RabbitMQ server
- SMTP server for email notifications

### Configuration

Create `application.properties` or `application.yml` with the following configurations:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/photographer_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# MinIO
minio.url=http://localhost:9000
minio.access-key=your_access_key
minio.secret-key=your_secret_key
minio.bucket-name=photos

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Running the Application

```bash
# Clone the repository
git clone https://github.com/LunovVladyslav/photographer-backend.git
cd photographer-backend

# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Sessions
- `POST /api/sessions` - Create a new session
- `GET /api/sessions` - Get all sessions (with filtering)
- `GET /api/sessions/{id}` - Get session by ID
- `PUT /api/sessions/{id}` - Update session
- `DELETE /api/sessions/{id}` - Delete session
- `GET /api/sessions/{id}/download` - Download session photos as ZIP

### Clients
- `POST /api/clients` - Create a new client
- `GET /api/clients` - Get all clients
- `GET /api/clients/{id}` - Get client by ID
- `PUT /api/clients/{id}` - Update client
- `DELETE /api/clients/{id}` - Delete client

### Photos
- `POST /api/photos` - Upload photos to a session
- `GET /api/photos/{id}` - Get photo details
- `GET /api/photos/{id}/download` - Download photo
- `GET /api/photos/thumbnail/{id}` - Get photo thumbnail
- `DELETE /api/photos/{id}` - Delete photo

### Notifications
- `POST /api/notifications/send` - Send email notification to client with access code

## Workflow

1. **Create Client** - Add client information to the system
2. **Create Session** - Create a photography session and link it to a client
3. **Upload Photos** - Upload photos to the session (stored in MinIO)
4. **Send Notification** - Click button to send email with access code to client
5. **Client Access** - Client receives email and uses access code to view/download photos
6. **Download Archive** - Client can download all session photos as a ZIP file

## Security

- Access code validation for client photo access
- Custom validators for data integrity
- Exception handling with detailed error responses
- Secure file storage with MinIO

## Testing

The project includes comprehensive tests using:
- JUnit 5
- Testcontainers for integration tests
- Spring Boot Test

Run tests with:
```bash
./gradlew test
```

## Development

### Database Schema

The application uses three main entities:
- **Client** - Client information (name, email, phone)
- **Session** - Photography session details
- **Photo** - Individual photos with metadata

Schema is initialized from `src/main/resources/schema.sql`

### Image Processing

Photos are automatically processed on upload:
- Original images stored in MinIO
- Thumbnails generated using Thumbnailator
- Metadata extracted and stored in PostgreSQL

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Contact

Vladyslav Lunov - [GitHub](https://github.com/LunovVladyslav)

Project Link: [https://github.com/LunovVladyslav/photographer-backend](https://github.com/LunovVladyslav/photographer-backend)
