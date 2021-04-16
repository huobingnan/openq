package org.openq.vasp.util;

import org.openq.vasp.bean.ContcarFile;
import org.openq.vasp.bean.CoordinateType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public final class ContcarParser
{
    public static ContcarFile parse(File contcarFile) throws Exception
    {
        FileReader fileReader = new FileReader(contcarFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        // 创建一个CONTCAR对象
        ContcarFile contcar = new ContcarFile();
        contcar.setFilePath(contcarFile.getPath());
        int validLineNumber = 0;  int coordinateStartLineNumber = 0;
        int componentNow = 0; int amount = 0, cnt = 0;
        while ((line = bufferedReader.readLine()) != null)
        {
            if (!line.isBlank()) validLineNumber++;
            else continue;
            if (validLineNumber == 1)
            {
                contcar.setName(line.trim());
            } else if (validLineNumber == 2)
            {
                contcar.setScale(Double.parseDouble(line));
            } else if (validLineNumber >= 3 && validLineNumber <=5)
            {
                Vector<String> cellVector = Strings.splitByAnyNumberBlank(line);
                int col = validLineNumber - 3;
                double[][] crystalMatrix = contcar.getCrystalMatrix();
                for (int i = 0 ; i < cellVector.size(); ++i)
                    crystalMatrix[i][col] = Double.parseDouble(cellVector.get(i));

            } else if (validLineNumber == 6)
            {
                // 解析晶体的组成
                contcar.setComponents(Strings.splitByAnyNumberBlank(line));
            } else if (validLineNumber == 7)
            {
                // 解析晶体组分在晶体中所占据的个数
                Vector<String> amountStrings = Strings.splitByAnyNumberBlank(line);
                List<Integer> amounts = amountStrings.stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                for (int a : amounts)
                    amount += a;
                contcar.setAmount(amounts); contcar.setTotalAtomAmount(amount);
            } else if (validLineNumber == 8)
            {
                // 解析晶体CONTCAR文件的晶体坐标类型
                if (line.equals(CoordinateType.Cartesian.getKey()))
                    contcar.setCoordinateType(CoordinateType.Cartesian);
                else
                    contcar.setCoordinateType(CoordinateType.Fractional);
            } else if (validLineNumber >= 9)
            {
                // 解析坐标信息
                char firstChar = line.trim().charAt(0);
                if (Character.isDigit(firstChar))
                {
                    if (coordinateStartLineNumber == 0)
                        coordinateStartLineNumber = validLineNumber;
                    Vector<String> coordinate = Strings.splitByAnyNumberBlank(line);
                    if (coordinate.size() < 3)
                    {
                        System.out.println("[ERROR]: invalid coordinate information");
                        throw new IllegalStateException();
                    }

                    // put component with ID
                    int id = validLineNumber - coordinateStartLineNumber + 1;
                    if (id > contcar.getAmount().get(componentNow))
                    {
                        componentNow++;
                        coordinateStartLineNumber = validLineNumber;
                        id = validLineNumber - coordinateStartLineNumber + 1;
                    }
                    String component = contcar.getComponents().get(componentNow) + id;
                    double[] coor = new double[3];
                    for (int i = 0; i < 3; ++i)
                        coor[i] = Double.parseDouble(coordinate.get(i));
                    contcar.getComponentAndCoordinate().put(component, coor);cnt++;
                    if (cnt >= amount) break;
                }
            }
        }
        return contcar;
    }
}
