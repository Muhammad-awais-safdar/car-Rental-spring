# Car Marketplace + Rental Platform

Enterprise-grade car marketplace and rental platform built with Spring Boot and React.

## 🚀 Features

### Core Functionality
- ✅ **Buy/Sell Cars** - Full listing management with CRUD operations
- ✅ **Rent Cars** - Rental listings with smart pricing (daily/weekly/monthly)
- ✅ **Booking System** - Date-based bookings with conflict detection
- ✅ **User Management** - JWT authentication, profile management
- ✅ **Admin Dashboard** - Listing moderation and approval workflow
- ✅ **File Upload** - Image upload for listings
- ✅ **Advanced Search** - Filter by make, model, price, year, location

### Technical Highlights
- **Modular Architecture** - Clean separation of concerns
- **API Standards** - Centralized response format and constants
- **Security** - JWT authentication, RBAC, BCrypt password hashing
- **Database** - Flyway migrations for version control
- **Responsive UI** - Professional design with Tailwind CSS

## 📋 Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA
- **Migration**: Flyway
- **Security**: Spring Security + JWT
- **Build**: Maven

### Frontend
- **Framework**: React 18
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **State**: React Context API

## 🛠️ Setup Instructions

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### Backend Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd "car rental/backend"
```

2. **Configure Database**

Create MySQL database:
```sql
CREATE DATABASE car_marketplace;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/car_marketplace
spring.datasource.username=root
spring.datasource.password=your_password
jwt.secret=your_secret_key_here
```

3. **Build and Run**
```bash
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend**
```bash
cd "car rental/frontend"
```

2. **Install dependencies**
```bash
npm install
```

3. **Configure Environment**

Create `.env` file:
```
VITE_API_URL=http://localhost:8080/api
```

4. **Run Development Server**
```bash
npm run dev
```

Frontend will start on `http://localhost:5173`

5. **Build for Production**
```bash
npm run build
```

## 📚 API Documentation

### Authentication
```
POST /api/auth/register - Register new user
POST /api/auth/login    - Login user
```

### Listings
```
GET    /api/listings/search      - Search listings
GET    /api/listings/{id}        - Get listing by ID
POST   /api/listings             - Create listing (Auth)
PUT    /api/listings/{id}        - Update listing (Auth)
DELETE /api/listings/{id}        - Delete listing (Auth)
GET    /api/listings/my          - Get user's listings (Auth)
```

### Rentals
```
GET  /api/rentals/{id}                    - Get rental by ID
GET  /api/rentals/listing/{listingId}     - Get rental by listing
POST /api/rentals                         - Create rental (Auth)
PUT  /api/rentals/{id}                    - Update rental (Auth)
POST /api/rentals/{id}/check-availability - Check availability
```

### Bookings
```
GET  /api/bookings/my              - Get user bookings (Auth)
GET  /api/bookings/{id}            - Get booking by ID (Auth)
POST /api/bookings                 - Create booking (Auth)
PUT  /api/bookings/{id}/cancel     - Cancel booking (Auth)
PUT  /api/bookings/{id}/confirm    - Confirm booking (Admin)
```

### User Profile
```
GET /api/users/me             - Get profile (Auth)
PUT /api/users/me             - Update profile (Auth)
PUT /api/users/me/password    - Change password (Auth)
```

### Admin
```
GET /api/admin/listings/pending       - Get pending listings (Admin)
PUT /api/admin/listings/{id}/approve  - Approve listing (Admin)
PUT /api/admin/listings/{id}/reject   - Reject listing (Admin)
```

### File Upload
```
POST   /api/files/upload          - Upload single file (Auth)
POST   /api/files/upload-multiple - Upload multiple files (Auth)
DELETE /api/files/{filename}      - Delete file (Auth)
```

## 🔐 Default Roles

The system includes the following roles:
- `SUPER_ADMIN` - Full system access
- `ADMIN` - Moderation and management
- `SELLER` - Can create sell listings
- `BUYER` - Can browse and buy
- `RENTER` - Can rent cars
- `CUSTOMER` - Combined buyer and renter

## 📁 Project Structure

### Backend
```
backend/src/main/java/com/marketplace/
├── common/          # Shared utilities, constants, exceptions
├── auth/            # Authentication & user management
├── listing/         # Car listings (buy/sell)
├── rental/          # Rental management
└── admin/           # Admin operations
```

### Frontend
```
frontend/src/
├── core/api/        # API client
├── features/
│   ├── auth/        # Login, Register
│   ├── listings/    # Listing pages
│   ├── rentals/     # Rental pages
│   ├── bookings/    # Booking management
│   ├── profile/     # User profile
│   └── admin/       # Admin dashboard
└── shared/          # Reusable components
```

## 🎨 Design System

**Colors:**
- Primary: Racing Red (#EF4444)
- Secondary: Jet Black (#0B0F14)
- Accent: Neon Yellow (#FACC15)

**Components:**
- Cards, buttons, inputs follow consistent styling
- Responsive design for mobile/tablet/desktop

## 🧪 Testing

### Backend Tests
```bash
mvn test
```

### Frontend Tests
```bash
npm test
```

## 📦 Build Status

- ✅ Backend: 40 Java files compiled
- ✅ Frontend: 119 modules (327KB gzipped)

## 🚀 Deployment

### Backend
```bash
mvn clean package
java -jar target/car-marketplace-1.0-SNAPSHOT.jar
```

### Frontend
```bash
npm run build
# Deploy dist/ folder to your hosting service
```

## 📝 License

MIT License

## 👥 Contributing

Contributions are welcome! Please follow the existing code style and conventions.

## 📧 Support

For issues and questions, please open an issue on GitHub.

---

**Built with ❤️ using Spring Boot and React**
# car-Rental-spring
