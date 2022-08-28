package ru.dvdishka.shops.handlers;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Sound;
import ru.dvdishka.shops.common.ConfigVariables;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.dvdishka.shops.common.Functions;

import java.util.ArrayList;
import java.util.List;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Unknown command or wrong answers!");
            return false;
        }
        Player senderPlayer = Bukkit.getPlayer(sender.getName());
        String commandName = args[0];
        assert senderPlayer != null;

        if (commandName.equals("create") && args.length == 2) {

            String shopName = args[1];

            for (Shop shop : CommonVariables.shops) {
                if (shop.getName().equals(shopName)) {
                    sender.sendMessage(ChatColor.RED + "Shop with that name already exists!");
                    return false;
                }
            }

            for (Shop shop : CommonVariables.infiniteShops) {
                if (shop.getName().equals(shopName)) {
                    sender.sendMessage(ChatColor.RED + "Shop with that name already exists!");
                    return false;
                }
            }

            CommonVariables.playerShopCreating.put(senderPlayer.getName(), shopName);

            BaseComponent[] text = new ComponentBuilder("Creating a shop costs " + ConfigVariables.shopCost.getAmount() + " "
                            + ConfigVariables.shopCost.getType().name().toLowerCase() + "\n")
                    .append("[CREATE]")
                    .color(net.md_5.bungee.api.ChatColor.GREEN)
                    .event(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/shop creating " + shopName + " " + senderPlayer.getName()))
                    .append("   ")
                    .append("[CANCEL]")
                    .color(net.md_5.bungee.api.ChatColor.RED)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shop creating cancel " + senderPlayer.getName()))
                    .create();
            sender.spigot().sendMessage(text);
            return true;
        }

        if (commandName.equals("creating") && args.length == 3 && !args[1].equals("cancel")) {

            if (CommonVariables.playerShopCreating.get(args[2]) == null ||
                    !CommonVariables.playerShopCreating.get(args[2]).equals(args[1])) {

                return false;
            }

            senderPlayer = Bukkit.getPlayer(args[2]);
            assert senderPlayer != null;

            if (!Functions.removeItem(senderPlayer, ConfigVariables.shopCost)) {
                senderPlayer.sendMessage(ChatColor.RED + "You do not have " + ConfigVariables.shopCost.getAmount()
                        + " " + ConfigVariables.shopCost.getType().name().toLowerCase());
                return false;
            }

            for (Shop checkShop : CommonVariables.shops) {
                if (checkShop.getName().equals(args[1])) {
                    senderPlayer.sendMessage(ChatColor.RED + "There is already a shop with the same name!");
                    return false;
                }
            }
            ItemStack icon = new ItemStack(Material.BARRIER);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(args[1]);
            icon.setItemMeta(iconMeta);
            Shop shop = new Shop(args[1], senderPlayer.getName(), icon);
            CommonVariables.shops.add(shop);

            ArrayList<Inventory> pages = new ArrayList<>();

            Inventory inventory = Bukkit.createInventory(null, ConfigVariables.defaultInventorySize,
                    ChatColor.GOLD + shop.getName() + " 1");

            inventory.setItem(ConfigVariables.defaultPrevPageIndex, CommonVariables.prevPage);
            inventory.setItem(ConfigVariables.defaultNextPageIndex, CommonVariables.nextPage);

            pages.add(inventory);

            CommonVariables.shopsInventories.put(shop.getName(), pages);
            CommonVariables.playerShopCreating.remove(senderPlayer.getName());

            senderPlayer.playSound(senderPlayer.getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP,
                    50, 1);

            senderPlayer.sendTitle(ChatColor.DARK_GREEN + shop.getName(),
                    ChatColor.GOLD + "has been created");
            return true;
        }

        if (commandName.equals("creating") && args.length == 3 && args[1].equals("cancel")) {

            senderPlayer = Bukkit.getPlayer(args[2]);
            if (!CommonVariables.playerShopCreating.containsKey(senderPlayer.getName())) {
                return false;
            }
            CommonVariables.playerShopCreating.remove(senderPlayer.getName());
            senderPlayer.sendMessage(ChatColor.RED + "You cancelled the creation of the shop!");
            return true;
        }

        if (commandName.equals("open") && args.length == 1) {

            ArrayList<Inventory> shopsIcons = new ArrayList<>();

            int shopsAmount = CommonVariables.shops.size();

            if (shopsAmount == 0) {
                sender.sendMessage(ChatColor.RED + "There are no shops yet");
                return false;
            }

            int i = 1;

            while (shopsAmount > 0) {

                Inventory page = Bukkit.createInventory(null, 54,
                        ChatColor.GOLD + "Shops " + i);

                ItemStack prevPage = new ItemStack(Material.ARROW);
                ItemStack nextPage = new ItemStack(Material.ARROW);

                ItemMeta prevPageMeta = prevPage.getItemMeta();
                prevPageMeta.setDisplayName("<--");
                prevPage.setItemMeta(prevPageMeta);

                ItemMeta nextPageMeta = nextPage.getItemMeta();
                nextPageMeta.setDisplayName("-->");
                nextPage.setItemMeta(nextPageMeta);

                page.setItem(45, prevPage);
                page.setItem(53, nextPage);

                shopsIcons.add(page);
                shopsAmount -= 52;
                i++;
            }

            int stackIndex = 0;
            int inventoryIndex = 0;

            for (Shop shop : CommonVariables.shops) {

                if (stackIndex == 45) {

                    stackIndex++;
                }
                if (stackIndex == 53) {

                    stackIndex = 0;
                    inventoryIndex++;
                }

                shopsIcons.get(inventoryIndex).setItem(stackIndex, shop.getIcon());
                stackIndex++;
            }

            stackIndex = 0;

            Inventory lastPage = shopsIcons.get(inventoryIndex);

            for (ItemStack itemStack : lastPage) {

                if (itemStack == null) {

                    lastPage.setItem(stackIndex, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                }
                stackIndex++;
            }

            shopsIcons.set(inventoryIndex, lastPage);
            CommonVariables.shopMenu = shopsIcons;
            senderPlayer.openInventory(shopsIcons.get(0));

            return true;
        }

        if (commandName.equals("edit") && args.length == 7 && args[2].equals("price")) {

            int page = 0;
            int index = 0;
            int price = 0;

            Shop shop = Shop.getShop(args[1]);

            if (shop == null) {
                sender.sendMessage(ChatColor.RED + "There is no shop with that name!");
                return false;
            }
            if (!shop.getOwner().equals(senderPlayer.getName())) {
                sender.sendMessage(ChatColor.RED + "You are not the owner of this shop!");
                return false;
            }
            try {
                page = Integer.parseInt(args[3]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Page must be a number");
                return false;
            }
            try {
                index = Integer.parseInt(args[4]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Index must be a number!");
                return false;
            }
            if (index < 1 || index > ConfigVariables.defaultInventorySize) {
                sender.sendMessage(ChatColor.RED + "Index must be >= 1 and <= " + ConfigVariables.defaultInventorySize);
                return false;
            }
            try {
                price = Integer.parseInt(args[6]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Amount must be a number!");
                return false;
            }
            if (price <= 0) {
                sender.sendMessage(ChatColor.RED + "Amount must be > 0!");
                return false;
            }
            try {
                Material.valueOf(args[5].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid price item!");
                return false;
            }

            if (!shop.isInfinite()) {

                if (CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1) == null ||
                        CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getType()
                                .equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                        CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1)
                                .equals(CommonVariables.prevPage) ||
                        CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).equals(CommonVariables.nextPage)) {

                    sender.sendMessage(ChatColor.RED + "There is no item in this slot!");
                    return false;
                }

                ItemMeta meta = CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getItemMeta();

                List<String> list = new ArrayList<>();

                list.add(ChatColor.GREEN + "Price: " + ChatColor.RED + args[6] + " " + args[5].toUpperCase());

                meta.setLore(list);

                CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).setItemMeta(meta);

            } else {

                if (CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1) == null ||
                        CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getType()
                                .equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                        CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1)
                                .equals(CommonVariables.prevPage) ||
                        CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).equals(CommonVariables.nextPage)) {
                    sender.sendMessage(ChatColor.RED + "There is no item in this slot!");
                    return false;
                }

                ItemMeta meta = CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getItemMeta();

                List<String> list = new ArrayList<>();

                if (shop.isSell()) {

                    list.add(ChatColor.RED + "Sell for: " + ChatColor.GREEN + args[6] + " " + args[5].toUpperCase());

                } else {

                    list.add(ChatColor.GREEN + "Price: " + ChatColor.RED + args[6] + " " + args[5].toUpperCase());
                }

                meta.setLore(list);

                CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).setItemMeta(meta);
            }

            senderPlayer.sendTitle(ChatColor.DARK_GREEN + shop.getName(), ChatColor.GOLD +
                    "price has been set");

            return true;
        }

        if (commandName.equals("edit") && args.length == 4 && args[2].equals("name")) {

            String name = args[3];
            Shop shop = Shop.getShop(args[1]);
            if (shop == null) {
                sender.sendMessage(ChatColor.RED + "There is no shop with that name!");
                return false;
            }
            if (!shop.getOwner().equals(senderPlayer.getName())) {
                sender.sendMessage(ChatColor.RED + "You are not the owner of this shop!");
                return false;
            }
            for (Shop checkShop : CommonVariables.shops) {
                if (checkShop.getName().equals(name)) {
                    sender.sendMessage(ChatColor.RED + "A shop with the same name already exists!");
                    return false;
                }
            }
            ArrayList<Inventory> shopInventory = CommonVariables.shopsInventories.get(shop.getName());
            ArrayList<Inventory> newShopInventory = new ArrayList<>();
            int i = 1;
            for (Inventory inventory : shopInventory) {
                Inventory newInventory = Bukkit.createInventory(null, inventory.getSize(),
                        ChatColor.GOLD + name + " " + i);
                newInventory.setContents(inventory.getContents());
                newShopInventory.add(newInventory);
                i++;
            }
            CommonVariables.shopsInventories.remove(shop.getName());
            CommonVariables.shopsInventories.put(name, newShopInventory);
            for (Shop checkShop : CommonVariables.shops) {
                if (checkShop.getName().equals(shop.getName())) {
                    checkShop.setName(name);
                    ItemStack icon = checkShop.getIcon();
                    ItemMeta iconMeta = icon.getItemMeta();
                    iconMeta.setDisplayName(checkShop.getName());
                    icon.setItemMeta(iconMeta);
                    checkShop.setIcon(icon);
                }
            }
            senderPlayer.sendTitle(ChatColor.DARK_GREEN + name,
                    ChatColor.GOLD + "Shop name has been set");
            return true;
        }

        if (commandName.equals("edit") && args.length == 3 && args[2].equals("icon")) {

            Shop shop = Shop.getShop(args[1]);
            if (shop == null) {
                sender.sendMessage(ChatColor.RED + "There is no shop with that name!");
                return false;
            }
            if (!shop.getOwner().equals(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "You are not owner of this shop!");
                return false;
            }

            int iconsAmount = Material.values().length - 2;
            if (iconsAmount == 0) {
                sender.sendMessage(ChatColor.RED + "There are no icons yet");
                return false;
            }

            ArrayList<Inventory> icons = new ArrayList<>();

            int i = 1;
            while (iconsAmount > 0) {
                Inventory page = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Icons " + i);
                ItemStack prevPage = new ItemStack(Material.ARROW);
                ItemStack nextPage = new ItemStack(Material.ARROW);
                ItemMeta prevPageMeta = prevPage.getItemMeta();
                prevPageMeta.setDisplayName("<--");
                prevPage.setItemMeta(prevPageMeta);
                ItemMeta nextPageMeta = nextPage.getItemMeta();
                nextPageMeta.setDisplayName("-->");
                nextPage.setItemMeta(nextPageMeta);
                page.setItem(45, prevPage);
                page.setItem(53, nextPage);
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
                }
            }

            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName("<--");
            prevPage.setItemMeta(prevPageMeta);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName("-->");
            nextPage.setItemMeta(nextPageMeta);

            int pagesAmount = icons.size();
            for (int page = 0; page < pagesAmount; page++) {
                boolean isClear = true;
                for (ItemStack item : icons.get(page)) {
                    if (item != null && !item.equals(nextPage) && !item.equals(prevPage)) {
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
            senderPlayer.openInventory(icons.get(0));
            CommonVariables.playerShopIconChoose.put(senderPlayer.getName(), shop.getName());
            CommonVariables.iconMenu = icons;
            return true;
        }

        if (commandName.equals("upgrade") && args.length == 2) {

            Shop shop = Shop.getShop(args[1]);
            if (shop == null) {
                sender.sendMessage(ChatColor.RED + "There is no shop with that name!");
                return false;
            }
            if (!shop.getOwner().equals(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "You are not the owner of this shop!");
                return false;
            }
            Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Upgrade");

            ItemStack newPage = new ItemStack(Material.PAPER);
            ItemMeta newPageMeta = newPage.getItemMeta();
            newPageMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "New Page");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ConfigVariables.newPageCost.getAmount() + " " +
                    ConfigVariables.newPageCost.getType().name());
            lore.add(ChatColor.AQUA + "Progress: " + ChatColor.GOLD + shop.getUpgrade().getPageProgress());
            newPageMeta.setLore(lore);
            newPage.setItemMeta(newPageMeta);
            inventory.setItem(13, newPage);

            ItemStack newLine = new ItemStack(Material.STICK);
            ItemMeta newLineMeta = newLine.getItemMeta();
            newLineMeta.setDisplayName(ChatColor.GREEN + "New Line");
            List<String> newLineLore = new ArrayList<>();
            newLineLore.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ConfigVariables.newLineCost.getAmount() + " " +
                    ConfigVariables.newLineCost.getType().name());
            newLineLore.add(ChatColor.AQUA + "Progress: " + ChatColor.GOLD + shop.getUpgrade().getLineProgress() + "/6");
            newLineMeta.setLore(newLineLore);
            newLine.setItemMeta(newLineMeta);
            inventory.setItem(12, newLine);

            int i = 0;
            for (ItemStack itemStack : inventory) {
                if (itemStack == null) {
                    inventory.setItem(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                }
                i++;
            }

            CommonVariables.upgradeMenu.put(senderPlayer.getName(), inventory);
            CommonVariables.playerShopUpgrade.put(sender.getName(), shop.getName());
            senderPlayer.openInventory(inventory);
            return true;
        }

        if (commandName.equals("coffer") && args.length == 2) {

            Shop shop = Shop.getShop(args[1]);

            if (shop == null) {
                sender.sendMessage(ChatColor.RED + "There is no shop with that name!");
                return false;
            }

            if (!shop.getOwner().equals(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "You are not the owner of this shop!");
                return false;
            }

            if (shop.getCoffer().size() == 0) {
                sender.sendMessage(ChatColor.RED + "You do not have anything in coffer!");
                return false;
            }

            int index = 0;

            for (ItemStack itemStack : shop.getCoffer().get(0)) {

                if (itemStack == null) {

                    shop.getCoffer().get(0).setItem(index, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                }

                index++;
            }

            Inventory inventory = shop.getCoffer().get(0);
            senderPlayer.openInventory(inventory);

            return true;
        }

        if (commandName.equals("infinite") && args.length == 4 && args[1].equals("sell") && args[2].equals("create")) {

            if (!senderPlayer.isOp()) {

                senderPlayer.sendMessage(ChatColor.RED + "You do not have permission to create infinite shop!");
                return false;
            }

            for (Shop shop : CommonVariables.infiniteShops) {

                if (shop.getName().equals(args[3])) {

                    senderPlayer.sendMessage(ChatColor.RED + "A shop with that name already exists!");
                    return false;
                }
            }

            for (Shop shop : CommonVariables.shops) {

                if (shop.getName().equals(args[3])) {

                    senderPlayer.sendMessage(ChatColor.RED + "A shop with that name already exists!");
                    return false;
                }
            }

            ItemStack shopIcon = new ItemStack(Material.BARRIER);
            ItemMeta shopIconMeta = shopIcon.getItemMeta();
            shopIconMeta.setDisplayName(args[3]);
            shopIcon.setItemMeta(shopIconMeta);

            Shop newShop = new Shop(args[3], senderPlayer.getName(), shopIcon, true, true);

            CommonVariables.infiniteShops.add(newShop);

            ArrayList<Inventory> shopPages = new ArrayList<>();
            Inventory shopPage = Bukkit.createInventory(null,
                    ConfigVariables.defaultInventorySize,
                    ChatColor.RED + newShop.getName() + " 1");

            shopPage.setItem(ConfigVariables.defaultPrevPageIndex, CommonVariables.prevPage);
            shopPage.setItem(ConfigVariables.defaultNextPageIndex, CommonVariables.nextPage);

            shopPages.add(shopPage);

            CommonVariables.infiniteShopsInventories.put(newShop.getName(), shopPages);

            senderPlayer.sendTitle(ChatColor.DARK_GREEN + newShop.getName(),
                    ChatColor.GOLD + "has been created");

            return true;
        }

        if (commandName.equals("infinite") && args.length == 4 && args[1].equals("buy") && args[2].equals("create")) {

            if (!senderPlayer.isOp()) {

                senderPlayer.sendMessage(ChatColor.RED + "You do not have permission to create infinite shop!");
                return false;
            }

            for (Shop shop : CommonVariables.infiniteShops) {

                if (shop.getName().equals(args[3])) {

                    senderPlayer.sendMessage(ChatColor.RED + "A shop with that name already exists!");
                    return false;
                }
            }

            for (Shop shop : CommonVariables.shops) {

                if (shop.getName().equals(args[3])) {

                    senderPlayer.sendMessage(ChatColor.RED + "A shop with that name already exists!");
                    return false;
                }
            }

            ItemStack shopIcon = new ItemStack(Material.BARRIER);
            ItemMeta shopIconMeta = shopIcon.getItemMeta();
            shopIconMeta.setDisplayName(args[3]);
            shopIcon.setItemMeta(shopIconMeta);

            Shop newShop = new Shop(args[3], senderPlayer.getName(), shopIcon, true, false);

            CommonVariables.infiniteShops.add(newShop);

            ArrayList<Inventory> shopPages = new ArrayList<>();
            Inventory shopPage = Bukkit.createInventory(null,
                    ConfigVariables.defaultInventorySize,
                    ChatColor.GREEN + newShop.getName() + " 1");

            shopPage.setItem(ConfigVariables.defaultPrevPageIndex, CommonVariables.prevPage);
            shopPage.setItem(ConfigVariables.defaultNextPageIndex, CommonVariables.nextPage);

            shopPages.add(shopPage);

            CommonVariables.infiniteShopsInventories.put(newShop.getName(), shopPages);

            senderPlayer.sendTitle(ChatColor.DARK_GREEN + newShop.getName(),
                    ChatColor.GOLD + "has been created");

            return true;
        }

        if (commandName.equals("infinite") && args.length == 3 && args[1].equals("sell") && args[2].equals("open")) {

            ArrayList<Inventory> shopsIcons = new ArrayList<>();
            ArrayList<Shop> infiniteSellShops = new ArrayList<>();

            int shopsAmount = 0;

            for (Shop shop : CommonVariables.infiniteShops) {

                if (shop.isSell()) {

                    infiniteSellShops.add(shop);
                    shopsAmount++;
                }
            }

            if (shopsAmount == 0) {

                sender.sendMessage(ChatColor.RED + "There are no shops yet");
                return false;
            }

            int pageNumber = 1;

            while (shopsAmount > 0) {

                Inventory page = Bukkit.createInventory(null, 54,
                        ChatColor.RED + "Infinite Sell Shops " + pageNumber);

                page.setItem(45, CommonVariables.prevPage);
                page.setItem(53, CommonVariables.nextPage);

                shopsIcons.add(page);
                shopsAmount -= 52;
                pageNumber++;
            }

            int stackIndex = 0;
            int inventoryIndex = 0;

            for (Shop shop : infiniteSellShops) {

                if (stackIndex == 45) {

                    stackIndex++;
                }
                if (stackIndex == 53) {

                    stackIndex = 0;
                    inventoryIndex++;
                }

                shopsIcons.get(inventoryIndex).setItem(stackIndex, shop.getIcon());
                stackIndex++;
            }

            stackIndex = 0;

            Inventory lastPage = shopsIcons.get(inventoryIndex);

            for (ItemStack itemStack : lastPage) {

                if (itemStack == null) {

                    lastPage.setItem(stackIndex, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                }
                stackIndex++;
            }

            shopsIcons.set(inventoryIndex, lastPage);
            CommonVariables.infiniteSellShopMenu = shopsIcons;
            senderPlayer.openInventory(shopsIcons.get(0));

            return true;
        }

        if (commandName.equals("infinite") && args.length == 3 && args[1].equals("buy") && args[2].equals("open")) {

            ArrayList<Inventory> shopsIcons = new ArrayList<>();
            ArrayList<Shop> infiniteBuyShops = new ArrayList<>();

            int shopsAmount = 0;

            for (Shop shop : CommonVariables.infiniteShops) {

                if (!shop.isSell()) {

                    infiniteBuyShops.add(shop);
                    shopsAmount++;
                }
            }

            if (shopsAmount == 0) {

                sender.sendMessage(ChatColor.RED + "There are no shops yet");
                return false;
            }

            int pageNumber = 1;

            while (shopsAmount > 0) {

                Inventory page = Bukkit.createInventory(null, 54,
                        ChatColor.GREEN + "Infinite Buy Shops " + pageNumber);

                page.setItem(45, CommonVariables.prevPage);
                page.setItem(53, CommonVariables.nextPage);

                shopsIcons.add(page);
                shopsAmount -= 52;
                pageNumber++;
            }

            int stackIndex = 0;
            int inventoryIndex = 0;

            for (Shop shop : infiniteBuyShops) {

                if (stackIndex == 45) {

                    stackIndex++;
                }
                if (stackIndex == 53) {

                    stackIndex = 0;
                    inventoryIndex++;
                }

                shopsIcons.get(inventoryIndex).setItem(stackIndex, shop.getIcon());
                stackIndex++;
            }

            stackIndex = 0;

            Inventory lastPage = shopsIcons.get(inventoryIndex);

            for (ItemStack itemStack : lastPage) {

                if (itemStack == null) {

                    lastPage.setItem(stackIndex, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                }
                stackIndex++;
            }

            shopsIcons.set(inventoryIndex, lastPage);
            CommonVariables.infiniteBuyShopMenu = shopsIcons;
            senderPlayer.openInventory(shopsIcons.get(0));

            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown command or wrong arguments!");
        return false;
    }
}
