create table organizations (
    id uuid primary key default gen_random_uuid(),
    name varchar(255) not null,
    created_at timestamptz not null default now()
);

create table employees (
    id uuid primary key default gen_random_uuid(),
    organization_id uuid not null references organizations(id),
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    email varchar(255) not null,
    job_title varchar(150),
    department varchar(150),
    manager_id uuid references employees(id),
    employment_status varchar(30) not null default 'ONBOARDING',
    hire_date date,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (organization_id, email)
);

create table app_users (
    id uuid primary key default gen_random_uuid(),
    organization_id uuid not null references organizations(id),
    employee_id uuid references employees(id),
    email varchar(255) not null,
    password_hash varchar(255) not null,
    role varchar(30) not null,
    created_at timestamptz not null default now(),
    unique (email)
);

create table leave_requests (
    id uuid primary key default gen_random_uuid(),
    organization_id uuid not null references organizations(id),
    employee_id uuid not null references employees(id),
    leave_type varchar(30) not null,
    start_date date not null,
    end_date date not null,
    status varchar(30) not null default 'PENDING',
    reason text,
    approver_id uuid references employees(id),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_employees_org on employees(organization_id);
create index idx_app_users_org on app_users(organization_id);
create index idx_leave_requests_org on leave_requests(organization_id);
create index idx_leave_requests_employee on leave_requests(employee_id);
