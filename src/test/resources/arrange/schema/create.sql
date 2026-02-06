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
    CONSTRAINT fk_superhero FOREIGN KEY (superhero_id) REFERENCES superhero(id),
    CONSTRAINT fk_skill FOREIGN KEY (skill_id) REFERENCES Skill(skill_id),
    CONSTRAINT unique_superhero_skill UNIQUE (superhero_id, skill_id)
);

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