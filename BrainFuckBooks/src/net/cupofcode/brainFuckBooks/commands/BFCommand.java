package net.cupofcode.brainFuckBooks.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import net.cupofcode.brainFuckBooks.BrainFuckBooks;
import net.cupofcode.brainFuckBooks.BrainFuckUtils;

public class BFCommand implements CommandExecutor {
	
	private BrainFuckBooks instance = BrainFuckBooks.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player p = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("bf")) {
			if (args.length > 0) {
				if (p.getInventory().getItemInMainHand() != null) {
					ItemStack book = p.getInventory().getItemInMainHand();
					
					if (book.hasItemMeta()) {
						if (book.getItemMeta().getPersistentDataContainer().has(instance.bookKey, PersistentDataType.STRING)) {
							BookMeta bookMeta = (BookMeta) book.getItemMeta();
							
							String code = "";
							for (String page : bookMeta.getPages()) {
								code += page;
							}
							code = code.replaceAll("[^,\\.\\[\\]<>\\+\\-]", "");
							
							String input = "";
							
							for (int i = 1; i < args.length; i++) {
								input += args[i] + " ";
							}
							if (input.length() > 0)
								input = input.substring(0, input.length() - 1);
							
							BrainFuckUtils.runBrainFuck(p, code, input);
							return true;
						}
					}
				}
			}
			p.sendMessage(
					ChatColor.DARK_BLUE + "Use: '/bf run [input]' while holding a BrainFuck Book to run a program.");
			return true;
		}

		return false;
	}

}