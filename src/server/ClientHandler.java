package server;

import common.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    // 현재 클라이언트의 연결
    private final Socket socket;

    // 현재 사용자의 닉네임
    private final String nickname;

    // 클라이언트에서 메시지를 읽음
    private final BufferedReader reader;

    // 현재 클라이언트에게 메시지를 보냄
    private final PrintWriter writer;

    // 상대방에게 메시지를 보냄
    private final PrintWriter otherWriter;

    public ClientHandler(
            Socket socket,
            String nickname,
            BufferedReader reader,
            PrintWriter writer,
            PrintWriter otherWriter) {

        this.socket = socket;
        this.nickname = nickname;
        this.reader = reader;
        this.writer = writer;
        this.otherWriter = otherWriter;
    }

    @Override
    public void run() {

        try {
            String content;

            // 클라이언트가 보내는 메시지를 계속 읽음
            while ((content = reader.readLine()) != null) {

                // /quit 입력 시 채팅 종료
                if (content.equals("/quit")) {

                    // 상대방에게 퇴장 알림
                    otherWriter.println(nickname + "님이 퇴장했습니다.");

                    break;
                }

                // 메시지 객체 생성
                Message message = new Message(
                        nickname,
                        content
                );

                // 서버에 채팅 기록 저장
                ChatLog.save(message);

                // 자기 자신에게 메시지 표시
                writer.println(message);

                // 상대방에게 메시지 전달
                otherWriter.println(message);
            }

        } catch (IOException e) {
            System.out.println(nickname + "님의 연결이 종료되었습니다.");

        } finally {
            // 종료 시 자원 정리
            closeResources();
        }
    }

    // Socket과 Stream 자원 정리
    private void closeResources() {

        try {
            if (reader != null) {
                reader.close();
            }

            if (writer != null) {
                writer.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {
            System.err.println("연결 종료 중 오류가 발생했습니다.");
        }
    }
}