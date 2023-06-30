package ru.dvdishka.shops.handlers.commands.edit;

import dev.jorel.commandapi.executors.CommandArguments;
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
import java.util.Arrays;

import static ru.dvdishka.shops.common.Functions.sendFailure;
import static ru.dvdishka.shops.common.Functions.sendSuccess;

public class EditNameShopCommand implements PlayerCommandHandler {


    @Override
    public boolean execute(Player sender, CommandArguments args) {

        String newShopName = (String) args.get(1);
        Shop shop = Shop.getShop((String) args.get(0));

        if (shop == null) {

            sendFailure(sender, "There is no shop with that name!");
            return false;
        }

        String oldShopName = shop.getName();

        if (!shop.getOwner().equals(sender.getName())) {

            sendFailure(sender, "You are not the owner of this shop!");
            return false;
        }

        for (Shop checkShop : CommonVariables.shops) {

            if (checkShop.getName().equals(newShopName)) {

                sendFailure(sender, "A shop with the same name already exists!");
                return false;
            }
        }

        for (Shop checkShop : CommonVariables.infiniteShops) {

            if (checkShop.getName().equals(newShopName)) {

                sendFailure(sender, "A shop with the same name already exists!");
                return false;
            }
        }

        ArrayList<Inventory> oldShopPages = new ArrayList<>();
        ArrayList<Inventory> newShopPages = new ArrayList<>();

        if (!shop.isInfinite()) {

            oldShopPages = CommonVariables.shopsInventories.get(shop.getName());

        } else {

            oldShopPages = CommonVariables.infiniteShopsInventories.get(shop.getName());
        }

        int i = 1;

        for (Inventory oldShopPage : oldShopPages) {

            Inventory newShopPage = Bukkit.createInventory(null, oldShopPage.getSize(),
                    shop.getColor() + (ChatColor.BOLD + newShopName + ": ") + ChatColor.RESET + i);

            newShopPage.setContents(oldShopPage.getContents());
            newShopPages.add(newShopPage);

            i++;
        }

        if (!shop.isInfinite()) {

            CommonVariables.shopsInventories.remove(shop.getName());
            CommonVariables.shopsInventories.put(newShopName, newShopPages);

        } else {

            CommonVariables.infiniteShopsInventories.remove(shop.getName());
            CommonVariables.infiniteShopsInventories.put(newShopName, newShopPages);
        }

        ArrayList<Shop> shops = new ArrayList<>();

        if (!shop.isInfinite()) {

            shops = CommonVariables.shops;

        } else {

            shops = CommonVariables.infiniteShops;
        }

        for (Shop checkShop : shops) {

            if (checkShop.getName().equals(shop.getName())) {

                checkShop.setName(newShopName);
                ItemStack icon = checkShop.getIcon();
                ItemMeta iconMeta = icon.getItemMeta();
                iconMeta.setDisplayName(shop.getColor() + (ChatColor.BOLD + checkShop.getName()));
                icon.setItemMeta(iconMeta);
                checkShop.setIcon(icon);
            }
        }

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

            for (ItemStack shopIcon : shopMenuPage) {

                ItemMeta shopIconMeta = shopIcon.getItemMeta();

                if (shopIconMeta != null && Functions.removeChatColors(shopIconMeta.getDisplayName()).equals(oldShopName)) {

                    shopIconMeta.setDisplayName(shop.getColor() + (ChatColor.BOLD + newShopName));
                    shopIconMeta.setLore(Arrays.asList(ChatColor.GREEN + "[Click to open]"));

                    shopIcon.setItemMeta(shopIconMeta);
                }
            }
        }

        sendSuccess(sender, "New shop name has been set");

        return true;
    }
}
