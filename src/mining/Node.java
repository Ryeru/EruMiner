package mining;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodContext;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.utilities.impl.Condition;

public abstract class Node {
    private MethodContext context;

    public Node(MethodContext context) {
        this.context = context;
    }

    public abstract void execute();

    public abstract boolean shouldExecute();

    protected MethodContext api() {
        return context;
    }

    protected void sleepUntil(Condition condition) {
        MethodProvider.sleepUntil(condition, (long) Calculations.nextGaussianRandom(600, 300));
    }

    protected void sleepUntil(Condition condition, int mean, int dev) {
        MethodProvider.sleepUntil(condition, (long) Calculations.nextGaussianRandom(mean, dev));
    }
}
