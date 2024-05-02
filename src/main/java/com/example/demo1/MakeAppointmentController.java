package com.example.demo1;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MakeAppointmentController implements Initializable {
    @FXML
    private ScrollPane buttonContainer;
    @FXML
    private ListView<String> Added;
    ArrayList<String> selectedServices = new ArrayList<>();
    @FXML
    TextField phone;
    @FXML
    TextField name;
    @FXML
    DatePicker date;
    @FXML
    TextField price;
    @FXML
    TextField tips;
    String times[]={"AM","PM"};
    @FXML
    ChoiceBox<String>ch;
    @FXML
    TextField timeH;
    @FXML
    TextField timeM;
    @FXML
    private Button deleteButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadWrite dataSet=new ReadWrite();
        ArrayList<String>services=dataSet.readServices("data.json");
        Collections.sort(services);
        createButtons(services);
        ch.getItems().addAll(times);
//        Added.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
//                String clicked = Added.getSelectionModel().getSelectedItem();
//                selectedServices.remove(clicked);
//                Added.getItems().clear();
//                Added.getItems().setAll(selectedServices);
//                //System.out.println(selectedServices);
//            }
//        });
        deleteButton.setOnAction(event -> handleDeleteButton());
    }
    private void handleDeleteButton() {
        String selectedItem = Added.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedServices.remove(selectedItem);
            Added.getItems().setAll(selectedServices);
        }
    }

    private void createButtons(ArrayList<String> buttonTexts) {
        FlowPane buttonFlowPane = new FlowPane();
        buttonFlowPane.setVgap(5);

        for (String text : buttonTexts) {
            Button button = new Button(text);
            button.getStyleClass().add("service-button");
            button.setOnAction(e -> handleButtonClick(text));
            buttonFlowPane.getChildren().add(button);
        }
        buttonContainer.setContent(buttonFlowPane);
    }
    private void handleButtonClick(String text) {
        normalizeAdded();
        System.out.println("Button clicked: " + text);
        if (!selectedServices.contains(text)) {
            selectedServices.add(text);
        }
        Added.getItems().clear();
        Added.getItems().setAll(selectedServices);
    }
    public void search(){
        ReadWrite rw=new ReadWrite();
        HashMap<String,ClientCard>mp=rw.readClients("data.json");
        System.out.println(mp);
        if(mp.containsKey(phone.getText())){
            name.setText(mp.get(phone.getText()).getName());
        }
        else{
            name.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        }
    }
    public void normalizeName(){
        name.setStyle("");
    }
    public void normalizePhone(){
        phone.setStyle("");
    }
    public void normalizeDate(){
        date.setStyle("");
    }
    public void normalizeAdded(){
        Added.setStyle("");
    }
    public void normalizeTimeH(){
        timeH.setStyle("");
    }
    public void normalizeTimeM(){
        timeM.setStyle("");
    }
    public void normalizeCh(){
        ch.setStyle("");
    }
    public void normalizePrice(){
        price.setStyle("");
    }
    public void onSubmit(){
        LocalDate selectedDate = date.getValue();
        if(name.getText().length()==0 || phone.getText().length()==0 || selectedDate==null || price.getText().length()==0 || selectedServices.size()==0 || timeH.getText().length()==0 || timeM.getText().length()==0 || ch.getValue()==null || ch.getValue().equals("")){
            if(name.getText().length()==0){
                name.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(selectedServices.size()==0){
                Added.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(phone.getText().length()==0){
                phone.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(price.getText().length()==0){
                price.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(timeH.getText().length()==0){
                timeH.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(timeM.getText().length()==0){
                timeM.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(ch.getValue()==null || ch.getValue().equals("" )){
                ch.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else{
                date.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
        }
        else{
            //System.out.println(price.getText());
            ReadWrite rw=new ReadWrite();
            ArrayList<Appointment> x=rw.readBookings("data.json");
            String dateString = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Appointment newApp=new Appointment();
            String timeStr=timeH.getText()+":"+timeM.getText()+" "+ch.getValue();
            System.out.println("hi "+ timeStr);
            newApp.setTime(timeStr);
            newApp.setDate(dateString);
            newApp.setName(name.getText());
            newApp.setNumber(phone.getText());
            newApp.setSelectedServices(selectedServices);
            newApp.setPrice(Integer.parseInt(price.getText()));
            newApp.setId(timeStr+dateString+phone.getText());
            if(tips.getText().length()==0){
                newApp.setTip(0);
            }
            else{
                newApp.setTip(Integer.parseInt(tips.getText()));
            }

            HashMap<String,ClientCard>mp=rw.readClients("data.json");
            if(mp.containsKey(phone.getText())){
                ClientCard c=mp.get(phone.getText());
                c.getVisits().add(newApp);
                mp.put(phone.getText(),c);
                rw.writeClients("data.json",mp);
            }
            else{
                ClientCard c=new ClientCard();
                c.setVisits(new ArrayList<>());
                c.getVisits().add(newApp);
                c.setName(name.getText());
                c.setNumber(phone.getText());
                mp.put(phone.getText(),c);
                rw.writeClients("data.json",mp);
            }
            phone.setText("");
            name.setText("");
            price.setText("");
            tips.setText("");
            timeM.setText("");
            timeH.setText("");
            ch.setValue("");
            //System.out.println(newApp+" yoo");


            x.add(newApp);
            rw.writeBookings("data.json",x);
            selectedServices.clear();
            Added.getItems().clear();
        }
    }

}
