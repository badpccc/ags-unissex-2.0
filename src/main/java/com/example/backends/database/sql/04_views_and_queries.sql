-- Views e consultas úteis para o sistema AGS Unissex
-- PostgreSQL Database - Views and Common Queries
-- Versão: 1.0

-- =============================================
-- VIEWS ÚTEIS
-- =============================================

-- View: Agendamentos completos com informações de cliente, funcionário e serviços
CREATE OR REPLACE VIEW view_appointments_full AS
SELECT 
    a.id as appointment_id,
    a.appointment_date_time,
    a.status,
    a.total_price,
    a.notes as appointment_notes,
    
    -- Informações do cliente
    c.id as client_id,
    c.name as client_name,
    c.phone_number as client_phone,
    c.email as client_email,
    
    -- Informações do funcionário
    e.id as employee_id,
    e.name as employee_name,
    e.position as employee_position,
    
    -- Serviços (concatenados)
    STRING_AGG(s.name, ', ') as services,
    COUNT(aps.service_id) as service_count,
    
    a.created_at,
    a.updated_at
FROM appointments a
JOIN clients c ON a.client_id = c.id
LEFT JOIN employees e ON a.stylist_id = e.id
LEFT JOIN appointment_services aps ON a.id = aps.appointment_id
LEFT JOIN services s ON aps.service_id = s.id
GROUP BY 
    a.id, a.appointment_date_time, a.status, a.total_price, a.notes,
    c.id, c.name, c.phone_number, c.email,
    e.id, e.name, e.position,
    a.created_at, a.updated_at;

-- View: Agenda do dia
CREATE OR REPLACE VIEW view_today_schedule AS
SELECT 
    a.id,
    a.appointment_date_time,
    c.name as client_name,
    c.phone_number as client_phone,
    e.name as employee_name,
    a.status,
    a.total_price,
    STRING_AGG(s.name, ', ') as services
FROM appointments a
JOIN clients c ON a.client_id = c.id
LEFT JOIN employees e ON a.stylist_id = e.id
LEFT JOIN appointment_services aps ON a.id = aps.appointment_id
LEFT JOIN services s ON aps.service_id = s.id
WHERE DATE(a.appointment_date_time) = CURRENT_DATE
AND a.status NOT IN ('CANCELADO')
GROUP BY a.id, a.appointment_date_time, c.name, c.phone_number, e.name, a.status, a.total_price
ORDER BY a.appointment_date_time;

-- View: Clientes com histórico
CREATE OR REPLACE VIEW view_clients_with_history AS
SELECT 
    c.*,
    COUNT(a.id) as total_appointments,
    COUNT(CASE WHEN a.status = 'CONCLUIDO' THEN 1 END) as completed_appointments,
    MAX(a.appointment_date_time) as last_appointment,
    SUM(CASE WHEN a.status = 'CONCLUIDO' THEN a.total_price ELSE 0 END) as total_spent
FROM clients c
LEFT JOIN appointments a ON c.id = a.client_id
GROUP BY c.id;

-- View: Funcionários com estatísticas
CREATE OR REPLACE VIEW view_employees_stats AS
SELECT 
    e.*,
    COUNT(a.id) as total_appointments,
    COUNT(CASE WHEN a.status = 'CONCLUIDO' THEN 1 END) as completed_appointments,
    SUM(CASE WHEN a.status = 'CONCLUIDO' THEN a.total_price ELSE 0 END) as total_revenue,
    AVG(CASE WHEN a.status = 'CONCLUIDO' THEN a.total_price END) as avg_ticket
FROM employees e
LEFT JOIN appointments a ON e.id = a.stylist_id
GROUP BY e.id;

-- View: Serviços mais populares
CREATE OR REPLACE VIEW view_popular_services AS
SELECT 
    s.*,
    COUNT(aps.appointment_id) as times_booked,
    SUM(aps.service_price) as total_revenue
FROM services s
LEFT JOIN appointment_services aps ON s.id = aps.service_id
LEFT JOIN appointments a ON aps.appointment_id = a.id
WHERE a.status = 'CONCLUIDO' OR a.status IS NULL
GROUP BY s.id
ORDER BY times_booked DESC;

-- =============================================
-- FUNÇÕES ÚTEIS
-- =============================================

-- Função para calcular idade de um cliente (se tivéssemos data de nascimento)
-- CREATE OR REPLACE FUNCTION calculate_age(birth_date DATE)
-- RETURNS INTEGER AS $$
-- BEGIN
--     RETURN EXTRACT(YEAR FROM AGE(birth_date));
-- END;
-- $$ LANGUAGE plpgsql;

-- Função para verificar disponibilidade de horário
CREATE OR REPLACE FUNCTION check_time_availability(
    stylist_id_param BIGINT,
    appointment_datetime_param TIMESTAMP,
    duration_minutes_param INTEGER DEFAULT 60
)
RETURNS BOOLEAN AS $$
DECLARE
    conflict_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO conflict_count
    FROM appointments
    WHERE stylist_id = stylist_id_param
    AND status NOT IN ('CANCELADO', 'CONCLUIDO')
    AND (
        -- Novo agendamento inicia durante um existente
        appointment_datetime_param BETWEEN appointment_date_time 
        AND appointment_date_time + INTERVAL '1 minute' * 60
        OR
        -- Novo agendamento termina durante um existente
        appointment_datetime_param + INTERVAL '1 minute' * duration_minutes_param 
        BETWEEN appointment_date_time 
        AND appointment_date_time + INTERVAL '1 minute' * 60
        OR
        -- Novo agendamento engloba um existente
        (appointment_datetime_param <= appointment_date_time 
         AND appointment_datetime_param + INTERVAL '1 minute' * duration_minutes_param 
         >= appointment_date_time + INTERVAL '1 minute' * 60)
    );
    
    RETURN conflict_count = 0;
END;
$$ LANGUAGE plpgsql;

-- =============================================
-- CONSULTAS ÚTEIS PARA RELATÓRIOS
-- =============================================

-- 1. Agendamentos de hoje
-- SELECT * FROM view_today_schedule;

-- 2. Top 5 clientes que mais gastaram
-- SELECT 
--     client_name, 
--     total_spent, 
--     total_appointments 
-- FROM view_clients_with_history 
-- WHERE total_spent > 0 
-- ORDER BY total_spent DESC 
-- LIMIT 5;

-- 3. Funcionário com mais agendamentos no mês atual
-- SELECT 
--     employee_name, 
--     COUNT(*) as appointments_this_month
-- FROM view_appointments_full 
-- WHERE EXTRACT(MONTH FROM appointment_date_time) = EXTRACT(MONTH FROM CURRENT_DATE)
-- AND EXTRACT(YEAR FROM appointment_date_time) = EXTRACT(YEAR FROM CURRENT_DATE)
-- GROUP BY employee_name 
-- ORDER BY appointments_this_month DESC;

-- 4. Receita por dia dos últimos 30 dias
-- SELECT 
--     DATE(appointment_date_time) as date,
--     SUM(total_price) as daily_revenue,
--     COUNT(*) as appointments_count
-- FROM appointments 
-- WHERE status = 'CONCLUIDO'
-- AND appointment_date_time >= CURRENT_DATE - INTERVAL '30 days'
-- GROUP BY DATE(appointment_date_time)
-- ORDER BY date DESC;

-- 5. Serviços mais lucrativos
-- SELECT * FROM view_popular_services WHERE times_booked > 0 ORDER BY total_revenue DESC;

-- 6. Clientes que não vêm há mais de 60 dias
-- SELECT 
--     name, 
--     phone_number, 
--     last_visit 
-- FROM view_clients_with_history 
-- WHERE last_appointment < CURRENT_DATE - INTERVAL '60 days'
-- OR last_appointment IS NULL
-- ORDER BY last_appointment DESC NULLS LAST;

-- 7. Horários livres para um funcionário em um dia específico
-- WITH business_hours AS (
--     SELECT generate_series(
--         '2025-11-07 08:00:00'::timestamp,
--         '2025-11-07 18:00:00'::timestamp,
--         '30 minutes'::interval
--     ) AS time_slot
-- )
-- SELECT time_slot
-- FROM business_hours
-- WHERE check_time_availability(1, time_slot, 60) = true
-- ORDER BY time_slot;

-- =============================================
-- ÍNDICES ADICIONAIS PARA PERFORMANCE DE VIEWS
-- =============================================

-- Índices compostos para melhor performance das views
CREATE INDEX IF NOT EXISTS idx_appointments_date_status_employee 
ON appointments(appointment_date_time, status, stylist_id);

CREATE INDEX IF NOT EXISTS idx_appointments_client_status_date 
ON appointments(client_id, status, appointment_date_time);

CREATE INDEX IF NOT EXISTS idx_appointment_services_appointment_service 
ON appointment_services(appointment_id, service_id);

-- =============================================
-- COMENTÁRIOS DAS VIEWS
-- =============================================

COMMENT ON VIEW view_appointments_full IS 'View completa com agendamentos, clientes, funcionários e serviços';
COMMENT ON VIEW view_today_schedule IS 'Agenda do dia atual';
COMMENT ON VIEW view_clients_with_history IS 'Clientes com estatísticas de agendamentos e gastos';
COMMENT ON VIEW view_employees_stats IS 'Funcionários com estatísticas de performance';
COMMENT ON VIEW view_popular_services IS 'Serviços ordenados por popularidade e receita';