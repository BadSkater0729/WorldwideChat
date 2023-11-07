package com.badskater0729.worldwidechat.conversations.configuration;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.badskater0729.worldwidechat.WorldwideChat;
import com.badskater0729.worldwidechat.inventory.configuration.MenuGui.CONFIG_GUI_TAGS;

import com.badskater0729.worldwidechat.util.CommonRefs;

import net.md_5.bungee.api.ChatColor;

public class GoogleSettingsConvos {

	private static WorldwideChat main = WorldwideChat.instance;
	
	public static class ApiKey extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			/* Close any open inventories */
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			((Player) context.getForWhom()).closeInventory();
			return ChatColor.AQUA + refs.getMsg("wwcConfigConversationGoogleTranslateAPIKeyInput", main.getConfigManager().getMainConfig().getString("Translator.googleTranslateAPIKey"));
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			CommonRefs refs = main.getServerFactory().getCommonRefs();
			return refs.genericConfigConvo(!input.equals("0"), context, "wwcConfigConversationGoogleTranslateAPIKeySuccess",
					new String[] {"Translator.googleTranslateAPIKey", "Translator.useGoogleTranslate"}, new Object[] {input, false}, CONFIG_GUI_TAGS.GOOGLE_TRANS_SET.smartInv);
		}
	}
	
}
