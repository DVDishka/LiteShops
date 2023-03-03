package ru.dvdishka.shops.handlers.commands.coffer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.Classes.Shop;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class CofferOpenShopCommand implements PlayerCommandHandler {

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

        if (shop.getCoffer().size() == 0) {

            sendFailure(sender, "You do not have anything in coffer!");
            return false;
        }

        int index = 0;

        for (ItemStack itemStack : shop.getCoffer().get(0)) {

            if (itemStack == null) {

                shop.getCoffer().get(0).setItem(index, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }

            index++;
        }

        Inventory inventory = shop.getCoffer().get(0);
        sender.openInventory(inventory);

        return true;
    }
}
