package ru.dvdishka.shops.handlers.commands.create;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.common.CommonVariables;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class CreatingCancelShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, CommandArguments args) {

        Player senderPlayer = Bukkit.getPlayer((String) args.get(0));

        if (!CommonVariables.playerShopCreating.containsKey(senderPlayer.getName())) {

            return false;
        }

        CommonVariables.playerShopCreating.remove(senderPlayer.getName());

        sendFailure(senderPlayer, "You cancelled the creation of the shop!");

        return true;
    }
}
