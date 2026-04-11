package com.tinhnguyenxanh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listens to application startup and displays the server port information.
 * This allows for flexible port allocation (e.g., 8080 + 1 if port is unavailable).
 */
@Component
public class ServerStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    
    @Value("${server.port:8080}")
    private String port;
    
    private static boolean alreadyLogged = false;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!alreadyLogged) {
            displayStartupMessage(port);
            alreadyLogged = true;
        }
    }
    
    private void displayStartupMessage(String port) {
        String message = String.format(
            "\n" +
            "╔════════════════════════════════════════════════════════════╗\n" +
            "║                                                            ║\n" +
            "║   ✓ Server running on http://localhost:%s                ║\n" +
            "║                                                            ║\n" +
            "║   Open your browser and navigate to the above URL         ║\n" +
            "║                                                            ║\n" +
            "╚════════════════════════════════════════════════════════════╝\n",
            port
        );
        
        System.out.println(message);
    }
}
