#!/bin/bash
docker stop convenios-service
docker rm convenios-service
docker rmi convenios-service-rest:1.0