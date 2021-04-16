package org.openq.vasp.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Frame
{
    @NotBlank(message = "Frame name can't be blank")
    private String name;

    @NotBlank(message = "Frame resource can't be blank")
    private String resource;
}
