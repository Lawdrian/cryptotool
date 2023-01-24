package de.hswt.swa.cryptotool.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

/**
 * @author AdrianWild
 * @version 1.0
 */
public class CryptoApiClient {

    /**
     * This method calls a REST API endpoint with a POST request to either encrypt or decrypt the content of a crypto
     * object. It first transforms the object into a JSON string and then sends it to the endpoint with the method type.
     * If everything worked fine, then it receives the updated object as JSON string from the endpoint. It then returns
     * the transformed crypto object.
     * @param hostName Hostname of the endpoint.
     * @param hostSlug URL Slug of the endpoint.
     * @param  port Port of the endpoint.
     * @param  method If encrypt -> send encrypt request, else if decrypt -> send decrypt request.
     * @param  crypto Crypto object, that should be either encrypted or decrypted.
     * @return Updated crypto object.
     * @throws RemoteException Endpoint not reachable.
     * @throws InvalidKeyException Wrong password.
     */
    public static Crypto callCryptoApi(String hostName, String hostSlug, int port, String method, Crypto crypto) throws RemoteException, InvalidKeyException {
        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create();
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

            // check the response code
            if (responseCode > 199 && responseCode < 300) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            }
            String responseString = bufferedReader.readLine();
            System.out.println(requestMessage);
            System.out.println(responseString);
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
