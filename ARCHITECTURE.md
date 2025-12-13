# Architecture du Système

## Backend (Spring Boot 4.0.0)
Port: 8080
Structure:
- config/ : SecurityConfig, CorsConfig
- controller/ : REST Controllers (@RestController)
- service/ : Business logic
- repository/ : JPA Repositories
- model/entity/ : Entités JPA
- model/dto/ : Request/Response DTOs
- security/jwt/ : JWT Provider, Filters
- exception/ : Global Exception Handler

## Frontend (React 18 + Vite)
Port: 5173
Structure:
- components/ : Composants réutilisables
- pages/ : Pages par rôle (etudiant/, entreprise/, admin/, tuteur/)
- services/ : API calls (Axios)
- context/ : AuthContext
- routes/ : React Router

## API Endpoints Existants
POST /api/auth/register
POST /api/auth/login
GET /api/offres/publiques
POST /api/offres (entreprise)
PUT /api/offres/{id}/valider (admin)
POST /api/candidatures (etudiant)
PUT /api/candidatures/{id}/accepter (entreprise)

## Base de Données
MySQL 8.0 via XAMPP
Tables créées automatiquement par Hibernate (ddl-auto=update)