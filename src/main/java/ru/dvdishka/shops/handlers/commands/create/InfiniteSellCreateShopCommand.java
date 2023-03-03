package ru.dvdishka.shops.handlers.commands.create;

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

import java.util.ArrayList;
import java.util.Arrays;

import static ru.dvdishka.shops.common.Functions.sendFailure;
import static ru.dvdishka.shops.common.Functions.sendSuccess;

public class InfiniteSellCreateShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, Object[] args) {

        for (Shop shop : CommonVariables.infiniteShops) {

            if (shop.getName().equals((String) args[0])) {

                sendFailure(sender, "A shop with that name already exists!");
                return false;
            }
        }

        for (Shop shop : CommonVariables.shops) {

            if (shop.getName().equals((String) args[0])) {

                sendFailure(sender, "A shop with that name already exists!");
                return false;
            }
        }

        ItemStack shopIcon = new ItemStack(Material.BARRIER);
        ItemMeta shopIconMeta = shopIcon.getItemMeta();
        ChatColor shopColor = Color.getRandomColor().getChatColor();

        shopIconMeta.setDisplayName(shopColor + (ChatColor.BOLD + (String) args[0]));
        shopIconMeta.setLore(Arrays.asList(ChatColor.GREEN + "[Click to open]"));
        shopIcon.setItemMeta(shopIconMeta);

        shopIcon.setItemMeta(shopIconMeta);

        Shop newShop = new Shop((String) args[0], sender.getName(), shopIcon, shopColor, true, true);

        CommonVariables.infiniteShops.add(newShop);

        ArrayList<Inventory> shopPages = new ArrayList<>();
        Inventory shopPage = Bukkit.createInventory(null,
                ConfigVariables.defaultInventorySize,
                shopColor + (ChatColor.BOLD + newShop.getName() + ": ") + ChatColor.RESET + "1");

        shopPage.setItem(ConfigVariables.defaultPrevPageIndex, CommonVariables.prevPage);
        shopPage.setItem(ConfigVariables.defaultNextPageIndex, CommonVariables.nextPage);

        shopPages.add(shopPage);

        CommonVariables.infiniteShopsInventories.put(newShop.getName(), shopPages);

        int slotIndex = 0;
        boolean hasShopBeenAdded = false;

        for (ItemStack item : CommonVariables.infiniteSellShopMenu.get(CommonVariables.shopMenu.size() - 1)) {

            if (item == null || item.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {

                CommonVariables.infiniteSellShopMenu.get(CommonVariables.infiniteSellShopMenu.size() - 1).setItem(slotIndex, shopIcon);
                hasShopBeenAdded = true;
                break;
            }
            slotIndex++;
        }

        if (!hasShopBeenAdded) {

            Inventory page = Bukkit.createInventory(null, 54,
                    ChatColor.RED + (ChatColor.BOLD + "Shops: ") + ChatColor.RESET +
                            CommonVariables.infiniteBuyShopMenu.size() + 1);
            for (int i = 0; i < 54; i++) {

                page.setItem(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }

            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            page.setItem(0, shopIcon);

            CommonVariables.infiniteSellShopMenu.add(page);
        }

        sender.playSound(sender.getLocation(),
                Sound.ENTITY_PLAYER_LEVELUP,
                50, 1);

        sendSuccess(sender, newShop.getName() + " has been created!", shopColor);

        return true;
    }
}
