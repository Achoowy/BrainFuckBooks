package net.cupofcode.brainFuckBooks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import net.cupofcode.brainFuckBooks.commands.BFCommand;
import net.cupofcode.brainFuckBooks.listeners.PlayerInteract;

public class BrainFuckBooks extends JavaPlugin {
	private static BrainFuckBooks instance;
	private File configFile;
	private FileConfiguration config;
	public NamespacedKey bookKey;

	@Override
	public void onEnable() {
		instance = this;
		bookKey = new NamespacedKey(instance, "brain_fuck_book"); // String "TRUE" for BrainFuck Book items
		loadConfig();

		getLogger().info("Loaded BrainFuckBooks");

		registerListeners(new PlayerInteract());

		getCommand("bf").setExecutor(new BFCommand());

		if (config.getBoolean("settings.brainfuckbook.recipe.enabled"))
			addBrainFuckBookRecipe();

		// Add bStats
		Metrics metrics = new Metrics(this, 10153);
		Bukkit.getLogger().info("[ChessBoards] bStats: " + metrics.isEnabled() + " plugin ver: " + getDescription().getVersion());
		metrics.addCustomChart(new Metrics.SimplePie("plugin_version", () -> getDescription().getVersion()));

	}

	private void registerListeners(Listener... listeners) {
		Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
	}

	public static BrainFuckBooks getInstance() {
		return instance;
	}

	public void addBrainFuckBookRecipe() {
		ItemStack brainFuckBook = new ItemStack(Material.WRITABLE_BOOK);
		BookMeta brainFuckBookMeta = (BookMeta) brainFuckBook.getItemMeta();

		brainFuckBookMeta.setDisplayName(ChatColor.DARK_BLUE + "BrainFuck Book");
		brainFuckBookMeta.getPersistentDataContainer().set(bookKey, PersistentDataType.STRING, "TRUE");
		brainFuckBookMeta.addPage("BrainFuck Code:");
		brainFuckBookMeta.setLore(Arrays.asList(ChatColor.BOLD + "punch a block to run code"));

		brainFuckBook.setItemMeta(brainFuckBookMeta);

		NamespacedKey key = new NamespacedKey(this, "brainFuckBook");
		ShapedRecipe recipe = new ShapedRecipe(key, brainFuckBook);

		ArrayList<String> shapeArr = (ArrayList<String>) config.get("settings.brainfuckbook.recipe.shape");
		recipe.shape(shapeArr.toArray(new String[shapeArr.size()]));

		for (String ingredientKey : config.getConfigurationSection("settings.brainfuckbook.recipe.ingredients")
				.getKeys(false)) {
			recipe.setIngredient(ingredientKey.charAt(0), Material
					.valueOf((String) config.get("settings.brainfuckbook.recipe.ingredients." + ingredientKey)));
		}

		Bukkit.addRecipe(recipe);
	}

	private void loadConfig() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = YamlConfiguration.loadConfiguration(configFile);

		HashMap<String, Object> defaultConfig = new HashMap<>();

		HashMap<String, String> defaultRecipe = new HashMap<>();
		defaultRecipe.put("B", Material.BOOK.toString());
		defaultRecipe.put("R", Material.REDSTONE.toString());

		defaultConfig.put("settings.brainfuckbook.recipe.enabled", true);
		defaultConfig.put("settings.brainfuckbook.recipe.shape", new ArrayList<String>() {
			{
				add("R");
				add("B");
			}
		});

		if (!config.contains("settings.brainfuckbook.recipe.ingredients")) {
			for (String key : defaultRecipe.keySet()) {
				defaultConfig.put("settings.brainfuckbook.recipe.ingredients." + key, defaultRecipe.get(key));
			}
		}

		for (String key : defaultConfig.keySet()) {
			if (!config.contains(key)) {
				config.set(key, defaultConfig.get(key));
			}
		}

		this.saveConfig();
	}

	@Override
	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}
}