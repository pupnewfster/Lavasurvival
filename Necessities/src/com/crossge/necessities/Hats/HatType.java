package com.crossge.necessities.Hats;

import java.util.Collection;
import java.util.HashMap;

public enum HatType {
    BoxTopHat("BOX_TOP_HAT"),
    TopHat("TOP_HAT"),
    StrawHat("STRAW_HAT"),
    Fedora("FEDORA");

    private static HashMap<String,HatType> nameMap = new HashMap<>();
    private String name;

    HatType(String name) {
        this.name = name;
    }

    public static HatType fromString(String name) {
        return nameMap.get(name.toUpperCase().replaceAll("_", ""));
    }

    public String getName() {
        return this.name;
    }

    public static void mapHats() {
        for(HatType h : values())
            nameMap.put(h.getName().replaceAll("_", ""), h);
    }

    public static Collection<String> getTypes() {
        return nameMap.keySet();
    }
}