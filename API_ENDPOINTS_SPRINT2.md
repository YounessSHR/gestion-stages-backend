# Documentation des Endpoints - Sprint 2

## 📋 Table des matières

1. [Conventions](#conventions)
2. [Suivi des Stages](#suivi-des-stages)
3. [Dashboard Administration](#dashboard-administration)

---

## 📄 Conventions

### 1. Récupérer toutes les conventions

**GET** `/api/conventions`

**Description**: Récupère toutes les conventions. Admin uniquement.

**Authentification**: Requise (JWT)

**Paramètres**: Aucun

**Réponse 200 OK**:
```json
[
  {
    "id": 1,
    "dateGeneration": "2025-01-15T10:30:00",
    "dateDebutStage": "2025-07-01",
    "dateFinStage": "2025-12-31",
    "statut": "SIGNEE",
    "signatureEtudiant": true,
    "signatureEntreprise": true,
    "signatureAdministration": true,
    "fichierPdf": "convention_1_1737023400000.pdf",
    "candidatureId": 1,
    "etudiantId": 1,
    "etudiantNom": "Dupont",
    "etudiantPrenom": "Jean",
    "etudiantEmail": "etudiant1@test.com",
    "offreId": 1,
    "offreTitre": "Développeur Full Stack - Stage",
    "entrepriseId": 1,
    "entrepriseNom": "TechCorp",
    "suiviStageId": 1,
    "hasSuiviStage": true
  }
]
```

---

### 2. Récupérer une convention par ID

**GET** `/api/conventions/{id}`

**Description**: Récupère les détails d'une convention par son ID.

**Authentification**: Requise (JWT)

**Paramètres de chemin**:
- `id` (Long) - ID de la convention

**Réponse 200 OK**:
```json
{
  "id": 1,
  "dateGeneration": "2025-01-15T10:30:00",
  "dateDebutStage": "2025-07-01",
  "dateFinStage": "2025-12-31",
  "statut": "SIGNEE",
  "signatureEtudiant": true,
  "signatureEntreprise": true,
  "signatureAdministration": true,
  "fichierPdf": "convention_1_1737023400000.pdf",
  "candidatureId": 1,
  "etudiantId": 1,
  "etudiantNom": "Dupont",
  "etudiantPrenom": "Jean",
  "etudiantEmail": "etudiant1@test.com",
  "offreId": 1,
  "offreTitre": "Développeur Full Stack - Stage",
  "entrepriseId": 1,
  "entrepriseNom": "TechCorp",
  "suiviStageId": 1,
  "hasSuiviStage": true
}
```

**Réponse 404 Not Found**:
```json
{
  "message": "Convention non trouvée avec l'ID: 1",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

### 3. Récupérer mes conventions

**GET** `/api/conventions/mes-conventions`

**Description**: Récupère toutes les conventions de l'utilisateur authentifié (étudiant ou entreprise).

**Authentification**: Requise (JWT)

**Paramètres**: Aucun

**Réponse 200 OK**: Même format que la liste des conventions

---

### 4. Récupérer les conventions d'un étudiant

**GET** `/api/conventions/etudiant`

**Description**: Récupère toutes les conventions de l'étudiant authentifié.

**Authentification**: Requise (JWT) - Étudiant uniquement

**Paramètres**: Aucun

**Réponse 200 OK**: Même format que la liste des conventions

---

### 5. Récupérer les conventions d'une entreprise

**GET** `/api/conventions/entreprise`

**Description**: Récupère toutes les conventions de l'entreprise authentifiée.

**Authentification**: Requise (JWT) - Entreprise uniquement

**Paramètres**: Aucun

**Réponse 200 OK**: Même format que la liste des conventions

---

### 6. Signer convention (Étudiant)

**PUT** `/api/conventions/{id}/signer-etudiant`

**Description**: L'étudiant propriétaire signe la convention. (RG04)

**Authentification**: Requise (JWT) - Étudiant propriétaire uniquement

**Paramètres de chemin**:
- `id` (Long) - ID de la convention

**Règle métier (RG04)**: 
- Première signature → Statut passe de `BROUILLON` à `EN_ATTENTE_SIGNATURES`
- Vérifie que l'étudiant est le propriétaire de la convention
- Vérifie que l'étudiant n'a pas déjà signé

**Réponse 200 OK**: Convention mise à jour

**Réponse 400 Bad Request**:
```json
{
  "message": "Vous avez déjà signé cette convention",
  "timestamp": "2025-01-15T10:30:00"
}
```

**Réponse 403 Forbidden**:
```json
{
  "message": "Vous n'êtes pas autorisé à signer cette convention",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

### 7. Signer convention (Entreprise)

**PUT** `/api/conventions/{id}/signer-entreprise`

**Description**: L'entreprise propriétaire signe la convention. (RG04)

**Authentification**: Requise (JWT) - Entreprise propriétaire uniquement

**Paramètres de chemin**:
- `id` (Long) - ID de la convention

**Règle métier (RG04)**: 
- Deuxième signature → Statut reste `EN_ATTENTE_SIGNATURES`
- Vérifie que l'entreprise est le propriétaire de la convention
- Vérifie que l'entreprise n'a pas déjà signé

**Réponse 200 OK**: Convention mise à jour

**Réponse 400 Bad Request**:
```json
{
  "message": "L'entreprise a déjà signé cette convention",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

### 8. Signer convention (Administration)

**PUT** `/api/conventions/{id}/signer-admin`

**Description**: L'administration signe la convention. (RG04)

**Authentification**: Requise (JWT) - Administration uniquement

**Paramètres de chemin**:
- `id` (Long) - ID de la convention

**Règle métier (RG04)**: 
- Troisième signature → Statut passe de `EN_ATTENTE_SIGNATURES` à `SIGNEE`
- **PDF généré automatiquement** quand les 3 signatures sont collectées
- Vérifie que l'administration n'a pas déjà signé

**Réponse 200 OK**: Convention mise à jour avec PDF généré

**Réponse 400 Bad Request**:
```json
{
  "message": "L'administration a déjà signé cette convention",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

### 9. Générer PDF (manuel)

**POST** `/api/conventions/{id}/generer-pdf`

**Description**: Génère manuellement le PDF d'une convention signée.

**Authentification**: Requise (JWT)

**Paramètres de chemin**:
- `id` (Long) - ID de la convention

**Prérequis**: La convention doit être signée (statut = `SIGNEE`)

**Réponse 200 OK**: Convention mise à jour avec PDF généré

**Réponse 400 Bad Request**:
```json
{
  "message": "Impossible de générer le PDF. La convention doit être signée par les trois parties.",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

### 10. Télécharger PDF

**GET** `/api/conventions/{id}/pdf`

**Description**: Télécharge le fichier PDF d'une convention.

**Authentification**: Requise (JWT)

**Paramètres de chemin**:
- `id` (Long) - ID de la convention

**Réponse 200 OK**: Fichier PDF (Content-Type: application/pdf)

**Réponse 404 Not Found**: PDF non trouvé

---

### 11. Archiver convention

**PUT** `/api/conventions/{id}/archiver`

**Description**: Archive une convention signée. (Admin uniquement)

**Authentification**: Requise (JWT) - Administration uniquement

**Paramètres de chemin**:
- `id` (Long) - ID de la convention

**Prérequis**: La convention doit être signée (statut = `SIGNEE`)

**Règle métier**: 
- Statut passe de `SIGNEE` à `ARCHIVEE`

**Réponse 200 OK**: Convention archivée

**Réponse 400 Bad Request**:
```json
{
  "message": "Seules les conventions signées peuvent être archivées",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

## 📊 Suivi des Stages

### 1. Assigner un tuteur

**POST** `/api/suivis/assigner-tuteur`

**Description**: Assigner un tuteur à une convention signée. (Admin uniquement) (RG05, RG07)

**Authentification**: Requise (JWT) - Administration uniquement

**Corps de la requête**:
```json
{
  "conventionId": 1,
  "tuteurId": 1
}
```

**Règles métier**:
- **RG05**: Vérifie que le tuteur n'a pas déjà 10 étudiants actifs (max 10)
- **RG07**: Vérifie que l'étudiant n'a pas déjà un stage actif
- La convention doit être signée (statut = `SIGNEE`)
- Un SuiviStage est créé automatiquement avec état `NON_COMMENCE`

**Réponse 201 Created**:
```json
{
  "id": 1,
  "dateAffectation": "2025-01-15T10:30:00",
  "etatAvancement": "NON_COMMENCE",
  "commentaires": null,
  "derniereVisite": null,
  "conventionId": 1,
  "dateDebutStage": "2025-07-01",
  "dateFinStage": "2025-12-31",
  "tuteurId": 1,
  "tuteurNom": "Martin",
  "tuteurPrenom": "Pierre",
  "tuteurEmail": "tuteur1@test.com",
  "tuteurDepartement": "Informatique",
  "tuteurSpecialite": "Développement Web",
  "etudiantId": 1,
  "etudiantNom": "Dupont",
  "etudiantPrenom": "Jean",
  "etudiantEmail": "etudiant1@test.com",
  "etudiantNiveau": "L3",
  "etudiantFiliere": "Informatique",
  "offreId": 1,
  "offreTitre": "Développeur Full Stack - Stage"
}
```

**Réponse 400 Bad Request** (RG05):
```json
{
  "message": "Ce tuteur a atteint la limite de 10 étudiants actifs",
  "timestamp": "2025-01-15T10:30:00"
}
```

**Réponse 400 Bad Request** (RG07):
```json
{
  "message": "Cet étudiant a déjà un stage actif",
  "timestamp": "2025-01-15T10:30:00"
}
```

**Réponse 400 Bad Request**:
```json
{
  "message": "Seules les conventions signées peuvent recevoir un tuteur",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

### 2. Récupérer tous les suivis

**GET** `/api/suivis`

**Description**: Récupère tous les suivis de stages. (Admin uniquement)

**Authentification**: Requise (JWT) - Administration uniquement

**Paramètres**: Aucun

**Réponse 200 OK**: Liste des SuiviStageResponse

---

### 3. Récupérer un suivi par ID

**GET** `/api/suivis/{id}`

**Description**: Récupère les détails d'un suivi par son ID.

**Authentification**: Requise (JWT)

**Paramètres de chemin**:
- `id` (Long) - ID du suivi

**Réponse 200 OK**: SuiviStageResponse

---

### 4. Récupérer mes étudiants (Tuteur)

**GET** `/api/suivis/mes-etudiants`

**Description**: Récupère tous les étudiants suivis par le tuteur authentifié.

**Authentification**: Requise (JWT) - Tuteur uniquement

**Paramètres**: Aucun

**Réponse 200 OK**: Liste des SuiviStageResponse

---

### 5. Récupérer mon stage (Étudiant)

**GET** `/api/suivis/mon-stage`

**Description**: Récupère le stage actif de l'étudiant authentifié.

**Authentification**: Requise (JWT) - Étudiant uniquement

**Paramètres**: Aucun

**Réponse 200 OK**: SuiviStageResponse

**Réponse 404 Not Found**: Aucun stage actif trouvé

---

### 6. Mettre à jour l'avancement (Tuteur)

**PUT** `/api/suivis/{id}/avancement`

**Description**: Le tuteur met à jour l'avancement du stage (état, commentaires, date de visite).

**Authentification**: Requise (JWT) - Tuteur propriétaire uniquement

**Paramètres de chemin**:
- `id` (Long) - ID du suivi

**Corps de la requête**:
```json
{
  "etatAvancement": "EN_COURS",
  "commentaires": "L'étudiant progresse bien. Il a terminé la première phase du projet.",
  "derniereVisite": "2025-07-15"
}
```

**Paramètres** (tous optionnels):
- `etatAvancement` (String) - `NON_COMMENCE`, `EN_COURS`, `TERMINE`
- `commentaires` (String) - Commentaires du tuteur (max 5000 caractères)
- `derniereVisite` (LocalDate) - Date de la dernière visite

**Réponse 200 OK**: SuiviStageResponse mis à jour

**Réponse 400 Bad Request**:
```json
{
  "message": "État d'avancement invalide: INVALID",
  "timestamp": "2025-01-15T10:30:00"
}
```

**Réponse 403 Forbidden**:
```json
{
  "message": "Vous n'êtes pas autorisé à modifier ce suivi",
  "timestamp": "2025-01-15T10:30:00"
}
```

---

## 📈 Dashboard Administration

### 1. Statistiques du dashboard

**GET** `/api/admin/dashboard/stats`

**Description**: Récupère toutes les statistiques pour le dashboard administration.

**Authentification**: Requise (JWT) - Administration uniquement

**Paramètres**: Aucun

**Réponse 200 OK**:
```json
{
  "totalOffres": 150,
  "offresEnAttente": 20,
  "offresValidees": 120,
  "offresExpirees": 10,
  "totalCandidatures": 500,
  "candidaturesEnAttente": 100,
  "candidaturesAcceptees": 80,
  "candidaturesRefusees": 320,
  "totalConventions": 80,
  "conventionsBrouillon": 10,
  "conventionsEnAttenteSignatures": 20,
  "conventionsSignees": 45,
  "conventionsArchivees": 5,
  "totalSuivis": 45,
  "stagesNonCommence": 5,
  "stagesEnCours": 35,
  "stagesTermine": 5,
  "etudiantsEnStage": 40,
  "tuteursActifs": 12,
  "topEntreprises": [
    {
      "entrepriseId": 1,
      "nomEntreprise": "TechCorp",
      "nombreOffres": 15
    },
    {
      "entrepriseId": 2,
      "nomEntreprise": "DevInc",
      "nombreOffres": 12
    }
  ],
  "topTuteurs": [
    {
      "tuteurId": 1,
      "tuteurNom": "Martin",
      "tuteurPrenom": "Pierre",
      "nombreEtudiants": 8
    },
    {
      "tuteurId": 2,
      "tuteurNom": "Dubois",
      "tuteurPrenom": "Marie",
      "nombreEtudiants": 7
    }
  ],
  "distributionOffres": {
    "EN_ATTENTE": 20,
    "VALIDEE": 120,
    "REFUSEE": 10
  },
  "distributionCandidatures": {
    "EN_ATTENTE": 100,
    "ACCEPTEE": 80,
    "REFUSEE": 320
  },
  "distributionConventions": {
    "BROUILLON": 10,
    "EN_ATTENTE_SIGNATURES": 20,
    "SIGNEE": 45,
    "ARCHIVEE": 5
  },
  "distributionAvancement": {
    "NON_COMMENCE": 5,
    "EN_COURS": 35,
    "TERMINE": 5
  }
}
```

---

## 🔒 Codes de Réponse HTTP

- **200 OK**: Requête réussie
- **201 Created**: Ressource créée avec succès
- **400 Bad Request**: Erreur de validation ou règle métier non respectée
- **401 Unauthorized**: Non authentifié (token manquant ou invalide)
- **403 Forbidden**: Non autorisé (rôle insuffisant ou non propriétaire)
- **404 Not Found**: Ressource non trouvée
- **500 Internal Server Error**: Erreur serveur

---

## 📝 Notes Importantes

### Règles Métier (RG)

- **RG04**: Une convention nécessite 3 signatures (étudiant, entreprise, admin)
  - Première signature → `BROUILLON` → `EN_ATTENTE_SIGNATURES`
  - Troisième signature → `EN_ATTENTE_SIGNATURES` → `SIGNEE` + PDF généré automatiquement

- **RG05**: Un tuteur peut suivre max 10 étudiants actifs
  - Seulement les étudiants actifs sont comptés (état != `TERMINE`)

- **RG07**: Un étudiant ne peut avoir qu'un seul stage actif à la fois
  - Un stage est actif si `etatAvancement != TERMINE`

### Authentification

Tous les endpoints (sauf `/api/auth/**`) nécessitent un token JWT dans l'en-tête :
```
Authorization: Bearer <token>
```

### Statuts de Convention

- `BROUILLON`: Convention créée mais non signée
- `EN_ATTENTE_SIGNATURES`: En attente de signatures (1 ou 2 signatures)
- `SIGNEE`: Convention signée par les 3 parties (PDF généré)
- `ARCHIVEE`: Convention archivée (stage terminé)

### États d'Avancement

- `NON_COMMENCE`: Stage pas encore commencé
- `EN_COURS`: Stage en cours
- `TERMINE`: Stage terminé

---

**Documentation générée pour le Sprint 2**  
**Date**: $(date)

