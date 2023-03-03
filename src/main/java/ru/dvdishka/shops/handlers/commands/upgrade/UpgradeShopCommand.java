package ru.dvdishka.shops.handlers.commands.upgrade;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.common.ConfigVariables;

import java.util.ArrayList;
import java.util.List;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class UpgradeShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, Object[] args) {

        Shop shop = Shop.getShop((String) args[0]);

        if (shop == null) {

            sendFailure(sender, "There is no shop with that name!");
            return false;
        }

        if (!shop.getOwner().equals(sender.getName())) {

            sendFailure(sender, "You are not the owner of this shop!");
            return false;
        }

        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.LIGHT_PURPLE +
                (ChatColor.BOLD + "Upgrade"));

        ItemStack newPage = new ItemStack(Material.PAPER);
        ItemMeta newPageMeta = newPage.getItemMeta();
        newPageMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "New Page");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ConfigVariables.newPageCost.getAmount() + " " +
                ConfigVariables.newPageCost.getType().name());
        lore.add(ChatColor.AQUA + "Progress: " + ChatColor.GOLD + shop.getUpgrade().getPageProgress());

        newPageMeta.setLore(lore);
        newPage.setItemMeta(newPageMeta);
        inventory.setItem(13, newPage);

        ItemStack newLine = new ItemStack(Material.STICK);
        ItemMeta newLineMeta = newLine.getItemMeta();
        newLineMeta.setDisplayName(ChatColor.GREEN + "New Line");

        List<String> newLineLore = new ArrayList<>();
        newLineLore.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ConfigVariables.newLineCost.getAmount() + " " +
                ConfigVariables.newLineCost.getType().name());
        newLineLore.add(ChatColor.AQUA + "Progress: " + ChatColor.GOLD + shop.getUpgrade().getLineProgress() + "/6");

        newLineMeta.setLore(newLineLore);
        newLine.setItemMeta(newLineMeta);
        inventory.setItem(12, newLine);

        int i = 0;

        for (ItemStack itemStack : inventory) {

            if (itemStack == null) {

                inventory.setItem(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }
            i++;
        }

        CommonVariables.upgradeMenu.put(sender.getName(), inventory);
        CommonVariables.playerShopUpgrade.put(sender.getName(), shop.getName());

        sender.openInventory(inventory);

        return true;
    }
}
