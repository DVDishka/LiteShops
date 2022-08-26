package ru.dvdishka.shops.common;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Functions {

    public static boolean doHave(Player player, ItemStack item) {

        int amount = item.getAmount();
        int playerItemAmount = 0;
        Inventory inventory = player.getInventory();
        for (ItemStack checkItem : inventory) {
            if (checkItem != null && checkItem.getType().equals(item.getType())) {
                playerItemAmount++;
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
}
