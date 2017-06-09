package mining.nodes;

import mining.Node;
import mining.data.Pickaxe;
import org.dreambot.api.methods.MethodContext;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.items.Item;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class BankHandler extends Node {

    public BankHandler(MethodContext context) {
        super(context);
    }

    public void execute() {
        if (api().getBank().isOpen()) {
            withdrawItems();
        } else {
            api().getBank().open(BankLocation.VARROCK_EAST);
        }
    }

    private void withdrawItems() {
        Optional<Pickaxe> pickaxe = Pickaxe.getBest();

        if (pickaxe.isPresent()) {
            if (api().getBank().getWithdrawMode().equals(BankMode.ITEM)) {
                if (pickaxe.get().isInEquipment()) {
                    handleWhenInEquipment();
                } else if (pickaxe.get().isInInventory()) {
                    handleWhenInInventory(pickaxe.get());
                } else if (pickaxe.get().isInBank()) {
                    handleWhenInBank(pickaxe.get());
                }
            } else if (api().getBank().setWithdrawMode(BankMode.ITEM)) {
                sleepUntil(() -> api().getBank().getWithdrawMode().equals(BankMode.ITEM));
            }
        }
    }

    private void handleWhenInInventory(Pickaxe pickaxe) {
        if (getUniqueItemCount() > 2 || duplicateInInventory(pickaxe.getName())) {
            if (api().getBank().depositAllItems()) {
                sleepUntil(() -> api().getInventory().isEmpty());
            }
        } else if (api().getInventory().emptySlotCount() != 27
                && api().getBank().depositAllExcept(pickaxe.getName())) {
            closeBank(() -> api().getInventory().emptySlotCount() == 27);
        } else {
            closeBank(() -> api().getInventory().emptySlotCount() == 27);
        }
    }

    private void handleWhenInBank(Pickaxe pickaxe) {
        if (!api().getInventory().isEmpty()) {
            if (api().getBank().depositAllItems()) {
                sleepUntil(() -> api().getInventory().isEmpty());
            }
        } else if (api().getBank().withdraw(pickaxe.getName(), 1)) {
            closeBank(() -> api().getInventory().contains(pickaxe.getName()) && getUniqueItemCount() == 1);
        }
    }

    private void handleWhenInEquipment() {
        if (!api().getInventory().isEmpty() && api().getBank().depositAllItems()) {
            closeBank(() -> api().getInventory().isEmpty());
        } else {
            closeBank(() -> api().getInventory().isEmpty());
        }
    }

    private void closeBank(Condition condition) {
        sleepUntil(condition);
        if (condition.verify() && api().getBank().close()) {
            sleepUntil(() -> !api().getBank().isOpen());
        }
    }

    private boolean duplicateInInventory(String name) {
        return api().getInventory().count(name) > 1;
    }


    private int getUniqueItemCount() {
        HashSet<Integer> uniques = new HashSet<>();
        for (Item item : api().getInventory().all(Objects::nonNull)) {
            uniques.add(item.getID());
        }
        return uniques.size();
    }

    public boolean shouldExecute() {
        Optional<Pickaxe> pickaxe = Pickaxe.getBest();
        return api().getBank().isOpen() || api().getInventory().isFull() || !pickaxe.isPresent();
    }

    @Override
    public boolean stopsExecution() {
        return true;
    }

}
