package org.openq.vasp.config;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public final class ChannelSettingKey
{
    private static final ArrayList<String> keyCollection = new ArrayList<>();
    private static final Hashtable<String, String> settingKeyDefaultValue = new Hashtable<>();
    private static final Hashtable<String, String> settingKeyToolTips = new Hashtable<>();

    public static final String BOND_LENGTH_MAX = "Bond Length Max";


    static
    {
        keyCollection.add(BOND_LENGTH_MAX);
        settingKeyDefaultValue.put(BOND_LENGTH_MAX, "2.00");
        settingKeyToolTips.put(BOND_LENGTH_MAX, "please input a number");
    }

    public static List<String> getKeyCollection()
    {
        return keyCollection;
    }

    public static String getKeyDefaultValue(String key)
    {
        return settingKeyDefaultValue.getOrDefault(key, "");
    }

    public static String getKeyToolTips(String key){ return settingKeyToolTips.getOrDefault(key, ""); }

}
