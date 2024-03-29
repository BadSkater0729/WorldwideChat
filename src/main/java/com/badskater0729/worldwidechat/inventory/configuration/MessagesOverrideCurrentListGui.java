package com.badskater0729.worldwidechat.inventory.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.badskater0729.worldwidechat.WorldwideChat;
import com.badskater0729.worldwidechat.inventory.WWCInventoryManager;
import com.badskater0729.worldwidechat.inventory.configuration.MenuGui.CONFIG_GUI_TAGS;
import com.cryptomorin.xseries.XMaterial;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;

import static com.badskater0729.worldwidechat.util.CommonRefs.getMsg;
import static com.badskater0729.worldwidechat.util.CommonRefs.debugMsg;

public class MessagesOverrideCurrentListGui implements InventoryProvider {

	private WorldwideChat main = WorldwideChat.instance;
	
	public static final SmartInventory overrideMessagesSettings = SmartInventory.builder().id("overrideMessagesMenu")
			.provider(new MessagesOverrideCurrentListGui()).size(6, 9)
			.manager(WorldwideChat.instance.getInventoryManager())
			.title(ChatColor.BLUE + getMsg("wwcConfigGUIChatMessagesOverrideSettings"))
	        .build();
	
	@Override
	public void init(Player player, InventoryContents contents) {
		try {
			/* Green stained glass borders */
			WWCInventoryManager.setBorders(contents, XMaterial.GREEN_STAINED_GLASS_PANE);
			
			/* Pagination */
			Pagination pagination = contents.pagination();
			HashMap<String, String> overridesFromConfig = new HashMap<String, String>();
			ClickableItem[] currentOverrides = new ClickableItem[0];
			FileConfiguration messagesConfig = main.getConfigManager().getMsgsConfig();
			
			if (messagesConfig.getConfigurationSection("Overrides") != null) {
				for (String eaKey : messagesConfig.getConfigurationSection("Overrides").getKeys(true)) {
					overridesFromConfig.put(eaKey, messagesConfig.getString("Overrides." + eaKey));
				}
				currentOverrides = new ClickableItem[overridesFromConfig.size()];
			}
			
			if (!overridesFromConfig.isEmpty()) {
				debugMsg("Adding existing overrides to inventory! Amount of overrides: " + currentOverrides.length);
				int currSpot = 0;
				for (Map.Entry<String, String> entry : overridesFromConfig.entrySet()) {
					ItemStack currentEntry = XMaterial.WRITABLE_BOOK.parseItem();
					ItemMeta currentEntryMeta = currentEntry.getItemMeta();
					
					currentEntryMeta.setDisplayName(entry.getKey());
					ArrayList<String> lore = new ArrayList<>();
					lore.add(getMsg("wwcConfigGUIMessagesOverrideOriginalLabel") + ": " + (messagesConfig.getString("Messages." + entry.getKey()) != null ? messagesConfig.getString("Messages." + entry.getKey()) : getMsg("wwcConfigGUIChatMessagesDeadOverride")));
					lore.add(getMsg("wwcConfigGUIMessagesOverrideCustomLabel") + ": " + entry.getValue());
					currentEntryMeta.setLore(lore);
					currentEntry.setItemMeta(currentEntryMeta);
					currentOverrides[currSpot] = ClickableItem.of(currentEntry, e -> {
						// Open Specific Override GUI
						MessagesOverrideModifyGui.getModifyCurrentOverride(entry.getKey()).open(player);
					});
					currSpot++;
				}
			}
			
			/* 28 messages per page, start at 1, 1 */
			pagination.setItems(currentOverrides);
			pagination.setItemsPerPage(28);
			pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1).allowOverride(false));
			
			/* Bottom Left Option: Previous Page */
			if (!pagination.isFirst()) {
				WWCInventoryManager.setCommonButton(5, 2, player, contents, "Previous");
			} else {
				WWCInventoryManager.setCommonButton(5, 2, player, contents, "Previous", new Object[] {CONFIG_GUI_TAGS.CHAT_SET.smartInv});
			}
			
			/* Bottom Middle Option: Add new override */
			WWCInventoryManager.genericOpenSubmenuButton(5, 4, player, contents, "wwcConfigGUIChatMessagesOverrideNewButton", MessagesOverridePossibleListGui.overrideNewMessageSettings);
			
			/* Bottom Right Option: Next Page */
			if (!pagination.isLast()) {
				WWCInventoryManager.setCommonButton(5, 6, player, contents, "Next");
			}
			
			/* Last Option: Page Number */
			WWCInventoryManager.setCommonButton(5, 8, player, contents, "Page Number", new String[] {pagination.getPage() + 1 + ""});
		} catch (Exception e) {
			WWCInventoryManager.inventoryError(player, e);
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}
