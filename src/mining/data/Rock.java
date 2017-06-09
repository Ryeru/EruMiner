package mining.data;

import org.dreambot.api.methods.map.Tile;

import java.awt.*;
import java.util.HashSet;

public enum Rock {
    CLAY((short) 6705, 1),
    COPPER((short) 4645, 1),
    TIN((short) 53, 1),
    IRON((short) 2576, 15);

    private short modelColour;
    private int requiredLevel;
    private static HashSet<Coord> selectedTiles = new HashSet<>();

    Rock(short modelColour, int requiredLevel) {
        this.modelColour = modelColour;
        this.requiredLevel = requiredLevel;
    }

    public static HashSet<Coord> getSelectedCoords() {
        return selectedTiles;
    }

    public static boolean removeSelectedTile(Coord tile) {
        return selectedTiles.remove(tile);
    }

    public static boolean addSelectedTile(Coord tile) {
        return selectedTiles.add(tile);
    }

    public static boolean isSelected(Coord tile) {
        return selectedTiles.contains(tile);
    }

    public boolean canMine(int level) {
        return level >= this.requiredLevel;
    }

    public short getModelColour() {
        return modelColour;
    }

    public static void drawSelectedTiles(Graphics graphics) {
        Color selectedTiles = new Color(0, 255, 3, 44);
        graphics.setColor(selectedTiles);

        for (Coord coord : getSelectedCoords()) {
            Tile newTile = coord.toTile();
            Polygon polygon;
            if (newTile != null && (polygon = newTile.getPolygon()) != null) {
                graphics.fillPolygon(polygon);
            }
        }
    }
}
