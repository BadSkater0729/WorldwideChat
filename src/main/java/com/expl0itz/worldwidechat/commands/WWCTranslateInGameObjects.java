package com.expl0itz.worldwidechat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.util.ActiveTranslator;
import com.expl0itz.worldwidechat.util.CommonDefinitions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class WWCTranslateInGameObjects extends BasicCommand {
	
	public WWCTranslateInGameObjects(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}
	
	private boolean isConsoleSender = sender instanceof ConsoleCommandSender;
	
	private WorldwideChat main = WorldwideChat.getInstance();
	
	/* Process command */
	public boolean processCommand() {
		/* Check args */
		if (args.length > 1) {
			final TextComponent invalidArgs = Component.text()
					.append(Component.text()
							.content(CommonDefinitions.getMessage("wwctInvalidArgs"))
							.color(NamedTextColor.RED))
					.build();
			CommonDefinitions.sendMessage(sender, invalidArgs);
		}
		
		/* If no args are provided */
		if (isConsoleSender && args.length == 0) {
			return CommonDefinitions.getNoConsoleChatMessage(sender);
		}
		if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(sender.getName()))) {
			if (!main.getActiveTranslator(((Player)sender).getUniqueId().toString()).getUUID().equals("")) {
				return toggleStatus((Player)sender);
			}
			// Player is not an active translator
			final TextComponent notATranslator = Component.text()
					.append(Component.text().content(
							CommonDefinitions.getMessage("wwctbNotATranslator"))
							.color(NamedTextColor.RED))
					.build();
			CommonDefinitions.sendMessage(sender, notATranslator);
			return true;
		}
		
		/* If there is an argument (another player) */
		if (args.length == 1) {
			if (Bukkit.getServer().getPlayerExact(args[0]) != null && !main.getActiveTranslator(Bukkit.getServer().getPlayerExact(args[0]).getUniqueId().toString()).getUUID().equals("")) {
				if (this instanceof WWCTranslateBook) {
					if (sender.hasPermission("worldwidechat.wwctb.otherplayers")) {
						return toggleStatus(Bukkit.getPlayerExact(args[0]));
					} else {
						badPermsMessage("worldwidechat.wwctb.otherplayers");
					}
				} else if (this instanceof WWCTranslateItem) {
					if (sender.hasPermission("worldwidechat.wwcti.otherplayers")) {
						return toggleStatus(Bukkit.getPlayerExact(args[0]));
					} else {
						badPermsMessage("worldwidechat.wwcti.otherplayers");
					}
				} else if (this instanceof WWCTranslateSign) {
					if (sender.hasPermission("worldwidechat.wwcts.otherplayers")) {
						return toggleStatus(Bukkit.getPlayerExact(args[0]));
					} else {
						badPermsMessage("worldwidechat.wwcts.otherplayers");
					}
				} else if (this instanceof WWCTranslateEntity) {
					if (sender.hasPermission("worldwidechat.wwcte.otherplayers")) {
						return toggleStatus(Bukkit.getPlayerExact(args[0]));
					} else {
						badPermsMessage("worldwidechat.wwcte.otherplayers");
					}
				}
			} else {
				// If target is not a string or active translator:
				final TextComponent notAPlayer = Component.text()
						.append(Component.text()
								.content(CommonDefinitions.getMessage("wwctbPlayerNotFound", new String[] {args[0]}))
								.color(NamedTextColor.RED))
						.build();
				CommonDefinitions.sendMessage(sender, notAPlayer);
			}
		}
		return false;
	}

	/* Toggle Each Class's Status */
	private boolean toggleStatus(Player inPlayer) {
		ActiveTranslator currentTranslator = main
				.getActiveTranslator((inPlayer.getUniqueId().toString()));
		/* If we are book translation... */
		if (this instanceof WWCTranslateBook) {
			/* Toggle book translation for sender! */
			if (!isConsoleSender && inPlayer.getName().equalsIgnoreCase(sender.getName())) {
				currentTranslator.setTranslatingBook(!currentTranslator.getTranslatingBook());
				if (currentTranslator.getTranslatingBook()) {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctbOnSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslation);
					CommonDefinitions.sendDebugMessage("Book translation enabled for " + inPlayer.getName() + ".");
				} else {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctbOffSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslation);
					CommonDefinitions.sendDebugMessage("Book translation disabled for " + inPlayer.getName() + ".");
				}
			/* Toggle book translation for target! */
			} else {
				currentTranslator.setTranslatingBook(!currentTranslator.getTranslatingBook());
				if (currentTranslator.getTranslatingBook()) {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctbOnTarget", new String[] {args[0]}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(sender, toggleTranslation);
					final TextComponent toggleTranslationTarget = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctbOnSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslationTarget);
					CommonDefinitions.sendDebugMessage("Book translation enabled for " + inPlayer.getName() + ".");
				} else {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctbOffTarget", new String[] {args[0]}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(sender, toggleTranslation);
					final TextComponent toggleTranslationTarget = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctbOffSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslationTarget);
					CommonDefinitions.sendDebugMessage("Book translation disabled for " + inPlayer.getName() + ".");
				}
			}
			return true;
		} else if (this instanceof WWCTranslateSign) {
			/* Toggle sign translation for sender! */
			if (!isConsoleSender && inPlayer.getName().equalsIgnoreCase(sender.getName())) {
				currentTranslator.setTranslatingSign(!currentTranslator.getTranslatingSign());
				if (currentTranslator.getTranslatingSign()) {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctsOnSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslation);
					CommonDefinitions.sendDebugMessage("Book translation enabled for " + inPlayer.getName() + ".");
				} else {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctsOffSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslation);
					CommonDefinitions.sendDebugMessage("Sign translation disabled for " + inPlayer.getName() + ".");
				}
			/* Toggle sign translation for target! */
			} else {
				currentTranslator.setTranslatingSign(!currentTranslator.getTranslatingSign());
				if (currentTranslator.getTranslatingSign()) {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctsOnTarget", new String[] {args[0]}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(sender, toggleTranslation);
					final TextComponent toggleTranslationTarget = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctsOnSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslationTarget);
					CommonDefinitions.sendDebugMessage("Book translation enabled for " + inPlayer.getName() + ".");
				} else {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctsOffTarget", new String[] {args[0]}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(sender, toggleTranslation);
					final TextComponent toggleTranslationTarget = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctsOffSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslationTarget);
					CommonDefinitions.sendDebugMessage("Sign translation disabled for " + inPlayer.getName() + ".");
				}
			}
			return true;
		} else if (this instanceof WWCTranslateItem) {
			/* Toggle item translation for sender! */
			if (!isConsoleSender && inPlayer.getName().equalsIgnoreCase(sender.getName())) {
				currentTranslator.setTranslatingItem(!currentTranslator.getTranslatingItem());
				if (currentTranslator.getTranslatingItem()) {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctiOnSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslation);
					CommonDefinitions.sendDebugMessage("Item translation enabled for " + inPlayer.getName() + ".");
				} else {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctiOffSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslation);
					CommonDefinitions.sendDebugMessage("Item translation disabled for " + inPlayer.getName() + ".");
				}
			/* Toggle item translation for target! */
			} else {
				currentTranslator.setTranslatingItem(!currentTranslator.getTranslatingItem());
				if (currentTranslator.getTranslatingItem()) {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctiOnTarget", new String[] {args[0]}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(sender, toggleTranslation);
					final TextComponent toggleTranslationTarget = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctiOnSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslationTarget);
					CommonDefinitions.sendDebugMessage("Item translation enabled for " + inPlayer.getName() + ".");
				} else {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctiOffTarget", new String[] {args[0]}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(sender, toggleTranslation);
					final TextComponent toggleTranslationTarget = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwctiOffSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslationTarget);
					CommonDefinitions.sendDebugMessage("Item translation disabled for " + inPlayer.getName() + ".");
				}
			}
			return true;
		} else if (this instanceof WWCTranslateEntity) {
			/* Toggle entity translation for sender! */
			if (!isConsoleSender && inPlayer.getName().equalsIgnoreCase(sender.getName())) {
				currentTranslator.setTranslatingEntity(!currentTranslator.getTranslatingEntity());
				if (currentTranslator.getTranslatingEntity()) {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwcteOnSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslation);
					CommonDefinitions.sendDebugMessage("Entity translation enabled for " + inPlayer.getName() + ".");
				} else {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwcteOffSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslation);
					CommonDefinitions.sendDebugMessage("Entity translation disabled for " + inPlayer.getName() + ".");
				}
			/* Toggle entity translation for target! */
			} else {
				currentTranslator.setTranslatingEntity(!currentTranslator.getTranslatingEntity());
				if (currentTranslator.getTranslatingEntity()) {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwcteOnTarget", new String[] {args[0]}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(sender, toggleTranslation);
					final TextComponent toggleTranslationTarget = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwcteOnSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslationTarget);
					CommonDefinitions.sendDebugMessage("Entity translation enabled for " + inPlayer.getName() + ".");
				} else {
					final TextComponent toggleTranslation = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwcteOffTarget", new String[] {args[0]}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(sender, toggleTranslation);
					final TextComponent toggleTranslationTarget = Component.text()
							.append(Component.text()
									.content(CommonDefinitions.getMessage("wwcteOffSender"))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					CommonDefinitions.sendMessage(inPlayer, toggleTranslationTarget);
					CommonDefinitions.sendDebugMessage("Entity translation disabled for " + inPlayer.getName() + ".");
				}
			}
			return true;
		}
		return false;
	}
	
	private void badPermsMessage(String correctPerm) {
		final TextComponent badPerms = Component.text() // Bad perms
				.append(Component.text()
						.content(CommonDefinitions.getMessage("wwcBadPerms"))
						.color(NamedTextColor.RED))
				.append(Component.text().content(" (" + correctPerm + ")")
						.color(NamedTextColor.LIGHT_PURPLE))
				.build();
		CommonDefinitions.sendMessage(sender, badPerms);
	}
}
