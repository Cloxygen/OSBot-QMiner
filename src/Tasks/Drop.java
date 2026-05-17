package src.Tasks;

import org.osbot.rs07.script.Script;
import src.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Drop extends Task {

    List<String> dontDrop = new ArrayList<>(
            Arrays.asList(
            "Waterskin(1)","Waterskin(2)","Waterskin(3)","Waterskin(4)","Coins"
            )
    );

    public Drop(Script script, String[] pickaxes) {
        super(script);
        dontDrop.addAll(Arrays.asList(pickaxes));
    }

    @Override
    public boolean canProcess() {
        return script.getInventory().isFull() && !Settings.ShouldBank;
    }

    @Override
    public void process() {
        script.getInventory().dropAllExcept(dontDrop.toArray(new String[0]));
    }

    @Override
    public String TaskState() {
        return "Dropping";
    }
}
