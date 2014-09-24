package scripts.Mgrs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Game;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSNPC;

import scripts.LANOgreRanger;

/**
 * 
 * Helper class for Tribot's ABC system.
 * 
 * @author Laniax
 *
 */
public class AntibanMgr {

	private static ABCUtil abcUtil;

	// singleton
	public static ABCUtil getUtil() {
		return abcUtil = abcUtil == null ? new ABCUtil() : abcUtil;
	}

	/**
	 * Do all the antiban actions we are supposed to do while idling.
	 */
	public static void doIdleActions() {
		doCheckXP();
		doRotateCamera();
		doExamineObject();
		doMouseLeaveGameApplet();
		doMousePickUp();
		doMouseRandomMovement();
		doMouseRandomRightClick();
	}
	
	/**
	 * Checks if our run energy is above a random threshold and toggles run on if it isn't already.
	 * 
	 * @return if run was activated or not.
	 */
	public static boolean doActivateRun() {

		if (!Game.isRunOn() && Game.getRunEnergy() >= getUtil().INT_TRACKER.NEXT_RUN_AT.next()) {
			
			getUtil().INT_TRACKER.NEXT_RUN_AT.reset();
			LANOgreRanger.statusText = "Antiban - Activate Run";
			
			return Options.setRunOn(true);
		}
		return false;
	}

	/**
	 * Reorganizes an array of NPCs so that they are in the proper order to attack them.
	 * 
	 * Does canReach and isInCombat checks as well.
	 * 
	 * @param npcs
	 * @return the npc to attack, or null if input array was null.
	 */
	public static RSNPC[] orderOfAttack(RSNPC[] npcs) {

		if (npcs.length > 0) {

			npcs = NPCs.sortByDistance(Player.getPosition(), npcs);

			List<RSNPC> orderedNPCs = new ArrayList<RSNPC>();

			for (RSNPC npc : npcs) {

				if (npc.isInCombat() || !npc.isValid())
					continue;
				
				if (LANOgreRanger.POS_SAFESPOT.distanceTo(npc) > 7)
					continue;

				orderedNPCs.add(npc);
			}

			if (orderedNPCs.size() > 1) {

				if (getUtil().BOOL_TRACKER.USE_CLOSEST.next()) {

					// if the 2nd closest npc is within 3 tiles of the closest npc, attack the 2nd one first.
					if (orderedNPCs.get(0).getPosition().distanceTo(orderedNPCs.get(1)) <= 3) 
						Collections.swap(orderedNPCs, 0, 1);
				}

				getUtil().BOOL_TRACKER.USE_CLOSEST.reset();
			}

			return orderedNPCs.toArray(new RSNPC[orderedNPCs.size()]);
		}

		return null;
	}

	/**
	 * Checks with the antiban if we are alowed to hover over the next object.
	 * @return
	 */
	public static boolean mayHoverNextObject() {
		getUtil().BOOL_TRACKER.HOVER_NEXT.reset();
		return getUtil().BOOL_TRACKER.HOVER_NEXT.next();
	}

	/**
	 * Checks the xp in the GameTab.Skills of one of the skills we are currently training.
	 * @return true if succesfully checked a skill
	 */
	public static boolean doCheckXP() {

		SKILLS skillToCheck = General.random(0, 3) > 1  ? SKILLS.RANGED : SKILLS.HITPOINTS;
		
		return getUtil().performXPCheck(skillToCheck);
	}
	
	/**
	 * Rotates the camera for a random duration
	 * @return true if succesfully rotated, false if not.
	 */
	public static boolean doRotateCamera() {
		
		return getUtil().performRotateCamera();
	}
	
	/**
	 * Does a delay for a random amount of time.
	 * Should be used when idling and a new object has spawned.
	 */
	public static void doDelayForNewObject() {
		
		LANOgreRanger.statusText = "Antiban - Delay";
		
		General.sleep(getUtil().DELAY_TRACKER.NEW_OBJECT_COMBAT.next());
		getUtil().DELAY_TRACKER.NEW_OBJECT_COMBAT.reset();
	}
	
	/**
	 * Does a delay for a random amount of time.
	 * Should be used when an object has been drained and a new object is already available.
	 */
	public static void doDelayForSwitchObject() {
		
		LANOgreRanger.statusText = "Antiban - Delay";

		General.sleep(getUtil().DELAY_TRACKER.SWITCH_OBJECT_COMBAT.next());
		getUtil().DELAY_TRACKER.SWITCH_OBJECT_COMBAT.reset();
	}
	
	/**
	 * Examines a random object
	 * @return true if an object was examined.
	 */
	public static boolean doExamineObject() {
		
		return getUtil().performExamineObject();
	}
	
	/**
	 * Throws the cursor of the game window to simulate doing other work on the pc.
	 * @return true if cursor was moved.
	 */
	public static boolean doMouseLeaveGameApplet() {
		
		return getUtil().performLeaveGame();
	}
	
	/**
	 * Move cursor as if a human picked up a mouse.
	 * @return true if cursor was moved.
	 */
	public static boolean doMousePickUp() {
		
		return getUtil().performPickupMouse();
	}
	
	/**
	 * Move cursor randomly.
	 * @return true if cursor was moved.
	 */
	public static boolean doMouseRandomMovement() {
		
		return getUtil().performPickupMouse();
	}
	
	/**
	 * Randomly right clicks somewhere in the game window
	 * @return true if click succeeded.
	 */
	public static boolean doMouseRandomRightClick() {
		
		return getUtil().performRandomRightClick();
	}
	
	// todo: worldhopping stuff
};