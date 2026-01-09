# Collection Postman Unifiée - Sprint 1 & 2

## 📋 Description

Cette collection Postman contient **TOUS** les endpoints et workflows de test pour les Sprint 1 et 2 du projet Gestion de Stages.

## 📦 Structure de la Collection

### Sections Principales

1. **1. Authentification** - Inscription et connexion pour tous les rôles
2. **2. Offres** - CRUD des offres de stage/alternance
3. **3. Candidatures** - Gestion des candidatures
4. **4. Conventions** - Gestion des conventions avec signatures
5. **5. Suivi des Stages** - Assignation de tuteurs et suivi
6. **6. Dashboard Administration** - Statistiques du dashboard
7. **7. Workflows de Test** - 8 scénarios de test end-to-end

### Workflows de Test Inclus

#### Workflow 1 - Convention complète avec signatures
- Cycle complet : Candidature acceptée → Convention → 3 Signatures → PDF → Assignation Tuteur
- 13 étapes séquentielles
- Validation du cycle de vie complet d'une convention

#### Workflow 2 - Assignation Tuteur et Suivi
- Assignation d'un tuteur à une convention signée
- Mise à jour de l'avancement
- Suivi du stage par étudiant et tuteur
- 5 étapes séquentielles

#### Workflow 3 - Dashboard Administration
- Récupération des statistiques complètes
- Validation de toutes les données du dashboard
- 2 étapes

#### Workflow 4 - Test RG04 (3 signatures)
- Test de la règle métier RG04
- Validation des signatures
- Génération automatique du PDF
- 4 étapes

#### Workflow 5 - Test RG05 (Max 10 étudiants)
- Test de la limite de 10 étudiants par tuteur
- Validation de la règle métier RG05
- 2 étapes (avec note pour prérequis)

#### Workflow 6 - Test RG07 (Un seul stage actif)
- Test qu'un étudiant ne peut avoir qu'un seul stage actif
- Validation de la règle métier RG07
- 4 étapes

#### Workflow 7 - Tests Sécurité et Permissions
- Tests des permissions d'accès
- Validation de la sécurité des endpoints
- 3 étapes

#### Workflow 8 - Archiver Convention
- Test de l'archivage d'une convention
- Validation des règles d'archivage
- 2 étapes

## 🚀 Utilisation

### Import dans Postman

1. Ouvrir Postman
2. Cliquer sur **Import**
3. Sélectionner le fichier `postman_collection.json`
4. La collection sera importée avec tous les endpoints et workflows

### Variables d'Environnement

Les variables suivantes sont automatiquement gérées :

- `baseUrl` : `http://localhost:8080` (déjà configuré)
- `studentToken` : Token JWT de l'étudiant (auto-sauvegardé)
- `companyToken` : Token JWT de l'entreprise (auto-sauvegardé)
- `adminToken` : Token JWT de l'admin (auto-sauvegardé)
- `tuteurToken` : Token JWT du tuteur (auto-sauvegardé)
- `offreId` : ID de l'offre (auto-sauvegardé)
- `candidatureId` : ID de la candidature (auto-sauvegardé)
- `conventionId` : ID de la convention (auto-sauvegardé)
- `tuteurId` : ID du tuteur (auto-sauvegardé)
- `suiviStageId` : ID du suivi de stage (auto-sauvegardé)
- `suiviStageId1` : ID du premier suivi de stage (auto-sauvegardé pour tests RG07)

### Ordre d'Exécution Recommandé

#### Pour tester les endpoints individuels :

1. **Authentification** (section 1)
   - Register pour chaque rôle
   - Login pour chaque rôle (les tokens seront sauvegardés)

2. **Offres** (section 2)
   - Créer une offre
   - Valider l'offre (admin)
   - Consulter les offres publiques

3. **Candidatures** (section 3)
   - Postuler à une offre
   - Accepter la candidature (entreprise)

4. **Conventions** (section 4)
   - Récupérer les conventions
   - Signer les conventions (étudiant, entreprise, admin)
   - Télécharger le PDF

5. **Suivi des Stages** (section 5)
   - Register/Login tuteur
   - Assigner un tuteur (admin)
   - Mettre à jour l'avancement (tuteur)

6. **Dashboard** (section 6)
   - Récupérer les statistiques (admin)

#### Pour tester les workflows complets :

1. Exécuter **Workflow 1** pour valider le cycle complet
2. Exécuter **Workflow 2** pour valider l'assignation et le suivi
3. Exécuter **Workflow 3** pour valider le dashboard
4. Exécuter **Workflow 4** pour tester RG04
5. Exécuter **Workflow 5** pour tester RG05 (nécessite 11 conventions)
6. Exécuter **Workflow 6** pour tester RG07
7. Exécuter **Workflow 7** pour tester la sécurité
8. Exécuter **Workflow 8** pour tester l'archivage

### Exécution en Série (Collection Runner)

Vous pouvez utiliser le **Collection Runner** de Postman pour exécuter automatiquement tous les workflows :

1. Cliquer sur la collection
2. Cliquer sur **Run**
3. Sélectionner les workflows à exécuter
4. Cliquer sur **Run Workflow 1** (ou autres)

**Note** : Les workflows sont conçus pour s'exécuter en séquence, car chaque étape dépend des précédentes (les IDs sont sauvegardés automatiquement).

## ✅ Tests Automatiques

Chaque requête contient des scripts de test qui :
- Vérifient les codes de statut HTTP
- Valident les réponses JSON
- Sauvegardent automatiquement les IDs et tokens
- Vérifient les règles métier (RG04, RG05, RG07)

## 📝 Notes Importantes

1. **Prérequis** : Assurez-vous que l'application Spring Boot est démarrée sur `http://localhost:8080`

2. **Données de test** : Certains workflows nécessitent des données préexistantes :
   - Workflow 5 (RG05) : 11 conventions signées avec 11 étudiants différents
   - Workflow 6 (RG07) : Une convention signée avec un étudiant

3. **Ordre d'exécution** : Les workflows doivent être exécutés dans l'ordre pour que les variables soient correctement définies

4. **Tokens** : Les tokens sont automatiquement sauvegardés après chaque login et réutilisés dans les requêtes suivantes

5. **Variables** : Les IDs (offreId, candidatureId, conventionId, etc.) sont automatiquement extraits des réponses et sauvegardés

## 🔄 Migration depuis les anciennes collections

Si vous aviez déjà la collection Sprint 1 :
- Les endpoints Sprint 1 sont conservés (sections 1-3)
- Les nouveaux endpoints Sprint 2 sont ajoutés (sections 4-6)
- Les workflows de test sont ajoutés (section 7)

**Vous pouvez importer cette nouvelle collection pour remplacer l'ancienne.**

## 📚 Documentation Complémentaire

- `API_ENDPOINTS_SPRINT2.md` - Documentation complète des endpoints Sprint 2
- `TEST_WORKFLOW_SPRINT2.json` - Structure JSON originale des workflows (référence)
- `POSTMAN_GUIDE.md` - Guide d'utilisation général de Postman
- `SPRINT2_VERIFICATION_REPORT.md` - Rapport de vérification du Sprint 2

---

**Collection créée le** : $(date)  
**Version** : Sprint 1 & 2 (Complète)

