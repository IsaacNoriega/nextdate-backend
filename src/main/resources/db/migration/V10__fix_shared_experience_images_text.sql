ALTER TABLE shared_experience_images DROP CONSTRAINT IF EXISTS shared_experience_images_pkey;
ALTER TABLE shared_experience_images ALTER COLUMN image_url TYPE TEXT;
ALTER TABLE shared_experiences ALTER COLUMN itinerary_id DROP NOT NULL;
