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

-- 4. SCHOOL CLASSES (5 Eventos Escolares)
INSERT INTO school_events (title, description, start_date, end_date, all_day, type, school_class_id, location, created_by, last_modified_by, created_at, updated_at) VALUES
('Feriado Nacional', 'Não haverá aula devido ao feriado.', '2026-05-01T00:00:00Z', '2026-05-01T23:59:59Z', true, 'HOLIDAY', NULL, 'Escola', 'system', 'system', NOW(), NOW()),
('Prova de Matemática', 'Conteúdo: Álgebra e Geometria.', '2026-05-10T08:00:00Z', '2026-05-10T10:00:00Z', false, 'EXAM', 1, 'Sala 04', 'system', 'system', NOW(), NOW()),
('Reunião de Pais', 'Pauta: Entrega de boletins do 1º bimestre.', '2026-05-15T19:00:00Z', '2026-05-15T21:00:00Z', false, 'MEETING', NULL, 'Auditório Principal', 'system', 'system', NOW(), NOW()),
('Feira de Ciências', 'Apresentação dos projetos dos alunos.', '2026-05-20T09:00:00Z', '2026-05-20T17:00:00Z', true, 'CULTURAL', NULL, 'Pátio Central', 'system', 'system', NOW(), NOW()),
('Torneio de Futebol', 'Final do campeonato interclasses.', '2026-05-25T14:00:00Z', '2026-05-25T16:00:00Z', false, 'SPORTS', 1, 'Quadra Poliesportiva', 'system', 'system', NOW(), NOW()),
-- APENAS PARA TESTAR (Evento no fim de semana)
('Prova de Matemática', 'Conteúdo: Álgebra Linear', NOW() + INTERVAL '2 days', NOW() + INTERVAL '2 days', false, 'EXAM', 1, 'Sala 04', 'system', 'system', NOW(), NOW());

-- 5. STUDENTS (18 Alunos vinculados aos Users 23-40)
-- INSERT INTO student (id, full_name, birth_date, class_name, registration_date, global_average, user_id, school_class_id, created_by, last_modified_by, created_at, updated_at)
-- SELECT
--     i,
--     'Student Name '||i,
--     '2010-01-01',
--     'Class '||(i % 5 + 1),
--     NOW(),
--     CASE WHEN i = 23 THEN 8.17 ELSE 7.42 END,
--     i,
--     (i % 5) + 1,
--     NULL,
--     NULL,
--     NOW(),
--     NOW()
-- FROM generate_series(23, 40) i;
INSERT INTO student (id, full_name, birth_date, class_name, registration_date, global_average, user_id, school_class_id, created_by, last_modified_by, created_at, updated_at)
SELECT i-22, 'Student Name '||i, '2010-01-01', 'Class '||i, NOW(),
       7.42,
       -- CASE WHEN i = 23 THEN 8.17 ELSE 7.42 END,
       i, (i % 5) + 1, NULL, NULL, NOW(), NOW()
FROM generate_series(23, 40) i;

-- 6. RESPONSABLE STUDENT (Vínculos solicitados)
INSERT INTO responsable_student (responsable_id, student_id, created_at, updated_at) VALUES
(13,1, NOW(), NOW()), (14,2, NOW(), NOW()), (15,3, NOW(), NOW()), -- 1 aluno cada
(16,4, NOW(), NOW()), (16,5, NOW(), NOW()), (17,6, NOW(), NOW()), (17,7, NOW(), NOW()), (18,8, NOW(), NOW()), (18,9, NOW(), NOW()), (19,10, NOW(), NOW()), (19,11, NOW(), NOW()), (20,12, NOW(), NOW()), (20,13, NOW(), NOW()), (21,14, NOW(), NOW()), (21,15, NOW(), NOW()), -- 2 alunos
(22,16, NOW(), NOW()), (22,17, NOW(), NOW()), (22,18, NOW(), NOW()); -- 3 alunos

-- 7. SUBJECTS (12 Disciplinas vinculadas aos Professores)
INSERT INTO subjects (id, name, school_year, teacher_user_id, is_active, created_at, updated_at) VALUES
(1, 'Matemática', '2025', 4, true, NOW(), NOW()),
(2, 'Português', '2025', 5, true, NOW(), NOW()),
(3, 'História', '2025', 6, true, NOW(), NOW()),
(4, 'Geografia', '2025', 7, true, NOW(), NOW()),
(5, 'Ciências', '2025', 4, true, NOW(), NOW()),
(6, 'Inglês', '2025', 9, true, NOW(), NOW()),
(7, 'Física', '2025', 10, true, NOW(), NOW()),
(8, 'Química', '2025', 11, true, NOW(), NOW()),
(9, 'Artes', '2025', 12, true, NOW(), NOW()),
(10, 'Ed. Física', '2025', 8, true, NOW(), NOW()), -- Einstein +1
(11, 'Biologia', '2025', 10, true, NOW(), NOW()), -- Tesla +1
(12, 'Filosofia', '2025', 4, true, NOW(), NOW()); -- Sócrates +1

-- 8. EVALUATIONS (6 Avaliações para o Estudante ID 1 ao longo dos últimos meses)
-- Inserir Avaliações para o Estudante ID 1
-- Agosto/2025
-- INSERT INTO evaluations (grade, title, student_id, subject_id, created_at)
-- VALUES (7.0, 'Avaliação Inicial', 1, 1, '2025-08-15 10:00:00');
--
-- -- Setembro/2025
-- INSERT INTO evaluations (grade, title, student_id, subject_id, created_at)
-- VALUES (8.5, 'Trabalho de Pesquisa', 1, 2, '2025-09-20 14:30:00');
--
-- -- Outubro/2025
-- INSERT INTO evaluations (grade, title, student_id, subject_id, created_at)
-- VALUES (6.0, 'Prova Intermédia', 1, 3, '2025-10-10 08:00:00');
--
-- -- Novembro/2025
-- INSERT INTO evaluations (grade, title, student_id, subject_id, created_at)
-- VALUES (9.5, 'Projeto Prático', 1, 4, '2025-11-25 16:00:00');
--
-- -- Dezembro/2025
-- INSERT INTO evaluations (grade, title, student_id, subject_id, created_at)
-- VALUES (8.0, 'Exame Final Semestre', 1, 1, '2025-12-15 09:00:00');
--
-- -- Janeiro/2026 (Mês Atual)
-- INSERT INTO evaluations (grade, title, student_id, subject_id, created_at)
-- VALUES (10.0, 'Atividade de Lógica e IA', 1, 2, '2026-01-24 11:00:00');
-- 8. EVALUATIONS (6 Avaliações para o Estudante ID 1 ao longo dos últimos meses)
-- Limpando possíveis dados residuais para o ID 1 para evitar duplicidade no teste
DELETE FROM evaluations WHERE student_id = 1;

-- 8. EVALUATIONS (6 Avaliações para o Estudante ID 1 (usuário 23) ao longo dos últimos meses)
-- NOTA: neste momento, estamos inserindo apenas para o Estudante ID 1 para garantir que os dados estejam
-- alinhados com os testes de dashboard. Para uma base de teste mais completa, seria ideal inserir avaliações
-- para outros alunos também.
INSERT INTO evaluations (grade, title, student_id, subject_id, created_at) VALUES
(7.0, 'Avaliação Inicial', 1, 1, '2025-08-15 10:00:00'),
(8.5, 'Trabalho de Pesquisa', 1, 2, '2025-09-20 14:30:00'),
(6.0, 'Prova Intermédia', 1, 3, '2025-10-10 08:00:00'),
(9.5, 'Projeto Prático', 1, 4, '2025-11-25 16:00:00'),
(8.0, 'Exame Final Semestre', 1, 1, '2025-12-15 09:00:00'),
(10.0, 'Atividade de Lógica e IA', 1, 2, '2026-01-24 11:00:00');

-- 9. TEACHER CLASSES
INSERT INTO teacher_classes (teacher_id, subject_id, school_class_id, created_by, last_modified_by, created_at, updated_at) VALUES
(4, 1, 1, NULL, NULL, NOW(), NOW()),
(5, 2, 1, NULL, NULL, NOW(), NOW()),
(8, 5, 2, NULL, NULL, NOW(), NOW()),
(10, 7, 3, NULL, NULL, NOW(), NOW());

-- Exemplo de inserção para os 20 registros com a ordem de auditoria solicitada
-- Ordem final: created_by, last_modified_by, created_at, updated_at
-- 10. TIMETABLES (20 Horários vinculados às Teacher Classes)
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
(4, 'FRIDAY', '10:00:00', '10:50:00', 'Lab Física', 'system', 'system', NOW(), NOW()),
-- APENAS PARA TESTAR (O DIA ESTÁ COMO SÁBADO)
(4, 'SATURDAY', '10:00:00', '10:50:00', 'Lab Física', 'system', 'system', NOW(), NOW());

-- 11. ASSESSMENTS (4 Bimestrais por disciplina)
-- Avaliações para o Teacher Class 4 (Professor ID 4)
-- INSERT INTO assessments (id, title, subject_id, created_by_user_id, teacher_class_id, max_score, weight, is_published, is_recovery, created_by, last_modified_by, created_at, updated_at)
-- SELECT (s.id-1)*8 + i, 'Prova '||i||'º Bimestre', s.id, 4, 4, 10.0, 1.0, true, false, NULL, NULL, NOW(), NOW()
-- FROM subjects s, generate_series(1,4) i
-- WHERE s.teacher_user_id = 4;
--
-- -- Avaliações para o Teacher Class 5 (Professor ID 4 em outra turma/disciplina)
-- INSERT INTO assessments (id, title, subject_id, created_by_user_id, teacher_class_id, max_score, weight, is_published, is_recovery, created_by, last_modified_by, created_at, updated_at)
-- SELECT (s.id-1)*8 + 4 + i, 'Prova '||i||'º Bimestre', s.id, 4, 5, 10.0, 1.0, true, false, NULL, NULL, NOW(), NOW()
-- FROM subjects s, generate_series(1,4) i
-- WHERE s.teacher_user_id = 4;
INSERT INTO assessments (id, title, subject_id, created_by_user_id, teacher_class_id, max_score, weight, is_published, is_recovery, created_by, last_modified_by, created_at, updated_at)
SELECT (s.id-1)*4 + i, 'Prova '||i||'º Bimestre', s.id, s.teacher_user_id, 1, 10.0, 1.0, true, false, NULL, NULL, NOW(), NOW()
FROM subjects s, generate_series(1,4) i;

-- -- Grade
-- INSERT INTO grades (assessment_id, student_user_id, score, max_score, feedback, graded_by_user_id, graded_at, is_absent, is_excused, created_by, last_modified_by, created_at, updated_at) VALUES
-- (2, 5, 6.50, 10.00, 'Bom trabalho! Mas precisa melhor um pouco mais!', 2 ,'2025-12-09 01:18:02.657663', FALSE, FALSE, NULL, NULL, NOW(), NOW()),
-- (3, 5, 9.50, 10.00, 'Ótimo trabalho!', 2, '2025-12-09 01:18:02.659669', FALSE,FALSE, NULL, NULL, NOW(), NOW()),
-- (1, 5, 8.50, 10.00, 'Bom trabalho!', 2, '2025-12-09 03:01:36.746502', FALSE, FALSE, NULL, NULL, NOW(), NOW());

-- 11. GRADES (Lógica 60% / 35% / 5%)
-- A INSTRUÇÃO ESTAVA GERANDO UM "PRODUTO CARTESIANO". DESTA FORMA, ESTAVAM SENDO GERADAS 864 NOTAS.
-- INSERT INTO grades (assessment_id, student_user_id, score, max_score, feedback, graded_by_user_id, graded_at, is_absent, is_excused, created_by, last_modified_by, created_at, updated_at)
-- SELECT
--     a.id,
--     u.id,
--     CASE
--         WHEN u.id <= 33 THEN (6.5 + random() * 3.5) -- 60% Aprovados
--         WHEN u.id <= 39 THEN (4.0 + random() * 2.0) -- 35% Recuperação
--         ELSE (random() * 3.9)                      -- 5% Reprovados
--         END,
--     10.0,
--     'Feedback automático gerado - verificar como personalizar por faixa de nota',
--     4, NOW(), false, false, NULL, NULL, NOW(), NOW()
-- FROM assessments a, users u WHERE u.id BETWEEN 23 AND 40;

-- 12. GRADES (Lógica 60% / 35% / 5%)
-- CORREÇÃO: GERAR AS NOTAS COM BASE NA COMBINAÇÃO DE ALUNOS E AVALIAÇÕES
INSERT INTO grades (
    assessment_id, student_user_id, score, max_score,
    feedback, graded_by_user_id, graded_at,
    is_absent, is_excused, created_by, last_modified_by,
    created_at, updated_at
)
SELECT
    a.id,
    u.id,
    CASE
        WHEN u.id <= 33 THEN (6.5 + random() * 3.5) -- 60% Aprovados
        WHEN u.id <= 39 THEN (4.0 + random() * 2.0) -- 35% Recuperação
        ELSE (random() * 3.9)                      -- 5% Reprovados
        END,
    10.0,
    'Feedback automático gerado - ' ||
    CASE
        WHEN random() > 0.7 THEN 'Excelente!'
        WHEN random() > 0.4 THEN 'Bom trabalho.'
        WHEN random() > 0.2 THEN 'Pode melhorar.'
        ELSE 'Atenção necessária.'
        END,
    4,
    NOW() - (random() * INTERVAL '90 days'),
    false,
    false,
    NULL,
    NULL,
    NOW(),
    NOW()
FROM users u
         CROSS JOIN LATERAL (
    SELECT id
    FROM assessments
    ORDER BY random()
        LIMIT 12 -- 12 avaliações por aluno
) a
WHERE u.id BETWEEN 23 AND 40; -- 216 notas no total
--   AND u.user_type = 'STUDENT';

-- 10. ATTENDANCES (Presenças - Reprovação por falta para Aluno 40)
-- Gera 30 dias de presença para o Aluno 1 (90% de presença)
-- Aluno 1: 31 dias de histórico (Este está correto)
INSERT INTO attendances (student_id, subject_id, timetable_id, date, present, note, created_at)
SELECT 1, (i % 4) + 1, (i % 20) + 1, CURRENT_DATE - i,
       CASE WHEN i % 10 = 0 THEN false ELSE true END,
       'Registro automático de frequência', NOW()
FROM generate_series(0, 30) i;

-- Demais Alunos: Apenas 1 registro por dia para evitar o produto cartesiano
INSERT INTO attendances (student_id, subject_id, timetable_id, date, present, note, created_at)
SELECT
    st.id,
    1, -- Fixamos uma disciplina/horário apenas para o teste de frequência
    1,
    CURRENT_DATE - i,
    CASE WHEN st.user_id = 40 AND i < 5 THEN false ELSE true END,
    'Frequência regular', NOW()
FROM student st, generate_series(1, 10) i
WHERE st.id != 1;


-- INSERT INTO attendances (student_id, subject_id, date, present, note, created_at) VALUES
-- (1, 1, CURRENT_DATE - INTERVAL '2 days', true, 'Presença normal', NOW()),
-- (1, 1, CURRENT_DATE - INTERVAL '1 day', false, 'Falta sem justificativa', NOW()),
-- (1, 1, CURRENT_DATE, true, 'Presença normal', NOW());

-- 13. ANNOUNCEMENTS (2 Anúncios)
INSERT INTO announcements (title, description, image_path, type, order_position, created_by, last_modified_by, created_at, updated_at, is_active) VALUES
('Welcome Back', 'Welcome to the new school year!', 'path/to/image1.jpg', 'BANNER', 1, NULL, NULL, NOW(), NOW(), true),
('School Trip', 'Upcoming trip to the museum.', 'path/to/image2.jpg', 'CAROUSEL', 2, NULL, NULL, NOW(), NOW(), true);

-- Conversations
-- 14. CONVERSATIONS (1 Conversa de Exemplo)
INSERT INTO conversations (sender_id, recipient_id, student_id, subject, content, attachment_path, sent_at, read_status) VALUES
(3, 2, 1, 'Homework Question', 'Can you clarify the math homework?', NULL, NOW(), 'UNREAD');

-- 15. EVENTS (1 Evento de Exemplo)
INSERT INTO events (title, description, start_date, end_date, color, created_at, updated_at) VALUES
('Parent Meeting', 'Meeting for all parents.', '2023-11-01 18:00:00', '2023-11-01 20:00:00', '#0A2558', NOW(), NOW());

-- 16. NOTIFICATIONS (1 Notificação de Exemplo)
INSERT INTO notifications (title, message, user_id, read, created_at, type) VALUES
('New Message', 'You have a new message from Responsible User.', 2, false, NOW(), 'MESSAGE');

-- 17. MESSAGES (Mensagens entre Professor e Responsável sobre o Aluno)
INSERT INTO messages (sender_id, recipient_id, student_id, subject, content, attachment_url, attachment_name, read_at, deleted_at, archived_by_sender, archived_by_recipient, created_by, last_modified_by, created_at, updated_at) VALUES
(2, 23, 1, 'Boas-vindas', 'Olá Aluno, seja bem-vindo ao portal!', NULL, NULL, NULL, NULL, FALSE, FALSE, 'system', 'system', NOW(), NOW()),
(5, 23, 1, 'Material de Apoio', 'Acabei de liberar o PDF da aula de amanhã.', NULL, NULL, NULL, NULL, FALSE, FALSE, 'system', 'system', NOW(), NOW()),
(2, 23, 1, 'Aviso Importante', 'Lembre-se de trazer o jaleco para o laboratório.', NULL, NULL, NULL, NULL,  FALSE, FALSE,'system', 'system', NOW(), NOW()),
(5, 2, 1, 'Re: Material Extra', 'Recebido! Iremos praticar em casa. Muito obrigado.', NULL, NULL, NULL, NULL, FALSE, FALSE, 'system', 'system', NOW(), NOW()),
(2, 5, 1, 'Ausência na Aula', 'Julia, notei que o João não compareceu à aula hoje. Está tudo bem?', NULL, NULL, NULL, NULL, FALSE, FALSE,'system', 'system', NOW(), NOW()),
(5, 2, 1, 'Re: Ausência na Aula', 'Ele teve uma consulta médica, mas amanhã levará o atestado.', NULL, NULL, NULL, NULL, FALSE, FALSE, 'system', 'system', NOW(), NOW()),
-- MENSSGEM APENAS PARA TESTAR NA INICIALIZAÇÃO DO DASHBOARD DO ALUNO NO FRONT-END
(1, 2, 5, 'Bem-vindo ao Portal', 'Olá aluno, este é o seu novo dashboard!', NULL, NULL, NULL, NULL, FALSE, FALSE, 'system', 'system', NOW(), NOW());

-- Attendances
-- Limpa para evitar erros de duplicidade em re-execuções, se necessário
-- DELETE FROM attendances WHERE student_id = 1;

-- INSERT INTO attendances (student_id, subject_id, date, present, note, created_at) VALUES
-- (1, 1, CURRENT_DATE - INTERVAL '2 days', true, 'Presença normal', NOW()),
-- (1, 1, CURRENT_DATE - INTERVAL '1 day', false, 'Falta sem justificativa', NOW()),
-- (1, 1, CURRENT_DATE, true, 'Presença normal', NOW());
