# RemoteCalc
A remote reverse polish calculator


# Build server
```
docker build -f polish-calc-server/src/main/docker/Dockerfile.jvm -t polish-calc/polish-server .
```
# Build client
```
docker build -f polish-calc-server/src/main/docker/Dockerfile.jvm -t polish-client/polish-client .
```

# Start server
```
docker run -it --rm -p 41000 polish-calc/polish-server 
or
docker run -it --rm -e SERVER_PORT=50000 -v ./logs:/work/application/logs -p 50000 polish-calc/polish-server 
```

# Start client
```
docker run -it --rm polish-calc/polish-client 
```