package src.Tasks;

import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.Script;
import src.Settings;

public class Return extends Task {
    public Return(Script script) {
        super(script);
    }

    @Override
    public boolean canProcess() {
        return !script.getInventory().isFull() && !Settings.MiningArea.contains(script.myPlayer());
    }

    @Override
    public void process() {
        script.getSettings().setRunning(true);
        WebWalkEvent webwalkEvent = new WebWalkEvent(Settings.StartPosition);
        webwalkEvent.setMinDistanceThreshold(0);
        script.execute(webwalkEvent);
    }

    @Override
    public String TaskState() {
        return "Returning";
    }
}
