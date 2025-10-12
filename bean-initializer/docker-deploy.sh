#!/bin/sh
# Run Docker container for bean-initializer

docker run -d -p 8080:8080 --name bean-initializer bean-initializer:1.0

