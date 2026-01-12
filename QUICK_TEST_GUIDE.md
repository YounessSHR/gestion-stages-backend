# ⚡ Guide de Test Rapide

## 🚀 Démarrage Rapide

```bash
# 1. Charger les données de test
mysql -u root -p gestion_stages < src/main/resources/data-test.sql

# 2. Démarrer le backend
cd gestion-stages-backend
./mvnw spring-boot:run

# 3. Démarrer le frontend (dans un autre terminal)
cd gestion-stages-frontend
npm run dev
```

## 🔑 Identifiants Rapides

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | `admin@gestion-stages.fr` | `password123` |
| Étudiant | `marie.martin@etudiant.fr` | `password123` |
| Entreprise | `contact@techcorp.fr` | `password123` |
| Tuteur | `prof.dubois@univ.fr` | `password123` |

## 📋 Workflow de Test Minimal (15 minutes)

### 1. Test Complet End-to-End (5 min)

**Étudiant** → **Entreprise** → **Admin** → **Tuteur**

1. **Étudiant** (`marie.martin@etudiant.fr`)
   - Uploader un CV dans "Mon Profil"
   - Consulter les offres
   - Postuler à une offre

2. **Entreprise** (`contact@techcorp.fr`)
   - Voir les candidatures reçues
   - Télécharger le CV du candidat
   - Accepter la candidature

3. **Admin** (`admin@gestion-stages.fr`)
   - Valider une offre en attente
   - Signer la convention (après signatures étudiant + entreprise)
   - Assigner un tuteur à la convention signée

4. **Tuteur** (`prof.dubois@univ.fr`)
   - Voir "Mes Étudiants"
   - Mettre à jour le suivi d'un stage

### 2. Test des Règles Métier (5 min)

- **RG01** : Essayer de postuler 2 fois → Erreur
- **RG02** : Voir que seules les offres validées sont publiques
- **RG03** : Accepter candidature → Convention créée automatiquement
- **RG04** : Signer 3 fois → PDF généré automatiquement
- **RG05** : Assigner 11 étudiants au même tuteur → Erreur
- **RG06** : Marquer offres expirées → `POST /api/offres/marquer-expirees`
- **RG07** : Assigner 2 stages actifs au même étudiant → Erreur

### 3. Test des Fonctionnalités Clés (5 min)

- ✅ Upload/Download CV
- ✅ Génération PDF convention
- ✅ Changement de mot de passe
- ✅ Modification de profil
- ✅ Dashboard avec statistiques

## 🧪 Tests avec Postman

1. Importer `postman_collection.json` dans Postman
2. Configurer les variables d'environnement :
   - `baseUrl` : `http://localhost:8080`
   - `token` : (auto-sauvegardé après login)
3. Exécuter les workflows dans l'ordre :
   - Workflow 1 : Authentification
   - Workflow 2 : Création offre → Validation → Candidature
   - Workflow 3 : Conventions et signatures
   - Workflow 4 : Test RG04
   - Workflow 5 : Test RG05
   - Workflow 6 : Test RG07

## ✅ Checklist Rapide

- [ ] Connexion fonctionne pour tous les rôles
- [ ] Upload CV fonctionne
- [ ] Création offre → Validation → Candidature → Convention
- [ ] 3 signatures → PDF généré
- [ ] Assignation tuteur → Suivi stage
- [ ] Toutes les règles métier respectées
- [ ] Pas d'erreurs dans la console
- [ ] Design PDF professionnel

## 🐛 Tests d'Erreurs Rapides

1. Mauvais mot de passe → Message d'erreur
2. Postuler 2 fois → Erreur RG01
3. Upload CV > 5MB → Erreur validation
4. Accès admin en tant qu'étudiant → Accès refusé
5. Générer PDF avant 3 signatures → Erreur RG04

---

**Pour les détails complets, voir `TESTING_WORKFLOW.md`**
