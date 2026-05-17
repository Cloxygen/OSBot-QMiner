package src.Tasks;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.InteractionEvent;
import org.osbot.rs07.script.Script;

import src.Settings;
import src.Sleep;

import java.util.List;

public class Mine extends Task {
    Area miningArea;
    List<Position> selectedOrePositions;
    Position hoverPos;
    public static RS2Object activeOre;
    short[] activeOreColors;
    short[] hoverOreColors;
    boolean isHovering = false;

    public Mine(Script script, Area miningArea, List<Position> selectedOrePositions) {
        super(script);
        this.miningArea = miningArea;
        this.selectedOrePositions = selectedOrePositions;
    }

    @Override
    public boolean canProcess() {
        if (script.getInventory().isFull())
           return false;
        if (!miningArea.contains(script.myPlayer()))
           return false;
        CheckSelectedOres();
        return true;
    }

    @Override
    public void process() {
        MoveCamera();
        if(script.getSettings().getRunEnergy() > 50 && !script.getSettings().isRunning())
            script.getSettings().setRunning(true);

        if (activeOre == null) {
            SetActiveOre();
            if(activeOre == null && Settings.ShouldWorldHop)
                WorldHop();
            if(activeOre != null)
                MineActiveOre();
            return;
        }

        UpdateActiveOreColors();
        if (activeOreColors == null) {
            activeOre = null;
            return;
        }

        if(isHovering){
            if(!IsHoverOreValid())
                ClearHover();
        }

        if (!isHovering && !script.myPlayer().isMoving()) {
            HoverNextOre();
            return;
        }

        if (!script.myPlayer().isAnimating() && !script.myPlayer().isMoving()) {
            MineActiveOre();
        }
    }

    @Override
    public String TaskState() {
        return "Mining";
    }

    void MoveCamera(){
        if(script.getCamera().getPitchAngle() < 60)
            script.getCamera().toTop();
    }

    void CheckSelectedOres(){
        if (selectedOrePositions.isEmpty()){
            script.log("No ore selected. Ending script.");
            script.stop(false);
        }
    }

    void UpdateActiveOreColors(){
        Position posHolder = activeOre.getPosition();
        activeOre = script.getObjects().closest(j -> j != null && j.getPosition().equals(posHolder) && j.hasAction("Mine"));
        activeOreColors = (activeOre != null) ? activeOre.getDefinition().getOriginalModelColors() : null;
    }

    void SetActiveOre(){
        if(!isHovering) {
            activeOre = FindValidOre();
            return;
        }
        if(IsHoverOreValid())
            MoveHoverToActive();
        else {
            ClearHover();
            activeOre = FindValidOre();
        }
    }

    void MoveHoverToActive(){
        script.log("Setting hover to active ore.");
        activeOre = script.getObjects().closest(
                j -> j != null && j.getPosition().equals(hoverPos) && j.hasAction("Mine")
        );
        hoverPos = null;
        isHovering = false;
    }

    RS2Object FindValidOre(){
        RS2Object checkOre = null;
        checkOre = script.getObjects().closest(
                j -> j != null
                && selectedOrePositions.contains(j.getPosition())
                && (activeOre != null ? !activeOre.getPosition().equals(j.getPosition()) : true)
                && j.hasAction("Mine")
                && j.getDefinition().getOriginalModelColors() != null
        );

        return checkOre;
    }

    void MineActiveOre() {
        InteractionEvent mineOre = new InteractionEvent(activeOre,"Mine");
        mineOre.setWalkTo(false);
        script.execute(mineOre);
        Sleep.sleepUntil(() -> script.myPlayer().isAnimating() && !script.myPlayer().isMoving(), 1500);
    }

    void HoverNextOre(){
        RS2Object hoverOre = FindValidOre();
        if (hoverOre != null) {
            hoverPos = hoverOre.getPosition();
            hoverPos.hover(script.getBot());
            isHovering = true;
        }
    }

    boolean IsHoverOreValid(){
        if (hoverPos == null) {
            return false;
        }
        UpdateHoverOreColors();
        return hoverOreColors != null;
    }

    void ClearHover(){
        hoverPos = null;
        hoverOreColors = null;
        isHovering = false;
    }
    void UpdateHoverOreColors(){
        RS2Object hoverOre = script.getObjects().closest(j -> j != null && j.getPosition().equals(hoverPos) && j.hasAction("Mine"));
        hoverOreColors = (hoverOre != null) ? hoverOre.getDefinition().getOriginalModelColors() : null;
    }

    void WorldHop(){
        if(script.getClient().isMember()) {
            int currentWorld = script.getWorlds().getCurrentWorld();
            script.getWorlds().hopToP2PWorld();
            Sleep.sleepUntil(() -> script.getWorlds().getCurrentWorld() != currentWorld, 5000);
        }
        else {
            int currentWorld = script.getWorlds().getCurrentWorld();
            script.getWorlds().hopToF2PWorld();
            Sleep.sleepUntil(() -> script.getWorlds().getCurrentWorld() != currentWorld, 5000);
        }
    }
}

