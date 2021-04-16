package org.openq.vasp.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jblas.DoubleMatrix;
import org.openq.vasp.bean.*;

import java.util.*;

public final class ChannelGraphFactory
{

// -------------------------------------------------- Algorithm --------------------------------------------------------

    /**
     * 晶体中组分的组合算法
     * @param num 选择组分的个数
     * @param componentList 组分列表
     * @return 组合结果
     */
    public static List<List<String>> componentCombinationSelect(int num, List<String> componentList)
    {
        List<List<String>> result = new ArrayList<List<String>>();
        if (num == 1)
        {
            for (String c : componentList)
            {
                List<String> list = new ArrayList<>();
                list.add(c);
                result.add(list);
            }
            return result;
        }
        if (num >= componentList.size())
        {
            return result;
        }
        int size = componentList.size();
        for (int i = 0; i < (size - num + 1); i++)
        {
            List<List<String>> cr =
                    componentCombinationSelect(num - 1, componentList.subList(i + 1, size));//从i+1处直至字符串末尾
            String c = componentList.get(i);//得到上面被去掉的字符，进行组合
            for (List<String> s : cr)
            {
                s.add(c);
                result.add(s);
            }
        }
        return result;
    }


// ---------------------------------------- Graph View Factory Methods -------------------------------------------------

    private static Node buildBondDistanceTableView(ArrayList<HashMap<String, Double>> frameBondResult,
                                                   List<Frame> frameList)
    {
        // 将将所有搜寻到的键取并集
        Set<String> finalBondSet = new TreeSet<>();
        for (HashMap<String, Double> stringDoubleHashMap : frameBondResult)
        {
            finalBondSet.addAll(stringDoubleHashMap.keySet());
        }
//        int maxSizeKeySetLength = -1;
//        for (var element : frameBondResult)
//        {
//            if (element.keySet().size() > maxSizeKeySetLength)
//            {
//                finalBondSet = element.keySet();
//                maxSizeKeySetLength = finalBondSet.size();
//            }
//        }
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
            for (HashMap<String, Double> map : frameBondResult)
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

        tableView.setItems(items);

        return tableView;
    }

    private static Node buildBondDistanceGraph(List<Frame> frameList, Hashtable<String, ContcarFile> resourceAndFile)
    {
        /*
           最终计算的结果，帧的查找键为帧在数组中的索引
         */
        final ArrayList<HashMap<String, Double>> frameBondResult = new ArrayList<>();

        final double distanceMax = 2.0D;
        for (int i = 0; i < frameList.size(); ++i)
        {
            // 初始化结果集
            frameBondResult.add(new HashMap<>());
            // 计算
            Frame frame = frameList.get(i);
            ContcarFile contcarFile = resourceAndFile.get(frame.getResource());
            Hashtable<String, double[]> componentAndCoordinate = contcarFile.getComponentAndCoordinate();
            Set<String> componentList = componentAndCoordinate.keySet();
            List<List<String>> combinationResult = componentCombinationSelect(2, new ArrayList<>(componentList));

            for (var combination : combinationResult)
            {
                String component1 = combination.get(0);
                String component2 = combination.get(1);
                double[] component1Coordinate = componentAndCoordinate.get(component1);
                double[] component2Coordinate = componentAndCoordinate.get(component2);
                double distance = 0.0D; // 键长

                // 坐标转换并计算
                DoubleMatrix coor1 = null;
                DoubleMatrix coor2 = null;
                if (contcarFile.getCoordinateType().equals(CoordinateType.Fractional))
                {
                    // 坐标是分数坐标，需要做一个转换之后再进行计算
                    coor1 = VASPCoordinateSupport.convertDirectToCartesian(contcarFile.getCrystalMatrix(),
                            component1Coordinate);
                    coor2 = VASPCoordinateSupport.convertDirectToCartesian(contcarFile.getCrystalMatrix(),
                            component2Coordinate);

                } else
                {
                    //  坐标是笛卡尔坐标，直接进行计算
                    coor1 = new DoubleMatrix(3, 1, component1Coordinate[0],
                            component1Coordinate[1], component1Coordinate[2]);
                    coor2 = new DoubleMatrix(3, 1, component2Coordinate[0],
                            component2Coordinate[1], component2Coordinate[2]);
                }
                distance = coor1.sub(coor2).norm2();

                // 检查该键长是否已经超出了化学成键距离的阈值
                if (distance <= distanceMax)
                {
                    // 放入结果
                    frameBondResult.get(i).put(component1 + "-" + component2, distance);
                }
            }
        }

        return buildBondDistanceTableView(frameBondResult, frameList);
    }

    public static Node build(Channel channel, List<Frame> frameList, Hashtable<String, ContcarFile> resourceAndFile)
    {
        String type = channel.getType();
        Node result = null;
        if (ChannelType.BOND_DISTANCE.equals(type))
        {
            result = buildBondDistanceGraph(frameList, resourceAndFile);
        }
        return result;
    }

}
