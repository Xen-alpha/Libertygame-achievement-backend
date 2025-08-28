# Libertygame Achievement Server
[리버티게임](https://libertyga.me) 도전과제 서버입니다. 아직 개발 중이며, 기능 및 안정화가 이루어진 1.0 버전까지 README의 내용이 바뀔 수 있습니다.

저작권은 MIT License에 따라 배포됩니다.

[도커 허브](https://hub.docker.com/r/senouis/libertygame_achievement)를 방문하여 Docker 이미지를 받아가세요!

## 사전 설정 환경(prerequisite)
AMD64 및 ARM64 리눅스 환경을 지원하며, [OAuth 확장 기능](https://www.mediawiki.org/wiki/Extension:OAuth)이 설치된 [미디어위키](https://www.mediawiki.org/wiki/MediaWiki/ko) 서버라면 아마 다 작동할 겁니다.

## API 관련 안내
OAuth2 연동을 한 이후 `(호스트):8000/swagger-ui/index.html`에서 사용 가능한 API 목록을 볼 수 있습니다.

## 설정 가능한 환경 변수(Enviromental Variables)

```
ENV HOST_DOMAIN="dev.libertygame.work"
ENV WALLET_PATH="/root/oracle_wallet"
ENV ORACLE_USERNAME=""
ENV ORACLE_PASSWORD=""
ENV MAIL_ADDR=""
ENV MAIL_PASS=""
ENV OAUTH_CLIENT_ID=""
ENV OAUTH_CLIENT_NAME=""
ENV JWT_SECRET=""
ENV JWT_EXPIRED=86400000
```

<!-- 작성 중 -->
<!-- ## ERD -->
