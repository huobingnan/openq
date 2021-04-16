package org.openq.vasp.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BondDistanceTableRow
{
    private String bondName;
    private List<Double> bondDistance = new ArrayList<>();
}
