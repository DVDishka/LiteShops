package ru.dvdishka.shops.shop.Classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.dvdishka.shops.backwardsCompatibility.ShopItem;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.common.ConfigVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SerializableAs("Shop")
public class Shop implements ConfigurationSerializable {

    private String name;
    private String owner;
    private ArrayList<Inventory> items = new ArrayList<>();
    private ArrayList<Inventory> coffer = new ArrayList<>();
    private ItemStack icon;
    private Upgrade upgrade;

    public Shop(String name, String owner, ArrayList<Inventory> items, ArrayList<Inventory> coffer, ItemStack icon,
                Upgrade upgrade) {
        this.name = name;
        this.owner = owner;
        this.items = items;
        this.coffer = coffer;
        this.icon = icon;
        this.upgrade = upgrade;
    }

    public Shop(String name, String owner, ItemStack icon) {
        this.name = name;
        this.owner = owner;
        this.icon = icon;
        if (ConfigVariables.defaultInventorySize % 9 == 0) {
            this.upgrade = new Upgrade(1, ConfigVariables.defaultInventorySize / 9);
        } else {
            this.upgrade = new Upgrade(1, ConfigVariables.defaultInventorySize / 9 + 1);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getOwner() {
        return this.owner;
    }

    public ArrayList<Inventory> getItems() {
        return this.items;
    }

    public ArrayList<Inventory> getCoffer() {
        return this.coffer;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public Upgrade getUpgrade() {
        return this.upgrade;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setItems(ArrayList<Inventory> items) {
        this.items = items;
    }

    public void setCoffer(ArrayList<Inventory> coffer) {
        this.coffer = coffer;
    }

    public void addCoffer(Inventory coffer) {
        this.coffer.add(coffer);
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setUpgrade(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

    public static Shop getShop(String name) {
        for (Shop shop : CommonVariables.shops) {
            if (shop.getName().equals(name)) {
                return shop;
            }
        }
        return null;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        ArrayList<ArrayList<ItemStack>> contents = new ArrayList<>();
        for (Inventory inventory : CommonVariables.shopsInventories.get(name)) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (int i = 0; i < inventory.getSize(); i++) {
                itemStacks.add(inventory.getItem(i));
            }
            contents.add(itemStacks);
        }
        ArrayList<ArrayList<ItemStack>> coffers = new ArrayList<>();
        for (Inventory inventory : coffer) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (int i = 0; i < inventory.getSize(); i++) {
                itemStacks.add(inventory.getItem(i));
            }
            coffers.add(itemStacks);
        }
        map.put("coffer", coffers);
        map.put("items", contents);
        map.put("icon", icon);
        map.put("upgrade", upgrade);
        return map;
    }

    public static Shop deserialize(Map<String, Object> map) {

        String name = (String) map.get("name");
        String owner = (String) map.get("owner");
        ArrayList<ArrayList<ItemStack>> contents = (ArrayList<ArrayList<ItemStack>>) map.get("items");
        ArrayList<ArrayList<ItemStack>> coffers = (ArrayList<ArrayList<ItemStack>>) map.get("coffer");
        ItemStack icon;
        if (map.get("icon") == null) {
            icon = new ItemStack(Material.BARRIER);
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName(name);
            icon.setItemMeta(iconMeta);
        } else {
            icon = (ItemStack) map.get("icon");
        }
        Upgrade upgrade = (Upgrade) map.get("upgrade");

        ItemStack prevPage = new ItemStack(Material.ARROW);
        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta prevPageMeta = prevPage.getItemMeta();
        prevPageMeta.setDisplayName("<--");
        prevPage.setItemMeta(prevPageMeta);
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.setDisplayName("-->");
        nextPage.setItemMeta(nextPageMeta);

        ArrayList<Inventory> coffer = new ArrayList<>();
        int i = 1;
        for (ArrayList<ItemStack> item : coffers) {
            Inventory inventory = Bukkit.createInventory(null, item.size(),
                    ChatColor.GOLD + name + " coffer " + i);
            int index = 0;
            for (ItemStack itemStack : item) {
                if (index != ConfigVariables.prevPageIndex && index != ConfigVariables.defaultNextPageIndex) {
                    inventory.setItem(index, itemStack);
                }
                index++;
            }
            coffer.add(inventory);
            i++;
        }

        try {
            i = 1;
            ArrayList<Inventory> items = new ArrayList<>();
            for (ArrayList<ItemStack> item : contents) {
                Inventory inventory = Bukkit.createInventory(null, item.size(),
                        ChatColor.GOLD + name + " " + i);
                int index = 0;
                for (ItemStack itemStack : item) {
                    inventory.setItem(index, itemStack);
                    index++;
                }
                items.add(inventory);
                i++;
            }
            return new Shop(name, owner, items, coffer, icon, upgrade);
        } catch (Exception e) {
            CommonVariables.logger.warning(e.getMessage());
            ArrayList<Inventory> items = new ArrayList<>();
            ArrayList<ShopItem> oldItems = (ArrayList<ShopItem>) map.get("items");
            Inventory inventory = Bukkit.createInventory(null, 27,
                    ChatColor.GOLD + name + " 1");
            i = 0;
            for (ShopItem shopItem : oldItems) {
                if (shopItem != null) {
                    inventory.setItem(i, shopItem.getItem());
                } else {
                    inventory.setItem(i, null);
                }
                i++;
            }
            inventory.setItem(ConfigVariables.prevPageIndex, prevPage);
            inventory.setItem(ConfigVariables.defaultNextPageIndex, nextPage);
            items.add(inventory);
            return new Shop(name, owner, items, coffer, icon, upgrade);
        }
    }
}