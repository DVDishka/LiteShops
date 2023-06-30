package ru.dvdishka.shops.handlers.commands.open;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.common.CommonVariables;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class OpenShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, CommandArguments args) {

        int shopsAmount = CommonVariables.shops.size();

        if (shopsAmount == 0) {

            sendFailure(sender, "There are no shops yet");
            return false;
        }

        sender.openInventory(CommonVariables.shopMenu.get(0));

        return true;
    }

}
