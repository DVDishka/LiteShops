package ru.dvdishka.shops.handlers.events.shopCoffer;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;

public class InfiniteShopCofferEvent implements Listener {

    @org.bukkit.event.EventHandler
    public void onInfiniteShopCofferEvent(InventoryClickEvent event) {

        for (Shop shop : CommonVariables.infiniteShops) {

            int i = 0;

            for (Inventory coffer : shop.getCoffer()) {

                if (event.getClickedInventory() != null && event.getView().getTopInventory().equals(coffer)) {

                    if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {

                        event.setCancelled(true);
                        return;
                    }

                    int index = 0;

                    for (ItemStack itemStack : event.getView().getTopInventory()) {

                        if (itemStack == null) {

                            event.getView().getTopInventory().setItem(index,
                                    new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                        }

                        index++;
                    }

                    if (event.getClickedInventory().equals(coffer) && event.getCurrentItem() != null) {

                        Player senderPlayer = (Player) event.getWhoClicked();

                        if (event.getCurrentItem().equals(CommonVariables.prevPage)) {

                            if (i > 0) {

                                senderPlayer.playSound(senderPlayer.getLocation(),
                                        org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                        50, 1);

                                index = 0;

                                for (ItemStack itemStack : shop.getCoffer().get(i - 1)) {

                                    if (itemStack == null) {

                                        shop.getCoffer().get(i - 1).setItem(index,
                                                new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                                    }

                                    index++;
                                }

                                senderPlayer.openInventory(shop.getCoffer().get(i - 1));

                            } else {

                                senderPlayer.playSound(senderPlayer.getLocation(),
                                        org.bukkit.Sound.BLOCK_ANVIL_PLACE,
                                        50, 1);
                            }
                            event.setCancelled(true);

                        } else if (event.getCurrentItem().equals(CommonVariables.nextPage)) {

                            if (i < shop.getCoffer().size() - 1) {

                                senderPlayer.playSound(senderPlayer.getLocation(),
                                        org.bukkit.Sound.ITEM_BOOK_PAGE_TURN,
                                        50, 1);

                                index = 0;

                                for (ItemStack itemStack : shop.getCoffer().get(i + 1)) {

                                    if (itemStack == null) {

                                        shop.getCoffer().get(i + 1).setItem(index,
                                                new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
                                    }

                                    index++;
                                }

                                senderPlayer.openInventory(shop.getCoffer().get(i + 1));

                            } else {

                                senderPlayer.playSound(senderPlayer.getLocation(),
                                        Sound.BLOCK_ANVIL_PLACE,
                                        50, 1);
                            }
                            event.setCancelled(true);

                        } else if (event.getCurrentItem().getType().equals(Material.LIGHT_GRAY_STAINED_GLASS_PANE) ||
                                event.getCursor().getType() != Material.AIR) {

                            senderPlayer.playSound(senderPlayer.getLocation(),
                                    Sound.BLOCK_ANVIL_PLACE,
                                    50, 1);
                            event.setCancelled(true);

                        }
                    }
                }
                i++;
            }
        }
    }
}
