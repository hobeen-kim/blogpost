#!/bin/zsh
# 토픽 생성
./kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 3 \
  --config min.insync.replicas=2 \
  --partitions 3 \
  --topic raw-queue

./kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 3 \
  --config min.insync.replicas=2 \
  --partitions 3 \
  --topic generate-queue

./kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 3 \
  --config min.insync.replicas=2 \
  --partitions 3 \
  --topic save-queue