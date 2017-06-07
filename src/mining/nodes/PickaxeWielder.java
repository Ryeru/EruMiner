package mining.nodes;

import mining.Node;
import mining.data.Pickaxe;
import org.dreambot.api.methods.MethodContext;

import java.util.Optional;

public class PickaxeWielder extends Node {

    public PickaxeWielder(MethodContext context) {
        super(context);
    }

    @Override
    public void execute() {
        Optional<Pickaxe> pickaxe = Pickaxe.getBest(api());
        if (pickaxe.isPresent() && pickaxe.get().isInInventory(api()) &&
                api().getInventory().get(pickaxe.get().getName()).interact("Wield")) {
            sleepUntil(() -> pickaxe.get().isInEquipment(api()));
        }
    }

    public boolean shouldExecute() {
        Optional<Pickaxe> pickaxe = Pickaxe.getBest(api());
        return pickaxe.isPresent() && pickaxe.get().canWear(api()) && pickaxe.get().isInInventory(api());
    }
}
