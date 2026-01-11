ALTER TABLE cars ALTER COLUMN version SET DEFAULT 0;
UPDATE cars SET version = 0 WHERE version IS NULL;

-- First insert addresses
INSERT INTO addresses (street1, street2, city, state, zip_code, country, latitude, longitude) VALUES
-- Boston Area
('1 Harborside Dr', NULL, 'Boston', 'MA', '02128', 'USA', 42.3656, -71.0096),
('125 High St', NULL, 'Boston', 'MA', '02110', 'USA', 42.3554, -71.0527),
('145 Dartmouth St', NULL, 'Boston', 'MA', '02116', 'USA', 42.3476, -71.0754),
('700 Atlantic Ave', NULL, 'Boston', 'MA', '02110', 'USA', 42.3519, -71.0552),

-- Cambridge Area
('77 Massachusetts Ave', NULL, 'Cambridge', 'MA', '02139', 'USA', 42.3601, -71.0942),
('1 Brattle St', NULL, 'Cambridge', 'MA', '02138', 'USA', 42.3736, -71.1190),

-- Worcester Area
('2 Washington Sq', NULL, 'Worcester', 'MA', '01604', 'USA', 42.2626, -71.7936),
('44 Front St', NULL, 'Worcester', 'MA', '01608', 'USA', 42.2626, -71.8023),

-- Springfield Area
('55 Frank B Murray St', NULL, 'Springfield', 'MA', '01103', 'USA', 42.1029, -72.5905),
('1391 Main St', NULL, 'Springfield', 'MA', '01103', 'USA', 42.1015, -72.5898),

-- North Shore
('65 Neptune Blvd', NULL, 'Lynn', 'MA', '01902', 'USA', 42.4668, -70.9493),
('252 Bridge St', NULL, 'Salem', 'MA', '01970', 'USA', 42.5195, -70.8967),

-- South Shore
('100 Burgin Pkwy', NULL, 'Quincy', 'MA', '02169', 'USA', 42.2334, -71.0068),
('50 Forbes Rd', NULL, 'Braintree', 'MA', '02184', 'USA', 42.2079, -71.0014),

-- Western MA
('75 North St', NULL, 'Pittsfield', 'MA', '01201', 'USA', 42.4501, -73.2453),

-- Central MA
('1 Worcester Rd', NULL, 'Framingham', 'MA', '01701', 'USA', 42.2793, -71.4162),

-- Cape Cod
('215 Iyannough Rd', NULL, 'Hyannis', 'MA', '02601', 'USA', 41.6526, -70.2830);

-- Then insert branches with address references
INSERT INTO branches (code, name, phone_number, address_id) VALUES
('BOS-LOGAN', 'Logan Airport', '617-555-0100', (SELECT id FROM addresses WHERE street1 = '1 Harborside Dr' AND city = 'Boston')),
('BOS-DTN', 'Boston Downtown', '617-555-0101', (SELECT id FROM addresses WHERE street1 = '125 High St' AND city = 'Boston')),
('BOS-BACK', 'Back Bay Station', '617-555-0102', (SELECT id FROM addresses WHERE street1 = '145 Dartmouth St')),
('BOS-SOUTH', 'South Station', '617-555-0103', (SELECT id FROM addresses WHERE street1 = '700 Atlantic Ave')),
('CAM-MIT', 'Cambridge MIT', '617-555-0200', (SELECT id FROM addresses WHERE street1 = '77 Massachusetts Ave')),
('CAM-SQUARE', 'Cambridge Harvard Square', '617-555-0201', (SELECT id FROM addresses WHERE street1 = '1 Brattle St')),
('WOR-UNION', 'Worcester Union Station', '508-555-0300', (SELECT id FROM addresses WHERE street1 = '2 Washington Sq')),
('WOR-DTN', 'Worcester Downtown', '508-555-0301', (SELECT id FROM addresses WHERE street1 = '44 Front St')),
('SPG-UNION', 'Springfield Union Station', '413-555-0400', (SELECT id FROM addresses WHERE street1 = '55 Frank B Murray St')),
('SPG-DTN', 'Springfield Downtown', '413-555-0401', (SELECT id FROM addresses WHERE street1 = '1391 Main St')),
('LYN-LYNN', 'Lynn Station', '781-555-0500', (SELECT id FROM addresses WHERE street1 = '65 Neptune Blvd')),
('SAL-SALEM', 'Salem Downtown', '978-555-0501', (SELECT id FROM addresses WHERE street1 = '252 Bridge St')),
('QUI-QUINCY', 'Quincy Adams Station', '617-555-0600', (SELECT id FROM addresses WHERE street1 = '100 Burgin Pkwy')),
('BRA-BRAIN', 'Braintree Station', '781-555-0601', (SELECT id FROM addresses WHERE street1 = '50 Forbes Rd')),
('PIT-PITTS', 'Pittsfield Downtown', '413-555-0700', (SELECT id FROM addresses WHERE street1 = '75 North St')),
('FRA-FRAML', 'Framingham Station', '508-555-0800', (SELECT id FROM addresses WHERE street1 = '1 Worcester Rd')),
('HYA-HYAN', 'Hyannis Transportation Center', '508-555-0900', (SELECT id FROM addresses WHERE street1 = '215 Iyannough Rd'));

-- Boston Logan Airport (BOS-LOGAN) - 15 cars
INSERT INTO cars (type, license_plate, make, model, year, current_branch_id, available) VALUES
('SEDAN', 'MA-S001', 'Toyota', 'Camry', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SEDAN', 'MA-S002', 'Honda', 'Accord', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SEDAN', 'MA-S003', 'Nissan', 'Altima', 2023, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SEDAN', 'MA-S004', 'Hyundai', 'Sonata', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SEDAN', 'MA-S005', 'Volkswagen', 'Jetta', 2023, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), false),
('SUV', 'MA-U001', 'Ford', 'Explorer', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SUV', 'MA-U002', 'Jeep', 'Grand Cherokee', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SUV', 'MA-U003', 'Toyota', 'RAV4', 2023, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SUV', 'MA-U004', 'Honda', 'CR-V', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SUV', 'MA-U005', 'Mazda', 'CX-5', 2023, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('VAN', 'MA-V001', 'Toyota', 'Sienna', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('VAN', 'MA-V002', 'Honda', 'Odyssey', 2023, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('VAN', 'MA-V003', 'Chrysler', 'Pacifica', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SEDAN', 'MA-S006', 'Tesla', 'Model 3', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),
('SUV', 'MA-U006', 'Tesla', 'Model Y', 2024, (SELECT id FROM branches WHERE code = 'BOS-LOGAN'), true),

-- Boston Downtown (BOS-DTN) - 10 cars
('SEDAN', 'MA-S101', 'BMW', '3 Series', 2024, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SEDAN', 'MA-S102', 'Audi', 'A4', 2023, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SEDAN', 'MA-S103', 'Mercedes', 'C-Class', 2024, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SEDAN', 'MA-S104', 'Lexus', 'ES 350', 2024, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SEDAN', 'MA-S105', 'Acura', 'TLX', 2023, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SUV', 'MA-U101', 'BMW', 'X5', 2024, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SUV', 'MA-U102', 'Audi', 'Q5', 2024, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SUV', 'MA-U103', 'Mercedes', 'GLE', 2023, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SUV', 'MA-U104', 'Lexus', 'RX 350', 2024, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),
('SEDAN', 'MA-S106', 'Tesla', 'Model S', 2024, (SELECT id FROM branches WHERE code = 'BOS-DTN'), true),

-- Back Bay Station (BOS-BACK) - 8 cars
('SEDAN', 'MA-S201', 'Toyota', 'Corolla', 2024, (SELECT id FROM branches WHERE code = 'BOS-BACK'), true),
('SEDAN', 'MA-S202', 'Honda', 'Civic', 2024, (SELECT id FROM branches WHERE code = 'BOS-BACK'), true),
('SEDAN', 'MA-S203', 'Mazda', '3', 2023, (SELECT id FROM branches WHERE code = 'BOS-BACK'), true),
('SEDAN', 'MA-S204', 'Subaru', 'Impreza', 2024, (SELECT id FROM branches WHERE code = 'BOS-BACK'), true),
('SUV', 'MA-U201', 'Subaru', 'Outback', 2024, (SELECT id FROM branches WHERE code = 'BOS-BACK'), true),
('SUV', 'MA-U202', 'Mazda', 'CX-30', 2023, (SELECT id FROM branches WHERE code = 'BOS-BACK'), true),
('SUV', 'MA-U203', 'Nissan', 'Rogue', 2024, (SELECT id FROM branches WHERE code = 'BOS-BACK'), true),
('VAN', 'MA-V201', 'Dodge', 'Grand Caravan', 2023, (SELECT id FROM branches WHERE code = 'BOS-BACK'), true),

-- South Station (BOS-SOUTH) - 6 cars
('SEDAN', 'MA-S301', 'Kia', 'K5', 2024, (SELECT id FROM branches WHERE code = 'BOS-SOUTH'), true),
('SEDAN', 'MA-S302', 'Hyundai', 'Elantra', 2023, (SELECT id FROM branches WHERE code = 'BOS-SOUTH'), true),
('SEDAN', 'MA-S303', 'Volkswagen', 'Passat', 2024, (SELECT id FROM branches WHERE code = 'BOS-SOUTH'), true),
('SUV', 'MA-U301', 'Kia', 'Sportage', 2024, (SELECT id FROM branches WHERE code = 'BOS-SOUTH'), true),
('SUV', 'MA-U302', 'Hyundai', 'Tucson', 2023, (SELECT id FROM branches WHERE code = 'BOS-SOUTH'), true),
('SUV', 'MA-U303', 'Volkswagen', 'Tiguan', 2024, (SELECT id FROM branches WHERE code = 'BOS-SOUTH'), true),

-- Cambridge MIT (CAM-MIT) - 7 cars
('SEDAN', 'MA-S401', 'Tesla', 'Model 3', 2024, (SELECT id FROM branches WHERE code = 'CAM-MIT'), true),
('SEDAN', 'MA-S402', 'Toyota', 'Prius', 2023, (SELECT id FROM branches WHERE code = 'CAM-MIT'), true),
('SEDAN', 'MA-S403', 'Honda', 'Insight', 2024, (SELECT id FROM branches WHERE code = 'CAM-MIT'), true),
('SUV', 'MA-U401', 'Tesla', 'Model Y', 2024, (SELECT id FROM branches WHERE code = 'CAM-MIT'), true),
('SUV', 'MA-U402', 'Chevrolet', 'Bolt EUV', 2023, (SELECT id FROM branches WHERE code = 'CAM-MIT'), true),
('SEDAN', 'MA-S404', 'Nissan', 'Leaf', 2024, (SELECT id FROM branches WHERE code = 'CAM-MIT'), true),
('SUV', 'MA-U403', 'Ford', 'Mustang Mach-E', 2024, (SELECT id FROM branches WHERE code = 'CAM-MIT'), true),

-- Cambridge Harvard Square (CAM-SQUARE) - 6 cars
('SEDAN', 'MA-S501', 'Audi', 'A3', 2024, (SELECT id FROM branches WHERE code = 'CAM-SQUARE'), true),
('SEDAN', 'MA-S502', 'BMW', '2 Series', 2023, (SELECT id FROM branches WHERE code = 'CAM-SQUARE'), true),
('SEDAN', 'MA-S503', 'Volvo', 'S60', 2024, (SELECT id FROM branches WHERE code = 'CAM-SQUARE'), true),
('SUV', 'MA-U501', 'Volvo', 'XC60', 2024, (SELECT id FROM branches WHERE code = 'CAM-SQUARE'), true),
('SUV', 'MA-U502', 'Audi', 'Q3', 2023, (SELECT id FROM branches WHERE code = 'CAM-SQUARE'), true),
('SUV', 'MA-U503', 'BMW', 'X3', 2024, (SELECT id FROM branches WHERE code = 'CAM-SQUARE'), true),

-- Worcester Union Station (WOR-UNION) - 8 cars
('SEDAN', 'MA-S601', 'Toyota', 'Camry', 2023, (SELECT id FROM branches WHERE code = 'WOR-UNION'), true),
('SEDAN', 'MA-S602', 'Honda', 'Accord', 2024, (SELECT id FROM branches WHERE code = 'WOR-UNION'), true),
('SEDAN', 'MA-S603', 'Nissan', 'Maxima', 2023, (SELECT id FROM branches WHERE code = 'WOR-UNION'), true),
('SUV', 'MA-U601', 'Ford', 'Escape', 2024, (SELECT id FROM branches WHERE code = 'WOR-UNION'), true),
('SUV', 'MA-U602', 'Chevrolet', 'Equinox', 2023, (SELECT id FROM branches WHERE code = 'WOR-UNION'), true),
('SUV', 'MA-U603', 'GMC', 'Terrain', 2024, (SELECT id FROM branches WHERE code = 'WOR-UNION'), true),
('VAN', 'MA-V601', 'Toyota', 'Sienna', 2023, (SELECT id FROM branches WHERE code = 'WOR-UNION'), true),
('VAN', 'MA-V602', 'Honda', 'Odyssey', 2024, (SELECT id FROM branches WHERE code = 'WOR-UNION'), true),

-- Worcester Downtown (WOR-DTN) - 5 cars
('SEDAN', 'MA-S701', 'Hyundai', 'Sonata', 2024, (SELECT id FROM branches WHERE code = 'WOR-DTN'), true),
('SEDAN', 'MA-S702', 'Kia', 'Optima', 2023, (SELECT id FROM branches WHERE code = 'WOR-DTN'), true),
('SUV', 'MA-U701', 'Hyundai', 'Santa Fe', 2024, (SELECT id FROM branches WHERE code = 'WOR-DTN'), true),
('SUV', 'MA-U702', 'Kia', 'Sorento', 2023, (SELECT id FROM branches WHERE code = 'WOR-DTN'), true),
('SUV', 'MA-U703', 'Mitsubishi', 'Outlander', 2024, (SELECT id FROM branches WHERE code = 'WOR-DTN'), true),

-- Springfield Union Station (SPG-UNION) - 7 cars
('SEDAN', 'MA-S801', 'Toyota', 'Avalon', 2023, (SELECT id FROM branches WHERE code = 'SPG-UNION'), true),
('SEDAN', 'MA-S802', 'Honda', 'Accord', 2024, (SELECT id FROM branches WHERE code = 'SPG-UNION'), true),
('SEDAN', 'MA-S803', 'Chevrolet', 'Malibu', 2023, (SELECT id FROM branches WHERE code = 'SPG-UNION'), true),
('SUV', 'MA-U801', 'Chevrolet', 'Traverse', 2024, (SELECT id FROM branches WHERE code = 'SPG-UNION'), true),
('SUV', 'MA-U802', 'Ford', 'Edge', 2023, (SELECT id FROM branches WHERE code = 'SPG-UNION'), true),
('SUV', 'MA-U803', 'Jeep', 'Wrangler', 2024, (SELECT id FROM branches WHERE code = 'SPG-UNION'), true),
('VAN', 'MA-V801', 'Chrysler', 'Voyager', 2023, (SELECT id FROM branches WHERE code = 'SPG-UNION'), true),

-- Springfield Downtown (SPG-DTN) - 5 cars
('SEDAN', 'MA-S901', 'Ford', 'Fusion', 2023, (SELECT id FROM branches WHERE code = 'SPG-DTN'), true),
('SEDAN', 'MA-S902', 'Chevrolet', 'Cruze', 2024, (SELECT id FROM branches WHERE code = 'SPG-DTN'), true),
('SUV', 'MA-U901', 'Ford', 'Explorer', 2024, (SELECT id FROM branches WHERE code = 'SPG-DTN'), true),
('SUV', 'MA-U902', 'Dodge', 'Durango', 2023, (SELECT id FROM branches WHERE code = 'SPG-DTN'), true),
('SUV', 'MA-U903', 'Jeep', 'Cherokee', 2024, (SELECT id FROM branches WHERE code = 'SPG-DTN'), true),

-- Lynn Station (LYN-LYNN) - 6 cars
('SEDAN', 'MA-S1001', 'Toyota', 'Corolla', 2024, (SELECT id FROM branches WHERE code = 'LYN-LYNN'), true),
('SEDAN', 'MA-S1002', 'Honda', 'Civic', 2023, (SELECT id FROM branches WHERE code = 'LYN-LYNN'), true),
('SEDAN', 'MA-S1003', 'Mazda', '3', 2024, (SELECT id FROM branches WHERE code = 'LYN-LYNN'), true),
('SUV', 'MA-U1001', 'Mazda', 'CX-5', 2024, (SELECT id FROM branches WHERE code = 'LYN-LYNN'), true),
('SUV', 'MA-U1002', 'Toyota', 'Highlander', 2023, (SELECT id FROM branches WHERE code = 'LYN-LYNN'), true),
('VAN', 'MA-V1001', 'Honda', 'Odyssey', 2024, (SELECT id FROM branches WHERE code = 'LYN-LYNN'), true),

-- Salem Downtown (SAL-SALEM) - 5 cars
('SEDAN', 'MA-S1101', 'Subaru', 'Legacy', 2024, (SELECT id FROM branches WHERE code = 'SAL-SALEM'), true),
('SEDAN', 'MA-S1102', 'Volkswagen', 'Jetta', 2023, (SELECT id FROM branches WHERE code = 'SAL-SALEM'), true),
('SUV', 'MA-U1101', 'Subaru', 'Forester', 2024, (SELECT id FROM branches WHERE code = 'SAL-SALEM'), true),
('SUV', 'MA-U1102', 'Volkswagen', 'Atlas', 2023, (SELECT id FROM branches WHERE code = 'SAL-SALEM'), true),
('SUV', 'MA-U1103', 'Nissan', 'Pathfinder', 2024, (SELECT id FROM branches WHERE code = 'SAL-SALEM'), true),

-- Quincy Adams Station (QUI-QUINCY) - 6 cars
('SEDAN', 'MA-S1201', 'Honda', 'Accord', 2024, (SELECT id FROM branches WHERE code = 'QUI-QUINCY'), true),
('SEDAN', 'MA-S1202', 'Toyota', 'Camry', 2023, (SELECT id FROM branches WHERE code = 'QUI-QUINCY'), true),
('SEDAN', 'MA-S1203', 'Nissan', 'Sentra', 2024, (SELECT id FROM branches WHERE code = 'QUI-QUINCY'), true),
('SUV', 'MA-U1201', 'Honda', 'Pilot', 2024, (SELECT id FROM branches WHERE code = 'QUI-QUINCY'), true),
('SUV', 'MA-U1202', 'Toyota', '4Runner', 2023, (SELECT id FROM branches WHERE code = 'QUI-QUINCY'), true),
('VAN', 'MA-V1201', 'Toyota', 'Sienna', 2024, (SELECT id FROM branches WHERE code = 'QUI-QUINCY'), true),

-- Braintree Station (BRA-BRAIN) - 5 cars
('SEDAN', 'MA-S1301', 'Kia', 'Forte', 2024, (SELECT id FROM branches WHERE code = 'BRA-BRAIN'), true),
('SEDAN', 'MA-S1302', 'Hyundai', 'Accent', 2023, (SELECT id FROM branches WHERE code = 'BRA-BRAIN'), true),
('SUV', 'MA-U1301', 'Kia', 'Telluride', 2024, (SELECT id FROM branches WHERE code = 'BRA-BRAIN'), true),
('SUV', 'MA-U1302', 'Hyundai', 'Palisade', 2023, (SELECT id FROM branches WHERE code = 'BRA-BRAIN'), true),
('SUV', 'MA-U1303', 'Genesis', 'GV70', 2024, (SELECT id FROM branches WHERE code = 'BRA-BRAIN'), true),

-- Pittsfield Downtown (PIT-PITTS) - 5 cars
('SEDAN', 'MA-S1401', 'Buick', 'Regal', 2023, (SELECT id FROM branches WHERE code = 'PIT-PITTS'), true),
('SEDAN', 'MA-S1402', 'Chrysler', '300', 2024, (SELECT id FROM branches WHERE code = 'PIT-PITTS'), true),
('SUV', 'MA-U1401', 'Buick', 'Enclave', 2024, (SELECT id FROM branches WHERE code = 'PIT-PITTS'), true),
('SUV', 'MA-U1402', 'GMC', 'Acadia', 2023, (SELECT id FROM branches WHERE code = 'PIT-PITTS'), true),
('SUV', 'MA-U1403', 'Cadillac', 'XT5', 2024, (SELECT id FROM branches WHERE code = 'PIT-PITTS'), true),

-- Framingham Station (FRA-FRAML) - 6 cars
('SEDAN', 'MA-S1501', 'Toyota', 'Camry Hybrid', 2024, (SELECT id FROM branches WHERE code = 'FRA-FRAML'), true),
('SEDAN', 'MA-S1502', 'Honda', 'Accord Hybrid', 2023, (SELECT id FROM branches WHERE code = 'FRA-FRAML'), true),
('SEDAN', 'MA-S1503', 'Hyundai', 'Ioniq', 2024, (SELECT id FROM branches WHERE code = 'FRA-FRAML'), true),
('SUV', 'MA-U1501', 'Toyota', 'Highlander Hybrid', 2024, (SELECT id FROM branches WHERE code = 'FRA-FRAML'), true),
('SUV', 'MA-U1502', 'Ford', 'Escape Hybrid', 2023, (SELECT id FROM branches WHERE code = 'FRA-FRAML'), true),
('VAN', 'MA-V1501', 'Chrysler', 'Pacifica Hybrid', 2024, (SELECT id FROM branches WHERE code = 'FRA-FRAML'), true),

-- Hyannis Transportation Center (HYA-HYAN) - 7 cars
('SEDAN', 'MA-S1601', 'Jeep', 'Compass', 2024, (SELECT id FROM branches WHERE code = 'HYA-HYAN'), true),
('SEDAN', 'MA-S1602', 'Ford', 'Focus', 2023, (SELECT id FROM branches WHERE code = 'HYA-HYAN'), true),
('SUV', 'MA-U1601', 'Jeep', 'Grand Cherokee', 2024, (SELECT id FROM branches WHERE code = 'HYA-HYAN'), true),
('SUV', 'MA-U1602', 'Ford', 'Bronco', 2024, (SELECT id FROM branches WHERE code = 'HYA-HYAN'), true),
('SUV', 'MA-U1603', 'Land Rover', 'Discovery Sport', 2023, (SELECT id FROM branches WHERE code = 'HYA-HYAN'), true),
('VAN', 'MA-V1601', 'Mercedes', 'Metris', 2024, (SELECT id FROM branches WHERE code = 'HYA-HYAN'), true),
('VAN', 'MA-V1602', 'Ford', 'Transit Connect', 2023, (SELECT id FROM branches WHERE code = 'HYA-HYAN'), true);
