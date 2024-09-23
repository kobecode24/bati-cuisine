-- Clients
INSERT INTO clients (name, address, phone, is_professional) VALUES
                                                                ('John Doe', '123 Main St, Anytown, AN 12345', '555-1234', false),
                                                                ('Jane Smith', '456 Oak Rd, Somewhere, SW 67890', '555-5678', false),
                                                                ('Acme Construction', '789 Industrial Pkwy, Bigcity, BC 13579', '555-9876', true),
                                                                ('Mary Johnson', '321 Elm St, Smallville, SV 24680', '555-4321', false),
                                                                ('Pro Builders Inc.', '654 Corporate Blvd, Megapolis, MP 97531', '555-8765', true);

-- Projects
INSERT INTO projects (project_name, client_id, project_status, profit_margin, total_cost, creation_date) VALUES
                                                                                                             ('Kitchen Remodel', 1, 'IN_PROGRESS', 15.00, 25000.00, '2024-09-01'),
                                                                                                             ('Bathroom Renovation', 2, 'COMPLETED', 12.50, 15000.00, '2024-08-15'),
                                                                                                             ('Office Kitchen', 3, 'IN_PROGRESS', 20.00, 50000.00, '2024-09-10'),
                                                                                                             ('Home Extension', 4, 'IN_PROGRESS', 18.00, 75000.00, '2024-09-05'),
                                                                                                             ('Restaurant Kitchen', 5, 'CANCELLED', 25.00, 100000.00, '2024-07-01');

-- Components (Materials)
INSERT INTO components (project_id, name, component_type, tax_rate) VALUES
                                                                        (1, 'Granite Countertop', 'MATERIAL', 10.00),
                                                                        (1, 'Kitchen Cabinets', 'MATERIAL', 10.00),
                                                                        (2, 'Ceramic Tiles', 'MATERIAL', 10.00),
                                                                        (3, 'Stainless Steel Appliances', 'MATERIAL', 10.00),
                                                                        (4, 'Lumber', 'MATERIAL', 10.00);

INSERT INTO materials (component_id, unit_cost, quantity, transport_cost, quality_coefficient) VALUES
                                                                                                   (1, 200.00, 30, 500.00, 1.2),
                                                                                                   (2, 500.00, 10, 1000.00, 1.1),
                                                                                                   (3, 15.00, 200, 300.00, 1.0),
                                                                                                   (4, 2000.00, 5, 1500.00, 1.3),
                                                                                                   (5, 10.00, 1000, 800.00, 1.0);

-- Components (Labor)
INSERT INTO components (project_id, name, component_type, tax_rate) VALUES
                                                                        (1, 'Installation Labor', 'LABOR', 5.00),
                                                                        (2, 'Plumbing Labor', 'LABOR', 5.00),
                                                                        (3, 'Electrical Work', 'LABOR', 5.00),
                                                                        (4, 'Carpentry Labor', 'LABOR', 5.00),
                                                                        (5, 'General Labor', 'LABOR', 5.00);

INSERT INTO labor (component_id, hourly_rate, hours_worked, worker_productivity) VALUES
                                                                                     (6, 50.00, 40, 1.0),
                                                                                     (7, 75.00, 20, 1.1),
                                                                                     (8, 80.00, 30, 1.2),
                                                                                     (9, 60.00, 100, 1.0),
                                                                                     (10, 45.00, 200, 0.9);

-- Estimates
INSERT INTO estimates (project_id, estimated_amount, issue_date, validity_date, accepted) VALUES
                                                                                              (1, 28750.00, '2024-08-25', '2024-10-25', true),
                                                                                              (2, 16875.00, '2024-08-01', '2024-10-01', true),
                                                                                              (3, 60000.00, '2024-09-05', '2024-11-05', false),
                                                                                              (4, 88500.00, '2024-08-30', '2024-10-30', true),
                                                                                              (5, 125000.00, '2024-06-15', '2024-08-15', false);