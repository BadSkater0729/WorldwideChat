package com.badskater0729.worldwidechat.conversations.configuration;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.badskater0729.worldwidechat.WorldwideChat;
import com.badskater0729.worldwidechat.inventory.configuration.MenuGui.CONFIG_GUI_TAGS;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;

import com.badskater0729.worldwidechat.util.CommonRefs;

public class SQLSettingsConvos {

	private static WorldwideChat main = WorldwideChat.instance;
	
	public static class Database extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			/* Close any open inventories */
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			Player currPlayer = ((Player) context.getForWhom());
			currPlayer.closeInventory();
			return ChatColor.AQUA + refs.getMsg("wwcConfigConversationSQLDatabaseNameInput", main.getConfigManager().getMainConfig().getString("Storage.sqlDatabaseName"), currPlayer);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			return refs.genericConfigConvo(!input.equals("0"), context, "wwcConfigConversationSQLDatabaseNameSuccess",
					new String[] {"Storage.sqlDatabaseName"}, new Object[] {input}, CONFIG_GUI_TAGS.SQL_SET.smartInv);
		}
	}
	
	public static class Hostname extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			/* Close any open inventories */
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			Player currPlayer = ((Player) context.getForWhom());
			currPlayer.closeInventory();
			return ChatColor.AQUA + refs.getMsg("wwcConfigConversationSQLHostnameInput", main.getConfigManager().getMainConfig().getString("Storage.sqlHostname"), currPlayer);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			return refs.genericConfigConvo(!input.equals("0"), context, "wwcConfigConversationSQLHostnameSuccess",
					new String[] {"Storage.sqlHostname"}, new Object[] {input}, CONFIG_GUI_TAGS.SQL_SET.smartInv);
		}
	}
	
	public static class OptionalArgs extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			/* Close any open inventories */
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			Player currPlayer = ((Player) context.getForWhom());
			currPlayer.closeInventory();
			return ChatColor.AQUA + refs.getMsg("wwcConfigConversationSQLOptionalArgsInput", (main.getConfigManager().getMainConfig().getList("Storage.sqlOptionalArgs") != null ? main.getConfigManager().getMainConfig().getList("Storage.sqlOptionalArgs").toString() : "empty"), currPlayer);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			if (input.equalsIgnoreCase("clear")) {
				Player currPlayer = ((Player) context.getForWhom());
				main.getConfigManager().getMainConfig().set("Storage.sqlOptionalArgs", new String[0]);
				final TextComponent badChange = Component.text()
								.content(refs.getMsg("wwcConfigConversationSQLOptionalArgsCleared", currPlayer))
								.color(NamedTextColor.YELLOW)
						.build();
				refs.sendMsg(currPlayer, badChange);
				return this;
			} else {
				return refs.genericConfigConvo(!input.equals("0"), context, "wwcConfigConversationSQLOptionalArgsSuccess",
						new String[] {"Storage.sqlOptionalArgs"}, new Object[] {input.split(",")}, CONFIG_GUI_TAGS.SQL_SET.smartInv);
			}
		}
	}
	
	public static class Password extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			/* Close any open inventories */
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			Player currPlayer = ((Player) context.getForWhom());
			currPlayer.closeInventory();
			return ChatColor.AQUA + refs.getMsg("wwcConfigConversationSQLPasswordInput", main.getConfigManager().getMainConfig().getString("Storage.sqlPassword"), currPlayer);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			return refs.genericConfigConvo(!input.equals("0"), context, "wwcConfigConversationSQLPasswordSuccess",
					new String[] {"Storage.sqlPassword"}, new Object[] {input}, CONFIG_GUI_TAGS.SQL_SET.smartInv);
		}
	}
	
	public static class Port extends NumericPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			/* Close any open inventories */
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			Player currPlayer = ((Player) context.getForWhom());
			currPlayer.closeInventory();
			return ChatColor.AQUA + refs.getMsg("wwcConfigConversationSQLPortInput", main.getConfigManager().getMainConfig().getString("Storage.sqlPort"), currPlayer);
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			return refs.genericConfigConvo(input.intValue() != 0, context, "wwcConfigConversationSQLPortSuccess",
					new String[] {"Storage.sqlPort"}, new Object[] {input}, CONFIG_GUI_TAGS.SQL_SET.smartInv);
		}
		
	}
	
	public static class Username extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			/* Close any open inventories */
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			Player currPlayer = ((Player) context.getForWhom());
			currPlayer.closeInventory();
			return ChatColor.AQUA + refs.getMsg("wwcConfigConversationSQLUsernameInput", main.getConfigManager().getMainConfig().getString("Storage.sqlUsername"), currPlayer);
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			return refs.genericConfigConvo(!input.equals("0"), context, "wwcConfigConversationSQLUsernameSuccess",
					new String[] {"Storage.sqlUsername"}, new Object[] {input}, CONFIG_GUI_TAGS.SQL_SET.smartInv);
		}
	}
	
}