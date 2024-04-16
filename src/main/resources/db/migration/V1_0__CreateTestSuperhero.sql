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
