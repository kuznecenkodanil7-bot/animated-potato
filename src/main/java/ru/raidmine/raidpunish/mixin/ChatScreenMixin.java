package ru.raidmine.raidpunish.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.raidmine.raidpunish.client.RaidPunishClient;
import ru.raidmine.raidpunish.util.PlayerNameExtractor;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void raidpunish$openPunishmentMenu(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (click.button() != 0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.inGameHud == null || client.currentScreen == null) {
            return;
        }

        ChatHud chatHud = client.inGameHud.getChatHud();
        Style style = chatHud.getTextStyleAt(click.x(), click.y());
        PlayerNameExtractor.extract(style).ifPresent(playerName -> {
            RaidPunishClient.openPunishmentScreen(playerName);
            cir.setReturnValue(true);
        });
    }
}
