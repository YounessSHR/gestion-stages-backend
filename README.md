# Gestion de Stages et Alternances - Backend

Plateforme web pour la gestion complète des stages et alternances.

## 🚀 Technologies

- **Spring Boot** : 4.0.0
- **Java** : 21
- **Base de données** : MySQL (XAMPP)
- **Sécurité** : Spring Security + JWT (0.12.5)
- **ORM** : Hibernate / JPA
- **Mapping** : ModelMapper 3.2.0
- **PDF** : iText 7 (8.0.3)

## 📋 Prérequis

- JDK 21
- Maven 3.8+
- MySQL 8.0 (XAMPP recommandé)
- IntelliJ IDEA (recommandé)

## ⚙️ Configuration

1. **Cloner le repository**
```bash
   git clone https://github.com/YounessSHR/gestion-stages-backend.git
```

2. **Créer la base de données**
```sql
   CREATE DATABASE gestion_stages CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Configurer `application.properties`**
   ```bash
   # Copy the example file
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
   - Vérifier l'URL de la base de données (par défaut: `jdbc:mysql://localhost:3306/gestion_stages`)
   - Modifier le mot de passe MySQL si nécessaire
   - **IMPORTANT**: Générer un secret JWT fort (voir `SECURITY.md` ou `SETUP.md`)
   - Configurer les credentials email si nécessaire
   
   ⚠️ **Sécurité**: Le fichier `application.properties` contient des informations sensibles et ne doit JAMAIS être commité. Voir `SECURITY.md` pour plus de détails.

4. **Lancer l'application**
```bash
   ./mvnw spring-boot:run
```
OU via IntelliJ : Run `GestionStagesBackendApplication`

5. **L'API sera disponible sur** : http://localhost:8080

## 📁 Structure du Projet

```
backend/
├── src/main/java/com/gestionstages/
│   ├── config/           # Configuration (Security, CORS, WebConfig)
│   ├── controller/       # REST Controllers
│   ├── service/          # Business Logic (interfaces + implementations)
│   ├── repository/       # Data Access Layer (JPA Repositories)
│   ├── model/            # Entities & DTOs (request/response)
│   ├── security/         # JWT & Authentication
│   ├── exception/        # Exception Handling
│   └── util/             # Utilities
├── src/main/resources/
│   ├── application.properties.example
│   └── templates/
│       └── convention-template.html
├── postman_collection.json  # Postman collection for API testing
├── POSTMAN_GUIDE.md         # Guide for using Postman collection
└── pom.xml
```

## ✅ Sprint 1 - Completed

### Features Implemented

#### 1. Authentication (JWT)
- ✅ User registration (Student, Enterprise, Tutor, Administration)
- ✅ User login with JWT token generation
- ✅ JWT-based authentication for protected endpoints
- ✅ Role-based access control

#### 2. Offers Management (CRUD)
- ✅ Create offer (Enterprise only)
- ✅ Update offer (Owner only)
- ✅ Delete offer (Owner only)
- ✅ Validate offer (Administration only)
- ✅ Get all public offers (validated and non-expired)
- ✅ Get offer by ID (public, but only validated offers)
- ✅ Search offers by title
- ✅ Get offers by enterprise

**Business Rules Implemented:**
- **RG02**: Offers must be validated by administration before being publicly visible
- Offers are created with status `EN_ATTENTE` (pending)
- Only validated and non-expired offers are accessible publicly

#### 3. Applications Management (CRUD)
- ✅ Create application (Student only)
- ✅ Get application by ID
- ✅ Get applications by student
- ✅ Get applications by offer (Enterprise owner only)
- ✅ Accept application (Enterprise owner only)
- ✅ Reject application (Enterprise owner only)
- ✅ Delete application (Student owner only)

**Business Rules Implemented:**
- **RG01**: A student can only apply once to the same offer
- **RG03**: An accepted application automatically triggers convention generation
- Only validated and non-expired offers can receive applications

### API Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

#### Offers
- `GET /api/offres/publiques` - Get all public offers (no auth required)
- `GET /api/offres/{id}` - Get offer by ID (no auth required, but only validated)
- `GET /api/offres/search?titre=...` - Search offers by title (no auth required)
- `POST /api/offres` - Create offer (Enterprise, auth required)
- `PUT /api/offres/{id}` - Update offer (Owner, auth required)
- `DELETE /api/offres/{id}` - Delete offer (Owner, auth required)
- `PUT /api/offres/{id}/valider` - Validate offer (Administration, auth required)
- `GET /api/offres/mes-offres` - Get my offers (Enterprise, auth required)

#### Applications
- `POST /api/candidatures` - Create application (Student, auth required)
- `GET /api/candidatures/{id}` - Get application by ID (auth required)
- `GET /api/candidatures/mes-candidatures` - Get my applications (Student, auth required)
- `GET /api/candidatures/offre/{offreId}` - Get applications for offer (Owner, auth required)
- `PUT /api/candidatures/{id}/accepter` - Accept application (Owner, auth required)
- `PUT /api/candidatures/{id}/refuser` - Reject application (Owner, auth required)
- `DELETE /api/candidatures/{id}` - Delete application (Owner, auth required)

### Testing

A complete Postman collection is provided (`postman_collection.json`) with:
- All endpoints pre-configured (Sprint 1 + Sprint 2)
- Automatic token management
- Test scenarios for business rules (RG01, RG02, RG03, RG04, RG05, RG07)
- Examples for all user roles (Student, Enterprise, Tutor, Administration)

A complete test workflow JSON is provided (`TEST_WORKFLOW_SPRINT2.json`) with:
- End-to-end test scenarios for Sprint 2
- Step-by-step workflows
- Business rules validation tests
- Security and permissions tests

See `POSTMAN_GUIDE.md` for detailed testing instructions.

## ✅ Sprint 2 - Completed

### Features Implemented

#### 1. Convention Management (RG04)
- ✅ Create convention (automatically when application is accepted - Sprint 1)
- ✅ Get all conventions (admin only)
- ✅ Get convention by ID
- ✅ Get my conventions (student/enterprise)
- ✅ Get conventions by student
- ✅ Get conventions by enterprise
- ✅ Student signs convention (student owner only)
- ✅ Enterprise signs convention (enterprise owner only)
- ✅ Administration signs convention (admin only)
- ✅ Generate PDF (manual and automatic when all 3 signatures are collected)
- ✅ Download PDF
- ✅ Archive convention (admin only)

**Business Rules Implemented:**
- **RG04**: A convention requires 3 signatures (student, enterprise, admin)
  - First signature → Status changes from `BROUILLON` to `EN_ATTENTE_SIGNATURES`
  - Third signature → Status changes to `SIGNEE` + PDF generated automatically

#### 2. Stage Follow-up (RG05, RG07)
- ✅ Assign tutor to signed convention (admin only)
- ✅ Get all follow-ups (admin only)
- ✅ Get follow-up by ID
- ✅ Get my students (tutor only)
- ✅ Get my stage (student only)
- ✅ Update progress (tutor owner only)

**Business Rules Implemented:**
- **RG05**: A tutor can follow max 10 active students
  - Only active students are counted (state != `TERMINE`)
- **RG07**: A student can only have one active internship at a time
  - An internship is active if `etatAvancement != TERMINE`

#### 3. Administration Dashboard
- ✅ Get dashboard statistics (admin only)
  - General statistics (offers, applications, conventions, follow-ups)
  - Top companies (by number of offers)
  - Top tutors (by number of active students)
  - Distribution by status/state

### API Endpoints (Sprint 2)

#### Conventions
- `GET /api/conventions` - Get all conventions (Admin, auth required)
- `GET /api/conventions/{id}` - Get convention by ID (auth required)
- `GET /api/conventions/mes-conventions` - Get my conventions (auth required)
- `GET /api/conventions/etudiant` - Get conventions by student (Student, auth required)
- `GET /api/conventions/entreprise` - Get conventions by enterprise (Enterprise, auth required)
- `PUT /api/conventions/{id}/signer-etudiant` - Student signs convention (Student, auth required)
- `PUT /api/conventions/{id}/signer-entreprise` - Enterprise signs convention (Enterprise, auth required)
- `PUT /api/conventions/{id}/signer-admin` - Admin signs convention (Admin, auth required)
- `POST /api/conventions/{id}/generer-pdf` - Generate PDF manually (auth required)
- `GET /api/conventions/{id}/pdf` - Download PDF (auth required)
- `PUT /api/conventions/{id}/archiver` - Archive convention (Admin, auth required)

#### Stage Follow-up
- `POST /api/suivis/assigner-tuteur` - Assign tutor (Admin, auth required)
- `GET /api/suivis` - Get all follow-ups (Admin, auth required)
- `GET /api/suivis/{id}` - Get follow-up by ID (auth required)
- `GET /api/suivis/mes-etudiants` - Get my students (Tutor, auth required)
- `GET /api/suivis/mon-stage` - Get my stage (Student, auth required)
- `PUT /api/suivis/{id}/avancement` - Update progress (Tutor, auth required)

#### Dashboard
- `GET /api/admin/dashboard/stats` - Get dashboard statistics (Admin, auth required)

**See `API_ENDPOINTS_SPRINT2.md` for complete API documentation.**

## 🔄 Next Steps (Sprint 3)

**Sprint 3 Features (Frontend):**
- [ ] React frontend implementation
- [ ] Integration with backend API
- [ ] User interface for all roles

## 📊 Database

The database schema is automatically created by Hibernate (`ddl-auto=update`).

**Main Entities:**
- `Utilisateur` (base class with inheritance)
  - `Etudiant`
  - `Entreprise`
  - `Tuteur`
  - `Administration`
- `OffreStage`
- `Candidature`
- `Convention`
- `SuiviStage`

## 🔐 Security

- JWT-based authentication
- Password encryption with BCrypt
- Role-based authorization
- CORS configured for React frontend (ports 3000, 5173)

**⚠️ Important**: See `SECURITY.md` for security guidelines and setup instructions.

## 📚 Documentation

- `SETUP.md` - Setup instructions
- `SECURITY.md` - Security guidelines
- `POSTMAN_GUIDE.md` - Postman testing guide
- `SPRINT2_PLAN.md` - Sprint 2 implementation plan
- `SPRINT2_VERIFICATION_REPORT.md` - Sprint 2 verification report
- `API_ENDPOINTS_SPRINT2.md` - Complete API documentation for Sprint 2
- `TEST_WORKFLOW_SPRINT2.json` - Complete test workflow for Sprint 2
- `CONCEPTION.md` - Project conception
- `ARCHITECTURE.md` - System architecture

## 👥 Équipe

- [Sahraoui Youness] - Backend Developer
- [Mjahdi Abdelouahab] - Backend Developer

## 📝 License

Ce projet est un projet académique.
