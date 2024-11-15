package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditDialogController {

    @FXML
    private TextField quantityField;
    private Stage dialogStage;
    private double initialQuantity;
    private Map.Entry<String, Double> selectedItem;
    private Appointment initialApp;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setInitialValues(Map.Entry<String, Double> selectedItem, Appointment app) {
        this.selectedItem = selectedItem;
        this.initialQuantity = selectedItem.getValue();
        this.initialApp=app;
        quantityField.setText(String.valueOf(initialQuantity));
    }
    public double getItemPriceByName(String itemName) {
        ReadWrite rw=new ReadWrite();
        HashMap<String, ArrayList<item>> services=rw.readServices("data.json");
        for (Map.Entry<String, ArrayList<item>> entry : services.entrySet()) {
            for(item itm:entry.getValue()){
                if(itemName.equals(itm.getName())){
                    return itm.getPrice();
                }
            }
        }
        return 0;
    }

    @FXML
    private void saveChanges() {
        double priceOfItem=getItemPriceByName(selectedItem.getKey());
        double newQuantity = Double.parseDouble(quantityField.getText());
        initialApp.setPrice(initialApp.getPrice()-priceOfItem*initialQuantity);

        if (selectedItem != null) {
            selectedItem.setValue(newQuantity);
        }
        initialApp.setPrice(initialApp.getPrice()+newQuantity*priceOfItem);
        initialApp.getSelectedServices().put(selectedItem.getKey(),newQuantity);
        if(newQuantity==0){
            initialApp.getSelectedServices().remove(selectedItem.getKey());
        }
        dialogStage.close();
    }

    @FXML
    private void cancelEdit() {
        dialogStage.close();
    }
}
