-- Migration: Adicionar campos de login para funcionários
-- Data: 2025-11-24
-- Descrição: Adiciona username e password_hash na tabela employees para permitir login

-- Adicionar coluna username (obrigatório e único)
ALTER TABLE employees 
ADD COLUMN IF NOT EXISTS username VARCHAR(100);

-- Adicionar coluna password_hash (obrigatório)
ALTER TABLE employees 
ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);

-- Criar índice para username (otimização de busca no login)
CREATE INDEX IF NOT EXISTS idx_employees_username ON employees(username);

-- Adicionar constraint de unicidade para username
-- (só adiciona se ainda não existir)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'employees_username_key'
    ) THEN
        ALTER TABLE employees ADD CONSTRAINT employees_username_key UNIQUE (username);
    END IF;
END $$;

-- Comentários
COMMENT ON COLUMN employees.username IS 'Nome de usuário para login no sistema';
COMMENT ON COLUMN employees.password_hash IS 'Senha criptografada com BCrypt';

-- Para funcionários existentes sem username/senha, você pode atualizar manualmente:
-- UPDATE employees SET username = 'funcionario1', password_hash = '$2a$10$...' WHERE id = 1;
