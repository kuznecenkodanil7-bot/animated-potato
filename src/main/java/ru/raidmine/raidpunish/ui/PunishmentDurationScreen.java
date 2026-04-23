package ru.raidmine.raidpunish.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import ru.raidmine.raidpunish.model.PunishmentType;

public final class PunishmentDurationScreen extends Screen {
    private final Screen parent;
    private final String playerName;
    private final PunishmentType type;
    private TextFieldWidget daysField;
    private TextFieldWidget hoursField;
    private String error;

    public PunishmentDurationScreen(Screen parent, String playerName, PunishmentType type) {
        super(Text.literal("Срок наказания"));
        this.parent = parent;
        this.playerName = playerName;
        this.type = type;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 2 - 45;

        this.daysField = new TextFieldWidget(this.textRenderer, centerX - 100, y, 200, 20, Text.literal("Дни"));
        this.daysField.setPlaceholder(Text.literal("d — количество дней"));
        this.daysField.setMaxLength(3);
        this.daysField.setText("0");
        this.addDrawableChild(this.daysField);

        this.hoursField = new TextFieldWidget(this.textRenderer, centerX - 100, y + 28, 200, 20, Text.literal("Часы"));
        this.hoursField.setPlaceholder(Text.literal("h — количество часов"));
        this.hoursField.setMaxLength(3);
        this.hoursField.setText("0");
        this.addDrawableChild(this.hoursField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Далее"), button -> openReasonScreen())
                .dimensions(centerX - 100, y + 60, 95, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Назад"), button -> close())
                .dimensions(centerX + 5, y + 60, 95, 20)
                .build());

        setInitialFocus(this.daysField);
    }

    private void openReasonScreen() {
        int days = parseNumber(daysField.getText());
        int hours = parseNumber(hoursField.getText());

        if (days < 0 || hours < 0 || (days == 0 && hours == 0)) {
            this.error = "Укажи срок: минимум 1 час или 1 день";
            return;
        }

        if (hours > 23) {
            this.error = "Часы должны быть от 0 до 23";
            return;
        }

        if (this.client != null) {
            this.client.setScreen(new PunishmentReasonScreen(this.parent, playerName, type, days, hours));
        }
    }

    private int parseNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
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
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Игрок: " + playerName), this.width / 2, 40, 0xA0A0A0);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Наказание: " + type.title().getString()), this.width / 2, 52, 0xA0A0A0);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Дни (d):"), this.width / 2 - 100, this.height / 2 - 58, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Часы (h):"), this.width / 2 - 100, this.height / 2 - 30, 0xFFFFFF);

        if (error != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(error), this.width / 2, this.height / 2 + 48, 0xFF5555);
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
