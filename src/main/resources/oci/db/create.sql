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
('self control'), -- 16 Selbstbeherrschung
('good looks'); -- 17 Gut-Aussehen

INSERT INTO application_user (email, password, role)
VALUES
('admin@example.com', '{bcrypt}$2a$12$anOoGoXF50pcBQMlUSDA..s7mJjQakVJ3dGXkQm3iRAiigRZWvts6', 0),
('chris@example.com', '{bcrypt}$2a$12$4cAnXB4ziicHOuRiV4XVQOhCNr3w4N95Q07d3d1SNovRmLFfcPchS', 1),
('samantha@example.com', '{bcrypt}$2a$12$XTxxbXQmCVr.c10nOM/V2OE0tFwxwumS.CDqOjFg6GkST2VhXwUuy', 1),
('kevin@example.com', '{bcrypt}$2a$12$tHbblSycdUJgFF6/nJ/yG.Fwz7tESgDMek9DRSTnKQhRlyz4Mtr32', 1),
('emma@example.com', '{bcrypt}$2a$12$o41irHYILPDLVR.RcMk8IeOVpe2QlbhqK8ocbbCOxzGaindMZtHW.', 1),
('oliver@example.com', '{bcrypt}$2a$12$nEp7ynfqWgYXX4hQTvkOkO.2XiFoIvk5ovBw0ehygF2apBTlLz./m', 1),
('cassandra@example.com', '{bcrypt}$2a$12$1EUCNLOWgT95K/Wq3LYU.egM6AWt2zl4bJ7QCx7XPJ3uwBJUsl0Ye', 1),
('gary@example.com', '{bcrypt}$2a$12$J5yrKvd5K0YN6Thy4aI.bes1hzurkc0fjDDlN/smvfqkV5IxZ7JuS', 1),
('leah@example.com', '{bcrypt}$2a$12$gF45UC2TnJnWZA9Wg3So..sruiYXYXwqfVUecQXfSUcWI1n/JQVY.', 1),
('pete@example.com', '{bcrypt}$2a$12$jDIBR0Bu6i/rMlMYJ/TiquwjStIH2qweKywiRa3oxV3MyClROh58O', 1),
('grace@example.com', '{bcrypt}$2a$12$XOAwRzHyJ3qe2NRpFIN56uBKVh/buBHpTUmU0Cc8Vn2n2H0WQJjWW', 1),
('carlos@example.com', '{bcrypt}$2a$12$iRtrPjgggB42.Q5L0HiskOtzB/38jKN/96nIJe1aXo7rHh..u9MAm', 1),
('sarah@example.com', '{bcrypt}$2a$12$eQbyf1v2gR10DMZJhVcvLupQxuMk/IFMGur97Nd0ucRTI/.OQK0NG', 1),
('keith@example.com', '{bcrypt}$2a$12$OHuZ3xXiwavZZqKG28B/s.9GT8cLmkexSxeiajsalYmJcxJeHk4Mq', 1),
('ella@example.com', '{bcrypt}$2a$12$Ya7ekHzToVdqyeuiozpoJODLto6HABrDNkSQ8Uigz2CgkvC0e/aUy', 1),
('walter@example.com', '{bcrypt}$2a$12$gErPAHGz.PrcwrTkLkZ2euMD02qqTYR3q7bjw0VAaEMenVQFrsnaC', 1);

INSERT INTO superhero (user_id, alias, real_name, date_of_birth, gender, occupation, origin_story)
VALUES
(1, 'The Administrator', 'Andreas Admin', '1987-07-22', 0, 'Administrator', 'Alex Admin gained superhuman administrative skills after being exposed to a mysterious computer virus.'),
(2, 'Captain Courageous', 'Chris Courage', '1980-05-15', 0, 'Defender of Justice', 'Chris Courage was a regular citizen until a fateful accident imbued him with superhuman courage.'),
(3, 'Speedster', 'Samantha Swift', '1992-08-21', 1, 'Delivery Driver', 'Samantha Swift gained the ability to move at incredible speeds after being struck by lightning.'),
(4, 'The Kindness Knight', 'Kevin Knight', '1975-02-10', 0, 'Social Worker', 'Kevin Knight was inspired to become a hero after witnessing acts of kindness in his community.'),
(5, 'Doctor Empathy', 'Emma Empath', '1988-11-03', 1, 'Psychologist', 'Emma Empath developed the power to sense and understand the emotions of others during a medical research project to rescue little puppies.'),
(6, 'The Wise Owl', 'Oliver Wisdom', '1963-09-28', 0, 'Retired Teacher', 'Oliver Wisdom''s wisdom is legendary, and he uses his knowledge to guide and mentor other heroes.'),
(7, 'Creative Genius', 'Cassandra Creative', '1995-04-17', 1, 'Artist', 'Cassandra Creative''s imaginative mind allows her to come up with ingenious solutions to any problem.'),
(8, 'The Gentle Giant', 'Gary Gentle', '1978-07-09', 0, 'Construction Worker', 'Gary Gentle possesses superhuman strength, but he uses it with care and gentleness to help others.'),
(9, 'Lady Loyalty', 'Leah Loyal', '1986-12-30', 1, 'Veterinarian', 'Leah Loyal is known for her unwavering loyalty to her friends and the causes she believes in.'),
(10, 'The Peacekeeper', 'Pete Peace', '1970-03-25', 0, 'Mediator', 'Pete Peace has the ability to calm any situation and find peaceful resolutions to conflicts.'),
(11, 'The Generous Guardian', 'Grace Generous', '1990-06-12', 1, 'Philanthropist', 'Grace Generous uses her wealth and resources to help those in need and make the world a better place.'),
(12, 'The Courageous Cat', 'Carlos Courage', '1983-10-05', 0, 'Firefighter', 'Carlos Courage fearlessly rushes into danger to save lives, inspired by his love for his pet cat.'),
(13, 'The Swift Swimmer', 'Sarah Swift', '1998-02-18', 1, 'Lifeguard', 'Sarah Swift is not only a fast swimmer but also a quick thinker, always ready to dive into action to save others.'),
(14, 'The Kind-hearted Knight', 'Keith Kindheart', '1972-06-30', 0, 'Teacher', 'Keith Kindheart is beloved by his community for his compassionate nature and willingness to help others.'),
(15, 'The Empathetic Eagle', 'Ella Empathy', '1984-09-12', 1, 'Therapist', 'Ella Empathy can feel the pain of others and uses her powers to heal both physically and emotionally.'),
(16, 'The Wise Wizard', 'Walter Wisdom', '1960-11-20', 0, 'Librarian', 'Walter Wisdom''s vast knowledge of ancient texts and magical artifacts makes him a formidable ally in the fight against evil.');

INSERT INTO skill_profile (superhero_id, skill_id, intensity) VALUES
-- 1 The Administrator
(1,1,2),(1,2,1),(1,3,4),(1,4,5),(1,5,3),(1,6,4),(1,7,3),(1,8,4),(1,9,2),(1,10,3),(1,11,5),(1,12,2),(1,13,4),(1,14,2),(1,15,2),(1,16,5),(1,17,1),
-- 2 Captain Courageous
(2,1,4),(2,2,3),(2,3,5),(2,4,3),(2,5,3),(2,6,5),(2,7,4),(2,8,4),(2,9,2),(2,10,3),(2,11,3),(2,12,2),(2,13,5),(2,14,2),(2,15,2),(2,16,4),(2,17,3),
-- 3 Speedster
(3,1,2),(3,2,5),(3,3,4),(3,4,2),(3,5,2),(3,6,4),(3,7,2),(3,8,2),(3,9,2),(3,10,3),(3,11,2),(3,12,2),(3,13,2),(3,14,1),(3,15,1),(3,16,3),(3,17,3),
-- 4 The Kindness Knight
(4,1,4),(4,2,2),(4,3,4),(4,4,4),(4,5,5),(4,6,3),(4,7,5),(4,8,5),(4,9,4),(4,10,3),(4,11,3),(4,12,2),(4,13,4),(4,14,5),(4,15,4),(4,16,3),(4,17,2),
-- 5 Doctor Empathy
(5,1,2),(5,2,3),(5,3,4),(5,4,4),(5,5,4),(5,6,3),(5,7,5),(5,8,3),(5,9,3),(5,10,5),(5,11,4),(5,12,3),(5,13,4),(5,14,3),(5,15,5),(5,16,4),(5,17,3),
-- 6 The Wise Owl
(6,1,1),(6,2,1),(6,3,4),(6,4,5),(6,5,3),(6,6,3),(6,7,3),(6,8,3),(6,9,2),(6,10,3),(6,11,5),(6,12,2),(6,13,4),(6,14,3),(6,15,2),(6,16,5),(6,17,1),
-- 7 Creative Genius
(7,1,2),(7,2,3),(7,3,4),(7,4,3),(7,5,3),(7,6,1),(7,7,3),(7,8,4),(7,9,2),(7,10,3),(7,11,4),(7,12,5),(7,13,3),(7,14,2),(7,15,2),(7,16,4),(7,17,5),
-- 8 The Gentle Giant
(8,1,5),(8,2,2),(8,3,3),(8,4,4),(8,5,4),(8,6,4),(8,7,4),(8,8,4),(8,9,4),(8,10,3),(8,11,3),(8,12,2),(8,13,4),(8,14,4),(8,15,5),(8,16,4),(8,17,1),
-- 9 Lady Loyalty
(9,1,2),(9,2,2),(9,3,4),(9,4,4),(9,5,4),(9,6,3),(9,7,4),(9,8,5),(9,9,3),(9,10,3),(9,11,3),(9,12,3),(9,13,3),(9,14,3),(9,15,3),(9,16,4),(9,17,3),
-- 10 The Peacekeeper
(10,1,2),(10,2,3),(10,3,4),(10,4,5),(10,5,4),(10,6,3),(10,7,4),(10,8,3),(10,9,3),(10,10,4),(10,11,4),(10,12,2),(10,13,5),(10,14,3),(10,15,4),(10,16,5),(10,17,2),
-- 11 The Generous Guardian
(11,1,2),(11,2,2),(11,3,4),(11,4,4),(11,5,4),(11,6,3),(11,7,3),(11,8,3),(11,9,5),(11,10,3),(11,11,4),(11,12,3),(11,13,3),(11,14,5),(11,15,3),(11,16,4),(11,17,3),
-- 12 The Courageous Cat
(12,1,4),(12,2,4),(12,3,4),(12,4,3),(12,5,3),(12,6,5),(12,7,3),(12,8,3),(12,9,3),(12,10,3),(12,11,3),(12,12,3),(12,13,3),(12,14,2),(12,15,2),(12,16,4),(12,17,3),
-- 13 The Swift Swimmer
(13,1,3),(13,2,5),(13,3,4),(13,4,3),(13,5,3),(13,6,4),(13,7,3),(13,8,3),(13,9,3),(13,10,4),(13,11,3),(13,12,3),(13,13,3),(13,14,2),(13,15,2),(13,16,4),(13,17,4),
-- 14 The Kind-hearted Knight
(14,1,3),(14,2,2),(14,3,4),(14,4,4),(14,5,5),(14,6,3),(14,7,5),(14,8,4),(14,9,3),(14,10,4),(14,11,4),(14,12,2),(14,13,3),(14,14,4),(14,15,4),(14,16,4),(14,17,2),
-- 15 The Empathetic Eagle
(15,1,3),(15,2,3),(15,3,4),(15,4,3),(15,5,4),(15,6,4),(15,7,5),(15,8,3),(15,9,3),(15,10,5),(15,11,4),(15,12,4),(15,13,3),(15,14,3),(15,15,4),(15,16,4),(15,17,5),
-- 16 The Wise Wizard
(16,1,1),(16,2,2),(16,3,4),(16,4,5),(16,5,3),(16,6,3),(16,7,3),(16,8,2),(16,9,2),(16,10,3),(16,11,5),(16,12,3),(16,13,3),(16,14,2),(16,15,2),(16,16,5),(16,17,1);

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vector_store (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    content text,
    metadata jsonb,
    embedding vector(1024)
);

CREATE INDEX IF NOT EXISTS vector_store_embedding_idx
ON vector_store
USING hnsw (embedding vector_cosine_ops);
