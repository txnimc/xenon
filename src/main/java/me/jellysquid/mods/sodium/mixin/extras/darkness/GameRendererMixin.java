package me.jellysquid.mods.sodium.mixin.extras.darkness;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.mixin.extras.darkness.accessors.LightTextureAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.embeddedt.embeddium.extras.darkness.DarknessPlus;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow @Final public LightTexture lightTexture;
	@Shadow public abstract Minecraft getMinecraft();

	@Inject(method = "renderLevel", at = @At(value = "HEAD"))
	private void inject$renderLevel(float tickDelta, long nanos, PoseStack matrixStack, CallbackInfo ci) {
		final var mc = getMinecraft();
		final var lightTexAccessor = (LightTextureAccessor) lightTexture;

		if (lightTexAccessor.isDirty()) {
			mc.getProfiler().push("lightTex");
			DarknessPlus.updateLuminance(tickDelta, mc, (GameRenderer) (Object) this, lightTexAccessor.getFlicker());
			mc.getProfiler().pop();
		}
	}
}