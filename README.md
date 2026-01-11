# Car Rental Marketplace - Spring Boot & React

A full-stack car rental and marketplace platform built with **Spring Boot** (backend) and **React** (frontend). This application provides a comprehensive solution for vehicle rentals, sales, and marketplace management with advanced features including real-time messaging, booking management, payment processing, and administrative controls.

## 🚀 Features

### Core Features

- **User Authentication & Authorization** - JWT-based secure authentication with role-based access control (Admin, Seller, Buyer, Customer, Driver)
- **Vehicle Listings Management** - Create, edit, and manage vehicle listings for both rental and sale
- **Advanced Search & Filtering** - Search vehicles by make, model, price, location, and more
- **Booking System** - Complete booking workflow with availability calendar and price calculation
- **Real-time Messaging** - In-app messaging between buyers and sellers
- **Reviews & Ratings** - User review system with rating statistics
- **Wishlist** - Save favorite vehicles for later viewing
- **Notifications** - Real-time notifications for bookings, messages, and updates

### Advanced Features

- **Admin Dashboard** - Comprehensive admin panel with analytics and user management
- **User Management** - Block/unblock users, manage roles, view user statistics
- **Category Management** - Manage vehicle makes and models
- **Activity Logs** - Track all user actions and system events
- **Seller Verification** - Verification system for trusted sellers
- **Audit Trail** - Complete audit trail for entity changes
- **Driver Management** - Assign drivers to bookings with location tracking
- **Subscription Plans** - Tiered subscription system for sellers
- **Payment Processing** - Integrated payment system with transaction history
- **Coupon System** - Create and manage discount coupons
- **Featured Listings** - Boost listings with featured placement
- **Promotion Banners** - Manage promotional banners with scheduling
- **SEO Optimization** - Meta tags, social sharing, and sitemap support
- **Google Analytics** - Integrated analytics tracking

## 🛠️ Technology Stack

### Backend

- **Java 21**
- **Spring Boot 3.2.1**
- **Spring Security** - JWT authentication
- **Spring Data JPA** - Database ORM
- **Flyway** - Database migration management
- **MySQL 8.0** - Primary database
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

### Frontend

- **React 18**
- **Vite** - Build tool
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **Tailwind CSS** - Utility-first CSS framework
- **React Helmet Async** - SEO meta tags
- **Google Analytics** - User tracking

## 📋 Prerequisites

- **Java 21** or higher
- **Node.js 18** or higher
- **MySQL 8.0** or higher
- **Maven 3.8** or higher
- **Git**

## 🔧 Installation & Setup

### 1. Clone the Repository

```bash
git clone git@github.com:Muhammad-awais-safdar/car-Rental-spring.git
cd car-Rental-spring
```

### 2. Database Setup

```bash
# Create MySQL database
mysql -u root -p
CREATE DATABASE car_marketplace;
exit;
```

### 3. Backend Setup

```bash
cd backend

# Update application.properties with your MySQL credentials
# Edit: src/main/resources/application.properties
# spring.datasource.username=your_username
# spring.datasource.password=your_password

# Run Flyway migrations
mvn flyway:migrate

# Build and run the backend
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Create .env file
cp .env.example .env

# Update .env with your configuration
# VITE_API_URL=http://localhost:8080/api
# VITE_SITE_URL=http://localhost:5173
# VITE_GA_TRACKING_ID=your_ga_tracking_id

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

## 📁 Project Structure

```
car-Rental-spring/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/marketplace/
│   │   │   │   ├── admin/          # Admin management
│   │   │   │   ├── auth/           # Authentication & authorization
│   │   │   │   ├── booking/        # Booking management
│   │   │   │   ├── category/       # Vehicle categories
│   │   │   │   ├── common/         # Shared utilities
│   │   │   │   ├── coupon/         # Coupon system
│   │   │   │   ├── driver/         # Driver management
│   │   │   │   ├── listing/        # Vehicle listings
│   │   │   │   ├── messaging/      # Real-time messaging
│   │   │   │   ├── notification/   # Notifications
│   │   │   │   ├── payment/        # Payment processing
│   │   │   │   ├── promotion/      # Promotion banners
│   │   │   │   ├── rental/         # Rental management
│   │   │   │   ├── review/         # Reviews & ratings
│   │   │   │   ├── seller/         # Seller verification
│   │   │   │   ├── subscription/   # Subscription plans
│   │   │   │   └── wishlist/       # Wishlist feature
│   │   │   └── resources/
│   │   │       ├── db/migration/   # Flyway migrations
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
└── frontend/
    ├── src/
    │   ├── features/
    │   │   ├── admin/              # Admin pages
    │   │   ├── auth/               # Auth pages
    │   │   ├── bookings/           # Booking pages
    │   │   ├── listings/           # Listing pages
    │   │   ├── messaging/          # Messaging pages
    │   │   ├── notifications/      # Notification components
    │   │   ├── profile/            # User profile
    │   │   ├── rentals/            # Rental pages
    │   │   ├── reviews/            # Review components
    │   │   ├── subscription/       # Subscription pages
    │   │   └── wishlist/           # Wishlist pages
    │   ├── shared/                 # Shared components
    │   ├── utils/                  # Utility functions
    │   ├── App.jsx
    │   └── main.jsx
    ├── public/
    ├── package.json
    └── vite.config.js
```

## 🔐 API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user

### Listings

- `GET /api/listings` - Get all listings
- `GET /api/listings/{id}` - Get listing by ID
- `POST /api/listings` - Create new listing
- `PUT /api/listings/{id}` - Update listing
- `DELETE /api/listings/{id}` - Delete listing
- `GET /api/listings/search` - Search listings

### Bookings

- `POST /api/bookings` - Create booking
- `GET /api/bookings/user` - Get user bookings
- `GET /api/bookings/{id}` - Get booking details
- `PUT /api/bookings/{id}/status` - Update booking status

### Reviews

- `POST /api/reviews` - Create review
- `GET /api/reviews/listing/{id}` - Get listing reviews
- `GET /api/reviews/statistics/{id}` - Get rating statistics

### Messaging

- `POST /api/messages` - Send message
- `GET /api/messages/conversations` - Get conversations
- `GET /api/messages/conversation/{id}` - Get conversation messages

### Admin

- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/{id}/block` - Block user
- `GET /api/admin/analytics` - Get analytics data
- `GET /api/admin/activity-logs` - Get activity logs

_For complete API documentation, see the API docs at `/api-docs` when running the backend._

## 🗄️ Database Schema

The application uses **24 Flyway migrations** to manage the database schema:

1. Users and roles
2. Vehicle listings
3. Rentals and bookings
4. Booking driver and location fields
5. Drivers table
6. Slug generation for listings
7. Rename listings to vehicles
8. Driver role
9. Wishlist
10. Reviews
11. Notifications
12. Messaging tables
13. User blocking
14. Makes and models
15. Activity logs
16. Seller verification and audit trails
17. Subscriptions and payments
18. Coupons
19. Featured listings
20. Promotion banners

## 👥 Default Users

After running the database seeder, the following test users are available:

- **Admin**: admin@example.com / password
- **Seller**: seller@example.com / password
- **Buyer**: buyer@example.com / password

## 🧪 Testing

### Backend Tests

```bash
cd backend
mvn test
```

### Frontend Tests

```bash
cd frontend
npm test
```

## 📦 Build for Production

### Backend

```bash
cd backend
mvn clean package
java -jar target/car-marketplace-1.0-SNAPSHOT.jar
```

### Frontend

```bash
cd frontend
npm run build
# Deploy the 'dist' folder to your hosting service
```

## 🚀 Deployment

### Backend Deployment

- Deploy the JAR file to any Java hosting service (AWS, Heroku, DigitalOcean, etc.)
- Ensure MySQL database is accessible
- Set environment variables for database credentials

### Frontend Deployment

- Build the frontend: `npm run build`
- Deploy the `dist` folder to:
  - Vercel
  - Netlify
  - AWS S3 + CloudFront
  - Any static hosting service

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**Muhammad Awais Safdar**

- GitHub: [@Muhammad-awais-safdar](https://github.com/Muhammad-awais-safdar)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful UI library
- All contributors and supporters of this project

## 📞 Support

For support, email your-email@example.com or open an issue in the GitHub repository.

---

**Note**: This is a comprehensive car rental marketplace platform with enterprise-level features. Make sure to configure all environment variables and security settings properly before deploying to production.
