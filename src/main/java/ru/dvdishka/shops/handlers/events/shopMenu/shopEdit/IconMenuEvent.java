package ru.dvdishka.shops.handlers.events.shopMenu.shopEdit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.Classes.Shop;

public class IconMenuEvent implements Listener {

    @org.bukkit.event.EventHandler
    public void onIconMenuEvent(InventoryClickEvent event) {

        int i = 0;

        for (Inventory iconMenuPage : CommonVariables.iconMenu) {

            if (iconMenuPage.equals(event.getClickedInventory())) {

                Player player = (Player) event.getWhoClicked();

                if (event.getCurrentItem() != null) {

                    if (event.getCurrentItem().equals(CommonVariables.prevPage)) {
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

                    if (event.getCurrentItem().equals(CommonVariables.nextPage)) {
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
}
