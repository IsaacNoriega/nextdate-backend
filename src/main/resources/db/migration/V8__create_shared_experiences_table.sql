CREATE TABLE shared_experiences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    itinerary_id UUID NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    tips TEXT,
    actual_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shared_experiences_itinerary FOREIGN KEY (itinerary_id) REFERENCES itineraries(id) ON DELETE CASCADE
);

CREATE TABLE shared_experience_images (
    shared_experience_id UUID NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    CONSTRAINT fk_shared_experience_images_parent FOREIGN KEY (shared_experience_id) REFERENCES shared_experiences(id) ON DELETE CASCADE,
    PRIMARY KEY (shared_experience_id, image_url)
);

CREATE INDEX idx_shared_experiences_user ON shared_experiences(user_id);
CREATE INDEX idx_shared_experiences_itinerary ON shared_experiences(itinerary_id);
