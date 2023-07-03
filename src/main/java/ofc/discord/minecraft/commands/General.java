package ofc.discord.minecraft.commands;

import ofc.discord.DiscordCraft;
import ofc.discord.discord.Discord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class General implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender))
            return true;

        if (!sender.hasPermission("discord.reloadconfig")) {
            sender.sendRichMessage("<red>Missing permissions.");
            return true;
        }

        if (args.length < 1) {
            sender.sendRichMessage("<red>Incorrect usage!");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            DiscordCraft.getPlugin().reloadConfig();
            sender.sendRichMessage("<green>Configuration reloaded!");
            Discord.refreshData();
            System.out.println("\033[0;32mConfiguration has been succesfully reloaded.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return args.length == 1
                ? List.of("reload")
                : List.of();
    }
}