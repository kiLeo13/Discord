package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ofc.discord.internal.SlashMinecraftCommand;
import ofc.discord.minecraft.Minecraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Health extends SlashMinecraftCommand {

    public Health(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {

        String playerInput = cmd.getOption("player").getAsString();
        double newHealth = cmd.getOption("health").getAsDouble();

        Player player = Bukkit.getPlayer(playerInput);

        if (player == null) {
            cmd.reply("Jogador não encontrado.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        setHealth(player, newHealth);

        player.sendRichMessage("<gold>Vida redefinida para <red>" + newHealth + "<gold>!");
        cmd.reply(String.format("Vida de `%s` foi redefinida para `%f`!", player.getName(), newHealth))
                .setEphemeral(true)
                .queue();
    }

    private void setHealth(Player player, double value) {

        if (value <= 20) {
            player.setMaxHealth(20);
            Minecraft.sync(() -> player.setHealth(value < 0 ? 0 : value));
        } else {
            player.setMaxHealth(value);
            player.setHealth(value);
        }
    }
}