package ru.dvdishka.shops.handlers.commands.edit;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.inventory.meta.ItemMeta;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.common.ConfigVariables;

import java.util.ArrayList;
import java.util.List;

import static ru.dvdishka.shops.common.Functions.sendFailure;
import static ru.dvdishka.shops.common.Functions.sendSuccess;

public class EditPriceShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, CommandArguments args) {

        int page = 0;
        int index = 0;
        int price = 0;

        Shop shop = Shop.getShop((String) args.get(0));

        if (shop == null) {

            sendFailure(sender, "There is no shop with that name!");
            return false;
        }

        if (!shop.getOwner().equals(sender.getName())) {

            sendFailure(sender, "You are not the owner of this shop!");
            return false;
        }

        page = (Integer) args.get(1);

        index = (Integer) args.get(2);

        if (index < 1 || index > shop.getUpgrade().getLineProgress() * 9) {

            sendFailure(sender, "Index must be >= 1 and <= " + shop.getUpgrade().getLineProgress() * 9);
            return false;
        }

        price = (Integer) args.get(4);

        if (price <= 0) {

            sendFailure(sender, "Amount must be > 0!");
            return false;
        }

        try {

            Material.valueOf(((String) args.get(3)).toUpperCase());

        } catch (Exception e) {

            sendFailure(sender, "Invalid price item!");
            return false;
        }

        if (!shop.isInfinite()) {

            if (CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1) == null ||
                    CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getType()
                            .equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                    CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1)
                            .equals(CommonVariables.prevPage) ||
                    CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).equals(CommonVariables.nextPage)) {

                sendFailure(sender, "There is no item in this slot!");
                return false;
            }

            ItemMeta meta = CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getItemMeta();

            List<String> list = new ArrayList<>();

            list.add(ChatColor.GREEN + "Price: " + ChatColor.RED + (String.valueOf(args.get(4))) + " " + ((String) args.get(3)).toUpperCase());

            meta.setLore(list);

            CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).setItemMeta(meta);

        } else {

            if (CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1) == null ||
                    CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getType()
                            .equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                    CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1)
                            .equals(CommonVariables.prevPage) ||
                    CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).equals(CommonVariables.nextPage)) {

                sendFailure(sender, "There is no item in this slot!");
                return false;
            }

            ItemMeta meta = CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getItemMeta();

            List<String> list = new ArrayList<>();

            if (shop.isSell()) {

                list.add(ChatColor.RED + "Sell for: " + ChatColor.GREEN + ((String) args.get(4)) + " " + ((String) args.get(3)).toUpperCase());

            } else {

                list.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ((String) args.get(4)) + " " + ((String) args.get(3)).toUpperCase());
            }

            meta.setLore(list);

            CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).setItemMeta(meta);
        }

        sendSuccess(sender, "Price has been set");

        return true;
    }
}
