INSERT INTO rate_plans (branch_code, car_type, daily_rate, weekly_rate, weekend_multiplier, effective_from, effective_to, currency, description, active) VALUES
-- Boston Logan Airport (Premium pricing due to airport location)
('BOS-LOGAN', 'SEDAN', 75.00, 450.00, 1.20, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Logan Airport Sedan rates', true),
('BOS-LOGAN', 'SUV', 105.00, 630.00, 1.25, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Logan Airport SUV rates', true),
('BOS-LOGAN', 'VAN', 125.00, 750.00, 1.15, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Logan Airport Van rates', true),

-- Boston Downtown (Premium urban location)
('BOS-DTN', 'SEDAN', 80.00, 480.00, 1.22, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Boston Downtown Sedan rates', true),
('BOS-DTN', 'SUV', 110.00, 660.00, 1.28, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Boston Downtown SUV rates', true),
('BOS-DTN', 'VAN', 120.00, 720.00, 1.18, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Boston Downtown Van rates', true),

-- Back Bay Station (Premium urban)
('BOS-BACK', 'SEDAN', 78.00, 468.00, 1.21, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Back Bay Sedan rates', true),
('BOS-BACK', 'SUV', 108.00, 648.00, 1.26, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Back Bay SUV rates', true),
('BOS-BACK', 'VAN', 118.00, 708.00, 1.17, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Back Bay Van rates', true),

-- South Station (Urban)
('BOS-SOUTH', 'SEDAN', 75.00, 450.00, 1.20, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'South Station Sedan rates', true),
('BOS-SOUTH', 'SUV', 105.00, 630.00, 1.24, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'South Station SUV rates', true),
('BOS-SOUTH', 'VAN', 115.00, 690.00, 1.16, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'South Station Van rates', true),

-- Cambridge MIT (Premium academic area)
('CAM-MIT', 'SEDAN', 77.00, 462.00, 1.19, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Cambridge MIT Sedan rates', true),
('CAM-MIT', 'SUV', 107.00, 642.00, 1.24, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Cambridge MIT SUV rates', true),
('CAM-MIT', 'VAN', 117.00, 702.00, 1.15, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Cambridge MIT Van rates', true),

-- Cambridge Harvard Square (Premium)
('CAM-SQUARE', 'SEDAN', 79.00, 474.00, 1.22, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Harvard Square Sedan rates', true),
('CAM-SQUARE', 'SUV', 109.00, 654.00, 1.27, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Harvard Square SUV rates', true),
('CAM-SQUARE', 'VAN', 119.00, 714.00, 1.18, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Harvard Square Van rates', true),

-- Worcester Union Station (Standard rates)
('WOR-UNION', 'SEDAN', 55.00, 330.00, 1.15, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Worcester Union Sedan rates', true),
('WOR-UNION', 'SUV', 80.00, 480.00, 1.18, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Worcester Union SUV rates', true),
('WOR-UNION', 'VAN', 95.00, 570.00, 1.12, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Worcester Union Van rates', true),

-- Worcester Downtown
('WOR-DTN', 'SEDAN', 53.00, 318.00, 1.14, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Worcester Downtown Sedan rates', true),
('WOR-DTN', 'SUV', 78.00, 468.00, 1.17, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Worcester Downtown SUV rates', true),
('WOR-DTN', 'VAN', 93.00, 558.00, 1.11, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Worcester Downtown Van rates', true),

-- Springfield Union Station (Standard rates)
('SPG-UNION', 'SEDAN', 52.00, 312.00, 1.13, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Springfield Union Sedan rates', true),
('SPG-UNION', 'SUV', 77.00, 462.00, 1.16, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Springfield Union SUV rates', true),
('SPG-UNION', 'VAN', 92.00, 552.00, 1.10, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Springfield Union Van rates', true),

-- Springfield Downtown
('SPG-DTN', 'SEDAN', 50.00, 300.00, 1.12, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Springfield Downtown Sedan rates', true),
('SPG-DTN', 'SUV', 75.00, 450.00, 1.15, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Springfield Downtown SUV rates', true),
('SPG-DTN', 'VAN', 90.00, 540.00, 1.09, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Springfield Downtown Van rates', true),

-- Lynn Station (Moderate rates)
('LYN-LYNN', 'SEDAN', 58.00, 348.00, 1.16, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Lynn Station Sedan rates', true),
('LYN-LYNN', 'SUV', 83.00, 498.00, 1.19, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Lynn Station SUV rates', true),
('LYN-LYNN', 'VAN', 98.00, 588.00, 1.13, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Lynn Station Van rates', true),

-- Salem Downtown (Moderate rates)
('SAL-SALEM', 'SEDAN', 57.00, 342.00, 1.15, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Salem Downtown Sedan rates', true),
('SAL-SALEM', 'SUV', 82.00, 492.00, 1.18, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Salem Downtown SUV rates', true),
('SAL-SALEM', 'VAN', 97.00, 582.00, 1.12, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Salem Downtown Van rates', true),

-- Quincy Adams Station (Moderate rates)
('QUI-QUINCY', 'SEDAN', 60.00, 360.00, 1.17, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Quincy Station Sedan rates', true),
('QUI-QUINCY', 'SUV', 85.00, 510.00, 1.20, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Quincy Station SUV rates', true),
('QUI-QUINCY', 'VAN', 100.00, 600.00, 1.14, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Quincy Station Van rates', true),

-- Braintree Station (Moderate rates)
('BRA-BRAIN', 'SEDAN', 59.00, 354.00, 1.16, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Braintree Station Sedan rates', true),
('BRA-BRAIN', 'SUV', 84.00, 504.00, 1.19, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Braintree Station SUV rates', true),
('BRA-BRAIN', 'VAN', 99.00, 594.00, 1.13, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Braintree Station Van rates', true),

-- Pittsfield Downtown (Lower rates for Western MA)
('PIT-PITTS', 'SEDAN', 48.00, 288.00, 1.10, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Pittsfield Sedan rates', true),
('PIT-PITTS', 'SUV', 73.00, 438.00, 1.13, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Pittsfield SUV rates', true),
('PIT-PITTS', 'VAN', 88.00, 528.00, 1.08, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Pittsfield Van rates', true),

-- Framingham Station (Moderate rates)
('FRA-FRAML', 'SEDAN', 62.00, 372.00, 1.18, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Framingham Station Sedan rates', true),
('FRA-FRAML', 'SUV', 87.00, 522.00, 1.21, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Framingham Station SUV rates', true),
('FRA-FRAML', 'VAN', 102.00, 612.00, 1.15, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Framingham Station Van rates', true),

-- Hyannis Transportation Center (Seasonal premium for Cape Cod)
('HYA-HYAN', 'SEDAN', 68.00, 408.00, 1.30, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Hyannis Cape Cod Sedan rates', true),
('HYA-HYAN', 'SUV', 98.00, 588.00, 1.35, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Hyannis Cape Cod SUV rates', true),
('HYA-HYAN', 'VAN', 113.00, 678.00, 1.25, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year', 'USD', 'Hyannis Cape Cod Van rates', true);

-- Seed data for comprehensive pricing rules

INSERT INTO pricing_rules (rule_code, description, rule_type, percentage_value, fixed_amount, min_days, active) VALUES
-- Massachusetts State Tax
('MA_SALES_TAX', 'Massachusetts Sales Tax (6.25%)', 'TAX', 6.25, NULL, NULL, true),

-- Weekly and Long-term Discounts
('WEEKLY_DISCOUNT', 'Weekly rental discount (7+ days)', 'LENGTH_DISCOUNT', 12.00, NULL, 7, true),
('MONTHLY_DISCOUNT', 'Monthly rental discount (30+ days)', 'LENGTH_DISCOUNT', 18.00, NULL, 30, true),
('EXTENDED_DISCOUNT', 'Extended stay discount (60+ days)', 'LENGTH_DISCOUNT', 25.00, NULL, 60, true),

-- Airport Surcharges
('AIRPORT_FEE_LOGAN', 'Logan Airport facility fee', 'AIRPORT_FEE', NULL, 25.00, NULL, true),

-- Weekend Surcharge
('WEEKEND_SURCHARGE', 'Weekend rate increase', 'WEEKEND_SURCHARGE', 15.00, NULL, NULL, true),

-- One-way Rental Fees
('ONE_WAY_FEE', 'One-way rental fee (different return location)', 'ONE_WAY_FEE', NULL, 85.00, NULL, true),
('ONE_WAY_LONG', 'One-way long distance fee (100+ miles)', 'ONE_WAY_FEE', NULL, 150.00, NULL, true),

-- Additional Driver Fee
('ADDITIONAL_DRIVER', 'Additional driver fee (per rental)', 'ADDITIONAL_DRIVER_FEE', NULL, 15.00, NULL, true),

-- Insurance
('BASIC_INSURANCE', 'Basic insurance coverage (per day)', 'INSURANCE_RATE', NULL, 18.00, NULL, true),
('PREMIUM_INSURANCE', 'Premium insurance coverage (per day)', 'INSURANCE_RATE', NULL, 35.00, NULL, true);
