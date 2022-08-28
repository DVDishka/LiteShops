package ru.dvdishka.shops.shop.shopHandlers;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import ru.dvdishka.shops.common.ConfigVariables;
import ru.dvdishka.shops.shop.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import ru.dvdishka.shops.common.Functions;
import ru.dvdishka.shops.shop.Classes.Upgrade;

import java.util.ArrayList;
import java.util.List;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onShopInventoryClick(InventoryClickEvent event) {

        for (Shop shop : CommonVariables.shops) {
            int page = 0;
            for (Inventory inventory : CommonVariables.shopsInventories.get(shop.getName()))
            {
                if (!shop.getOwner().equals(event.getWhoClicked().getName()) &&
                        event.getView().getTopInventory().equals(inventory)) {
                    int i = 0;
                    for (ItemStack item : inventory) {
                        if (item == null) {
                            CommonVariables.shopsInventories.get(shop.getName()).get(page)
                                    .setItem(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                        }
                        i++;
                    }
                }
                page++;
            }
        }

        for (Shop shop : CommonVariables.shops) {
            int i = 0;
            for (Inventory inventory : CommonVariables.shopsInventories.get(shop.getName())) {
                if (inventory.equals(event.getClickedInventory())) {
                    if (event.getCurrentItem() != null) {

                        Player player = (Player) event.getWhoClicked();

                        ItemStack prevPage = new ItemStack(Material.ARROW);
                        ItemStack nextPage = new ItemStack(Material.ARROW);
                        ItemMeta prevPageMeta = prevPage.getItemMeta();
                        prevPageMeta.setDisplayName("<--");
                        prevPage.setItemMeta(prevPageMeta);
                        ItemMeta nextPageMeta = nextPage.getItemMeta();
                        nextPageMeta.setDisplayName("-->");
                        nextPage.setItemMeta(nextPageMeta);

                        if (event.getCurrentItem().equals(prevPage)) {

                            if (i > 0) {
                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                        50, 1);
                                event.getWhoClicked().openInventory(CommonVariables.shopsInventories
                                        .get(shop.getName()).get(i - 1));
                                event.setCancelled(true);
                                return;
                            } else {
                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                        50, 1);
                            }
                            event.setCancelled(true);
                            return;
                        }

                        if (event.getCurrentItem().equals(nextPage)) {

                            if (i < CommonVariables.shopsInventories.get(shop.getName()).size() - 1) {
                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                        50, 1);
                                event.getWhoClicked().openInventory(CommonVariables.shopsInventories
                                        .get(shop.getName()).get(i + 1));
                                event.setCancelled(true);
                                return;
                            } else {
                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                        50, 1);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                i++;
            }
        }

        boolean firstFlag = false;
        boolean secondFlag = false;

        for (Shop shop : CommonVariables.shops) {
            if (!event.getWhoClicked().getName().equals(shop.getOwner())) {
                for (Inventory inventory : CommonVariables.shopsInventories.get(shop.getName())) {
                    if (inventory == event.getClickedInventory()) {
                        firstFlag = true;
                    }
                    if (event.getView().getTopInventory().equals(inventory)) {
                        secondFlag = true;
                    }
                }
            }
        }
        if (!firstFlag && secondFlag) {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

                Player player = (Player) event.getWhoClicked();

                player.playSound(player.getLocation(),
                        org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                        50, 1);
                event.setCancelled(true);
                return;
            }
        }

        for (Shop shop : CommonVariables.shops) {

            for (Inventory inventory : CommonVariables.shopsInventories.get(shop.getName())) {

                if (inventory == event.getClickedInventory()) {

                    if (event.getWhoClicked().getName().equals(shop.getOwner())) {
                        if (event.getCurrentItem() != null) {
                            ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
                            List<String> list = itemMeta.getLore();
                            if (list != null) {
                                try {
                                    if (list.get(list.size() - 1).startsWith("Price: ")) {
                                        list.remove(list.size() - 1);
                                        if (list.size() > 0) {
                                            itemMeta.setLore(list);
                                        } else {
                                            itemMeta.setLore(null);
                                        }
                                        event.getCurrentItem().setItemMeta(itemMeta);
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }

                    if (!event.getWhoClicked().getName().equals(shop.getOwner())) {

                        Player player = (Player) event.getWhoClicked();

                        if (event.getCursor() != null && !event.getCursor().getType().equals(Material.AIR)) {
                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                    50, 1);
                            event.setCancelled(true);
                            return;
                        }

                        if (event.getCurrentItem() != null) {
                            if (event.getCurrentItem().getType() != Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                                if (event.getCurrentItem().getItemMeta().getLore() != null && event.getCurrentItem().getItemMeta().getLore().size() > 0) {

                                    int price = 0;
                                    try {

                                        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

                                        CommonVariables.logger.warning("1");

                                        Material priceMaterial = Material.valueOf(Functions.splitBySpace(itemMeta.getLore()
                                                .get(itemMeta.getLore().size() - 1)).get(2).toUpperCase());

                                        CommonVariables.logger.warning("1");

                                        price = Integer.parseInt(Functions.splitBySpace(itemMeta.getLore()
                                                .get(itemMeta.getLore().size() - 1)).get(1));

                                        CommonVariables.logger.warning("1");

                                        if (!Functions.removeItem((Player) event.getWhoClicked(), new ItemStack(priceMaterial, price))) {

                                            event.getWhoClicked().sendMessage(ChatColor.RED + "You do not have "
                                                    + price + " " + priceMaterial.name());
                                            player.playSound(player.getLocation(),
                                                    org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                                    50, 1);
                                            event.setCancelled(true);
                                            return;
                                        } else {
                                            int cofferIndex = 0;
                                            boolean flag = false;
                                            for (Inventory coffer : shop.getCoffer()) {
                                                int index = 0;
                                                for (ItemStack item : coffer) {
                                                    if (item == null) {
                                                        shop.getCoffer().get(cofferIndex).setItem
                                                                (index, new ItemStack(priceMaterial, price));
                                                        flag = true;
                                                        break;
                                                    }
                                                    index++;
                                                }
                                                if (flag) {
                                                    break;
                                                }
                                                cofferIndex++;
                                            }
                                            if (!flag) {

                                                Inventory newCoffer = Bukkit.createInventory(null,
                                                        ConfigVariables.defaultInventorySize,
                                                        ChatColor.GOLD + shop.getName() + " coffer " +
                                                                shop.getCoffer().size() + 1);
                                                ItemStack prevPage = new ItemStack(Material.ARROW);
                                                ItemStack nextPage = new ItemStack(Material.ARROW);
                                                ItemMeta prevPageMeta = prevPage.getItemMeta();
                                                ItemMeta nextPageMeta = nextPage.getItemMeta();
                                                prevPageMeta.setDisplayName("<--");
                                                nextPageMeta.setDisplayName("-->");
                                                prevPage.setItemMeta(prevPageMeta);
                                                nextPage.setItemMeta(nextPageMeta);
                                                newCoffer.setItem(ConfigVariables.prevPageIndex, prevPage);
                                                newCoffer.setItem(ConfigVariables.defaultNextPageIndex, nextPage);
                                                newCoffer.setItem(0, new ItemStack(priceMaterial, price));

                                                shop.addCoffer(newCoffer);
                                            }
                                        }


                                        List<String> list = itemMeta.getLore();
                                        list.remove(list.size() - 1);
                                        if (list.size() > 0) {
                                            itemMeta.setLore(list);
                                        } else {
                                            itemMeta.setLore(null);
                                        }
                                        event.getCurrentItem().setItemMeta(itemMeta);


                                        player.playSound(player.getLocation(),
                                                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                                                50, 1);

                                    } catch (Exception e) {
                                        event.getWhoClicked().sendMessage(ChatColor.RED + "Something went wrong!");
                                        player.playSound(player.getLocation(),
                                                org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                                50, 1);
                                        event.setCancelled(true);
                                        return;
                                    }
                                } else {
                                    event.getWhoClicked().sendMessage(ChatColor.RED + "This item has no price!");
                                    player.playSound(player.getLocation(),
                                            org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                            50, 1);
                                    event.setCancelled(true);
                                    return;
                                }
                            } else {
                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                        50, 1);
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onShopMenuInventoryClick(InventoryClickEvent event) {

        int i = 0;

        for (Inventory shopMenuPage : CommonVariables.shopMenu) {

            if (shopMenuPage.equals(event.getClickedInventory())) {

                Player player = (Player) event.getWhoClicked();

                ItemStack prevPage = new ItemStack(Material.ARROW);
                ItemStack nextPage = new ItemStack(Material.ARROW);
                ItemMeta prevPageMeta = prevPage.getItemMeta();
                ItemMeta nextPageMeta = nextPage.getItemMeta();
                prevPageMeta.setDisplayName("<--");
                nextPageMeta.setDisplayName("-->");
                prevPage.setItemMeta(prevPageMeta);
                nextPage.setItemMeta(nextPageMeta);

                if (event.getCurrentItem() != null) {

                    if (event.getCurrentItem().equals(prevPage)) {
                        if (i > 0) {
                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                    50, 1);
                            event.getWhoClicked().openInventory(CommonVariables.shopMenu.get(i - 1));
                        } else {
                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                    50, 1);
                        }
                        event.setCancelled(true);
                        return;
                    }

                    if (event.getCurrentItem().equals(nextPage)) {

                        if (i < CommonVariables.shopMenu.size() - 1) {

                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                    50, 1);
                            event.getWhoClicked().openInventory(CommonVariables.shopMenu.get(i + 1));
                        } else {

                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                    50, 1);
                        }
                        event.setCancelled(true);
                        return;
                    }

                    if (event.getCurrentItem().equals(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE))) {
                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                50, 1);
                        event.setCancelled(true);
                        return;
                    }

                    ItemMeta currentItemMeta = event.getCurrentItem().getItemMeta();
                    player.playSound(player.getLocation(),
                            org.bukkit.Sound.UI_BUTTON_CLICK,
                            50, 1);
                    if (!event.getWhoClicked().getName().equals(Shop.getShop(currentItemMeta.getDisplayName()).getOwner())) {
                        int pageIndex = 0;
                        for (Inventory inventory : CommonVariables.shopsInventories.get(currentItemMeta.getDisplayName())) {
                            int index = 0;
                            for (ItemStack item : inventory) {
                                if (item == null) {
                                    CommonVariables.shopsInventories.get(currentItemMeta.getDisplayName()).get(pageIndex)
                                            .setItem(index, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                                }
                                index++;
                            }
                            pageIndex++;
                        }
                    } else {
                        int pageIndex = 0;
                        for (Inventory inventory : CommonVariables.shopsInventories.get(currentItemMeta.getDisplayName())) {
                            int index = 0;
                            for (ItemStack item : inventory) {
                                if (item != null && item.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {
                                    CommonVariables.shopsInventories.get(currentItemMeta.getDisplayName()).get(pageIndex)
                                            .setItem(index, null);
                                }
                                index++;
                            }
                            pageIndex++;
                        }
                    }
                    event.getWhoClicked().openInventory(CommonVariables.shopsInventories
                            .get(currentItemMeta.getDisplayName()).get(0));
                    event.setCancelled(true);
                    return;
                }
                return;
            }
            i++;
        }
    }

    @org.bukkit.event.EventHandler
    public void onIconMenuInventoryClick(InventoryClickEvent event) {

        int i = 0;

        for (Inventory iconMenuPage : CommonVariables.iconMenu) {

            if (iconMenuPage.equals(event.getClickedInventory())) {

                Player player = (Player) event.getWhoClicked();

                ItemStack prevPage = new ItemStack(Material.ARROW);
                ItemStack nextPage = new ItemStack(Material.ARROW);
                ItemMeta prevPageMeta = prevPage.getItemMeta();
                ItemMeta nextPageMeta = nextPage.getItemMeta();
                prevPageMeta.setDisplayName("<--");
                nextPageMeta.setDisplayName("-->");
                prevPage.setItemMeta(prevPageMeta);
                nextPage.setItemMeta(nextPageMeta);

                if (event.getCurrentItem() != null) {

                    if (event.getCurrentItem().equals(prevPage)) {
                        if (i > 0) {
                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                    50, 1);
                            event.getWhoClicked().openInventory(CommonVariables.iconMenu.get(i - 1));
                        } else {
                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                    50, 1);
                        }
                        event.setCancelled(true);
                        return;
                    }

                    if (event.getCurrentItem().equals(nextPage)) {
                        if (i < CommonVariables.iconMenu.size() - 1) {
                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                    50, 1);
                            event.getWhoClicked().openInventory(CommonVariables.iconMenu.get(i + 1));
                        } else {
                            player.playSound(player.getLocation(),
                                    org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                    50, 1);
                        }
                        event.setCancelled(true);
                        return;
                    }

                    ItemStack icon = new ItemStack(event.getCurrentItem().getType());
                    if (!icon.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {
                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                                50, 1);
                        ItemMeta iconMeta = icon.getItemMeta();
                        iconMeta.setDisplayName(CommonVariables.playerShopIconChoose.get(event.getWhoClicked().getName()));
                        icon.setItemMeta(iconMeta);
                        Shop shop = Shop.getShop(CommonVariables.playerShopIconChoose.get(event.getWhoClicked().getName()));
                        shop.setIcon(icon);
                        player.sendTitle(ChatColor.DARK_GREEN + shop.getName(),
                                ChatColor.GOLD + "New icon has been set");
                        player.closeInventory();
                    } else {
                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                50, 1);
                    }
                    event.setCancelled(true);
                    return;
                }
            }
            i++;
        }
    }

    @org.bukkit.event.EventHandler
    public void onUpgradeMenuClick(InventoryClickEvent event) {

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(CommonVariables.upgradeMenu.
                get(event.getWhoClicked().getName()))) {
            if (event.getCurrentItem() != null) {

                Player player = (Player) event.getWhoClicked();

                event.setCancelled(true);

                Shop shop = Shop.getShop(CommonVariables.playerShopUpgrade.get(event.getWhoClicked().getName()));

                ItemStack newPage = new ItemStack(Material.PAPER);
                ItemMeta newPageMeta = newPage.getItemMeta();
                newPageMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "New Page");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ConfigVariables.newPageCost.getAmount() + " " +
                        ConfigVariables.newPageCost.getType().name());
                lore.add(ChatColor.AQUA + "Progress: " + ChatColor.GOLD + shop.getUpgrade().getPageProgress());
                newPageMeta.setLore(lore);
                newPage.setItemMeta(newPageMeta);

                ItemStack newLine = new ItemStack(Material.STICK);
                ItemMeta newLineMeta = newLine.getItemMeta();
                newLineMeta.setDisplayName(ChatColor.GREEN + "New Line");
                List<String> newLineLore = new ArrayList<>();
                newLineLore.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ConfigVariables.newLineCost.getAmount() + " " +
                        ConfigVariables.newLineCost.getType().name());
                newLineLore.add(ChatColor.AQUA + "Progress: " + ChatColor.GOLD + shop.getUpgrade().getLineProgress() + "/6");
                newLineMeta.setLore(newLineLore);
                newLine.setItemMeta(newLineMeta);

                if (event.getCurrentItem().equals(newPage)) {

                    if (Functions.removeItem((Player) event.getWhoClicked(), ConfigVariables.newPageCost)) {

                        shop.setUpgrade(new Upgrade(shop.getUpgrade().getPageProgress() + 1,
                                shop.getUpgrade().getLineProgress()));

                        lore.clear();
                        newPageMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "New Page");
                        lore.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ConfigVariables.newPageCost.getAmount() + " " +
                                ConfigVariables.newPageCost.getType().name());
                        lore.add(ChatColor.AQUA + "Progress: " + ChatColor.GOLD + shop.getUpgrade().getPageProgress());
                        newPageMeta.setLore(lore);
                        newPage.setItemMeta(newPageMeta);

                        event.getCurrentItem().setItemMeta(newPageMeta);

                        ItemStack prevPage = new ItemStack(Material.ARROW);
                        ItemStack nextPage = new ItemStack(Material.ARROW);
                        ItemMeta prevPageMeta = prevPage.getItemMeta();
                        prevPageMeta.setDisplayName("<--");
                        prevPage.setItemMeta(prevPageMeta);
                        ItemMeta nextPageMeta = nextPage.getItemMeta();
                        nextPageMeta.setDisplayName("-->");
                        nextPage.setItemMeta(nextPageMeta);
                        Inventory inventory = Bukkit.createInventory(null, CommonVariables.shopsInventories.get(shop.getName()).get(0).getSize(),
                                ChatColor.GOLD + shop.getName() + " " +
                                        (CommonVariables.shopsInventories.get(shop.getName()).size() + 1));
                        inventory.setItem(ConfigVariables.prevPageIndex, prevPage);
                        inventory.setItem(ConfigVariables.defaultNextPageIndex, nextPage);
                        CommonVariables.shopsInventories.get(shop.getName()).add(inventory);
                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                                50, 1);
                    } else {

                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                50, 1);
                    }
                    event.setCancelled(true);
                }

                if (event.getCurrentItem().equals(newLine)) {

                    if (shop.getUpgrade().getLineProgress() >= 6) {

                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                50, 1);
                        event.setCancelled(true);
                        return;
                    }

                    if (Functions.removeItem((Player) event.getWhoClicked(), ConfigVariables.newLineCost)) {

                        shop.setUpgrade(new Upgrade(shop.getUpgrade().getPageProgress(),
                                shop.getUpgrade().getLineProgress() + 1));

                        newLineLore.clear();

                        newLineMeta.setDisplayName(ChatColor.GREEN + "New Line");
                        newLineLore.add(ChatColor.GREEN + "Price: " + ChatColor.RED + ConfigVariables.newLineCost.getAmount() + " " +
                                ConfigVariables.newLineCost.getType().name());
                        newLineLore.add(ChatColor.AQUA + "Progress: " + ChatColor.GOLD + shop.getUpgrade().getLineProgress() + "/6");
                        newLineMeta.setLore(newLineLore);
                        newLine.setItemMeta(newLineMeta);

                        event.getCurrentItem().setItemMeta(newLineMeta);

                        ItemStack prevPage = new ItemStack(Material.ARROW);
                        ItemStack nextPage = new ItemStack(Material.ARROW);
                        ItemMeta prevPageMeta = prevPage.getItemMeta();
                        prevPageMeta.setDisplayName("<--");
                        prevPage.setItemMeta(prevPageMeta);
                        ItemMeta nextPageMeta = nextPage.getItemMeta();
                        nextPageMeta.setDisplayName("-->");
                        nextPage.setItemMeta(nextPageMeta);

                        ArrayList<Inventory> inventories = new ArrayList<>();

                        int newInventorySize = CommonVariables.shopsInventories
                                .get(shop.getName()).get(0).getSize() + 9;

                        int i = 0;
                        for (Inventory inventory : CommonVariables.shopsInventories.get(shop.getName())) {
                            int j = 0;
                            Inventory newInventory = Bukkit.createInventory(null, CommonVariables.shopsInventories
                                    .get(shop.getName()).get(0).getSize() + 9,
                                    ChatColor.GOLD + shop.getName() + " " + (i + 1));
                            for (ItemStack itemStack : inventory) {
                                if (itemStack == null || !itemStack.equals(prevPage) && !itemStack.equals(nextPage)) {
                                    newInventory.setItem(j, itemStack);
                                }
                                j++;

                            }
                            int nextPageIndex = newInventorySize - 1;
                            int prevPageIndex = 0;
                            if (newInventorySize % 9 == 0) {
                                prevPageIndex = nextPageIndex - 8;
                            } else {
                                prevPageIndex = (newInventorySize / 9) * 9;
                            }
                            newInventory.setItem(prevPageIndex, prevPage);
                            newInventory.setItem(nextPageIndex, nextPage);
                            inventories.add(newInventory);
                            CommonVariables.shopsInventories.get(shop.getName()).set(i, inventory);
                            i++;
                        }

                        shop.setItems(inventories);
                        CommonVariables.shopsInventories.remove(shop.getName());
                        CommonVariables.shopsInventories.put(shop.getName(), inventories);

                        event.setCancelled(true);

                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                                50, 1);
                    } else {

                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                50, 1);
                        event.setCancelled(true);
                        return;
                    }
                }

                if (event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {

                    player.playSound(player.getLocation(),
                            org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                            50, 1);
                }
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onCofferInventoryClick(InventoryClickEvent event) {

        for (Shop shop : CommonVariables.shops) {

            int i = 0;

            for (Inventory coffer : shop.getCoffer()) {
                if (event.getClickedInventory() != null && event.getClickedInventory().equals(coffer)) {

                    if (event.getCurrentItem() != null) {

                        ItemStack prevPage = new ItemStack(Material.ARROW);
                        ItemStack nextPage = new ItemStack(Material.ARROW);
                        ItemMeta prevPageMeta = prevPage.getItemMeta();
                        prevPageMeta.setDisplayName("<--");
                        prevPage.setItemMeta(prevPageMeta);
                        ItemMeta nextPageMeta = nextPage.getItemMeta();
                        nextPageMeta.setDisplayName("-->");
                        nextPage.setItemMeta(nextPageMeta);

                        if (event.getCurrentItem().equals(prevPage)) {

                            Player player = (Player) event.getWhoClicked();

                            if (i > 0) {

                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                        50, 1);
                                player.openInventory(shop.getCoffer().get(i - 1));
                            } else {

                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                        50, 1);
                            }
                            event.setCancelled(true);
                        }

                        if (event.getCurrentItem().equals(nextPage)) {

                            Player player = (Player) event.getWhoClicked();

                            if (i < shop.getCoffer().size() - 1) {

                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                        50, 1);
                                player.openInventory(shop.getCoffer().get(i + 1));
                            } else {

                                player.playSound(player.getLocation(),
                                        Sound.BLOCK_ANVIL_PLACE,
                                        50, 1);
                            }
                            event.setCancelled(true);
                        }
                    }
                }
                i++;
            }
        }
    }
}
