INSERT INTO application_user (email, password, role, deleted) VALUES
('admin@example.com', '{bcrypt}$2a$12$anOoGoXF50pcBQMlUSDA..s7mJjQakVJ3dGXkQm3iRAiigRZWvts6', 0, false),
('user@example.com', '{bcrypt}$2a$12$4cAnXB4ziicHOuRiV4XVQOhCNr3w4N95Q07d3d1SNovRmLFfcPchS', 1, false),
('deleted@example.com', '{bcrypt}$2a$12$4cAnXB4ziicHOuRiV4XVQOhCNr3w4N95Q07d3d1SNovRmLFfcPchS', 1, true);

INSERT INTO skill (skill_name) VALUES
('foo'),
('bar'),
('baz');

INSERT INTO superhero (user_id, alias, real_name, date_of_birth, gender, occupation, origin_story, deleted) VALUES
(1, 'foo', 'Mr. Foo', '1970-01-01', 'Male', 'foo', 'foo', false),
(2, 'bar', 'Mrs. Bar', '1970-01-02', 'Female', 'bar', 'bar', true),
(3, 'baz', 'Mrs. baz', '1970-01-03', 'Male', 'baz', 'baz', true);

INSERT INTO skill_profile (superhero_id, skill_id, intensity) VALUES
(1, 2, 1),
(1, 1, 1),
(2, 1, 2);