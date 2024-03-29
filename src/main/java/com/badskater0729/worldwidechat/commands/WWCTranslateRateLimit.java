package com.badskater0729.worldwidechat.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.badskater0729.worldwidechat.WorldwideChat;
import com.badskater0729.worldwidechat.util.ActiveTranslator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import static com.badskater0729.worldwidechat.util.CommonRefs.getMsg;
import static com.badskater0729.worldwidechat.util.CommonRefs.sendMsg;
import static com.badskater0729.worldwidechat.util.CommonRefs.sendNoConsoleChatMsg;

public class WWCTranslateRateLimit extends BasicCommand {

	private WorldwideChat main = WorldwideChat.instance;

	private boolean isConsoleSender = sender instanceof ConsoleCommandSender;
	
	public WWCTranslateRateLimit(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}

	@Override
	public boolean processCommand() {
		/* Sanitize args */
		if (args.length > 2) {
			// Not enough/too many args
			final TextComponent invalidArgs = Component.text()
					.append(Component.text()
							.content(getMsg("wwctInvalidArgs"))
							.color(NamedTextColor.RED))
					.build();
			sendMsg(sender, invalidArgs);
			return false;
		}

		/* Disable existing personal rate limit */
		if (args.length == 0 && isConsoleSender) {
			return sendNoConsoleChatMsg(sender);
		} else if ((args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(sender.getName())))) {
			return changeRateLimit(sender.getName(), 0);
		}

		/* Set personal rate limit */
		if (args.length == 1 && StringUtils.isNumeric(args[0])) {
			if (isConsoleSender) {
				return sendNoConsoleChatMsg(sender);
			}
			if (StringUtils.isNumeric(args[0])) {
				return changeRateLimit(sender.getName(), Integer.parseInt(args[0]));
			}
			/* If rate limit is not valid/user wants to disable it */
			rateLimitBadIntMessage(sender);
			return false;
		}

		/* Disable another user's existing rate limit */
		if (args.length == 1 && args[0] != null) {
			return changeRateLimit(args[0], 0);
		}

		/* Set rate limit for another user */
		if (args.length == 2 && args[0] != null) {
			if (StringUtils.isNumeric(args[1])) {
				return changeRateLimit(args[0], Integer.parseInt(args[1]));
			}
            rateLimitBadIntMessage(sender);
			return false;
		}
		return false;
	}
	
	private boolean changeRateLimit(String inName, int newLimit) {
		/* Player check, translator check */
		Player inPlayer = Bukkit.getPlayerExact(inName);
		/* If player is null */
		if (inPlayer == null) {
			playerNotFoundMessage(sender, inName);
			return false;
		} else if (!main.isActiveTranslator(inPlayer)) {
			/* If translator is null, determine sender and send correct message */
			if (!isConsoleSender && inPlayer.getName().equalsIgnoreCase(inName)) {
				final TextComponent notAPlayer = Component.text()
						.append(Component.text()
								.content(getMsg("wwctrlNotATranslator"))
								.color(NamedTextColor.RED))
						.build();
				sendMsg(sender, notAPlayer);
				return false;
			}
			playerNotFoundMessage(sender, inName);
			return false;
		}
		ActiveTranslator currTranslator = main.getActiveTranslator((inPlayer).getUniqueId().toString());
		if (!isConsoleSender && inPlayer.getName().equalsIgnoreCase(sender.getName())) {
			/* If we are changing our own rate limit */
			if (newLimit > 0) {
				/* Enable rate limit */
				currTranslator.setRateLimit(newLimit);
				enableRateLimitMessage(inPlayer, newLimit);
			} else {
				/* Disable rate limit */
				if (!(currTranslator.getRateLimit() > 0)) {
					/* Rate limit already disabled */
					final TextComponent rateLimitAlreadyOff = Component.text()
							.append(Component.text()
									.content(getMsg("wwctrlRateLimitAlreadyOffSender"))
									.color(NamedTextColor.YELLOW))
							.build();
					sendMsg(inPlayer, rateLimitAlreadyOff);
				} else {
					/* Disable rate limit */
					currTranslator.setRateLimit(0);
					disableRateLimitMessage(inPlayer);
				}
			}
			return true;
		} else {
			/* If we are changing somebody else's rate limit */
			if (newLimit > 0) {
				/* Enable rate limit */
				currTranslator.setRateLimit(newLimit);
				final TextComponent rateLimitSet = Component.text().append(Component.text()
								.content(getMsg("wwctrlRateLimitSetTarget", new String[] {inPlayer.getName(), newLimit + ""}))
								.color(NamedTextColor.LIGHT_PURPLE))
						.build();
				sendMsg(sender, rateLimitSet);
				enableRateLimitMessage(inPlayer, newLimit);
			} else {
				/* Disable rate limit */
				if (!(currTranslator.getRateLimit() > 0)) {
					/* Rate limit already disabled */
					final TextComponent rateLimitAlreadyOff = Component.text()
							.append(Component.text().content(getMsg("wwctrlRateLimitAlreadyOffTarget", new String[] {inPlayer.getName()}))
									.color(NamedTextColor.YELLOW))
							.build();
					sendMsg(sender, rateLimitAlreadyOff);
				} else {
					/* Disable rate limit */
					currTranslator.setRateLimit(0);
					final TextComponent rateLimitOffReceiver = Component.text()
							.append(Component.text()
									.content(getMsg("wwctrlRateLimitOffTarget", new String[] {inPlayer.getName()}))
									.color(NamedTextColor.LIGHT_PURPLE))
							.build();
					sendMsg(sender, rateLimitOffReceiver);
					disableRateLimitMessage(inPlayer);
				}
			}
			return true;
		}
	}
	
	private void disableRateLimitMessage(Player inPlayer) {
		final TextComponent rateLimitOffReceiver = Component.text()
				.append(Component.text()
						.content(getMsg("wwctrlRateLimitOffSender"))
						.color(NamedTextColor.LIGHT_PURPLE))
				.build();
		sendMsg(inPlayer, rateLimitOffReceiver);
	}
	
	private void enableRateLimitMessage(Player inPlayer, int limit) {
		final TextComponent rateLimitSet = Component.text()
				.append(Component.text()
						.content(getMsg("wwctrlRateLimitSetSender", new String[] {limit + ""}))
						.color(NamedTextColor.LIGHT_PURPLE))
				.build();
		sendMsg(inPlayer, rateLimitSet);
	}
	
	private void rateLimitBadIntMessage(CommandSender sender) {
		final TextComponent rateLimitOffReceiver = Component.text()
				.append(Component.text()
						.content(getMsg("wwctrlRateLimitBadInt"))
						.color(NamedTextColor.RED))
				.build();
		sendMsg(sender, rateLimitOffReceiver);
	}
	
	private void playerNotFoundMessage(CommandSender sender, String inName) {
		final TextComponent notAPlayer = Component.text()
				.append(Component.text()
						.content(getMsg("wwctrlPlayerNotFound", new String[] {inName}))
						.color(NamedTextColor.RED))
				.build();
		sendMsg(sender, notAPlayer);
	}
	
}

