package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;

public class Controller {
    @FXML
    private Circle myCircle;
    private double x;
    private double y;
    public void Up(ActionEvent e){
        myCircle.setCenterY(y-=1);
    }
    public void Down(ActionEvent e){
        myCircle.setCenterY(y+=1);
    }
    public void Left(ActionEvent e){
        myCircle.setCenterX(x-=1);
    }
    public void Right(ActionEvent e){
        myCircle.setCenterX(x+=1);
    }
}
