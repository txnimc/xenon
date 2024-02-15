package me.jellysquid.mods.sodium.mixin.extras.borderless;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.embeddedt.embeddium.extras.ExtrasConfig;

import static org.embeddedt.embeddium.extras.ExtrasConfig.fullScreen;

@Mixin(KeyboardHandler.class)
public class KeyboardF11Mixin {
    @Shadow @Final public Minecraft minecraft;

    @Inject(method = "keyPress", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;toggleFullScreen()V"), cancellable = true)
    public void redirect$handleFullScreenToggle(long pWindowPointer, int pKey, int pScanCode, int pAction, int pModifiers, CallbackInfo ci) {
        switch (ExtrasConfig.borderlessAttachModeF11.get()) {
            case ATTACH -> ExtrasConfig.setFullScreenMode(minecraft.options, ExtrasConfig.FullScreenMode.nextOf(fullScreen.get()));
            case REPLACE -> ExtrasConfig.setFullScreenMode(minecraft.options, ExtrasConfig.FullScreenMode.nextBorderless(fullScreen.get()));
            case OFF -> ExtrasConfig.setFullScreenMode(minecraft.options, ExtrasConfig.FullScreenMode.nextFullscreen(fullScreen.get()));
        }
        ci.cancel();
    }
}
