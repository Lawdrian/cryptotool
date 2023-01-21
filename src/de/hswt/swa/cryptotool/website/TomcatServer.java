package de.hswt.swa.cryptotool.website;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;

public class TomcatServer {


    public static void main(String[] args) throws LifecycleException, ServletException {

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(8070);

        String contextPath = "";
        String docBase = new File("").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);
        String servletName = "Servlet1";
        String urlPattern = "/go";

        CryptoServlet servlet = new CryptoServlet();
        tomcat.addServlet(contextPath, servletName, servlet);
        context.addServletMappingDecoded(urlPattern, servletName);


        // configure the server
        // configure web applications

        tomcat.start();
        tomcat.getServer().await();
    }

}
