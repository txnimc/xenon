package me.jellysquid.mods.sodium.mixin.extras.entitydistance;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.embeddedt.embeddium.extras.ExtrasConfig;
import org.embeddedt.embeddium.extras.ExtrasTools;
import org.embeddedt.embeddium.extras.entitydistance.IWhitelistCheck;

@Mixin(EntityRenderDispatcher.class)
public class EntityDispatcherMixin {
    @Inject(at = @At("HEAD"), method = "shouldRender", cancellable = true)
    public <E extends Entity> void inject$shouldRender(E entity, Frustum clippingHelper, double cameraX, double cameraY, double cameraZ, CallbackInfoReturnable<Boolean> cir) {
        if (!ExtrasConfig.entityDistanceCullingCache) return;

        if (!((IWhitelistCheck) entity.getType()).embPlus$isAllowed() && !ExtrasTools.isEntityWithinDistance(
                entity,
                cameraX,
                cameraY,
                cameraZ,
                ExtrasConfig.entityCullingDistanceYCache,
                ExtrasConfig.entityCullingDistanceXCache
        )) {
            cir.setReturnValue(false);
        }
    }
}