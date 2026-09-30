-- ============================================================================
-- V2__identity_and_audit_schemas.sql
-- Criação de Schemas Corporativos (identity, audit), Tabelas e Seed de Usuários
-- ============================================================================

-- 1. Novos Schemas de Governança
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS audit;

-- 2. Schema identity: Tabela de Departamentos
CREATE TABLE IF NOT EXISTS identity.departments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_departments_code ON identity.departments(code);

-- 3. Inserção de Departamentos Padronizados
INSERT INTO identity.departments (code, name, description) VALUES
    ('ATENDIMENTO', 'Atendimento & Cooperados', 'Canais de relacionamento com cooperados e suporte operacional'),
    ('DHO', 'Desenvolvimento Humano e Organizacional (DHO)', 'Capacitação, cultura organizacional e gestão de pessoas'),
    ('TI_GOV', 'Governança & TI', 'Segurança da informação, infraestrutura e arquitetura de sistemas')
ON CONFLICT (code) DO NOTHING;

-- 4. Inserção / Sincronização de Usuários Canônicos (Keycloak -> public.users)
-- Novos usuários criados para cada perfil:
INSERT INTO public.users (keycloak_id, name, email, role, department) VALUES
    ('06784c3d-9b81-47f0-a405-8d24549e358b', 'Lucas Almeida', 'lucas.colaborador@coop.local', 'COLABORADOR', 'Atendimento & Cooperados'),
    ('3c5cc604-857a-444f-8269-0d3ad66c032b', 'Mariana Ribeiro', 'mariana.gestora@coop.local', 'GESTOR', 'Desenvolvimento Humano e Organizacional (DHO)'),
    ('43ec9b62-9fed-4e17-8a42-40fe896f0787', 'Rodrigo Martins', 'rodrigo.admin@coop.local', 'ADMIN', 'Governança & TI')
ON CONFLICT (keycloak_id) DO UPDATE SET
    name = EXCLUDED.name,
    email = EXCLUDED.email,
    role = EXCLUDED.role,
    department = EXCLUDED.department,
    updated_at = CURRENT_TIMESTAMP;

-- Usuários padrão legados sincronizados:
INSERT INTO public.users (keycloak_id, name, email, role, department) VALUES
    ('9492006c-0c7e-4113-9a35-66c1b7f2e7a0', 'Administrador Master', 'admin@coop.local', 'ADMIN', 'Governança & TI'),
    ('1e1c6475-cdc8-4692-9998-6a2c77272213', 'Gestor Treinamentos', 'gestor@coop.local', 'GESTOR', 'Desenvolvimento Humano e Organizacional (DHO)'),
    ('0cbd65c5-76a3-4b88-8283-8e39bc1a5c2b', 'Colaborador Novo', 'colaborador@coop.local', 'COLABORADOR', 'Atendimento & Cooperados'),
    ('b851aa1f-cd27-4eda-a7f5-a0f9c805f229', 'Carlos Souza', 'carlos.souza@coop.local', 'COLABORADOR', 'Atendimento & Cooperados'),
    ('292ff876-9048-4d59-b9d9-a9b42761777d', 'Fernanda Lima', 'fernanda.lima@coop.local', 'GESTOR', 'Desenvolvimento Humano e Organizacional (DHO)')
ON CONFLICT (keycloak_id) DO NOTHING;

-- 5. Schema identity: Tabela de Perfis Complementares (Perfil Rico)
CREATE TABLE IF NOT EXISTS identity.user_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    department_id UUID REFERENCES identity.departments(id) ON DELETE SET NULL,
    job_title VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    bio TEXT,
    onboarding_status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_profiles_user_id UNIQUE (user_id)
);

CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON identity.user_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_profiles_department ON identity.user_profiles(department_id);

-- 6. Popular Perfis Complementares
INSERT INTO identity.user_profiles (user_id, department_id, job_title, bio, onboarding_status)
SELECT 
    u.id,
    d.id,
    'Analista de Atendimento Júnior',
    'Novo colaborador na cooperativa focado no atendimento e acolhimento dos cooperados.',
    'IN_PROGRESS'
FROM public.users u
CROSS JOIN identity.departments d
WHERE u.email = 'lucas.colaborador@coop.local' AND d.code = 'ATENDIMENTO'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO identity.user_profiles (user_id, department_id, job_title, bio, onboarding_status)
SELECT 
    u.id,
    d.id,
    'Coordenadora de Onboarding & DHO',
    'Gestora responsável pela condução de turmas e avaliação contínua de competências.',
    'COMPLETED'
FROM public.users u
CROSS JOIN identity.departments d
WHERE u.email = 'mariana.gestora@coop.local' AND d.code = 'DHO'
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO identity.user_profiles (user_id, department_id, job_title, bio, onboarding_status)
SELECT 
    u.id,
    d.id,
    'Arquiteto de Soluções & Governança',
    'Administrador do ecossistema de integração, segurança IAM Keycloak e governança.',
    'COMPLETED'
FROM public.users u
CROSS JOIN identity.departments d
WHERE u.email = 'rodrigo.admin@coop.local' AND d.code = 'TI_GOV'
ON CONFLICT (user_id) DO NOTHING;

-- 7. Schema audit: Tabela de Auditoria de Acessos e Sessões
CREATE TABLE IF NOT EXISTS audit.user_access_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES public.users(id) ON DELETE SET NULL,
    keycloak_id VARCHAR(64) NOT NULL,
    action VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    details JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_access_logs_user_id ON audit.user_access_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_access_logs_action ON audit.user_access_logs(action);
CREATE INDEX IF NOT EXISTS idx_access_logs_created_at ON audit.user_access_logs(created_at);

-- 8. Registro de Auditoria da Inicialização dos Usuários
INSERT INTO audit.user_access_logs (user_id, keycloak_id, action, details)
SELECT 
    id, 
    keycloak_id, 
    'USER_CREATED_AND_SYNCED', 
    json_build_object(
        'profile', role,
        'email', email,
        'department', department,
        'provisioned_by', 'KEYCLOAK_IAM_ORCHESTRATION'
    )::jsonb
FROM public.users
WHERE email IN (
    'lucas.colaborador@coop.local',
    'mariana.gestora@coop.local',
    'rodrigo.admin@coop.local'
);
