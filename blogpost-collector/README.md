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
:collector-adapters:adapter-banksalad:build
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
