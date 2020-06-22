package dev.mylesmor.sudosigns.listeners;

import dev.mylesmor.sudosigns.SudoSigns;
import dev.mylesmor.sudosigns.data.PlayerInput;
import dev.mylesmor.sudosigns.menus.SignEditor;
import dev.mylesmor.sudosigns.data.SudoUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;


/**
 * InventoryListener class to listen for GUI actions.
 * @author MylesMor
 * @author https://mylesmor.dev
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        SudoUser user = SudoSigns.users.get(p.getUniqueId());
        if (user != null) {
            if (user.isEditing()) {
                SignEditor editor = user.getEditor();
                if (e.getCurrentItem() != null) {
                    e.setCancelled(true);
                    Material m = e.getCurrentItem().getType();
                    String itemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                    switch (editor.getCurrentPage()) {
                        case MAIN:
                            checkForMainMenuClicks(editor, m);
                            break;
                        case COMMANDS:
                            checkForCommandsClicks(editor, m, itemName);
                            break;
                        case PERMISSIONS:
                            checkForPermissionsClicks(editor, m, itemName);
                            break;
                        case CHOOSE_COMMAND:
                            chooseCommandType(editor, m);
                            break;
                        case CHOOSE_PERMISSION:
                            choosePermissionType(editor, m);
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        SudoUser user = SudoSigns.users.get(p.getUniqueId());
        if (user != null) {
            // Checks whether the user has closed the GUI.
            if (user.isEditing()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(SudoSigns.sudoSignsPlugin, () -> {
                    InventoryView currentInv = p.getOpenInventory();
                    if (currentInv.getTitle().equalsIgnoreCase("CRAFTING") && !user.isTextInput()) {
                        user.getEditor().endEditor();
                        user.removeEditor();
                    }
                }, 5L);
            }
        }
    }

    /**
     * Checks for GUI clicks in the main menu.
     * @param editor The SignEditor class of the particular user.
     * @param m The material clicked on in the menu.
     */
    public void checkForMainMenuClicks(SignEditor editor, Material m) {
        switch (m) {
            case BOOK:
                editor.prepareRename();
                break;
            case OAK_SIGN:
                editor.editSignText();
                break;
            case BARRIER:
                editor.goToPermissions();
                break;
            case COMMAND_BLOCK:
                editor.goToCommands();
        }
    }

    /**
     * Checks for GUI clicks in the Commands menu.
     * @param editor The SignEditor class of the particular user.
     * @param m The material clicked on in the menu.
     * @param itemName The name of the item clicked.
     */
    public void checkForCommandsClicks(SignEditor editor, Material m, String itemName) {
        switch (m) {
            case WRITABLE_BOOK:
                editor.getCommandsMenu().prepareCommand();
                break;
            case BOOK:
                editor.getCommandsMenu().deleteCommand(ChatColor.stripColor(itemName));
                break;
            case ARROW:
                editor.goToMain();
        }
    }

    public void choosePermissionType(SignEditor editor, Material m) {
        switch (m) {
            case PLAYER_HEAD:
                editor.getPermMenu().addPermission(true, null);
                break;
            case COMMAND_BLOCK:
                editor.getPermMenu().addPermission(false, null);
                break;
            case ARROW:
                editor.goToPermissions();
        }
    }

    public void chooseCommandType(SignEditor editor, Material m) {
        switch (m) {
            case PLAYER_HEAD:
                editor.getCommandsMenu().chooseCommandType(PlayerInput.PLAYER_COMMAND);
                break;
            case COMMAND_BLOCK:
                editor.getCommandsMenu().chooseCommandType(PlayerInput.CONSOLE_COMMAND);
                break;
            case ARROW:
                editor.goToCommands();
        }
    }




    /**
     * Checks for GUI clicks in the Permissions menu.
     * @param editor The SignEditor class of the particular user.
     * @param m The material clicked on in the menu.
     * @param itemName The name of the item clicked.
     */
    public void checkForPermissionsClicks(SignEditor editor, Material m, String itemName) {
        switch (m) {
            case BOOK:
                editor.getPermMenu().deletePermission(ChatColor.stripColor(itemName));
                break;
            case WRITABLE_BOOK:
                editor.getPermMenu().preparePermission();
                break;
            case ARROW:
                editor.goToMain();
        }
    }
}
