package org.embeddedt.embeddium.extras.leafculling;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.LeavesBlock;

public class LeafCulling {
    private static final Direction[] VALUES = Direction.values();

    public static boolean isFacingAir(BlockGetter view, BlockPos pos, Direction facing) {
        var vec = facing.getNormal();
        return view.getBlockState(pos.offset(vec)).getBlock() instanceof AirBlock;
    }

    public static boolean surroundedByLeaves(BlockGetter view, BlockPos pos) {
        var isAggressiveMode = SodiumClientMod.options().performance.leafCullingQuality == SodiumGameOptions.LeafCullingQuality.SOLID_AGGRESSIVE;
        for (Direction dir : VALUES) {
            if (isAggressiveMode && (dir == Direction.DOWN || dir == Direction.UP))
                continue;

            var dirPos = pos.offset(dir.getNormal());
            var blockstate = view.getBlockState(dirPos);
            if (blockstate.getBlock() instanceof LeavesBlock)
                continue;

            if (blockstate.isSolidRender(view, pos))
                continue;

            return false;
        }

        return true;
    }

    public static boolean shouldCullSide(BlockGetter view, BlockPos pos, Direction facing, int depth) {


        if (isFacingAir(view, pos, facing))
            return false;

        var vec = facing.getNormal();
        var cull = true;
        for (int i = 1; i <= depth; i++) {
            var state = view.getBlockState(pos.offset(vec.multiply(i)));
            cull &= state != null && state.getBlock() instanceof LeavesBlock;
        }
        return cull;

//        var normal = facing.getNormal();
//        var vec = pos.offset(normal);
//
//
//        boolean hasAirBlock = false;
//        for (Direction dir : Direction.values()) {
//            var dirPos = vec.offset(dir.getNormal());
//            if (!view.getBlockState(dirPos).isSolidRender(view, dirPos)) {
//                hasAirBlock = true;
//                break;
//            }
//        }
//
//        return !hasAirBlock;
    }
}
