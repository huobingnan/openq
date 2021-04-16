package org.openq.vasp.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.openq.vasp.bean.*;
import org.openq.vasp.util.FrameLists;

import java.util.*;

public final class ChannelGraphFactory
{


// ---------------------------------------- Graph View Factory Methods -------------------------------------------------

    private static Node buildBondDistanceTableView(List<Frame> frameList, Map<String, ContcarFile> resourceAndFile)
    {
        final List<Map<String, Double>> frameBondResult = FrameLists.calculateBondDistance(frameList, resourceAndFile);
        // 将将所有搜寻到的键取并集
        Set<String> finalBondSet = new TreeSet<>();
        for (Map<String, Double> stringDoubleHashMap : frameBondResult)
        {
            finalBondSet.addAll(stringDoubleHashMap.keySet());
        }
        // 构建TableView
        TableView<BondDistanceTableRow> tableView = new TableView<>();

        // 创建化学键显示列
        TableColumn<BondDistanceTableRow, String> bondNameColumn = new TableColumn<>("Bond Name");
        bondNameColumn.setPrefWidth(100);
        bondNameColumn.setCellValueFactory(new PropertyValueFactory<>("bondName"));
        tableView.getColumns().add(bondNameColumn);
        // 根据frameList创建列
        for (int i = 0; i < frameList.size(); ++i)
        {
            Frame frame = frameList.get(i);
            TableColumn<BondDistanceTableRow, String> column = new TableColumn<>(frame.getName());

            final int index = i;
            column.setCellValueFactory(cellDataFeatures ->
            {
                BondDistanceTableRow value = cellDataFeatures.getValue();
                Double distance = value.getBondDistance().get(index);
                if (distance.equals(-1.0D))
                {
                    // 键已经断裂了，无法达到成键的距离
                    return new SimpleStringProperty("-");
                }
                return new SimpleStringProperty(String.format("%.2f", distance));
            });
            column.setPrefWidth(200);
            tableView.getColumns().add(column);
        }
        // 构建显示内容
        ObservableList<BondDistanceTableRow> items = FXCollections.observableArrayList();

        for (String key : finalBondSet)
        {
            BondDistanceTableRow row = new BondDistanceTableRow();
            row.setBondName(key);
            for (Map<String, Double> map : frameBondResult)
            {
                Double distance = map.get(key);
                if (distance == null)
                {
                    row.getBondDistance().add(-1.0D);
                }else
                {
                    row.getBondDistance().add(distance);
                }
            }
            items.add(row); // 添加到TableView中显示
        }
        // 设置数据
        tableView.setItems(items);
        return tableView;
    }


    /**
     * 渲染Channel图形的入口方法
     * @param channel 通道对象
     * @param frameList 帧列表
     * @param resourceAndFile 资源和文件对象
     * @return 渲染之后的图形
     */
    public static Node build(Channel channel, List<Frame> frameList, Hashtable<String, ContcarFile> resourceAndFile)
    {
        String type = channel.getType();
        String displayType = channel.getPrimaryDisplayType();
        Node result = null;
        if (ChannelType.BOND_DISTANCE.equals(type))
        {
            // 处理键长的渲染
            if (ChannelDisplayType.TABLE_VIEW.equals(displayType))
            {
                // 处理键长列表显示形式的渲染
                result = buildBondDistanceTableView(frameList, resourceAndFile);
            }
        }
        return result;
    }

}
