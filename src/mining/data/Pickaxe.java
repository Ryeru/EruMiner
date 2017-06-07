package mining.data;

import org.dreambot.api.methods.MethodContext;
import org.dreambot.api.methods.skills.Skill;

import java.util.Optional;

public enum Pickaxe {
    RUNE("Rune pickaxe", 41),
    ADAMANT("Adamant pickaxe", 31),
    MITHRIL("Mithril pickaxe", 21),
    BLACK("Black pickaxe", 11),
    STEEL("Steek pickaxe", 6),
    IRON("Iron pickaxe", 1),
    BRONZE("Bronze pickaxe", 1);

    public static MethodContext api;

    String name;
    int requiredLevel;

    Pickaxe(String name, int requiredLevel) {
        this.name = name;

        this.requiredLevel = requiredLevel;
    }

    public static Optional<Pickaxe> getBest() {
        for (Pickaxe axe : values()) {
            if ((axe.isInBank() || axe.isInInventory() || axe.isInEquipment()) && axe.canUse()) {
                return Optional.of(axe);
            }
        }
        return Optional.empty();
    }

    public boolean canUse() {
        return api.getSkills().getRealLevel(Skill.MINING) >= this.requiredLevel;
    }

    public boolean canWear() {
        return api.getSkills().getRealLevel(Skill.ATTACK) >= this.requiredLevel - 1;
    }

    public boolean isInInventory() {
        return api.getInventory().contains(this.getName());
    }

    public boolean isInBank() {
        return api.getBank().contains(this.getName());
    }

    public boolean isInEquipment() {
        return api.getEquipment().contains(this.getName());
    }

    public String getName() {
        return name;
    }

}
