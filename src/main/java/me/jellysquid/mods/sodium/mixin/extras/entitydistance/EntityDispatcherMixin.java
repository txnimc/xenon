package me.jellysquid.mods.sodium.mixin.extras.entitydistance;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
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

        var isHostile = entity instanceof Monster;

        if (!((IWhitelistCheck) entity.getType()).embPlus$isAllowed() && !ExtrasTools.isEntityWithinDistance(
                entity,
                cameraX,
                cameraY,
                cameraZ,
                isHostile ? ExtrasConfig.hostileDistanceYCache : ExtrasConfig.entityCullingDistanceYCache,
                isHostile ? ExtrasConfig.hostileDistanceXCache : ExtrasConfig.entityCullingDistanceXCache
        )) {
            cir.setReturnValue(false);
        }
    }
}