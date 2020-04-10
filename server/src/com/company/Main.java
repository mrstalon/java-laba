package com.company;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {

    private static Connection connection;
    private static ServerSocket serverSock;
    private static Socket clientSock;

    public static int clientNum = 0;

    public static void main(String args[]) {
        try {
            try {
                String userName = "root";
                String password = "1111";
                String connectionUrl = "jdbc:mysql://localhost:3306/Countries";
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(connectionUrl, userName, password);
                System.out.println("Соединение с базой данных установлено.");

                System.out.println("Ожидание запроса на подключение...");
                while (true) {
                    ServerSocket serverSock = new ServerSocket(1230);
                    Socket clientSock = serverSock.accept();
                    clientNum += 1;
                    clientSock.getPort();
                    System.out.println("Клиент " + clientNum + " подключён.");
                    MyThread myThread = new MyThread(connection, clientSock);
                    myThread.start();
                }
            } finally {
                serverSock.close();
                System.out.println("Сервер закрыт.");
            }
        } catch (Exception e) {
            System.out.println("Error " + e.toString());
        }

    }
}