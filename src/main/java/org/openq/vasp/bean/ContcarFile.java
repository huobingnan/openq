package org.openq.vasp.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Hashtable;
import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ContcarFile
{
    private String name;
    private String filePath;
    private int totalAtomAmount;
    private double scale;
    private double[][] crystalMatrix = new double[3][3];
    private CoordinateType coordinateType;
    private List<String> components;
    private List<Integer> amount;
    private Hashtable<String, double[]> componentAndCoordinate = new Hashtable<>();
}
