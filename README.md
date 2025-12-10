# Gestion de Stages et Alternances - Backend

Plateforme web pour la gestion complète des stages et alternances.

## 🚀 Technologies

- **Spring Boot** : 4.0.0
- **Java** : 21
- **Base de données** : MySQL (XAMPP)
- **Sécurité** : Spring Security + JWT
- **ORM** : Hibernate / JPA

## 📋 Prérequis

- JDK 21
- Maven 3.8+
- MySQL (XAMPP recommandé)
- IntelliJ IDEA (recommandé)

## ⚙️ Configuration

1. **Cloner le repository**
```bash
   git clone https://github.com/VotreUsername/gestion-stages-backend.git
```

2. **Créer la base de données**
```sql
   CREATE DATABASE gestion_stages CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Configurer `application.properties`**
    - Vérifier l'URL de la base de données
    - Modifier le mot de passe MySQL si nécessaire

4. **Lancer l'application**
```bash
   mvn spring-boot:run
```
OU via IntelliJ : Run `GestionStagesApplication`

5. **L'API sera disponible sur** : http://localhost:8080

## 📁 Structure du Projet
```
backend/
├── src/main/java/com/gestionstages/
│   ├── config/           # Configuration (Security, CORS, etc.)
│   ├── controller/       # REST Controllers
│   ├── service/          # Business Logic
│   ├── repository/       # Data Access Layer
│   ├── model/            # Entities & DTOs
│   ├── security/         # JWT & Authentication
│   └── exception/        # Exception Handling
├── src/main/resources/
│   ├── application.properties
│   └── templates-pdf/
└── pom.xml
```

## 👥 Équipe

- [Sahraoui Youness] - Backend Developer
- [Mjahdi Abdelouahab] - Backend Developer

## 📝 Endpoints API

Documentation complète à venir...

## 📄 Licence

Ce projet est un projet académique.