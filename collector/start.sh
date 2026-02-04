#!/bin/bash

./gradlew build -x test

pkill -f 'collector[^ ]*\.jar'

nohup java -jar build/libs/collector-1.0.1.jar --spring.profiles.active=local > ~/logs/collector.log 2>&1 &

echo "실행 완료 ...."
echo ""

spinner() {
  pid="$1"
  chars='|/-\'
  i=0
  while kill -0 "$pid" 2>/dev/null; do
    i=$(( (i + 1) % 4 ))
    printf "\r로딩중... (프로세스 실행 대기) %c" "$(printf "%s" "$chars" | cut -c $((i+1)))"
    sleep 0.1
  done
  printf "\r로딩 완료!   \n"
}

(
  for i in 1 2 3 4 5 6 7 8 9 10; do
    sleep 1
  done
) &
job=$!

spinner "$job"
wait "$job"

cat ~/logs/collector.log