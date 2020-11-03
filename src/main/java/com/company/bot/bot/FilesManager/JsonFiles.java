package com.company.bot.bot.FilesManager;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonFiles {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public File getFile(String token, Update update){
        try {

            JSONObject json = readJsonFromUrl("https://api.telegram.org/bot" + token + "/getFile?file_id=" + update.getMessage().getDocument().getFileId()); // reading stored files on
                                                                                                                  // telegram servers
            String namePath = json.getJSONObject("result").getString("file_path"); // get Name Path from JSON

            URL url = new URL("https://api.telegram.org/file/bot" + token + "/" + namePath);
            File newFile = new File("example");

            FileUtils.copyURLToFile(url, newFile); // download our file

            return newFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
