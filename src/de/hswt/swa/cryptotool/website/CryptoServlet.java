package de.hswt.swa.cryptotool.website;

import de.hswt.swa.cryptotool.socket.ConnectionState;
import de.hswt.swa.cryptotool.tools.CryptoTool;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class CryptoServlet  extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("doGet called");
        // Forward to the jsp page
        request.setAttribute("output", "");
        request.setAttribute("error", "");
        System.out.println("Context path: " + this.getServletContext().getContextPath());
        System.out.println(this.getServletContext().getRealPath("/Crypto.jsp"));
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/Crypto.jsp");
        System.out.println(dispatcher);
        dispatcher.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("doPost called");

        String method = request.getParameter("method");
        String input = request.getParameter("input");
        String password = request.getParameter("password");
        String output = "";
        String error = null;
        CryptoTool cryptoTool = new CryptoTool();
        if (method.equals("encrypt")) {
            try {
                ByteArrayOutputStream outByte = new ByteArrayOutputStream();
                boolean successfulEncrypt = cryptoTool.encrypt(outByte, input.getBytes(), password);
                if (successfulEncrypt) {
                    output = Base64.getEncoder().encodeToString(outByte.toByteArray());
                    System.out.println("encrypt: " + output);
                    outByte.close();
                } else {
                    error = "Text could not be encrypted.";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (method.equals("decrypt")) {
            try {
                byte[] bytes = Base64.getDecoder().decode(input);
                InputStream is = new ByteArrayInputStream(bytes);
                byte[] plainText = cryptoTool.decrypt(is, password);
                is.close();
                if (plainText != null) {
                    output = new String(plainText);
                } else {
                    error = "Text could not be decrypted.";
                }
                System.out.println("decrypt: " + output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Forward to the jsp page
        request.setAttribute("output", output);
        request.setAttribute("error", error);
        System.out.println("Context path: " + this.getServletContext().getContextPath());

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/Crypto.jsp");
        System.out.println(dispatcher);
        dispatcher.forward(request, response);
    }

}
