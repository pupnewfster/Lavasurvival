package me.eddiep;

public enum PhysicsType {
    CLASSIC("Classic"),
    REVERSE("Reverse"),
    DEFAULT("Default");

    public static PhysicsType getFromName(String name) {
        if(name.equalsIgnoreCase("classic"))
            return CLASSIC;
        else if(name.equalsIgnoreCase("reverse"))
            return REVERSE;
        else
            return DEFAULT;
    }

    private String name;

    PhysicsType(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}