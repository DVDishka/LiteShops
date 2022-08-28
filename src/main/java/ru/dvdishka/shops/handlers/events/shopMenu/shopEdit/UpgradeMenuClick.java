package ru.dvdishka.shops.handlers.events.shopMenu.shopEdit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.common.ConfigVariables;
import ru.dvdishka.shops.common.Functions;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.Classes.Upgrade;

import java.util.ArrayList;
import java.util.List;

public class UpgradeMenuClick implements Listener {

    @org.bukkit.event.EventHandler
    public void onUpgradeMenuClick(InventoryClickEvent event) {

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(CommonVariables.upgradeMenu.
                get(event.getWhoClicked().getName()))) {

            if (event.getCurrentItem() != null) {

                Player player = (Player) event.getWhoClicked();

                event.setCancelled(true);

                Shop shop = Shop.getShop(CommonVariables.playerShopUpgrade.get(event.getWhoClicked().getName()));

                boolean isInfinite = CommonVariables.infiniteShops.contains(shop);

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

                        Inventory inventory;

                        if (!isInfinite) {

                            inventory = Bukkit.createInventory(null, CommonVariables.shopsInventories.get(shop.getName()).get(0).getSize(),
                                    ChatColor.GOLD + shop.getName() + " " +
                                            (CommonVariables.shopsInventories.get(shop.getName()).size() + 1));
                        } else {

                            if (shop.isSell()) {

                                inventory = Bukkit.createInventory(null, CommonVariables.infiniteShopsInventories.get(shop.getName()).get(0).getSize(),
                                        ChatColor.GREEN + shop.getName() + " " +
                                                (CommonVariables.infiniteShopsInventories.get(shop.getName()).size() + 1));

                            } else {

                                inventory = Bukkit.createInventory(null, CommonVariables.infiniteShopsInventories.get(shop.getName()).get(0).getSize(),
                                        ChatColor.GREEN + shop.getName() + " " +
                                                (CommonVariables.infiniteShopsInventories.get(shop.getName()).size() + 1));
                            }
                        }

                        int nextPageIndex = inventory.getSize() - 1;
                        int prevPageIndex = 0;
                        if (inventory.getSize() % 9 == 0) {
                            prevPageIndex = nextPageIndex - 8;
                        } else {
                            prevPageIndex = (inventory.getSize()/ 9) * 9;
                        }

                        inventory.setItem(prevPageIndex, CommonVariables.prevPage);
                        inventory.setItem(nextPageIndex, CommonVariables.nextPage);

                        if (!isInfinite) {

                            CommonVariables.shopsInventories.get(shop.getName()).add(inventory);
                        } else {

                            CommonVariables.infiniteShopsInventories.get(shop.getName()).add(inventory);
                        }

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

                        ArrayList<Inventory> inventories = new ArrayList<>();

                        int newInventorySize;

                        if (!isInfinite) {

                            newInventorySize = CommonVariables.shopsInventories
                                    .get(shop.getName()).get(0).getSize() + 9;
                        } else {

                            newInventorySize = CommonVariables.infiniteShopsInventories
                                    .get(shop.getName()).get(0).getSize() + 9;
                        }

                        int i = 0;

                        if (!isInfinite) {

                            for (Inventory inventory : CommonVariables.shopsInventories.get(shop.getName())) {

                                int j = 0;

                                Inventory newInventory = Bukkit.createInventory(null, CommonVariables.shopsInventories
                                                .get(shop.getName()).get(0).getSize() + 9,
                                        ChatColor.GOLD + shop.getName() + " " + (i + 1));
                                for (ItemStack itemStack : inventory) {
                                    if (itemStack == null || !itemStack.equals(CommonVariables.prevPage) && !itemStack.equals(CommonVariables.nextPage)) {
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
                                newInventory.setItem(prevPageIndex, CommonVariables.prevPage);
                                newInventory.setItem(nextPageIndex, CommonVariables.nextPage);
                                inventories.add(newInventory);
                                CommonVariables.shopsInventories.get(shop.getName()).set(i, inventory);
                                i++;
                            }

                        } else {

                            for (Inventory inventory : CommonVariables.infiniteShopsInventories.get(shop.getName())) {

                                int j = 0;

                                Inventory newInventory;

                                if (!shop.isSell()) {

                                    newInventory = Bukkit.createInventory(null, CommonVariables.infiniteShopsInventories
                                                    .get(shop.getName()).get(0).getSize() + 9,
                                            ChatColor.GREEN + shop.getName() + " " + (i + 1));

                                } else {

                                    newInventory = Bukkit.createInventory(null, CommonVariables.infiniteShopsInventories
                                                    .get(shop.getName()).get(0).getSize() + 9,
                                            ChatColor.RED + shop.getName() + " " + (i + 1));
                                }
                                for (ItemStack itemStack : inventory) {
                                    if (itemStack == null || !itemStack.equals(CommonVariables.prevPage) && !itemStack.equals(CommonVariables.nextPage)) {
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
                                newInventory.setItem(prevPageIndex, CommonVariables.prevPage);
                                newInventory.setItem(nextPageIndex, CommonVariables.nextPage);
                                inventories.add(newInventory);
                                CommonVariables.infiniteShopsInventories.get(shop.getName()).set(i, inventory);
                                i++;
                            }
                        }

                        shop.setItems(inventories);

                        if (!isInfinite) {

                            CommonVariables.shopsInventories.remove(shop.getName());
                            CommonVariables.shopsInventories.put(shop.getName(), inventories);
                        } else {

                            CommonVariables.infiniteShopsInventories.remove(shop.getName());
                            CommonVariables.infiniteShopsInventories.put(shop.getName(), inventories);
                        }

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
}
