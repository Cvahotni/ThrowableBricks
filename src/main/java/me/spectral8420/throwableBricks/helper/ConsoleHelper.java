package me.spectral8420.throwableBricks.helper;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class ConsoleHelper {
    private static final String prefix = "[ThrowableBricks] ";

    public static void log(String message) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(prefix + message);
    }
}
