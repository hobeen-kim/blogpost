# 수집 소스 목록

add-source 스킬 사용 시 이 목록을 확인하고, 소스 추가/변경/삭제 시 업데이트할 것.

## 활성화 소스 (53개 target, 45개 source)

| source | 한글명 | 수집 방식 | target_name | 비고 |
|--------|--------|-----------|-------------|------|
| 11st | 11번가 | RSS | 11st | |
| 29cm | 29CM | RSS (Medium) | 29cm | proxy 필요 |
| aws | AWS | RSS | aws | |
| banksalad | 뱅크샐러드 | RSS | banksalad | |
| beusable | 뷰저블 | HTML Paged | beusable | |
| buzzvil | 버즈빌 | HTML Paged | buzzvil | |
| com2us | 게임빌컴투스 | RSS | com2us | |
| daangn | 당근 | RSS (Medium) | daangn | proxy 필요 |
| devocean | 데보션 | HTML Paged | devoceanAll | |
| devsisters | 데브시스터즈 | RSS | devsisters | |
| encarai | 엔카닷컴AI | RSS (Medium) | encarai | proxy 필요 |
| ffbits | 44bits | HTML Paged | ffbits | |
| gabia | 가비아 | RSS | gabia | |
| gangnamunni | 강남언니 | HTML | gangnamunni | |
| gccompany | 여기어때 | RSS (Medium) | gccompany | proxy 필요 |
| gmarket | 지마켓 | RSS (Tistory) | gmarket | |
| hancom | 한글과컴퓨터 | HTML Paged | hancomPaged | |
| hyperconnect | 하이퍼커넥트 | RSS | hyperconnect | |
| kakao | 카카오 | RSS | kakao | |
| kakaobank | 카카오뱅크 | RSS | kakaobank | |
| kakaomobility | 카카오모빌리티 | HTML | kakaomobility | |
| kakaopay | 카카오페이 | RSS | kakaopay | |
| kakaostyle | 카카오스타일 | RSS | kakaostyle | |
| kurly | 컬리 | HTML Paged | kurly | |
| line | 라인 | RSS | line | |
| lotteon | 롯데ON | RSS (Medium) | lotteon | proxy 필요 |
| musinsa | 무신사 | RSS (Medium) | musinsa | proxy 필요 |
| myrealtrip | 마이리얼트립 | RSS (Medium) | myrealtrip | proxy 필요 |
| naver | 네이버 | naverCrawler | naver | |
| netmarble | 넷마블 | RSS | netmarble | |
| nhn | NHN | nhnCrawler | nhn | |
| nongshim | 농심데이터시스템 | RSS | nongshim | |
| oliveyoung | 올리브영 | RSS | oliveyoung | |
| petfriends | 펫프렌즈 | RSS (Medium) | petfriends | proxy 필요 |
| ridi | 리디 | HTML Paged | ridi | |
| samsung | 삼성전자 | HTML Paged | samsungPaged | |
| saramin | 사람인 | RSS | saramin | |
| skplanet | SK플래닛 | skplanetExtractor | skplanet | |
| socar | 쏘카 | HTML Paged | socar | |
| spoqa | 스포카 | HTML | spoqa | |
| swing | 더스윙 | RSS | swing | |
| toss | 토스 | RSS | toss | |
| watcha | 왓챠 | RSS (Medium) | watcha | proxy 필요 |
| wanted | 원티드 | RSS (Medium) | wanted | proxy 필요 |
| woowahan | 우아한형제들 | RSS (CF Worker 프록시) | woowahan | Cloudflare 차단 → Worker 프록시 경유 |
| yanolja | 야놀자 | RSS (Medium) | yanolja | proxy 필요 |
| yogiyo | 요기요 | RSS (Medium) | yogiyo | proxy 필요 |
| zum | ZUM | RSS (Jekyll) | zum | |
| class101 | 클래스101 | RSS (Medium) | class101 | proxy 필요 |
| cloudmt | 안랩클라우드메이트 | RSS (Ghost) | cloudmt | 도메인: techblog.ahnlabcloudmate.com |
| estsoft | 이스트소프트 | RSS (Ghost) | estsoft | |
| kakaoent | 카카오엔터프라이즈 | RSS (Tistory) | kakaoent | |

## 비활성화 소스

| source | 한글명 | 이유 | 비활성화 일시 |
|--------|--------|------|-------------|
| ab180 | AB180 | 상세 페이지에 pubDate 없음. Notion 기반이라 표준 메타 태그 미지원 | 2026-04-01 |
| hwahae | 화해 | 비활성화 상태 (미확인) | 이전 |

## 삭제된 소스

| source | 한글명 | 이유 | 삭제 일시 |
|--------|--------|------|----------|
| coupang | 쿠팡 | 최신 글 2024-10. 사실상 비활성 블로그 | 2026-04-02 |
| zigbang | 직방 | 최신 글 2023-12. 완전 비활성 블로그 | 2026-04-02 |
| kakaomobility (RSS) | 카카오모빌리티 | RSS URL(techblogs.xml) 404. HTML 크롤링으로 전환 | 2026-04-01 |

## 참고: Medium 기반 소스 프록시 설정

Medium은 서버 IP에서 403 차단됨. metadatagenerator의 `parse_props.props`에 proxy 설정 필요:
```json
{"proxy": "https://damp-wind-ae04.sksjsksh32-2cc.workers.dev/?url="}
```
해당 소스: 29cm, class101, daangn, encarai, gccompany, lotteon, musinsa, myrealtrip, petfriends, wanted, watcha, yanolja, yogiyo

## 참고: Cloudflare 차단 소스

woowahan은 서버 IP가 Cloudflare에서 완전 차단. collector target URL을 Worker 프록시 경유로 설정:
```
https://damp-wind-ae04.sksjsksh32-2cc.workers.dev/?url=https://techblog.woowahan.com/feed/
```
