package ru.dvdishka.shops.common;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Functions {

    public static boolean hasItem(Player player, ItemStack item) {

        int amount = item.getAmount();
        int playerItemAmount = 0;
        Inventory inventory = player.getInventory();
        for (ItemStack checkItem : inventory) {
            if (checkItem != null && checkItem.getType().equals(item.getType())) {
                playerItemAmount += checkItem.getAmount();
            }
        }
        return playerItemAmount >= amount;
    }

    public static boolean removeItem(Player player, ItemStack item) {

        int amount = item.getAmount();
        int playerItemAmount = 0;
        Inventory inventory = player.getInventory();
        for (ItemStack checkItem : inventory) {
            if (checkItem != null && checkItem.getType().equals(item.getType())) {
                playerItemAmount += checkItem.getAmount();
            }
        }
        if (playerItemAmount < amount) {
            return false;
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack checkItem = inventory.getItem(i);
            if (checkItem != null && checkItem.getType().equals(item.getType())) {
                if (checkItem.getAmount() <= amount) {
                    amount -= checkItem.getAmount();
                    inventory.setItem(i, null);
                } else {
                    checkItem.setAmount(checkItem.getAmount() - amount);
                    inventory.setItem(i, checkItem);
                    amount = 0;
                }
            }
            if (amount == 0) {
                break;
            }
        }
        return true;
    }

    public static ArrayList<String> splitBySpace(String str) {

        ArrayList<String> splitStrings = new ArrayList<>();
        String splitString = "";

        for (char ch : str.toCharArray()) {
            if (ch == ' ' || ch == '\n') {
                splitStrings.add(splitString);
                splitString = "";
            } else {
                splitString += ch;
            }
        }
        splitStrings.add(splitString);
        return splitStrings;
    }

    public static String removeChatColors(String str) {

        for (ChatColor chatColor : ChatColor.values()) {

            str = str.replace(chatColor.toString(), "");
        }

        return str;
    }

    public static boolean isCreatingShop(Player player) {

        return CommonVariables.playerShopCreating.containsKey(player.getName());
    }

    public static boolean doHaveOneOfPermissions(Player player, List<String> permissions) {

        for (String permission : permissions) {

            if (player.hasPermission(permission)) {

                return true;
            }
        }
        return false;
    }

    public static void sendSuccess(Player player, String message) {

        player.sendMessage(ChatColor.DARK_GREEN + (ChatColor.BOLD + message));
    }

    public static void sendSuccess(Player player, String message, ChatColor color) {

        player.sendMessage(color + (ChatColor.BOLD + message));
    }

    public static void sendFailure(Player player, String message) {

        player.sendMessage(ChatColor.RED + (ChatColor.BOLD + message));
    }

    public static void sendFailure(Player player, String message, ChatColor color) {

        player.sendMessage(color + (ChatColor.BOLD + message));
    }
}
