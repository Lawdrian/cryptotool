package de.hswt.swa.cryptotool.website;

import de.hswt.swa.cryptotool.utils.CryptoTool;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * @author AdrianWild
 * @version 1.0
 */
public class CryptoServlet  extends HttpServlet {

    /**
     * This method gets called when a user enters the URL of the endpoint in the browser.
     * It renders an empty Crypto.jsp page
     *
     * @param request Request object
     * @param response Response object,
     * @throws ServletException Servlet error.
     * @throws IOException Error.
     */
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

    /**
     * This method gets called when the Crypto.jsp sends a POST request to the URL of this endpoint.
     * Depending on the method it encrypts or decrypts the sent input text.
     * It then renders the Crypto.jsp with the output set.
     *
     * @param request Request object
     * @param response Response object,
     * @throws ServletException Servlet error.
     * @throws IOException Invalid password.
     */
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
