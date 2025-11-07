# Scripts SQL - AGS Unissex 2.0

Este diretório contém todos os scripts SQL necessários para configurar e gerenciar o banco de dados PostgreSQL do sistema AGS Unissex.

## 📁 Estrutura dos Scripts

### 1. `01_create_tables.sql`
**Propósito**: Criação da estrutura completa do banco de dados
- ✅ Cria todas as tabelas principais
- ✅ Define relacionamentos (foreign keys)
- ✅ Cria índices para performance
- ✅ Configura triggers para `updated_at`
- ✅ Adiciona comentários e documentação

**Tabelas criadas**:
- `clients` - Clientes do salão
- `employees` - Funcionários/cabeleireiros
- `services` - Serviços oferecidos
- `appointments` - Agendamentos
- `appointment_services` - Relacionamento N:N entre agendamentos e serviços

### 2. `02_sample_data.sql`
**Propósito**: Inserção de dados de exemplo para teste
- ✅ 18 serviços variados (cortes, coloração, tratamentos, etc.)
- ✅ 5 funcionários com diferentes especialidades
- ✅ 8 clientes com perfis diversos
- ✅ 15 agendamentos (passados, atuais e futuros)
- ✅ Relacionamentos completos entre agendamentos e serviços

### 3. `03_drop_tables.sql`
**Propósito**: Limpeza e reset do banco (desenvolvimento)
- ⚠️ Script para dropar tabelas (comentado por segurança)
- ⚠️ Opção para limpar dados mantendo estrutura
- ⚠️ Reset de sequences (IDs)

### 4. `04_views_and_queries.sql`
**Propósito**: Views úteis e consultas comuns
- ✅ Views para relatórios e consultas frequentes
- ✅ Função para verificar disponibilidade de horários
- ✅ Exemplos de consultas para relatórios
- ✅ Índices adicionais para performance

## 🚀 Como Usar

### Configuração Inicial (Primeira vez)

1. **Certifique-se que o PostgreSQL está rodando no Docker**:
```bash
# Se não estiver rodando, suba o container
docker-compose up -d postgres
```

2. **Execute os scripts na ordem**:
```bash
# 1. Criar estrutura das tabelas
psql -h localhost -U your_user -d your_database -f 01_create_tables.sql

# 2. Inserir dados de exemplo (opcional, mas recomendado para testes)
psql -h localhost -U your_user -d your_database -f 02_sample_data.sql

# 3. Criar views e funções úteis
psql -h localhost -U your_user -d your_database -f 04_views_and_queries.sql
```

### Usando com seu arquivo .env

Com base no seu `.env.development`, os comandos ficam:
```bash
psql -h localhost -U ags_user -d ags_unissex_db -f 01_create_tables.sql
psql -h localhost -U ags_user -d ags_unissex_db -f 02_sample_data.sql
psql -h localhost -U ags_user -d ags_unissex_db -f 04_views_and_queries.sql
```

### Reset do Banco (Desenvolvimento)

Se precisar resetar tudo:
```bash
# 1. Descomente as linhas em 03_drop_tables.sql
# 2. Execute o drop
psql -h localhost -U ags_user -d ags_unissex_db -f 03_drop_tables.sql

# 3. Recrie tudo
psql -h localhost -U ags_user -d ags_unissex_db -f 01_create_tables.sql
psql -h localhost -U ags_user -d ags_unissex_db -f 02_sample_data.sql
psql -h localhost -U ags_user -d ags_unissex_db -f 04_views_and_queries.sql
```

## 📊 Views Disponíveis

Após executar os scripts, você terá acesso às seguintes views:

- **`view_appointments_full`**: Agendamentos completos com cliente, funcionário e serviços
- **`view_today_schedule`**: Agenda do dia atual
- **`view_clients_with_history`**: Clientes com estatísticas de agendamentos
- **`view_employees_stats`**: Funcionários com estatísticas de performance
- **`view_popular_services`**: Serviços mais populares

### Exemplos de Uso das Views

```sql
-- Ver agenda de hoje
SELECT * FROM view_today_schedule;

-- Top 5 clientes que mais gastaram
SELECT client_name, total_spent, total_appointments 
FROM view_clients_with_history 
WHERE total_spent > 0 
ORDER BY total_spent DESC 
LIMIT 5;

-- Funcionários com melhor performance
SELECT employee_name, completed_appointments, total_revenue, avg_ticket
FROM view_employees_stats
WHERE completed_appointments > 0
ORDER BY total_revenue DESC;
```

## 🔧 Mapeamento Java ↔ SQL

### Correspondência de Campos

**Client.java ↔ clients table**:
- `hairType` ↔ `hair_type`
- `hairTexture` ↔ `hair_texture`
- `preferredStylist` ↔ `preferred_stylist`
- `registrationDate` ↔ `registration_date`
- `lastVisit` ↔ `last_visit`

**Employee.java ↔ employees table**:
- `experienceLevel` ↔ `experience_level`
- `baseSalary` ↔ `base_salary`
- `commissionRate` ↔ `commission_rate`
- `workingHours` ↔ `working_hours`
- `workingDays` ↔ `working_days` (JSON)
- `canPerformChemicalTreatments` ↔ `can_perform_chemical_treatments`
- `preferredClientType` ↔ `preferred_client_type`

**Service.java ↔ services table**:
- `Duration duration` ↔ `duration_minutes INTEGER`

**Appointment.java ↔ appointments table**:
- `appointmentDateTime` ↔ `appointment_date_time`
- `totalPrice` ↔ `total_price`
- `List<Long> serviceIds` ↔ `appointment_services.service_id`

## ⚠️ Importantes

1. **Backup**: Sempre faça backup antes de executar scripts de drop
2. **Ambiente**: Use o script de drop apenas em desenvolvimento
3. **Sequences**: Os IDs começam do 1 e são auto-incrementais
4. **Triggers**: O campo `updated_at` é atualizado automaticamente
5. **Foreign Keys**: Cuidado ao deletar registros com relacionamentos

## 🧪 Dados de Teste

Os dados de exemplo incluem:
- **Serviços**: Desde corte básico (R$ 25) até penteado de noiva (R$ 150)
- **Funcionários**: Diferentes especialidades e níveis de experiência
- **Clientes**: Perfis variados com tipos de cabelo e preferências
- **Agendamentos**: Histórico, agenda atual e futura

## 📈 Performance

Os scripts incluem índices otimizados para:
- Consultas por data de agendamento
- Busca por cliente/funcionário
- Relatórios de faturamento
- Views de estatísticas

## 🔗 Integração com Java

Estes scripts foram criados para trabalhar perfeitamente com:
- Suas classes Java existentes
- O sistema de pool de conexões HikariCP
- Os DAOs que serão implementados
- As configurações do `.env.development`