package ru.dvdishka.shops;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import ru.dvdishka.shops.backwardsCompatibility.ShopItem;
import ru.dvdishka.shops.common.ConfigVariables;
import ru.dvdishka.shops.shop.Classes.Shop;
import ru.dvdishka.shops.common.CommonVariables;
import ru.dvdishka.shops.shop.Classes.Upgrade;
import ru.dvdishka.shops.shop.shopHandlers.EventHandler;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public final class Shops extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(Shop.class, "Shop");
        ConfigurationSerialization.registerClass(ShopItem.class, "ShopItem");
        ConfigurationSerialization.registerClass(Upgrade.class, "Upgrade");
    }

    @Override
    public void onEnable() {

        File rootDir = new File("plugins/Shops");
        File shopsFile = new File("plugins/Shops/shops.yml");
        File configFile = new File("plugins/Shops/config.yml");

        if (!rootDir.exists()) {
            if (rootDir.mkdir()) {
                CommonVariables.logger.info("Shops directory has been created!");
            } else {
                CommonVariables.logger.warning("Shops directory can not be created!");
            }
        }
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    FileConfiguration config = new YamlConfiguration();
                    config.set("shopCost.material", "diamond");
                    config.set("shopCost.amount", 10);
                    config.set("newPageCost.material", "diamond");
                    config.set("newPageCost.amount", 10);
                    config.set("defaultInventorySize", 27);
                    config.set("newLineCost.material", "diamond");
                    config.set("newLineCost.amount", 10);
                    config.save(configFile);
                    CommonVariables.logger.info("config.yml file has been created!");
                } else {
                    CommonVariables.logger.warning("config.yml file can not be created!");
                }
            } catch (Exception e) {
                CommonVariables.logger.warning("config.yml file can not be created!");
            }
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Shops/config.yml"));

            try {
                int shopCostAmount = config.getInt("shopCost.amount");
                if (shopCostAmount < 0) {
                    CommonVariables.logger.warning("shopCost.amount must be >= 0");
                } else {
                    Material shopCostMaterial = Material.valueOf(config.getString("shopCost.material").toUpperCase());
                    ConfigVariables.shopCost = new ItemStack(shopCostMaterial, shopCostAmount);
                }
            } catch (Exception e) {
                CommonVariables.logger.warning("Wrong material in shopCost.material (config.yml)");
            }

            try {
                int newPageCostAmount = config.getInt("newPageCost.amount");
                if (newPageCostAmount < 0) {
                    CommonVariables.logger.warning("newPageCost.amount must be >= 0");
                } else {
                    Material newPageCostMaterial = Material.valueOf(config.getString("newPageCost.material").toUpperCase());
                    ConfigVariables.newPageCost = new ItemStack(newPageCostMaterial, newPageCostAmount);
                }
            } catch (Exception e) {
                CommonVariables.logger.warning("Wrong material in newPageCost.material (config.yml)");
            }

            try {
                int newLineCostAmount = config.getInt("newLineCost.amount");
                if (newLineCostAmount < 0) {
                    CommonVariables.logger.warning("newLineCost.amount must be >= 0");
                } else {
                    Material newLineCostMaterial = Material.valueOf(config.getString("newLineCost.material").toUpperCase());
                    ConfigVariables.newLineCost = new ItemStack(newLineCostMaterial, newLineCostAmount);
                }
            } catch (Exception e) {
                CommonVariables.logger.warning("Wrong material in newLineCost.material (config.yml)");
            }

            int inventorySize = config.getInt("defaultInventorySize");
            if (inventorySize >= 1 && inventorySize <= 54) {
                ConfigVariables.defaultInventorySize = inventorySize;
            } else {
                CommonVariables.logger.warning("Inventory size must be >= 1 and <= 54");
            }

            ConfigVariables.defaultNextPageIndex = ConfigVariables.defaultInventorySize - 1;
            if (ConfigVariables.defaultInventorySize % 9 == 0) {
                ConfigVariables.prevPageIndex = ConfigVariables.defaultInventorySize - 9;
            } else {
                ConfigVariables.prevPageIndex = (ConfigVariables.defaultInventorySize / 9) * 9;
            }
        }
        if (!shopsFile.exists()) {
            try {
                if (shopsFile.createNewFile()) {
                    CommonVariables.logger.info("shops.yml file has been created!");
                } else {
                    CommonVariables.logger.warning("shops.yml file can not be created!");
                }
            } catch (Exception e) {
                CommonVariables.logger.warning("shops.yml file can not be created!");
            }
        } else {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Shops/shops.yml"));
                CommonVariables.shops = (ArrayList<Shop>) config.get("Shops");
                HashMap<String, ArrayList<Inventory>> shopsInventories = new HashMap<>();
                for (Shop shop : CommonVariables.shops) {
                    shopsInventories.put(shop.getName(), shop.getItems());
                }
                CommonVariables.shopsInventories = shopsInventories;
            } catch (Exception e) {
                CommonVariables.logger.warning("Something went wrong while trying to read shops.yml");
            }
        }


        PluginCommand shopCommand = Bukkit.getPluginCommand("shop");

        ru.dvdishka.shops.shop.shopHandlers.CommandExecutor shopCommandExecutor = new ru.dvdishka.shops.shop.shopHandlers.CommandExecutor();
        ru.dvdishka.shops.shop.shopHandlers.TabCompleter shopTabCompleter = new ru.dvdishka.shops.shop.shopHandlers.TabCompleter();

        Bukkit.getPluginManager().registerEvents(new EventHandler(), this);

        assert shopCommand != null;
        shopCommand.setExecutor(shopCommandExecutor);
        shopCommand.setTabCompleter(shopTabCompleter);

        CommonVariables.logger.info("Shops plugin has been enabled!");
    }

    @Override
    public void onDisable() {

        File file = new File("plugins/Shops/prices.json");

        FileConfiguration shopsConfig = new YamlConfiguration();
        shopsConfig.set("Shops", CommonVariables.shops);
        try {
            shopsConfig.save(new File("plugins/Shops/shops.yml"));
        } catch (Exception e) {
            CommonVariables.logger.warning("Something went wrong while trying to write shops.yml file");
        }
        CommonVariables.logger.info("Bank plugin has been disabled!");
    }
}
