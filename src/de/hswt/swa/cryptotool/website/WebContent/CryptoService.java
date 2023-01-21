package de.hswt.swa.cryptotool.website.WebContent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.hswt.swa.cryptotool.data.Crypto;
import de.hswt.swa.cryptotool.tools.CryptoTool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.Scanner;

public class CryptoService extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CryptoTool cryptoTool = new CryptoTool();
        try {

            Scanner scanner = new Scanner(request.getInputStream());
            String json = scanner.nextLine();

            PrintWriter writer = response.getWriter();

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
            String responseMessage = gson.toJson(responseObj);
            writer.println(responseMessage);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
