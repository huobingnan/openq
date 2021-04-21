package org.openq.vasp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Slf4j(topic = "ApplicationMain")
public class ApplicationMain extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {
        URL fxmlUrl = FXMLLoader.getDefaultClassLoader().getResource("view/MainPageV1.fxml");
        if (fxmlUrl == null)
        {
            log.error("Can't load main view FXML file, application run failed!!!");
            System.exit(-1);
        }
        BorderPane root = (BorderPane) FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.setTitle("iVASP"); // 更改名称
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
