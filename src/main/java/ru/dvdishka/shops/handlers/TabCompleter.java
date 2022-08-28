package ru.dvdishka.shops.handlers;

import org.bukkit.Material;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
            for (Shop shop : CommonVariables.infiniteShops) {
                if (shop.getOwner().equals(sender.getName())) {
                    flag = true;
                }
            }
            if (flag) {
                return Arrays.asList("create", "edit", "upgrade", "open", "coffer", "infinite");
            } else {
                return Arrays.asList("create", "open", "infinite");
            }
        }

        if (args.length == 2) {

            if (args[0].equals("create")) {
                return Arrays.asList("<name>");
            }

            else if (args[0].equals("edit") || args[0].equals("upgrade") || args[0].equals("coffer")) {

                ArrayList<String> list = new ArrayList<>();

                for (Shop shop : CommonVariables.shops) {

                    if (shop.getOwner().equals(sender.getName())) {

                        list.add(shop.getName());
                    }
                }

                for (Shop shop : CommonVariables.infiniteShops) {

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
            } else if (args[0].equals("infinite")) {

                return Arrays.asList("sell", "buy");
            }
        }

        if (args.length == 3) {

            if (args[0].equals("edit")) {

                return Arrays.asList("price", "name", "icon");

            } else if (args[0].equals("infinite") && sender.isOp()) {

                return Arrays.asList("create", "open");

            } else if (args[0].equals("infinite") && !sender.isOp()) {

                return Arrays.asList("open");
            }
        }

        if (args.length == 4) {

            if (args[0].equals("edit") && args[2].equals("price")) {

                return Arrays.asList("<page>");

            } else if (args[0].equals("edit") && args[2].equals("name")) {

                return Arrays.asList("<newName>");

            } else if (args[0].equals("infinite") && args[1].equals("create")) {

                return Arrays.asList("<shopName>");
            }
        }

        if (args.length == 5) {

            if (args[0].equals("edit") && args[2].equals("price")) {
                return Arrays.asList("<index>");
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
                return Arrays.asList("<amount>");
            }
        }
        return Arrays.asList();
    }
}
