package com.m3u8.download.video.m3u8.listener;

/**
 * @author Small_Tsk
 * @create 2023-06-14
 **/
import com.m3u8.download.video.m3u8.download.M3u8DownloadFactory;
import com.m3u8.download.video.m3u8.utils.Constant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private int port;

    private String url;

    private String name;

    private String position;

    private JTable jTable1;

    private DefaultTableModel model;

    private List<DownloadListener> listeners;

    public MyServer(int port, String url, String name, String position, JTable table, DefaultTableModel model) {
        this.port = port;
        this.url = url;
        this.name = name;
        this.position = position;
        this.jTable1 = table;
        this.model = model;
    }

    public MyServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(this.port);
        System.out.println("Server is listening on port " + this.port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

            // create a new thread to handle the client connection
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;



        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                // create a new listener for this client connection
                M3u8DownloadFactory.M3u8Download m3u8Download = new M3u8DownloadFactory().getInstance(url);

                //设置生成目录
                m3u8Download.setDir(position);
                //设置视频名称
                m3u8Download.setFileName(name);
                //设置线程数
                m3u8Download.setThreadCount(15);
                //设置重试次数
                m3u8Download.setRetryCount(100);
                //设置连接超时时间（单位：毫秒）
                m3u8Download.setTimeoutMillisecond(10000L);
        /*
        设置日志级别
        可选值：NONE INFO DEBUG ERROR
        */
                m3u8Download.setLogLevel(Constant.INFO);
                //设置监听器间隔（单位：毫秒）
                m3u8Download.setInterval(500L);
                model.addRow(new Object[]{1, "测试", "", ""
                        , "", "", "", ""});

                int rowIndex = jTable1.getRowCount() - 1;

            }catch (Exception e)
            {

            }

        }
    }

    public static void main(String[] args) throws IOException {
        // create multiple instances of MyServer with different port numbers
        MyServer server1 = new MyServer(8000);
        MyServer server2 = new MyServer(8001);
        MyServer server3 = new MyServer(8002);

        // start each instance in a separate thread
        new Thread(() -> {
            try {
                server1.start();
            } catch (IOException e) {
                System.err.println("Error starting server1: " + e.getMessage());
            }
        }).start();

        new Thread(() -> {
            try {
                server2.start();
            } catch (IOException e) {
                System.err.println("Error starting server2: " + e.getMessage());
            }
        }).start();

        new Thread(() -> {
            try {
                server3.start();
            } catch (IOException e) {
                System.err.println("Error starting server3: " + e.getMessage());
            }
        }).start();


    }
}
