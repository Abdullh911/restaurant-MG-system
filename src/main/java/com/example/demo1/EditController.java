package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class EditController implements Initializable {

    @FXML
    private DatePicker editDate;
    @FXML
    private TextField editTimeH;
    @FXML
    private TextField editTimeM;
    @FXML
    private TextField editPrice;
    @FXML
    private TextField editTips;
    @FXML
    private ListView<String> available;
    @FXML
    private ListView<String> added;
    ArrayList<String>selected;
    @FXML
    ChoiceBox<String>editAmPm;
    Appointment oldApp;
    private AppointmentListController appointmentListController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selected = new ArrayList<>();
        available.setOnMouseClicked(event -> handleAvailableListViewClick());
        //added.setOnMouseClicked(event -> handleAddedListViewClick());
    }

    private void handleAvailableListViewClick() {
        String selectedItem = available.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selected.contains(selectedItem)) {
            selected.add(selectedItem);
            added.getItems().setAll(selected);
        }
    }
    @FXML
    private void deleteSelectedItemFromAdded() {
        String selectedItem = added.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selected.remove(selectedItem);
            added.getItems().setAll(selected);
        }
    }
    private void handleAddedListViewClick() {
        String selectedItem = added.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selected.remove(selected.indexOf(selectedItem));
            added.getItems().setAll(selected);
        }
    }

    public void initData(Appointment appointment,AppointmentListController appointmentListController) {
        this.appointmentListController = appointmentListController;
        String[] parts = appointment.getTime().split(":");
        editTimeH.setText(parts[0]);
        String[] minutesParts = parts[1].split(" ");
        editTimeM.setText(minutesParts[0]);
        editPrice.setText(String.valueOf(appointment.getPrice()));
        editTips.setText(String.valueOf(appointment.getTip()));
        added.getItems().setAll(appointment.getSelectedServices());
        ReadWrite rw=new ReadWrite();
        ArrayList<String>services=rw.readServices("data.json");
        available.getItems().setAll(services);
        selected=new ArrayList<>(appointment.getSelectedServices());
        String times[]={"Am","Pm"};
        editAmPm.getItems().setAll(times);
        String amPm = minutesParts[1];
        editAmPm.setValue(amPm);
        LocalDate appointmentDate = LocalDate.parse(appointment.getDate(), DateTimeFormatter.ISO_DATE);
        editDate.setValue(appointmentDate);
        this.oldApp=appointment;
    }
    public void submit(){
        LocalDate selectedDate = editDate.getValue();
        if(selectedDate==null || editPrice.getText().length()==0 || selected.size()==0 || editTimeH.getText().length()==0 || editTimeM.getText().length()==0 || editAmPm.getValue()==null || editAmPm.getValue().equals("")){
            if(selected.size()==0){
                added.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(editPrice.getText().length()==0){
                editPrice.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(editTimeH.getText().length()==0){
                editTimeH.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(editTimeM.getText().length()==0){
                editTimeM.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else if(editAmPm.getValue()==null || editAmPm.getValue().equals("" )){
                editAmPm.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            else{
                editDate.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
        }
        else{
            Appointment editedApp=new Appointment();
            String dateString = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            System.out.println("ahhhhh "+dateString);
            editedApp.setDate(dateString);
            editedApp.setId(oldApp.getId());
            editedApp.setTip(Integer.parseInt(editTips.getText()));
            String timeStr=editTimeH.getText()+":"+editTimeM.getText()+" "+editAmPm.getValue();
            editedApp.setTime(timeStr);
            editedApp.setName(oldApp.getName());
            editedApp.setNumber(oldApp.getNumber());
            editedApp.setSelectedServices(selected);
            editedApp.setPrice(Integer.parseInt(editPrice.getText()));
            ReadWrite rw=new ReadWrite();
            ArrayList<Appointment>apps=rw.readBookings("data.json");
            for(int i=0;i<apps.size();i++){
                if(apps.get(i).getId().equals(oldApp.getId())){
                    apps.set(i,editedApp);
                    break;
                }
            }
            HashMap<String,ClientCard>clients=rw.readClients("data.json");
            for(int i=0;i<clients.get(oldApp.getNumber()).getVisits().size();i++){
                if(clients.get(oldApp.getNumber()).getVisits().get(i).getId().equals(oldApp.getId())){
                    clients.get(oldApp.getNumber()).getVisits().set(i,editedApp);
                    break;
                }
            }
            rw.writeClients("data.json",clients);
            rw.writeBookings("data.json",apps);
            //SceneController x=new SceneController();
            Stage stage = (Stage) editDate.getScene().getWindow();
            stage.close();
            if (appointmentListController != null) {
                appointmentListController.refreshTable();
            }
        }
    }
    public void delete(){
        ReadWrite rw=new ReadWrite();
        ArrayList<Appointment>apps=rw.readBookings("data.json");
        HashMap<String,ClientCard>clients=rw.readClients("data.json");
        for(int i=0;i<apps.size();i++){
            if(apps.get(i).getId().equals(oldApp.getId())){
                apps.remove(i);
                break;
            }
        }
        for(int i=0;i<clients.get(oldApp.getNumber()).getVisits().size();i++){
            if(clients.get(oldApp.getNumber()).getVisits().get(i).getId().equals(oldApp.getId())){
                clients.get(oldApp.getNumber()).getVisits().remove(i);
                break;
            }
        }
        rw.writeClients("data.json",clients);
        rw.writeBookings("data.json",apps);
        //SceneController x=new SceneController();
        Stage stage = (Stage) editDate.getScene().getWindow();
        stage.close();
        if (appointmentListController != null) {
            appointmentListController.refreshTable();
        }

    }

}
