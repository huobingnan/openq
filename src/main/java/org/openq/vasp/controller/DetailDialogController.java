package org.openq.vasp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openq.vasp.bean.ContcarFile;

import java.util.Hashtable;
import java.util.Map;


@Slf4j(topic = "DetailDialogController")
public class DetailDialogController
{
    @FXML private TableView<Attribute> infoAttributeTable;

    @FXML private TableView<ComponentAndCoordinate> infoAtomCoordinate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComponentAndCoordinate
    {
        private String component;
        private Number x;
        private Number y;
        private Number z;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attribute
    {
        private String attributeName;
        private String attributeValue;
    }


    @FXML private void initialize()
    {
        log.info("Execute DetailDialogController initialize method");
        infoAtomCoordinate.setItems(FXCollections.observableArrayList());
        TableColumn<ComponentAndCoordinate, String> componentColumn = new TableColumn<>("Component");
        componentColumn.setCellValueFactory(new PropertyValueFactory<ComponentAndCoordinate, String>("component"));
        componentColumn.setReorderable(false);
        TableColumn<ComponentAndCoordinate, Number> xColumn = new TableColumn<>("X");
        xColumn.setCellValueFactory(new PropertyValueFactory<ComponentAndCoordinate, Number>("x"));
        xColumn.setReorderable(false);
        xColumn.setPrefWidth(180);
        TableColumn<ComponentAndCoordinate, Number> yColumn = new TableColumn<>("Y");
        yColumn.setCellValueFactory(new PropertyValueFactory<ComponentAndCoordinate, Number>("y"));
        yColumn.setReorderable(false);
        yColumn.setPrefWidth(180);
        TableColumn<ComponentAndCoordinate, Number> zColumn = new TableColumn<>("Z");
        zColumn.setCellValueFactory(new PropertyValueFactory<ComponentAndCoordinate, Number>("z"));
        zColumn.setReorderable(false);
        zColumn.setPrefWidth(180);
        infoAtomCoordinate.getColumns().add(componentColumn);
        infoAtomCoordinate.getColumns().add(xColumn);
        infoAtomCoordinate.getColumns().add(yColumn);
        infoAtomCoordinate.getColumns().add(zColumn);

        infoAttributeTable.setItems(FXCollections.observableArrayList());
        TableColumn<Attribute, String> attributeNameColumn = new TableColumn<>("Attribute");
        attributeNameColumn.setReorderable(false);
        attributeNameColumn.setSortable(false);
        attributeNameColumn.setCellValueFactory(new PropertyValueFactory<Attribute, String>("attributeName"));
        TableColumn<Attribute, String> attributeValueColumn = new TableColumn<>("Value");
        attributeValueColumn.setCellValueFactory(new PropertyValueFactory<Attribute, String>("attributeValue"));
        attributeValueColumn.setReorderable(false);
        attributeValueColumn.setSortable(false);
        infoAttributeTable.getColumns().add(attributeNameColumn);
        infoAttributeTable.getColumns().add(attributeValueColumn);
    }


    public void acceptContcarFile(ContcarFile contcarFile)
    {
        if (contcarFile == null) return;
        // 清空已有的显示内容
        infoAttributeTable.getItems().clear();
        infoAtomCoordinate.getItems().clear();

        // 设置name
        infoAttributeTable.getItems().add(new Attribute("name", contcarFile.getName()));
        // 设置晶胞向量
        double[][] crystalMatrix = contcarFile.getCrystalMatrix();
        infoAttributeTable.getItems().add(new Attribute("cell vector a",
                String.format("[%f, %f, %f]",
                crystalMatrix[0][0],
                crystalMatrix[0][1],
                crystalMatrix[0][2])));
       infoAttributeTable.getItems().add(new Attribute("cell vector b",
               String.format(
                       "[%f, %f, %f]",
                       crystalMatrix[1][0],
                       crystalMatrix[1][1],
                       crystalMatrix[1][2]
               )));
        infoAttributeTable.getItems().add(new Attribute("cell vector c",
                String.format("[%f, %f, %f]",
                        crystalMatrix[2][0],
                        crystalMatrix[2][1],
                        crystalMatrix[2][2])
                ));
        // 设置坐标类型
        infoAttributeTable.getItems().add(new Attribute("coordinate type",
                contcarFile.getCoordinateType().getKey()));
        infoAttributeTable.getItems().add(new Attribute("scale", String.valueOf(contcarFile.getScale())));
        infoAttributeTable.getItems().add(new Attribute("atom number",
                String.valueOf(contcarFile.getTotalAtomAmount())));

        // 设置文件所在路径
        infoAttributeTable.getItems().add(new Attribute("file location", contcarFile.getFilePath()));

        // 组成与坐标
        Hashtable<String, double[]> componentAndCoordinate = contcarFile.getComponentAndCoordinate();
        for (Map.Entry<String, double[]> entry : componentAndCoordinate.entrySet())
        {
            double[] coor = entry.getValue();
            infoAtomCoordinate.getItems().add(
                    new ComponentAndCoordinate(
                            entry.getKey(),
                            coor[0],
                            coor[1],
                            coor[2]
                    )
            );
        }

    }
}
