CREATE EXTENSION pgcrypto;

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
    gender VARCHAR(10),
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
    CONSTRAINT fk_superhero FOREIGN KEY (superhero_id) REFERENCES superhero(id),
    CONSTRAINT fk_skill FOREIGN KEY (skill_id) REFERENCES Skill(skill_id),
    CONSTRAINT unique_superhero_skill UNIQUE (superhero_id, skill_id)
);

INSERT INTO skill (skill_name) VALUES
('strength'), -- Stärke
('speed'), -- Schnelligkeit
('self_confidence'), -- Selbstvertrauen
('patience'), -- Geduld
('kindness'), -- Freundlichkeit
('courage'), -- Mut
('compassion'), -- Mitgefühl
('loyalty'), -- Treue
('generosity'), -- Großzügigkeit
('empathy'), -- Empathie
('wisdom'), -- Weisheit
('creativity'), -- Kreativität
('peacekeeping'), -- Friedensstifter
('charity'), -- Nächstenliebe
('gentleness'), -- Sanftmut
('self_control'), -- Selbstbeherrschung
('good_looks'); -- Gut-Aussehen

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
(1, 'The Administrator', 'Andreas Admin', '1987-07-22', 'Male', 'Administrator', 'Alex Admin gained superhuman administrative skills after being exposed to a mysterious computer virus.'),
(2, 'Captain Courageous', 'Chris Courage', '1980-05-15', 'Male', 'Defender of Justice', 'Chris Courage was a regular citizen until a fateful accident imbued him with superhuman courage.'),
(3, 'Speedster', 'Samantha Swift', '1992-08-21', 'Female', 'Delivery Driver', 'Samantha Swift gained the ability to move at incredible speeds after being struck by lightning.'),
(4, 'The Kindness Knight', 'Kevin Knight', '1975-02-10', 'Male', 'Social Worker', 'Kevin Knight was inspired to become a hero after witnessing acts of kindness in his community.'),
(5, 'Doctor Empathy', 'Emma Empath', '1988-11-03', 'Female', 'Psychologist', 'Emma Empath developed the power to sense and understand the emotions of others during a medical experiment.'),
(6, 'The Wise Owl', 'Oliver Wisdom', '1963-09-28', 'Male', 'Retired Teacher', 'Oliver Wisdom''s wisdom is legendary, and he uses his knowledge to guide and mentor other heroes.'),
(7, 'Creative Genius', 'Cassandra Creative', '1995-04-17', 'Female', 'Artist', 'Cassandra Creative''s imaginative mind allows her to come up with ingenious solutions to any problem.'),
(8, 'The Gentle Giant', 'Gary Gentle', '1978-07-09', 'Male', 'Construction Worker', 'Gary Gentle possesses superhuman strength, but he uses it with care and gentleness to help others.'),
(9, 'Lady Loyalty', 'Leah Loyal', '1986-12-30', 'Female', 'Veterinarian', 'Leah Loyal is known for her unwavering loyalty to her friends and the causes she believes in.'),
(10, 'The Peacekeeper', 'Pete Peace', '1970-03-25', 'Male', 'Mediator', 'Pete Peace has the ability to calm any situation and find peaceful resolutions to conflicts.'),
(11, 'The Generous Guardian', 'Grace Generous', '1990-06-12', 'Female', 'Philanthropist', 'Grace Generous uses her wealth and resources to help those in need and make the world a better place.'),
(12, 'The Courageous Cat', 'Carlos Courage', '1983-10-05', 'Male', 'Firefighter', 'Carlos Courage fearlessly rushes into danger to save lives, inspired by his love for his pet cat.'),
(13, 'The Swift Swimmer', 'Sarah Swift', '1998-02-18', 'Female', 'Lifeguard', 'Sarah Swift is not only a fast swimmer but also a quick thinker, always ready to dive into action to save others.'),
(14, 'The Kind-hearted Knight', 'Keith Kindheart', '1972-06-30', 'Male', 'Teacher', 'Keith Kindheart is beloved by his community for his compassionate nature and willingness to help others.'),
(15, 'The Empathetic Eagle', 'Ella Empathy', '1984-09-12', 'Female', 'Therapist', 'Ella Empathy can feel the pain of others and uses her powers to heal both physically and emotionally.'),
(16, 'The Wise Wizard', 'Walter Wisdom', '1960-11-20', 'Male', 'Librarian', 'Walter Wisdom''s vast knowledge of ancient texts and magical artifacts makes him a formidable ally in the fight against evil.');

INSERT INTO skill_profile (superhero_id, skill_id, intensity)
VALUES
-- Insert skill profiles for 'The Administrator'
(1, 1, 4), (1, 7, 2), (1, 11, 5), (1, 15, 3), (1, 5, 4), (1, 10, 1),
-- Insert skill profiles for 'Captain Courageous'
(2, 6, 3), (2, 7, 5), (2, 12, 2), (2, 16, 4), (2, 1, 3), (2, 11, 5), (2, 14, 2),
-- Insert skill profiles for 'Speedster'
(3, 2, 5), (3, 10, 4), (3, 15, 3), (3, 16, 2), (3, 5, 1), (3, 9, 5), (3, 13, 3),
-- Insert skill profiles for 'The Kindness Knight'
(4, 5, 5), (4, 7, 4), (4, 9, 3), (4, 15, 2), (4, 1, 4), (4, 3, 3), (4, 8, 5), (4, 14, 1),
-- Insert skill profiles for 'Doctor Empathy'
(5, 7, 5), (5, 9, 4), (5, 10, 3), (5, 11, 2), (5, 2, 5), (5, 4, 3), (5, 12, 4), (5, 15, 1),
-- Insert skill profiles for 'The Wise Owl'
(6, 6, 5), (6, 10, 4), (6, 11, 3), (6, 16, 2), (6, 3, 5), (6, 8, 2), (6, 13, 4), (6, 14, 3),
-- Insert skill profiles for 'Creative Genius'
(7, 11, 5), (7, 12, 4), (7, 13, 3), (7, 16, 2), (7, 2, 4), (7, 4, 3), (7, 5, 2), (7, 8, 5),
-- Insert skill profiles for 'The Gentle Giant'
(8, 1, 5), (8, 7, 4), (8, 9, 3), (8, 15, 2), (8, 3, 5), (8, 6, 3), (8, 10, 4), (8, 14, 1),
-- Insert skill profiles for 'Lady Loyalty'
(9, 7, 5), (9, 9, 4), (9, 11, 3), (9, 16, 2), (9, 1, 3), (9, 4, 4), (9, 12, 5), (9, 15, 1),
-- Insert skill profiles for 'The Peacekeeper'
(10, 10, 5), (10, 11, 4), (10, 13, 3), (10, 15, 2), (10, 2, 5), (10, 3, 3), (10, 7, 4), (10, 9, 1),
-- Insert skill profiles for 'The Generous Guardian'
(11, 9, 5), (11, 11, 4), (11, 12, 3), (11, 16, 2), (11, 1, 2), (11, 5, 3), (11, 13, 4), (11, 14, 1),
-- Insert skill profiles for 'The Courageous Cat'
(12, 1, 5), (12, 7, 4), (12, 12, 3), (12, 16, 2), (12, 2, 5), (12, 4, 3), (12, 9, 4), (12, 15, 1),
-- Insert skill profiles for 'The Swift Swimmer'
(13, 2, 5), (13, 11, 4), (13, 12, 3), (13, 16, 2), (13, 6, 5), (13, 8, 3), (13, 9, 4), (13, 15, 1),
-- Insert skill profiles for 'The Kind-hearted Knight'
(14, 5, 5), (14, 7, 4), (14, 11, 3), (14, 16, 2), (14, 2, 4), (14, 3, 3), (14, 10, 5), (14, 13, 1),
-- Insert skill profiles for 'The Empathetic Eagle'
(15, 7, 5), (15, 11, 4), (15, 15, 3), (15, 16, 2), (15, 1, 3), (15, 6, 4), (15, 12, 5), (15, 14, 1),
-- Insert skill profiles for 'The Wise Wizard'
(16, 6, 5), (16, 10, 4), (16, 11, 3), (16, 16, 2), (16, 2, 5), (16, 5, 3), (16, 9, 4), (16, 13, 1);
