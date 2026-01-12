# 🧪 Guide de Test Complet - Gestion de Stages

## 📋 Prérequis

1. **Base de données** : Exécuter le script SQL de test
   ```bash
   mysql -u root -p gestion_stages < src/main/resources/data-test.sql
   ```

2. **Backend** : Démarré sur `http://localhost:8080`
3. **Frontend** : Démarré sur `http://localhost:5173` (ou 3000)

## 🔐 Identifiants de Test

**Mot de passe pour tous : `password123`**

- **Admin** : `admin@gestion-stages.fr`
- **Étudiant 1** : `marie.martin@etudiant.fr` (L3 Informatique)
- **Étudiant 2** : `pierre.durand@etudiant.fr` (M1 Génie Logiciel)
- **Étudiant 3** : `sophie.bernard@etudiant.fr` (L2 Réseaux)
- **Étudiant 4** : `lucas.moreau@etudiant.fr` (M2 IA) - **A un stage actif**
- **Entreprise 1** : `contact@techcorp.fr` (TechCorp Solutions)
- **Entreprise 2** : `rh@innovasoft.fr` (InnovaSoft)
- **Entreprise 3** : `info@webdev.fr` (WebDev Agency)
- **Tuteur 1** : `prof.dubois@univ.fr` (Développement Logiciel)
- **Tuteur 2** : `prof.martinez@univ.fr` (Réseaux et Systèmes)

---

## 🔄 Workflow 1 : Authentification et Inscription

### 1.1 Test de Connexion

**Étapes** :
1. Ouvrir `http://localhost:5173`
2. Cliquer sur "Se connecter"
3. Tester avec différents rôles :
   - Admin : `admin@gestion-stages.fr` / `password123`
   - Étudiant : `marie.martin@etudiant.fr` / `password123`
   - Entreprise : `contact@techcorp.fr` / `password123`
   - Tuteur : `prof.dubois@univ.fr` / `password123`

**Résultat attendu** :
- ✅ Redirection vers le dashboard approprié selon le rôle
- ✅ Token JWT stocké
- ✅ Menu de navigation adapté au rôle

### 1.2 Test d'Inscription

**Étapes** :
1. Cliquer sur "S'inscrire"
2. Remplir le formulaire selon le rôle :
   - **Étudiant** : Email, mot de passe, nom, prénom, niveau, filière
   - **Entreprise** : Email, mot de passe, nom, prénom, nom entreprise, secteur
   - **Tuteur** : Email, mot de passe, nom, prénom, département, spécialité
3. Soumettre le formulaire

**Résultat attendu** :
- ✅ Compte créé avec succès
- ✅ Redirection vers le dashboard
- ✅ Email de confirmation (si configuré)

---

## 🔄 Workflow 2 : Gestion des Offres (Entreprise)

### 2.1 Créer une Offre

**Connexion** : `contact@techcorp.fr` / `password123`

**Étapes** :
1. Aller dans "Mes Offres" → "Créer une offre"
2. Remplir le formulaire :
   - Titre : "Développeur Full Stack - Stage"
   - Description : "Stage de 6 mois..."
   - Type : STAGE
   - Durée : 6 mois
   - Dates : Début et fin
   - Compétences : React, Node.js, JavaScript
   - Rémunération : 800€
   - Date expiration : Date future
3. Soumettre

**Résultat attendu** :
- ✅ Offre créée avec statut `EN_ATTENTE`
- ✅ Message de succès
- ✅ Offre visible dans "Mes Offres"

### 2.2 Modifier une Offre

**Étapes** :
1. Aller dans "Mes Offres"
2. Cliquer sur "Modifier" sur une offre en attente
3. Modifier le titre ou la description
4. Sauvegarder

**Résultat attendu** :
- ✅ Offre mise à jour
- ✅ Modifications visibles

### 2.3 Valider une Offre (Admin)

**Connexion** : `admin@gestion-stages.fr` / `password123`

**Étapes** :
1. Aller dans "Validation des offres"
2. Voir les offres en attente
3. Cliquer sur "Valider" pour une offre
4. Confirmer

**Résultat attendu** :
- ✅ Statut change à `VALIDEE`
- ✅ Offre visible publiquement
- ✅ Email envoyé à l'entreprise (si configuré)
- ✅ Bouton "Valider" disparaît après validation

---

## 🔄 Workflow 3 : Candidatures (Étudiant)

### 3.1 Consulter les Offres

**Connexion** : `marie.martin@etudiant.fr` / `password123`

**Étapes** :
1. Aller dans "Offres disponibles"
2. Voir la liste des offres validées
3. Cliquer sur une offre pour voir les détails

**Résultat attendu** :
- ✅ Liste des offres validées uniquement
- ✅ Détails complets de l'offre
- ✅ Offres expirées non visibles

### 3.2 Postuler à une Offre

**Étapes** :
1. Ouvrir une offre
2. Cliquer sur "Postuler"
3. Rédiger une lettre de motivation
4. Soumettre

**Résultat attendu** :
- ✅ Candidature créée avec statut `EN_ATTENTE`
- ✅ Message de succès
- ✅ Candidature visible dans "Mes Candidatures"
- ✅ Impossible de postuler deux fois (RG01)

### 3.3 Voir Mes Candidatures

**Étapes** :
1. Aller dans "Mes Candidatures"
2. Voir toutes les candidatures avec leurs statuts

**Résultat attendu** :
- ✅ Liste complète des candidatures
- ✅ Statuts visibles (EN_ATTENTE, ACCEPTEE, REFUSEE)
- ✅ Détails de chaque candidature

---

## 🔄 Workflow 4 : Gestion des Candidatures (Entreprise)

### 4.1 Voir les Candidatures Reçues

**Connexion** : `contact@techcorp.fr` / `password123`

**Étapes** :
1. Aller dans "Candidatures reçues"
2. Voir toutes les candidatures pour vos offres

**Résultat attendu** :
- ✅ Liste des candidatures
- ✅ Informations étudiant visibles
- ✅ Bouton "CV" pour télécharger le CV

### 4.2 Télécharger le CV d'un Candidat

**Étapes** :
1. Cliquer sur le bouton "CV" à côté d'une candidature
2. Le CV se télécharge

**Résultat attendu** :
- ✅ Téléchargement du fichier PDF/DOC
- ✅ Nom de fichier : `CV_Nom_Prenom.pdf`

### 4.3 Accepter une Candidature

**Étapes** :
1. Cliquer sur "Accepter" pour une candidature
2. Confirmer

**Résultat attendu** :
- ✅ Statut change à `ACCEPTEE`
- ✅ Convention créée automatiquement (RG03)
- ✅ Email envoyé à l'étudiant (si configuré)
- ✅ Boutons "Accepter/Refuser" disparaissent

### 4.4 Refuser une Candidature

**Étapes** :
1. Cliquer sur "Refuser"
2. Entrer un commentaire (optionnel)
3. Confirmer

**Résultat attendu** :
- ✅ Statut change à `REFUSEE`
- ✅ Commentaire enregistré
- ✅ Email envoyé à l'étudiant (si configuré)

---

## 🔄 Workflow 5 : Conventions (RG04)

### 5.1 Voir Mes Conventions (Étudiant)

**Connexion** : `marie.martin@etudiant.fr` / `password123`

**Étapes** :
1. Aller dans "Mes Conventions" (ou Dashboard)
2. Voir les conventions liées à vos candidatures acceptées

**Résultat attendu** :
- ✅ Liste des conventions
- ✅ Statut visible (EN_ATTENTE_SIGNATURES, SIGNEE)
- ✅ État des signatures visible

### 5.2 Signer une Convention (Étudiant)

**Étapes** :
1. Ouvrir une convention en attente
2. Cliquer sur "Signer"
3. Confirmer

**Résultat attendu** :
- ✅ Signature étudiante enregistrée
- ✅ Statut mis à jour
- ✅ Email envoyé (si configuré)

### 5.3 Signer une Convention (Entreprise)

**Connexion** : `contact@techcorp.fr` / `password123`

**Étapes** :
1. Aller dans "Mes Conventions"
2. Ouvrir une convention
3. Cliquer sur "Signer"
4. Confirmer

**Résultat attendu** :
- ✅ Signature entreprise enregistrée
- ✅ Statut mis à jour

### 5.4 Signer une Convention (Admin)

**Connexion** : `admin@gestion-stages.fr` / `password123`

**Étapes** :
1. Aller dans "Conventions"
2. Ouvrir une convention signée par étudiant et entreprise
3. Cliquer sur "Signer"
4. Confirmer

**Résultat attendu** :
- ✅ Signature admin enregistrée
- ✅ Statut change à `SIGNEE`
- ✅ PDF généré automatiquement (RG04)
- ✅ Email envoyé aux parties (si configuré)

### 5.5 Télécharger le PDF de Convention

**Étapes** :
1. Ouvrir une convention signée
2. Cliquer sur "Télécharger PDF"

**Résultat attendu** :
- ✅ PDF téléchargé avec design professionnel
- ✅ Toutes les informations présentes
- ✅ Signatures visibles

---

## 🔄 Workflow 6 : Suivi de Stage (RG05, RG07)

### 6.1 Assigner un Tuteur (Admin)

**Connexion** : `admin@gestion-stages.fr` / `password123`

**Étapes** :
1. Aller dans "Suivis de Stage"
2. Sélectionner une convention signée
3. Assigner un tuteur
4. Confirmer

**Résultat attendu** :
- ✅ Tuteur assigné
- ✅ Suivi de stage créé
- ✅ Email envoyé au tuteur (si configuré)
- ✅ Impossible d'assigner si tuteur a déjà 10 étudiants (RG05)
- ✅ Impossible d'assigner si étudiant a déjà un stage actif (RG07)

### 6.2 Voir Mes Étudiants (Tuteur)

**Connexion** : `prof.dubois@univ.fr` / `password123`

**Étapes** :
1. Aller dans "Mes Étudiants"
2. Voir la liste des étudiants assignés

**Résultat attendu** :
- ✅ Liste des étudiants suivis
- ✅ Informations du stage
- ✅ État d'avancement visible

### 6.3 Mettre à Jour le Suivi (Tuteur)

**Étapes** :
1. Cliquer sur "Mettre à jour" pour un étudiant
2. Modifier l'état d'avancement
3. Ajouter des commentaires
4. Mettre à jour la date de dernière visite
5. Sauvegarder

**Résultat attendu** :
- ✅ Suivi mis à jour
- ✅ Modifications visibles
- ✅ Message de succès

### 6.4 Voir Mon Stage (Étudiant)

**Connexion** : `lucas.moreau@etudiant.fr` / `password123`

**Étapes** :
1. Aller dans "Mon Stage"
2. Voir les informations du stage actif

**Résultat attendu** :
- ✅ Informations du stage
- ✅ Tuteur assigné visible
- ✅ État d'avancement visible
- ✅ Commentaires du tuteur

---

## 🔄 Workflow 7 : Gestion du CV

### 7.1 Uploader un CV (Étudiant)

**Connexion** : `marie.martin@etudiant.fr` / `password123`

**Étapes** :
1. Aller dans "Mon Profil"
2. Section "Mon CV"
3. Cliquer sur "Télécharger mon CV"
4. Sélectionner un fichier (PDF, DOC, DOCX, max 5MB)
5. Uploader

**Résultat attendu** :
- ✅ CV uploadé avec succès
- ✅ Indicateur visuel "CV téléchargé"
- ✅ Boutons "Télécharger", "Remplacer", "Supprimer" disponibles

### 7.2 Télécharger son CV

**Étapes** :
1. Cliquer sur "Télécharger mon CV"

**Résultat attendu** :
- ✅ Fichier téléchargé
- ✅ Nom : `CV_Nom_Prenom.pdf`

### 7.3 Remplacer un CV

**Étapes** :
1. Cliquer sur "Remplacer le CV"
2. Sélectionner un nouveau fichier
3. Uploader

**Résultat attendu** :
- ✅ Ancien CV supprimé
- ✅ Nouveau CV uploadé
- ✅ Message de succès

### 7.4 Télécharger CV d'un Candidat (Entreprise)

**Connexion** : `contact@techcorp.fr` / `password123`

**Étapes** :
1. Aller dans "Candidatures reçues"
2. Cliquer sur le bouton "CV" à côté d'une candidature

**Résultat attendu** :
- ✅ CV téléchargé
- ✅ Erreur si l'étudiant n'a pas de CV

---

## 🔄 Workflow 8 : Profil Utilisateur

### 8.1 Voir Mon Profil

**Étapes** :
1. Cliquer sur "Mon Profil" dans le menu
2. Voir toutes les informations

**Résultat attendu** :
- ✅ Informations complètes affichées
- ✅ Champs spécifiques au rôle visibles

### 8.2 Modifier Mon Profil

**Étapes** :
1. Cliquer sur "Modifier"
2. Modifier les champs (nom, prénom, téléphone, etc.)
3. Sauvegarder

**Résultat attendu** :
- ✅ Profil mis à jour
- ✅ Modifications visibles immédiatement
- ✅ Message de succès

### 8.3 Changer le Mot de Passe

**Étapes** :
1. Section "Changement de mot de passe"
2. Cliquer sur "Changer le mot de passe"
3. Entrer :
   - Mot de passe actuel
   - Nouveau mot de passe (min 8 caractères)
   - Confirmer le nouveau mot de passe
4. Sauvegarder

**Résultat attendu** :
- ✅ Mot de passe changé
- ✅ Possibilité de se reconnecter avec le nouveau mot de passe
- ✅ Erreur si ancien mot de passe incorrect

---

## 🔄 Workflow 9 : Dashboard Admin

### 9.1 Voir les Statistiques

**Connexion** : `admin@gestion-stages.fr` / `password123`

**Étapes** :
1. Aller dans "Tableau de bord"
2. Voir les statistiques

**Résultat attendu** :
- ✅ Nombre total d'offres, candidatures, conventions
- ✅ Offres en attente, validées, expirées
- ✅ Candidatures par statut
- ✅ Conventions par statut
- ✅ Top entreprises
- ✅ Top tuteurs

### 9.2 Marquer les Offres Expirées (RG06)

**Étapes** :
1. Appeler l'endpoint : `POST /api/offres/marquer-expirees`
   (ou créer un scheduler pour l'exécution automatique)

**Résultat attendu** :
- ✅ Offres expirées marquées automatiquement
- ✅ Statut change de `VALIDEE` à `EXPIREE`
- ✅ Offres expirées non visibles publiquement

---

## 🔄 Workflow 10 : Règles Métier (Tests de Validation)

### 10.1 RG01 - Une seule candidature par offre

**Étapes** :
1. Se connecter en tant qu'étudiant
2. Postuler à une offre
3. Essayer de postuler à nouveau à la même offre

**Résultat attendu** :
- ✅ Erreur : "Vous avez déjà postulé à cette offre"

### 10.2 RG02 - Offres validées uniquement

**Étapes** :
1. Se connecter en tant qu'étudiant
2. Voir les offres disponibles

**Résultat attendu** :
- ✅ Seules les offres `VALIDEE` sont visibles
- ✅ Offres `EN_ATTENTE` non visibles

### 10.3 RG03 - Convention auto-générée

**Étapes** :
1. Entreprise accepte une candidature
2. Vérifier qu'une convention est créée automatiquement

**Résultat attendu** :
- ✅ Convention créée avec statut `EN_ATTENTE_SIGNATURES`
- ✅ Convention liée à la candidature acceptée

### 10.4 RG04 - 3 signatures requises

**Étapes** :
1. Étudiant signe → Statut reste `EN_ATTENTE_SIGNATURES`
2. Entreprise signe → Statut reste `EN_ATTENTE_SIGNATURES`
3. Admin signe → Statut change à `SIGNEE` + PDF généré

**Résultat attendu** :
- ✅ PDF généré uniquement après 3 signatures
- ✅ Impossible de générer PDF avant 3 signatures

### 10.5 RG05 - Max 10 étudiants par tuteur

**Étapes** :
1. Assigner 10 étudiants à un tuteur
2. Essayer d'assigner un 11ème étudiant

**Résultat attendu** :
- ✅ Erreur : "Ce tuteur a atteint la limite de 10 étudiants actifs"

### 10.6 RG06 - Offres expirées

**Étapes** :
1. Créer une offre avec date d'expiration passée
2. Valider l'offre
3. Appeler `POST /api/offres/marquer-expirees`
4. Vérifier que l'offre est marquée `EXPIREE`

**Résultat attendu** :
- ✅ Offre marquée comme expirée
- ✅ Offre non visible publiquement

### 10.7 RG07 - Un seul stage actif par étudiant

**Étapes** :
1. Assigner un tuteur à un étudiant qui a déjà un stage actif
2. Essayer d'assigner un autre tuteur

**Résultat attendu** :
- ✅ Erreur : "Cet étudiant a déjà un stage actif"

---

## 🔄 Workflow 11 : Recherche et Filtres

### 11.1 Rechercher des Offres

**Étapes** :
1. Aller dans "Offres disponibles"
2. Utiliser la barre de recherche
3. Entrer un mot-clé (ex: "React", "Java")

**Résultat attendu** :
- ✅ Résultats filtrés par titre
- ✅ Offres pertinentes affichées

### 11.2 Filtrer par Type d'Offre

**Étapes** :
1. Filtrer par "STAGE" ou "ALTERNANCE"

**Résultat attendu** :
- ✅ Liste filtrée selon le type

---

## 🔄 Workflow 12 : Gestion des Erreurs

### 12.1 Test d'Erreurs d'Authentification

**Étapes** :
1. Essayer de se connecter avec un mauvais mot de passe
2. Essayer d'accéder à une page sans être connecté

**Résultat attendu** :
- ✅ Message d'erreur clair
- ✅ Redirection vers login si non authentifié

### 12.2 Test d'Erreurs d'Autorisation

**Étapes** :
1. Se connecter en tant qu'étudiant
2. Essayer d'accéder à `/admin/dashboard`

**Résultat attendu** :
- ✅ Accès refusé
- ✅ Message d'erreur approprié

### 12.3 Test de Validation

**Étapes** :
1. Créer une offre avec des dates invalides (fin < début)
2. Uploader un CV trop volumineux (>5MB)
3. Uploader un CV avec un mauvais format

**Résultat attendu** :
- ✅ Messages d'erreur de validation clairs
- ✅ Formulaire non soumis

---

## 📊 Checklist de Test Complète

### Authentification
- [ ] Connexion avec tous les rôles
- [ ] Inscription pour chaque type d'utilisateur
- [ ] Déconnexion
- [ ] Gestion des erreurs de connexion

### Offres
- [ ] Créer une offre (entreprise)
- [ ] Modifier une offre (entreprise)
- [ ] Supprimer une offre (entreprise)
- [ ] Valider une offre (admin)
- [ ] Voir les offres publiques (étudiant)
- [ ] Rechercher des offres
- [ ] Marquer offres expirées (RG06)

### Candidatures
- [ ] Postuler à une offre (étudiant)
- [ ] Voir mes candidatures (étudiant)
- [ ] Voir candidatures reçues (entreprise)
- [ ] Accepter candidature (entreprise)
- [ ] Refuser candidature (entreprise)
- [ ] Télécharger CV candidat (entreprise)
- [ ] Test RG01 (double candidature)

### Conventions
- [ ] Voir mes conventions (tous rôles)
- [ ] Signer convention (étudiant)
- [ ] Signer convention (entreprise)
- [ ] Signer convention (admin)
- [ ] Générer PDF (automatique après 3 signatures)
- [ ] Télécharger PDF
- [ ] Test RG04 (3 signatures)

### Suivi de Stage
- [ ] Assigner tuteur (admin)
- [ ] Voir mes étudiants (tuteur)
- [ ] Mettre à jour suivi (tuteur)
- [ ] Voir mon stage (étudiant)
- [ ] Test RG05 (max 10 étudiants)
- [ ] Test RG07 (un seul stage actif)

### CV
- [ ] Uploader CV (étudiant)
- [ ] Télécharger son CV (étudiant)
- [ ] Remplacer CV (étudiant)
- [ ] Supprimer CV (étudiant)
- [ ] Télécharger CV candidat (entreprise)
- [ ] Validation format/taille

### Profil
- [ ] Voir profil
- [ ] Modifier profil
- [ ] Changer mot de passe
- [ ] Champs spécifiques par rôle

### Dashboard
- [ ] Statistiques admin
- [ ] Navigation selon rôle
- [ ] Liens fonctionnels

---

## 🐛 Scénarios de Test d'Erreurs

### Erreurs à Tester
1. **Connexion** : Mauvais email/mot de passe
2. **Autorisation** : Accès non autorisé
3. **Validation** : Champs manquants/invalides
4. **Fichiers** : Format/taille incorrecte
5. **Règles métier** : Violation RG01-RG07
6. **Ressources** : ID inexistant
7. **Conflits** : Double candidature, double signature

---

## 📝 Notes de Test

- **Temps estimé** : 2-3 heures pour tous les workflows
- **Outils recommandés** : Postman pour les tests API, navigateur pour le frontend
- **Logs** : Vérifier les logs backend pour les erreurs
- **Base de données** : Réinitialiser avec `data-test.sql` si nécessaire

---

## ✅ Critères de Succès

L'application est fonctionnelle si :
- ✅ Tous les workflows ci-dessus fonctionnent
- ✅ Toutes les règles métier (RG01-RG07) sont respectées
- ✅ Les erreurs sont gérées proprement
- ✅ Les emails sont envoyés (si configurés)
- ✅ Les PDFs sont générés correctement
- ✅ La sécurité est respectée (autorisations)
