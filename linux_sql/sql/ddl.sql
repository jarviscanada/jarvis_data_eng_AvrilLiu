-- Drop tables if they already exist
DROP TABLE IF EXISTS public.host_usage;
DROP TABLE IF EXISTS public.host_info;
-- Create host_info table
CREATE TABLE IF NOT EXISTS public.host_info
(
    id               SERIAL PRIMARY KEY,
    hostname         VARCHAR(255) NOT NULL UNIQUE,
    cpu_number       SMALLINT     NOT NULL,
    cpu_architecture VARCHAR(50)  NOT NULL,
    cpu_model        VARCHAR(255) NOT NULL,
    cpu_mhz          FLOAT8       NOT NULL,
    l2_cache         INTEGER      NULL,
    total_mem        INTEGER      NOT NULL,
    "timestamp"      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- Create host_usage table
CREATE TABLE IF NOT EXISTS public.host_usage
(
    "timestamp"      TIMESTAMP NOT NULL,
    host_id          INTEGER   NOT NULL,
    memory_free      INTEGER   NOT NULL,
    cpu_idle         SMALLINT  NOT NULL,
    cpu_kernel       SMALLINT  NOT NULL,
    disk_io          INTEGER   NOT NULL,
    disk_available   INTEGER   NOT NULL,
    CONSTRAINT host_usage_host_info_fk
        FOREIGN KEY (host_id)
        REFERENCES public.host_info (id)
        ON DELETE CASCADE
);
DO $$
BEGIN
   RAISE NOTICE 'DDL executed successfully: host_info and host_usage tables created.';
END
$$;
