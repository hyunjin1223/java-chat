package client;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ChatWindow window;

    // 이미 자원을 정리했는지 확인
    private boolean closed;

    public static void main(String[] args) {
        // Swing GUI는 EDT에서 실행
        SwingUtilities.invokeLater(() -> {
            Client client = new Client();
            client.start();
        });
    }

    private void start() {

        // 닉네임 입력
        String nickname = JOptionPane.showInputDialog(
                null,
                "닉네임을 입력하세요."
        );

        // 입력 취소 또는 빈 값이면 종료
        if (nickname == null || nickname.trim().isEmpty()) {
            return;
        }

        nickname = nickname.trim();

        try {
            // 서버에 연결
            socket = new Socket("localhost", 5000);

            // 서버에서 오는 메시지를 읽음
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // 서버로 메시지를 보냄
            writer = new PrintWriter(
                    socket.getOutputStream(), true);

            // 서버에 닉네임 전송
            writer.println(nickname);

            // 채팅 창 생성
            window = new ChatWindow(
                    nickname,

                    // 메시지 전송
                    this::sendMessage,

                    // 창 닫기
                    this::closeFromWindow
            );

            window.setVisible(true);
            window.focusInput();

            // 서버에서 오는 메시지를 받음
            startReceiver();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "서버에 연결할 수 없습니다.",
                    "연결 오류",
                    JOptionPane.ERROR_MESSAGE
            );

            closeResources();
        }
    }

    // 서버에 메시지를 전송
    private void sendMessage(String message) {
        if (writer == null) {
            return;
        }

        writer.println(message);

        // /quit 입력 시 연결 종료
        if (message.equals("/quit")) {
            closeResources();

            if (window != null) {
                window.dispose();
            }
        }
    }

    // 창을 닫았을 때 실행
    private void closeFromWindow() {
        if (writer != null && !closed) {
            writer.println("/quit");
        }

        closeResources();
    }

    // 서버에서 메시지를 받는 Thread
    private void startReceiver() {

        Thread receiver = new Thread(() -> {

            try {
                String message;

                // 서버에서 메시지를 계속 받음
                while ((message = reader.readLine()) != null) {

                    if (window != null) {
                        window.addMessage(message);
                    }
                }

            } catch (IOException e) {
                // 연결 종료 과정에서 발생하는 예외는 별도 처리하지 않음
            } finally {
                closeResources();

                // 연결이 종료되면 창도 닫음
                SwingUtilities.invokeLater(() -> {
                    if (window != null && window.isDisplayable()) {
                        window.dispose();
                    }
                });
            }
        });

        receiver.start();
    }

    // Socket과 Stream 자원 정리
    private synchronized void closeResources() {

        // 이미 정리했다면 다시 실행하지 않음
        if (closed) {
            return;
        }

        closed = true;

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