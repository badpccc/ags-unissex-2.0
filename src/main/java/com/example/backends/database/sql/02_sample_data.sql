-- Script de inserção de dados de exemplo
-- AGS Unissex - Sistema de Gerenciamento de Salão
-- PostgreSQL Database - Sample Data
-- Versão: 1.0

-- =============================================
-- DADOS DE EXEMPLO - SERVIÇOS
-- =============================================

INSERT INTO services (name, description, price, duration_minutes, category) VALUES
-- Cortes
('Corte Masculino', 'Corte de cabelo masculino tradicional', 25.00, 30, 'Cortes'),
('Corte Feminino', 'Corte de cabelo feminino', 45.00, 60, 'Cortes'),
('Corte Infantil', 'Corte de cabelo para crianças até 12 anos', 20.00, 30, 'Cortes'),
('Corte + Barba', 'Corte masculino com acabamento de barba', 35.00, 45, 'Cortes'),

-- Coloração
('Coloração Completa', 'Coloração de todo o cabelo', 80.00, 120, 'Coloração'),
('Mechas', 'Mechas tradicionais', 120.00, 180, 'Coloração'),
('Luzes', 'Luzes no cabelo', 100.00, 150, 'Coloração'),
('Retoque de Raiz', 'Retoque da raiz do cabelo', 60.00, 90, 'Coloração'),

-- Tratamentos
('Hidratação', 'Tratamento hidratante para cabelos', 40.00, 45, 'Tratamentos'),
('Cauterização', 'Tratamento de cauterização capilar', 80.00, 90, 'Tratamentos'),
('Botox Capilar', 'Tratamento de botox para cabelos', 120.00, 120, 'Tratamentos'),
('Reconstrução', 'Reconstrução capilar profunda', 100.00, 90, 'Tratamentos'),

-- Penteados
('Escova Simples', 'Escova básica', 25.00, 30, 'Penteados'),
('Escova Modelada', 'Escova com modelagem', 35.00, 45, 'Penteados'),
('Penteado para Festa', 'Penteado elaborado para eventos', 80.00, 90, 'Penteados'),
('Penteado Noiva', 'Penteado especial para noivas', 150.00, 120, 'Penteados'),

-- Químicas
('Progressiva', 'Alisamento progressivo', 150.00, 180, 'Química'),
('Relaxamento', 'Relaxamento do cabelo', 120.00, 150, 'Química'),
('Permanente', 'Permanente para ondular cabelo', 100.00, 120, 'Química');

-- =============================================
-- DADOS DE EXEMPLO - FUNCIONÁRIOS
-- =============================================

INSERT INTO employees (name, email, phone_number, cpf, position, experience_level, specialties, base_salary, commission_rate, working_hours, working_days, can_perform_chemical_treatments, preferred_client_type) VALUES
('Maria Silva', 'maria.silva@agsunissex.com', '(11) 98765-4321', '123.456.789-01', 'Cabeleireira Sênior', 'Avançado', 'Cortes femininos, Coloração, Tratamentos', 2500.00, 0.35, '08:00-18:00', '["Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"]', true, 'Feminino'),

('João Santos', 'joao.santos@agsunissex.com', '(11) 98765-4322', '234.567.890-12', 'Barbeiro', 'Especialista', 'Cortes masculinos, Barba, Bigode', 2200.00, 0.30, '08:00-18:00', '["Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"]', false, 'Masculino'),

('Ana Costa', 'ana.costa@agsunissex.com', '(11) 98765-4323', '345.678.901-23', 'Colorista', 'Avançado', 'Coloração, Mechas, Luzes, Química', 2800.00, 0.40, '09:00-19:00', '["Terça", "Quarta", "Quinta", "Sexta", "Sábado"]', true, 'Todos'),

('Pedro Oliveira', 'pedro.oliveira@agsunissex.com', '(11) 98765-4324', '456.789.012-34', 'Cabeleireiro', 'Intermediário', 'Cortes masculinos e femininos, Escova', 1800.00, 0.25, '08:00-17:00', '["Segunda", "Terça", "Quarta", "Quinta", "Sexta"]', false, 'Todos'),

('Carla Mendes', 'carla.mendes@agsunissex.com', '(11) 98765-4325', '567.890.123-45', 'Supervisora', 'Especialista', 'Gestão, Penteados para eventos, Tratamentos', 3500.00, 0.20, '07:00-16:00', '["Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"]', true, 'Todos');

-- =============================================
-- DADOS DE EXEMPLO - CLIENTES
-- =============================================

INSERT INTO clients (name, email, phone_number, address, hair_type, hair_texture, scalp, observations) VALUES
('Julia Rodrigues', 'julia.rodrigues@email.com', '(11) 99111-2222', 'Rua das Flores, 123 - São Paulo/SP', 'cacheado', 'médio', 'oleoso', 'Prefere produtos naturais'),

('Carlos Pereira', 'carlos.pereira@email.com', '(11) 99333-4444', 'Av. Paulista, 456 - São Paulo/SP', 'liso', 'grosso', 'seco', 'Corta sempre muito baixo'),

('Fernanda Lima', 'fernanda.lima@email.com', '(11) 99555-6666', 'Rua do Comércio, 789 - São Paulo/SP', 'ondulado', 'fino', 'sensível', 'Alérgica a amônia'),

('Roberto Silva', 'roberto.silva@email.com', '(11) 99777-8888', 'Rua da Liberdade, 321 - São Paulo/SP', 'liso', 'médio', 'misto', 'Cliente há 5 anos'),

('Amanda Santos', 'amanda.santos@email.com', '(11) 99999-0000', 'Av. Brigadeiro, 654 - São Paulo/SP', 'crespo', 'grosso', 'seco', 'Gosta de penteados elaborados'),

('Marcos Costa', 'marcos.costa@email.com', '(11) 98888-1111', 'Rua Augusta, 987 - São Paulo/SP', 'liso', 'fino', 'oleoso', 'Vem sempre nas sextas'),

('Luciana Alves', 'luciana.alves@email.com', '(11) 97777-2222', 'Rua Oscar Freire, 159 - São Paulo/SP', 'ondulado', 'médio', 'seco', 'Executiva, horários flexíveis'),

('Thiago Ferreira', 'thiago.ferreira@email.com', '(11) 96666-3333', 'Rua Consolação, 753 - São Paulo/SP', 'cacheado', 'grosso', 'misto', 'Cabelo com química anterior');

-- =============================================
-- DADOS DE EXEMPLO - AGENDAMENTOS
-- =============================================

-- Agendamentos para hoje e próximos dias
INSERT INTO appointments (client_id, stylist_id, appointment_date_time, status, notes) VALUES
-- Hoje
(1, 1, CURRENT_TIMESTAMP + INTERVAL '2 hours', 'AGENDADO', 'Primeira vez com coloração'),
(2, 2, CURRENT_TIMESTAMP + INTERVAL '3 hours', 'CONFIRMADO', 'Cliente regular'),
(3, 3, CURRENT_TIMESTAMP + INTERVAL '4 hours', 'AGENDADO', 'Teste de alergia necessário'),

-- Amanhã
(4, 4, CURRENT_DATE + INTERVAL '1 day' + TIME '09:00', 'AGENDADO', ''),
(5, 5, CURRENT_DATE + INTERVAL '1 day' + TIME '10:30', 'AGENDADO', 'Penteado para casamento'),
(6, 2, CURRENT_DATE + INTERVAL '1 day' + TIME '14:00', 'AGENDADO', ''),
(7, 1, CURRENT_DATE + INTERVAL '1 day' + TIME '15:30', 'CONFIRMADO', 'Cliente VIP'),

-- Depois de amanhã
(8, 4, CURRENT_DATE + INTERVAL '2 days' + TIME '10:00', 'AGENDADO', ''),
(1, 3, CURRENT_DATE + INTERVAL '2 days' + TIME '16:00', 'AGENDADO', 'Retoque de coloração'),

-- Alguns agendamentos passados (para histórico)
(2, 2, CURRENT_DATE - INTERVAL '7 days' + TIME '10:00', 'CONCLUIDO', ''),
(3, 1, CURRENT_DATE - INTERVAL '14 days' + TIME '14:30', 'CONCLUIDO', ''),
(4, 4, CURRENT_DATE - INTERVAL '21 days' + TIME '09:00', 'CONCLUIDO', ''),
(5, 5, CURRENT_DATE - INTERVAL '30 days' + TIME '11:00', 'CONCLUIDO', ''),
(1, 2, CURRENT_DATE - INTERVAL '5 days' + TIME '15:00', 'CANCELADO', 'Cliente cancelou de última hora');

-- =============================================
-- RELACIONAMENTO AGENDAMENTOS x SERVIÇOS
-- =============================================

-- Serviços para os agendamentos criados acima
INSERT INTO appointment_services (appointment_id, service_id, service_price) VALUES
-- Agendamento 1: Julia - Coloração completa
(1, 5, 80.00),

-- Agendamento 2: Carlos - Corte + Barba
(2, 4, 35.00),

-- Agendamento 3: Fernanda - Mechas
(3, 6, 120.00),

-- Agendamento 4: Roberto - Corte masculino
(4, 1, 25.00),

-- Agendamento 5: Amanda - Penteado para festa + Escova
(5, 15, 80.00),
(5, 14, 35.00),

-- Agendamento 6: Marcos - Corte masculino
(6, 1, 25.00),

-- Agendamento 7: Luciana - Corte feminino + Hidratação
(7, 2, 45.00),
(7, 9, 40.00),

-- Agendamento 8: Thiago - Corte masculino
(8, 1, 25.00),

-- Agendamento 9: Julia - Retoque de raiz
(9, 8, 60.00),

-- Agendamentos históricos
(10, 1, 25.00), -- Roberto - Corte passado
(11, 2, 45.00), -- Fernanda - Corte passado
(12, 1, 25.00), -- Roberto - Corte passado
(13, 15, 80.00), -- Amanda - Penteado passado
(14, 1, 25.00); -- Julia - Corte cancelado

-- =============================================
-- ATUALIZAR PREÇOS TOTAIS DOS AGENDAMENTOS
-- =============================================

-- Atualizar o total_price baseado nos serviços
UPDATE appointments SET total_price = (
    SELECT COALESCE(SUM(service_price), 0)
    FROM appointment_services 
    WHERE appointment_services.appointment_id = appointments.id
);

-- =============================================
-- ATUALIZAR LAST_VISIT DOS CLIENTES
-- =============================================

-- Atualizar a última visita dos clientes baseado nos agendamentos concluídos
UPDATE clients SET last_visit = (
    SELECT MAX(appointment_date_time)
    FROM appointments 
    WHERE appointments.client_id = clients.id 
    AND appointments.status = 'CONCLUIDO'
);

-- =============================================
-- VERIFICAÇÕES E ESTATÍSTICAS
-- =============================================

-- Mostrar estatísticas após inserção
SELECT 'Clientes cadastrados: ' || COUNT(*) FROM clients;
SELECT 'Funcionários cadastrados: ' || COUNT(*) FROM employees;
SELECT 'Serviços cadastrados: ' || COUNT(*) FROM services;
SELECT 'Agendamentos criados: ' || COUNT(*) FROM appointments;
SELECT 'Relações agendamento-serviço: ' || COUNT(*) FROM appointment_services;