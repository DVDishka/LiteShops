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
import ru.dvdishka.shops.common.Functions;

import java.util.Arrays;

import static ru.dvdishka.shops.common.Functions.sendSuccess;

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
                        String shopName = CommonVariables.playerShopIconChoose.get(event.getWhoClicked().getName());

                        Shop shop = Shop.getShop(CommonVariables.playerShopIconChoose.get(event.getWhoClicked().getName()));
                        ChatColor shopColor = shop.getColor();

                        iconMeta.setDisplayName(shopColor + (ChatColor.BOLD + shopName));
                        iconMeta.setLore(Arrays.asList(ChatColor.GREEN + "[Click to open]"));
                        icon.setItemMeta(iconMeta);

                        shop.setIcon(icon);
                        sendSuccess(player, "New icon has been set to " + shopName);
                        player.closeInventory();

                        if (!shop.isInfinite()) {

                            for (Inventory shopMenuPage : CommonVariables.shopMenu) {

                                for (ItemStack shopIcon : shopMenuPage) {

                                    if (shopIcon != null) {

                                        ItemMeta shopIconMeta = shopIcon.getItemMeta();

                                        if (shopIconMeta != null) {

                                            shopName = Functions.removeChatColors(shopIconMeta.getDisplayName());

                                            if (shopName.equals(shop.getName())) {

                                                shopIcon.setType(icon.getType());
                                            }
                                        }
                                    }
                                }
                            }
                        } else {

                            if (shop.isSell()) {

                                for (Inventory shopMenuPage : CommonVariables.infiniteSellShopMenu) {

                                    for (ItemStack shopIcon : shopMenuPage) {

                                        if (shopIcon != null) {

                                            ItemMeta shopIconMeta = shopIcon.getItemMeta();

                                            if (shopIconMeta != null) {

                                                shopName = Functions.removeChatColors(shopIconMeta.getDisplayName());

                                                if (shopName.equals(shop.getName())) {

                                                    shopIcon.setType(icon.getType());
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {

                                for (Inventory shopMenuPage : CommonVariables.infiniteBuyShopMenu) {

                                    for (ItemStack shopIcon : shopMenuPage) {

                                        if (shopIcon != null) {

                                            ItemMeta shopIconMeta = shopIcon.getItemMeta();

                                            if (shopIconMeta != null) {

                                                shopName = Functions.removeChatColors(shopIconMeta.getDisplayName());

                                                if (shopName.equals(shop.getName())) {

                                                    shopIcon.setType(icon.getType());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
