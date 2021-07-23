package com.expl0itz.worldwidechat.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.expl0itz.worldwidechat.WorldwideChat;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.md_5.bungee.api.ChatColor;

public class TempItemInventory implements InventoryProvider {

	private ItemStack displayedItem;
	
	public TempItemInventory(ItemStack displayedItem) {
		this.displayedItem = displayedItem;
	}
	
	public static SmartInventory getTempItemInventory(ItemStack displayedItem) {
		return SmartInventory.builder()
				.id("tempItemMenu")
				.provider(new TempItemInventory(displayedItem))
				.size(6, 9)
				.manager(WorldwideChat.getInstance().getInventoryManager())
				.title(ChatColor.DARK_GREEN + WorldwideChat.getInstance().getConfigManager().getMessagesConfig().getString("Messages.wwcGUITempItem"))
				.build();
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		/* Set borders to purple */
		ItemStack customDefaultBorders = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		ItemMeta defaultBorderMeta = customDefaultBorders.getItemMeta();
		defaultBorderMeta.setDisplayName(" ");
		customDefaultBorders.setItemMeta(defaultBorderMeta);
		contents.fillBorders(ClickableItem.empty(customDefaultBorders));
		
		/* Display item in center */
		contents.set(3, 4, ClickableItem.empty(displayedItem));
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}