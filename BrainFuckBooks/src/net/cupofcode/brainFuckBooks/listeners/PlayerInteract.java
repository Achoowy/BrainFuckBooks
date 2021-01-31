package net.cupofcode.brainFuckBooks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import net.cupofcode.brainFuckBooks.BrainFuckBooks;
import net.cupofcode.brainFuckBooks.BrainFuckUtils;

public class PlayerInteract implements Listener {

	private BrainFuckBooks instance = BrainFuckBooks.getInstance();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasItem())
			return;

		ItemStack book = event.getItem();

		if (!book.hasItemMeta())
			return;

		if (book.getType() == Material.WRITTEN_BOOK || book.getType() == Material.WRITABLE_BOOK) {
			BookMeta bookMeta = (BookMeta) book.getItemMeta();

			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				// check that book is BrainFuck Book
				if (!bookMeta.getPersistentDataContainer().has(instance.bookKey, PersistentDataType.STRING)) {
					return;
				}

				// run BF code
				event.setCancelled(true);
				String code = "";

				for (String page : bookMeta.getPages()) {
					code += page;
				}

				code = code.replaceAll("[^,\\.\\[\\]<>\\+\\-]", "");
				if (code.contains(",")) {
					// code takes input
					event.getPlayer().sendMessage(ChatColor.DARK_BLUE
							+ "This program requires input. Use: '/bf run [input]' to run the code.");
				} else {
					BrainFuckUtils.runBrainFuck(event.getPlayer(), code, "");
				}
			}
		}
	}

}
