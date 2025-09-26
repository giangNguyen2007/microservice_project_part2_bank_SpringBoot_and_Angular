#!/bin/bash

curl -i -X OPTIONS \
  http://172.25.240.1:8081/user \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: authorization,content-type"



docker network inspect docker-compose_bank-app-network