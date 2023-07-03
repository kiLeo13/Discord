package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ofc.discord.internal.SlashMinecraftCommand;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Unban extends SlashMinecraftCommand {

    public Unban(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {

        String playerInput = cmd.getOption("player").getAsString();

        OfflinePlayer player = Bukkit.getOfflinePlayer(playerInput);
        BanList list = Bukkit.getBanList(BanList.Type.NAME);

        if (!list.isBanned(player.getUniqueId().toString())) {
            cmd.reply("Jogador não está banido.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        list.pardon(player.getUniqueId().toString());
        cmd.reply("Jogador `" + player.getName() + "` foi desbanido.")
                .setEphemeral(true)
                .queue();
    }
}