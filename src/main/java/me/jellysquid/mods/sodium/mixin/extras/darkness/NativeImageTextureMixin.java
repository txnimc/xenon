package me.jellysquid.mods.sodium.mixin.extras.darkness;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.embeddedt.embeddium.extras.darkness.DarknessPlus;
import org.embeddedt.embeddium.extras.darkness.accessors.TextureAccess;

@Mixin(DynamicTexture.class)
public class NativeImageTextureMixin implements TextureAccess
{
	@Shadow private NativeImage pixels;
	@Unique private boolean embPlus$enableHook = false;

	@Inject(method = "upload", at = @At(value = "HEAD"))
	private void inject$onUpload(CallbackInfo ci) {
        if (!embPlus$enableHook || !DarknessPlus.enabled) return;

        final NativeImage img = pixels;
        for (int b = 0; b < 16; b++) {
            for (int s = 0; s < 16; s++) {
                final int color = DarknessPlus.darken(img.getPixelRGBA(b, s), b, s);
                img.setPixelRGBA(b, s, color);
            }
        }
    }

	// TODO: RID OFF OF THIS
	@Override
	public void embPlus$enableUploadHook() {
		embPlus$enableHook = true;
	}
}