package ru.raidmine.raidpunish.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerNameExtractor {
    private static final Pattern PLAYER_NAME = Pattern.compile("(?<![A-Za-z0-9_])([A-Za-z0-9_]{3,16})(?![A-Za-z0-9_])");

    private PlayerNameExtractor() {
    }

    public static Optional<String> extract(Style style) {
        if (style == null) {
            return Optional.empty();
        }

        String insertion = style.getInsertion();
        if (isPlayerName(insertion)) {
            return Optional.of(insertion);
        }

        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent != null) {
            String value = clickEvent.getValue();
            Optional<String> fromClick = scan(value);
            if (fromClick.isPresent()) {
                return fromClick;
            }
        }

        HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent != null) {
            String value = hoverEvent.toString();
            Optional<String> fromHover = scan(value);
            if (fromHover.isPresent()) {
                return fromHover;
            }
        }

        return Optional.empty();
    }

    private static Optional<String> scan(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        Matcher matcher = PLAYER_NAME.matcher(text);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }

    public static boolean isPlayerName(String value) {
        return value != null && PLAYER_NAME.matcher(value).matches();
    }
}
