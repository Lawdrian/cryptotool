package de.hswt.swa.cryptotool.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.hswt.swa.cryptotool.data.Crypto;
import de.hswt.swa.cryptotool.utils.CryptoTool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.Scanner;

/**
 * @author AdrianWild
 * @version 1.0
 */
public class CryptoApiService extends HttpServlet {

    /**
     * This method provides a REST API POST endpoint. It encrypts or decrypts the content of a crypto object that has
     * been sent as a JSON string.
     *
     * @param request Request object
     * @param response Response object,
     * @throws ServletException Servlet error.
     * @throws IOException Invalid password.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CryptoTool cryptoTool = new CryptoTool();
        try {
            Scanner scanner = new Scanner(request.getInputStream());
            String json = scanner.nextLine();

            Gson gson = new Gson();
            JsonObject requestObj = gson.fromJson(json, com.google.gson.JsonObject.class);
            JsonObject responseObj = new JsonObject();
            String content = requestObj.get("content").getAsString();
            String method = requestObj.get("method").getAsString();
            Crypto crypto = gson.fromJson(content, Crypto.class);

            if (method.equals("encrypt")) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    // encrypt the plain text with the password and save the encrypted text in the cipher variable
                    boolean successfulEncrypt = cryptoTool.encrypt(out, crypto.getPlainText().getBytes(), crypto.getPassword());

                    if (successfulEncrypt) {
                        String s = Base64.getEncoder().encodeToString(out.toByteArray());
                        crypto.setCipher(s);
                    } else {
                        throw new IOException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    crypto.setCipher(null);
                    responseObj.addProperty("error", "invalid password");
                } finally {
                    String result = gson.toJson(crypto);
                    responseObj.addProperty("content", result);
                }
            } else if (method.equals("decrypt")) {
                try {
                    // decrypt the cipher with the given password and save the plain text in the plainText variable
                    byte[] bytes = Base64.getDecoder().decode(crypto.getCipher());
                    InputStream is = new ByteArrayInputStream(bytes);
                    byte[] plainText = cryptoTool.decrypt(is, crypto.getPassword());
                    crypto.setPlainText(new String(plainText));
                } catch (Exception e) {
                    e.printStackTrace();
                    crypto.setPlainText(null);
                    responseObj.addProperty("error", "wrong password");
                } finally {
                    String result = gson.toJson(crypto);
                    responseObj.addProperty("content", result);
                }
            } else {
                responseObj.addProperty("error", "invalid method");
            }

            // use UTF-8 encoding to be able to correctly send umlauts.
            response.setCharacterEncoding("UTF-8");
            String responseMessage = gson.toJson(responseObj);
            PrintWriter writer = response.getWriter();
            writer.println(responseMessage);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getOutputStream().println(e.toString());
        }
    }
}
