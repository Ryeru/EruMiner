package mining.nodes;

import mining.Node;
import mining.data.Coord;
import mining.data.Rock;
import org.dreambot.api.methods.MethodContext;
import org.dreambot.core.Instance;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RockSelector extends Node {

    private boolean selecting = false;
    private boolean deselecting = false;
    private volatile boolean isRunning = true;

    public RockSelector(MethodContext api) {
        super(api);
        addKeyListener();
        startListenerThread();
    }

    private void startListenerThread() {
        new Thread(() -> {
            while (isRunning) {
                if (deselecting) {
                    api().getMouse().getEntitiesOnCursor().forEach(entity -> {
                        String name;
                        if (entity != null && (name = entity.getName()) != null && name.equals("Rocks")) {
                            Coord tile = new Coord(entity.getTile());
                            if (Rock.getSelectedCoords().contains(tile)) {
                                Rock.removeSelectedTile(tile);
                            }
                        }
                    });
                } else if (selecting) {
                    api().getMouse().getEntitiesOnCursor().forEach(entity -> {
                        String name;
                        if (entity != null && (name = entity.getName()) != null && name.equals("Rocks")) {
                            Coord tile = new Coord(entity.getTile());
                            if (!Rock.isSelected(tile)) {
                                Rock.addSelectedTile(tile);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void addKeyListener() {
        Instance.getInstance().getCanvas().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    deselecting = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    selecting = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    deselecting = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    selecting = false;
                }
            }
        });
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean shouldExecute() {
        return false;
    }

    @Override
    public boolean stopsExecution() {
        return true;
    }

    @Override
    public void kill() {
        super.kill();
        isRunning = false;
    }
}
