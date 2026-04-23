package ru.raidmine.raidpunish.model;

import net.minecraft.text.Text;

public enum PunishmentType {
    MUTE("mute", "/mute", Text.literal("Мут")),
    WARN("warn", "/warn", Text.literal("Предупреждение")),
    BAN("ban", "/ban", Text.literal("Бан")),
    IP_BAN("ipban", "/ipban", Text.literal("Бан по IP"));

    private final String id;
    private final String command;
    private final Text title;

    PunishmentType(String id, String command, Text title) {
        this.id = id;
        this.command = command;
        this.title = title;
    }

    public String id() {
        return id;
    }

    public String command() {
        return command;
    }

    public Text title() {
        return title;
    }
}
