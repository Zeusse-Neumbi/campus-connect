package com.example.school.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ngrok.Session;
import com.ngrok.Forwarder;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.net.URI;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Starts an ngrok tunnel when the application deploys,
 * making the local server accessible via a public URL.
 *
 * Requires the NGROK_AUTHTOKEN environment variable or `.env` entry to be set.
 */
@WebListener
public class NgrokListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(NgrokListener.class);

    private Session session;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String authtoken = System.getenv("NGROK_AUTHTOKEN");
        
        if (authtoken == null || authtoken.isBlank()) {
            try {
                Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
                authtoken = dotenv.get("NGROK_AUTHTOKEN");
            } catch (Exception e) {
                System.out.println("[ngrok] Failed to load .env file: " + e.getMessage());
            }
        }

        if (authtoken == null || authtoken.isBlank()) {
            System.out.println("[ngrok] NGROK_AUTHTOKEN not set — skipping tunnel.");
            return;
        }

        try {
            session = Session.withAuthtoken(authtoken.trim()).connect();
            Forwarder.Endpoint forwarder = session.httpEndpoint()
                    .forward(URI.create("http://localhost:9091").toURL());
            System.out.println("============================================");
            System.out.println("[ngrok] Public URL: " + forwarder.getUrl());
            System.out.println("============================================");
            sce.getServletContext().setAttribute("ngrokUrl", forwarder.getUrl());
        } catch (Exception e) {
            System.err.println("[ngrok] Failed to start tunnel: " + e.getMessage());
            log.error("An exception occurred: ", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (session != null) {
            try {
                session.close();
                System.out.println("[ngrok] Tunnel closed.");
            } catch (Exception e) {
                System.err.println("[ngrok] Error closing tunnel: " + e.getMessage());
            }
        }
    }
}
