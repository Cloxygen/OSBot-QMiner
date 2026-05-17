package src.Tasks;

import org.osbot.rs07.script.Script;

public abstract class Task {
    protected Script script;

    public Task(Script script) {
        this.script = script;
    }

    public abstract boolean canProcess();

    public abstract void process();

    public abstract String TaskState();

    public void run() {
        if (canProcess())
            process();
    }
} 