package org.openq.vasp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.openq.vasp.bean.Channel;
import org.openq.vasp.bean.ChannelType;

import java.util.List;

@Slf4j(topic = "NewChannelDialogController")
public class NewChannelDialogController
{

    @FXML private TextField channelNameTextField;

    @FXML private ComboBox<String> channelTypeComboBox;

    @FXML private ComboBox<String> displayTypeComboBox;

    @FXML private ComboBox<String> displayPaneComboBox;



    @FXML private void initialize()
    {
        log.info("Execute NewChannelDialogController initialize method");

        channelTypeComboBox.setItems(FXCollections.observableArrayList());
        channelTypeComboBox.getItems().add(ChannelType.BOND_ANGLE);
        channelTypeComboBox.getItems().add(ChannelType.BOND_DISTANCE);
        channelTypeComboBox.getSelectionModel().select(0);

        displayTypeComboBox.setItems(FXCollections.observableArrayList());
        displayTypeComboBox.getItems().add("Line chart");
        displayTypeComboBox.getItems().add("Bar chart");
        displayTypeComboBox.getSelectionModel().select(0);

        displayPaneComboBox.setItems(FXCollections.observableArrayList());
    }


    public void accept(List<Tab> graphTabs)
    {
        if (graphTabs != null && !graphTabs.isEmpty())
        {
            displayPaneComboBox.getItems().clear();
            graphTabs.forEach(tab ->
            {
                displayPaneComboBox.getItems().add(tab.getText());
            });
            if (!displayPaneComboBox.getItems().isEmpty())
            {
                displayPaneComboBox.getSelectionModel().select(0);
            }
        }
    }

    public Channel getChannel()
    {
        Channel channel = new Channel();
        channel.setName(channelNameTextField.getText());
        channel.setType(channelTypeComboBox.getSelectionModel().getSelectedItem());
        channel.setPrimaryDisplayType(channelTypeComboBox.getSelectionModel().getSelectedItem());
        channel.setDisplayPane(displayPaneComboBox.getSelectionModel().getSelectedItem());
        return channel;
    }



}
