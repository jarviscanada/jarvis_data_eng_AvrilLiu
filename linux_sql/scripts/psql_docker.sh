# script usage
./scripts/psql_docker.sh start|stop|create [db_username][db_password]

# examples
## create a psql docker container with the given username and password.
## print error message if username or password is not given
## print error message if the container is already created
./scripts/psql_docker.sh create db_username db_password

## start the stoped psql docker container
## print error message if the container is not created
./scripts/psql_docker.sh start

## stop the running psql docker container
## print error message if the container is not created
./scripts/psql_docker.sh stop
