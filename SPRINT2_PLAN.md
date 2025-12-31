# Sprint 2 - Plan d'Implémentation

## 🎯 Objectif du Sprint 2

Implémenter la gestion des conventions, le suivi des stages, et le dashboard administration.

---

## 📋 Features à Implémenter

### 1. Gestion des Conventions (RG04)

#### 1.1. Signature des Conventions
- **RG04**: Une convention nécessite 3 signatures (étudiant, entreprise, admin)
- Signature étudiant (étudiant propriétaire)
- Signature entreprise (entreprise propriétaire)
- Signature administration (admin uniquement)
- Passage automatique au statut `SIGNEE` quand les 3 signatures sont collectées

#### 1.2. Génération PDF
- Générer un PDF de convention à partir du template HTML
- Stocker le PDF dans `uploads/conventions/`
- Retourner l'URL du PDF

#### 1.3. Gestion des Statuts
- `BROUILLON` → `EN_ATTENTE_SIGNATURES` (quand la première signature est apposée)
- `EN_ATTENTE_SIGNATURES` → `SIGNEE` (quand les 3 signatures sont collectées)
- `SIGNEE` → `ARCHIVEE` (après la fin du stage)

---

### 2. Suivi des Stages (RG05, RG07)

#### 2.1. Assignation de Tuteurs
- **RG05**: Un tuteur peut suivre max 10 étudiants
- Admin assigne un tuteur à une convention signée
- Vérifier que le tuteur n'a pas déjà 10 étudiants assignés
- Créer automatiquement un SuiviStage lors de l'assignation

#### 2.2. Gestion de l'Avancement
- Tuteur peut mettre à jour l'état d'avancement (NON_COMMENCE, EN_COURS, TERMINE)
- Tuteur peut ajouter des commentaires
- Tuteur peut mettre à jour la date de dernière visite

#### 2.3. Règles Métier
- **RG07**: Un étudiant ne peut avoir qu'un seul stage actif à la fois
- Vérifier qu'un étudiant n'a pas déjà un stage actif avant d'assigner un tuteur

---

### 3. Dashboard Administration

#### 3.1. Statistiques Générales
- Nombre total d'offres (par statut)
- Nombre total de candidatures (par statut)
- Nombre total de conventions (par statut)
- Nombre total d'étudiants en stage actif

#### 3.2. Statistiques par Entité
- Top entreprises (par nombre d'offres)
- Top tuteurs (par nombre d'étudiants suivis)
- Répartition des stages par état d'avancement

---

## 📦 Structure à Créer

### DTOs à Créer
```
model/dto/response/
  ├── ConventionResponse.java
  ├── SuiviStageResponse.java
  └── DashboardStatsResponse.java

model/dto/request/
  ├── SignatureRequest.java (pour les signatures)
  ├── AssignTuteurRequest.java
  └── UpdateSuiviRequest.java
```

### Services à Implémenter
```
service/
  ├── ConventionService.java (interface)
  ├── ConventionServiceImpl.java
  ├── SuiviService.java (interface)
  ├── SuiviServiceImpl.java
  └── DashboardService.java (nouveau)
```

### Controllers à Implémenter
```
controller/
  ├── ConventionController.java
  ├── SuiviController.java
  └── DashboardController.java (nouveau)
```

### Services Utilitaires
```
service/
  ├── PdfGeneratorService.java (existe déjà - à compléter)
  └── EmailService.java (existe déjà - à utiliser)
```

---

## 🔌 Endpoints à Implémenter

### Conventions

#### Base
- `GET /api/conventions` - Liste toutes les conventions (admin)
- `GET /api/conventions/{id}` - Détails d'une convention
- `GET /api/conventions/mes-conventions` - Mes conventions (étudiant/entreprise)

#### Signatures
- `PUT /api/conventions/{id}/signer-etudiant` - Signature étudiant
- `PUT /api/conventions/{id}/signer-entreprise` - Signature entreprise
- `PUT /api/conventions/{id}/signer-admin` - Signature admin

#### PDF
- `GET /api/conventions/{id}/pdf` - Télécharger le PDF
- `POST /api/conventions/{id}/generer-pdf` - Générer le PDF

### Suivi des Stages

#### Assignation
- `POST /api/suivis/assigner-tuteur` - Assigner un tuteur (admin)
- `GET /api/suivis` - Liste tous les suivis (admin)
- `GET /api/suivis/mes-etudiants` - Mes étudiants (tuteur)
- `GET /api/suivis/mon-stage` - Mon stage (étudiant)

#### Gestion
- `PUT /api/suivis/{id}/avancement` - Mettre à jour l'avancement (tuteur)
- `PUT /api/suivis/{id}/commentaire` - Ajouter/modifier commentaire (tuteur)
- `GET /api/suivis/{id}` - Détails d'un suivi

### Dashboard

- `GET /api/admin/dashboard/stats` - Statistiques générales
- `GET /api/admin/dashboard/offres` - Stats offres
- `GET /api/admin/dashboard/candidatures` - Stats candidatures
- `GET /api/admin/dashboard/conventions` - Stats conventions

---

## 🗄️ Repositories à Enrichir

### ConventionRepository
```java
List<Convention> findByStatut(StatutConventionEnum statut);
List<Convention> findByCandidature_Etudiant_Email(String email);
List<Convention> findByCandidature_Offre_Entreprise_Email(String email);
Optional<Convention> findByCandidatureId(Long candidatureId);
```

### SuiviStageRepository
```java
List<SuiviStage> findByTuteur_Email(String email);
List<SuiviStage> findByConvention_Candidature_Etudiant_Email(String email);
Long countByTuteur_Email(String email); // Pour RG05
List<SuiviStage> findByEtatAvancement(EtatAvancementEnum etat);
```

### TuteurRepository
```java
Optional<Tuteur> findByEmail(String email);
List<Tuteur> findAll();
```

---

## 📝 Règles Métier à Implémenter

### RG04: Convention nécessite 3 signatures
- Vérifier que chaque signataire peut signer (propriétaire ou admin)
- Quand 3 signatures sont collectées → statut = `SIGNEE`
- Générer automatiquement le PDF quand signée

### RG05: Tuteur max 10 étudiants
- Avant assignation, vérifier `countByTuteur_Email < 10`
- Si >= 10 → erreur "Tuteur a atteint la limite de 10 étudiants"

### RG07: Un étudiant, un seul stage actif
- Avant assignation tuteur, vérifier qu'il n'y a pas déjà un SuiviStage actif
- Un stage est actif si `etatAvancement != TERMINE`

---

## 🔄 Workflow Complet

### Cycle de Vie d'une Convention

1. **Candidature acceptée** (Sprint 1) → Convention créée en `BROUILLON`
2. **Première signature** → Statut passe à `EN_ATTENTE_SIGNATURES`
3. **Deuxième signature** → Toujours `EN_ATTENTE_SIGNATURES`
4. **Troisième signature** → Statut passe à `SIGNEE` + PDF généré
5. **Admin assigne tuteur** → SuiviStage créé
6. **Fin du stage** → Convention archivée (`ARCHIVEE`)

### Cycle de Vie d'un Suivi

1. **Assignation tuteur** (admin) → SuiviStage créé avec `NON_COMMENCE`
2. **Tuteur met à jour** → `EN_COURS` + commentaires
3. **Stage terminé** → `TERMINE` + date de fin

---

## 📊 Dashboard - Données à Afficher

### Statistiques Générales
```json
{
  "totalOffres": 150,
  "offresEnAttente": 20,
  "offresValidees": 120,
  "totalCandidatures": 500,
  "candidaturesEnAttente": 100,
  "candidaturesAcceptees": 80,
  "totalConventions": 80,
  "conventionsSignees": 50,
  "stagesActifs": 45,
  "etudiantsEnStage": 45
}
```

### Top Entreprises
```json
{
  "topEntreprises": [
    { "nom": "TechCorp", "nbOffres": 15 },
    { "nom": "DevInc", "nbOffres": 12 }
  ]
}
```

### Répartition Avancement
```json
{
  "avancement": {
    "nonCommence": 5,
    "enCours": 35,
    "termine": 5
  }
}
```

---

## ✅ Checklist d'Implémentation

### Phase 1: Conventions
- [ ] Créer ConventionResponse DTO
- [ ] Implémenter ConventionService
- [ ] Implémenter ConventionController
- [ ] Gérer les signatures (3 endpoints)
- [ ] Logique de changement de statut
- [ ] Génération PDF (utiliser PdfGeneratorService)
- [ ] Tests avec Postman

### Phase 2: Suivi Stages
- [ ] Créer SuiviStageResponse DTO
- [ ] Créer request DTOs (AssignTuteurRequest, UpdateSuiviRequest)
- [ ] Implémenter SuiviService
- [ ] Implémenter SuiviController
- [ ] Assignation tuteur avec vérification RG05
- [ ] Mise à jour avancement
- [ ] Tests avec Postman

### Phase 3: Dashboard
- [ ] Créer DashboardStatsResponse DTO
- [ ] Implémenter DashboardService
- [ ] Implémenter DashboardController
- [ ] Statistiques générales
- [ ] Statistiques détaillées
- [ ] Tests avec Postman

### Phase 4: Validation
- [ ] Tester toutes les règles métier (RG04, RG05, RG07)
- [ ] Tester les workflows complets
- [ ] Vérifier les permissions
- [ ] Documenter les endpoints
- [ ] Mettre à jour README.md

---

## 🚀 Ordre de Développement Recommandé

1. **Conventions** (base + signatures)
2. **Génération PDF**
3. **Suivi Stages** (assignation)
4. **Suivi Stages** (gestion avancement)
5. **Dashboard** (statistiques)

---

## 📚 Ressources

- Template PDF: `src/main/resources/templates/convention-template.html`
- Service PDF: `PdfGeneratorService.java` (à compléter)
- Service Email: `EmailService.java` (à utiliser pour notifications)

---

## ⚠️ Points d'Attention

1. **PDF Generation**: Vérifier que iText est bien configuré
2. **Permissions**: S'assurer que chaque endpoint vérifie les rôles
3. **RG05**: Bien compter les étudiants actifs (pas terminés)
4. **RG07**: Vérifier qu'un étudiant n'a qu'un seul stage actif
5. **Transactions**: Utiliser `@Transactional` pour les opérations complexes

