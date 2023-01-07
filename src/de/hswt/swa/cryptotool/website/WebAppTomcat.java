package de.hswt.swa.cryptotool.website;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;

public class WebAppTomcat {


    public static void main(String[] args) throws LifecycleException, ServletException {

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(8080);

        String contextPath = "/";
        String webappDir = new File("./src/de/hswt/swa/cryptotool/website/WebContent").getAbsolutePath();

        tomcat.addWebapp(contextPath, webappDir);


        // configure the server
        // configure web applications

        tomcat.start();
        tomcat.getServer().await();
    }

}
