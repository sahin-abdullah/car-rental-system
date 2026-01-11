ALTER TABLE reservations ALTER COLUMN version SET DEFAULT 0;
UPDATE reservations SET version = 0 WHERE version IS NULL;

INSERT INTO reservations (
    customer_name, 
    customer_email, 
    customer_phone, 
    car_id, 
    pickup_branch_code, 
    return_branch_code, 
    pickup_date, 
    return_date, 
    status, 
    total_price,
    created_at,
    updated_at
) VALUES

-- Completed Reservations (Past)
(
    'John Smith',
    'john.smith@email.com',
    '617-555-1001',
    (SELECT id FROM cars WHERE license_plate = 'MA-S001' LIMIT 1),
    'BOS-LOGAN',
    'BOS-LOGAN',
    CURRENT_DATE - INTERVAL '30 days',
    CURRENT_DATE - INTERVAL '25 days',
    'COMPLETED',
    412.50,
    CURRENT_DATE - INTERVAL '35 days',
    CURRENT_DATE - INTERVAL '25 days'
),
(
    'Sarah Johnson',
    'sarah.j@email.com',
    '617-555-1002',
    (SELECT id FROM cars WHERE license_plate = 'MA-U101' LIMIT 1),
    'BOS-DTN',
    'BOS-DTN',
    CURRENT_DATE - INTERVAL '20 days',
    CURRENT_DATE - INTERVAL '17 days',
    'COMPLETED',
    352.80,
    CURRENT_DATE - INTERVAL '28 days',
    CURRENT_DATE - INTERVAL '17 days'
),
(
    'Michael Brown',
    'mbrown@email.com',
    '508-555-2001',
    (SELECT id FROM cars WHERE license_plate = 'MA-S601' LIMIT 1),
    'WOR-UNION',
    'WOR-UNION',
    CURRENT_DATE - INTERVAL '15 days',
    CURRENT_DATE - INTERVAL '8 days',
    'COMPLETED',
    425.25,
    CURRENT_DATE - INTERVAL '22 days',
    CURRENT_DATE - INTERVAL '8 days'
),
(
    'Emily Davis',
    'emily.davis@email.com',
    '413-555-3001',
    (SELECT id FROM cars WHERE license_plate = 'MA-V801' LIMIT 1),
    'SPG-UNION',
    'SPG-UNION',
    CURRENT_DATE - INTERVAL '12 days',
    CURRENT_DATE - INTERVAL '10 days',
    'COMPLETED',
    228.40,
    CURRENT_DATE - INTERVAL '20 days',
    CURRENT_DATE - INTERVAL '10 days'
),
(
    'David Wilson',
    'dwilson@email.com',
    '617-555-1003',
    (SELECT id FROM cars WHERE license_plate = 'MA-S401' LIMIT 1),
    'CAM-MIT',
    'BOS-LOGAN',
    CURRENT_DATE - INTERVAL '10 days',
    CURRENT_DATE - INTERVAL '7 days',
    'COMPLETED',
    318.75,
    CURRENT_DATE - INTERVAL '18 days',
    CURRENT_DATE - INTERVAL '7 days'
),

-- Active Reservations (Currently in use)
(
    'Jennifer Martinez',
    'jmartinez@email.com',
    '781-555-4001',
    (SELECT id FROM cars WHERE license_plate = 'MA-S1001' LIMIT 1),
    'LYN-LYNN',
    'LYN-LYNN',
    CURRENT_DATE - INTERVAL '2 days',
    CURRENT_DATE + INTERVAL '3 days',
    'ACTIVE',
    348.00,
    CURRENT_DATE - INTERVAL '10 days',
    CURRENT_DATE - INTERVAL '2 days'
),
(
    'Robert Taylor',
    'rtaylor@email.com',
    '508-555-5001',
    (SELECT id FROM cars WHERE license_plate = 'MA-U1601' LIMIT 1),
    'HYA-HYAN',
    'HYA-HYAN',
    CURRENT_DATE - INTERVAL '1 day',
    CURRENT_DATE + INTERVAL '6 days',
    'ACTIVE',
    784.00,
    CURRENT_DATE - INTERVAL '15 days',
    CURRENT_DATE - INTERVAL '1 day'
),
(
    'Amanda Anderson',
    'aanderson@email.com',
    '617-555-1004',
    (SELECT id FROM cars WHERE license_plate = 'MA-U003' LIMIT 1),
    'BOS-LOGAN',
    'BOS-LOGAN',
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '4 days',
    'ACTIVE',
    462.00,
    CURRENT_DATE - INTERVAL '7 days',
    CURRENT_DATE
),

-- Confirmed Reservations (Future - Customers have confirmed but not picked up)
(
    'Christopher Lee',
    'clee@email.com',
    '617-555-1005',
    (SELECT id FROM cars WHERE license_plate = 'MA-S101' LIMIT 1),
    'BOS-DTN',
    'CAM-SQUARE',
    CURRENT_DATE + INTERVAL '2 days',
    CURRENT_DATE + INTERVAL '5 days',
    'CONFIRMED',
    332.40,
    CURRENT_DATE - INTERVAL '5 days',
    CURRENT_DATE - INTERVAL '4 days'
),
(
    'Jessica White',
    'jwhite@email.com',
    '508-555-6001',
    (SELECT id FROM cars WHERE license_plate = 'MA-V601' LIMIT 1),
    'WOR-UNION',
    'BOS-LOGAN',
    CURRENT_DATE + INTERVAL '3 days',
    CURRENT_DATE + INTERVAL '10 days',
    'CONFIRMED',
    658.50,
    CURRENT_DATE - INTERVAL '3 days',
    CURRENT_DATE - INTERVAL '2 days'
),
(
    'Matthew Harris',
    'mharris@email.com',
    '617-555-1006',
    (SELECT id FROM cars WHERE license_plate = 'MA-S502' LIMIT 1),
    'CAM-SQUARE',
    'CAM-SQUARE',
    CURRENT_DATE + INTERVAL '5 days',
    CURRENT_DATE + INTERVAL '8 days',
    'CONFIRMED',
    268.50,
    CURRENT_DATE - INTERVAL '2 days',
    CURRENT_DATE - INTERVAL '1 day'
),
(
    'Ashley Clark',
    'aclark@email.com',
    '978-555-7001',
    (SELECT id FROM cars WHERE license_plate = 'MA-U1101' LIMIT 1),
    'SAL-SALEM',
    'SAL-SALEM',
    CURRENT_DATE + INTERVAL '7 days',
    CURRENT_DATE + INTERVAL '14 days',
    'CONFIRMED',
    656.40,
    CURRENT_DATE - INTERVAL '1 day',
    CURRENT_DATE
),

-- Pending Reservations (Awaiting confirmation/payment)
(
    'Daniel Rodriguez',
    'drodriguez@email.com',
    '617-555-1007',
    (SELECT id FROM cars WHERE license_plate = 'MA-S201' LIMIT 1),
    'BOS-BACK',
    'BOS-BACK',
    CURRENT_DATE + INTERVAL '4 days',
    CURRENT_DATE + INTERVAL '6 days',
    'PENDING',
    187.20,
    CURRENT_DATE,
    CURRENT_DATE
),
(
    'Lisa Thompson',
    'lthompson@email.com',
    '413-555-3002',
    (SELECT id FROM cars WHERE license_plate = 'MA-U701' LIMIT 1),
    'WOR-DTN',
    'WOR-DTN',
    CURRENT_DATE + INTERVAL '8 days',
    CURRENT_DATE + INTERVAL '15 days',
    'PENDING',
    624.00,
    CURRENT_DATE,
    CURRENT_DATE
),
(
    'Kevin Garcia',
    'kgarcia@email.com',
    '617-555-1008',
    (SELECT id FROM cars WHERE license_plate = 'MA-S1501' LIMIT 1),
    'FRA-FRAML',
    'BOS-DTN',
    CURRENT_DATE + INTERVAL '10 days',
    CURRENT_DATE + INTERVAL '12 days',
    'PENDING',
    213.60,
    CURRENT_DATE,
    CURRENT_DATE
),

-- Cancelled Reservations (Past cancellations)
(
    'Nancy Martinez',
    'nmartinez@email.com',
    '781-555-4002',
    (SELECT id FROM cars WHERE license_plate = 'MA-S1201' LIMIT 1),
    'QUI-QUINCY',
    'QUI-QUINCY',
    CURRENT_DATE + INTERVAL '3 days',
    CURRENT_DATE + INTERVAL '7 days',
    'CANCELLED',
    288.00,
    CURRENT_DATE - INTERVAL '6 days',
    CURRENT_DATE - INTERVAL '1 day'
),
(
    'Paul Jackson',
    'pjackson@email.com',
    '413-555-8001',
    (SELECT id FROM cars WHERE license_plate = 'MA-U1401' LIMIT 1),
    'PIT-PITTS',
    'PIT-PITTS',
    CURRENT_DATE + INTERVAL '5 days',
    CURRENT_DATE + INTERVAL '9 days',
    'CANCELLED',
    345.60,
    CURRENT_DATE - INTERVAL '4 days',
    CURRENT_DATE - INTERVAL '2 days'
),

-- Long-term Rental (Active)
(
    'Corporate Account - TechStart Inc',
    'fleet@techstart.com',
    '617-555-9001',
    (SELECT id FROM cars WHERE license_plate = 'MA-S004' LIMIT 1),
    'BOS-LOGAN',
    'BOS-LOGAN',
    CURRENT_DATE - INTERVAL '5 days',
    CURRENT_DATE + INTERVAL '25 days',
    'ACTIVE',
    1980.00,
    CURRENT_DATE - INTERVAL '20 days',
    CURRENT_DATE - INTERVAL '5 days'
),

-- Weekend Rental (Confirmed for upcoming weekend)
(
    'Mark Robinson',
    'mrobinson@email.com',
    '617-555-1009',
    (SELECT id FROM cars WHERE license_plate = 'MA-U202' LIMIT 1),
    'BOS-BACK',
    'BOS-BACK',
    CURRENT_DATE + INTERVAL '6 days',
    CURRENT_DATE + INTERVAL '8 days',
    'CONFIRMED',
    268.80,
    CURRENT_DATE - INTERVAL '3 days',
    CURRENT_DATE - INTERVAL '2 days'
),

-- One-way Rental (Confirmed)
(
    'Sandra King',
    'sking@email.com',
    '508-555-6002',
    (SELECT id FROM cars WHERE license_plate = 'MA-S602' LIMIT 1),
    'WOR-UNION',
    'BOS-LOGAN',
    CURRENT_DATE + INTERVAL '12 days',
    CURRENT_DATE + INTERVAL '14 days',
    'CONFIRMED',
    245.00,
    CURRENT_DATE - INTERVAL '1 day',
    CURRENT_DATE
),

-- Cape Cod Summer Rental (Future)
(
    'Brian Wright',
    'bwright@email.com',
    '508-555-6003',
    (SELECT id FROM cars WHERE license_plate = 'MA-V1602' LIMIT 1),
    'HYA-HYAN',
    'HYA-HYAN',
    CURRENT_DATE + INTERVAL '15 days',
    CURRENT_DATE + INTERVAL '22 days',
    'CONFIRMED',
    945.00,
    CURRENT_DATE - INTERVAL '2 days',
    CURRENT_DATE - INTERVAL '1 day'
);

-- Update car availability for cars currently in active reservations
UPDATE cars SET available = false WHERE license_plate IN (
    'MA-S1001',  -- Jennifer Martinez active rental
    'MA-U1601',  -- Robert Taylor active rental
    'MA-U003',   -- Amanda Anderson active rental
    'MA-S004'    -- Corporate long-term rental
);
