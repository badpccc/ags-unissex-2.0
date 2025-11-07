-- Script para limpar/dropar tabelas
-- AGS Unissex - Sistema de Gerenciamento de Salão
-- PostgreSQL Database - Drop Tables
-- Versão: 1.0
-- ATENÇÃO: Este script apaga TODOS os dados!

-- =============================================
-- AVISO IMPORTANTE
-- =============================================
-- Este script irá APAGAR TODAS as tabelas e dados!
-- Use apenas em ambiente de desenvolvimento.
-- Descomente as linhas abaixo para executar.

-- =============================================
-- LIMPAR DADOS (manter estrutura)
-- =============================================

-- Descomente para limpar apenas os dados, mantendo as tabelas
/*
TRUNCATE TABLE appointment_services CASCADE;
TRUNCATE TABLE appointments CASCADE;
TRUNCATE TABLE services CASCADE;
TRUNCATE TABLE employees CASCADE;
TRUNCATE TABLE clients CASCADE;
*/

-- =============================================
-- DROPAR TABELAS (apagar tudo)
-- =============================================

-- Descomente para apagar completamente as tabelas
/*
-- Dropar tabelas na ordem correta (respeitando foreign keys)
DROP TABLE IF EXISTS appointment_services CASCADE;
DROP TABLE IF EXISTS appointments CASCADE;
DROP TABLE IF EXISTS services CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS clients CASCADE;

-- Dropar função de trigger
DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;
*/

-- =============================================
-- RESETAR SEQUENCES (IDs)
-- =============================================

-- Descomente para resetar os IDs para começar do 1 novamente
/*
ALTER SEQUENCE clients_id_seq RESTART WITH 1;
ALTER SEQUENCE employees_id_seq RESTART WITH 1;
ALTER SEQUENCE services_id_seq RESTART WITH 1;
ALTER SEQUENCE appointments_id_seq RESTART WITH 1;
ALTER SEQUENCE appointment_services_id_seq RESTART WITH 1;
*/

-- =============================================
-- VERIFICAR SE TABELAS EXISTEM
-- =============================================

SELECT 
    table_name,
    table_type
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('clients', 'employees', 'services', 'appointments', 'appointment_services')
ORDER BY table_name;