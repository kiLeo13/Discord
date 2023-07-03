package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ofc.discord.DiscordCraft;
import ofc.discord.internal.SlashMinecraftCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Teleport extends SlashMinecraftCommand {

    public Teleport(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {

        FileConfiguration config = DiscordCraft.getPlugin().getConfig();
        String playerInput = cmd.getOption("player").getAsString();
        String worldName = cmd.getOption("environment").getAsString();
        double x = cmd.getOption("x").getAsDouble();
        double y = cmd.getOption("y").getAsDouble();
        double z = cmd.getOption("z").getAsDouble();

        Player player = Bukkit.getPlayer(playerInput);
        World world = null;

        switch (worldName) {
            case "world" -> {
                String input = config.getString("overworld");
                world = Bukkit.getWorld(input == null ? "world" : input);
            }

            case "nether" -> {
                String input = config.getString("nether");
                world = Bukkit.getWorld(input == null ? "world_nether" : input);
            }

            case "end" -> {
                String input = config.getString("end");
                world = Bukkit.getWorld(input == null ? "world_the_end" : input);
            }
        }

        if (player == null) {
            cmd.reply("Jogador não encontrado.").queue();
            return;
        }

        if (world == null)
            cmd.reply("Dimensão `" + cmd.getOption("environment").getName() + "` não foi encontrada! A dimensão atual do jogador está sendo usado no lugar.")
                    .setEphemeral(true)
                    .queue();
        else
            cmd.reply(String.format("Teletransportando `%s` para (`%s`) `%f`, `%f`, `%f`", player.getName(), world.getName(), x, y, z))
                    .setEphemeral(true)
                    .queue();

        Location location = new Location(world == null ? player.getWorld() : world, x, y, z);
        player.teleportAsync(location);
    }
}