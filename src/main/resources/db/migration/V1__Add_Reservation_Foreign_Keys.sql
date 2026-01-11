-- Foreign key: reservations.car_id -> cars.id
-- Prevents reserving non-existent cars
-- RESTRICT: Cannot delete a car that has reservations
ALTER TABLE reservations 
    DROP CONSTRAINT IF EXISTS fk_reservation_car;

ALTER TABLE reservations 
    ADD CONSTRAINT fk_reservation_car 
    FOREIGN KEY (car_id) 
    REFERENCES cars(id) 
    ON DELETE RESTRICT;

-- Foreign key: reservations.pickup_branch_code -> branches.code
-- Ensures valid pickup locations
-- RESTRICT: Cannot delete a branch that has reservations
ALTER TABLE reservations 
    DROP CONSTRAINT IF EXISTS fk_reservation_pickup_branch;

ALTER TABLE reservations 
    ADD CONSTRAINT fk_reservation_pickup_branch 
    FOREIGN KEY (pickup_branch_code) 
    REFERENCES branches(code) 
    ON DELETE RESTRICT;

-- Foreign key: reservations.return_branch_code -> branches.code
-- Ensures valid return locations
-- RESTRICT: Cannot delete a branch that has reservations
ALTER TABLE reservations 
    DROP CONSTRAINT IF EXISTS fk_reservation_return_branch;

ALTER TABLE reservations 
    ADD CONSTRAINT fk_reservation_return_branch 
    FOREIGN KEY (return_branch_code) 
    REFERENCES branches(code) 
    ON DELETE RESTRICT;

-- Foreign key: rate_plans.branch_code -> branches.code
-- Ensures rate plans are associated with valid branches
-- CASCADE: When a branch is deleted, its rate plans are also deleted
ALTER TABLE rate_plans 
    DROP CONSTRAINT IF EXISTS fk_rate_plan_branch;

ALTER TABLE rate_plans 
    ADD CONSTRAINT fk_rate_plan_branch 
    FOREIGN KEY (branch_code) 
    REFERENCES branches(code) 
    ON DELETE CASCADE;
