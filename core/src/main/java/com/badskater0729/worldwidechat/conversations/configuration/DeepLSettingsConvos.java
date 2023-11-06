package com.badskater0729.worldwidechat.conversations.configuration;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.badskater0729.worldwidechat.WorldwideChat;
import com.badskater0729.worldwidechat.inventory.configuration.MenuGui.CONFIG_GUI_TAGS;

import com.badskater0729.worldwidechat.util.CommonRefs;

import net.md_5.bungee.api.ChatColor;

public class DeepLSettingsConvos {

private static WorldwideChat main = WorldwideChat.instance;
	
	public static class ApiKey extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			/* Close any open inventories */
			CommonRefs refs = new CommonRefs();
			((Player) context.getForWhom()).closeInventory();
			return ChatColor.AQUA + refs.getMsg("wwcConfigConversationDeepLTranslateApiKeyInput", main.getConfigManager().getMainConfig().getString("Translator.deepLAPIKey"));
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String input) {
			CommonRefs refs = new CommonRefs();
			return refs.genericConfigConvo(!input.equals("0"), context, "wwcConfigConversationDeepLTranslateApiKeySuccess",
					new String[] {"Translator.deepLAPIKey", "Translator.useDeepLTranslate"}, new Object[] {input, false}, CONFIG_GUI_TAGS.DEEP_TRANS_SET.smartInv);
		}
	}
	
}
