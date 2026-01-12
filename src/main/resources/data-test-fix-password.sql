-- ============================================
-- SCRIPT DE CORRECTION DES MOTS DE PASSE
-- ============================================
-- Utilisez ce script si vous avez des problèmes de connexion
-- 
-- ÉTAPE 1: Générer un nouveau hash BCrypt
-- Appelez: GET http://localhost:8080/api/test/hash-password?password=password123
-- Copiez le hash retourné
-- 
-- ÉTAPE 2: Remplacez VOTRE_HASH_ICI ci-dessous par le hash généré
-- 
-- ÉTAPE 3: Exécutez ce script
-- ============================================

-- REMPLACEZ CE HASH PAR CELUI GÉNÉRÉ PAR L'ENDPOINT DE TEST
SET @new_password_hash = '$2a$10$VOTRE_HASH_ICI';

-- Mettre à jour tous les mots de passe
UPDATE utilisateur 
SET mot_de_passe = @new_password_hash 
WHERE email IN (
    'admin@gestion-stages.fr',
    'marie.martin@etudiant.fr',
    'pierre.durand@etudiant.fr',
    'sophie.bernard@etudiant.fr',
    'lucas.moreau@etudiant.fr',
    'contact@techcorp.fr',
    'rh@innovasoft.fr',
    'info@webdev.fr',
    'prof.dubois@univ.fr',
    'prof.martinez@univ.fr'
);

-- Vérification
SELECT email, LEFT(mot_de_passe, 20) as hash_start, role, actif 
FROM utilisateur 
ORDER BY email;
-- hash $2a$10$RIjnLE3NGC7BHCza7.7.VuWDOe3bHm0sBoEWlmvsADFeW2ziJosSq
