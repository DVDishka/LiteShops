package ru.dvdishka.shops.handlers.commands.open;

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

import java.util.ArrayList;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class OpenShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, Object[] args) {

        int shopsAmount = CommonVariables.shops.size();

        if (shopsAmount == 0) {

            sendFailure(sender, "There are no shops yet");
            return false;
        }

        sender.openInventory(CommonVariables.shopMenu.get(0));

        return true;
    }

}
