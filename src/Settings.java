package src;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Settings {
    public static Area MiningArea;
    public static Position StartPosition;
    public static List<RS2Object> NearbyOres;
    public static List<Position> SelectedOrePositions = new ArrayList<>();
    public static int MiningRadius = 5;
    public static boolean ShouldBank = false;
    public static boolean ShouldWorldHop = false;
    public static boolean Started = false;
    public static boolean HidePaint = false;
    public static boolean FatigueEnabled = true;
    public static String StateString = "Waiting";
    public final static String[] Pickaxes = {
            "Bronze pickaxe","Iron pickaxe","Steel pickaxe","Black pickaxe","Mithril pickaxe",
            "Adamant pickaxe","Rune pickaxe","Dragon pickaxe","3rd age pickaxe","Infernal pickaxe", "Crystal pickaxe"
    };

    public static void SetMiningArea(Script script){
        Settings.MiningArea = script.myPlayer().getArea(Settings.MiningRadius);
    }

    public static void SetNearbyOres(Script script){
        Settings.NearbyOres = script.getObjects().getAll().stream().filter(o -> o.hasAction("Mine") && Settings.MiningArea.contains(o)).collect(Collectors.toList());
    }

    public static class Stats{
        public static int OreMinedCount = 0;
    }
}
