package ofc.discord.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ofc.discord.DiscordCraft;
import ofc.discord.internal.SlashMinecraftCommand;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SlashCommandHandler extends ListenerAdapter {
    private static final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();
    private static final ArrayList<SlashMinecraftCommand> commands = new ArrayList<>(100);

    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (!event.isFromGuild()) return;

        String name = event.getName();

        SlashMinecraftCommand command = getCommand(name);
        if (command == null) return;

        command.run(event.getInteraction());
    }

    private SlashMinecraftCommand getCommand(String name) {
        for (SlashMinecraftCommand cmd : commands)
            if (cmd.name().equals(name))
                return cmd;

        return null;
    }

    public static void addCommands(SlashMinecraftCommand... minecraftCommands) {
        final Map<String, InputSlashCommand> inputSlashCommands = loadCommands(false);
        final List<SlashCommandData> slashCommands = new ArrayList<>();

        commands.addAll(List.of(minecraftCommands));

        if (inputSlashCommands == null) {
            System.err.println("Could not register Slash commands! Commands will not be updated.");
            return;
        }

        inputSlashCommands.forEach((name, cmd) -> {
            SlashCommandData slash = Commands.slash(name, cmd.description);

            for (SlashOption opt : cmd.options) {
                OptionData option = new OptionData(opt.optionType, opt.name, opt.description, opt.required);

                if (opt.choices != null)
                    for (Choice c : opt.choices)
                        option.addChoice(c.name, c.value);

                slash.addOptions(option);
            }

            if (cmd.permission != null)
                slash.setDefaultPermissions(DefaultMemberPermissions.enabledFor(cmd.permission));

            slashCommands.add(slash);
        });

        DiscordCraft.getJda().updateCommands().addCommands(slashCommands).queue(cmds -> {
            if (cmds.isEmpty())
                System.out.println("No Discord slash commands were added!");
            else
                System.out.printf("Successfully added %d Discord slash commands!\n", cmds.size());
        }, Throwable::printStackTrace);
    }

    private static Map<String, InputSlashCommand> loadCommands(boolean retry) {
        String json = loadFileJSON(retry);

        try {
            TypeToken<HashMap<String, InputSlashCommand>> token = new TypeToken<>(){};
            Map<String, InputSlashCommand> inputCommands = gson.fromJson(json, token.getType());

            return inputCommands == null
                    ? Map.of()
                    : Collections.unmodifiableMap(inputCommands);
        } catch (JsonSyntaxException e) {
            if (!retry) {
                System.err.println("JSON syntax has problems in file 'commands.json'! Attempting to generate a new one for you...");
                return loadCommands(true);
            }
            return null;
        }
    }

    /**
     * Loads the 'commands.json' file from the Disk.
     *
     * @param regen Whether it should regenerate the file override the old one or not.
     * @return A {@link String} JSON containing all the requested data.
     */
    private static String loadFileJSON(boolean regen) {
        String fileName = "commands.json";
        File dataFolder = DiscordCraft.getPlugin().getDataFolder();
        File file = new File(dataFolder, fileName);

        if (dataFolder.mkdirs())
            System.out.println("Plugin data folder was not found! Creating a new one for you...");

        if (regen && file.exists()) {
            if (file.renameTo(new File(dataFolder, "commands-DAMAGED.json"))) {
                DiscordCraft.getPlugin().saveResource(fileName, true);
                System.out.printf("File '%s' has been successfully regenerated!\n", fileName);
            }
        }

        if (!file.exists()) {
            System.out.printf("File '%s' was not found! Generating a new one for you...\n", fileName);
            DiscordCraft.getPlugin().saveResource(fileName, false);
            System.out.printf("File '%s' has been successfully created!\n", fileName);
        }

        try (InputStream stream = new FileInputStream(file)) {
            return new String(stream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private record InputSlashCommand(
            String description,
            Permission permission,
            List<SlashOption> options
    ) {}

    private record SlashOption(
            List<Choice> choices,
            OptionType optionType,
            String name,
            String description,
            boolean required
    ) {}

    private record Choice(String name, String value) {}
}