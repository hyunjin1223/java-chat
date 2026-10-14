package common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 메시지를 객체 단위로 관리하는 불변 클래스
public final class Message {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String nickname;
    private final String content;
    private final LocalDateTime time;

    public Message(String nickname, String content) {
        this.nickname = nickname;
        this.content = content;
        this.time = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "[" + time.format(FORMATTER) + "] "
                + nickname + ": " + content;
    }

    public String getNickname() {
        return nickname;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTime() {
        return time;
    }
}