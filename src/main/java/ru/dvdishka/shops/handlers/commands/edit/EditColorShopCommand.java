package ru.dvdishka.shops.handlers.commands.edit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.common.Functions;

import java.util.ArrayList;

import static ru.dvdishka.shops.common.Functions.sendFailure;
import static ru.dvdishka.shops.common.Functions.sendSuccess;

public class EditColorShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, Object[] args) {

        String shopName = (String) args[0];
        ChatColor newShopColor = (ChatColor) args[1];

        Shop shop = Shop.getShop((String) args[0]);

        if (shop == null) {

            sendFailure(sender, "There is no shop with that name!");
            return false;
        }

        if (!shop.getOwner().equals(sender.getName())) {

            sendFailure(sender, "You are not owner of this shop!");
            return false;
        }

        shop.setColor(newShopColor);

        ArrayList<Inventory> shopMenu = new ArrayList<>();

        if (!shop.isInfinite()) {

            shopMenu = CommonVariables.shopMenu;

        } else {

            if (shop.isSell()) {

                shopMenu = CommonVariables.infiniteSellShopMenu;

            } else {

                shopMenu = CommonVariables.infiniteBuyShopMenu;
            }
        }

        for (Inventory shopMenuPage : shopMenu) {

            for (ItemStack shopMenuIcon : shopMenuPage) {

                if (shopMenuIcon != null && shopMenuIcon.getItemMeta() != null &&
                        Functions.removeChatColors(shopMenuIcon.getItemMeta().getDisplayName()).equals(shopName)) {

                    ItemMeta shopIconMeta = shopMenuIcon.getItemMeta();
                    shopIconMeta.setDisplayName(newShopColor + (ChatColor.BOLD + shopName));

                    shopMenuIcon.setItemMeta(shopIconMeta);
                }
            }
        }

        ArrayList<Inventory> newShopPages = new ArrayList<>();
        ArrayList<Inventory> oldShopPages = new ArrayList<>();

        if (!shop.isInfinite()) {

            oldShopPages = CommonVariables.shopsInventories.get(shop.getName());

        } else {

            oldShopPages = CommonVariables.infiniteShopsInventories.get(shop.getName());
        }

        int i = 1;

        for (Inventory oldShopPage : oldShopPages) {

            Inventory newShopPage = Bukkit.createInventory(null, oldShopPage.getSize(),
                    newShopColor + (ChatColor.BOLD + shopName + ": ") + ChatColor.RESET + i);

            newShopPage.setContents(oldShopPage.getContents());
            newShopPages.add(newShopPage);

            i++;
        }

        if (!shop.isInfinite()) {

            CommonVariables.shopsInventories.remove(shopName);
            CommonVariables.shopsInventories.put(shopName, newShopPages);

        } else {

            CommonVariables.infiniteShopsInventories.remove(shopName);
            CommonVariables.infiniteShopsInventories.put(shopName, newShopPages);
        }

        sendSuccess(sender, "Shop color has been changed!", newShopColor);

        return false;
    }
}
