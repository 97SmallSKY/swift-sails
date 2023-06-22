package com.m3u8.download.video.m3u8.utils;

/**
 * @author Small_Tsk
 * @create 2023-06-14
 **/
import java.io.IOException;
import java.net.ServerSocket;

public class PortFinder {
    public static int findAvailablePort(int startPort) {
        int port = startPort;
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                // the port is available
                return port;
            } catch (IOException e) {
                // the port is not available, try the next one
                port++;
            }
        }
    }

    public static void main(String[] args) {
        int startPort = 8000;
        int port = findAvailablePort(startPort);
        System.out.println("Found available port: " + port);
    }
}

