CREATE EXTENSION pgcrypto;

CREATE TABLE superhero (
    id SERIAL PRIMARY KEY,
    alias VARCHAR(100),
    real_name VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(10),
    occupation VARCHAR(100),
    origin_story TEXT,
    deleted BOOLEAN DEFAULT false
);

CREATE TABLE skill (
    skill_id SERIAL PRIMARY KEY,
    skill_name VARCHAR(100) NOT NULL,
    deleted BOOLEAN DEFAULT false
);

CREATE TABLE skill_profile (
    skill_profile_id SERIAL PRIMARY KEY,
    superhero_id INT REFERENCES superhero(id),
    skill_id INT REFERENCES Skill(skill_id),
    intensity NUMERIC NOT NULL,
    CONSTRAINT fk_superhero FOREIGN KEY (superhero_id) REFERENCES superhero(id),
    CONSTRAINT fk_skill FOREIGN KEY (skill_id) REFERENCES Skill(skill_id),
    deleted BOOLEAN DEFAULT false
);

CREATE TABLE rescue_action (
    rescue_action_id SERIAL PRIMARY KEY,
    superhero_initiator_id INT REFERENCES superhero(id),
    action VARCHAR(100) NOT NULL,
    description TEXT,
    CONSTRAINT fk_superhero_initiator_rescue FOREIGN KEY (superhero_initiator_id) REFERENCES superhero(id),
    deleted BOOLEAN DEFAULT false
);

CREATE TABLE participation (
    participation_id SERIAL PRIMARY KEY,
    rescue_action_id INT REFERENCES rescue_action(rescue_action_id),
    superhero_id INT REFERENCES superhero(id),
    CONSTRAINT fk_rescue_action FOREIGN KEY (rescue_action_id) REFERENCES rescue_action(rescue_action_id),
    CONSTRAINT fk_superhero_participation FOREIGN KEY (superhero_id) REFERENCES superhero(id),
    deleted BOOLEAN DEFAULT false
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

INSERT INTO superhero (alias, real_name, date_of_birth, gender, occupation, origin_story)
VALUES
('The Administrator', 'Alex Admin', '1987-07-22', 'Male', 'Administrator', 'Alex Admin gained superhuman administrative skills after being exposed to a mysterious computer virus.'),
('Captain Courageous', 'Chris Courage', '1980-05-15', 'Male', 'Defender of Justice', 'Chris Courage was a regular citizen until a fateful accident imbued him with superhuman courage.'),
('Speedster', 'Samantha Swift', '1992-08-21', 'Female', 'Delivery Driver', 'Samantha Swift gained the ability to move at incredible speeds after being struck by lightning.'),
('The Kindness Knight', 'Kevin Knight', '1975-02-10', 'Male', 'Social Worker', 'Kevin Knight was inspired to become a hero after witnessing acts of kindness in his community.'),
('Doctor Empathy', 'Emma Empath', '1988-11-03', 'Female', 'Psychologist', 'Emma Empath developed the power to sense and understand the emotions of others during a medical experiment.'),
('The Wise Owl', 'Oliver Wisdom', '1963-09-28', 'Male', 'Retired Teacher', 'Oliver Wisdom''s wisdom is legendary, and he uses his knowledge to guide and mentor other heroes.'),
('Creative Genius', 'Cassandra Creative', '1995-04-17', 'Female', 'Artist', 'Cassandra Creative''s imaginative mind allows her to come up with ingenious solutions to any problem.'),
('The Gentle Giant', 'Gary Gentle', '1978-07-09', 'Male', 'Construction Worker', 'Gary Gentle possesses superhuman strength, but he uses it with care and gentleness to help others.'),
('Lady Loyalty', 'Leah Loyal', '1986-12-30', 'Female', 'Veterinarian', 'Leah Loyal is known for her unwavering loyalty to her friends and the causes she believes in.'),
('The Peacekeeper', 'Pete Peace', '1970-03-25', 'Male', 'Mediator', 'Pete Peace has the ability to calm any situation and find peaceful resolutions to conflicts.'),
('The Generous Guardian', 'Grace Generous', '1990-06-12', 'Female', 'Philanthropist', 'Grace Generous uses her wealth and resources to help those in need and make the world a better place.'),
('The Courageous Cat', 'Carlos Courage', '1983-10-05', 'Male', 'Firefighter', 'Carlos Courage fearlessly rushes into danger to save lives, inspired by his love for his pet cat.'),
('The Swift Swimmer', 'Sarah Swift', '1998-02-18', 'Female', 'Lifeguard', 'Sarah Swift is not only a fast swimmer but also a quick thinker, always ready to dive into action to save others.'),
('The Kind-hearted Knight', 'Keith Kindheart', '1972-06-30', 'Male', 'Teacher', 'Keith Kindheart is beloved by his community for his compassionate nature and willingness to help others.'),
('The Empathetic Eagle', 'Ella Empathy', '1984-09-12', 'Female', 'Therapist', 'Ella Empathy can feel the pain of others and uses her powers to heal both physically and emotionally.'),
('The Wise Wizard', 'Walter Wisdom', '1960-11-20', 'Male', 'Librarian', 'Walter Wisdom''s vast knowledge of ancient texts and magical artifacts makes him a formidable ally in the fight against evil.');

-- Assigning random skills to superheroes
INSERT INTO skill_profile (superhero_id, skill_id, intensity)
SELECT id, skill_id, FLOOR(RANDOM() * 5) + 1 FROM superhero CROSS JOIN skill ORDER BY RANDOM() LIMIT 15;

INSERT INTO rescue_action (superhero_initiator_id, action, description)
VALUES
(1, 'Rescue at the Burning Building', 'Captain Courageous saves a group of trapped civilians from a burning building.'),
(2, 'High-Speed Chase', 'Speedster foils a bank robbery by apprehending the criminals in a high-speed chase.'),
(3, 'Kindness Campaign', 'The Kindness Knight spreads positivity and kindness throughout the city, inspiring others to do the same.'),
(4, 'Crisis Intervention', 'Doctor Empathy provides emotional support and counseling to survivors of a natural disaster.'),
(5, 'Wisdom Seminar', 'The Wise Owl hosts a seminar on wisdom and knowledge, teaching valuable life lessons to attendees.'),
(6, 'Artistic Rescue', 'Creative Genius uses her creative skills to design a solution to a city-wide problem.'),
(7, 'Gentle Giant''s Construction Effort', 'The Gentle Giant assists in the construction of a new community center, using his strength to lift heavy materials.'),
(8, 'Loyalty Test', 'Lady Loyalty stays true to her principles and refuses to betray her friends even under pressure.'),
(9, 'Peacekeeping Negotiation', 'The Peacekeeper mediates a tense standoff between rival gangs, preventing violence and finding a peaceful resolution.'),
(10, 'Generosity Gala', 'The Generous Guardian hosts a charity gala to raise funds for local charities and support those in need.'),
(11, 'Courageous Cat''s Rescue Mission', 'The Courageous Cat rescues a group of stranded hikers from a treacherous mountain trail.'),
(12, 'Swift Water Rescue', 'The Swift Swimmer saves a child from drowning in a fast-moving river.'),
(13, 'Kind-hearted Knight''s Volunteer Day', 'The Kind-hearted Knight organizes a volunteer day to clean up a local park and plant trees.'),
(14, 'Empathetic Eagle''s Therapy Session', 'The Empathetic Eagle provides therapy and emotional support to individuals struggling with mental health issues.'),
(15, 'Wise Wizard''s Library Restoration', 'The Wise Wizard uses his knowledge of ancient spells to restore a magical library to its former glory.');

INSERT INTO participation (rescue_action_id, superhero_id)
VALUES
(1, 2),
(2, 10),
(3, 4),
(4, 6),
(5, 7),
(6, 9),
(7, 11),
(8, 14),
(9, 3),
(10, 8),
(11, 1),
(12, 5),
(13, 12),
(14, 15),
(15, 13);
