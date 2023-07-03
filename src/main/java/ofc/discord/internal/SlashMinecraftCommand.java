package ofc.discord.internal;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public abstract class SlashMinecraftCommand implements MinecraftCommand {
    private final String name;

    public SlashMinecraftCommand(String name) {
        this.name = name;
    }

    public final String name() {
        return this.name;
    }
}

interface MinecraftCommand {
    void run(SlashCommandInteraction cmd);
}