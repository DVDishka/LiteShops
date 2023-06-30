package ru.dvdishka.shops.common;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.CustomChart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dvdishka.shops.Classes.Shop;
import ru.dvdishka.shops.handlers.commands.coffer.CofferOpenShopCommand;
import ru.dvdishka.shops.handlers.commands.create.*;
import ru.dvdishka.shops.handlers.commands.edit.EditColorShopCommand;
import ru.dvdishka.shops.handlers.commands.edit.EditIconShopCommand;
import ru.dvdishka.shops.handlers.commands.edit.EditNameShopCommand;
import ru.dvdishka.shops.handlers.commands.edit.EditPriceShopCommand;
import ru.dvdishka.shops.handlers.commands.open.InfiniteBuyOpenShopCommand;
import ru.dvdishka.shops.handlers.commands.open.InfiniteSellOpenShopCommand;
import ru.dvdishka.shops.handlers.commands.open.OpenShopCommand;
import ru.dvdishka.shops.handlers.commands.upgrade.UpgradeShopCommand;
import ru.dvdishka.shops.handlers.events.shopCoffer.InfiniteShopCofferEvent;
import ru.dvdishka.shops.handlers.events.shopCoffer.ShopCofferEvent;
import ru.dvdishka.shops.handlers.events.shopInventory.InfiniteBuyShopInventoryEvent;
import ru.dvdishka.shops.handlers.events.shopInventory.InfiniteSellShopInventoryEvent;
import ru.dvdishka.shops.handlers.events.shopInventory.ShopInventoryEvent;
import ru.dvdishka.shops.handlers.events.shopMenu.InfiniteBuyShopMenuEvent;
import ru.dvdishka.shops.handlers.events.shopMenu.InfiniteSellShopMenuEvent;
import ru.dvdishka.shops.handlers.events.shopMenu.ShopMenuEvent;
import ru.dvdishka.shops.handlers.events.shopMenu.shopEdit.IconMenuEvent;
import ru.dvdishka.shops.handlers.events.shopMenu.shopEdit.UpgradeMenuEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class
Initialization {

    public static void registerEventHandlers(Plugin plugin) {

        Bukkit.getPluginManager().registerEvents(new ShopMenuEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new InfiniteSellShopMenuEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new InfiniteBuyShopMenuEvent(), plugin);

        Bukkit.getPluginManager().registerEvents(new ShopInventoryEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new InfiniteSellShopInventoryEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new InfiniteBuyShopInventoryEvent(), plugin);

        Bukkit.getPluginManager().registerEvents(new IconMenuEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new UpgradeMenuEvent(), plugin);

        Bukkit.getPluginManager().registerEvents(new ShopCofferEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new InfiniteShopCofferEvent(), plugin);
    }

    public static void registerCommands() {

        CommandTree shopCommandTree = new CommandTree("shop");

        // CREATE
        {
            // CREATE
            {
                shopCommandTree.then(new LiteralArgument("create").setListed(false)
                        .withPermission(Permissions.create)

                        .then(new StringArgument("shopName")

                                .executesPlayer((sender, args) -> {

                                    new CreateShopCommand().execute(sender, args);
                                })
                        )
                );
            }

            // CREATING
            {
                shopCommandTree.then(new LiteralArgument("creating").setListed(false)
                        .withRequirement(sender -> Functions.isCreatingShop((Player) sender))

                        .then(new StringArgument("shopName")

                                .then(new StringArgument("playerName")

                                        .executesPlayer((sender, args) -> {

                                            new CreatingShopCommand().execute(sender, args);
                                        })
                                )
                        )
                );
            }

            // CANCEL
            {
                shopCommandTree.then(new LiteralArgument("cancel").setListed(false)
                        .withRequirement(sender -> Functions.isCreatingShop((Player) sender))

                        .then(new StringArgument("playerName")

                                .executesPlayer((sender, args) -> {
                                    new CreatingCancelShopCommand().execute(sender, args);
                                })
                        )
                );
            }
        }

        // OPEN
        {
            shopCommandTree.then(new LiteralArgument("open").setListed(false)
                    .withPermission(Permissions.open)

                    .executesPlayer((sender, args) -> {

                        new OpenShopCommand().execute(sender, args);
                    })
            );
        }

        // EDIT
        {
            shopCommandTree.then(new LiteralArgument("edit").setListed(false)
                    .withPermission(Permissions.edit)

                    .then(new StringArgument("shopName").includeSuggestions
                                    (ArgumentSuggestions.strings(Shop::getPlayerShops))

                            // PRICE
                            .then(new LiteralArgument("price").setListed(false)

                                    .then(new IntegerArgument("shopPage")

                                            .then(new IntegerArgument("slot")

                                                    .then(new StringArgument("priceItem")
                                                            .includeSuggestions(ArgumentSuggestions
                                                                    .strings(CommonVariables.materials))

                                                            .then(new IntegerArgument("priceItemAmount")

                                                                    .executesPlayer((sender, args) -> {

                                                                        new EditPriceShopCommand().execute(sender, args);
                                                                    })
                                                            )
                                                    )
                                            )
                                    )
                            )

                            // NAME
                            .then(new LiteralArgument("name").setListed(false)

                                    .then(new StringArgument("newShopName")

                                            .executesPlayer((sender, args) -> {

                                                new EditNameShopCommand().execute(sender, args);
                                            })
                                    )
                            )

                            // ICON
                            .then(new LiteralArgument("icon").setListed(false)

                                    .executesPlayer((sender, args) -> {

                                        new EditIconShopCommand().execute(sender, args);
                                    })
                            )

                            // UPGRADE
                            .then(new LiteralArgument("upgrade").setListed(false)

                                    .executesPlayer((sender, args) -> {

                                        new UpgradeShopCommand().execute(sender, args);
                                    })
                            )

                            // COLOR
                            .then(new LiteralArgument("color").setListed(false)

                                    .then(new ChatColorArgument("newColor")

                                            .executesPlayer((sender, args) -> {

                                                new EditColorShopCommand().execute(sender, args);
                                            })
                                    )
                            )
                    )
            );
        }

        {
            // COFFER
            shopCommandTree.then(new LiteralArgument("coffer").setListed(false)
                    .withPermission(Permissions.coffer)

                    .then(new StringArgument("shopName").includeSuggestions
                            (ArgumentSuggestions.strings(Shop::getPlayerShops))

                            .executesPlayer((sender, args) -> {

                                new CofferOpenShopCommand().execute(sender, args);
                            })
                    )
            );
        }

        {
            // INFINITE SHOPS
            shopCommandTree.then(new LiteralArgument("infinite").setListed(false)
                    .withRequirement(sender -> Functions.doHaveOneOfPermissions((Player) sender,
                            Arrays.asList(Permissions.infiniteCreateBuy, Permissions.infiniteCreateSell,
                                    Permissions.infiniteOpenBuy, Permissions.infiniteOpenSell)))

                    // SELL
                    .then(new LiteralArgument("sell").setListed(false)
                            .withRequirement(sender -> Functions.doHaveOneOfPermissions((Player) sender,
                                    Arrays.asList(Permissions.infiniteOpenSell, Permissions.infiniteCreateSell)))

                            // OPEN
                            .then(new LiteralArgument("open")
                                    .withPermission(Permissions.infiniteOpenSell)

                                    .executesPlayer((sender, args) -> {

                                        new InfiniteSellOpenShopCommand().execute(sender, args);
                                    })
                            )

                            // CREATE
                            .then(new LiteralArgument("create").setListed(false)
                                    .withPermission(Permissions.infiniteCreateSell)

                                    .then(new StringArgument("shopName")

                                            .executesPlayer((sender, args) -> {

                                                new InfiniteSellCreateShopCommand().execute(sender, args);
                                            })
                                    )
                            )
                    )

                    // BUY
                    .then(new LiteralArgument("buy").setListed(false)
                            .withRequirement(sender -> Functions.doHaveOneOfPermissions((Player) sender,
                                    Arrays.asList(Permissions.infiniteOpenBuy, Permissions.infiniteCreateBuy)))

                            // OPEN
                            .then(new LiteralArgument("open").setListed(false)
                                    .withPermission(Permissions.infiniteOpenBuy)

                                    .executesPlayer((sender, args) -> {

                                        new InfiniteBuyOpenShopCommand().execute(sender, args);
                                    })
                            )

                            // CREATE
                            .then(new LiteralArgument("create").setListed(false)
                                    .withPermission(Permissions.infiniteCreateBuy)

                                    .then(new StringArgument("shopName")

                                            .executesPlayer((sender, args) -> {

                                                new InfiniteBuyCreateShopCommand().execute(sender, args);
                                            })
                                    )
                            )
                    )
            );
        }

        shopCommandTree.register();
    }

    public static void shopMenuCreating() {

        ArrayList<Inventory> shopsIcons = new ArrayList<>();

        int shopsAmount = CommonVariables.shops.size();

        int i = 1;

        if (shopsAmount == 0) {

            Inventory page = Bukkit.createInventory(null, 54,
                    ChatColor.DARK_GREEN + (ChatColor.BOLD + "Shops: ") +
                            ChatColor.WHITE + (ChatColor.RESET + String.valueOf(CommonVariables.shopMenu.size() + 1)));

            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            shopsIcons.add(page);
            shopsAmount -= 52;
            i++;
        }

        while (shopsAmount > 0) {

            Inventory page = Bukkit.createInventory(null, 54,
                    ChatColor.DARK_GREEN + (ChatColor.BOLD + "Shops: ") +
                            ChatColor.WHITE + (ChatColor.RESET + String.valueOf(CommonVariables.shopMenu.size() + 1)));

            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            shopsIcons.add(page);
            shopsAmount -= 52;
            i++;
        }

        int stackIndex = 0;
        int inventoryIndex = 0;

        for (Shop shop : CommonVariables.shops) {

            if (stackIndex == 45) {

                stackIndex++;
            }
            if (stackIndex == 53) {

                stackIndex = 0;
                inventoryIndex++;
            }

            shopsIcons.get(inventoryIndex).setItem(stackIndex, shop.getIcon());
            stackIndex++;
        }

        stackIndex = 0;

        Inventory lastPage = shopsIcons.get(inventoryIndex);

        for (ItemStack itemStack : lastPage) {

            if (itemStack == null) {

                lastPage.setItem(stackIndex, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }
            stackIndex++;
        }

        shopsIcons.set(inventoryIndex, lastPage);
        CommonVariables.shopMenu = shopsIcons;
    }

    public static void infiniteSellShopCreating() {

        ArrayList<Inventory> shopsIcons = new ArrayList<>();
        ArrayList<Shop> infiniteSellShops = new ArrayList<>();

        int shopsAmount = 0;

        for (Shop shop : CommonVariables.infiniteShops) {

            if (shop.isSell()) {

                infiniteSellShops.add(shop);
                shopsAmount++;
            }
        }

        int pageNumber = 1;

        if (shopsAmount == 0) {

            Inventory page = Bukkit.createInventory(null, 54,
                    ChatColor.AQUA + (ChatColor.BOLD + "Shops: ") + ChatColor.RESET + pageNumber);
            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            shopsIcons.add(page);
            shopsAmount -= 52;
            pageNumber++;
        }

        while (shopsAmount > 0) {

            Inventory page = Bukkit.createInventory(null, 54,
                    ChatColor.AQUA + (ChatColor.BOLD + "Shops: ") + ChatColor.RESET + pageNumber);

            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            shopsIcons.add(page);
            shopsAmount -= 52;
            pageNumber++;
        }

        int stackIndex = 0;
        int inventoryIndex = 0;

        for (Shop shop : infiniteSellShops) {

            if (stackIndex == 45) {

                stackIndex++;
            }
            if (stackIndex == 53) {

                stackIndex = 0;
                inventoryIndex++;
            }

            shopsIcons.get(inventoryIndex).setItem(stackIndex, shop.getIcon());
            stackIndex++;
        }

        stackIndex = 0;

        Inventory lastPage = shopsIcons.get(inventoryIndex);

        for (ItemStack itemStack : lastPage) {

            if (itemStack == null) {

                lastPage.setItem(stackIndex, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }
            stackIndex++;
        }

        shopsIcons.set(inventoryIndex, lastPage);
        CommonVariables.infiniteSellShopMenu = shopsIcons;
    }

    public static void infiniteBuyShopMenuCreating() {

        ArrayList<Inventory> shopsIcons = new ArrayList<>();
        ArrayList<Shop> infiniteBuyShops = new ArrayList<>();

        int shopsAmount = 0;

        for (Shop shop : CommonVariables.infiniteShops) {

            if (!shop.isSell()) {

                infiniteBuyShops.add(shop);
                shopsAmount++;
            }
        }

        int pageNumber = 1;

        if (shopsAmount == 0) {

            Inventory page = Bukkit.createInventory(null, 54,
                    ChatColor.RED + (ChatColor.BOLD + "Shops: ") + ChatColor.RESET + pageNumber);

            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            shopsIcons.add(page);
            shopsAmount -= 52;
            pageNumber++;
        }

        while (shopsAmount > 0) {

            Inventory page = Bukkit.createInventory(null, 54,
                    ChatColor.RED + (ChatColor.BOLD + "Shops: ") + ChatColor.RESET + pageNumber);

            page.setItem(45, CommonVariables.prevPage);
            page.setItem(53, CommonVariables.nextPage);

            shopsIcons.add(page);
            shopsAmount -= 52;
            pageNumber++;
        }

        int stackIndex = 0;
        int inventoryIndex = 0;

        for (Shop shop : infiniteBuyShops) {

            if (stackIndex == 45) {

                stackIndex++;
            }
            if (stackIndex == 53) {

                stackIndex = 0;
                inventoryIndex++;
            }

            shopsIcons.get(inventoryIndex).setItem(stackIndex, shop.getIcon());
            stackIndex++;
        }

        stackIndex = 0;

        Inventory lastPage = shopsIcons.get(inventoryIndex);

        for (ItemStack itemStack : lastPage) {

            if (itemStack == null) {

                lastPage.setItem(stackIndex, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }
            stackIndex++;
        }

        shopsIcons.set(inventoryIndex, lastPage);
        CommonVariables.infiniteBuyShopMenu = shopsIcons;
    }

    public static void implementBStats(JavaPlugin plugin) {

        Metrics bStats = new Metrics(plugin, CommonVariables.bStatsPluginID);
    }
}
