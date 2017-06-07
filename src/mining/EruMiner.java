package mining;

import mining.data.Pickaxe;
import mining.nodes.BankHandler;
import mining.nodes.MiningHandler;
import mining.nodes.PickaxeWielder;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

import java.util.ArrayList;

@ScriptManifest(name = "EruMiner", author = "Eru", category = Category.MINING, version = 1D)
public class EruMiner extends AbstractScript {
    private ArrayList<Node> tasks = new ArrayList<>();

    public static void main(String[] args) {
        new EruMiner();
    }

    @Override
    public void onStart() {
        super.onStart();
        Pickaxe.api = this;
        tasks.add(new BankHandler(this));
        tasks.add(new PickaxeWielder(this));
        tasks.add(new MiningHandler(this));
    }

    @Override
    public int onLoop() {
        for (Node task : tasks) {
            if (task.shouldExecute()) {
                task.execute();
                break;
            }
        }
        return (int) Calculations.nextGaussianRandom(450, 150);
    }

}
