create table attendance_entries (
    id uuid primary key default gen_random_uuid(),
    organization_id uuid not null references organizations(id),
    employee_id uuid not null references employees(id),
    work_date date not null,
    clock_in timestamptz not null,
    clock_out timestamptz,
    status varchar(30) not null default 'PENDING',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (employee_id, work_date)
);

create index idx_attendance_entries_org on attendance_entries(organization_id);
create index idx_attendance_entries_employee on attendance_entries(employee_id);
