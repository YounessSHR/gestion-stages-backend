# Phase 1 : Analyse et Conception

## Acteurs
1. Étudiant : Consulter offres, postuler, suivre candidatures
2. Entreprise : Publier offres, gérer candidatures, valider conventions
3. Administration : Valider offres, générer conventions, assigner tuteurs
4. Tuteur : Suivre étudiants, commenter avancement

## Cas d'Utilisation Principaux
- S'authentifier
- Gérer profil
- Publier/Rechercher offres
- Postuler à une offre
- Accepter/Refuser candidature
- Générer convention
- Suivre stage

## Règles de Gestion
RG01: Un étudiant ne peut postuler qu'une fois à la même offre
RG02: Une offre doit être validée par l'administration avant d'être visible
RG03: Une candidature acceptée déclenche la génération d'une convention
RG04: Une convention nécessite 3 signatures (étudiant, entreprise, admin)
RG05: Un tuteur peut suivre max 10 étudiants
RG06: Une offre expirée n'est plus consultable
RG07: Un étudiant ne peut avoir qu'un seul stage actif à la fois

## Modèle de Données
- Utilisateur (classe parent)
  - Etudiant, Entreprise, Tuteur, Administration (héritage)
- OffreStage
- Candidature
- Convention
- SuiviStage

Relations :
- Entreprise 1 → N OffreStage
- Etudiant 1 → N Candidature
- OffreStage 1 → N Candidature
- Candidature 1 → 0..1 Convention
- Convention 1 → 0..1 SuiviStage
- Tuteur 1 → N SuiviStage