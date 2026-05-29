CREATE TABLE itineraries(
  id UUID PRIMARY KEY,
  user_id UUID  NOT NULL,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  total_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE itinerary_items (
    id UUID PRIMARY KEY,
    itinerary_id UUID NOT NULL,
    place_id UUID NOT NULL,
    sequence_order INT NOT NULL,
    duration_in_minutes INT NOT NULL DEFAULT 60,
    notes TEXT,
    transport_to_next VARCHAR(20) NOT NULL DEFAULT 'NONE',
    transit_time_to_next INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_itinerary_items_itinerary FOREIGN KEY (itinerary_id) REFERENCES itineraries(id) ON DELETE CASCADE,
    CONSTRAINT fk_itinerary_items_place FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE RESTRICT
);

CREATE INDEX idx_itineraries_user ON itineraries(user_id);
CREATE INDEX idx_itinerary_items_itinerary ON itinerary_items(itinerary_id, sequence_order);
