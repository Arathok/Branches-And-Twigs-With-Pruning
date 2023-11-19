package org.arathok.wurmunlimited.mods.branchesWithPruning;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;
import javassist.*;
import javassist.expr.ExprEditor;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.logging.Level;

public class PruningHook {

    public static void terraformingwrapper() {


        //static boolean prune(final Action action, final Creature performer, final Item sickle, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter) {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass terraforming = null;
        try {
            terraforming = classPool.getCtClass("com.wurmonline.server.behaviours.Terraforming");
        } catch (NotFoundException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "Class not found", e);
            throw new RuntimeException(e);
        }

        try {

            CtMethod prune = terraforming.getMethod("prune", "(Lcom/wurmonline/server/behaviours/Action;Lcom/wurmonline/server/creatures/Creature;Lcom/wurmonline/server/items/Item;IIILcom/wurmonline/mesh/Tiles$Tile;F)Z");
            //CtMethod prune = terraforming.getMethod("getCaveDoorDifference","(III)I");
            prune.insertAfter("org.arathok.wurmunlimited.mods.branchesWithPruning.PruningHook.generateBranch($_,$2,$3,$7,$8);");
        } catch (NotFoundException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "method not found", e);
            e.printStackTrace();

        } catch (CannotCompileException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "Code for hook is incorrect", e);
            e.printStackTrace();

        }
    }

    public static void trellisWrapper() {


        //static boolean prune(final Action action, final Creature performer, final Item sickle, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter) {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass trellis = null;
        try {
            trellis = classPool.getCtClass("com.wurmonline.server.behaviours.TrellisBehaviour");
        } catch (NotFoundException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "Class not found", e);
            throw new RuntimeException(e);
        }

        try {

            CtMethod prune = trellis.getDeclaredMethod("prune");
            //CtMethod prune = terraforming.getMethod("getCaveDoorDifference","(III)I");
            prune.insertAfter("org.arathok.wurmunlimited.mods.branchesWithPruning.PruningHook.generateTwig($_,$2,$3,$5);");
        } catch (NotFoundException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "method not found", e);
            e.printStackTrace();

        } catch (CannotCompileException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "Code for hook is incorrect", e);
            e.printStackTrace();

        }
    }

    public static void hedgeWrapper() {
        //static boolean pruneHedge(final Action action, final Creature performer, final Item sickle, final Fence hedge, final boolean onSurface, final float counter) {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass trellis = null;
        try {
            trellis = classPool.getCtClass("com.wurmonline.server.behaviours.Terraforming");
        } catch (NotFoundException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "Class not found", e);
            throw new RuntimeException(e);
        }

        try {

            CtMethod prune = trellis.getDeclaredMethod("pruneHedge");
            //CtMethod prune = terraforming.getMethod("getCaveDoorDifference","(III)I");
            prune.insertAfter("org.arathok.wurmunlimited.mods.branchesWithPruning.PruningHook.generateTwig($_,$2,$3,$6);"); //$_ is the returnValue of the method
        } catch (NotFoundException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "method not found", e);
            e.printStackTrace();

        } catch (CannotCompileException e) {
            BranchesWithPruning.logger.log(Level.WARNING, "Code for hook is incorrect", e);
            e.printStackTrace();

        }
    }

    public static void generateBranch(boolean result, Creature performer,Item sickle, Tiles.Tile theTile,float counter) {
        try {
            final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
            final Skill sickskill = performer.getSkills().getSkillOrLearn(10046);
            int time = 150;
            if (sickle.getTemplateId() == 267) {
                time = Actions.getStandardActionTime(performer, forestry, sickle, sickskill.getKnowledge(0.0));
            }
            if (counter*10>=time)
            if (!Config.onlySuccessfullPruning || !result) {
                boolean success = false;
                int chance = 0;
                chance = (int) ((performer.getSkills().getSkillOrLearn(10048).getKnowledge() / Config.guaranteedBranchAt) * 100);
                if (chance > 100)
                    success = true;
                if (com.wurmonline.server.Server.rand.nextInt(100) < chance)
                    success = true;
                if (success && theTile.isTree()) {
                    com.wurmonline.server.items.Item branch = ItemFactory.createItem(688, (float) java.lang.Math.min(performer.getSkills().getSkillOrLearn(10048).getKnowledge(), (float) Server.rand.nextInt(100)), performer.getName());
                    performer.getInventory().insertItem(branch);
                    performer.getCommunicator().sendSafeServerMessage("you manage to also find a branch while pruning!");
                } else if (success && theTile.isBush()) {
                    Item twig = ItemFactory.createItem(1353, (float) java.lang.Math.min(performer.getSkills().getSkillOrLearn(10048).getKnowledge(), (float) Server.rand.nextInt(100)), performer.getName());
                    performer.getInventory().insertItem(twig);
                    performer.getCommunicator().sendSafeServerMessage("you manage to also find a twig while pruning!");
                }
            }

        } catch (NoSuchTemplateException | FailedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateTwig(boolean result, Creature performer,Item sickle,float counter) {
        try {
            final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
            final Skill sickskill = performer.getSkills().getSkillOrLearn(10046);
            int time = 150;
            if (sickle.getTemplateId() == 267) {
                time = Actions.getStandardActionTime(performer, forestry, sickle, sickskill.getKnowledge(0.0));
            }
            if (counter*10>=time)
                if (!Config.onlySuccessfullPruning || !result) {
                    boolean success = false;
                    int chance = 0;
                    chance = (int) ((performer.getSkills().getSkillOrLearn(10048).getKnowledge() / Config.guaranteedBranchAt) * 100);
                    if (chance > 100)
                        success = true;
                    if (com.wurmonline.server.Server.rand.nextInt(100) < chance)
                        success = true;
                   if (success) {
                        Item twig = ItemFactory.createItem(1353, (float) java.lang.Math.min(performer.getSkills().getSkillOrLearn(10048).getKnowledge(), (float) Server.rand.nextInt(100)), performer.getName());
                        performer.getInventory().insertItem(twig);
                        performer.getCommunicator().sendSafeServerMessage("you manage to also find a twig while pruning!");
                    }
                }

        } catch (NoSuchTemplateException | FailedException e) {
            throw new RuntimeException(e);
        }
    }


}



