package ofc.discord.minecraft;

import java.awt.*;

public enum ServerStatus {
    ONLINE("✔ Online", Color.GREEN),
    OFFLINE("❌ Offline", Color.RED),
    MAINTENANCE("📡 Manutenção", Color.YELLOW); // This is a really special case

    final String message;
    final Color color;

    ServerStatus(String message, Color color) {
        this.message = message;
        this.color = color;
    }

    public String message() {
        return this.message;
    }

    public Color color() {
        return this.color;
    }
}