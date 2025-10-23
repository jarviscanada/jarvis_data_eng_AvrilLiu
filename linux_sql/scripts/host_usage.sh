#!/bin/bash
# Use Shebang

# ==================================
# Setup arguments
# ==================================
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# ==================================
# Validate arguments
# ==================================
if [ "$#" -ne 5 ]; then
  echo "Illegal number of parameters"
  echo "Usage: ./host_info.sh psql_host psql_port db_name psql_user psql_password"
  exit 1
fi

# ==================================
# Parse host hardware specification
# ==================================
hostname=$(hostname -f)
lscpu_out=$(lscpu)
meminfo_out=$(cat /proc/meminfo)

# Parse key specifications
cpu_number=$(echo "$lscpu_out" | egrep "^CPU\(s\):" | awk '{print $2}')
cpu_architecture=$(echo "$lscpu_out" | egrep "^Architecture:" | awk '{print $2}')
cpu_model=$(echo "$lscpu_out" | egrep "^Model name:" | cut -d ':' -f2 | xargs)
cpu_mhz=$(echo "$lscpu_out" | grep -E "CPU MHz|CPU max MHz" | head -n1 | awk '{print $3}')
if [ -z "$cpu_mhz" ]; then
  cpu_mhz=0
fi

# ==================================
# Construct and execute INSERT statement
# ==================================
timestamp=$(date +"%Y-%m-%d %H:%M:%S")
insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, total_mem, \"timestamp\") VALUES('${hostname}', ${cpu_number}, '${cpu_architecture}', '${cpu_model}', ${cpu_mhz}, ${total_mem}, '${timestamp}');"

export PGPASSWORD=$psql_password
psql -h "$psql_host" -p "$psql_port" -U "$psql_user" -d "$db_name" -c "$insert_stmt"

# Check for errors and clean up PGPASSWORD
if [ $? -ne 0 ]; then
    unset PGPASSWORD
    exit 1
fi
unset PGPASSWORD

# put appropriate exit number
exit 0
