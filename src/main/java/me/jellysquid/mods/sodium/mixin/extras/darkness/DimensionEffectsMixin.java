package me.jellysquid.mods.sodium.mixin.extras.darkness;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toni.xenon.extras.ExtrasConfig;
import toni.xenon.extras.darkness.DarknessPlus;

@Mixin(DimensionSpecialEffects.class)
public class DimensionEffectsMixin {

    @Mixin(DimensionSpecialEffects.NetherEffects.class)
    public static class NetherMixin {
        @Inject(method = "getBrightnessDependentFogColor", at = @At(value = "RETURN"), cancellable = true)
        private void inject$brightFogColor(CallbackInfoReturnable<Vec3> cir) {
            if (ExtrasConfig.darknessMode.get() == ExtrasConfig.DarknessMode.OFF) return;
            if (!ExtrasConfig.darknessOnNetherCache) return;

            cir.setReturnValue(DarknessPlus.getDarkFogColor(
                    cir.getReturnValue(),
                    ExtrasConfig.darknessNetherFogBrightCache)
            );
        }
    }

    @Mixin(DimensionSpecialEffects.EndEffects.class)
    public static class EndMixin {
        @Inject(method = "getBrightnessDependentFogColor", at = @At(value = "RETURN"), cancellable = true)
        private void inject$brightFogColor(CallbackInfoReturnable<Vec3> cir) {
            if (ExtrasConfig.darknessMode.get() == ExtrasConfig.DarknessMode.OFF) return;
            if (!ExtrasConfig.darknessOnEndCache) return;

            cir.setReturnValue(DarknessPlus.getDarkFogColor(
                    cir.getReturnValue(),
                    ExtrasConfig.darknessEndFogBrightCache)
            );
        }
    }
}
