package com.gexcat.translate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Traduce un texto desde un idioma a otro.
 * <p>
 * Para obtener la CLAVE DE SERVIDOR que va en main():
 * <p>
 * https://console.cloud.google.com/apis/api/translate.googleapis.com/credentials?project=api-project-579234896733
 * 
 * @author Ravishanker Kusuma
 * 
 */
public class GoogleTranslate {

    private String key;

    public GoogleTranslate(String apiKey) {
        key = apiKey;
    }

    String translte(String text, String from, String to) {
        StringBuilder result = new StringBuilder();
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://www.googleapis.com/language/translate/v2?key=" + key + "&q=" + encodedText
                + "&target=" + to + "&source=" + from;

            URL url = new URL(urlStr);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            InputStream stream;
            if (conn.getResponseCode() == 200) // success
            {
                stream = conn.getInputStream();
            } else
                stream = conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonElement element = JsonParser.parseString(result.toString());
            JsonElement code = null;
            JsonElement message = null;

            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.get("error") == null) {
                    String translatedText = obj.get("data").getAsJsonObject().get("translations").getAsJsonArray().get(
                        0).getAsJsonObject().get("translatedText").getAsString();
                    return translatedText;

                }
                code = obj.get("error").getAsJsonObject().get("code");
                message = obj.get("error").getAsJsonObject().get("message");
            }

            if (conn.getResponseCode() != 200) {
                System.err.println(result);
                return "code=" + code + ", message=" + message;
            }

        } catch (IOException | JsonSyntaxException ex) {
            System.err.println(ex.getMessage());
        }

        return null;
    }

    public static void main(String[] args) {

        GoogleTranslate translator = new GoogleTranslate("AIzaSyBBdgJb6Jl7Vk2NI9D-I9LAIeobkSM3Vms");
        String text = translator.translte("How are you?. I am fine. Thanks!", "en", "ca");
        System.out.println(text);
    }
}