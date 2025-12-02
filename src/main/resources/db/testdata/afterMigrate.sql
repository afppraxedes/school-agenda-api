-- Users
INSERT INTO users (email, username, password, name, push_subscription) VALUES
('director@school.com', 'director', '$2a$10$wPHGWKkG.gL.m.j/V.i/..5/..', 'Director User', NULL),
('teacher@school.com', 'teacher', '$2a$10$wPHGWKkG.gL.m.j/V.i/..5/..', 'Teacher User', NULL),
('responsible@school.com', 'responsible', '$2a$10$wPHGWKkG.gL.m.j/V.i/..5/..', 'Responsible User', NULL),
('admin@school.com', 'admin', '$2a$10$eAccYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORr7x.56trjDZ.mc7.', 'Administrator', NULL),
('student@school.com', 'student', '$2a$10$eAccYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORr7x.56trjDZ.mc7.', 'Student User', NULL);

-- User Roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'DIRECTOR'),
(2, 'TEACHER'),
(3, 'RESPONSIBLE'),
(4, 'ADMINISTRATOR'),
(5, 'STUDENT');

-- Students
INSERT INTO student (full_name, birth_date, class_name, profile_photo, registration_date, user_id) VALUES
('Alice Student', '2015-05-10', 'Class 1A', NULL, NOW(), NULL),
('Bob Student', '2014-08-20', 'Class 2B', NULL, NOW(), NULL),
('Student User', '2010-01-01', 'Class 3C', NULL, NOW(), 5);

-- Responsible Student
INSERT INTO responsable_student (responsable_id, student_id, created_at, updated_at) VALUES
(3, 1, NOW(), NOW()),
(3, 2, NOW(), NOW());

-- Announcements
INSERT INTO announcements (title, description, image_path, type, order_position, created_at, updated_at, is_active) VALUES
('Welcome Back', 'Welcome to the new school year!', 'path/to/image1.jpg', 'BANNER', 1, NOW(), NOW(), true),
('School Trip', 'Upcoming trip to the museum.', 'path/to/image2.jpg', 'CAROUSEL', 2, NOW(), NOW(), true);

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
INSERT INTO teacher_classes (teacher_id, class_name, created_at, updated_at) VALUES
(2, 'Class 1A', NOW(), NOW()),
(2, 'Class 2B', NOW(), NOW());
