# Guide de Démonstration - LinkUp
## Plateforme de Gestion de Stages et Alternances

**Durée recommandée : 15-20 minutes**

---

## 📋 Préparation Avant la Démonstration

### 1. Vérifications Préliminaires
- [ ] Backend démarré sur `http://localhost:8080`
- [ ] Frontend démarré sur `http://localhost:5173`
- [ ] Base de données MySQL accessible avec données de test
- [ ] Navigateur ouvert et prêt
- [ ] Comptes de test créés (voir section "Comptes de Test")

### 2. Comptes de Test Recommandés
Créer au moins un compte de chaque rôle :
- **Étudiant** : email@etudiant.com / password123
- **Entreprise** : entreprise@test.com / password123
- **Administration** : admin@test.com / password123
- **Tuteur** : tuteur@test.com / password123

---

## 🎯 Plan de Démonstration

### **Phase 1 : Présentation Générale (2 min)**

1. **Page d'accueil / Login**
   - Présenter l'interface moderne inspirée de LinkedIn
   - Montrer le branding "LinkUp" en noir et gras
   - Expliquer les 4 types d'utilisateurs (Étudiant, Entreprise, Tuteur, Admin)

2. **Architecture**
   - **Backend** : Spring Boot + JWT + MySQL
   - **Frontend** : React + Vite + Tailwind CSS
   - **Sécurité** : JWT, RBAC, CORS

---

### **Phase 2 : Workflow Étudiant (5 min)**

#### 2.1 Inscription et Connexion
- [ ] S'inscrire comme **Étudiant**
- [ ] Se connecter
- [ ] Vérifier le dashboard étudiant

#### 2.2 Consultation des Offres
- [ ] Naviguer vers **"Offres"**
- [ ] Montrer :
  - Pagination des offres (10 par page)
  - Filtres avancés :
    - Recherche par titre/description
    - Filtre par type d'offre
    - Filtre par localisation
    - Filtre par date de début
    - Filtre par rémunération
    - Tri (date, rémunération, etc.)
- [ ] Cliquer sur une offre pour voir les détails
- [ ] Montrer le bouton "Postuler"

#### 2.3 Candidature
- [ ] Postuler à une offre
- [ ] Vérifier la candidature dans **"Mes Candidatures"**
- [ ] Montrer les différents statuts (EN_ATTENTE, ACCEPTEE, REFUSEE)

#### 2.4 Gestion du CV
- [ ] Aller dans **"Profil"**
- [ ] Télécharger un CV (si pas déjà fait)
- [ ] Montrer que le CV est lié aux candidatures

#### 2.5 Notifications
- [ ] Cliquer sur l'icône de notification dans la navbar
- [ ] Montrer les notifications (si des actions ont été effectuées)
- [ ] Marquer une notification comme lue

#### 2.6 Conventions
- [ ] Si une candidature a été acceptée, aller dans **"Conventions"**
- [ ] Montrer le workflow de signature :
  - Signature Étudiant → Signature Entreprise → Signature Admin
- [ ] Télécharger le PDF de convention (si toutes les signatures sont collectées)

---

### **Phase 3 : Workflow Entreprise (4 min)**

#### 3.1 Connexion Entreprise
- [ ] Se connecter avec un compte **Entreprise**

#### 3.2 Création d'Offre
- [ ] Aller dans **"Mes Offres"**
- [ ] Cliquer sur **"Créer une Offre"**
- [ ] Remplir le formulaire :
  - Titre, description, compétences requises
  - Type d'offre, localisation, dates
  - Rémunération
- [ ] Créer l'offre
- [ ] Expliquer : **L'offre est en attente de validation par l'admin (RG02)**

#### 3.3 Gestion des Candidatures
- [ ] Aller dans **"Candidatures"**
- [ ] Montrer la liste des candidatures reçues
- [ ] Pour chaque candidature :
  - Voir le CV de l'étudiant
  - **Accepter** une candidature → Génération automatique de convention
  - **Refuser** une candidature → Notification envoyée à l'étudiant
- [ ] Montrer les états de chargement (spinners) lors des actions

#### 3.4 Conventions Entreprise
- [ ] Aller dans **"Conventions"**
- [ ] Montrer les conventions liées
- [ ] Signer une convention (si pas déjà signée)

---

### **Phase 4 : Workflow Administration (4 min)**

#### 4.1 Connexion Admin
- [ ] Se connecter avec un compte **Administration**

#### 4.2 Dashboard Administrateur
- [ ] Présenter le **Dashboard** avec :
  - Statistiques générales (offres, candidatures, conventions, suivis)
  - Top entreprises (par nombre d'offres)
  - Top tuteurs (par nombre d'étudiants)
  - Distributions par statut/état

#### 4.3 Validation des Offres
- [ ] Aller dans **"Validation Offres"**
- [ ] Montrer les offres en attente (`EN_ATTENTE`)
- [ ] **Valider** une offre → Elle devient publique
- [ ] **Refuser** une offre → Notification envoyée à l'entreprise
- [ ] Montrer les offres expirées (RG06)

#### 4.4 Gestion des Conventions
- [ ] Aller dans **"Conventions"**
- [ ] Montrer :
  - Toutes les conventions
  - Filtres (Active / Archivées)
  - Signature Admin (si pas déjà signée)
  - Archivage de conventions terminées
- [ ] Télécharger un PDF de convention

#### 4.5 Assignation de Tuteurs
- [ ] Aller dans **"Suivis"**
- [ ] Assigner un tuteur à une convention signée
- [ ] Vérifier la règle RG05 : **Un tuteur peut suivre max 10 étudiants actifs**

---

### **Phase 5 : Workflow Tuteur (3 min)**

#### 5.1 Connexion Tuteur
- [ ] Se connecter avec un compte **Tuteur**

#### 5.2 Mes Étudiants
- [ ] Aller dans **"Mes Étudiants"**
- [ ] Voir la liste des étudiants assignés
- [ ] Cliquer sur un étudiant pour voir son suivi

#### 5.3 Mise à Jour du Suivi
- [ ] Mettre à jour l'avancement du stage
- [ ] Changer l'état (EN_COURS, EN_VALIDATION, VALIDE, TERMINE)
- [ ] Ajouter des notes/commentaires

---

### **Phase 6 : Fonctionnalités Avancées (2 min)**

#### 6.1 Notifications In-App
- [ ] Revenir sur le système de notifications
- [ ] Montrer :
  - Badge avec nombre de non-lues
  - Dropdown avec toutes les notifications
  - Clic sur notification → Navigation vers la page correspondante
  - "Marquer toutes comme lues"

#### 6.2 Performance et Asynchronisme
- [ ] Expliquer que les emails et notifications sont envoyés de manière asynchrone
- [ ] Montrer que les actions de validation/acceptation sont rapides (pas d'attente)

#### 6.3 UI/UX Professionnelle
- [ ] Montrer :
  - Design inspiré de LinkedIn (thème blanc, colonne centrée)
  - Navigation intuitive avec navbar
  - Footer avec copyright LinkUp
  - Responsive design

---

## 🎓 Points Techniques à Mentionner

### Backend (JEE)
1. **Spring Boot 4.0**
   - RESTful API
   - Spring Security + JWT
   - JPA/Hibernate pour la persistance

2. **Architecture en Couches**
   - Controller → Service → Repository → Entity
   - DTOs pour les réponses
   - Gestion d'exceptions centralisée

3. **Règles Métier Implémentées**
   - **RG01** : Un étudiant ne peut postuler qu'une fois par offre
   - **RG02** : Les offres doivent être validées par l'admin
   - **RG03** : Acceptation candidature → Génération convention
   - **RG04** : Convention nécessite 3 signatures
   - **RG05** : Tuteur max 10 étudiants actifs
   - **RG06** : Offres expirées non accessibles
   - **RG07** : Un étudiant max 1 stage actif

4. **Fonctionnalités Techniques**
   - Génération PDF avec iText 7
   - Envoi d'emails avec Thymeleaf templates
   - Upload/Download de fichiers (CV)
   - Pagination et filtres avancés
   - Notifications in-app
   - Traitement asynchrone (@Async)

### Frontend (React)
1. **React 19 + Vite**
   - Composants réutilisables
   - Context API pour l'authentification
   - React Router pour la navigation

2. **UI/UX**
   - Tailwind CSS pour le styling
   - Design responsive
   - États de chargement
   - Notifications toast

---

## 🔍 Questions Probables du Professeur

### Questions Techniques
- **"Comment fonctionne l'authentification ?"**
  - JWT tokens stockés dans localStorage
  - Token ajouté automatiquement aux requêtes API
  - Backend vérifie le token et les rôles

- **"Comment gérez-vous les règles métier ?"**
  - Validation dans les Services (couche métier)
  - Exceptions métier personnalisées
  - Messages d'erreur clairs

- **"Comment fonctionne la pagination ?"**
  - Spring Data JPA `Pageable` côté backend
  - État de pagination dans le frontend
  - Requêtes optimisées avec `Specification` pour les filtres

### Questions Fonctionnelles
- **"Que se passe-t-il si un étudiant postule deux fois ?"**
  - RG01 : Erreur "Vous avez déjà postulé à cette offre"

- **"Comment sont générées les conventions ?"**
  - Automatiquement quand une candidature est acceptée
  - Template HTML avec Thymeleaf, converti en PDF avec iText

- **"Comment sont gérées les notifications ?"**
  - Table `notification` en base de données
  - Création asynchrone lors des actions importantes
  - Frontend interroge l'API toutes les 30 secondes

---

## 📝 Checklist Finale

Avant de finir la démo, vérifier :
- [ ] Tous les rôles ont été présentés
- [ ] Au moins une action complète par workflow (inscription → candidature → convention → signature)
- [ ] Les notifications fonctionnent
- [ ] Les filtres et pagination sont démontrés
- [ ] Le PDF de convention peut être téléchargé
- [ ] L'application répond rapidement (asynchrone)

---

## 🎬 Conclusion

**Points Forts à Mentionner :**
- Architecture propre et maintenable
- Respect des règles métier
- Interface utilisateur professionnelle
- Performance optimisée (asynchrone)
- Sécurité (JWT, RBAC)
- Expérience utilisateur fluide

**Prochaines Améliorations Possibles :**
- Recherche sémantique avancée
- Statistiques détaillées
- Export de données (Excel)
- Application mobile

---

**Bonne démonstration ! 🚀**
