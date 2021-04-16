package org.openq.vasp.util;

import org.jblas.DoubleMatrix;

public class VASPCoordinateSupport
{

    /**
     * 将Direct形式的分数左边转换为Cartesian形式的笛卡尔坐标
     * @param cellMatrix 晶胞矩阵
     * @param atomCoordinate 原子坐标
     * @return Cartesian笛卡尔坐标
     */
    public static DoubleMatrix convertDirectToCartesian(DoubleMatrix cellMatrix, DoubleMatrix atomCoordinate)
    {

        return cellMatrix.mulColumnVector(atomCoordinate);
    }


    public static DoubleMatrix convertDirectToCartesian(double[][] cellMatrix, double[] atomCoordinate)
    {
        // 3x3 cell vector matrix
        DoubleMatrix doubleCellMatrix = new DoubleMatrix(cellMatrix);
        // 3x1 atom coordinate vector
        DoubleMatrix atomMatrix = new DoubleMatrix(3, 1,
                atomCoordinate[0], atomCoordinate[1], atomCoordinate[2]);
        return convertDirectToCartesian(doubleCellMatrix, atomMatrix);
    }
}
