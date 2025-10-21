# script usage
#./scripts/psql_docker.sh start|stop|create [db_username][db_password]

# examples
## create a psql docker container with the given username and password.
## print error message if username or password is not given
## print error message if the container is already created
#./scripts/psql_docker.sh create db_username db_password

## start the stoped psql docker container
## print error message if the container is not created
#./scripts/psql_docker.sh start

## stop the running psql docker container
## print error message if the container is not created
#./scripts/psql_docker.sh stop
#!/bin/bash

# -----------------------------------------------
# Usage:
# ./scripts/psql_docker.sh start|stop|create [db_username] [db_password]
# -----------------------------------------------

container_name="jrvs-psql"

case $1 in
  create)
    if [ -z "$2" ] || [ -z "$3" ]; then
      echo "Error: Missing username or password."
      echo "Usage: $0 create [db_username] [db_password]"
      exit 1
    fi

    # Check if container already exists
    if [ "$(docker container ls -a -f name=$container_name | wc -l)" -gt 1 ]; then
      echo "Error: Container $container_name already exists."
      exit 1
    fi

    echo "Creating PostgreSQL container..."
    docker volume create pgdata
    docker run --name $container_name -e POSTGRES_USER=$2 -e POSTGRES_PASSWORD=$3 \
      -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres:latest
    echo "PostgreSQL container created successfully."
    ;;

  start)
    if [ "$(docker container ls -a -f name=$container_name | wc -l)" -eq 1 ]; then
      echo "Error: Container $container_name not found."
      exit 1
    fi
    docker container start $container_name
    echo "PostgreSQL container started."
    ;;

  stop)
    if [ "$(docker container ls -a -f name=$container_name | wc -l)" -eq 1 ]; then
      echo "Error: Container $container_name not found."
      exit 1
    fi
    docker container stop $container_name
    echo "PostgreSQL container stopped."
    ;;

  *)
    echo "Usage: $0 {create|start|stop} [username] [password]"
    exit 1
    ;;
esac

