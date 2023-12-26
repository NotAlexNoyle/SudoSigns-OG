package dev.mylesmor.sudosigns.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.util.Util;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * The class assigned to each created sign.
 * @author MylesMor
 * @author https://mylesmor.dev
 */
public class SudoSign {

	private ArrayList<SignCommand> playerCommands = new ArrayList<>();
	private ArrayList<SignCommand> consoleCommands = new ArrayList<>();
	private ArrayList<String> permissions = new ArrayList<>();
	private ArrayList<SignMessage> messages = new ArrayList<>();
	private ArrayList<String> text = new ArrayList<>();
	private double price = 0;
	private String worldName;
	private double x;
	private double y;
	private double z;
	private String name;

	public SudoSign(String name) {

		this.name = name;

	}

	public void setSign(Sign sign) {

		Location loc = sign.getLocation();

		this.worldName = loc.getWorld().getName();
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();

	}

	public Sign getSign() {

		if (worldName != null) {

			Location loc = new Location(Bukkit.getWorld(worldName), x, y, z); 

			BlockState blockState = loc.getBlock().getState();
			if (blockState instanceof Sign || blockState instanceof WallSign) {

				return (Sign) blockState;

			}

		}

		Bukkit.getLogger().warning("Failed to locate sign " + name + "!");

		return null;

	}

	public void setName(String name) {

		this.name = name;

	}

	public void addPlayerCommand(SignCommand sc) {

		playerCommands.add(sc);

	}

	public List<String> getText() {

		return text;

	}

	public void addLines() {

		Sign sign = getSign();

		for (int i = 0; i < 4; i++) {

			String line = PlainTextComponentSerializer.plainText().serialize(sign.line(i));

			text.add(line.replaceAll("§", "&"));
			sign.line(i, Util.legacySerializerAnyCase(line));

		}

		sign.update();

	}

	public void editLine(int lineNumber, TextComponent message) {

		Sign sign = getSign();
		String line = PlainTextComponentSerializer.plainText().serialize(sign.line(lineNumber));

		text.add(line.replaceAll("§", "&"));

		sign.line(lineNumber, message);
		sign.update();

	}

	public void addConsoleCommand(SignCommand sc) {

		consoleCommands.add(sc);

	}

	public void deleteConsoleCommand(SignCommand sc) {

		consoleCommands.remove(sc);

	}

	public void deletePlayerCommand(SignCommand sc) {

		playerCommands.remove(sc);

	}

	public void addPermission(String s) {

		permissions.add(s);

	}

	public void removePermission(String s) {

		permissions.remove(s);

	}

	public ArrayList<String> getPermissions() {

		return permissions;

	}

	public void addMessage(SignMessage s) {

		messages.add(s);

	}

	public void removeMessage(SignMessage s) {

		messages.remove(s);

	}

	public void copyFrom(SudoSign s) {

		this.permissions = s.getPermissions();
		this.playerCommands = s.getPlayerCommands();
		this.consoleCommands = s.getConsoleCommands();
		this.messages = s.getMessages();
		this.price = s.getPrice();

	}

	public ArrayList<SignMessage> getMessages() {

		return messages;

	}

	public void setMessages(ArrayList<SignMessage> messages) {

		this.messages = messages;

	}


	public void setPlayerCommands(ArrayList<SignCommand> playerCommands) {

		this.playerCommands = playerCommands;

	}

	public void setConsoleCommands(ArrayList<SignCommand> consoleCommands) {

		this.consoleCommands = consoleCommands;

	}

	public void setPermissions(ArrayList<String> permissions) {

		this.permissions = permissions;

	}

	public ArrayList<SignCommand> getPlayerCommands() {

		return playerCommands;

	}

	public ArrayList<SignCommand> getConsoleCommands() {

		return consoleCommands;

	}

	public void setPrice(double price) {

		this.price = price;

	}

	public double getPrice() {

		return price;

	}

	/**
	 * Executes all of the commands attached to the sign, if the player has the required permissions.
	 * @param p The player who is running the sign.
	 */
	public void executeCommands(Player p) {

		boolean hasPermission = true;
		for (String perm : permissions) {

			if (! p.hasPermission(perm)) {

				hasPermission = false;

			}

		}
		if (hasPermission) {

			if (SudoSigns.econ != null) {

				EconomyResponse r = SudoSigns.econ.withdrawPlayer(p, price);
				if (! r.transactionSuccess()) {

					if (price == 1.0) {

						Util.sudoSignsMessage(p, "&cERROR: You do not have a &b" + SudoSigns.econ.currencyNameSingular() + " &cto run this sign! &6The cost is: &b" + price + "&b" + SudoSigns.econ.currencyNameSingular() + "&6.");

					}
					else {

						Util.sudoSignsMessage(p, "&cERROR: You do not have enough &b" + SudoSigns.econ.currencyNamePlural() + " &cto run this sign! &6The cost is: &b" + price + "&6.");

					}

					return;

				}
				if (price == 1.0) {

					Util.sudoSignsMessage(p, "&b" + price + "&b" + SudoSigns.econ.currencyNameSingular() + " &6has been withdrawn from your balance.");

				}
				else if (price != 0.0) {

					Util.sudoSignsMessage(p, "&b" + price + "&b" + SudoSigns.econ.currencyNamePlural() + " &6has been withdrawn from your balance.");

				}

			}
			for (SignMessage sm : messages) {

				new BukkitRunnable() {

					@Override
					public void run() {

						Util.sudoSignsMessage(p, (sm.getMessage().replaceAll("(?i)%PLAYER%", p.getName())));

					}

				}.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sm.getDelay() / 50));

			}
			for (SignCommand sc : playerCommands) {

				new BukkitRunnable() {

					@Override
					public void run() {

						p.performCommand(sc.getCommand());

					}

				}.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sc.getDelay() / 50));

			}
			for (SignCommand sc : consoleCommands) {

				String cmd = sc.getCommand().replaceAll("(?i)%PLAYER%", p.getName());

				new BukkitRunnable() {

					@Override
					public void run() {

						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

					}

				}.runTaskLater(SudoSigns.sudoSignsPlugin, (long) (sc.getDelay() / 50));

			}

		}
		else {

			Util.sudoSignsErrorMessage(p);

		}

	}

	public String getName() {

		return name;

	}


	public int getNextCommandNumber() {

		int number = 0;
		for (SignCommand c: consoleCommands) {

			number = Math.max(c.getNumber(), number);

		}

		for(SignCommand sc: playerCommands) {

			number = Math.max(sc.getNumber(), number);

		}

		return number + 1;

	}

	public int getNextMessageNumber() {

		int number = 0;
		for (SignMessage m : messages) {

			number = Math.max(m.getNumber(), number);

		}

		return number + 1;

	}

	public SignMessage getSignMessageByNumber(int number) {

		for (SignMessage sm: messages) {

			if (sm.getNumber() == number) {

				return sm;

			}

		}

		return null;

	}


	public SignCommand getSignCommandByNumber(int number) {

		for (SignCommand c: consoleCommands) {

			if (c.getNumber() == number) {

				return c;

			}

		}

		for(SignCommand sc: playerCommands) {

			if (sc.getNumber() == number) {

				return sc;

			}

		}

		return null;

	}

}