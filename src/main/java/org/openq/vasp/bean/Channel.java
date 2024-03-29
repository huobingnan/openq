package org.openq.vasp.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


import javax.validation.constraints.NotBlank;
import java.util.Hashtable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Channel
{
    // 通道的名称
    @NotBlank(message = "Channel name can't be blank")
    private String name;

    // 通道的类型
    @NotBlank(message = "Channel type can't be blank")
    private String type;

    // 首选的展示形式
    @NotBlank(message = "Primary display type can't be blank")
    private String primaryDisplayType;

    // 展示在哪个图形界面上
    @NotBlank(message = "Display pane can't be blank")
    private String displayPane;

    // channel的一些配置项，这里配置项的值对象一定要重写toString方法
    private Hashtable<String, Object> settings = new Hashtable<>();

    public void putSetting(String settingKey, Object value)
    {
        settings.put(settingKey, value);
    }

}
