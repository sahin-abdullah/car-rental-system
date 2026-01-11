CREATE EXTENSION IF NOT EXISTS postgis;

-- Create spatial index using GIST (Generalized Search Tree) with geometry type
CREATE INDEX IF NOT EXISTS idx_address_location 
    ON addresses 
    USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));

-- Create spatial index using GIST with geography type for distance calculations
CREATE INDEX IF NOT EXISTS idx_addresses_geog 
    ON addresses 
    USING GIST (geography(ST_MakePoint(longitude, latitude)));

