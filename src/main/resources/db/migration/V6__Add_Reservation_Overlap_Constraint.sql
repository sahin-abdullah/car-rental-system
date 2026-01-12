-- Enable btree_gist extension for date range exclusion constraints
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Add exclusion constraint to prevent overlapping reservations for the same car
-- This constraint ensures that no two active reservations (PENDING, CONFIRMED, ACTIVE) 
-- can have overlapping date ranges for the same car
-- Using '[)' range allows back-to-back reservations (one ends when next begins)
ALTER TABLE reservations 
ADD CONSTRAINT reservations_no_overlap_per_car 
EXCLUDE USING gist (
    car_id WITH =, 
    daterange(pickup_date, return_date, '[)') WITH &&
)
WHERE (status NOT IN ('CANCELLED', 'COMPLETED'));
