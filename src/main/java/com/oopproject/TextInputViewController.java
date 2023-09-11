package com.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Objects;

public class TextInputViewController {
    @FXML Label currentDefinition;
    @FXML Label currentField;
    @FXML Label newDefinition;
    @FXML TextField newField;

    TextInputDialog dialog;

    String value = "";
    boolean cancel = false;

    @FXML
    public void initialize() {

    }

    public void setCurrentDefinition(String currentDefinition) {
        this.currentDefinition.setText(currentDefinition);
    }

    public void setCurrentField(String currentField) {
        this.currentField.setText(currentField);
    }

    public void setNewDefinition(String newDefinition) {
        this.newDefinition.setText(newDefinition);
    }

    public void setDialog(TextInputDialog dialog)   {
        this.dialog = dialog;
        dialog.showAndWait();
    }

    public String getValue() throws IllegalAccessException {
        if (cancel) {
            throw new IllegalAccessException("cancel operation");
        }
        return value;
    }

    @FXML
    public void confirmAction() {
        value = newField.getText();
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        dialog.close();
    }

    @FXML
    public void cancelAction() throws Exception {
        value = "";
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        dialog.close();
        cancel = true;
    }

    private void setImage(Button button, String uri){
        ImageView thisImageView =
                new ImageView( new Image(Objects.requireNonNull(getClass().getResourceAsStream(uri))));
        thisImageView.setFitHeight(40);
        thisImageView.setFitWidth(35.0);
        button.setGraphic(thisImageView);
    }
}
