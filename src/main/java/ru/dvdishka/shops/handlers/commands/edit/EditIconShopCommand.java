package ru.dvdishka.shops.handlers.commands.edit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;

import java.util.ArrayList;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class EditIconShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, Object[] args) {

        Shop shop = Shop.getShop((String) args[0]);

        if (shop == null) {

            sendFailure(sender, "There is no shop with that name!");
            return false;
        }

        if (!shop.getOwner().equals(sender.getName())) {

            sendFailure(sender, "You are not owner of this shop!");
            return false;
        }

        int iconsAmount = Material.values().length - 2;

        if (iconsAmount == 0) {

            sendFailure(sender, "There are no icons yet");
            return false;
        }

        ArrayList<Inventory> icons = new ArrayList<>();

        int i = 1;

        while (iconsAmount > 0) {

            Inventory page = Bukkit.createInventory(null, 54,  ChatColor.GOLD +
                    (ChatColor.BOLD + "Icons: ") + ChatColor.RESET + i);

            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            icons.add(page);
            iconsAmount -= 52;

            i++;
        }

        int stackIndex = 0;
        int inventoryIndex = 0;

        for (Material icon : Material.values()) {

            if (!icon.equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) && !icon.equals(Material.AIR)) {

                if (stackIndex == 45) {

                    stackIndex++;
                }

                if (stackIndex == 53) {

                    stackIndex = 0;
                    inventoryIndex++;
                }

                icons.get(inventoryIndex).setItem(stackIndex, new ItemStack(icon));
                stackIndex++;

                if (icons.get(inventoryIndex).getItem(stackIndex - 1) == null) {

                    stackIndex--;
                }
            }
        }

        int pagesAmount = icons.size();

        for (int page = 0; page < pagesAmount; page++) {

            boolean isClear = true;

            for (ItemStack item : icons.get(page)) {

                if (item != null && !item.equals(CommonVariables.nextPage) && !item.equals(CommonVariables.prevPage)) {

                    isClear = false;
                    break;
                }
            }

            if (isClear) {

                icons.remove(page);
                pagesAmount--;
                page--;
            }
        }

        stackIndex = 0;

        Inventory lastPage = icons.get(icons.size() - 1);

        for (ItemStack itemStack : lastPage) {

            if (itemStack == null) {

                lastPage.setItem(stackIndex, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }
            stackIndex++;
        }

        icons.set(icons.size() - 1, lastPage);

        sender.openInventory(icons.get(0));

        CommonVariables.playerShopIconChoose.put(sender.getName(), shop.getName());
        CommonVariables.iconMenu = icons;

        return true;
    }
}
