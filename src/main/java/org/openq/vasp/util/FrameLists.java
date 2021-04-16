package org.openq.vasp.util;

import org.jblas.DoubleMatrix;
import org.openq.vasp.bean.ContcarFile;
import org.openq.vasp.bean.CoordinateType;
import org.openq.vasp.bean.Frame;


import java.util.*;

/**
 * 处理帧列表的工具类
 */
public final class FrameLists
{

// -------------------------------------------------- Algorithm --------------------------------------------------------

    /**
     * 晶体中组分的组合算法
     * @param num 选择组分的个数
     * @param componentList 组分列表
     * @return 组合结果
     */
    private static List<List<String>> componentCombinationSelect(int num, List<String> componentList)
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


    /**
     * 计算键长类型的帧列表数据
     * @param frameList 帧列表
     * @param resourceAndFile 资源和文件
     * @return 计算结果
     */
    public static List<Map<String, Double>> calculateBondDistance(List<Frame> frameList,
                                                                      Map<String, ContcarFile> resourceAndFile)
    {
        /*
           最终计算的结果，帧的查找键为帧在数组中的索引
         */
        final ArrayList<Map<String, Double>> bondDistanceResult = new ArrayList<>();

        final double distanceMax = 2.0D;
        for (int i = 0; i < frameList.size(); ++i)
        {
            // 初始化结果集
            bondDistanceResult.add(new HashMap<>());
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
                    bondDistanceResult.get(i).put(component1 + "-" + component2, distance);
                }
            }
        }
        return bondDistanceResult;
    }
}
