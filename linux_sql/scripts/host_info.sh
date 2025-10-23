#!/bin/bash
# ----------------------------------------------
# Script Name: host_info.sh
# Description: Collect host hardware info and insert into host_info table
# Usage: ./host_info.sh psql_host psql_port db_name psql_user psql_password
# ----------------------------------------------

# Arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Validate arguments
if [ "$#" -ne 5 ]; then
  echo "Illegal number of parameters"
  echo "Usage: ./host_info.sh psql_host psql_port db_name psql_user psql_password"
  exit 1
fi

# Collect hardware info
hostname=$(hostname -f)
lscpu_out=$(lscpu)
meminfo_out=$(cat /proc/meminfo)

cpu_number=$(echo "$lscpu_out" | egrep "^CPU\(s\):" | awk '{print $2}')
cpu_architecture=$(echo "$lscpu_out" | egrep "^Architecture:" | awk '{print $2}')
cpu_model=$(echo "$lscpu_out" | egrep "^Model name:" | cut -d ':' -f2 | xargs)
cpu_mhz=$(echo "$lscpu_out" | grep -E "CPU MHz|CPU max MHz" | head -n1 | awk '{print $3}')
if [ -z "$cpu_mhz" ]; then
  cpu_mhz=0
fi
l2_cache=$(echo "$lscpu_out" | egrep "^L2 cache:" | awk '{print $3}' | sed 's/[^0-9]*//g')
if [ -z "$l2_cache" ]; then
  l2_cache=0
fi
total_mem=$(echo "$meminfo_out" | egrep "^MemTotal:" | awk '{print $2}')
timestamp=$(date +"%Y-%m-%d %H:%M:%S")

# Insert data
insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, total_mem, \"timestamp\") VALUES('${hostname}', ${cpu_number}, '${cpu_architecture}', '${cpu_model}', ${cpu_mhz}, ${l2_cache}, ${total_mem}, '${timestamp}');"

export PGPASSWORD=$psql_password
psql -h "$psql_host" -p "$psql_port" -U "$psql_user" -d "$db_name" -c "$insert_stmt"

exit 0

