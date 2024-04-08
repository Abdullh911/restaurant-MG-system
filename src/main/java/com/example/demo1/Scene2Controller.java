package com.example.demo1;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Scene2Controller implements Initializable {
    @FXML
    private ListView<String>texts;
    @FXML
    private ListView<String>selected;
    @FXML
    private Label myLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String>data=new ArrayList<>();
        ArrayList<String>select=new ArrayList<>();
        data.add("spa");
        data.add("massage");
        texts.getItems().addAll(data);
        selected.getItems().addAll(select);
        texts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String clicked=texts.getSelectionModel().getSelectedItem();
                select.add(clicked);
                selected.getItems().setAll(select);
            }
        });
    }
}
