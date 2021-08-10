package com.expl0itz.worldwidechat.conversations.configuration;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.inventory.configuration.ConfigurationEachTranslatorSettingsGUI;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;

public class TranslatorSettingsAmazonTranslateRegionConversation extends StringPrompt {

	private WorldwideChat main = WorldwideChat.getInstance();
	
	@Override
	public String getPromptText(ConversationContext context) {
		/* Close any open inventories */
		((Player)context.getForWhom()).closeInventory();
		return ChatColor.AQUA + "" + main.getConfigManager().getMessagesConfig().getString("Messages.wwcConfigConversationAmazonTranslateRegionInput").replace("%i", main.getConfigManager().getMainConfig().getString("Translator.amazonRegion"));
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		if (!input.equals("0")) {
			main.getConfigManager().getMainConfig().set("Translator.amazonRegion", input);
			main.getConfigManager().getMainConfig().set("Translator.useAmazonTranslate", false);
			try {
				main.addPlayerUsingConfigurationGUI((Player)context.getForWhom());
				final TextComponent successfulChange = Component.text()
		                .append(main.getPluginPrefix().asComponent())
		                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcConfigConversationAmazonTranslateRegionSuccess")).color(NamedTextColor.GREEN))
		                .build();
		            Audience adventureSender = main.adventure().sender((CommandSender)context.getForWhom());
		        adventureSender.sendMessage(successfulChange);
				main.getConfigManager().getMainConfig().save(main.getConfigManager().getConfigFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ConfigurationEachTranslatorSettingsGUI.getCurrentTranslatorSettings("Amazon Translate").open((Player)context.getForWhom());
		return END_OF_CONVERSATION;
	}

}
