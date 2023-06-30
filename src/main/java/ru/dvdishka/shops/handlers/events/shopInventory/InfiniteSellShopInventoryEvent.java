package ru.dvdishka.shops.handlers.events.shopInventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.common.ConfigVariables;
import ru.dvdishka.shops.common.Functions;
import ru.dvdishka.shops.Classes.Shop;

import java.util.List;

import static ru.dvdishka.shops.common.Functions.sendFailure;

public class InfiniteSellShopInventoryEvent implements Listener {

    @org.bukkit.event.EventHandler
    public void onInfiniteSellShopInventoryEvent(InventoryClickEvent event) {

        for (Shop shop : CommonVariables.infiniteShops) {

            int page = 0;

            for (Inventory inventory : CommonVariables.infiniteShopsInventories.get(shop.getName())) {

                if (!shop.getOwner().equals(event.getWhoClicked().getName()) &&
                        event.getView().getTopInventory().equals(inventory)) {

                    int i = 0;

                    if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                        event.setCancelled(true);
                        return;
                    }

                    for (ItemStack item : inventory) {

                        if (item == null) {

                            CommonVariables.infiniteShopsInventories.get(shop.getName()).get(page)
                                    .setItem(i, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                        }
                        i++;
                    }
                }
                page++;
            }
        }

        for (Shop shop : CommonVariables.infiniteShops) {

            int i = 0;

            for (Inventory inventory : CommonVariables.infiniteShopsInventories.get(shop.getName())) {

                if (inventory.equals(event.getView().getTopInventory()) && shop.isSell()) {

                    if (inventory.equals(event.getView().getTopInventory()) && event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                        event.setCancelled(true);
                        return;
                    }

                    int index = 0;

                    for (ItemStack itemStack : event.getView().getTopInventory()) {

                        if (!event.getWhoClicked().getName().equals(shop.getOwner())) {

                            if (itemStack == null) {

                                event.getView().getTopInventory().setItem(index,
                                        new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                            }
                        } else {

                            if (itemStack != null && itemStack.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE)) {

                                event.getView().getTopInventory().setItem(index, null);
                            }
                        }

                        index++;
                    }
                }

                if (inventory.equals(event.getClickedInventory()) && shop.isSell()) {

                    if (event.getCurrentItem() != null) {

                        Player player = (Player) event.getWhoClicked();

                        if (event.getCurrentItem().equals(CommonVariables.prevPage)) {

                            if (i > 0) {
                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                        50, 1);
                                event.getWhoClicked().openInventory(CommonVariables.infiniteShopsInventories
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

                        if (event.getCurrentItem().equals(CommonVariables.nextPage)) {

                            if (i < CommonVariables.infiniteShopsInventories.get(shop.getName()).size() - 1) {

                                player.playSound(player.getLocation(),
                                        org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                        50, 1);
                                event.getWhoClicked().openInventory(CommonVariables.infiniteShopsInventories
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

        for (Shop shop : CommonVariables.infiniteShops) {
            if (!event.getWhoClicked().getName().equals(shop.getOwner())) {
                for (Inventory inventory : CommonVariables.infiniteShopsInventories.get(shop.getName())) {
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

        for (Shop shop : CommonVariables.infiniteShops) {

            for (Inventory inventory : CommonVariables.infiniteShopsInventories.get(shop.getName())) {

                if (inventory == event.getClickedInventory() && shop.isSell()) {

                    if (event.getWhoClicked().getName().equals(shop.getOwner())) {

                        if (event.getCurrentItem() != null) {
                            ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
                            List<String> list = itemMeta.getLore();
                            if (list != null) {

                                try {

                                    if (Functions.removeChatColors(list.get(list.size() - 1)).startsWith("Sell for: ")) {

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

                                        String itemPriceLore = Functions.removeChatColors(itemMeta.getLore()
                                                .get(itemMeta.getLore().size() - 1));

                                        Material priceMaterial = Material.valueOf(Functions.splitBySpace(itemPriceLore)
                                                .get(3).toUpperCase());

                                        price = Integer.parseInt(Functions.splitBySpace(itemPriceLore).get(2));

                                        ItemStack priceItem = new ItemStack(priceMaterial, price);

                                        if (!Functions.hasItem((Player) event.getWhoClicked(), event.getCurrentItem())) {

                                            sendFailure((Player) event.getWhoClicked(), "You do not have "
                                                    + event.getCurrentItem().getAmount() + " " +
                                                    event.getCurrentItem().getType().name());

                                            player.playSound(player.getLocation(),
                                                    org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                                    50, 1);

                                            event.setCancelled(true);
                                            return;

                                        } else {

                                            boolean canAddItem = false;

                                            for (ItemStack item : player.getInventory().getStorageContents()) {

                                                if (item == null || item.getType() == priceMaterial && item.getAmount()
                                                        + price <= priceMaterial.getMaxStackSize()) {

                                                    canAddItem = true;
                                                }
                                            }

                                            if (canAddItem) {

                                                Functions.removeItem((Player) event.getWhoClicked(), event.getCurrentItem());

                                                player.getInventory().addItem(priceItem);

                                            } else {

                                                player.playSound(player.getLocation(),
                                                        org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                                        50, 1);

                                                event.setCancelled(true);
                                                return;
                                            }

                                            int cofferIndex = 0;

                                            boolean flag = false;
                                            for (Inventory coffer : shop.getCoffer()) {
                                                int index = 0;
                                                for (ItemStack item : coffer) {
                                                    if (item == null) {
                                                        shop.getCoffer().get(cofferIndex).setItem
                                                                (index, event.getCurrentItem());
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
                                                        ChatColor.RED + (ChatColor.BOLD + shop.getName() + " coffer: ") +
                                                                ChatColor.RESET + shop.getCoffer().size() + 1);

                                                newCoffer.setItem(ConfigVariables.defaultPrevPageIndex, CommonVariables.prevPage);
                                                newCoffer.setItem(ConfigVariables.defaultNextPageIndex, CommonVariables.nextPage);
                                                newCoffer.setItem(0, priceItem);

                                                shop.addCoffer(newCoffer);
                                            }

                                            event.setCancelled(true);
                                        }

                                        player.playSound(player.getLocation(),
                                                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                                                50, 1);

                                    } catch (Exception e) {

                                        sendFailure((Player) event.getWhoClicked(), "Something went wrong!");
                                        player.playSound(player.getLocation(),
                                                org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                                50, 1);
                                        event.setCancelled(true);
                                        return;
                                    }
                                } else {

                                    sendFailure((Player) event.getWhoClicked(), "This item has no price!");
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
}
