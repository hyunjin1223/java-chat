package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class ChatWindow extends JFrame {

    private final JTextArea chatArea;
    private final JTextField inputField;
    private final Consumer<String> onSend;
    private final Runnable onClose;

    public ChatWindow(
            String nickname,
            Consumer<String> onSend,
            Runnable onClose) {

        this.onSend = onSend;
        this.onClose = onClose;

        setTitle("1:1 Chat - " + nickname);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 채팅 내용을 표시하는 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // 메시지 입력 영역
        JPanel inputPanel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        JButton sendButton = new JButton("전송");

        // 전송 버튼 클릭
        sendButton.addActionListener(e -> sendMessage());

        // Enter 키로 전송
        inputField.addActionListener(e -> sendMessage());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // 창을 닫을 때 실행
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (onClose != null) {
                    onClose.run();
                }
            }
        });
    }

    // Swing EDT에서 메시지 추가
    public void addMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");

            // 최신 메시지가 보이도록 스크롤 이동
            chatArea.setCaretPosition(
                    chatArea.getDocument().getLength());
        });
    }

    // 입력한 메시지를 전송
    private void sendMessage() {
        String message = inputField.getText().trim();

        // 빈 메시지는 보내지 않음
        if (message.isEmpty()) {
            return;
        }

        if (onSend != null) {
            onSend.accept(message);
        }

        inputField.setText("");
    }

    // 입력창에 포커스
    public void focusInput() {
        inputField.requestFocusInWindow();
    }
}