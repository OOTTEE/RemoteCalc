# RemoteCalc
A remote reverse polish calculator

# Description
This project build two artifacts:
 - Server: TCP server accept and resolve simple mathematical operations in reverse polish annotation
   -- SERVER_PORT: Listen tcp port. Default 41000.
 - Client: TCP client can read from standard input and send the operations to the server
   -- PORT: Connection port. Default 41000.
   -- HOST: Connection host. Default 127.0.0.1
   
# Protocol
[Protocol description](https://github.com/OOTTEE/RemoteCalc/blob/develop/PROTOCOL_DEF.md)


# Docker usage
## Build server
```
docker build -f polish-calc-server/src/main/docker/Dockerfile.jvm -t polish-calc/polish-server .
```
## Build client
```
docker build -f polish-calc-client/src/main/docker/Dockerfile.jvm -t polish-calc/polish-client .
```

## Application run
```
docker network create polish_net
 
docker run -it --network polish_net --name server polish-calc/polish-server

docker run -it --rm -e SERVER_PORT=50000 -v $HOME/logs:/work/application/logs -p 50000 --network polish_net --name server polish-calc/polish-server
```

##Start client
```
docker run -it --rm -e PORT=50000 -e HOST=server -e PORT=50000  --network polish_net polish-calc/polish-client
```

# Docker compose usage
## build the services
``` 
docker-compose build
```
## Start the service
```
docker-compose up -d server
```
## Open server logs
```
docker-compose logs -f
```
## Attach client console
```
docker-compose run client
```
