package me.eddiep;

public class PhysicsType {
    public static PhysicsType CLASSIC = new PhysicsType("Classic");
    public static PhysicsType RISE = new PhysicsType("Rise");
    public static PhysicsType REVERSE = new PhysicsType("Reverse");
    public static PhysicsType DEFAULT = new PhysicsType("Default");
    private String name;

    public static PhysicsType getFromName(String name) {
        if(name.equalsIgnoreCase("classic"))
            return CLASSIC;
        else if(name.equalsIgnoreCase("rise"))
            return RISE;
        else if(name.equalsIgnoreCase("reverse"))
            return REVERSE;
        else
            return DEFAULT;
    }

    public PhysicsType(String name){
        this.name = name;
    }
}