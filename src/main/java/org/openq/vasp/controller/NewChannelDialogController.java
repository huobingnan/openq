package org.openq.vasp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.openq.vasp.bean.Channel;
import org.openq.vasp.bean.ChannelDisplayType;
import org.openq.vasp.bean.ChannelSettingPair;
import org.openq.vasp.bean.ChannelType;
import org.openq.vasp.ui.ChannelUiFactory;

import java.util.List;
import java.util.Optional;

@Slf4j(topic = "NewChannelDialogController")
public class NewChannelDialogController
{

    @FXML private TextField channelNameTextField;

    @FXML private ComboBox<String> channelTypeComboBox;

    @FXML private ComboBox<String> displayTypeComboBox;

    @FXML private ComboBox<String> displayPaneComboBox;

    @FXML private TableView<ChannelSettingPair> channelSettingTableView;

    private final Dialog<ChannelSettingPair> newChannelSettingDialog = ChannelUiFactory.buildNewChannelSettingDialog();

    @FXML private void initialize()
    {
        log.info("Execute NewChannelDialogController initialize method");

        channelTypeComboBox.setItems(FXCollections.observableArrayList());
        channelTypeComboBox.getItems().add(ChannelType.BOND_DISTANCE);
        channelTypeComboBox.getItems().add(ChannelType.BOND_ANGLE);
        channelTypeComboBox.getSelectionModel().select(0);

        displayTypeComboBox.setItems(FXCollections.observableArrayList());
        displayTypeComboBox.getItems().add(ChannelDisplayType.TABLE_VIEW);
        displayTypeComboBox.getItems().add(ChannelDisplayType.LINE_CHART);
        displayTypeComboBox.getItems().add(ChannelDisplayType.BAR_CHART);
        displayTypeComboBox.getSelectionModel().select(0);

        displayPaneComboBox.setItems(FXCollections.observableArrayList());



        // 初始化ChannelSettingTableView的ContextMenu
        ContextMenu contextMenu = new ContextMenu();

        // ContextMenu Items
        MenuItem newSettingItem = new MenuItem("new");
        newSettingItem.setOnAction(this::doMenuItemNewClick);
        contextMenu.getItems().add(newSettingItem);

        // 初始化ChannelSettingTableView
        channelSettingTableView.setEditable(true);
        channelSettingTableView.setItems(FXCollections.observableArrayList());
        channelSettingTableView.setContextMenu(contextMenu);
        TableColumn<ChannelSettingPair, String> settingKeyColumn = new TableColumn<>("Setting");
        settingKeyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        settingKeyColumn.setReorderable(false);
        settingKeyColumn.setPrefWidth(260);
        settingKeyColumn.setResizable(false);
        settingKeyColumn.setSortable(false);
        settingKeyColumn.setEditable(false);
        TableColumn<ChannelSettingPair, String> settingValueColumn = new TableColumn<>("Value");
        settingValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        settingValueColumn.setEditable(true);
        settingValueColumn.setReorderable(false);
        settingValueColumn.setPrefWidth(300);
        settingValueColumn.setResizable(false);
        settingValueColumn.setSortable(false);

        channelSettingTableView.getColumns().add(settingKeyColumn);
        channelSettingTableView.getColumns().add(settingValueColumn);

        channelSettingTableView.setPlaceholder(new Label("Right click to new settings"));

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
        channel.setPrimaryDisplayType(displayTypeComboBox.getSelectionModel().getSelectedItem());
        channel.setDisplayPane(displayPaneComboBox.getSelectionModel().getSelectedItem());

        ObservableList<ChannelSettingPair> channelSettings = channelSettingTableView.getItems();
        if (!channelSettings.isEmpty())
        {
            channelSettings.forEach(setting ->
            {
                channel.getSettings().put(setting.getKey(), setting.getValue());
            });
        }
        return channel;
    }



// ----------------------------------- Channel setting table view context menu -----------------------------------------

    private void doMenuItemNewClick(ActionEvent event)
    {
        for(;;)
        {
            Optional<ChannelSettingPair> channelSettingPair = newChannelSettingDialog.showAndWait();
            if (channelSettingPair.isPresent())
            {
                ChannelSettingPair pair = channelSettingPair.get();
                ObservableList<ChannelSettingPair> settingPairs = channelSettingTableView.getItems();
                if (settingPairs.isEmpty())
                {
                    // 添加
                    channelSettingTableView.getItems().add(pair);
                } else
                {
                    boolean duplicatedKey = false;
                    // 查看键是否已经存在
                    for (ChannelSettingPair settingPair : settingPairs)
                    {
                        if (settingPair.getKey().equals(pair.getKey()))
                        {
                            duplicatedKey = true; break;
                        }
                    }
                    if (duplicatedKey)
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Duplicated setting " + pair.getKey(),
                                ButtonType.OK);
                        alert.setTitle("Error");
                        alert.setHeaderText("Duplicated setting!!!");
                        alert.showAndWait();
                        continue; // 继续输入
                    }
                }
            }
            break;
        }
    }


}
