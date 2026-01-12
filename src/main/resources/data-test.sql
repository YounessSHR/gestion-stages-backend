-- ============================================
-- SCRIPT SQL DE TEST POUR GESTION STAGES
-- ============================================
-- Ce script insère des données de test pour tous les modules
-- Mot de passe par défaut pour tous les utilisateurs : password123
-- 
-- IMPORTANT: Pour générer un hash BCrypt valide, utilisez:
-- 1. L'endpoint GET /api/test/hash-password?password=password123
-- 2. Ou utilisez un générateur BCrypt en ligne
-- 
-- Hash BCrypt utilisé (vérifié): $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- Si ce hash ne fonctionne pas, générez-en un nouveau avec l'endpoint de test
-- ============================================

-- Désactiver les vérifications de clés étrangères temporairement
SET FOREIGN_KEY_CHECKS = 0;

-- Nettoyer les tables (optionnel, commenté pour éviter de perdre des données)
-- TRUNCATE TABLE suivi_stage;
-- TRUNCATE TABLE convention;
-- TRUNCATE TABLE candidature;
-- TRUNCATE TABLE offre_stage;
-- TRUNCATE TABLE administration;
-- TRUNCATE TABLE tuteur;
-- TRUNCATE TABLE entreprise;
-- TRUNCATE TABLE etudiant;
-- TRUNCATE TABLE utilisateur;

-- ============================================
-- 1. UTILISATEURS DE BASE
-- ============================================

-- Administrateurs
INSERT INTO utilisateur (id, email, mot_de_passe, nom, prenom, telephone, role, date_creation, actif) VALUES
(1, 'admin@gestion-stages.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Dupont', 'Jean', '01 23 45 67 89', 'ADMINISTRATION', NOW(), TRUE);

INSERT INTO administration (id) VALUES (1);

-- Étudiants
INSERT INTO utilisateur (id, email, mot_de_passe, nom, prenom, telephone, role, date_creation, actif) VALUES
(2, 'marie.martin@etudiant.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Martin', 'Marie', '06 12 34 56 78', 'ETUDIANT', DATE_SUB(NOW(), INTERVAL 30 DAY), TRUE),
(3, 'pierre.durand@etudiant.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Durand', 'Pierre', '06 23 45 67 89', 'ETUDIANT', DATE_SUB(NOW(), INTERVAL 25 DAY), TRUE),
(4, 'sophie.bernard@etudiant.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bernard', 'Sophie', '06 34 56 78 90', 'ETUDIANT', DATE_SUB(NOW(), INTERVAL 20 DAY), TRUE),
(5, 'lucas.moreau@etudiant.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Moreau', 'Lucas', '06 45 67 89 01', 'ETUDIANT', DATE_SUB(NOW(), INTERVAL 15 DAY), TRUE);

INSERT INTO etudiant (id, niveau, filiere, cv, date_naissance) VALUES
(2, 'L3', 'Informatique', NULL, '2001-05-15'),
(3, 'M1', 'Génie Logiciel', NULL, '2000-03-22'),
(4, 'L2', 'Réseaux et Télécommunications', NULL, '2002-08-10'),
(5, 'M2', 'Intelligence Artificielle', NULL, '1999-11-30');

-- Entreprises
INSERT INTO utilisateur (id, email, mot_de_passe, nom, prenom, telephone, role, date_creation, actif) VALUES
(6, 'contact@techcorp.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Dubois', 'Marc', '01 45 67 89 10', 'ENTREPRISE', DATE_SUB(NOW(), INTERVAL 60 DAY), TRUE),
(7, 'rh@innovasoft.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lefebvre', 'Claire', '01 56 78 90 12', 'ENTREPRISE', DATE_SUB(NOW(), INTERVAL 45 DAY), TRUE),
(8, 'info@webdev.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Garcia', 'Thomas', '01 67 89 01 23', 'ENTREPRISE', DATE_SUB(NOW(), INTERVAL 30 DAY), TRUE);

INSERT INTO entreprise (id, nom_entreprise, secteur_activite, adresse, site_web, description) VALUES
(6, 'TechCorp Solutions', 'Technologie', '123 Avenue de la République, 75011 Paris', 'https://www.techcorp.fr', 'Entreprise leader dans le développement de solutions logicielles innovantes.'),
(7, 'InnovaSoft', 'Informatique', '45 Rue de la Paix, 69001 Lyon', 'https://www.innovasoft.fr', 'Spécialisée dans le développement d''applications web et mobiles.'),
(8, 'WebDev Agency', 'Digital', '78 Boulevard Saint-Michel, 75005 Paris', 'https://www.webdev.fr', 'Agence web créative spécialisée dans le design et le développement frontend.');

-- Tuteurs
INSERT INTO utilisateur (id, email, mot_de_passe, nom, prenom, telephone, role, date_creation, actif) VALUES
(9, 'prof.dubois@univ.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Dubois', 'Professeur', '01 78 90 12 34', 'TUTEUR', DATE_SUB(NOW(), INTERVAL 90 DAY), TRUE),
(10, 'prof.martinez@univ.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Martinez', 'Professeur', '01 89 01 23 45', 'TUTEUR', DATE_SUB(NOW(), INTERVAL 80 DAY), TRUE);

INSERT INTO tuteur (id, departement, specialite) VALUES
(9, 'Informatique', 'Développement Logiciel'),
(10, 'Informatique', 'Réseaux et Systèmes');

-- ============================================
-- 2. OFFRES DE STAGE
-- ============================================

INSERT INTO offre_stage (id, titre, description, type_offre, duree, date_debut, date_fin, competences_requises, remuneration, entreprise_id, statut, date_publication, date_expiration) VALUES
-- Offres en attente de validation
(1, 'Développeur Full Stack Junior', 
 'Recherche d''un développeur full stack pour rejoindre notre équipe. Vous travaillerez sur des projets web modernes utilisant React et Node.js. Environnement dynamique et formateur.',
 'STAGE', 6, DATE_ADD(CURDATE(), INTERVAL 1 MONTH), DATE_ADD(CURDATE(), INTERVAL 7 MONTH), 'React, Node.js, JavaScript, HTML/CSS, Git', 600.00, 6, 'EN_ATTENTE', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY)),

(2, 'Stage Développement Mobile iOS',
 'Stage de 6 mois en développement d''applications iOS. Vous participerez au développement de nouvelles fonctionnalités pour notre application mobile.',
 'STAGE', 6, DATE_ADD(CURDATE(), INTERVAL 1 MONTH), DATE_ADD(CURDATE(), INTERVAL 7 MONTH), 'Swift, iOS SDK, Xcode, Git', 650.00, 7, 'EN_ATTENTE', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY)),

-- Offres validées (publiques)
(3, 'Alternance Développeur Web',
 'Alternance de 12 mois en développement web. Formation en entreprise avec suivi académique. Stack technique : React, TypeScript, Node.js, PostgreSQL.',
 'ALTERNANCE', 12, DATE_ADD(CURDATE(), INTERVAL 3 MONTH), DATE_ADD(CURDATE(), INTERVAL 15 MONTH), 'React, TypeScript, Node.js, PostgreSQL, Git', 1200.00, 6, 'VALIDEE', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 2 MONTH)),

(4, 'Stage Data Science',
 'Stage de 4 mois en data science. Analyse de données, machine learning, visualisation. Environnement Python, pandas, scikit-learn.',
 'STAGE', 4, DATE_ADD(CURDATE(), INTERVAL 1 MONTH), DATE_ADD(CURDATE(), INTERVAL 5 MONTH), 'Python, pandas, scikit-learn, SQL, Jupyter', 700.00, 7, 'VALIDEE', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_ADD(CURDATE(), INTERVAL 25 DAY)),

(5, 'Alternance DevOps',
 'Alternance de 12 mois en DevOps. Gestion de l''infrastructure, CI/CD, Docker, Kubernetes. Formation complète en entreprise.',
 'ALTERNANCE', 12, DATE_ADD(CURDATE(), INTERVAL 3 MONTH), DATE_ADD(CURDATE(), INTERVAL 15 MONTH), 'Docker, Kubernetes, CI/CD, Linux, Git', 1300.00, 8, 'VALIDEE', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 2 MONTH)),

(6, 'Stage Frontend React',
 'Stage de 3 mois en développement frontend avec React. Vous travaillerez sur l''interface utilisateur de notre plateforme web.',
 'STAGE', 3, DATE_ADD(CURDATE(), INTERVAL 1 MONTH), DATE_ADD(CURDATE(), INTERVAL 4 MONTH), 'React, JavaScript, HTML/CSS, Redux, Git', 550.00, 8, 'VALIDEE', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY)),

(7, 'Stage Backend Node.js',
 'Stage de 5 mois en développement backend. API REST, bases de données, microservices. Stack : Node.js, Express, MongoDB.',
 'STAGE', 5, DATE_ADD(CURDATE(), INTERVAL 2 MONTH), DATE_ADD(CURDATE(), INTERVAL 7 MONTH), 'Node.js, Express, MongoDB, REST API, Git', 650.00, 6, 'VALIDEE', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(CURDATE(), INTERVAL 1 MONTH));

-- ============================================
-- 3. CANDIDATURES
-- ============================================

INSERT INTO candidature (id, etudiant_id, offre_id, lettre_motivation, date_candidature, statut, commentaire, date_traitement) VALUES
-- Candidatures en attente
(1, 2, 3, 'Bonjour, je suis très intéressé par cette alternance en développement web. J''ai des compétences en React et je souhaite approfondir mes connaissances en TypeScript et Node.js. Je suis motivé et prêt à m''investir pleinement dans ce projet.', DATE_SUB(NOW(), INTERVAL 2 DAY), 'EN_ATTENTE', NULL, NULL),

(2, 3, 4, 'Je suis passionné par la data science et j''aimerais mettre en pratique mes connaissances théoriques acquises en master. Ce stage me permettrait de travailler sur des projets concrets et d''apprendre de votre équipe.', DATE_SUB(NOW(), INTERVAL 1 DAY), 'EN_ATTENTE', NULL, NULL),

(3, 4, 5, 'L''alternance DevOps m''intéresse beaucoup. J''ai déjà des bases en Linux et Git, et je souhaite me spécialiser dans le cloud et les conteneurs. Je suis disponible pour commencer en septembre.', NOW(), 'EN_ATTENTE', NULL, NULL),

-- Candidatures acceptées (avec conventions créées)
(4, 2, 6, 'Je suis très motivé pour ce stage en développement frontend React. J''ai déjà réalisé plusieurs projets personnels avec React et je souhaite maintenant travailler en équipe sur un projet professionnel.', DATE_SUB(NOW(), INTERVAL 5 DAY), 'ACCEPTEE', 'Candidature très prometteuse', DATE_SUB(NOW(), INTERVAL 4 DAY)),

(5, 3, 7, 'Ce stage backend correspond parfaitement à mes attentes. J''ai des compétences en Node.js et je souhaite approfondir mes connaissances en architecture de microservices.', DATE_SUB(NOW(), INTERVAL 4 DAY), 'ACCEPTEE', 'Profil adapté au poste', DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- Candidature refusée
(6, 4, 3, 'Je suis intéressé par cette alternance mais je n''ai pas encore assez d''expérience en TypeScript.', DATE_SUB(NOW(), INTERVAL 3 DAY), 'REFUSEE', 'Profil ne correspond pas aux exigences', DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Candidature acceptée supplémentaire pour convention signée (stage actif)
(7, 5, 3, 'Alternance très intéressante pour mon parcours en M2 IA. Je suis très motivé et prêt à m''investir pleinement dans ce projet.', DATE_SUB(NOW(), INTERVAL 12 DAY), 'ACCEPTEE', 'Excellent profil', DATE_SUB(NOW(), INTERVAL 11 DAY));

-- ============================================
-- 4. CONVENTIONS
-- ============================================

INSERT INTO convention (id, candidature_id, date_generation, date_debut_stage, date_fin_stage, statut, signature_etudiant, signature_entreprise, signature_administration, fichier_pdf) VALUES
-- Convention en attente de signatures (candidature 4)
(1, 4, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), DATE_ADD(CURDATE(), INTERVAL 4 MONTH), 'EN_ATTENTE_SIGNATURES', FALSE, FALSE, FALSE, NULL),

-- Convention signée par l'étudiant et l'entreprise, en attente de l'admin (candidature 5)
(2, 5, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 2 MONTH), DATE_ADD(CURDATE(), INTERVAL 7 MONTH), 'EN_ATTENTE_SIGNATURES', TRUE, TRUE, FALSE, NULL),

-- Convention complètement signée (pour suivi de stage) - référence candidature 7
-- Stage actif : date de début dans le passé pour montrer un stage en cours
(3, 7, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 2 MONTH), DATE_ADD(CURDATE(), INTERVAL 10 MONTH), 'SIGNEE', TRUE, TRUE, TRUE, 'convention_3_1234567890.pdf');

-- ============================================
-- 5. SUIVIS DE STAGE
-- ============================================

INSERT INTO suivi_stage (id, convention_id, tuteur_id, date_affectation, etat_avancement, commentaires, derniere_visite) VALUES
(1, 3, 9, DATE_SUB(NOW(), INTERVAL 8 DAY), 'EN_COURS', 'L''étudiant progresse bien dans son stage. Bonne intégration dans l''équipe. Première visite effectuée avec succès.', DATE_SUB(NOW(), INTERVAL 5 DAY));

-- ============================================
-- RÉACTIVER LES VÉRIFICATIONS DE CLÉS ÉTRANGÈRES
-- ============================================

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- VÉRIFICATION DES DONNÉES INSÉRÉES
-- ============================================

-- Afficher le nombre d'enregistrements par table
SELECT 'Utilisateurs' AS TableName, COUNT(*) AS Count FROM utilisateur
UNION ALL
SELECT 'Étudiants', COUNT(*) FROM etudiant
UNION ALL
SELECT 'Entreprises', COUNT(*) FROM entreprise
UNION ALL
SELECT 'Tuteurs', COUNT(*) FROM tuteur
UNION ALL
SELECT 'Administrations', COUNT(*) FROM administration
UNION ALL
SELECT 'Offres', COUNT(*) FROM offre_stage
UNION ALL
SELECT 'Candidatures', COUNT(*) FROM candidature
UNION ALL
SELECT 'Conventions', COUNT(*) FROM convention
UNION ALL
SELECT 'Suivis', COUNT(*) FROM suivi_stage;

-- ============================================
-- INFORMATIONS DE CONNEXION POUR TESTS
-- ============================================
-- Admin: admin@gestion-stages.fr / password123
-- Étudiant 1: marie.martin@etudiant.fr / password123
-- Étudiant 2: pierre.durand@etudiant.fr / password123
-- Étudiant 3: sophie.bernard@etudiant.fr / password123
-- Étudiant 4: lucas.moreau@etudiant.fr / password123
-- Entreprise 1: contact@techcorp.fr / password123
-- Entreprise 2: rh@innovasoft.fr / password123
-- Entreprise 3: info@webdev.fr / password123
-- Tuteur 1: prof.dubois@univ.fr / password123
-- Tuteur 2: prof.martinez@univ.fr / password123
-- ============================================
