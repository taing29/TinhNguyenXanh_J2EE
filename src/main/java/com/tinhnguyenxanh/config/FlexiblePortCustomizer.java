package com.tinhnguyenxanh.config;

import org.springframework.boot.ApplicationContextFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Flexible port configuration via ApplicationContextInitializer.
 * This runs very early to find an available port before the server starts.
 */
public class FlexiblePortCustomizer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    private static final int BASE_PORT = 8080;
    private static final int MAX_PORT_ATTEMPTS = 10;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        // Get the current port setting
        String currentPort = environment.getProperty("server.port");
        
        // If port is already set to something other than 8080, skip
        if (currentPort != null && !currentPort.equals("8080")) {
            System.out.println("[Port Manager] Using configured port: " + currentPort);
            return;
        }
        
        // Find an available port
        int availablePort = findAvailablePort(BASE_PORT);
        
        // Set it in the environment
        Map<String, Object> properties = new HashMap<>();
        properties.put("server.port", availablePort);
        
        MapPropertySource propertySource = new MapPropertySource(
            "flexiblePortCustomizer", properties
        );
        
        environment.getPropertySources().addFirst(propertySource);
    }

    /**
     * Finds an available port starting from the base port.
     *
     * @param basePort The base port to start checking from
     * @return An available port number
     */
    private int findAvailablePort(int basePort) {
        for (int i = 0; i < MAX_PORT_ATTEMPTS; i++) {
            int port = basePort + i;
            if (isPortAvailable(port)) {
                System.out.println("[Port Manager] Found available port: " + port);
                return port;
            }
        }
        // If all attempted ports are busy, return base port and let Spring try
        System.out.println("[Port Manager] All ports busy, using fallback port: " + basePort);
        return basePort;
    }

    /**
     * Checks if a port is available.
     *
     * @param port The port to check
     * @return true if the port is available, false otherwise
     */
    private boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

