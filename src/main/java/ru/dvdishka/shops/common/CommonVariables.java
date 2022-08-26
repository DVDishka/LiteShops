package ru.dvdishka.shops.common;

import ru.dvdishka.shops.shop.Classes.Shop;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class CommonVariables {

    public static Logger logger = Bukkit.getLogger();
    public static ArrayList<Shop> shops = new ArrayList<>();
    public static HashMap<String, ArrayList<Inventory>> shopsInventories = new HashMap<>();
    public static ArrayList<Inventory> shopMenu = new ArrayList<>();
    public static ArrayList<Inventory> iconMenu = new ArrayList<>();
    public static HashMap<String, String> playerShopIconChoose = new HashMap<>();
    public static HashMap<String, String> playerShopCreating = new HashMap<>();
    public static HashMap<String, Inventory> upgradeMenu = new HashMap<>();
    public static HashMap<String, String> playerShopUpgrade = new HashMap<>();
}
