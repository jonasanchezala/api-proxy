#!/bin/bash
docker build -t apiproxy:1.0 .
docker run -d -p 9090:8080 --name apiproxy apiproxy:1.0