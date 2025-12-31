-- Users
-- 1. USUÁRIOS (40 Usuários)
-- IDs 1-2: Admin | ID 3: Diretor/Admin | IDs 4-12: Professores | IDs 13-22: Responsáveis | IDs 23-40: Alunos
INSERT INTO users (id, email, username, password, name) VALUES
(1, 'admin1@school.com', 'admin1', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Admin Master'),
(2, 'admin2@school.com', 'admin2', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Admin Suporte'),
(3, 'marcelo.diretor@school.com', 'marcelo', '$2a$10$5GxIkMhjdW8o21iFOaVIQucO/zXxucFQQpJOXaRxGuFfcNWV8BPUu', 'Marcelo Diretor');

-- Professores (IDs 4-12)
INSERT INTO users (id, email, username, password, name) VALUES
(4, 'teacher1@school.com', 'teacher1', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Newton'),
(5, 'teacher2@school.com', 'teacher2', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Camões'),
(6, 'teacher3@school.com', 'teacher3', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Curie'),
(7, 'teacher4@school.com', 'teacher4', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Darwin'),
(8, 'teacher5@school.com', 'teacher5', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Einstein'),
(9, 'teacher6@school.com', 'teacher6', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Da Vinci'),
(10, 'teacher7@school.com', 'teacher7', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Tesla'),
(11, 'teacher8@school.com', 'teacher8', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Pitágoras'),
(12, 'teacher9@school.com', 'teacher9', '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'Prof. Sócrates');

-- Responsáveis (IDs 13-22) e Alunos (IDs 23-40) gerados simplificadamente
INSERT INTO users (id, email, username, password, name)
SELECT i, 'user'||i||'@school.com', 'user'||i, '$2a$10$bYwKQhPkLtnSfBt7EVDdqehvCQHb3SBVtctXhuiUsxtN2yFUg4b2y', 'User '||i
FROM generate_series(13, 40) i;

-- 2. USERS ROLES
INSERT INTO user_roles (user_id, role) VALUES
(1, 'ADMINISTRATOR'),
(1, 'DIRECTOR'),
(2, 'ADMINISTRATOR'),
(3, 'ADMINISTRATOR'),
(3, 'DIRECTOR');
INSERT INTO user_roles (user_id, role) SELECT id, 'TEACHER' FROM users WHERE id BETWEEN 4 AND 12;
INSERT INTO user_roles (user_id, role) SELECT id, 'RESPONSIBLE' FROM users WHERE id BETWEEN 13 AND 22;
INSERT INTO user_roles (user_id, role) SELECT id, 'STUDENT' FROM users WHERE id BETWEEN 23 AND 40;

-- 3. SCHOOL CLASSES (5 Turmas)
INSERT INTO school_classes (id, name, description, year, is_active, coordinator_id, created_at, updated_at) VALUES
(1, '7º Ano B - 2025', 'Turma B', 2025, true, 3, NOW(), NOW()),
(2, '5º Ano A - 2025', 'Turma A', 2025, true, 3, NOW(), NOW()),
(3, '9º Ano A - 2025', 'Turma A', 2025, true, 3, NOW(), NOW()),
(4, '8º Ano C - 2025', 'Turma C', 2025, true, 3, NOW(), NOW()),
(5, '6º Ano D - 2025', 'Turma D', 2025, true, 3, NOW(), NOW());

-- 4. STUDENTS (18 Alunos vinculados aos Users 23-40)
INSERT INTO student (id, full_name, birth_date, class_name, registration_date, user_id, school_class_id, created_by, last_modified_by, created_at, updated_at)
SELECT i-22, 'Student Name '||i, '2010-01-01', 'Class '||i, NOW(), i, (i % 5) + 1, NULL, NULL, NOW(), NOW()
FROM generate_series(23, 40) i;

-- 5. RESPONSABLE STUDENT (Vínculos solicitados)
INSERT INTO responsable_student (responsable_id, student_id, created_at, updated_at) VALUES
(13,1, NOW(), NOW()), (14,2, NOW(), NOW()), (15,3, NOW(), NOW()), -- 1 aluno cada
(16,4, NOW(), NOW()), (16,5, NOW(), NOW()), (17,6, NOW(), NOW()), (17,7, NOW(), NOW()), (18,8, NOW(), NOW()), (18,9, NOW(), NOW()), (19,10, NOW(), NOW()), (19,11, NOW(), NOW()), (20,12, NOW(), NOW()), (20,13, NOW(), NOW()), (21,14, NOW(), NOW()), (21,15, NOW(), NOW()), -- 2 alunos
(22,16, NOW(), NOW()), (22,17, NOW(), NOW()), (22,18, NOW(), NOW()); -- 3 alunos

-- 6. SUBJECTS (12 Disciplinas vinculadas aos Professores)
INSERT INTO subjects (id, name, school_year, teacher_user_id, is_active, created_at, updated_at) VALUES
(1, 'Matemática', '2025', 4, true, NOW(), NOW()), (2, 'Português', '2025', 5, true, NOW(), NOW()),
(3, 'História', '2025', 6, true, NOW(), NOW()), (4, 'Geografia', '2025', 7, true, NOW(), NOW()),
(5, 'Ciências', '2025', 8, true, NOW(), NOW()), (6, 'Inglês', '2025', 9, true, NOW(), NOW()),
(7, 'Física', '2025', 10, true, NOW(), NOW()), (8, 'Química', '2025', 11, true, NOW(), NOW()),
(9, 'Artes', '2025', 12, true, NOW(), NOW()), (10, 'Ed. Física', '2025', 8, true, NOW(), NOW()), -- Einstein +1
(11, 'Biologia', '2025', 10, true, NOW(), NOW()), -- Tesla +1
(12, 'Filosofia', '2025', 12, true, NOW(), NOW()); -- Sócrates +1

-- 7. TEACHER CLASSES
INSERT INTO teacher_classes (teacher_id, subject_id, school_class_id, created_by, last_modified_by, created_at, updated_at) VALUES
(4, 1, 1, NULL, NULL, NOW(), NOW()),
(5, 2, 1, NULL, NULL, NOW(), NOW()),
(8, 5, 2, NULL, NULL, NOW(), NOW()),
(10, 7, 3, NULL, NULL, NOW(), NOW());

-- Exemplo de inserção para os 20 registros com a ordem de auditoria solicitada
-- Ordem final: created_by, last_modified_by, created_at, updated_at

INSERT INTO timetables (
    teacher_class_id, day_of_week, start_time, end_time, room_name,
    created_by, last_modified_by, created_at, updated_at) VALUES
(1, 'MONDAY', '08:00:00', '08:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(1, 'TUESDAY', '08:00:00', '08:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(2, 'MONDAY', '08:50:00', '09:40:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(2, 'WEDNESDAY', '10:00:00', '10:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(3, 'MONDAY', '08:00:00', '08:50:00', 'Sala 202', 'system', 'system', NOW(), NOW()),
(3, 'FRIDAY', '11:00:00', '11:50:00', 'Sala 202', 'system', 'system', NOW(), NOW()),
(4, 'WEDNESDAY', '08:00:00', '08:50:00', 'Lab Física', 'system', 'system', NOW(), NOW()),
(4, 'THURSDAY', '09:00:00', '09:50:00', 'Lab Física', 'system', 'system', NOW(), NOW()),
(1, 'WEDNESDAY', '08:00:00', '08:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(2, 'THURSDAY', '08:00:00', '08:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(3, 'TUESDAY', '09:00:00', '09:50:00', 'Sala 202', 'system', 'system', NOW(), NOW()),
(4, 'MONDAY', '10:00:00', '10:50:00', 'Lab Física', 'system', 'system', NOW(), NOW()),
(1, 'FRIDAY', '08:00:00', '08:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(2, 'FRIDAY', '09:00:00', '09:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(3, 'THURSDAY', '10:00:00', '10:50:00', 'Sala 202', 'system', 'system', NOW(), NOW()),
(4, 'TUESDAY', '11:00:00', '11:50:00', 'Lab Física', 'system', 'system', NOW(), NOW()),
(1, 'THURSDAY', '11:00:00', '11:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(2, 'TUESDAY', '10:00:00', '10:50:00', 'Sala 101', 'system', 'system', NOW(), NOW()),
(3, 'WEDNESDAY', '09:00:00', '09:50:00', 'Sala 202', 'system', 'system', NOW(), NOW()),
(4, 'FRIDAY', '10:00:00', '10:50:00', 'Lab Física', 'system', 'system', NOW(), NOW());

-- 8. ASSESSMENTS (4 Bimestrais por disciplina)
INSERT INTO assessments (id, title, subject_id, created_by_user_id, max_score, weight, is_published, is_recovery, created_by, last_modified_by, created_at, updated_at)
SELECT (s.id-1)*4 + i, 'Prova '||i||'º Bimestre', s.id, s.teacher_user_id, 10.0, 1.0, true, false, NULL, NULL, NOW(), NOW()
FROM subjects s, generate_series(1,4) i;

-- -- Grade
-- INSERT INTO grades (assessment_id, student_user_id, score, max_score, feedback, graded_by_user_id, graded_at, is_absent, is_excused, created_by, last_modified_by, created_at, updated_at) VALUES
-- (2, 5, 6.50, 10.00, 'Bom trabalho! Mas precisa melhor um pouco mais!', 2 ,'2025-12-09 01:18:02.657663', FALSE, FALSE, NULL, NULL, NOW(), NOW()),
-- (3, 5, 9.50, 10.00, 'Ótimo trabalho!', 2, '2025-12-09 01:18:02.659669', FALSE,FALSE, NULL, NULL, NOW(), NOW()),
-- (1, 5, 8.50, 10.00, 'Bom trabalho!', 2, '2025-12-09 03:01:36.746502', FALSE, FALSE, NULL, NULL, NOW(), NOW());

-- 10. GRADES (Lógica 60% / 35% / 5%)
INSERT INTO grades (assessment_id, student_user_id, score, max_score, feedback, graded_by_user_id, graded_at, is_absent, is_excused, created_by, last_modified_by, created_at, updated_at)
SELECT
    a.id,
    u.id,
    CASE
        WHEN u.id <= 33 THEN (6.5 + random() * 3.5) -- 60% Aprovados
        WHEN u.id <= 39 THEN (4.0 + random() * 2.0) -- 35% Recuperação
        ELSE (random() * 3.9)                      -- 5% Reprovados
        END,
    10.0,
    'Feedback automático gerado - verificar como personalizar por faixa de nota',
    4, NOW(), false, false, NULL, NULL, NOW(), NOW()
FROM assessments a, users u WHERE u.id BETWEEN 23 AND 40;

-- 10. ATTENDANCES (Reprovação por falta para Aluno 40)
INSERT INTO attendances (student_id, subject_id, timetable_id, date, present, note, created_at)
SELECT
    st.id, sub.id, ttb.id CURRENT_DATE - i,
    CASE WHEN st.user_id = 40 AND i < 5 THEN false ELSE true END, -- Aluno 40 com muitas faltas
    'Verificar como colocar no script o "Note"', NOW()
FROM student st, subjects sub, timetables ttb, generate_series(1, 10) i;
-- INSERT INTO attendances (student_id, subject_id, date, present, note, created_at) VALUES
-- (1, 1, CURRENT_DATE - INTERVAL '2 days', true, 'Presença normal', NOW()),
-- (1, 1, CURRENT_DATE - INTERVAL '1 day', false, 'Falta sem justificativa', NOW()),
-- (1, 1, CURRENT_DATE, true, 'Presença normal', NOW());

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

-- Attendances
-- Limpa para evitar erros de duplicidade em re-execuções, se necessário
-- DELETE FROM attendances WHERE student_id = 1;

-- INSERT INTO attendances (student_id, subject_id, date, present, note, created_at) VALUES
-- (1, 1, CURRENT_DATE - INTERVAL '2 days', true, 'Presença normal', NOW()),
-- (1, 1, CURRENT_DATE - INTERVAL '1 day', false, 'Falta sem justificativa', NOW()),
-- (1, 1, CURRENT_DATE, true, 'Presença normal', NOW());
