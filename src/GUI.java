package src;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.input.mouse.BotMouseListener;
import org.osbot.rs07.script.Script;
import src.Tasks.Mine;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    protected Script script;
    protected List<RS2Object> nearbyOres = new ArrayList<>();
    public static BotMouseListener ClickListener;

    //UI Constants
    static final int
            panelX = 10,
            panelY = 305,
            panelWidth = 250,
            panelHeight = 190,
            padding = 10,
            startBtnWidth = 70,
            startBtnHeight = 25,
            bankToggleWidth = 40,
            bankToggleheight = 20,
            xBtnSize = 15
    ;
    static final int startBtnX = panelX + (panelWidth - startBtnWidth) / 2;
    static final int startBtnbtnY = panelY - startBtnHeight - 10;
    static final int bankToggleX = panelX + padding + 55;
    static final int bankToggleY = 464;
    static final int worldHopToggleX = bankToggleX + padding + 107;
    static final int xBtnX = panelX + panelWidth - xBtnSize - 5;
    static final int xBtnY = panelY + 5;
    static final Font headerFont = new Font("SansSerif", Font.BOLD, 16);
    static final Font textFont = new Font("SansSerif", Font.PLAIN, 13);
    static final Color bgColor = new Color(25, 25, 35);
    static final Color white = Color.WHITE;
    static final Color blue = new Color(12, 197, 210);
    static final Color pink = new Color(255, 75, 140);
    static final Color gold = new Color(255, 170, 0);
    static final Color green = new Color(0, 255, 140);
    static final Color darkGreen = new Color(0, 130, 80);

    public GUI(Script script, List<RS2Object> nearbyOres) {
        this.script = script;
        this.nearbyOres = nearbyOres;
        ClickListener = new BotMouseListener() {
            @Override
            public void checkMouseEvent(MouseEvent e) {
                if(e.getID() != MouseEvent.MOUSE_PRESSED)
                    return;

                if(Buttons.xButton.contains(e.getPoint())){
                    script.log("Toggling Paint");
                    Settings.HidePaint = !Settings.HidePaint;
                    e.consume();
                }
                if (Buttons.startButton.contains(e.getPoint())) {
                    Settings.Started = true;
                    script.log("Script Started");
                    e.consume();
                }
                if(!Settings.HidePaint) {
                    if (Buttons.bankToggle.contains(e.getPoint())) {
                        Settings.ShouldBank = !Settings.ShouldBank;
                        script.log("Toggling Bank");
                        e.consume();
                    }
                    if(Buttons.worldHopToggle.contains(e.getPoint())){
                        Settings.ShouldWorldHop = !Settings.ShouldWorldHop;
                        script.log("Toggling World Hopping");
                        e.consume();
                    }
                    if (!Settings.Started) {
                        if(DeselectClickedOre(e))
                            return;
                        SelectClickedOre(e);
                    }
                }
            }
        };
    }
    private boolean DeselectClickedOre(MouseEvent e){
        for (int i = 0; i < Settings.SelectedOrePositions.size(); i++) {
            if (Settings.SelectedOrePositions.get(i).getPolygon(script.getBot()).contains(e.getPoint())) {
                Settings.SelectedOrePositions.remove(Settings.SelectedOrePositions.get(i));
                e.consume();
                return true;
            }
        }
        return false;
    }

    private void SelectClickedOre(MouseEvent e){
        for (RS2Object nearbyOre : nearbyOres) {
            if (nearbyOre.getPosition().getPolygon(script.getBot()).contains(e.getPoint())) {
                Settings.SelectedOrePositions.add(nearbyOre.getPosition());
                e.consume();
                return;
            }
        }
    }

    public static class Buttons{
        private static final Rectangle bankToggle = new Rectangle(bankToggleX,bankToggleY,bankToggleWidth, bankToggleheight);
        private static final Rectangle worldHopToggle = new Rectangle(worldHopToggleX, bankToggleY, bankToggleWidth, bankToggleheight);
        private static final Rectangle startButton = new Rectangle(startBtnX,startBtnbtnY,startBtnWidth, startBtnHeight);
        private static final Rectangle xButton = new Rectangle(xBtnX, xBtnY, xBtnSize, xBtnSize);
    }

    public static class Paint{
        public static void DrawPaint(Graphics2D g, Script script){
            if(!Settings.HidePaint) {
                DrawCursor(g, script);
                if (Settings.MiningArea.contains(script.myPlayer())) {
                    DrawNearbyOres(g, script);
                    DrawSelectedOre(g, script);
                    DrawActiveOre(g, script);
                }
                DrawInfoPanel(g, script);
            }
            DrawHidePaintButton(g);
            if(!Settings.Started)
                DrawStartButton(g);
        }

        private static void DrawCursor(Graphics2D g, Script script){
            g.setColor(new Color(255, 255, 255, 100));
            Point mousePos = script.mouse.getPosition();
            g.drawLine(mousePos.x, 0, mousePos.x, 500); //draws vertical line from mouse position
            g.drawLine(0, mousePos.y, 764, mousePos.y); //draws horizontal line from mouse position
        }

        private static void DrawNearbyOres(Graphics2D g, Script script){
            if (!Settings.Started) {
                g.setColor(Color.WHITE);

                g.setColor(new Color(200, 200, 200, 100));
                for (int i = 0; i < Settings.NearbyOres.size(); i++) {
                    if (Settings.NearbyOres.get(i) != null) {
                        g.draw(Settings.NearbyOres.get(i).getPosition().getPolygon(script.getBot()));
                    }
                }
            }
        }

        private static void DrawSelectedOre(Graphics2D g, Script script){
            g.setColor(new Color(255, 255, 255, 150));
            for (Position pos : Settings.SelectedOrePositions) {
                if (pos != null) {
                    if(Mine.activeOre == null)
                        g.draw(pos.getPolygon(script.getBot()));
                    else if(!pos.equals(Mine.activeOre.getPosition())) {
                        g.draw(pos.getPolygon(script.getBot()));
                    }
                }
            }
        }

        private static void DrawActiveOre(Graphics2D g, Script script){
            if (Mine.activeOre != null) {
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(2));
                g.draw(Mine.activeOre.getPosition().getPolygon(script.getBot()));
            }
        }

        private static void DrawInfoPanel(Graphics2D g, Script script){
            // stats
            long elapsed = script.experienceTracker.getElapsed(Skill.MINING);
            String runtime = formatTime(elapsed);
            int oresPerHr = (int) (Settings.Stats.OreMinedCount * 3600000D / elapsed);
            int currentLevel = script.getSkills().getStatic(Skill.MINING);
            int expToLevel = script.getSkills().experienceToLevel(Skill.MINING);
            int expeBetweenLevels =
                    script.getSkills().getExperienceForLevel(currentLevel + 1) - script.getSkills().getExperienceForLevel(currentLevel);
            float progressPct = 100 - ((float)expToLevel / (float)expeBetweenLevels * 100);
            progressPct = Math.round(progressPct * 100f) / 100f;
            int experienceGained = script.experienceTracker.getGainedXP(Skill.MINING);
            int xpPerHr = script.experienceTracker.getGainedXPPerHour(Skill.MINING);
            int levelGain = script.experienceTracker.getGainedLevels(Skill.MINING);

            // Main panel
            g.setColor(bgColor);
            g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);

            // Title
            g.setFont(headerFont);
            g.setColor(gold);
            g.drawString("QMiner AIO v2.1", panelX + padding, panelY + 25);

            // Body text
            g.setFont(textFont);
            g.setColor(white);
            int textY = panelY + 50;
            g.drawString("Runtime: " + runtime, panelX + padding, textY); textY += 18;
            g.setColor(pink);
            g.drawString("Status: " + Settings.StateString, panelX + padding, textY); textY += 18;
            g.setColor(white);
            g.drawString("Ores Mined: " + Settings.Stats.OreMinedCount + " (" + oresPerHr + "/hr)", panelX + padding, textY); textY += 18;
            g.drawString("XP Gained: " + experienceGained + " (" + xpPerHr + "/hr)", panelX + padding, textY); textY += 18;
            g.drawString("Level: " + currentLevel + " (+" + levelGain + ")", panelX + padding, textY); textY += 10;

            // Progress bar
            int barWidth = panelWidth - 2 * padding;
            int barHeight = 20;
            int barX = panelX + padding;
            int barY = textY;
            g.setColor(gold);
            g.fillRect(barX, barY, (int)(barWidth * (progressPct / 100.0)), barHeight);
            g.setColor(new Color(40, 40, 40));
            g.fillRect(barX + (int)(barWidth * (progressPct / 100.0)), barY, barWidth - (int)(barWidth * (progressPct / 100.0)), barHeight);
            g.setColor(bgColor);
            g.drawRect(barX, barY, barWidth, barHeight);
            g.drawString(progressPct + "% to " + (currentLevel + 1), barX + 10, barY + 15);
            textY += 27;

            // Banking toggle
            g.setColor(white);
            g.drawString("Banking:", barX, textY + 15);
            g.setColor(Settings.ShouldBank ? green : darkGreen);
            g.fillRoundRect(bankToggleX, bankToggleY, bankToggleWidth, bankToggleheight, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(Settings.ShouldBank ? "On" : "Off", bankToggleX + 10, textY + 15);

            //World hopping toggle
            g.setColor(white);
            g.drawString("World hop:", bankToggleX + bankToggleWidth + padding, textY + 15);
            g.setColor(Settings.ShouldWorldHop ? green : darkGreen);
            g.fillRoundRect(worldHopToggleX, bankToggleY, bankToggleWidth, bankToggleheight, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(Settings.ShouldWorldHop ? "On" : "Off", worldHopToggleX + 10, textY + 15);
        }

        private static void DrawStartButton(Graphics2D g){
            Font headerFont = new Font("SansSerif", Font.BOLD, 16);
            g.setColor(green);
            g.fillRoundRect(startBtnX, startBtnbtnY, startBtnWidth, startBtnHeight, 10, 10);
            g.setColor(Color.BLACK);
            g.setFont(headerFont);
            g.drawString("Start", startBtnX + 16, startBtnbtnY + 18);
        }

        private static void DrawHidePaintButton(Graphics2D g){
            // Close (X) button
            g.setColor(white);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString("X", xBtnX + 3, xBtnY + 12);
        }

        // Helper
         private static String formatTime(long ms) {
            long s = ms / 1000 % 60;
            long m = ms / (1000 * 60) % 60;
            long h = ms / (1000 * 60 * 60);
            return String.format("%02d:%02d:%02d", h, m, s);
        }

    }
}
