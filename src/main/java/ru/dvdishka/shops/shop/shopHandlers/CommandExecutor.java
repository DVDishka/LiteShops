package ru.dvdishka.shops.shop.shopHandlers;

import com.destroystokyo.paper.Title;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import ru.dvdishka.shops.common.ConfigVariables;
import ru.dvdishka.shops.shop.Classes.Shop;
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
        Player player = Bukkit.getPlayer(sender.getName());
        String commandName = args[0];
        assert player != null;

        if (commandName.equals("create") && args.length == 2) {

            String shopName = args[1];

            for (Shop shop : CommonVariables.shops) {
                if (shop.getName().equals(shopName)) {
                    sender.sendMessage(ChatColor.RED + "Shop with that name already exists!");
                    return false;
                }
            }

            CommonVariables.playerShopCreating.put(player.getName(), shopName);

            TextComponent text = Component
                    .text("Creating a shop costs " + ConfigVariables.shopCost.getAmount() + " "
                            + ConfigVariables.shopCost.getType().name().toLowerCase() + "\n")
                    .append(Component.text(ChatColor.GREEN + "[CREATE]")
                    .clickEvent(ClickEvent.runCommand("/shop creating " + shopName + " " + player.getName())))
                    .append(Component.text("   "))
                    .append(Component.text(ChatColor.RED + "[CANCEL]")
                    .clickEvent(ClickEvent.runCommand("/shop creating cancel " + player.getName())));
            sender.sendMessage(text);
            return true;
        }

        if (commandName.equals("creating") && args.length == 3 && !args[1].equals("cancel")) {

            if (CommonVariables.playerShopCreating.get(args[2]) == null ||
                    !CommonVariables.playerShopCreating.get(args[2]).equals(args[1])) {
                return false;
            }

            player = Bukkit.getPlayer(args[2]);
            assert player != null;

            if (!Functions.removeItem(player, ConfigVariables.shopCost)) {
                player.sendMessage(ChatColor.RED + "You do not have " + ConfigVariables.shopCost.getAmount()
                        + " " + ConfigVariables.shopCost.getType().name().toLowerCase());
                return false;
            }

            for (Shop checkShop : CommonVariables.shops) {
                if (checkShop.getName().equals(args[1])) {
                    player.sendMessage(ChatColor.RED + "There is already a shop with the same name!");
                    return false;
                }
            }
            ItemStack icon = new ItemStack(Material.BARRIER);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(args[1]);
            icon.setItemMeta(iconMeta);
            Shop shop = new Shop(args[1], player.getName(), icon);
            CommonVariables.shops.add(shop);
            ArrayList<Inventory> pages = new ArrayList<>();
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta prevPageMeta = prevPage.getItemMeta();
            prevPageMeta.setDisplayName("<--");
            prevPage.setItemMeta(prevPageMeta);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName("-->");
            nextPage.setItemMeta(nextPageMeta);
            Inventory inventory = Bukkit.createInventory(null, ConfigVariables.defaultInventorySize,
                    ChatColor.GOLD + shop.getName() + " 1");
            inventory.setItem(ConfigVariables.prevPageIndex, prevPage);
            inventory.setItem(ConfigVariables.defaultNextPageIndex, nextPage);
            pages.add(inventory);
            CommonVariables.shopsInventories.put(shop.getName(), pages);
            CommonVariables.playerShopCreating.remove(player.getName());

            player.playSound(
                    net.kyori.adventure.sound.Sound.sound
                    (org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                            Sound.Source.NEUTRAL,
                            50,
                            1));

            player.sendTitle(Title
                    .builder()
                    .title(ChatColor.DARK_GREEN + shop.getName())
                    .subtitle(ChatColor.GOLD + "has been created")
                    .build());
            return true;
        }

        if (commandName.equals("creating") && args.length == 3 && args[1].equals("cancel")) {

            player = Bukkit.getPlayer(args[2]);
            if (!CommonVariables.playerShopCreating.containsKey(player.getName())) {
                return false;
            }
            CommonVariables.playerShopCreating.remove(player.getName());
            player.sendMessage(ChatColor.RED + "You cancelled the creation of the shop!");
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
            player.openInventory(shopsIcons.get(0));
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
            if (!shop.getOwner().equals(player.getName())) {
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

            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta prevPageMeta = prevPage.getItemMeta();
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            prevPageMeta.setDisplayName("<--");
            nextPageMeta.setDisplayName("-->");
            prevPage.setItemMeta(prevPageMeta);
            nextPage.setItemMeta(nextPageMeta);

            if (CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1) == null ||
                    CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getType()
                            .equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                    CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1)
                            .equals(prevPage) ||
                    CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).equals(nextPage)) {
                sender.sendMessage(ChatColor.RED + "There is no item in this slot!");
                return false;
            }
            ItemMeta meta = CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).getItemMeta();

            List<BaseComponent[]> list = new ArrayList<>();
            list.add(new ComponentBuilder("Price: ")
                    .color(net.md_5.bungee.api.ChatColor.GREEN)
                    .append(args[6])
                    .color(net.md_5.bungee.api.ChatColor.RED)
                    .append(" ")
                    .append(args[5].toUpperCase())
                    .color(net.md_5.bungee.api.ChatColor.RED)
                    .create());
            meta.setLoreComponents(list);

            CommonVariables.shopsInventories.get(shop.getName()).get(page - 1).getItem(index - 1).setItemMeta(meta);
            player.sendTitle(Title.builder().title(ChatColor.DARK_GREEN + shop.getName()).subtitle(ChatColor.GOLD +
                    "price has been set").build());
            return true;
        }

        if (commandName.equals("edit") && args.length == 4 && args[2].equals("name")) {

            String name = args[3];
            Shop shop = Shop.getShop(args[1]);
            if (shop == null) {
                sender.sendMessage(ChatColor.RED + "There is no shop with that name!");
                return false;
            }
            if (!shop.getOwner().equals(player.getName())) {
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
            player.sendTitle(Title.builder()
                    .title(ChatColor.DARK_GREEN + name)
                    .subtitle(ChatColor.GOLD + "Shop name has been set")
                    .build());
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
            player.openInventory(icons.get(0));
            CommonVariables.playerShopIconChoose.put(player.getName(), shop.getName());
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
            newPageMeta.setDisplayNameComponent(new ComponentBuilder("New Page")
                    .color(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE)
                    .create());
            List<BaseComponent[]> lore = new ArrayList<>();
            lore.add(new ComponentBuilder("Price: ")
                    .color(net.md_5.bungee.api.ChatColor.GREEN)
                    .append(ConfigVariables.newPageCost.getAmount() + " " +
                            ConfigVariables.newPageCost.getType().name())
                    .color(net.md_5.bungee.api.ChatColor.RED)
                    .create());
            lore.add(new ComponentBuilder("Progress: ")
                    .color(net.md_5.bungee.api.ChatColor.AQUA)
                    .append(String.valueOf(shop.getUpgrade().getPageProgress()))
                    .color(net.md_5.bungee.api.ChatColor.GOLD)
                    .create());
            newPageMeta.setLoreComponents(lore);
            newPage.setItemMeta(newPageMeta);
            inventory.setItem(13, newPage);

            ItemStack newLine = new ItemStack(Material.STICK);
            ItemMeta newLineMeta = newLine.getItemMeta();
            newLineMeta.setDisplayNameComponent(new ComponentBuilder("New Line")
                    .color(net.md_5.bungee.api.ChatColor.GREEN)
                    .create());
            List<BaseComponent[]> newLineLore = new ArrayList<>();
            newLineLore.add(new ComponentBuilder("Price: ")
                    .color(net.md_5.bungee.api.ChatColor.GREEN)
                    .append(ConfigVariables.newLineCost.getAmount() + " " +
                            ConfigVariables.newLineCost.getType().name())
                    .color(net.md_5.bungee.api.ChatColor.RED)
                    .create());
            newLineLore.add(new ComponentBuilder("Progress: ")
                    .color(net.md_5.bungee.api.ChatColor.AQUA)
                    .append(shop.getUpgrade().getLineProgress() + "/6")
                    .color(net.md_5.bungee.api.ChatColor.GOLD)
                    .create());
            newLineMeta.setLoreComponents(newLineLore);
            newLine.setItemMeta(newLineMeta);
            inventory.setItem(12, newLine);

            int i = 0;
            for (ItemStack itemStack : inventory) {
                if (itemStack == null) {
                    inventory.setItem(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                }
                i++;
            }

            CommonVariables.upgradeMenu.put(player.getName(), inventory);
            CommonVariables.playerShopUpgrade.put(sender.getName(), shop.getName());
            player.openInventory(inventory);
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
            if (shop.getCoffer().get(0) == null) {
                sender.sendMessage(ChatColor.RED + "You do not have anything in coffer!");
                return false;
            }
            Inventory inventory = shop.getCoffer().get(0);
            player.openInventory(inventory);
        }

        sender.sendMessage(ChatColor.RED + "Unknown command or wrong arguments!");
        return false;
    }
}
