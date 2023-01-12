package de.hswt.swa.cryptotool.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.hswt.swa.cryptotool.data.Crypto;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Scanner;

public class CryptoAPI extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("doPost called");

        try {
            Scanner scanner = new Scanner(request.getInputStream());
            String json = scanner.nextLine();
            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(json, com.google.gson.JsonObject.class);
            String content = obj.get("content").getAsString();
            Crypto crypto = gson.fromJson(content, Crypto.class);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }


    }
}
