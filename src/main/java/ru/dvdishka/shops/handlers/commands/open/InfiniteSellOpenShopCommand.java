package ru.dvdishka.shops.handlers.commands.open;

import org.bukkit.entity.Player;

import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class InfiniteSellOpenShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, Object[] args) {

        int shopsAmount = 0;

        for (Shop shop : CommonVariables.infiniteShops) {

            if (shop.isSell()) {

                shopsAmount++;
            }
        }

        if (shopsAmount == 0) {

            sendFailure(sender, "There are no shops yet");
            return false;
        }

        sender.openInventory(CommonVariables.infiniteSellShopMenu.get(0));

        return true;
    }
}
