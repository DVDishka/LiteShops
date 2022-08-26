package ru.dvdishka.shops.shop.shopHandlers;

import org.bukkit.Material;
import ru.dvdishka.shops.shop.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {

            boolean flag = false;
            for (Shop shop : CommonVariables.shops) {
                if (shop.getOwner().equals(sender.getName())) {
                    flag = true;
                }
            }
            if (flag) {
                return List.of("create", "edit", "upgrade", "open", "coffer");
            } else {
                return List.of("create", "open");
            }
        }

        if (args.length == 2) {

            if (args[0].equals("create")) {
                return List.of("<name>");
            }

            else if (args[0].equals("edit") || args[0].equals("upgrade") || args[0].equals("coffer")) {
                ArrayList<String> list = new ArrayList<>();
                for (Shop shop : CommonVariables.shops) {
                    if (shop.getOwner().equals(sender.getName())) {
                        list.add(shop.getName());
                    }
                }
                list.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                return list;
            }
        }

        if (args.length == 3) {

            if (args[0].equals("edit")) {
                return List.of("price", "name", "icon");
            }
        }

        if (args.length == 4) {

            if (args[0].equals("edit") && args[2].equals("price")) {
                return List.of("<page>");
            } else if (args[0].equals("edit") && args[2].equals("name")) {
                return List.of("<newName>");
            }
        }

        if (args.length == 5) {

            if (args[0].equals("edit") && args[2].equals("price")) {
                return List.of("<index>");
            }
        }

        if (args.length == 6) {

            if (args[0].equals("edit") && args[2].equals("price")) {
                ArrayList<String> list = new ArrayList<>();
                for (Material material : Material.values()) {
                    list.add(material.name().toLowerCase());
                }
                list.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                return list;
            }
        }

        if (args.length == 7) {

            if (args[0].equals("edit") && args[2].equals("price")) {
                return List.of("<amount>");
            }
        }
        return List.of();
    }
}
