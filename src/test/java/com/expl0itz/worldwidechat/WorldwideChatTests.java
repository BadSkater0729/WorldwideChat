package com.expl0itz.worldwidechat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.expl0itz.worldwidechat.commands.TestPlayerCommands;
import com.expl0itz.worldwidechat.inventory.TestPlayerGUI;
import com.expl0itz.worldwidechat.util.TestTranslationUtils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorldwideChatTests {
	private static ServerMock server;
	private static WorldwideChat plugin;
	private static PlayerMock playerMock;
	private static PlayerMock secondPlayerMock;

	private static int testCount = 0;

	/* Init all test classes */
	TestPlayerCommands testPlayerCommands = new TestPlayerCommands(server, plugin, playerMock, secondPlayerMock);
	TestPlayerGUI testPlayerGUI = new TestPlayerGUI(server, plugin, playerMock, secondPlayerMock);
	TestTranslationUtils testTranslationUtils = new TestTranslationUtils(server, plugin, playerMock, secondPlayerMock);

	@BeforeAll
	public static void setUp() {
		server = MockBukkit.mock();
		plugin = (WorldwideChat) MockBukkit.load(WorldwideChat.class);
		playerMock = server.addPlayer();
		playerMock.setName("player1");
		secondPlayerMock = server.addPlayer();
		secondPlayerMock.setName("player2");

		/* Add perms */
		playerMock.addAttachment(plugin, "worldwidechat.wwct", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwct.otherplayers", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwcg", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwctb", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwctb.otherplayers", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwcts", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwcts.otherplayers", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwcti", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwcti.otherplayers", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwctrl", true);
		playerMock.addAttachment(plugin, "worldwidechat.wwcs", true);
		secondPlayerMock.addAttachment(plugin, "worldwidechat.wwct", true);
		secondPlayerMock.addAttachment(plugin, "worldwidechat.wwct.otherplayers", true);
	}

	@AfterAll
	public static void tearDown() {
		MockBukkit.unmock();
	}

	public void resetWWC() {
		plugin.getActiveTranslators().clear();
		plugin.getPlayerRecords().clear();
		plugin.getCache().clear();
		plugin.cancelBackgroundTasks();
		plugin.onEnable();
	}

	public void sendCompletedMessage() {
		testCount++;
		plugin.getLogger().info("=== (Test " + testCount + ") Completed Successfully ===");
	}

	/* Command Tests */
	@Order(1)
	@Test
	public void testPlayerCommands() {
		/* Print start message */
		plugin.getLogger().info("=== Test Player Commands ===");

		/* Run tests */
		testPlayerCommands.testTranslateCommandPlayerSourceTarget();
		testPlayerCommands.testTranslateCommandPlayerSourceTargetOther();
		testPlayerCommands.testTranslateCommandPlayerTarget();
		testPlayerCommands.testTranslateCommandPlayerTargetOther();
		testPlayerCommands.testGlobalTranslateCommandPlayerSourceTarget();
		testPlayerCommands.testGlobalTranslateCommandPlayerTarget();
		testPlayerCommands.testBookTranslateCommandPlayer(true);
		testPlayerCommands.testBookTranslateCommandPlayer(false);
		testPlayerCommands.testBookTranslateCommandPlayerOther(true);
		testPlayerCommands.testBookTranslateCommandPlayerOther(false);
		testPlayerCommands.testBookTranslateCommandPlayerOtherButSamePlayer(true);
		testPlayerCommands.testBookTranslateCommandPlayerOtherButSamePlayer(false);
		testPlayerCommands.testSignTranslateCommandPlayer(true);
		testPlayerCommands.testSignTranslateCommandPlayer(false);
		testPlayerCommands.testSignTranslateCommandPlayerOther(true);
		testPlayerCommands.testSignTranslateCommandPlayerOther(false);
		testPlayerCommands.testSignTranslateCommandPlayerOtherButSamePlayer(true);
		testPlayerCommands.testSignTranslateCommandPlayerOtherButSamePlayer(false);
		testPlayerCommands.testItemTranslateCommandPlayer(true);
		testPlayerCommands.testItemTranslateCommandPlayer(false);
		testPlayerCommands.testItemTranslateCommandPlayerOther(true);
		testPlayerCommands.testItemTranslateCommandPlayerOther(false);
		testPlayerCommands.testItemTranslateCommandPlayerOtherButSamePlayer(true);
		testPlayerCommands.testItemTranslateCommandPlayerOtherButSamePlayer(false);
		testPlayerCommands.testRateLimitTranslateCommandPlayer(true);
		testPlayerCommands.testRateLimitTranslateCommandPlayer(false);
		testPlayerCommands.testRateLimitTranslateCommandPlayerOther(true);
		testPlayerCommands.testRateLimitTranslateCommandPlayerOther(false);
		testPlayerCommands.testRateLimitTranslateCommandOtherButSamePlayer(true);
		testPlayerCommands.testRateLimitTranslateCommandOtherButSamePlayer(false);

		/* Print finished message */
		sendCompletedMessage();
	}

	/* GUI Tests */
	@Order(2)
	@Test
	public void testPlayerGUI() {
		/* Reset Translators */
		resetWWC();

		/* Print start message */
		plugin.getLogger().info("=== Test Player GUIs ===");

		/* Run tests */
		testPlayerGUI.testTranslateCommandPlayerGUI();
		testPlayerGUI.testTranslateCommandPlayerGUIActive();
		testPlayerGUI.testTranslateCommandPlayerGUIOther();
		testPlayerGUI.testTranslateCommandPlayerGUIOtherActive();
		testPlayerGUI.testGlobalTranslateCommandPlayerGUI();
		testPlayerGUI.testGlobalTranslateCommandPlayerGUIActive();
		testPlayerGUI.testConfigurationCommandPlayerGUI();

		/* Print finished message */
		sendCompletedMessage();
	}

	/* Util Tests */
	@Order(3)
	@Test
	public void testUtils() {
		/* Reset Translators */
		resetWWC();

		/* Print start message */
		plugin.getLogger().info("=== Test Internal Utilities ===");

		/* Run tests */
		testTranslationUtils.testTranslationFunctionSourceTarget();
		testTranslationUtils.testTranslationFunctionSourceTargetOther();
		testTranslationUtils.testTranslationFunctionTarget();
		testTranslationUtils.testTranslationFunctionTargetOther();
		testTranslationUtils.testPluginDataRetention();

		/* Print finished message */
		sendCompletedMessage();
	}
}
