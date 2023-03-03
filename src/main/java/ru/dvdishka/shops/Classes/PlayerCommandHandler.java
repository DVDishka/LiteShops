package ru.dvdishka.shops.Classes;

import org.bukkit.entity.Player;
import static ru.dvdishka.shops.common.Functions.sendSuccess;
import static ru.dvdishka.shops.common.Functions.sendFailure;

public interface PlayerCommandHandler {

    public boolean execute(Player sender, Object[] args);
}
