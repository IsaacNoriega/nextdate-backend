CREATE TABLE places (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    price_range VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    location GEOMETRY(Point, 4326) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_places_location ON places USING gist(location);