CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE profiles(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    birthdate DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    bio TEXT,
    location GEOMETRY(Point, 4326),
    active BOOLEAN NOT NULL
);

CREATE INDEX idx_profiles_location ON profiles USING gist(location);

