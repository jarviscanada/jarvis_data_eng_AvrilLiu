# Linux Cluster Monitoring Agent

This project implements a Linux Cluster Monitoring Agent that collects and stores hardware specifications and real-time resource usage data from multiple Linux servers into a centralized PostgreSQL database.

The project follows the GitFlow branching strategy. Each feature is developed in a separate branch and merged into `develop`. Finally, all features are integrated into the `main` branch through a single pull request.

---

## 1. Introduction
Two Bash scripts are used:
- `host_info.sh`: Collects static hardware information such as CPU, memory, and architecture.
- `host_usage.sh`: Collects real-time usage metrics such as CPU idle, memory, and disk.

Hardware information is collected once per host, while usage data is collected every minute using `crontab`. All data are stored in a PostgreSQL database running inside a Docker container.

---

## 2. Quick Start

```bash
# Start PostgreSQL container
bash scripts/psql_docker.sh create postgres password

# Connect and create database
psql -h localhost -U postgres -W
CREATE DATABASE host_agent;

# Create tables
psql -h localhost -U postgres -d host_agent -f sql/ddl.sql

# Insert host hardware info (run once)
bash scripts/host_info.sh localhost 5432 host_agent postgres password

# Insert real-time usage info (test)
bash scripts/host_usage.sh localhost 5432 host_agent postgres password

# Schedule usage collection every minute
crontab -e
* * * * * bash /home/rocky/dev/jarvis_data_eng_AvrilLiu/linux_sql/scripts/host_usage.sh localhost 5432 host_agent postgres password > /tmp/host_usage.log 2>&1
```

---

## 3. Scripts

| Script | Description |
|--------|--------------|
| `psql_docker.sh` | Manages PostgreSQL container lifecycle |
| `host_info.sh` | Collects and inserts static hardware information |
| `host_usage.sh` | Collects and inserts live resource usage data |
| `ddl.sql` | Defines database schema for `host_info` and `host_usage` |
| `crontab` | Automates periodic execution of `host_usage.sh` |

---

## 4. Implementation

For this project, I built two main Bash scripts that interact with a PostgreSQL database.  
The first one, `host_info.sh`, collects static hardware details like CPU model, architecture, and memory, and inserts them into a table called `host_info`.  
The second one, `host_usage.sh`, collects live system usage data (CPU, memory, disk) every minute and writes it into the `host_usage` table.  
To automate this, I set up a crontab job that runs the usage script every minute.  
All the data is stored in a central PostgreSQL container running on Docker, so it’s easy to test, reset, or migrate.  
The implementation is straightforward but tries to follow a real-world DevOps workflow where monitoring and data ingestion are separated and automated.

---

## 5. Architecture

The project uses a simple cluster setup:  
three Linux hosts are connected to a single PostgreSQL instance running in Docker.  
Each host runs the two agent scripts (one-time hardware info and recurring usage collection).  
The collected data is pushed to the central DB, allowing centralized monitoring and later analysis.

I drew the architecture diagram in **draw.io**, saved as `assets/architecture.png`.

---

## 6. Database Modeling

The database contains two tables, `host_info` and `host_usage`.  
They are linked by a foreign key (`host_id`), so each usage record maps back to its host hardware info.

---

### `host_info`
| Column Name | Data Type | Description |
|--------------|------------|-------------|
| id | SERIAL (PK) | Unique host identifier |
| hostname | VARCHAR | Name of the host |
| cpu_number | INT | Number of CPUs |
| cpu_architecture | VARCHAR | CPU architecture type |
| cpu_model | VARCHAR | CPU model name |
| cpu_mhz | FLOAT | Clock speed in MHz |
| l2_cache | INT | L2 cache size (KB) |
| total_mem | INT | Total memory (KB) |
| timestamp | TIMESTAMP | Record insertion time |

### `host_usage`
| Column Name | Data Type | Description |
|--------------|------------|-------------|
| timestamp | TIMESTAMP | Time when data was collected |
| host_id | INT (FK) | References `host_info(id)` |
| memory_free | INT | Available memory (KB) |
| cpu_idle | INT | CPU idle percentage |
| cpu_kernel | INT | CPU kernel usage percentage |
| disk_io | INT | Disk I/O count |
| disk_available | INT | Free disk space (KB) |

---

## 7. Deployment

I used a Dockerized PostgreSQL setup for this project.  
All scripts and SQL files are pushed to GitHub under the `linux_sql` folder.  
After cloning the repo, I ran `psql_docker.sh` to start a DB container, created the tables using `ddl.sql`,  
and then executed `host_info.sh` and `host_usage.sh` to populate data.  
For automation, I added a crontab entry so the usage script runs every minute in the background.  
This setup makes it easy to replicate or migrate the monitoring system to any Linux environment.

---

## 8. Testing

```bash
# Verify container
docker ps

# Check tables
psql -h localhost -U postgres -d host_agent -c "\dt"

# Validate data
psql -h localhost -U postgres -d host_agent -c "SELECT * FROM host_info;"
psql -h localhost -U postgres -d host_agent -c "SELECT * FROM host_usage LIMIT 5;"
```

---

## 9. Future Improvements

- Automatically update host information when hardware changes.  
- Add logging and error-handling for database connection failures.  
- Integrate with a dashboard (Grafana or Flask) for real-time visualization.

