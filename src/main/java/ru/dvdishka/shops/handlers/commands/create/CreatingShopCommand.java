package ru.dvdishka.shops.handlers.commands.create;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.dvdishka.shops.Classes.Color;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.common.ConfigVariables;
import ru.dvdishka.shops.common.Functions;

import java.util.ArrayList;
import java.util.Arrays;

import static ru.dvdishka.shops.common.Functions.sendFailure;
import static ru.dvdishka.shops.common.Functions.sendSuccess;

public class CreatingShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, CommandArguments args) {

        String shopName = (String) args.get(0);
        String playerName = (String) args.get(1);

        if (CommonVariables.playerShopCreating.get(playerName) == null ||
                !CommonVariables.playerShopCreating.get(playerName).equals(shopName)) {

            return false;
        }

        Player senderPlayer = Bukkit.getPlayer(playerName);
        assert senderPlayer != null;

        if (!Functions.removeItem(senderPlayer, ConfigVariables.shopCost)) {

            sendFailure(senderPlayer, "You do not have " + ConfigVariables.shopCost.getAmount()
                    + " " + ConfigVariables.shopCost.getType().name().toLowerCase());
            return false;
        }

        for (Shop checkShop : CommonVariables.shops) {

            if (checkShop.getName().equals(args.get(1))) {

                sendFailure(senderPlayer, "There is already a shop with the same name!");
                return false;
            }
        }
        ItemStack icon = new ItemStack(Material.BARRIER);
        ItemMeta iconMeta = icon.getItemMeta();
        ChatColor shopColor = Color.getRandomColor().getChatColor();

        iconMeta.setDisplayName(shopColor + (ChatColor.BOLD + shopName));
        iconMeta.setLore(Arrays.asList(ChatColor.GREEN + "[Click to open]"));
        icon.setItemMeta(iconMeta);

        Shop shop = new Shop(shopName, senderPlayer.getName(), icon, shopColor);
        CommonVariables.shops.add(shop);

        ArrayList<Inventory> pages = new ArrayList<>();

        Inventory inventory = Bukkit.createInventory(null, ConfigVariables.defaultInventorySize,
                shopColor + (ChatColor.BOLD + shop.getName() + ": ") + ChatColor.RESET + "1");

        inventory.setItem(ConfigVariables.defaultPrevPageIndex, CommonVariables.prevPage);
        inventory.setItem(ConfigVariables.defaultNextPageIndex, CommonVariables.nextPage);

        pages.add(inventory);

        CommonVariables.shopsInventories.put(shop.getName(), pages);
        CommonVariables.playerShopCreating.remove(senderPlayer.getName());

        int slotIndex = 0;
        boolean hasShopBeenAdded = false;

        for (ItemStack item : CommonVariables.shopMenu.get(CommonVariables.shopMenu.size() - 1)) {

            if (item == null || item.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {

                CommonVariables.shopMenu.get(CommonVariables.shopMenu.size() - 1).setItem(slotIndex, icon);
                hasShopBeenAdded = true;
                break;
            }
            slotIndex++;
        }

        if (!hasShopBeenAdded) {

            Inventory page = Bukkit.createInventory(null, 54,
                    ChatColor.DARK_GREEN + (ChatColor.BOLD + "Shops: ") +
                            ChatColor.WHITE + (ChatColor.RESET + String.valueOf(CommonVariables.shopMenu.size() + 1)));

            for (int i = 0; i < 54; i++) {

                page.setItem(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }

            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            page.setItem(0, icon);

            CommonVariables.shopMenu.add(page);
        }

        senderPlayer.playSound(senderPlayer.getLocation(),
                Sound.ENTITY_PLAYER_LEVELUP,
                50, 1);

        sendSuccess(senderPlayer, shop.getName() + " has been created!", shop.getColor());

        return true;
    }
}
