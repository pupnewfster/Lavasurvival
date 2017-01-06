package me.eddiep.minecraft.ls.system.specialblocks;

public enum InventoryTiers {
    COMMON("Common"),
    UNCOMMON("Uncommon"),
    RARE("Rare"),
    LEGENDARY("Legendary");

    private String name;

    InventoryTiers(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}