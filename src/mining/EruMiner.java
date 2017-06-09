package mining;

import mining.data.Coord;
import mining.data.Pickaxe;
import mining.data.Rock;
import mining.nodes.BankHandler;
import mining.nodes.MiningHandler;
import mining.nodes.PickaxeWielder;
import mining.nodes.RockSelector;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.core.Instance;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

@ScriptManifest(name = "EruMiner", author = "Eru", category = Category.MINING, version = 1D)
public class EruMiner extends AbstractScript {
    private ArrayList<Node> tasks = new ArrayList<>();
    private boolean selectionEnabled = false;


    public static void main(String[] args) {
        new EruMiner();
    }

    @Override
    public void onStart() {
        super.onStart();

        Instance.getInstance().getCanvas().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    selectionEnabled = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        Pickaxe.api = this;
        tasks.add(new RockSelector(this));
        tasks.add(new BankHandler(this));
        tasks.add(new PickaxeWielder(this));
        tasks.add(new MiningHandler(this));
    }

    @Override
    public void onExit() {
        tasks.forEach(Node::kill);
    }

    @Override
    public int onLoop() {
        for (Node task : tasks) {
            if (task.shouldExecute()) {
                task.execute();
                if (task.stopsExecution()) break;
            }
        }
        return (int) Calculations.nextGaussianRandom(450, 150);
    }

    @Override
    public void onPaint(Graphics graphics) {
        super.onPaint(graphics);
        Rock.drawSelectedTiles(graphics);
    }
}
