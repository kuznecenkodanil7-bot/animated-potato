package ru.raidmine.raidpunish.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import ru.raidmine.raidpunish.model.PresetReason;
import ru.raidmine.raidpunish.model.PunishmentType;

import java.util.List;

public final class PunishmentReasonScreen extends Screen {
    private static final int PAGE_SIZE = 7;

    private final Screen parent;
    private final String playerName;
    private final PunishmentType type;
    private final int days;
    private final int hours;
    private final List<PresetReason> presets = PresetReason.defaults();

    private TextFieldWidget reasonField;
    private String selectedDetails = "Выбери шаблон или впиши свою причину.";
    private int page = 0;
    private String status;

    public PunishmentReasonScreen(Screen parent, String playerName, PunishmentType type, int days, int hours) {
        super(Text.literal("Причина наказания"));
        this.parent = parent;
        this.playerName = playerName;
        this.type = type;
        this.days = days;
        this.hours = hours;
    }

    @Override
    protected void init() {
        clearChildren();

        int centerX = this.width / 2;
        int left = 20;
        int top = 84;

        this.reasonField = new TextFieldWidget(this.textRenderer, centerX - 150, 54, 300, 20, Text.literal("Причина"));
        this.reasonField.setMaxLength(256);
        this.reasonField.setPlaceholder(Text.literal("Причина для команды"));
        this.addDrawableChild(this.reasonField);

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, presets.size());
        int y = top;

        for (int i = start; i < end; i++) {
            PresetReason preset = presets.get(i);
            this.addDrawableChild(ButtonWidget.builder(Text.literal(preset.rule() + " — " + preset.shortReason()), button -> {
                        this.reasonField.setText(preset.shortReason());
                        this.selectedDetails = preset.details();
                    })
                    .dimensions(left, y, 220, 20)
                    .build());
            y += 24;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> changePage(-1))
                .dimensions(left, this.height - 54, 20, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> changePage(1))
                .dimensions(left + 24, this.height - 54, 20, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Скопировать команду"), button -> copyCommand())
                .dimensions(centerX - 150, this.height - 54, 146, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Отправить в чат"), button -> sendCommand())
                .dimensions(centerX + 4, this.height - 54, 146, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Назад"), button -> close())
                .dimensions(centerX - 50, this.height - 28, 100, 20)
                .build());

        setInitialFocus(this.reasonField);
    }

    private void changePage(int delta) {
        int maxPage = Math.max(0, (presets.size() - 1) / PAGE_SIZE);
        this.page = Math.max(0, Math.min(maxPage, this.page + delta));
        init();
    }

    private void copyCommand() {
        String command = buildCommand();
        if (command == null) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.keyboard != null) {
            client.keyboard.setClipboard(command);
            this.status = "Команда скопирована в буфер обмена";
        }
    }

    private void sendCommand() {
        String command = buildCommand();
        if (command == null) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            client.player.networkHandler.sendChatCommand(command.substring(1));
            this.status = "Команда отправлена";
            client.setScreen(null);
        }
    }

    private String buildCommand() {
        String reason = reasonField.getText().trim();
        if (reason.isBlank()) {
            this.status = "Укажи причину наказания";
            return null;
        }

        StringBuilder duration = new StringBuilder();
        if (days > 0) {
            duration.append(days).append('d');
        }
        if (hours > 0) {
            if (!duration.isEmpty()) {
                duration.append(' ');
            }
            duration.append(hours).append('h');
        }

        return type.command() + ' ' + playerName + ' ' + duration + ' ' + reason;
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Игрок: " + playerName + " | Наказание: " + type.title().getString()), this.width / 2, 34, 0xA0A0A0);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Срок: " + (days > 0 ? days + "d " : "") + (hours > 0 ? hours + "h" : "")), this.width / 2, 46, 0xA0A0A0);

        context.drawTextWithShadow(this.textRenderer, Text.literal("Шаблоны правил"), 20, 68, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Описание выбранного шаблона"), this.width / 2 + 10, 68, 0xFFFFFF);

        context.fill(this.width / 2 + 10, 84, this.width - 20, this.height - 64, 0x66000000);
        drawWrapped(context, Text.literal(selectedDetails), this.width / 2 + 16, 90, this.width / 2 - 42, 0xE0E0E0);

        String preview = buildPreviewText();
        context.drawTextWithShadow(this.textRenderer, Text.literal("Предпросмотр: " + preview), 20, this.height - 70, 0x55FF55);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Страница: " + (page + 1) + "/" + Math.max(1, (presets.size() + PAGE_SIZE - 1) / PAGE_SIZE)), 50, this.height - 49, 0xA0A0A0);

        if (status != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(status), this.width / 2, this.height - 84, 0xFFFF55);
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    private String buildPreviewText() {
        String reason = reasonField == null ? "<причина>" : reasonField.getText().trim();
        if (reason.isBlank()) {
            reason = "<причина>";
        }

        StringBuilder duration = new StringBuilder();
        if (days > 0) {
            duration.append(days).append('d');
        }
        if (hours > 0) {
            if (!duration.isEmpty()) {
                duration.append(' ');
            }
            duration.append(hours).append('h');
        }

        return type.command() + " " + playerName + " " + duration + " " + reason;
    }

    private void drawWrapped(DrawContext context, Text text, int x, int y, int maxWidth, int color) {
        int lineY = y;
        for (var orderedText : this.textRenderer.wrapLines(text, maxWidth)) {
            context.drawTextWithShadow(this.textRenderer, orderedText, x, lineY, color);
            lineY += 10;
        }
    }
}
