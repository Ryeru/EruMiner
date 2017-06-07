package mining.data;

public enum Rock {
    CLAY((short) 6705, 1),
    COPPER((short) 4645, 1),
    TIN((short) 53, 1),
    IRON((short) 2576, 15);

    private short modelColour;
    private int requiredLevel;

    Rock(short modelColour, int requiredLevel) {
        this.modelColour = modelColour;
        this.requiredLevel = requiredLevel;
    }

    public boolean canMine(int level) {
        return level >= this.requiredLevel;
    }

    public short getModelColour() {
        return modelColour;
    }
}
