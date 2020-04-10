package Controllers;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerCS
{
    private static Socket clientSock;
    private static DataInputStream in;
    private static DataOutputStream out;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button connectButton;

    @FXML
    private TextField ipAddrTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextArea resultTextArea;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private ComboBox<String> countryComboBox;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField countryTextField;

    @FXML
    private TextField capitalTextField;

    @FXML
    private TextField populationTextField;

    @FXML
    private TextField territoryTextField;

    @FXML
    private TextField moneyTextField;

    @FXML
    private TextField formTextField;

    @FXML
    private Button saveButton;

    @FXML
    void initialize() {
        disconnectButton.setDisable(true);
        deleteButton.setDisable(true);
        editButton.setDisable(true);
        addButton.setDisable(true);
        saveButton.setDisable(true);

        idTextField.setDisable(true);
        countryTextField.setDisable(true);
        capitalTextField.setDisable(true);
        populationTextField.setDisable(true);
        territoryTextField.setDisable(true);
        moneyTextField.setDisable(true);
        formTextField.setDisable(true);

        countryComboBox.getItems().addAll("Австрия", "Германия", "Италия");

        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    clientSock = new Socket(InetAddress.getByName(ipAddrTextField.getText()), Integer.parseInt(portTextField.getText()));
                    in = new DataInputStream(clientSock.getInputStream());
                    out = new DataOutputStream(clientSock.getOutputStream());
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/View/connectionSuccess.fxml"));
                    loader.load();
                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                    connectButton.setDisable(true);
                    disconnectButton.setDisable(false);
                    deleteButton.setDisable(false);
                    editButton.setDisable(false);
                    addButton.setDisable(false);
                    ipAddrTextField.setFocusTraversable(false);
                    portTextField.setFocusTraversable(false);

//                    String query = "ComboBoxData";
//                    out.writeUTF(query);
//                    out.flush();
//                    String dataFromServer = in.readUTF();
//                    String lineData[];
//                    lineData = dataFromServer.split("\n");
//                    for (int i = 0; i < lineData.length; i++) {
//                        countryComboBox.getItems().add(lineData[i]);
//                    }

                } catch (Exception e) {
                    try {
                        System.out.println("Не удалось подключиться к серверу.");
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/View/connectionFail.fxml"));
                        loader.load();
                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                        if (clientSock != null) {
                            clientSock.close();
                            in.close();
                            out.close();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if (clientSock != null) {
                        out.writeUTF("Finish");
                        out.flush();
                        in.close();
                        out.close();
                        clientSock.close();
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/View/disconnection.fxml"));
                        loader.load();
                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                        disconnectButton.setDisable(true);
                        saveButton.setDisable(true);
                        deleteButton.setDisable(true);
                        editButton.setDisable(true);
                        addButton.setDisable(true);
                        portTextField.setFocusTraversable(false);
                        System.out.println("Соединение с сервером разорвано.");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        deleteButton.setOnAction(event -> {
           if (checkComboBox()) {
               String data = "DELETE FROM country WHERE Country =\'" + countryComboBox.getValue() + "\'";
               try {
                   out.writeUTF(data);
                   out.flush();
                   String dataFromServer = in.readUTF();
                   System.out.println("Получено от сервера: " + dataFromServer);
                   resultTextArea.setText("Результат: \n" + dataFromServer);
                   countryComboBox.getItems().remove(countryComboBox.getValue());
                   countryComboBox.setValue(null);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        });

        editButton.setOnAction(event -> {
            if (checkComboBox()) {
                countryTextField.setDisable(false);
                capitalTextField.setDisable(false);
                populationTextField.setDisable(false);
                territoryTextField.setDisable(false);
                moneyTextField.setDisable(false);
                formTextField.setDisable(false);
                String commandLineToEdit = "SELECT " + countryComboBox.getValue();
                try {
                    out.writeUTF(commandLineToEdit);
                    out.flush();
                    countryComboBox.setValue(null);
                    String dataFromServer = in.readUTF();
                    System.out.println("Получено от сервера: " + dataFromServer);
                    String lineData[];
                    lineData = dataFromServer.split("\n");
                    String id = lineData[0];
                    idTextField.setText(id);
                    String country = lineData[1];
                    countryTextField.setText(country);
                    String capital = lineData[2];
                    capitalTextField.setText(capital);
                    String population = lineData[3];
                    populationTextField.setText(population);
                    String territory = lineData[4];
                    territoryTextField.setText(territory);
                    String money = lineData[5];
                    moneyTextField.setText(money);
                    String form = lineData[6];
                    formTextField.setText(form);
                    countryComboBox.getItems().remove(country);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveButton.setDisable(false);
                deleteButton.setDisable(true);
                addButton.setDisable(true);
            }
        });

        addButton.setOnAction(event -> {
            countryTextField.setDisable(false);
            capitalTextField.setDisable(false);
            populationTextField.setDisable(false);
            territoryTextField.setDisable(false);
            moneyTextField.setDisable(false);
            formTextField.setDisable(false);
            saveButton.setDisable(false);
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        });

        saveButton.setOnAction(event -> {
            String country = countryTextField.getText();
            String capital = capitalTextField.getText();
            String population = populationTextField.getText();
            String territory = territoryTextField.getText();
            String money = moneyTextField.getText();
            String form = formTextField.getText();
            try {
                checkWord(country);
                checkWord(capital);
                checkWord(money);
                checkWord(form);
                checkDigit(population);
                checkDigit(territory);
            } catch (Exception e) {
                try {
                    System.out.println("Неверные данные.");
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/View/wrongData.fxml"));
                    loader.load();
                    Parent root = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                    return;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            String data = new String();
            if (idTextField.getText().isEmpty()) {
                data = "INSERT INTO country (Country, Capital, Population, Territory, Currency, Form) VALUES (\'" + country + "\', \'" + capital + "\', \'" + population + "\', \'" + territory + "\', \'" + money + "\', \'" + form + "\')";
                countryComboBox.getItems().add(country);
                System.out.println("Вы добавили строку: \n" + country + "\t" + capital + "\t" + population + "\t" + territory + "\t" + money + "\t" + form);
            }
            else {
                data = "UPDATE country SET Country = \'"+ country +"\', Capital = \'"+ capital +"\', Population = \'"+ population +"\', Territory = \'"+ territory +"\', Currency = \'"+ money +"\', Form = \'"+ form +"\' WHERE Id = " +idTextField.getText();
                System.out.println("Вы отредактировали строку: \n" + country + "\t" + capital + "\t" + population + "\t" + territory + "\t" + money + "\t" + form);
                countryComboBox.getItems().remove(country);
                countryComboBox.getItems().add(country);
            }

            try {
                out.writeUTF(data);
                out.flush();
                String dataFromServer = in.readUTF();
                System.out.println("Получено от сервера: \n" + dataFromServer);
                resultTextArea.setText("Результат: \n" + dataFromServer);
                idTextField.clear();
                countryTextField.clear();
                capitalTextField.clear();
                populationTextField.clear();
                territoryTextField.clear();
                moneyTextField.clear();
                formTextField.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
            idTextField.setDisable(true);
            countryTextField.setDisable(true);
            capitalTextField.setDisable(true);
            populationTextField.setDisable(true);
            territoryTextField.setDisable(true);
            moneyTextField.setDisable(true);
            formTextField.setDisable(true);
            deleteButton.setDisable(false);
            editButton.setDisable(false);
            addButton.setDisable(false);
            saveButton.setDisable(true);
        });
    }

    private void checkDigit(String data) throws Exception {
        for (int i = 0; i < data.length(); i++) {
            if (!(Character.isDigit(data.charAt(i)) || data.charAt(i)=='.')) {
                throw new Exception();
            }
        }
    }

    private void checkWord(String data) throws Exception {
        for (int i = 0; i < data.length(); i++) {
            if (Character.isDigit(data.charAt(i))) {
                throw new Exception();
            }
        }
    }

    public boolean checkComboBox() {
        if (countryComboBox.getValue() == null) {
            try {
                System.out.println("Неверные данные.");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/View/needToChooseCountry.fxml"));
                loader.load();
                Parent root = loader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.showAndWait();
                return false;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return true;
    }

    public static void closeWindow() {
        try {
            if (!clientSock.isClosed()) {
                out.writeUTF("Finish");
                out.flush();
                in.close();
                out.close();
                clientSock.close();
                System.out.println("Соединение с сервером разорвано.");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }




}
