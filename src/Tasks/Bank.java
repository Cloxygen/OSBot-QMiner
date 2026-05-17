package src.Tasks;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.Script;
import src.Settings;
import src.Sleep;

import java.util.ArrayList;
import java.util.Arrays;

public class Bank extends Task {
    Area SHILOBANK = new Area(new Position(2854,2957,0),new Position(2850,2951,0));
    Area PORTSARIMBANK = new Area(new Position(3047,3233,0),new Position(3043,3237,0));
    Area MININGGUILDBANK = new Area(new Position(3018,9716, 0), new Position(3011, 9721, 0));
    Area SHILOMINEBANK = new Area(new Position(2845, 9379, 0), new Position(2839, 9388, 0));
    Area SHILOMINE = new Area(new Position(2860, 9371, 0), new Position(2820, 9402, 0));
    Area DUEL_ARENA = new Area(new Position(3381, 3272, 0), new Position(3387, 3267, 0));
    org.osbot.rs07.api.map.Area[] f2pBanks = {
            Banks.AL_KHARID, Banks.DRAYNOR, Banks.EDGEVILLE, Banks.FALADOR_EAST, Banks.FALADOR_WEST,
            Banks.GRAND_EXCHANGE, Banks.LUMBRIDGE_UPPER, Banks.TZHAAR, Banks.VARROCK_EAST, Banks.VARROCK_WEST,
            PORTSARIMBANK, DUEL_ARENA
    };
    org.osbot.rs07.api.map.Area[] allBanks = {
            Banks.AL_KHARID, Banks.ARCEUUS_HOUSE, Banks.ARDOUGNE_NORTH, Banks.ARDOUGNE_SOUTH, Banks.CAMELOT, Banks.CANIFIS,
            Banks.CASTLE_WARS, Banks.CATHERBY, Banks.DRAYNOR, Banks.EDGEVILLE, Banks.FALADOR_EAST, Banks.FALADOR_WEST,
            Banks.GNOME_STRONGHOLD, Banks.GRAND_EXCHANGE, Banks.HOSIDIUS_HOUSE, Banks.LOVAKENGJ_HOUSE, Banks.LOVAKITE_MINE,
            Banks.LUMBRIDGE_UPPER, Banks.PEST_CONTROL, Banks.PISCARILIUS_HOUSE, Banks.SHAYZIEN_HOUSE,
            Banks.TZHAAR, Banks.VARROCK_EAST, Banks.VARROCK_WEST, Banks.YANILLE,
            SHILOBANK,PORTSARIMBANK, MININGGUILDBANK, SHILOMINEBANK, DUEL_ARENA
    };
    private final String[] bankActions = {
            "Bank","Deposit"
    };
    ArrayList<Area> banksList = new ArrayList<Area>();
    Area closestBank;

    public Bank(Script script) {
        super(script);
        SetBanksList();
        RemoveUnusableBanks();
        closestBank = FindClosestBank();
    }

    @Override
    public boolean canProcess() {
        return script.getInventory().isFull() && Settings.ShouldBank;
    }

    @Override
    public void process() {
        if(!closestBank.contains(script.myPlayer())) {
            script.log("Walking to bank");
            WalkToBank();
        }
        else
            BankItems();
    }

    private void WalkToBank(){
        script.getSettings().setRunning(false);
        WebWalkEvent webwalkEvent = new WebWalkEvent(closestBank);
        script.execute(webwalkEvent);
    }

    @Override
    public String TaskState() {
        return "Banking";
    }

    void RemoveUnusableBanks(){
        if(script.getSkills().getStatic(Skill.MINING) < 60)
            banksList.remove(MININGGUILDBANK);
        if(!SHILOMINE.contains(script.myPlayer())){
            banksList.remove(SHILOMINEBANK);
        }
    }

    void SetBanksList() {
        if (script.getWorlds().isMembersWorld()){
            banksList.addAll(Arrays.asList(allBanks));
        }
        else{
            banksList.addAll(Arrays.asList(f2pBanks));
        }

    }

    Area FindClosestBank(){
        Area[] bankArray = new Area[banksList.size()];
        bankArray = banksList.toArray(bankArray);
        WebWalkEvent webwalkEvent = new WebWalkEvent(bankArray);
        webwalkEvent.setMinDistanceThreshold(0);
        webwalkEvent.prefetchRequirements(script);
        Position destination = webwalkEvent.getDestination();

        for (Area area : banksList) {
            if (area.contains(destination))
                return area;
        }
        return null;
    }

    void BankItems(){
        Entity bankEntity = FindBankEntity();
        if(!script.getBank().isOpen() && !script.getDepositBox().isOpen()) {
            if (bankEntity != null) {
                bankEntity.interact(bankActions);
                Sleep.sleepUntil(() -> script.getBank().isOpen() || script.getDepositBox().isOpen(), 2500);
            }
            else {
                script.log("Bank not found, ending script.");
                script.stop(false);
            }
        }
        else {
            script.log("despoiting items");
            if(bankEntity.hasAction("Deposit")) {
                script.getDepositBox().depositAllExcept(Settings.Pickaxes);
                script.getDepositBox().close();
                Sleep.sleepUntil(() -> !script.getDepositBox().isOpen(), 1000);
            }
            else {
                script.getBank().depositAllExcept(Settings.Pickaxes);
                script.getBank().close();
                Sleep.sleepUntil(() -> !script.getBank().isOpen(), 1000);
            }
        }
    }

    Entity FindBankEntity(){
        Entity bankEntity = null;

        bankEntity = script.getObjects().closest(i -> i != null && i.hasAction(bankActions));
        if(bankEntity != null)
            return bankEntity;

        bankEntity = script.getNpcs().closest(i -> i != null && i.hasAction(bankActions));
        return bankEntity;
    }
}
