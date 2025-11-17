-- Script de criação das tabelas para o sistema AGS Unissex
-- PostgreSQL Database Schema
-- Versão: 1.0
-- Data: 2025-11-06

-- =============================================
-- EXTENSÕES E CONFIGURAÇÕES
-- =============================================

-- Extensão para UUID (caso necessário no futuro)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- TABELA: clients
-- =============================================
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    address TEXT,
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    
    -- Campos específicos para salão de beleza
    hair_type VARCHAR(50), -- liso, ondulado, cacheado, crespo
    hair_texture VARCHAR(50), -- fino, médio, grosso
    scalp VARCHAR(50), -- oleoso, seco, misto, sensível
    allergies TEXT,
    last_visit TIMESTAMP,
    observations TEXT,
    
    -- Metadados
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- TABELA: employees
-- =============================================
CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    hire_date DATE NOT NULL DEFAULT CURRENT_DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    
    -- Campos específicos para cabeleireiros
    specialties TEXT, -- Especialidades (corte, coloração, tratamentos, etc.)
    experience_level VARCHAR(50) NOT NULL DEFAULT 'Iniciante', -- Iniciante, Intermediário, Avançado, Especialista
    base_salary DECIMAL(10,2),
    commission_rate DECIMAL(5,4) DEFAULT 0.30, -- Percentual de comissão (ex: 0.30 = 30%)
    working_hours VARCHAR(50), -- Horário de trabalho (ex: "08:00-18:00")
    working_days TEXT, -- JSON array dos dias da semana que trabalha
    position VARCHAR(100), -- Cargo (Cabeleireiro, Cabeleireiro Sênior, Supervisor, etc.)
    last_training_date TIMESTAMP,
    certificates TEXT,
    can_perform_chemical_treatments BOOLEAN NOT NULL DEFAULT FALSE,
    preferred_client_type VARCHAR(50), -- Masculino, Feminino, Infantil, Todos
    
    -- Metadados
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- TABELA: services
-- =============================================
CREATE TABLE IF NOT EXISTS services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    duration_minutes INTEGER NOT NULL, -- Duração em minutos
    category VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Metadados
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- TABELA: appointments
-- =============================================
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    stylist_id BIGINT,
    appointment_date_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AGENDADO',
    total_price DECIMAL(10,2),
    notes TEXT,
    
    -- Metadados
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints de chave estrangeira
    CONSTRAINT fk_appointments_client
        FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointments_stylist
        FOREIGN KEY (stylist_id) REFERENCES employees(id) ON DELETE SET NULL,
    
    -- Constraint para validar status
    CONSTRAINT chk_appointment_status
        CHECK (status IN ('AGENDADO', 'CONFIRMADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO', 'NAO_COMPARECEU'))
);

-- =============================================
-- TABELA: appointment_services (relacionamento N:N)
-- =============================================
CREATE TABLE IF NOT EXISTS appointment_services (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    service_price DECIMAL(10,2) NOT NULL, -- Preço do serviço na época do agendamento
    
    -- Constraints de chave estrangeira
    CONSTRAINT fk_appointment_services_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_services_service
        FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
    
    -- Evitar duplicatas
    CONSTRAINT uk_appointment_service UNIQUE (appointment_id, service_id)
);

-- =============================================
-- ÍNDICES PARA PERFORMANCE
-- =============================================

-- Índices para clients
CREATE INDEX IF NOT EXISTS idx_clients_name ON clients(name);
CREATE INDEX IF NOT EXISTS idx_clients_phone ON clients(phone_number);
CREATE INDEX IF NOT EXISTS idx_clients_email ON clients(email);
CREATE INDEX IF NOT EXISTS idx_clients_active ON clients(is_active);
CREATE INDEX IF NOT EXISTS idx_clients_registration_date ON clients(registration_date);

-- Índices para employees
CREATE INDEX IF NOT EXISTS idx_employees_name ON employees(name);
CREATE INDEX IF NOT EXISTS idx_employees_cpf ON employees(cpf);
CREATE INDEX IF NOT EXISTS idx_employees_active ON employees(is_active);
CREATE INDEX IF NOT EXISTS idx_employees_position ON employees(position);

-- Índices para services
CREATE INDEX IF NOT EXISTS idx_services_name ON services(name);
CREATE INDEX IF NOT EXISTS idx_services_category ON services(category);
CREATE INDEX IF NOT EXISTS idx_services_active ON services(is_active);

-- Índices para appointments
CREATE INDEX IF NOT EXISTS idx_appointments_client_id ON appointments(client_id);
CREATE INDEX IF NOT EXISTS idx_appointments_stylist_id ON appointments(stylist_id);
CREATE INDEX IF NOT EXISTS idx_appointments_date_time ON appointments(appointment_date_time);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments(status);
CREATE INDEX IF NOT EXISTS idx_appointments_date_status ON appointments(appointment_date_time, status);

-- =============================================
-- TRIGGERS PARA UPDATED_AT
-- =============================================

-- Função para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers para cada tabela
CREATE TRIGGER update_clients_updated_at 
    BEFORE UPDATE ON clients 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_employees_updated_at 
    BEFORE UPDATE ON employees 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_services_updated_at 
    BEFORE UPDATE ON services 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_appointments_updated_at 
    BEFORE UPDATE ON appointments 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================
-- COMENTÁRIOS DAS TABELAS
-- =============================================

COMMENT ON TABLE clients IS 'Tabela de clientes do salão de beleza';
COMMENT ON TABLE employees IS 'Tabela de funcionários/cabeleireiros';
COMMENT ON TABLE services IS 'Tabela de serviços oferecidos pelo salão';
COMMENT ON TABLE appointments IS 'Tabela de agendamentos';
COMMENT ON TABLE appointment_services IS 'Relacionamento N:N entre agendamentos e serviços';

-- Comentários das colunas importantes
COMMENT ON COLUMN clients.hair_type IS 'Tipo de cabelo: liso, ondulado, cacheado, crespo';
COMMENT ON COLUMN clients.scalp IS 'Tipo de couro cabeludo: oleoso, seco, misto, sensível';
COMMENT ON COLUMN employees.commission_rate IS 'Percentual de comissão (0.30 = 30%)';
COMMENT ON COLUMN employees.working_days IS 'JSON array com os dias da semana que trabalha';
COMMENT ON COLUMN services.duration_minutes IS 'Duração do serviço em minutos';
COMMENT ON COLUMN appointments.status IS 'Status: AGENDADO, CONFIRMADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO, NAO_COMPARECEU';