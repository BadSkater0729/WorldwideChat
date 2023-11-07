package com.badskater0729.worldwidechat.listeners;

import com.badskater0729.worldwidechat.util.CommonRefs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.badskater0729.worldwidechat.WorldwideChat;
import com.badskater0729.worldwidechat.util.ActiveTranslator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class OnPlayerJoinListener implements Listener {

	private WorldwideChat main = WorldwideChat.instance;

	private CommonRefs refs = main.getServerFactory().getCommonRefs();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoinListener(PlayerJoinEvent event) {
		// Check if plugin has updates
		if ((main.getConfigManager().getMainConfig().getBoolean("Chat.sendPluginUpdateChat")) && (main.getOutOfDate())
				&& (event.getPlayer().hasPermission("worldwidechat.chatupdate"))) {
			final TextComponent outOfDate = Component.text()
							.content(refs.getMsg("wwcUpdaterOutOfDateChat"))
							.color(NamedTextColor.YELLOW)
					.append(Component.text().content(" (").color(NamedTextColor.GOLD))
					.append(Component.text().content("https://github.com/BadSkater0729/WorldwideChat/releases")
							.color(NamedTextColor.GOLD)
							.clickEvent(ClickEvent.openUrl("https://github.com/BadSkater0729/WorldwideChat/releases"))
							.decoration(TextDecoration.UNDERLINED, true))
					.append(Component.text().content(")").color(NamedTextColor.GOLD)).build();
			refs.sendMsg(event.getPlayer(), outOfDate);
		}

		/* Global translate is disabled, and user has a translation config */
		if ((main.getConfigManager().getMainConfig().getBoolean("Chat.sendTranslationChat"))
				&& !main.isActiveTranslator("GLOBAL-TRANSLATE-ENABLED")
				&& main.isActiveTranslator(event.getPlayer())) {
			ActiveTranslator currTranslator = main.getActiveTranslator(event.getPlayer().getUniqueId().toString());
			if (!currTranslator.getInLangCode().equalsIgnoreCase("None")) {
				final TextComponent langToLang = Component.text()
								.content(refs.getMsg("wwcOnJoinTranslationNotificationSourceLang", new String[] {currTranslator.getInLangCode(), currTranslator.getOutLangCode()}))
								.color(NamedTextColor.LIGHT_PURPLE)
						.build();
				refs.sendMsg(event.getPlayer(), langToLang);
			} else {
				final TextComponent noSource = Component.text()
								.content(refs.getMsg("wwcOnJoinTranslationNotificationNoSourceLang", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE)
						.build();
				refs.sendMsg(event.getPlayer(), noSource);
			}
		/* Global translate is enabled, and user does not have a translation config */
		} else if ((main.getConfigManager().getMainConfig().getBoolean("Chat.sendTranslationChat"))
				&& main.isActiveTranslator("GLOBAL-TRANSLATE-ENABLED")
				&& !main.isActiveTranslator(event.getPlayer())) {
			ActiveTranslator currTranslator = main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED");
			if (!currTranslator.getInLangCode().equalsIgnoreCase("None")) {
				final TextComponent langToLang = Component.text()
								.content(refs.getMsg("wwcGlobalOnJoinTranslationNotificationSourceLang", new String[] {currTranslator.getInLangCode(), currTranslator.getOutLangCode()}))
								.color(NamedTextColor.LIGHT_PURPLE)
						.build();
				refs.sendMsg(event.getPlayer(), langToLang);
			} else {
				final TextComponent noSource = Component.text()
								.content(refs. getMsg("wwcGlobalOnJoinTranslationNotificationNoSourceLang", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE)
						.build();
				refs. sendMsg(event.getPlayer(), noSource);
			}
		/* Global translate is enabled, but user ALSO has a translation config */
		} else if ((main.getConfigManager().getMainConfig().getBoolean("Chat.sendTranslationChat"))
				&& main.isActiveTranslator("GLOBAL-TRANSLATE-ENABLED")
				&& main.isActiveTranslator(event.getPlayer())) {
			ActiveTranslator currTranslator = main.getActiveTranslator(event.getPlayer().getUniqueId().toString());
			if (!currTranslator.getInLangCode().equalsIgnoreCase("None")) {
				final TextComponent langToLang = Component.text()
								.content(refs.getMsg("wwcOverrideGlobalOnJoinTranslationNotificationSourceLang", new String[] {currTranslator.getInLangCode(), currTranslator.getOutLangCode()}))
								.color(NamedTextColor.LIGHT_PURPLE)
						.build();
				refs.sendMsg(event.getPlayer(), langToLang);
			} else {
				final TextComponent noSource = Component.text()
								.content(refs.getMsg("wwcOverrideGlobalOnJoinTranslationNotificationNoSourceLang", currTranslator.getOutLangCode()))
								.color(NamedTextColor.LIGHT_PURPLE)
						.build();
				refs.sendMsg(event.getPlayer(), noSource);
			}
		}
	}
}
