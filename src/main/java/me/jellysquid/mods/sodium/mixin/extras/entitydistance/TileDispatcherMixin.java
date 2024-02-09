package me.jellysquid.mods.sodium.mixin.extras.entitydistance;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo; 
import toni.xenon.extras.ExtrasConfig;
import toni.xenon.extras.ExtrasTools;
import toni.xenon.extras.entitydistance.IWhitelistCheck;

@Mixin(BlockEntityRenderDispatcher.class)
public class TileDispatcherMixin {
    @Shadow public Camera camera;

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public <E extends BlockEntity> void render(E tile, float val, PoseStack matrix, MultiBufferSource bufferSource, CallbackInfo ci) {
        if (!ExtrasConfig.tileEntityDistanceCullingCache) return;

        if (!((IWhitelistCheck) tile.getType()).embPlus$isAllowed() && !ExtrasTools.isEntityWithinDistance(
                tile.getBlockPos(),
                camera.getPosition(),
                ExtrasConfig.tileEntityCullingDistanceYCache,
                ExtrasConfig.tileEntityCullingDistanceXCache
        )) {
            ci.cancel();
        }
    }
}