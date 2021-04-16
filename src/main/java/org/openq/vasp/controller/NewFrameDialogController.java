package org.openq.vasp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.openq.vasp.bean.Channel;
import org.openq.vasp.bean.Frame;

@Slf4j(topic = "NewFrameDialogController")
public class NewFrameDialogController
{
    @FXML private Label channelTypeLabel;

    @FXML private ComboBox<String> resourceComboBox;

    @FXML private TextField frameNameTextField;

    @FXML private void initialize()
    {
        log.info("Execute NewFrameDialogController initialize method");
    }

    public void accept(Object intent)
    {
        if (intent instanceof Channel)
        {
            channelTypeLabel.setText(((Channel)intent).getType());
        }
    }

    public Frame getFrame()
    {
        return new Frame(frameNameTextField.getText(),
                resourceComboBox.getSelectionModel().getSelectedItem());
    }
}
