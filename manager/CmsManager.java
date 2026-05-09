import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;

public class CmsManager {
    static final String AGENT_IP = "127.0.0.1";
    static final int PORT = 10293;
    static final String PASSWORD = "1234";
    static final int INTERVAL = 10;

    public static void main(String[] args) {
        try (Socket socket = new Socket(AGENT_IP, PORT)) {
            socket.setSoTimeout(INTERVAL * 3000);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            JsonObject auth = new JsonObject();
            auth.addProperty("password", PASSWORD);
            out.println(auth.toString());

            String response = in.readLine();
            JsonObject authResult = JsonParser.parseString(response).getAsJsonObject();
            if (!authResult.get("auth").getAsString().equals("AUTH_OK")) {
                System.out.println("인증 실패");
                return;
            }
            System.out.println("인증 성공");

            // 3. 수신 루프
            try {
                String json;
                while ((json = in.readLine()) != null) {
                    System.out.println("[수신] " + json);
                }
            } catch (SocketTimeoutException e) {
                System.out.println("접속 시간 초과 (연결 해제됨)");
            }

        } catch (Exception e) {
            System.out.println("연결 종료: " + e.getMessage());
        }
    }
}