package org.openq.vasp.util;

import java.util.Vector;

public final class Strings
{
    public static Vector<String> splitByAnyNumberBlank(String input)
    {
        Vector<String> result = new Vector<>();
        if (input != null && !input.isBlank())
        {
            boolean wordOccur = false;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < input.length(); ++i)
            {
                if (input.charAt(i) != ' ')
                {
                    wordOccur = true;
                    builder.append(input.charAt(i));
                } else
                {
                    if (wordOccur)
                    {
                        // 添加到结果里面
                        result.add(builder.toString());
                        builder.setLength(0);
                        wordOccur = false;
                    }
                }
                if (i == input.length() - 1 && builder.length() != 0)
                {
                    result.add(builder.toString());
                }
            }
        }
        return result;
    }
}
