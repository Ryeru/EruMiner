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
        Optional<Pickaxe> pickaxe = Pickaxe.getBest();
        if (pickaxe.isPresent() && pickaxe.get().isInInventory() &&
                api().getInventory().get(pickaxe.get().getName()).interact("Wield")) {
            sleepUntil(() -> pickaxe.get().isInEquipment());
        }
    }

    public boolean shouldExecute() {
        Optional<Pickaxe> pickaxe = Pickaxe.getBest();
        return pickaxe.isPresent() && pickaxe.get().canWear() && pickaxe.get().isInInventory();
    }
}
