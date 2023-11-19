package org.arathok.wurmunlimited.mods.branchesWithPruning;

import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import jdk.jfr.internal.JVM;
import org.gotti.wurmunlimited.modloader.interfaces.*;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BranchesWithPruning implements WurmServerMod, Initable, PreInitable, Configurable, ItemTemplatesCreatedListener, ServerStartedListener, ServerPollListener, PlayerMessageListener,PlayerLoginListener{
    public static Logger logger=Logger.getLogger("BranchesWithPruning");

    @Override
    public void configure(Properties properties) {


       Config.guaranteedBranchAt = Float.parseFloat(properties.getProperty("guaranteedBranchAt", "50.0"));
       Config.onlySuccessfullPruning=Boolean.parseBoolean(properties.getProperty("onlySuccessfullPruning","true"));


    }

    @Override
    public void onItemTemplatesCreated() {

    }

    @Override
    public void onPlayerLogin(Player player) {

    }

    @Override
    public boolean onPlayerMessage(Communicator communicator, String s) {
        return false;
    }

    @Override
    public void onServerPoll() {

    }

    @Override
    public void onServerStarted() {

    }

    @Override
    public void init() {
        logger.log(Level.INFO,"inserting pruning hook for terraforming actions");
        PruningHook.terraformingwrapper();
        logger.log(Level.INFO,"inserting pruning hook for trellis actions");
        PruningHook.trellisWrapper();
        logger.log(Level.INFO,"inserting pruning hook for hedge actions");
        PruningHook.hedgeWrapper();
    }

    @Override
    public void preInit() {

    }
}
