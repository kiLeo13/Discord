package ofc.discord.discord.minecraft.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import ofc.discord.internal.SlashMinecraftCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Tell extends SlashMinecraftCommand {

    public Tell(String name) {
        super(name);
    }

    @Override
    public void run(SlashCommandInteraction cmd) {

        Member member = cmd.getMember();
        String playerInput = cmd.getOption("player").getAsString();
        String content = cmd.getOption("content").getAsString();

        Player target = Bukkit.getPlayer(playerInput);

        if (target == null) {
            cmd.reply("Jogador não encontrado.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String message = String.format("<blue>[Discord-Tell]<reset> %s: %s", member.getUser().getEffectiveName(), content);
        target.sendRichMessage(message);
        cmd.reply("Mensagem enviada com sucesso à `" + target.getName() + "`!")
                .setEphemeral(true)
                .queue();
    }
}