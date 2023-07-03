package ofc.discord.minecraft;

import ofc.discord.DiscordCraft;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Minecraft {

    public static void sync(Runnable task) {

        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLater(DiscordCraft.getPlugin(), 0);
    }

    public static String skin(Player player) {
        return "https://cravatar.eu/helmavatar/" + player.getName() + "/64.png";
    }
}