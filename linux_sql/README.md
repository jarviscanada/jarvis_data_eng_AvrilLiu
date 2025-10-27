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

## 4. Testing

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

## 5. Future Improvements

- Automatically update host information when hardware changes.  
- Add logging and error-handling for database connection failures.  
- Integrate with a dashboard (Grafana or Flask) for real-time visualization.

