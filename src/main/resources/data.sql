
INSERT INTO roles (role_name) VALUES ('ROLE_STUDENT');
INSERT INTO roles (role_name) VALUES ('ROLE_TEACHER');
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN');

INSERT INTO organizations (name, registration_key, contact_email, max_students, is_active, license_expires_at, created_at, updated_at)
VALUES ('École Test', 'PRE-ABCD1234', 'contact@ecoletest.fr', 100, true, '2027-12-31 23:59:59', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Badges pour la gamification
INSERT INTO badges (name, display_name, description, icon_url, criteria, created_at) VALUES
('FIRST_COURSE', 'Premier pas', 'Avoir complété son premier cours', '/icons/first-course.svg', 'Compléter 1 cours', CURRENT_TIMESTAMP),
('MODULE_COMPLETED', 'Module maîtrisé', 'Avoir complété un module entier', '/icons/module-completed.svg', 'Compléter 1 module', CURRENT_TIMESTAMP),
('STREAK_7', 'Une semaine d''engagement', 'Se connecter 7 jours consécutifs', '/icons/streak-7.svg', '7 jours de streak', CURRENT_TIMESTAMP),
('STREAK_30', 'Mois d''engagement', 'Se connecter 30 jours consécutifs', '/icons/streak-30.svg', '30 jours de streak', CURRENT_TIMESTAMP),
('XP_100', 'Apprenti', 'Atteindre 100 XP', '/icons/xp-100.svg', '100 XP cumulés', CURRENT_TIMESTAMP),
('XP_500', 'Expert en herbe', 'Atteindre 500 XP', '/icons/xp-500.svg', '500 XP cumulés', CURRENT_TIMESTAMP),
('XP_1000', 'Maître', 'Atteindre 1000 XP', '/icons/xp-1000.svg', '1000 XP cumulés', CURRENT_TIMESTAMP),
('QUIZ_MASTER', 'Maître des quiz', 'Compléter 10 quiz', '/icons/quiz-master.svg', '10 quiz complétés', CURRENT_TIMESTAMP),
('PERFECT_SCORE', 'Score parfait', 'Obtenir 100% à un quiz', '/icons/perfect-score.svg', 'Score parfait à un quiz', CURRENT_TIMESTAMP);
