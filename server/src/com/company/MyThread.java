package com.company;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;

import java.net.*;
import java.io.*;
import java.sql.*;

public class MyThread extends Thread {

    private static Socket clientSock;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Connection connection;
    private static Statement statement;

    public MyThread(Connection connection, Socket clientSock) {
        System.out.println("Новый поток создан...");
        this.connection = connection;
        this.clientSock = clientSock;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Поток разрушен...");
        super.finalize();
    }

    public static boolean isSelectQuery(String str) {
        int pos = str.indexOf(" ");
        String firstWord = str.substring(0, pos);
        return firstWord.equals("SELECT");
    }

    public static String execute(String str) {
        try {
            statement = connection.createStatement();
            statement.execute(str);
            return "";
        }
        catch (Exception e) {
            return "Ошибка! " + e.toString();
        }
    }

    @Override
    public void run() {
        System.out.println("Новый поток запущен...");
        try {
            try {
                while (true) {
                    in = new DataInputStream(clientSock.getInputStream());
                    out = new DataOutputStream(clientSock.getOutputStream());
                    String data = new String();
                    String dataForClient = new String();
                    while (true) {
                        data = in.readUTF();
                        System.out.println(clientSock.toString());
                        if (!data.equals("Finish")) {
                            if (isSelectQuery(data)) {
                                System.out.println("Клиент прислал: \n" + data);
                                int pos = data.indexOf(" ");
                                String stringToEdit = data.substring(pos+1);
                                data = "SELECT * FROM country WHERE Country = '"+stringToEdit+"'";

                                statement = connection.createStatement();
                                ResultSet resultSet = statement.executeQuery(data);
                                while (resultSet.next()) {
                                    int id = resultSet.getInt("Id");
                                    String country = resultSet.getString("Country");
                                    String capital = resultSet.getString("Capital");
                                    double population = resultSet.getDouble("Population");
                                    double territory = resultSet.getDouble("Territory");
                                    String currency = resultSet.getString("Currency");
                                    String form = resultSet.getString("Form");
                                    dataForClient = id + "\n" + country + "\n" + capital + "\n" + population + "\n" + territory + "\n" + currency + "\n" + form;
                                }
                            } else {
//                                if (data.equals("ComboBoxData")) {
//                                    System.out.println("combo");
//                                //    dataForClient = "";
//                                    statement = connection.createStatement();
//                                    ResultSet resultSet = statement.executeQuery("SELECT Country FROM country");
//                                    while (resultSet.next()) {
//                                        dataForClient = dataForClient + resultSet.getString("Country") + "\n";
//                                        System.out.println(dataForClient);
//                                    }
//                                } else {
                                    System.out.println("Клиент прислал: \n" + data);
                                    dataForClient = execute(data);
                                    if (dataForClient.equals("")) {
                                        data = "SELECT * FROM country";

                                        statement = connection.createStatement();
                                        ResultSet resultSet = statement.executeQuery(data);
                                        while (resultSet.next()) {
                                            int id = resultSet.getInt("Id");
                                            String country = resultSet.getString("Country");
                                            String capital = resultSet.getString("Capital");
                                            double population = resultSet.getDouble("Population");
                                            double territory = resultSet.getDouble("Territory");
                                            String currency = resultSet.getString("Currency");
                                            String form = resultSet.getString("Form");
                                            dataForClient = dataForClient + "Id: " + id + " || Country: " + country + " || Capital: " + capital + " || Population: " + population + " || Territory: " + territory + " || Currency: " + currency + " || Form: " + form + "\n===================\n";
                                        }
                                    }
                                //}
                            }
                            out.writeUTF(dataForClient);
                            System.out.println("Отправлено клиенту: \n" + dataForClient);
                            out.flush();
                        } else
                            return;
                    }
                }

            } finally {
                if (statement != null)
                    statement.close();
            }
        } catch (Exception e) {
            System.out.println("Error " + e.toString());
        }
    }
}
