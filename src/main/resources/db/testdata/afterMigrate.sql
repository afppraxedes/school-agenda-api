-- Users
INSERT INTO users (email, username, password, name, push_subscription) VALUES
('director@school.com', 'director', '$2a$10$RR44jktAtMMuHOzQife3GeEZU48aXYcs6E1j5xHSdPvudKHFPsE4G','Director User', NULL),
('teacher@school.com', 'teacher', '$2a$10$RR44jktAtMMuHOzQife3GeEZU48aXYcs6E1j5xHSdPvudKHFPsE4G', 'Teacher User', NULL),
('responsible@school.com', 'responsible', '$2a$10$RR44jktAtMMuHOzQife3GeEZU48aXYcs6E1j5xHSdPvudKHFPsE4G', 'Responsible User', NULL),
('admin@school.com', 'admin', '$2a$10$eAccYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORr7x.56trjDZ.mc7.', 'Administrator', NULL),
('student_rodrigo@school.com', 'student_rodigo', '$2a$10$eAccYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORr7x.56trjDZ.mc7.', 'Student Rodrigo', NULL),
('marcelo.diretor@school.com', 'marcelo', '$2a$10$5GxIkMhjdW8o21iFOaVIQucO/zXxucFQQpJOXaRxGuFfcNWV8BPUu', 'Marcelo Diretor', NULL),
('alexander.admin@school.com', 'alex', '$2a$10$RR44jktAtMMuHOzQife3GeEZU48aXYcs6E1j5xHSdPvudKHFPsE4G', 'Alex Administrador', NULL),
('teacher.carlos@school.com', 'teacher_carlos', '$2a$10$RR44jktAtMMuHOzQife3GeEZU48aXYcs6E1j5xHSdPvudKHFPsE4G', 'Teacher Carlos', NULL),
('responsible.julia@school.com', 'responsible_julia', '$2a$10$RR44jktAtMMuHOzQife3GeEZU48aXYcs6E1j5xHSdPvudKHFPsE4G', 'Responsible Julia', NULL),
('student.mariana@school.com', 'student_mariana', '$2a$10$eAccYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORr7x.56trjDZ.mc7.', 'Student Mariana', NULL);

-- User Roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'DIRECTOR'),
(2, 'TEACHER'),
(3, 'RESPONSIBLE'),
(4, 'ADMINISTRATOR'),
(5, 'STUDENT'),
(6, 'DIRECTOR'),
(7, 'ADMINISTRATOR'),
(7,	'DIRECTOR');

-- School Class
INSERT INTO school_classes (name, description, year, is_active, coordinator_id, created_by, last_modified_by, created_at, updated_at) VALUES
('7º Ano B - 2025', 'Turna do 7º Ano - Segundo Semestre', 2025, true, 1, NULL, NULL, NOW(), NOW()),
('5º Ano A - 2025', 'Turna do 5º Ano - Primeiro Semestre', 2025, true, 1, NULL, NULL, NOW(), NOW()),
('9º Ano A - 2025', 'Turna do 9º Ano - Segundo Semestre', 2025, true, 1, NULL, NULL, NOW(), NOW());

-- Students
INSERT INTO student (full_name, birth_date, class_name, profile_photo, registration_date, user_id, school_class_id, created_by, last_modified_by, created_at, updated_at) VALUES
('Alice Student', '2015-05-10', 'Class 1A', NULL, NOW(), 1, 1, NULL, NULL, NOW(), NOW()),
('Bob Student', '2014-08-20', 'Class 2B', NULL, NOW(), 2, 2, NULL, NULL, NOW(), NOW()),
('Student User', '2010-01-01', 'Class 3C', NULL, NOW(), 4, 3, NULL, NULL, NOW(), NOW());

-- Responsible Student
INSERT INTO responsable_student (id, responsable_id, student_id, created_at, updated_at) VALUES
(1, 3, 1, NOW(), NOW()),
(2, 3, 2, NOW(), NOW()),
(3, 9, 3, NOW(),NOW());

-- Subjects
INSERT INTO subjects (name, school_year, teacher_user_id, is_active, created_at, updated_at) VALUES
('Matemática', '2025', 2, TRUE,  '2025-12-06 16:08:43.302868', '2025-12-06 16:08:43.302868'),
('Geografia', '2025', 2, TRUE, '2025-12-08 22:08:37.385838', '2025-12-08 22:08:37.385838'),
('História', '2025', 2, TRUE, '2025-12-08 22:08:54.704289', '2025-12-08 22:08:54.704289'),
('Física', '2025', 2, TRUE, '2025-12-08, 22:09:06.694278', '2025-12-08 22:09:06.694278'),
('Química', '2025', 2, FALSE, '2025-12-09, 16:54:47.862365', '2025-12-09 13:57:58.45501');

-- Assessment
INSERT INTO assessments (title, description, subject_id, created_by_user_id, due_date, max_score, weight, is_published, is_recovery, created_by, last_modified_by, created_at, updated_at) VALUES
('Prova de Matemática', 'Prova do quarto bimestre', 1, 2, '2025-12-10', 10.00, 2.0, TRUE, FALSE, NULL, NULL, NOW(), NOW()),
('Prova de Matemática', 'Prova do terceiro bimestre', 1,2,'2025-09-05', 10.00, 2.0, TRUE, FALSE, NULL, NULL, NOW(), NOW()),
('Prova de Geografia', 'Prova do quarto bimestre', 2, 2, '2025-12-09', 10.00, 1.0, TRUE, FALSE, NULL, NULL, NOW(), NOW());

-- Grade
INSERT INTO grades (assessment_id, student_user_id, score, max_score, feedback, graded_by_user_id, graded_at, is_absent, is_excused, created_by, last_modified_by, created_at, updated_at) VALUES
(2, 5, 6.50, 10.00, 'Bom trabalho! Mas precisa melhor um pouco mais!', 2 ,'2025-12-09 01:18:02.657663', FALSE, FALSE, NULL, NULL, NOW(), NOW()),
(3, 5, 9.50, 10.00, 'Ótimo trabalho!', 2, '2025-12-09 01:18:02.659669', FALSE,FALSE, NULL, NULL, NOW(), NOW()),
(1, 5, 8.50, 10.00, 'Bom trabalho!', 2, '2025-12-09 03:01:36.746502', FALSE, FALSE, NULL, NULL, NOW(), NOW());

-- Announcements
INSERT INTO announcements (title, description, image_path, type, order_position, created_by, last_modified_by, created_at, updated_at, is_active) VALUES
('Welcome Back', 'Welcome to the new school year!', 'path/to/image1.jpg', 'BANNER', 1, NULL, NULL, NOW(), NOW(), true),
('School Trip', 'Upcoming trip to the museum.', 'path/to/image2.jpg', 'CAROUSEL', 2, NULL, NULL, NOW(), NOW(), true);

-- Conversations
INSERT INTO conversations (sender_id, recipient_id, student_id, subject, content, attachment_path, sent_at, read_status) VALUES
(3, 2, 1, 'Homework Question', 'Can you clarify the math homework?', NULL, NOW(), 'UNREAD');

-- Events
INSERT INTO events (title, description, start_date, end_date, color, created_at, updated_at) VALUES
('Parent Meeting', 'Meeting for all parents.', '2023-11-01 18:00:00', '2023-11-01 20:00:00', '#0A2558', NOW(), NOW());

-- Notifications
INSERT INTO notifications (title, message, user_id, read, created_at, type) VALUES
('New Message', 'You have a new message from Responsible User.', 2, false, NOW(), 'MESSAGE');

-- Teacher Classes
INSERT INTO teacher_classes (teacher_id, subject_id, school_class_id, created_by, last_modified_by, created_at, updated_at) VALUES
(2, 1, 1, NULL, NULL, NOW(), NOW()),
(2, 4, 2, NULL, NULL, NOW(), NOW());

-- Attendances
-- Limpa para evitar erros de duplicidade em re-execuções, se necessário
-- DELETE FROM attendances WHERE student_id = 1;

INSERT INTO attendances (student_id, subject_id, date, present, note, created_at) VALUES
(1, 1, CURRENT_DATE - INTERVAL '2 days', true, 'Presença normal', NOW()),
(1, 1, CURRENT_DATE - INTERVAL '1 day', false, 'Falta sem justificativa', NOW()),
(1, 1, CURRENT_DATE, true, 'Presença normal', NOW());
