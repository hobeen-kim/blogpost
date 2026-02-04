#!/bin/bash

./gradlew build -x test

pkill -f 'collector[^ ]*\.jar'

nohup java -jar collector-1.0.1.jar --spring.profiles.active=local > ~/logs/collector.log 2>&1 &