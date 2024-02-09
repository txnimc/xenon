package me.jellysquid.mods.sodium.mixin.extras.entitydistance;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import toni.xenon.extras.ExtrasConfig;
import toni.xenon.extras.ExtrasTools;
import toni.xenon.extras.entitydistance.IWhitelistCheck;

import javax.annotation.Nullable;


@Mixin(BlockEntityType.class)
public abstract class TileEntityTypeMixin implements IWhitelistCheck {
    @Unique private static final Marker e$IT = MarkerManager.getMarker("BlockEntityType");
    @Unique private boolean embPlus$checked = false;
    @Unique private boolean embPlus$whitelisted = false;

    @Override
    public boolean embPlus$isAllowed() {
        if (embPlus$checked) return embPlus$whitelisted;

        var resource = getKey(embPlus$cast());
        this.embPlus$whitelisted = ExtrasTools.isWhitelisted(resource, ExtrasConfig.tileEntityWhitelist);
        this.embPlus$checked = true;

        SodiumClientMod.logger().debug("Whitelist checked for {}", resource.toString());
        return embPlus$whitelisted;
    }

    @Shadow @Nullable public static ResourceLocation getKey(BlockEntityType<?> pBlockEntityType) {
        throw new UnsupportedOperationException("stub!");
    }

    @Unique
    private BlockEntityType<?> embPlus$cast() {
        return (BlockEntityType<?>) ((Object) this);
    }
}
