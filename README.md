# Java Chat

Java `Socket`과 Swing을 이용해 만든 간단한 1:1 채팅 프로그램입니다.

## Features

- 1:1 채팅
- 닉네임 설정
- 입장 / 퇴장 알림
- 메시지 시간 표시
- `/quit`으로 채팅 종료
- 채팅 기록 저장
- Swing GUI
- Enter 키 또는 전송 버튼으로 메시지 전송

## Requirements

- JDK 17 이상
- IntelliJ IDEA

## Project Structure

```text
src/
├─ client/
│  ├─ Client.java
│  └─ ChatWindow.java
├─ common/
│  └─ Message.java
└─ server/
   ├─ Server.java
   ├─ ClientHandler.java
   └─ ChatLog.java
```

## How It Works

```text
Client 1 ── Socket ──> Server ── Socket ──> Client 2
                         │
                         ├─ ClientHandler
                         ├─ Message
                         └─ chat.log
```

두 개의 Client가 하나의 Server에 연결됩니다.

Server는 Client의 메시지를 받아 `ClientHandler`에서 처리하고,
`Message` 객체로 만든 뒤 `chat.log`에 저장합니다.
이후 메시지를 보낸 사용자와 상대방 모두에게 전달합니다.

## Run

1. `Server.java`를 실행합니다.
2. `Client.java`를 실행합니다.
3. `Client.java`를 한 번 더 실행합니다.
4. 각 Client에서 닉네임을 입력합니다.
5. 두 명이 접속하면 채팅을 시작합니다.

현재는 `localhost`를 사용해 같은 컴퓨터에서 테스트합니다.

## Example

```text
[23:50:12] User1: 안녕하세요
[23:50:18] User2: 안녕하세요!
```

`/quit`을 입력하면 채팅을 종료하고 상대방에게 퇴장 알림을 보냅니다.

```text
User1님이 퇴장했습니다.
```

## Chat Log

채팅 메시지는 서버의 `chat.log`에 추가됩니다.

```text
[23:50:12] User1: 안녕하세요
[23:50:18] User2: 안녕하세요!
```

## Notes

- Spring Boot 없이 순수 Java로 구현했습니다.
- GUI는 Swing을 사용합니다.
- 기본 서버 포트는 `5000`입니다.
- 자세한 구현 과정은 [`study.md`](study.md)에서 확인할 수 있습니다.
