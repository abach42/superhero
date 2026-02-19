CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE application_user (
    id SERIAL PRIMARY KEY,
    email VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    role smallint NOT NULL,
    deleted BOOLEAN DEFAULT false
);

CREATE TABLE superhero (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES application_user(id) ON DELETE CASCADE,
    alias VARCHAR(100),
    real_name VARCHAR(100),
    date_of_birth DATE,
    gender smallint NOT NULL,
    occupation VARCHAR(100),
    origin_story TEXT,
    deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_application_user FOREIGN KEY (user_id) REFERENCES application_user(id)
);

CREATE TABLE skill (
    skill_id SERIAL PRIMARY KEY,
    skill_name VARCHAR(100) NOT NULL
);

CREATE TABLE skill_profile (
    skill_profile_id SERIAL PRIMARY KEY,
    superhero_id INT,
    skill_id INT,
    intensity NUMERIC NOT NULL,
    CONSTRAINT fk_skill_superhero FOREIGN KEY (superhero_id) REFERENCES superhero(id),
    CONSTRAINT fk_skill FOREIGN KEY (skill_id) REFERENCES Skill(skill_id),
    CONSTRAINT unique_superhero_skill UNIQUE (superhero_id, skill_id)
);

INSERT INTO skill (skill_name) VALUES
('strength'), -- 1 Stärke
('speed'), -- 2 Schnelligkeit
('self confidence'), -- 3 Selbstvertrauen
('patience'), -- 4 Geduld
('kindness'), -- 5 Freundlichkeit
('courage'), -- 6 Mut
('compassion'), -- 7 Mitgefühl
('loyalty'), -- 8 Treue
('generosity'), -- 9 Großzügigkeit
('empathy'), -- 10 Empathie
('wisdom'), -- 11 Weisheit
('creativity'), -- 12 Kreativität
('peacekeeping'), -- 13 Friedensstifter
('charity'), -- 14 Nächstenliebe
('gentleness'), -- 15 Sanftmut
('self control'); -- 16 Selbstbeherrschung

INSERT INTO application_user (email, password, role)
VALUES
('admin@example.com', '{bcrypt}$2a$12$anOoGoXF50pcBQMlUSDA..s7mJjQakVJ3dGXkQm3iRAiigRZWvts6', 0),
('captain@example.com', '{bcrypt}$2a$12$4cAnXB4ziicHOuRiV4XVQOhCNr3w4N95Q07d3d1SNovRmLFfcPchS', 1),
('speedster@example.com', '{bcrypt}$2a$12$XTxxbXQmCVr.c10nOM/V2OE0tFwxwumS.CDqOjFg6GkST2VhXwUuy', 1),
('knight@example.com', '{bcrypt}$2a$12$tHbblSycdUJgFF6/nJ/yG.Fwz7tESgDMek9DRSTnKQhRlyz4Mtr32', 1),
('empathy@example.com', '{bcrypt}$2a$12$o41irHYILPDLVR.RcMk8IeOVpe2QlbhqK8ocbbCOxzGaindMZtHW.', 1),
('genius@example.com', '{bcrypt}$2a$12$nEp7ynfqWgYXX4hQTvkOkO.2XiFoIvk5ovBw0ehygF2apBTlLz./m', 1),
('giant@example.com', '{bcrypt}$2a$12$1EUCNLOWgT95K/Wq3LYU.egM6AWt2zl4bJ7QCx7XPJ3uwBJUsl0Ye', 1),
('peacekeeper@example.com', '{bcrypt}$2a$12$J5yrKvd5K0YN6Thy4aI.bes1hzurkc0fjDDlN/smvfqkV5IxZ7JuS', 1),
('swimmer@example.com', '{bcrypt}$2a$12$gF45UC2TnJnWZA9Wg3So..sruiYXYXwqfVUecQXfSUcWI1n/JQVY.', 1),
('kindknight@example.com', '{bcrypt}$2a$12$jDIBR0Bu6i/rMlMYJ/TiquwjStIH2qweKywiRa3oxV3MyClROh58O', 1),
('wizard@example.com', '{bcrypt}$2a$12$XOAwRzHyJ3qe2NRpFIN56uBKVh/buBHpTUmU0Cc8Vn2n2H0WQJjWW', 1),
('cat@example.com', '{bcrypt}$2a$12$iRtrPjgggB42.Q5L0HiskOtzB/38jKN/96nIJe1aXo7rHh..u9MAm', 1),
('loyalty@example.com', '{bcrypt}$2a$12$eQbyf1v2gR10DMZJhVcvLupQxuMk/IFMGur97Nd0ucRTI/.OQK0NG', 1),
('guardian@example.com', '{bcrypt}$2a$12$OHuZ3xXiwavZZqKG28B/s.9GT8cLmkexSxeiajsalYmJcxJeHk4Mq', 1),
('eagle@example.com', '{bcrypt}$2a$12$Ya7ekHzToVdqyeuiozpoJODLto6HABrDNkSQ8Uigz2CgkvC0e/aUy', 1),
('owl@example.com', '{bcrypt}$2a$12$gErPAHGz.PrcwrTkLkZ2euMD02qqTYR3q7bjw0VAaEMenVQFrsnaC', 1);

INSERT INTO superhero (user_id, alias, real_name, date_of_birth, gender, occupation, origin_story)
VALUES
(1, 'The Administrator', 'Michael Anderson', '1987-07-22', 0, 'Strategic Operations Director',
'After leading digital transformation initiatives across public institutions, he became known for building resilient governance systems. His expertise lies in crisis coordination, institutional reform, and transparent administrative structures that strengthen democratic stability.'),
(2, 'Captain Courageous', 'Jonathan Hayes', '1980-05-15', 0, 'Civil Rights Advocate',
'Originally a legal observer during high-risk civil rights protests, he developed a reputation for standing firm in volatile situations. He now works on protecting vulnerable communities and promoting lawful, nonviolent resistance to injustice.'),
(3, 'Speedster', 'Natalie Brooks', '1992-08-21', 1, 'Emergency Logistics Coordinator',
'Working in rapid disaster-response supply chains, she optimized emergency transport networks across regions. Her strength lies in mobilizing resources with exceptional speed and precision during humanitarian crises.'),
(4, 'The Kindness Knight', 'Thomas Gallagher', '1975-02-10', 0, 'Community Social Worker',
'After decades in underserved neighborhoods, he built community-based support networks that reduce crime through mentorship and restorative programs. His influence stems from trust, compassion, and long-term relationship building.'),
(5, 'Doctor Empathy', 'Dr. Rebecca Lawson', '1988-11-03', 1, 'Clinical Psychologist',
'Specialized in trauma recovery and intercultural psychology, she facilitates reconciliation programs in divided communities. Her approach integrates emotional intelligence, structured dialogue, and evidence-based therapy.'),
(6, 'Creative Genius', 'Isabella Morgan', '1995-04-17', 1, 'Innovation Strategist',
'Through interdisciplinary design thinking and artistic strategy workshops, she develops unconventional solutions to systemic social challenges. Her work combines creativity with pragmatic implementation models.'),
(7, 'The Gentle Giant', 'Marcus Thompson', '1978-07-09', 0, 'Structural Safety Engineer',
'With a background in heavy infrastructure projects, he ensures safety in high-risk construction zones. Known for calm leadership under pressure, he combines physical capability with measured restraint.'),
(8, 'The Peacekeeper', 'Daniel Foster', '1970-03-25', 0, 'International Mediator',
'Having negotiated ceasefires in politically unstable regions, he specializes in diplomatic de-escalation, dialogue facilitation, and multilateral peace frameworks.'),
(9, 'The Swift Swimmer', 'Emily Carter', '1998-02-18', 1, 'Coastal Rescue Specialist',
'As a maritime rescue coordinator, she leads high-risk sea evacuations and emergency response operations. Her expertise blends physical endurance with rapid situational assessment.'),
(10, 'The Kind-hearted Knight', 'William Turner', '1972-06-30', 0, 'Civic Education Teacher',
'Dedicated to inclusive education, he designs civic literacy programs that promote empathy, social responsibility, and democratic participation among youth.'),
(11, 'The Wise Wizard', 'Professor Richard Collins', '1960-11-20', 0, 'Public Policy Scholar',
'A senior researcher in governance ethics and historical conflict studies, he advises institutions on long-term stability strategies and preventative policy frameworks.'),
(12, 'The Courageous Cat', 'Daniel Carter', '1983-10-05', 0, 'Urban Search & Rescue Commander',
'After coordinating multi-agency disaster responses in earthquake zones, he became known for entering unstable areas to negotiate safe evacuations and calm civilian panic. His leadership focuses on disciplined courage, structured conflict de-escalation, and protection of vulnerable communities.'),
(13, 'Lady Loyalty', 'Olivia Bennett', '1986-12-30', 1, 'International Humanitarian Veterinarian',
'While leading cross-border animal health missions in crisis regions, she built trust between divided rural communities. Her work bridges cultural tensions through shared care initiatives and long-term relationship building.'),
(14, 'The Generous Guardian', 'Charlotte Whitmore', '1990-06-12', 1, 'Global Education Foundation Director',
'After inheriting a multinational foundation, she redirected its assets into education reform, scholarship systems, and social inclusion programs in underfunded regions. She specializes in strategic philanthropy and sustainable institutional development.'),
(15, 'The Empathetic Eagle', 'Sophia Reynolds', '1984-09-12', 1, 'Conflict Resolution Therapist',
'Trained in trauma therapy and restorative justice, she mediates post-conflict reconciliation programs. She facilitates dialogue between opposing groups and designs emotional resilience training for communities affected by violence.'),
(16, 'The Wise Owl', 'Edward Harrington', '1963-09-28', 0, 'Retired Ethics Professor',
'A former professor of moral philosophy and civic education, he now advises NGOs and policy groups on ethical governance, peaceful reform, and education-based conflict prevention strategies.');

INSERT INTO skill_profile (superhero_id, skill_id, intensity)
VALUES
-- 1 The Administrator
(1,1,2),(1,2,2),(1,3,5),(1,4,4),(1,5,3),(1,6,4),
(1,7,3),(1,8,4),(1,9,3),(1,10,3),(1,11,5),(1,12,4),
(1,13,4),(1,14,3),(1,15,3),(1,16,5),

-- 2 Captain Courageous
(2,1,4),(2,2,3),(2,3,5),(2,4,3),(2,5,3),(2,6,5),
(2,7,4),(2,8,4),(2,9,3),(2,10,3),(2,11,4),(2,12,3),
(2,13,4),(2,14,2),(2,15,2),(2,16,4),

-- 3 Speedster
(3,1,3),(3,2,5),(3,3,4),(3,4,3),(3,5,3),(3,6,4),
(3,7,3),(3,8,3),(3,9,3),(3,10,3),(3,11,3),(3,12,3),
(3,13,3),(3,14,2),(3,15,2),(3,16,4),

-- 4 The Kindness Knight
(4,1,3),(4,2,2),(4,3,4),(4,4,5),(4,5,5),(4,6,3),
(4,7,5),(4,8,4),(4,9,4),(4,10,5),(4,11,4),(4,12,3),
(4,13,4),(4,14,4),(4,15,5),(4,16,4),

-- 5 Doctor Empathy
(5,1,2),(5,2,2),(5,3,4),(5,4,5),(5,5,4),(5,6,3),
(5,7,5),(5,8,3),(5,9,3),(5,10,5),(5,11,5),(5,12,3),
(5,13,5),(5,14,3),(5,15,5),(5,16,5),

-- 6 Creative Genius
(6,1,2),(6,2,3),(6,3,4),(6,4,3),(6,5,3),(6,6,3),
(6,7,3),(6,8,3),(6,9,3),(6,10,3),(6,11,4),(6,12,5),
(6,13,3),(6,14,2),(6,15,2),(6,16,4),

-- 7 The Gentle Giant
(7,1,5),(7,2,2),(7,3,4),(7,4,4),(7,5,4),(7,6,4),
(7,7,4),(7,8,3),(7,9,3),(7,10,3),(7,11,3),(7,12,2),
(7,13,4),(7,14,2),(7,15,5),(7,16,5),

-- 8 The Peacekeeper
(8,1,3),(8,2,2),(8,3,5),(8,4,5),(8,5,4),(8,6,4),
(8,7,5),(8,8,4),(8,9,3),(8,10,5),(8,11,5),(8,12,3),
(8,13,5),(8,14,3),(8,15,4),(8,16,5),

-- 9 The Swift Swimmer
(9,1,4),(9,2,4),(9,3,4),(9,4,3),(9,5,3),(9,6,4),
(9,7,3),(9,8,3),(9,9,3),(9,10,3),(9,11,3),(9,12,3),
(9,13,3),(9,14,2),(9,15,3),(9,16,4),

-- 10 The Kind-hearted Knight
(10,1,2),(10,2,2),(10,3,4),(10,4,5),(10,5,5),(10,6,3),
(10,7,5),(10,8,4),(10,9,4),(10,10,5),(10,11,4),(10,12,3),
(10,13,4),(10,14,4),(10,15,5),(10,16,4),

-- 11 The Wise Wizard
(11,1,1),(11,2,1),(11,3,4),(11,4,5),(11,5,4),(11,6,3),
(11,7,4),(11,8,3),(11,9,3),(11,10,4),(11,11,5),(11,12,4),
(11,13,5),(11,14,3),(11,15,3),(11,16,5),

-- 12 Daniel Carter
(12,1,4),(12,2,3),(12,3,4),(12,4,4),(12,5,3),(12,6,5),
(12,7,4),(12,8,4),(12,9,3),(12,10,4),(12,11,4),(12,12,3),
(12,13,5),(12,14,3),(12,15,3),(12,16,5),

-- 13 Olivia Bennett
(13,1,2),(13,2,2),(13,3,4),(13,4,5),(13,5,5),(13,6,3),
(13,7,5),(13,8,5),(13,9,4),(13,10,4),(13,11,4),(13,12,3),
(13,13,4),(13,14,4),(13,15,5),(13,16,4),

-- 14 Charlotte Whitmore
(14,1,2),(14,2,2),(14,3,5),(14,4,4),(14,5,4),(14,6,3),
(14,7,4),(14,8,3),(14,9,5),(14,10,4),(14,11,5),(14,12,4),
(14,13,4),(14,14,5),(14,15,3),(14,16,4),

-- 15 Sophia Reynolds
(15,1,3),(15,2,3),(15,3,4),(15,4,5),(15,5,5),(15,6,4),
(15,7,5),(15,8,3),(15,9,3),(15,10,5),(15,11,5),(15,12,4),
(15,13,5),(15,14,3),(15,15,5),(15,16,5),

-- 16 Edward Harrington
(16,1,1),(16,2,1),(16,3,4),(16,4,5),(16,5,4),(16,6,3),
(16,7,4),(16,8,3),(16,9,3),(16,10,4),(16,11,5),(16,12,3),
(16,13,5),(16,14,3),(16,15,3),(16,16,5);

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vector_profiles (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(1024)
);

CREATE INDEX IF NOT EXISTS vector_profiles_embedding_idx
ON vector_profiles
USING hnsw (embedding vector_cosine_ops);

CREATE TABLE IF NOT EXISTS vector_all (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(1024)
);

CREATE INDEX IF NOT EXISTS vector_all_embedding_idx
ON vector_all
USING hnsw (embedding vector_cosine_ops);