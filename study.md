# Java Chat Study

`java-chat`을 만들면서 사용한 내용들을 간단하게 정리했다.

## 1. 구조

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

- `client` : 클라이언트와 GUI
- `common` : 메시지 객체
- `server` : 접속 처리, 메시지 전달, 로그 저장

## 2. 동작 방식

Server가 `5000`번 포트에서 클라이언트 접속을 기다린다.

```text
Client 1 ──┐
           ├─→ Server ─→ Client 1
Client 2 ──┘             └→ Client 2
```

두 명이 접속하면 각각 `ClientHandler`를 Thread로 실행한다.

메시지를 보내면:

```text
Client
  ↓
Socket
  ↓
Server
  ↓
ClientHandler
  ├─ Message 생성
  ├─ chat.log 저장
  ├─ 자기 자신에게 전송
  └─ 상대방에게 전송
```

## 3. Socket과 I/O

`ServerSocket`으로 접속을 받고, `Socket`으로 클라이언트와 통신한다.

```java
Socket client = serverSocket.accept();
```

`accept()`는 클라이언트가 접속할 때까지 기다린다.

메시지를 받을 때는 `BufferedReader`, 보낼 때는 `PrintWriter`를 사용한다.

```java
String message = reader.readLine();
writer.println(message);
```

## 4. 동시성

두 Client의 통신을 각각 Thread에서 처리한다.

```text
Client 1
   ↓
Thread 1

Client 2
   ↓
Thread 2
```

Client에서도 서버의 메시지를 받는 수신 Thread를 따로 실행한다.

여러 Thread가 채팅 로그를 동시에 저장할 수 있기 때문에 `ChatLog.save()`는 `synchronized`로 처리했다.

```java
public static synchronized void save(Message message)
```

Server에서는 `join()`을 사용해 두 채팅 Thread가 끝날 때까지 기다린다.

```java
t1.join();
t2.join();
```

- `Thread` → 여러 Client의 통신을 동시에 처리
- `synchronized` → 로그 저장 시 동시 접근 제어
- `join()` → 채팅 Thread가 끝날 때까지 대기

## 5. Message

메시지를 `String` 하나로만 다루지 않고 닉네임, 내용, 시간을 `Message` 객체로 묶었다.

```java
Message message = new Message(nickname, content);
```

`Message`는 `final` 클래스이고 필드도 `final`로 선언해서 만든 뒤 값을 바꾸지 않는 구조로 했다.

출력 형식은 `toString()`에서 정한다.

```text
[23:50:12] User1: 안녕하세요
```

## 6. 채팅 로그

받은 메시지는 서버에서 `chat.log`에 추가한다.

```java
ChatLog.save(message);
```

기존 내용을 유지하면서 뒤에 계속 추가하도록 `FileWriter`의 append 모드를 사용했다.

## 7. GUI

Swing으로 채팅 화면을 만들었다.

- `JTextArea` : 채팅 내용
- `JTextField` : 메시지 입력
- `JButton` : 전송

메시지 수신은 별도 Thread에서 처리하고, 화면에 추가할 때 `SwingUtilities.invokeLater()`를 사용했다.

## 8. 종료 처리

`/quit`을 입력하거나 창을 닫으면 상대방에게 퇴장 알림을 보내고 Socket과 Stream을 정리한다.

Client와 `ClientHandler`의 `finally`에서도 자원을 정리하도록 했다.

## 9. 실행 순서

```text
1. Server.java 실행
2. Client.java 실행
3. Client.java 한 번 더 실행
4. User1 / User2 닉네임 입력
5. 채팅 시작
```

현재는 `localhost`를 사용해서 같은 컴퓨터에서 테스트하는 방식이다.

## 10. 정리

처음에는 콘솔 채팅으로 시작한 뒤 닉네임, 로그 저장, GUI 등을 추가했다.

이번 프로젝트에서는 Java Socket 통신, Thread와 동시성, 객체지향 구조, 파일 I/O, Swing GUI를 한 번에 사용해봤다.
