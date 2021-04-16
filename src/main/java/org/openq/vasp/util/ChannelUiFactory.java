package org.openq.vasp.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.openq.vasp.bean.Channel;
import org.openq.vasp.bean.Frame;

import java.util.function.Consumer;

public final class ChannelUiFactory
{
    public static Button buildFrameButton(Frame frame)
    {
        if (frame == null) return null;
        // 添加一个Effect
        DropShadow dropShadowEffect = new DropShadow();
        dropShadowEffect.setOffsetX(5);
        dropShadowEffect.setOffsetY(5);
        Button result = new Button();
        result.setPrefWidth(80);
        result.setPrefHeight(80);
        result.setOpacity(0.75D);
        result.setEffect(dropShadowEffect);
        result.setText(frame.getName());
        return result;
    }

    public static Dialog<ButtonType> buildNewFrameDialog(Parent view)
    {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New frame options");
        dialog.getDialogPane().setContent(view);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        return dialog;
    }

    public static Dialog<ButtonType> buildNewChannelDialog(Parent view)
    {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New channel options");
        dialog.getDialogPane().setContent(view);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        return dialog;
    }


    /**
     * 根据输入的Channel构建channel UI界面
     * @param channel Channel对象
     * @return TabPane的Tab
     */
    public static Tab buildChannelTab(Channel channel, EventHandler<ActionEvent> addFrameButtonClickAction)
    {
        Button addFrameButton = new Button();
        addFrameButton.setOpacity(0.5D);
        addFrameButton.setStyle("-fx-background-color: rgba(255,255,255,0.5); -fx-background-image: url(/icon/AddFrameIcon.png);");
        addFrameButton.setPrefWidth(80);
        addFrameButton.setPrefHeight(80);
        BorderStroke addFrameButtonStroke = new BorderStroke(Color.rgb(76, 185, 169),
                BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderStroke.THIN);
        addFrameButton.setBorder(new Border(addFrameButtonStroke));
        addFrameButton.setOnAction(addFrameButtonClickAction);
        // 为AddFrameButton添加一个动画
        Timeline timeline = new Timeline();
        KeyValue key1 = new KeyValue(addFrameButton.rotateProperty(), 0.0D);
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(0.0D), "kf1", key1);
        KeyValue key2 = new KeyValue(addFrameButton.rotateProperty(), 90.0D);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(300.0D), "kf2", key2);
        timeline.getKeyFrames().addAll(keyFrame1, keyFrame2);
        addFrameButton.setOnMouseEntered(e -> timeline.play());


        HBox frameContainer = new HBox();
        frameContainer.getChildren().add(addFrameButton);
        HBox.setMargin(addFrameButton, new Insets(10));
        ScrollPane scrollPane = new ScrollPane(frameContainer);
        return new Tab(channel.getName(), scrollPane);
    }
}
