package mining.data;

import org.dreambot.api.methods.map.Tile;

/**
 * Created by Leafs on 8-6-2017.
 */

//TODO - Use Tile instead of my custom class Coord for drawing graphics.
public class Coord {
    private int x;
    private int y;
    private int z;

    public Coord(Tile tile) {
        this.x = tile.getX();
        this.y = tile.getY();
        this.z = tile.getZ();
    }

    public Tile toTile() {
        return new Tile(0, 0, z).translate(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coord coord = (Coord) o;

        return x == coord.x && y == coord.y && z == coord.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }
}
