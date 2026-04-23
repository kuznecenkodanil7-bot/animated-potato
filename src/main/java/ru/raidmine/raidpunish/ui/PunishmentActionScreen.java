package ru.raidmine.raidpunish.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import ru.raidmine.raidpunish.model.PunishmentType;

public final class PunishmentActionScreen extends Screen {
    private final Screen parent;
    private final String playerName;

    public PunishmentActionScreen(Screen parent, String playerName) {
        super(Text.literal("Выбор наказания"));
        this.parent = parent;
        this.playerName = playerName;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 2 - 55;

        for (PunishmentType type : PunishmentType.values()) {
            this.addDrawableChild(ButtonWidget.builder(type.title(), button ->
                            this.client.setScreen(new PunishmentDurationScreen(this, playerName, type)))
                    .dimensions(centerX - 100, y, 200, 20)
                    .build());
            y += 25;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Отмена"), button -> close())
                .dimensions(centerX - 100, y + 8, 200, 20)
                .build());
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
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Нажми на тип наказания"), this.width / 2, 58, 0xA0A0A0);
        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
