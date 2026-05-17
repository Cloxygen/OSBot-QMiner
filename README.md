# QMiner

> [!WARNING]  
> **Legacy Archive Notice**: OSBot has transitioned to a non-Java client, meaning Java-based scripts are no longer supported. This project is deprecated and non-functional. It is preserved here solely as an archived reference for custom state-machine (State/Task) botting architecture in Java.

An All-in-One (AIO) mining script for OSBot that worked in any location. It featured interactive on-screen ore selection, mouse pre-hovering, and options for banking, dropping, and world-hopping.

---

## 📸 Screenshots



---

## Features (Historical)

* **Interactive HUD**: Showed runtime, XP gained, XP/hr, level progress bar, and simple toggle buttons for banking and world-hopping.
* **On-Screen Selection**: Clicked rocks directly in the game window to select or deselect them before starting.
* **Pre-Hovering**: Moved the mouse to the next rock while still mining the current one, keeping XP rates competitive and simulating human behavior.
* **Smart Banking**: Found the closest F2P or Members bank, walked there using web-walking, and deposited everything except pickaxes.
* **Power Mining**: Dropped ores instantly when the inventory was full (keeping waterskins, coins, and pickaxes safe).
* **World Hopping**: Automatically hopped worlds if all selected rocks were depleted (supported members and F2P world separation).
* **Auto-Return**: If the player got dragged out of the selected mining area, the script automatically walked back to the starting position.

---

## Codebase Structure

The script is built around a prioritized list of tasks (State/Task pattern) located in `src/Tasks`. Every loop iteration (every 100ms), the script ran the first task in the list that was ready.

### Core System
* [Main.java](src/Main.java) - The script entry point. Set up the initial configuration, handled the task list loop, and managed GUI updates.
* [Settings.java](src/Settings.java) - Held configurations (like toggles, starting position, and selected rocks) and active stats.
* [GUI.java](src/GUI.java) - Managed the custom screen overlay, custom mouse listener for selecting rocks, and HUD toggles.
* [Sleep.java](src/Sleep.java) - Wrapper for OSBot's `ConditionalSleep` to allow waiting for specific conditions.
* [Utility.java](src/Utility.java) - Simple helpers (e.g. converting milliseconds to a readable time format).
* [Fatigue.java](src/Fatigue.java) - Placeholder for future fatigue/exhaustion logic.

### Tasks (`src/Tasks`)
* [Task.java](src/Tasks/Task.java) - Base abstract class for tasks.
* [Mine.java](src/Tasks/Mine.java) - Mining, pre-hovering next rock, and world-hopping logic.
* [Bank.java](src/Tasks/Bank.java) - Dynamically checked for the nearest bank and deposited inventory.
* [Drop.java](src/Tasks/Drop.java) - Dropped ores for power-mining while keeping pickaxes and waterskins safe.
* [Return.java](src/Tasks/Return.java) - Safety return walking to the start position if the player drifted away.

---

## State Flow Diagram

```mermaid
flowchart TD
    Start([onLoop Start]) --> CheckStarted{Script Started?}
    
    CheckStarted -- No --> StateSelect[Set State: 'Select Ores'] --> LoopEnd([100ms Sleep])
    CheckStarted -- Yes --> Prioritize{Check Task Priority}
    
    Prioritize --> TaskDrop[1. Drop]
    TaskDrop -- Inventory Full & Banking Off --> ExecDrop[Drop Items] --> StateDrop[State: 'Dropping'] --> LoopEnd
    TaskDrop -- false --> TaskBank[2. Bank]
    
    TaskBank -- Inventory Full & Banking On --> ExecBank[Walk & Bank] --> StateBank[State: 'Banking'] --> LoopEnd
    TaskBank -- false --> TaskReturn[3. Return]
    
    TaskReturn -- Not Full & Out of Area --> ExecReturn[Walk to Start] --> StateReturn[State: 'Returning'] --> LoopEnd
    TaskReturn -- false --> TaskMine[4. Mine]
    
    TaskMine -- Ready --> ExecMine[Mine & Pre-Hover] --> StateMine[State: 'Mining'] --> LoopEnd
    TaskMine -- false --> LoopEnd
```

---

## Historical Setup & Running (Java Client)

*These instructions are preserved for historical reference only. They are no longer functional under OSBot's current non-Java architecture.*

### Prerequisites
1. Download the [OSBot Client](https://osbot.org/).
2. Make sure JDK 8 is installed.

### Setup
1. Import the project into your IDE (like IntelliJ IDEA using `QMiner.iml`).
2. Add `osbot-client.jar` as a dependency.
3. Build the project as a `.jar` file.
4. Copy the compiled JAR into your OSBot scripts folder: `%USERPROFILE%/OSBot/Scripts/`

### How to use
1. Log in to OSBot and stand in your preferred mining spot.
2. Launch the **QMiner** script.
3. **Select your rocks**: Click on the rocks you want to mine directly on the game screen. They will draw outlines. You can click them again to remove them.
4. Adjust your settings on the bottom-left panel:
   * **Banking**: Toggle to `On` to walk to the bank, or `Off` to power-mine.
   * **World Hop**: Toggle `On` to hop worlds when all selected rocks are depleted.
5. Click the green **Start** button.
