# collector-engine
1. engine.run() 으로 run
2. 기본 adapter 를 가짐
3. 받아야 하는거 : 대상 및 adapter

# collector-common
1. domain 
2. utils

# collector-A-adapter
1. 대상 url 및 adapter 구현
2. engine 을 run

# collector-infra
1. port out 관련 infra

# 빌드

## 전체 빌드
```aiexclude
./gradlew \
:collector-adapters:adapter-woowahan:build \
:collector-adapters:adapter-toss:build \
:collector-adapters:adapter-musinsa:build \
:collector-adapters:adapter-kurly:build \
:collector-adapters:adapter-devsisters:build \
:collector-adapters:adapter-line:build \
:collector-adapters:adapter-daangn:build \
:collector-adapters:adapter-watcha:build \
:collector-adapters:adapter-banksalad:build \
:collector-adapters:adapter-yogiyo:build \
:collector-adapters:adapter-ridi:build \
:collector-adapters:adapter-nhn:build \

```

## 전체 실행
```aiexclude
./gradlew \
:collector-adapters:adapter-woowahan:bootRun \
:collector-adapters:adapter-toss:bootRun \
:collector-adapters:adapter-musinsa:bootRun \
:collector-adapters:adapter-kurly:bootRun \
:collector-adapters:adapter-devsisters:bootRun \
:collector-adapters:adapter-line:bootRun \
:collector-adapters:adapter-daangn:bootRun \
:collector-adapters:adapter-watcha:bootRun \
:collector-adapters:adapter-banksalad:bootRun --parallel
```

# crontab -e
```aiexclude
5 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-woowahan/build/libs/adapter-woowahan-0.0.1.jar >> /home/hobeenkim/logs/adapter-woowahan.log 2>&1
10 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-toss/build/libs/adapter-toss-0.0.1.jar >> /home/hobeenkim/logs/adapter-toss.log 2>&1
15 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-musinsa/build/libs/adapter-musinsa-0.0.1.jar >> /home/hobeenkim/logs/adapter-musinsa.log 2>&1
20 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-kurly/build/libs/adapter-kurly-0.0.1.jar >> /home/hobeenkim/logs/adapter-kurly.log 2>&1
25 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-devsisters/build/libs/adapter-devsisters-0.0.1.jar >> /home/hobeenkim/logs/adapter-devsisters.log 2>&1
30 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-line/build/libs/adapter-line-0.0.1.jar >> /home/hobeenkim/logs/adapter-line.log 2>&1
35 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-daangn/build/libs/adapter-daangn-0.0.1.jar >> /home/hobeenkim/logs/adapter-daangn.log 2>&1
40 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-watcha/build/libs/adapter-watcha-0.0.1.jar >> /home/hobeenkim/logs/adapter-watcha.log 2>&1
45 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-banksalad/build/libs/adapter-banksalad-0.0.1.jar >> /home/hobeenkim/logs/adapter-banksalad.log 2>&1
50 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-yogiyo/build/libs/adapter-yogiyo-0.0.1.jar >> /home/hobeenkim/logs/adapter-yogiyo.log 2>&1
55 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-ridi/build/libs/adapter-ridi-0.0.1.jar >> /home/hobeenkim/logs/adapter-ridi.log 2>&1
0 * * * * /usr/bin/java -jar /home/hobeenkim/blogpost/blogpost-collector/collector-adapters/adapter-nhn/build/libs/adapter-nhn-0.0.1.jar >> /home/hobeenkim/logs/adapter-nhn.log 2>&1
```
