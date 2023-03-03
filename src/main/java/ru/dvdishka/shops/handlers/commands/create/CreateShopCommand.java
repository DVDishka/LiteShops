package ru.dvdishka.shops.handlers.commands.create;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.dvdishka.shops.Classes.PlayerCommandHandler;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.common.ConfigVariables;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class CreateShopCommand implements PlayerCommandHandler {

    @Override
    public boolean execute(Player sender, Object[] args) {

        String shopName = (String) args[0];


        for (Shop shop : CommonVariables.shops) {

            if (shop.getName().equals(shopName)) {

                sendFailure(sender, "Shop with that name already exists!");
                return false;
            }
        }

        for (Shop shop : CommonVariables.infiniteShops) {

            if (shop.getName().equals(shopName)) {

                sendFailure(sender, "Shop with that name already exists!");
                return false;
            }
        }

        CommonVariables.playerShopCreating.put(sender.getName(), shopName);

        BaseComponent[] text = new ComponentBuilder("Creating a shop costs " + ConfigVariables.shopCost.getAmount() + " "
                + ConfigVariables.shopCost.getType().name().toLowerCase() + "\n")
                .append("[CREATE]")
                .color(net.md_5.bungee.api.ChatColor.GREEN)
                .event(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/shop creating " + shopName + " " + sender.getName()))
                .append("   ")
                .append("[CANCEL]")
                .color(net.md_5.bungee.api.ChatColor.RED)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shop cancel " + sender.getName()))
                .create();
        sender.spigot().sendMessage(text);

        return true;
    }
}
