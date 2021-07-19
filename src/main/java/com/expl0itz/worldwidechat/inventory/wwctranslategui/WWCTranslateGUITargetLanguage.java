package com.expl0itz.worldwidechat.inventory.wwctranslategui;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.commands.WWCGlobal;
import com.expl0itz.worldwidechat.commands.WWCTranslate;
import com.expl0itz.worldwidechat.inventory.EnchantGlowEffect;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class WWCTranslateGUITargetLanguage implements InventoryProvider {

	private WorldwideChat main = WorldwideChat.getInstance();
	
	private String selectedSourceLanguage = "";
	private String targetPlayerUUID = null;
	
	public WWCTranslateGUITargetLanguage(String source, String targetPlayerUUID) {
		selectedSourceLanguage = source;
		this.targetPlayerUUID = targetPlayerUUID;
	}
	
	public static SmartInventory getTargetLanguageInventory(String source, String targetPlayerUUID) {
		return SmartInventory.builder()
				.id("translateTargetLanguage")
				.provider(new WWCTranslateGUITargetLanguage(source, targetPlayerUUID))
				.size(6, 9)
				.manager(WorldwideChat.getInstance().getInventoryManager())
				.title(ChatColor.BLUE + WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.wwctGUINewTranslationTarget"))
				.build();
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		try {
			/* Pagination: Lets you generate pages rather than set defined ones */
			Pagination pagination = contents.pagination();
			ClickableItem[] listOfAvailableLangs = new ClickableItem[main.getSupportedTranslatorLanguages().size()];
			
			/* Add each supported language from each respective translator */
			if (!main.getTranslatorName().equals("Invalid")) {
				for (int i = 0; i < main.getSupportedTranslatorLanguages().size(); i++) {
					ItemStack currentLang = new ItemStack(Material.TARGET);
					ItemMeta currentLangMeta = currentLang.getItemMeta();
					/* Add Glow Effect */
					if (targetPlayerUUID != null && main.getActiveTranslator(targetPlayerUUID) != null) { //If target player is active translator
						if ((main.getActiveTranslator(targetPlayerUUID).getOutLangCode().equals(main.getSupportedTranslatorLanguages().get(i).getLangCode()))) {
							EnchantGlowEffect glow = new EnchantGlowEffect(new NamespacedKey(main, "wwc_glow"));
							currentLangMeta.addEnchant(glow, 1, true);
						}
					} else if (targetPlayerUUID == null && main.getActiveTranslator(player.getUniqueId().toString()) != null) { //If this player is an active translator
						if ((main.getActiveTranslator(player.getUniqueId().toString()).getOutLangCode().equals(main.getSupportedTranslatorLanguages().get(i).getLangCode()))) {
							EnchantGlowEffect glow = new EnchantGlowEffect(new NamespacedKey(main, "wwc_glow"));
							currentLangMeta.addEnchant(glow, 1, true);
						}
					}
					currentLangMeta.setDisplayName(main.getSupportedTranslatorLanguages().get(i).getLangName());
					ArrayList<String> lore = new ArrayList<>();
					if (!main.getSupportedTranslatorLanguages().get(i).getNativeLangName().equals("")) {
						lore.add(main.getSupportedTranslatorLanguages().get(i).getNativeLangName());
					}
					lore.add(main.getSupportedTranslatorLanguages().get(i).getLangCode());
					currentLangMeta.setLore(lore);
					currentLang.setItemMeta(currentLangMeta);
					String outLang = main.getSupportedTranslatorLanguages().get(i).getLangCode();
					listOfAvailableLangs[i] = ClickableItem.of(currentLang, 
							e -> {
								/* Send to /wwct */
								if (targetPlayerUUID != null && targetPlayerUUID.equals("GLOBAL-TRANSLATE-ENABLED")) {
									String[] args;
									if (selectedSourceLanguage.equals("None")) {
										args = new String[]{outLang};
									} else {
										args = new String[]{selectedSourceLanguage, outLang};
									}
									WWCGlobal globalTranslation = new WWCGlobal((CommandSender)player, null, null, args);
									player.closeInventory();
									globalTranslation.processCommand();
									return;
								}
								
								String[] args;
								if (targetPlayerUUID == null && selectedSourceLanguage.equals("None")) { //If no target, no input lang
									args = new String[]{outLang};
								} else if (targetPlayerUUID == null) { //No target, yes input lang
									args = new String[]{selectedSourceLanguage, outLang};
								} else if (targetPlayerUUID != null && selectedSourceLanguage.equals("None")) { //Yes target, no input lang
									args = new String[] {main.getServer().getPlayer(UUID.fromString(targetPlayerUUID)).getName(), outLang};
								} else { //Yes target, yes input lang
									args = new String[]{main.getServer().getPlayer(UUID.fromString(targetPlayerUUID)).getName(), selectedSourceLanguage, outLang};
								}
								
								WWCTranslate newTranslation = new WWCTranslate((CommandSender)player, null, null, args);
								player.closeInventory();
								newTranslation.processCommand(false);
							});
				}
			} else {
				listOfAvailableLangs = new ClickableItem[1];
				listOfAvailableLangs[0] = ClickableItem.empty(new ItemStack(Material.STONE));
			}
			
			/* 45 langs per page, start at 0, 0 */
			pagination.setItems(listOfAvailableLangs);
			pagination.setItemsPerPage(45);
			pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
			
			/* Deep Bottom Left Option: Back to source translation menu */
			ItemStack stopButton = new ItemStack(Material.BARRIER);
			ItemMeta stopMeta = stopButton.getItemMeta();
			stopMeta.setDisplayName(ChatColor.RED + main.getConfigManager().getMessagesConfig().getString("Messages.wwctGUIBackToSourceLanguageButton"));
			stopButton.setItemMeta(stopMeta);
			contents.set(5, 0, ClickableItem.of(stopButton,
				e -> {
					WWCTranslateGUISourceLanguage.getSourceLanguageInventory(selectedSourceLanguage, targetPlayerUUID).open(player);
				}));
			
			/* Bottom Left Option: Previous Page */
			ItemStack previousPageButton = new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
			ItemMeta previousPageMeta = previousPageButton.getItemMeta();
			previousPageMeta.setDisplayName(ChatColor.GREEN + main.getConfigManager().getMessagesConfig().getString("Messages.wwcConfigGUIPreviousPageButton"));
		    previousPageButton.setItemMeta(previousPageMeta);
		    if (!pagination.isFirst()) {
		    	contents.set(5, 2, ClickableItem.of(previousPageButton, 
						e -> getTargetLanguageInventory(selectedSourceLanguage, targetPlayerUUID).open(player, pagination.previous().getPage())));
		    }
		    
		    /* Bottom Right Option: Next Page */
			ItemStack nextPageButton = new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
			ItemMeta nextPageMeta = nextPageButton.getItemMeta();
			nextPageMeta.setDisplayName(ChatColor.GREEN + main.getConfigManager().getMessagesConfig().getString("Messages.wwcConfigGUINextPageButton"));
		    nextPageButton.setItemMeta(nextPageMeta);
		    if (!pagination.isLast()) {
		    	contents.set(5, 6, ClickableItem.of(nextPageButton,
		                e -> getTargetLanguageInventory(selectedSourceLanguage, targetPlayerUUID).open(player, pagination.next().getPage())));
		    }
		} catch (Exception e) {
	    	final TextComponent inventoryError = Component.text()
                    .append(main.getPluginPrefix().asComponent())
                    .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcInventoryErrorPlayer")).color(NamedTextColor.RED))
                    .build();
                main.adventure().sender(player).sendMessage(inventoryError);
	    	main.getLogger().severe(main.getConfigManager().getMessagesConfig().getString("Messages.wwcInventoryError").replace("%i", player.getName()));
	    	e.printStackTrace();
	    }
	}

	@Override
	public void update(Player player, InventoryContents contents) {
		if (targetPlayerUUID != null && !targetPlayerUUID.equals("GLOBAL-TRANSLATE-ENABLED")) {
	    	if (Bukkit.getPlayer(UUID.fromString(targetPlayerUUID)) == null) {
	    		//Target player no longer online
				player.closeInventory();
				final TextComponent targetPlayerDC = Component.text()
	                    .append(main.getPluginPrefix().asComponent())
	                    .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctGUITargetPlayerNull")).color(NamedTextColor.RED).decorate(TextDecoration.ITALIC))
	                    .build();
	                main.adventure().sender(player).sendMessage(targetPlayerDC);
	    	}
	    }
	}

}
