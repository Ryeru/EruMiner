package mining.nodes;

import mining.Node;
import mining.data.Coord;
import mining.data.Rock;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodContext;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MiningHandler extends Node {

    private static final int MINING_ANIMATION = 600;
    //Varrock East
    private Tile miningLocationTile = new Tile(3287, 3363);
    // Varrock West
    // private Tile miningLocationTile = new Tile(3181, 3371);

    public MiningHandler(MethodContext context) {
        super(context);
    }

    private Tile tileInFrontOfPlayer(Player player) {
        double orientation = player.getOrientation();
        double x = Math.sin(((orientation / 2048)) * (2 * Math.PI)) * -1D;
        double y = Math.cos(((orientation / 2048)) * (2 * Math.PI)) * -1D;

        return player.getTile().translate((int) Math.round(x), (int) Math.round(y));
    }

    @Override
    public void execute() {
        HashSet<GameObject> inUse = getAlreadyInUseRocks();
        Optional<GameObject> rock = nearest(inUse);
        Iterator<Coord> coordIterator;
        if (rock.isPresent()) {
            if (rock.get().isOnScreen() && rock.get().distance() < 7) {
                if (stoppedMining() && rock.get().interact("Mine")) {
                    sleepUntil(() -> api().getLocalPlayer().getAnimation() >= MINING_ANIMATION, 2000, 500);
                } else {
                    Point point = rock.get().getClickablePoint();
                    if (point != null) {
                        api().getMouse().move(point);
                    }
                    sleepUntil(this::stoppedMining, 5000, 3000);
                }
            } else if (rock.get().distance() > 4) {
                if (api().getWalking().walk(rock.get().getTile())) {
                    sleepUntil(() -> {
                        Optional<GameObject> newRock = nearest(inUse);
                        return newRock.isPresent() && newRock.get().isOnScreen();
                    });
                }
            } else if (api().getCamera().mouseRotateToEntity(rock.get())) {
                sleepUntil(() -> {
                    Optional<GameObject> newRock = nearest(inUse);
                    return newRock.isPresent() && newRock.get().isOnScreen();
                });
            }
        } else if (Rock.getSelectedCoords().size() == 1
                && (coordIterator = Rock.getSelectedCoords().iterator()).hasNext()
                && coordIterator.next().toTile().distance() < 15) {
            sleepUntil(() -> {
                Optional<GameObject> newRock = nearest(inUse);
                return newRock.isPresent() && newRock.get().isOnScreen();
            });
        } else if (api().getWalking().walk(miningLocationTile)) {
            int randomDistance = (int) Calculations.nextGaussianRandom(5, 3);
            sleepUntil(() -> api().getWalking().getDestinationDistance() < randomDistance, 3200, 200);
        }
    }

    private boolean stoppedMining() {
        GameObject facingRock = api().getGameObjects().getTopObjectOnTile(tileInFrontOfPlayer(api().getLocalPlayer()));

        short[] colours;
        return !(facingRock != null && (colours = facingRock.getModelColors()) != null
                && colours.length > 0) || api().getPlayers().localPlayer().getAnimation() < MINING_ANIMATION;
    }

    private Optional<GameObject> nearest(HashSet<GameObject> inUse) {
        boolean hasSelectedRocks = Rock.getSelectedCoords().size() > 0;
        HashSet<Short> colours = new HashSet<>();
        List<GameObject> unusedObjects = new LinkedList<>();
        List<GameObject> objects = new LinkedList<>();

        for (GameObject gameObject : api().getGameObjects().all()) {
            String name;
            short[] modelColours;

            if (gameObject != null && (name = gameObject.getName()) != null && name.equals("Rocks")
                    && (modelColours = gameObject.getModelColors()) != null && modelColours.length > 0
                    && gameObject.distance() < 12) {

                Coord tile = new Coord(gameObject.getTile());
                colours.add(modelColours[0]);

                if (!hasSelectedRocks || Rock.getSelectedCoords().contains(tile)) {
                    if (!inUse.contains(gameObject) && gameObject.distance() < 5) {
                        unusedObjects.add(gameObject);
                    } else {
                        objects.add(gameObject);
                    }
                }
            }
        }

        return bestObjectMatch(colours, unusedObjects, objects);
    }

    private Optional<GameObject> bestObjectMatch(HashSet<Short> colours, List<GameObject> notInUse, List<GameObject> inUse) {
        notInUse.sort((o1, o2) -> (int) (o1.distance() - o2.distance()));
        inUse.sort((o1, o2) -> (int) (o1.distance() - o2.distance()));

        for (Rock rock : Rock.values()) {
            if (rock.canMine(api().getSkills().getRealLevel(Skill.MINING))
                    && colours.contains(rock.getModelColour())) {
                for (GameObject object : notInUse) {
                    if (object.getModelColors()[0] == rock.getModelColour()) {
                        return Optional.of(object);
                    }
                }
                for (GameObject object : inUse) {
                    if (object.getModelColors()[0] == rock.getModelColour()) {
                        return Optional.of(object);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private GameObject getAlreadyInUseRock(Player player) {
        return api().getGameObjects().getTopObjectOnTile(tileInFrontOfPlayer(player));
    }

    private HashSet<GameObject> getAlreadyInUseRocks() {
        HashSet<GameObject> inUse = new HashSet<>();
        for (Player player : api().getPlayers().all(player -> player.distance() <= 7
                && player.getAnimation() >= MINING_ANIMATION)) {
            inUse.add(getAlreadyInUseRock(player));
        }
        return inUse;
    }

    public boolean shouldExecute() {
        return true;
    }

    @Override
    public boolean stopsExecution() {
        return true;
    }
}
