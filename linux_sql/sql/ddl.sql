-- ============================================
-- DDL Script: Initialize host_agent database
-- Description: Create host_info and host_usage tables
-- ============================================

-- Note:
-- Database (host_agent) must be created manually before running this script
-- Example: psql -h localhost -U postgres -d host_agent -f sql/ddl.sql

-- Create host_info table
CREATE TABLE IF NOT EXISTS public.host_info (
  id SERIAL PRIMARY KEY,
  hostname VARCHAR(255) NOT NULL UNIQUE,
  cpu_number SMALLINT NOT NULL,
  cpu_architecture VARCHAR(50) NOT NULL,
  cpu_model VARCHAR(100) NOT NULL,
  cpu_mhz DOUBLE PRECISION NOT NULL,
  l2_cache INTEGER NOT NULL,    -- in KB
  total_mem INTEGER NOT NULL,   -- in KB
  "timestamp" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create host_usage table
CREATE TABLE IF NOT EXISTS public.host_usage (
  id SERIAL PRIMARY KEY,
  host_id INTEGER NOT NULL REFERENCES public.host_info(id),
  memory_free INTEGER NOT NULL,    -- in KB
  cpu_idle NUMERIC(5,2) NOT NULL,
  cpu_kernel NUMERIC(5,2) NOT NULL,
  disk_io NUMERIC(5,2) NOT NULL,
  disk_available INTEGER NOT NULL, -- in KB
  "timestamp" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Verify created tables
\dt
