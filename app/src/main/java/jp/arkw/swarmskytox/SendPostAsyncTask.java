package jp.arkw.swarmskytox;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class SendPostAsyncTask extends AsyncTask<SendPostTaskParams, String, String> {
    @Override
    protected String doInBackground(SendPostTaskParams... params) {
        String urlString = params[0].url;
        String postDataParams = params[0].postData;
        StringBuilder response = new StringBuilder();
        try {
            byte[] postDataBytes = postDataParams.getBytes(StandardCharsets.UTF_8);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.getOutputStream().write(postDataBytes);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
            } else {
                response = new StringBuilder();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return response.toString();
    }
}
