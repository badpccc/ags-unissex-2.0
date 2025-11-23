package com.example.utils;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramNotifier {

    // Coloque seu token aqui
    private static final String BOT_TOKEN = "8327091559:AAFFqXpibjJ0gZ12t0B5h0uNaXAdd6EVu-8";

    // Seu chat ID (pode ser ID de canal, grupo ou pessoa)
    private static final String CHAT_ID = "1355190696";

    private static void sendMessage(String texto) {
        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN +
                    "/sendMessage?chat_id=" + CHAT_ID +
                    "&parse_mode=Markdown" +
                    "&text=" + URLEncoder.encode(texto, StandardCharsets.UTF_8);

            URL url = URI.create(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.getInputStream();

            System.out.println("Telegram enviado!");
        } catch (Exception e) {
            System.out.println("Falha ao enviar para Telegram: " + e.getMessage());
        }
    }

    // 🔵 Notificação normal
    public static void send(String mensagem) {
        sendMessage("✅ *Notificação*\n\n" + mensagem);
    }

    // 🔴 Notificação de erro
    public static void sendError(String mensagem) {
        sendMessage("❌ *Erro*\n\n" + mensagem);
    }
}
