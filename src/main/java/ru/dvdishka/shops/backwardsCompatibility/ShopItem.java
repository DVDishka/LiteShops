package ru.dvdishka.shops.backwardsCompatibility;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("ShopItem")
public class ShopItem implements ConfigurationSerializable {

    private ItemStack item;
    private int price;

    public ShopItem(ItemStack item, int price) {
        this.item = item;
        this.price = price;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public int getPrice() {
        return this.price;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("item", item);
        map.put("price", price);
        return map;
    }

    public static ShopItem deserialize(Map<String, Object> map) {
        ItemStack item = (ItemStack) map.get("item");
        int price = (int) map.get("price");
        return new ShopItem(item, price);
    }
}