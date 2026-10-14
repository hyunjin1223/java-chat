package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        // 5000번 포트에서 클라이언트의 접속을 기다림
        try (ServerSocket serverSocket = new ServerSocket(5000)) {

            System.out.println("서버가 시작되었습니다.");
            System.out.println("두 명의 접속을 기다리는 중...");

            // 첫 번째 클라이언트 접속
            Socket client1 = serverSocket.accept();
            System.out.println("첫 번째 사용자가 접속했습니다.");

            BufferedReader reader1 = new BufferedReader(
                    new InputStreamReader(client1.getInputStream()));

            PrintWriter writer1 = new PrintWriter(client1.getOutputStream(), true);

            // 첫 번째 사용자의 닉네임을 받음
            String nickname1 = reader1.readLine();

            // 두 번째 클라이언트 접속
            Socket client2 = serverSocket.accept();
            System.out.println("두 번째 사용자가 접속했습니다.");

            BufferedReader reader2 = new BufferedReader(
                    new InputStreamReader(client2.getInputStream()));

            PrintWriter writer2 = new PrintWriter(client2.getOutputStream(), true);

            // 두 번째 사용자의 닉네임을 받음
            String nickname2 = reader2.readLine();

            System.out.println("두 명이 모두 접속했습니다.");
            System.out.println("채팅을 시작합니다.");

            // 서로의 입장 알림
            writer1.println(nickname2 + "님이 입장했습니다.");

            writer2.println(nickname1 + "님이 입장했습니다.");

            // 첫 번째 클라이언트 처리
            Thread t1 = new Thread(
                    new ClientHandler(client1, nickname1, reader1, writer1, writer2
                    )
            );

            // 두 번째 클라이언트 처리
            Thread t2 = new Thread(
                    new ClientHandler(client2, nickname2, reader2, writer2, writer1)
            );

            t1.start();
            t2.start();

            // 두 채팅 Thread가 끝날 때까지 대기
            t1.join();
            t2.join();

            System.out.println("채팅 세션이 종료되었습니다.");

        } catch (IOException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            System.out.println("서버 스레드가 중단되었습니다.");

            // 현재 Thread의 중단 상태를 다시 설정
            Thread.currentThread().interrupt();
        }
    }
}