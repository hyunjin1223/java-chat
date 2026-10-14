package server;

import common.Message;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ChatLog {

    // 여러 Thread에서 동시에 로그를 저장하지 않도록 동기화
    public static synchronized void save(Message message) {

        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(
                        new FileWriter("chat.log", true)))) {

            // 기존 기록 뒤에 메시지를 추가
            writer.println(message);

        } catch (IOException e) {
            System.err.println("채팅 기록을 저장할 수 없습니다: " + e.getMessage());
        }
    }
}