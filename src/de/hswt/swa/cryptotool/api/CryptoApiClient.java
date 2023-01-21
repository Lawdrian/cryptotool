package de.hswt.swa.cryptotool.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.hswt.swa.cryptotool.data.Crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;

public class CryptoApiClient {

    public static Crypto callCryptoApi(String hostName, String hostSlug, int port, String method, Crypto crypto) throws RemoteException, InvalidKeyException {
        Gson gson = new Gson();
        try {
            String urlSpec = "http://" + hostName + ":" + port + "/" + hostSlug;
            URL url = new URL(urlSpec);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");

            JsonObject jsonObject = new JsonObject();
            String contentJson = gson.toJson(crypto);
            jsonObject.addProperty("method", method);
            jsonObject.addProperty("content", contentJson);
            String requestMessage = gson.toJson(jsonObject);
            PrintWriter out = new PrintWriter(httpConnection.getOutputStream());
            out.println(requestMessage);
            out.close();

            int responseCode = httpConnection.getResponseCode();
            BufferedReader bufferedReader;

            if (responseCode > 199 && responseCode < 300) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            }
            String responseString = bufferedReader.readLine();
            JsonObject responseObj = gson.fromJson(responseString, com.google.gson.JsonObject.class);
            String result = responseObj.get("content").getAsString();
            crypto = gson.fromJson(result, Crypto.class);
            if (responseObj.get("error") != null) {
                // if the server sends an error message, it means that the server couldn't fulfill the request
                String error = responseObj.get("error").getAsString();
                System.out.println("error: " + error);
                throw new InvalidKeyException();
            }
            return crypto;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException();
        }
    }
}
