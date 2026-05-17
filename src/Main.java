package src;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import src.Tasks.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ScriptManifest(author = "Cloxygen", name = "QMiner", info = "A mining script for any normal ores, works at any location", version = 2.1, logo = "https://iili.io/dV18Ymv.png")

public class Main extends Script {
    GUI gui;
    List<Task> tasks = new ArrayList<>();
    int previousExp = 0;

    @Override
    public void onStart(){
        Settings.StartPosition = myPlayer().getPosition();
        Settings.SetMiningArea(this);
        Settings.SetNearbyOres(this);

        gui = new GUI(this, Settings.NearbyOres);
        getBot().addMouseListener(GUI.ClickListener);
        experienceTracker.start(Skill.MINING);

        tasks.add(new Drop(this, Settings.Pickaxes));
        tasks.add(new Bank(this));
        tasks.add(new Return(this));
        tasks.add(new Mine(this, Settings.MiningArea, Settings.SelectedOrePositions));

        getSettings().setRunning(true);
    }

    @Override
    public int onLoop() {
        if(Settings.Started) {
            for (Task task : tasks) {
                if(task.canProcess()) {
                    Settings.StateString = task.TaskState();
                    task.process();
                    UpdateOreCount();
                    return 100;
                }
            }
        }
        else
            Settings.StateString = "Select Ores";
        return 100;
    }

    private void UpdateOreCount(){
        int gainedExp = experienceTracker.getGainedXP(Skill.MINING);
        if(gainedExp != previousExp){
            Settings.Stats.OreMinedCount++;
            previousExp = gainedExp;
        }

    }

    public void onPaint(Graphics2D g){
        GUI.Paint.DrawPaint(g, this);
    }
}
