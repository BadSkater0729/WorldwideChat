package com.expl0itz.worldwidechat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.misc.ActiveTranslator;
import com.expl0itz.worldwidechat.misc.CommonDefinitions;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ChatListener implements Listener {

    private WorldwideChat main = WorldwideChat.getInstance();
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!main.isReloading() && main.getActiveTranslator(event.getPlayer().getUniqueId().toString()) instanceof ActiveTranslator || main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") instanceof ActiveTranslator) {
        	String outMsg = CommonDefinitions.translateText(event.getMessage(), event.getPlayer());
        	if (!event.getMessage().equals(outMsg)) {
        		event.setMessage(outMsg); 
        	} else {
        		final TextComponent chatTranslationFail = Component.text()
		                .append(main.getPluginPrefix().asComponent())
		                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcChatTranslationFail")).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, true))
		                .build();
		            Audience adventureSender = main.adventure().sender(event.getPlayer());
		            adventureSender.sendMessage(chatTranslationFail);
        	}
        }
    }
}