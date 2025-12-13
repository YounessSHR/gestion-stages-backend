# Guide d'utilisation de la collection Postman

## 📋 Prérequis

1. **Démarrer l'application Spring Boot**
   ```bash
   ./mvnw spring-boot:run
   ```
   Ou depuis votre IDE (IntelliJ, Eclipse, etc.)

2. **Importer la collection dans Postman**
   - Ouvrir Postman
   - Cliquer sur "Import"
   - Sélectionner le fichier `postman_collection.json`

3. **Configurer les variables d'environnement**
   - La collection utilise des variables automatiques qui seront remplies lors des requêtes
   - `baseUrl` : `http://localhost:8080` (déjà configuré)

## 🚀 Ordre d'exécution recommandé

### Étape 1 : Authentification
1. **Register - Étudiant** : Créer un compte étudiant
2. **Register - Entreprise** : Créer un compte entreprise
3. **Register - Administration** : Créer un compte admin
4. **Login - Étudiant** : Se connecter (le token sera sauvegardé automatiquement)
5. **Login - Entreprise** : Se connecter (le token sera sauvegardé automatiquement)
6. **Login - Administration** : Se connecter (le token sera sauvegardé automatiquement)

### Étape 2 : Gestion des offres
1. **Créer une offre (Entreprise)** : Créer une offre de stage
   - L'offre sera créée avec le statut `EN_ATTENTE`
   - L'ID de l'offre sera sauvegardé automatiquement

2. **Récupérer toutes les offres publiques** : 
   - Ne devrait retourner aucune offre (car pas encore validée)
   - Accessible sans authentification

3. **Valider une offre (Admin)** : 
   - Change le statut de `EN_ATTENTE` à `VALIDEE`
   - Après validation, l'offre apparaîtra dans les offres publiques

4. **Récupérer toutes les offres publiques** (à nouveau) :
   - Devrait maintenant retourner l'offre validée

5. **Mes offres (Entreprise)** : Voir toutes les offres de l'entreprise connectée

6. **Rechercher des offres par titre** : Recherche publique

7. **Modifier une offre** : Modifier les détails d'une offre

8. **Supprimer une offre** : Supprimer une offre (optionnel)

### Étape 3 : Gestion des candidatures
1. **Postuler à une offre (Étudiant)** :
   - L'étudiant postule à l'offre validée
   - L'ID de la candidature sera sauvegardé automatiquement
   - **RG01** : Si vous essayez de postuler deux fois, cela échouera

2. **Mes candidatures (Étudiant)** : Voir toutes les candidatures de l'étudiant

3. **Candidatures pour une offre (Entreprise)** : 
   - L'entreprise voit toutes les candidatures pour son offre

4. **Accepter une candidature (Entreprise)** :
   - Change le statut de `EN_ATTENTE` à `ACCEPTEE`
   - **RG03** : Génère automatiquement une convention avec statut `BROUILLON`

5. **Refuser une candidature (Entreprise)** : Refuser avec un commentaire

6. **Tester RG01 - Double candidature** :
   - Doit retourner une erreur 400
   - Message : "Vous avez déjà postulé à cette offre"

## 🔐 Authentification

Toutes les requêtes authentifiées utilisent le **Bearer Token** (JWT).

Les tokens sont automatiquement sauvegardés dans les variables :
- `studentToken` : Token de l'étudiant
- `companyToken` : Token de l'entreprise
- `adminToken` : Token de l'administration

## 📝 Variables automatiques

La collection sauvegarde automatiquement :
- `offreId` : ID de la dernière offre créée
- `candidatureId` : ID de la dernière candidature créée
- `studentToken`, `companyToken`, `adminToken` : Tokens JWT après login

## ✅ Tests à effectuer

### Tests de validation
- ✅ Créer une offre sans être connecté → Doit échouer (401)
- ✅ Postuler sans être connecté → Doit échouer (401)
- ✅ Valider une offre sans être admin → Doit échouer (403)
- ✅ Postuler deux fois à la même offre → Doit échouer (400) - RG01
- ✅ Postuler à une offre non validée → Doit échouer (400) - RG02
- ✅ Postuler à une offre expirée → Doit échouer (400)

### Tests de succès
- ✅ Créer une offre (entreprise) → 201
- ✅ Valider une offre (admin) → 200
- ✅ Voir les offres publiques → 200
- ✅ Postuler à une offre (étudiant) → 201
- ✅ Accepter une candidature (entreprise) → 200
- ✅ Vérifier qu'une convention a été créée → Vérifier en base de données

## 🐛 Dépannage

### Erreur 401 (Unauthorized)
- Vérifier que vous êtes connecté
- Vérifier que le token est valide (pas expiré)
- Relancer la requête de login

### Erreur 403 (Forbidden)
- Vérifier que vous avez le bon rôle (entreprise pour créer offres, admin pour valider, etc.)

### Erreur 400 (Bad Request)
- Vérifier le format JSON de la requête
- Vérifier les validations (dates futures, champs obligatoires, etc.)

### Erreur 404 (Not Found)
- Vérifier que l'ID existe
- Vérifier que vous avez les droits d'accès

## 📊 Codes de statut HTTP

- **200 OK** : Requête réussie
- **201 Created** : Ressource créée avec succès
- **204 No Content** : Suppression réussie
- **400 Bad Request** : Erreur de validation ou règle métier
- **401 Unauthorized** : Non authentifié
- **403 Forbidden** : Pas les permissions
- **404 Not Found** : Ressource non trouvée
- **500 Internal Server Error** : Erreur serveur

## 🔍 Vérification en base de données

Pour vérifier que tout fonctionne correctement, vous pouvez consulter la base de données MySQL :

```sql
-- Voir toutes les offres
SELECT * FROM offre_stage;

-- Voir toutes les candidatures
SELECT * FROM candidature;

-- Voir toutes les conventions (créées automatiquement lors de l'acceptation)
SELECT * FROM convention;

-- Voir les utilisateurs
SELECT * FROM utilisateur;
```

## 📌 Notes importantes

1. **RG01** : Un étudiant ne peut postuler qu'une fois à la même offre
2. **RG02** : Une offre doit être validée par l'administration avant d'être visible publiquement
3. **RG03** : Une candidature acceptée déclenche automatiquement la génération d'une convention

## 🎯 Prochaines étapes (Sprint 2)

Une fois Sprint 1 validé, vous pourrez tester :
- Gestion des conventions (signatures, PDF)
- Suivi des stages
- Dashboard administration

