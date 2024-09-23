-- Create tables

CREATE TABLE clients (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         address VARCHAR(255) NOT NULL,
                         phone VARCHAR(20) NOT NULL,
                         is_professional BOOLEAN NOT NULL
);

CREATE TABLE projects (
                          id SERIAL PRIMARY KEY,
                          project_name VARCHAR(100) NOT NULL,
                          client_id INTEGER REFERENCES clients(id),
                          project_status VARCHAR(20) NOT NULL,
                          profit_margin DECIMAL(5,2) NOT NULL,
                          total_cost DECIMAL(10,2) NOT NULL,
                          creation_date DATE NOT NULL
);

CREATE TABLE components (
                            id SERIAL PRIMARY KEY,
                            project_id INTEGER REFERENCES projects(id),
                            name VARCHAR(100) NOT NULL,
                            component_type VARCHAR(20) NOT NULL,
                            tax_rate DECIMAL(5,2) NOT NULL
);

CREATE TABLE materials (
                           component_id INTEGER PRIMARY KEY REFERENCES components(id),
                           unit_cost DECIMAL(10,2) NOT NULL,
                           quantity DECIMAL(10,2) NOT NULL,
                           transport_cost DECIMAL(10,2) NOT NULL,
                           quality_coefficient DECIMAL(3,2) NOT NULL
);

CREATE TABLE labor (
                       component_id INTEGER PRIMARY KEY REFERENCES components(id),
                       hourly_rate DECIMAL(10,2) NOT NULL,
                       hours_worked DECIMAL(10,2) NOT NULL,
                       worker_productivity DECIMAL(3,2) NOT NULL
);

CREATE TABLE estimates (
                           id SERIAL PRIMARY KEY,
                           project_id INTEGER REFERENCES projects(id),
                           estimated_amount DECIMAL(10,2) NOT NULL,
                           issue_date DATE NOT NULL,
                           validity_date DATE NOT NULL,
                           accepted BOOLEAN NOT NULL
);

