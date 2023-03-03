package ru.dvdishka.shops.Classes;

import org.bukkit.ChatColor;

import java.util.Random;

public enum Color {

    AQUA("AQUA"),
    BLACK("BLACK"),
    BLUE("BLUE"),
    DARK_AQUA("DARK_AQUA"),
    DARK_BLUE("DARK_BLUE"),
    DARK_GRAY("DARK_GRAY"),
    DARK_GREEN("DARK_GREEN"),
    DARK_PURPLE("DARK_PURPLE"),
    DARK_RED("DARK_RED"),
    GOLD("GOLD"),
    GREY("GRAY"),
    GREEN("GREEN"),
    LIGHT_PURPLE("LIGHT_PURPLE"),
    WHITE("WHITE"),
    YELLOW("YELLOW");

    private final String name;

    Color(String name) {

        this.name = name;
    }

    public static Color getRandomColor() {

        return Color.values()[new Random().nextInt(Color.values().length) - 1];
    }

    public ChatColor getChatColor() {

        return ChatColor.valueOf(name);
    }
}
