package org.openq.vasp.controller;


import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.openq.vasp.bean.Channel;
import org.openq.vasp.bean.ChannelDisplayType;
import org.openq.vasp.bean.ContcarFile;
import org.openq.vasp.bean.Frame;
import org.openq.vasp.ui.ChannelUiFactory;
import org.openq.vasp.ui.ChannelGraphFactory;
import org.openq.vasp.util.ContcarParser;
import org.openq.vasp.util.MyValidator;


import java.io.File;
import java.util.*;

@Slf4j(topic = "MainPageController")
public class MainPageController
{

    //  --------------------- FXML Loader --------------------

    private final FXMLLoader detailDialogLoader = new FXMLLoader(
            getClass().getResource("/view/DetailPage.fxml"));

    private final FXMLLoader newChannelDialogLoader = new FXMLLoader(
            getClass().getResource("/view/NewChannelDialog.fxml")
    );

    private final FXMLLoader newFrameDialogLoader = new FXMLLoader(
            getClass().getResource("/view/NewFrameDialog.fxml")
    );

    // ---------------------- FXML Component --------------------
    @FXML private BorderPane rootNode;

    @FXML private ListView<String> resourceListView;

    @FXML private TabPane channelTabPane;

    @FXML private TabPane graphTabPane;

    @FXML private Button newChannelButton;

    @FXML private Button showChannelButton;


    // --------------------- Custom Component -------------------
    private final ContextMenu resourceListViewContextMenu = new ContextMenu();

    private Parent newChannelDialogRoot;

    private Parent newFrameDialogRoot;

    private final Dialog<Void> resourceDetailDialog = new Dialog<>();


    // --------------------- Resource properties ---------------------
    private final Hashtable<String, File> resourceAndFile = new Hashtable<>();
    private final Hashtable<String, ContcarFile> resourceAndContcarFile = new Hashtable<>();

    // --------------------- Channel properties -----------------------
    private final Hashtable<String, Channel> channelNameAndInstance = new Hashtable<>();
    private final Hashtable<String, List<Frame>> channelNameAndFrames = new Hashtable<>();

// -------------------------------------------- Graph properties --------------------------------------------------


    private void nodePropertiesBind()
    {
        @SuppressWarnings("unchecked")
        ComboBox<String> node1 = (ComboBox<String>) newFrameDialogRoot.lookup("#resourceComboBox");
        if (node1 != null)
        {
            node1.itemsProperty().bind(new SimpleListProperty<String>(resourceListView.getItems()));
            log.info("@NewFrameDialog-resourceComboBox bind successfully!");
        }
    }


    @FXML private void initialize() throws Exception
    {
        log.info("Execute MainPageController initialize method");
        Parent detailDialogRoot = detailDialogLoader.load();
        log.info("Detail dialog view load successfully!");
        newChannelDialogRoot = newChannelDialogLoader.load();
        log.info("New channel dialog view load successfully!");
        newFrameDialogRoot = newFrameDialogLoader.load();
        log.info("New frame dialog view load successfully!");

        // initialize FXML component
        ObservableList<String> resourceListViewModel = FXCollections.observableArrayList();
        resourceListView.setItems(resourceListViewModel);
        resourceListView.setPlaceholder(new Label("Right click to add resource"));
        resourceListView.setContextMenu(resourceListViewContextMenu);

        // initialize custom component

        // construct context menu items
        // 导入资源文件按钮
        MenuItem importItem = new MenuItem("import");
        importItem.setOnAction(this::doImportResource);
        // 删除资源文件按钮
        MenuItem deleteItem = new MenuItem("delete");
        deleteItem.setOnAction(this::doDeleteResource);
        // 展示资源文件详情
        MenuItem detailItem = new MenuItem("detail");
        detailItem.setOnAction(this::doShowResourceDetail);

        /*
         * 在右键菜单出现之前注册一个监听事件
         * 这个监听事件的主要作用是，当列表中未选中任何一项时:
         *      不显示删除按钮 ❌
         *      不显示详情按钮 ❌
         */
        resourceListViewContextMenu.setOnShowing(windowEvent ->
        {
            importItem.setVisible(true);
            deleteItem.setVisible(true);
            detailItem.setVisible(true);
            ObservableList<String> selectedItems = resourceListView.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty())
            {
                // 选中了列表中的某项资源文件
                deleteItem.setVisible(false);
                detailItem.setVisible(false);
            }
        });
        // add menu item
        ObservableList<MenuItem> contextMenuItems = resourceListViewContextMenu.getItems();
        contextMenuItems.add(importItem);
        contextMenuItems.add(deleteItem);
        contextMenuItems.add(detailItem);

        // 资源详情对话框
        resourceDetailDialog.getDialogPane().setContent(detailDialogRoot);
        resourceDetailDialog.setWidth(500);
        resourceDetailDialog.setHeight(500);
        resourceDetailDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // channel component initialize
        newChannelButton.setOnAction(this::doNewChannelButtonClick);
        showChannelButton.setOnAction(this::doShowChannelButtonClick);

        // new frame dialog component initialize;

        // 这个方法一定要在方法末尾执行，确保所有组件都完成初始化。
        nodePropertiesBind();
    }



    /**
     * 处理导入资源的
     */
    private void doImportResource(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("import VASP file");
        File file = fileChooser.showOpenDialog(rootNode.getScene().getWindow());
        if (file == null) return;
        // 检查是否已经加载过这样一份资源文件
        String resource = file.getName();
        boolean contains = resourceAndFile.containsKey(resource);
        while (contains)
        {
            // 提示用户将资源重命名加载
            TextInputDialog renameAction = new TextInputDialog();
            renameAction.setTitle("Rename resource");
            renameAction.setHeaderText("Resource has already existed, please rename resource.");
            renameAction.setContentText("new name:");
            TextField editor = renameAction.getEditor();
            editor.setText(resource + "(副本)");
            editor.selectAll();
            Optional<String> newNameOptional = renameAction.showAndWait();
            if (newNameOptional.isPresent())
            {
                resource = newNameOptional.get();
                contains = resourceAndFile.containsKey(resource);
            } else
            {
                resource = null;
                contains = false;
            }
        }
        if (resource != null)
        {
            // 创建一个新线程用于解析CONTCAR文件
            try
            {
                ContcarFile contcarFile = ContcarParser.parse(file);
                resourceListView.getItems().add(resource);
                resourceAndFile.put(resource, file);
                resourceAndContcarFile.put(resource, contcarFile);
                log.info("file parse successfully!!!");
            }catch (Exception ex)
            {
                log.error(ex.getMessage());
            }
        }
    }

    /**
     * 展示资源的详情信息
     */
    private void doShowResourceDetail(ActionEvent event)
    {
        String selectedItem = resourceListView.getSelectionModel().getSelectedItem();
        if (resourceAndContcarFile.containsKey(selectedItem))
        {
            DetailDialogController detailDialogController = detailDialogLoader.getController();
            // 接收contcar file更新UI界面
            detailDialogController.acceptContcarFile(resourceAndContcarFile.get(selectedItem));
            resourceDetailDialog.setTitle(selectedItem + "-detail");
            resourceDetailDialog.show();
        }
    }

    /**
     * 删除列表中的资源
     * @param event ActionEvent
     */
    private void doDeleteResource(ActionEvent event)
    {
        ObservableList<String> deleteItems = resourceListView.getSelectionModel().getSelectedItems();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You will delete those resource file:\n");
        for (String item : deleteItems)
            stringBuilder.append(item).append("\n");
        Alert beforeDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION,
                stringBuilder.toString(), ButtonType.OK, ButtonType.CANCEL);
        beforeDeleteAlert.setTitle("Whether to delete the resource file");
        Optional<ButtonType> clickButton = beforeDeleteAlert.showAndWait();
        if (clickButton.isPresent() && clickButton.get().equals(ButtonType.OK))
        {
            resourceListView.getItems().removeAll(deleteItems);
        }
    }



    /**
     * 处理添加节点帧按钮点击
     * @param event ActionEvent
     */
    private void doAddFrameButtonClick(ActionEvent event)
    {
        String channelName = channelTabPane.getSelectionModel().getSelectedItem().getText();
        NewFrameDialogController controller = newFrameDialogLoader.getController();
        controller.accept(channelNameAndInstance.get(channelName));
        Dialog<ButtonType> newFrameDialog = ChannelUiFactory.buildNewFrameDialog(newFrameDialogRoot);
        Alert invalidInputAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        invalidInputAlert.setTitle("Error input");
        invalidInputAlert.setHeaderText("Invalid options input");
        for(;;)
        {
            Optional<ButtonType> buttonType = newFrameDialog.showAndWait();
            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK))
            {
                Frame newFrame = controller.getFrame();
                List<String> errorMessages = MyValidator.validate(newFrame);
                if (!errorMessages.isEmpty())
                {
                    final StringBuilder builder = new StringBuilder();
                    builder.append("For those specific reasons:\n");
                    errorMessages.forEach(error -> builder.append("\t").append(error).append("\n"));
                    invalidInputAlert.setContentText(builder.toString());
                    invalidInputAlert.showAndWait();
                    continue;
                }
                Button source = (Button)event.getSource();
                HBox frameContainer = (HBox)source.getParent();
                Button frame = ChannelUiFactory.buildFrameButton(newFrame);
                HBox.setMargin(frame, new Insets(10));
                ObservableList<Node> children = frameContainer.getChildren();
                int insertIndex = children.size() - 1;
                // 将这一帧的数据缓存起来
                if (channelNameAndFrames.containsKey(channelName))
                {
                    channelNameAndFrames.get(channelName).add(newFrame);
                }else
                {
                    List<Frame> frameList = new ArrayList<>();
                    frameList.add(newFrame);
                    channelNameAndFrames.put(channelName, frameList);
                }
                children.add(insertIndex, frame);
            }
            break;
        }


    }

    /**
     * 处理新建Channel按钮的点击事件
     * @param event ActionEvent
     */
    private void doNewChannelButtonClick(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING, "channel name has already existed", ButtonType.CLOSE);
        Alert invalidInputAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        invalidInputAlert.setTitle("Error");
        invalidInputAlert.setHeaderText("Invalid options input");
        Dialog<ButtonType> newChannelDialog = ChannelUiFactory.buildNewChannelDialog(newChannelDialogRoot);
        NewChannelDialogController newChannelDialogController
                = newChannelDialogLoader.getController();
        newChannelDialogController.accept(graphTabPane.getTabs());
        for(;;)
        {
            Optional<ButtonType> buttonType = newChannelDialog.showAndWait();
            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK))
            {
                Channel channel = newChannelDialogController.getChannel();
                // 检验输入
                List<String> errorMessage = MyValidator.validate(channel);
                if (!errorMessage.isEmpty())
                {
                    final StringBuilder errors = new StringBuilder();
                    errors.append("For those specific reasons:\n");
                    errorMessage.forEach(error -> errors.append("\t").append(error).append("\n"));
                    invalidInputAlert.setContentText(errors.toString());
                    // 提示给用户
                    invalidInputAlert.showAndWait();
                    continue;
                }
                if (channelNameAndInstance.containsKey(channel.getName()))
                {
                    alert.showAndWait();
                } else
                {
                    Tab tab = ChannelUiFactory.buildChannelTab(channel, this::doAddFrameButtonClick);
                    channelTabPane.getTabs().add(tab);
                    channelNameAndInstance.put(channel.getName(), channel);
                    break;
                }
            }else
            {
                break;
            }
        }
    }


    /**
     * 展示当前Channel的变化
     * @param event ActionEvent
     */
    private void doShowChannelButtonClick(ActionEvent event)
    {
        Alert unSupportDisplayAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        unSupportDisplayAlert.setTitle("UnSupport Error");
        unSupportDisplayAlert.setHeaderText("UnSupport Display Operation");
        Tab selectedTab = channelTabPane.getSelectionModel().getSelectedItem();
        String channelName = selectedTab.getText();
        Channel instance = channelNameAndInstance.get(channelName);
        List<Frame> frameList = channelNameAndFrames.get(channelName);
        Node node = ChannelGraphFactory.build(instance, frameList, resourceAndContcarFile);
        if (node != null)
        {
            graphTabPane.getSelectionModel().getSelectedItem().setContent(node);
        } else
        {
            unSupportDisplayAlert.setContentText("UnSupport Display Type : " + instance.getPrimaryDisplayType() +
                    " With Channel Type : " + instance.getType());
            unSupportDisplayAlert.showAndWait();
        }
    }
}
