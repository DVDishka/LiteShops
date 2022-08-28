package ru.dvdishka.shops.shop.Classes;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Upgrade")
public class Upgrade implements ConfigurationSerializable {

    private int pageProgress;
    private int lineProgress;

    public Upgrade(int pageProgress, int lineProgress) {
        this.pageProgress = pageProgress;
        this.lineProgress = lineProgress;
    }

    public int getPageProgress() {
        return this.pageProgress;
    }

    public int getLineProgress() {
        return this.lineProgress;
    }

    public void setPageProgress(int pageProgress) {
        this.pageProgress = pageProgress;
    }

    public void setLineProgress(int lineProgress) {
        this.lineProgress = lineProgress;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageProgress", pageProgress);
        map.put("lineProgress", lineProgress);
        return map;
    }

    public static Upgrade deserialize(Map<String, Object> map) {
        return new Upgrade((Integer) map.get("pageProgress"),
               (Integer) map.get("lineProgress"));
    }
}
