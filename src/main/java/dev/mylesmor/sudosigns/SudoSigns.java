package dev.mylesmor.sudosigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import dev.mylesmor.sudosigns.commands.Commands;
import dev.mylesmor.sudosigns.commands.SudoSignsTabCompleter;
import dev.mylesmor.sudosigns.config.ConfigManager;
import dev.mylesmor.sudosigns.data.SudoSign;
import dev.mylesmor.sudosigns.data.SudoUser;
import dev.mylesmor.sudosigns.listeners.ChatListener;
import dev.mylesmor.sudosigns.listeners.InventoryListener;
import dev.mylesmor.sudosigns.listeners.PlayerListener;
import dev.mylesmor.sudosigns.listeners.SignListener;
import net.milkbowl.vault.economy.Economy;

/**
 * Main class for SudoSigns.
 * @author MylesMor
 * @author https://mylesmor.dev
 */
public class SudoSigns extends JavaPlugin {

	public static Map<String, SudoSign> signs = new HashMap<>();
	public static ArrayList<String> invalidSigns = new ArrayList<>();

	public static Map<UUID, SudoUser> users = new HashMap<>();

	public static ConfigManager config;

	public static Plugin sudoSignsPlugin;

	public static String version;

	public static Economy econ = null;

	@Override
	public void onEnable() {
		setupEconomy();
		String version = getServer().getVersion().split("MC: ")[1];
		SudoSigns.version = version.substring(0, version.length()-1);
		getServer().getPluginManager().registerEvents(new SignListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		sudoSignsPlugin = this;
		config = new ConfigManager();
		config.loadModules();
		this.getCommand("sudosigns").setExecutor(new Commands());
		this.getCommand("sudosigns").setTabCompleter(new SudoSignsTabCompleter());
	}

	public boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			Bukkit.getLogger().warning("[SUDOSIGNS] Vault not found, sign prices disabled...");
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			Bukkit.getLogger().warning("[SUDOSIGNS] No compatible economy plugin found, sign prices disabled...");
			return false;
		}
		econ = rsp.getProvider();
		return true;
	}

	@Override
	public void onDisable() {

	}
}