package com.expl0itz.worldwidechat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.util.ActiveTranslator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class OnPlayerJoinListener implements Listener {

	private WorldwideChat main = WorldwideChat.getInstance();

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoinListener(PlayerJoinEvent event) {
		// Check if plugin has updates
		if ((main.getConfigManager().getMainConfig().getBoolean("Chat.sendPluginUpdateChat")) && (main.getOutOfDate())
				&& (event.getPlayer().hasPermission("worldwidechat.chatupdate"))) {
			final TextComponent outOfDate = Component.text().append(main.getPluginPrefix().asComponent())
					.append(Component.text()
							.content(main.getConfigManager().getMessagesConfig()
									.getString("Messages.wwcUpdaterOutOfDateChat"))
							.color(NamedTextColor.YELLOW))
					.append(Component.text().content(" (").color(NamedTextColor.GOLD))
					.append(Component.text().content("https://github.com/3xpl0itz/WorldwideChat/releases")
							.color(NamedTextColor.GOLD)
							.clickEvent(ClickEvent.openUrl("https://github.com/3xpl0itz/WorldwideChat/releases"))
							.decoration(TextDecoration.UNDERLINED, true))
					.append(Component.text().content(")").color(NamedTextColor.GOLD)).build();
			main.adventure().sender(event.getPlayer()).sendMessage(outOfDate);
		}

		/* Global translate is disabled, and user has a translation config */
		if ((main.getConfigManager().getMainConfig().getBoolean("Chat.sendTranslationChat"))
				&& main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") == null
				&& main.getActiveTranslator(event.getPlayer().getUniqueId().toString()) != null) { // if they have a
																									// config
			ActiveTranslator currTranslator = main.getActiveTranslator(event.getPlayer().getUniqueId().toString());
			if (!currTranslator.getInLangCode().equalsIgnoreCase("None")) {
				final TextComponent langToLang = Component.text().append(main.getPluginPrefix().asComponent())
						.append(Component.text()
								.content(main.getConfigManager().getMessagesConfig()
										.getString("Messages.wwcOnJoinTranslationNotificationSourceLang")
										.replace("%i", currTranslator.getInLangCode())
										.replace("%o", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE))
						.build();
				main.adventure().sender(event.getPlayer()).sendMessage(langToLang);
			} else {
				final TextComponent noSource = Component.text().append(main.getPluginPrefix().asComponent())
						.append(Component.text()
								.content(main.getConfigManager().getMessagesConfig()
										.getString("Messages.wwcOnJoinTranslationNotificationNoSourceLang")
										.replace("%o", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE))
						.build();
				main.adventure().sender(event.getPlayer()).sendMessage(noSource);
			}
			/* Global translate is enabled, and user does not have a translation config */
		} else if ((main.getConfigManager().getMainConfig().getBoolean("Chat.sendTranslationChat"))
				&& main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") != null
				&& main.getActiveTranslator(event.getPlayer().getUniqueId().toString()) == null) { // If global
																									// translate is
																									// enabled...
			ActiveTranslator currTranslator = main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED");
			if (!currTranslator.getInLangCode().equalsIgnoreCase("None")) {
				final TextComponent langToLang = Component.text().append(main.getPluginPrefix().asComponent())
						.append(Component.text()
								.content(main.getConfigManager().getMessagesConfig()
										.getString("Messages.wwcGlobalOnJoinTranslationNotificationSourceLang")
										.replace("%i", currTranslator.getInLangCode())
										.replace("%o", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE))
						.build();
				main.adventure().sender(event.getPlayer()).sendMessage(langToLang);
			} else {
				final TextComponent noSource = Component.text().append(main.getPluginPrefix().asComponent())
						.append(Component.text()
								.content(main.getConfigManager().getMessagesConfig()
										.getString("Messages.wwcGlobalOnJoinTranslationNotificationNoSourceLang")
										.replace("%o", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE))
						.build();
				main.adventure().sender(event.getPlayer()).sendMessage(noSource);
			}
			/* Global translate is enabled, but user has a translation config */
		} else if ((main.getConfigManager().getMainConfig().getString("Chat.sendTranslationChat").equals("true"))
				&& main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") != null
				&& main.getActiveTranslator(event.getPlayer().getUniqueId().toString()) != null) { // If global
																									// translate is
																									// enabled...
			ActiveTranslator currTranslator = main.getActiveTranslator(event.getPlayer().getUniqueId().toString());
			if (!currTranslator.getInLangCode().equalsIgnoreCase("None")) {
				final TextComponent langToLang = Component.text().append(main.getPluginPrefix().asComponent())
						.append(Component.text()
								.content(main.getConfigManager().getMessagesConfig()
										.getString("Messages.wwcOverrideGlobalOnJoinTranslationNotificationSourceLang")
										.replace("%i", currTranslator.getInLangCode())
										.replace("%o", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE))
						.build();
				main.adventure().sender(event.getPlayer()).sendMessage(langToLang);
			} else {
				final TextComponent noSource = Component.text().append(main.getPluginPrefix().asComponent())
						.append(Component.text()
								.content(main.getConfigManager().getMessagesConfig()
										.getString(
												"Messages.wwcOverrideGlobalOnJoinTranslationNotificationNoSourceLang")
										.replace("%o", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE))
						.build();
				main.adventure().sender(event.getPlayer()).sendMessage(noSource);
			}
		}
	}
}
