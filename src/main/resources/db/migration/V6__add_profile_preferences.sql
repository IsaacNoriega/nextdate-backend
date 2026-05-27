ALTER TABLE profiles
ADD COLUMN dietary_preference VARCHAR(30) NOT NULL DEFAULT 'NONE',
ADD COLUMN preferred_price_range VARCHAR(20) NOT NULL DEFAULT 'MODERATE';

CREATE TABLE profile_interests(
    profile_id UUID NOT NULL,
    interest VARCHAR(50) NOT NULL,
    CONSTRAINT fk_profile_interests_profile_id FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, interest)
);