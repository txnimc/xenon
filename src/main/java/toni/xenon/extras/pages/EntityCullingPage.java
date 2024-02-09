package toni.xenon.extras.pages;

import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.network.chat.Component;
import toni.xenon.extras.ExtrasConfig;

import java.util.ArrayList;
import java.util.List;

public class EntityCullingPage extends OptionPage {
    private static final SodiumOptionsStorage performanceOptionsStorage = new SodiumOptionsStorage();

    public EntityCullingPage() {
        super(Component.translatable("xenon.extras.options.culling.page"), create());
    }

    private static ImmutableList<OptionGroup> create() {
        final List<OptionGroup> groups = new ArrayList<>();

        var enableDistanceChecks = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.culling.entity.title"))
                .setTooltip(Component.translatable("xenon.extras.options.culling.entity.desc"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> {
                            ExtrasConfig.entityDistanceCulling.set(value);
                            ExtrasConfig.entityDistanceCullingCache = value;
                        },
                        (options) -> ExtrasConfig.entityDistanceCullingCache)
                .setImpact(OptionImpact.HIGH)
                .build();

        var maxEntityDistance = OptionImpl.createBuilder(int.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.culling.entity.distance.horizontal.title"))
                .setTooltip(Component.translatable("xenon.extras.options.culling.entity.distance.horizontal.desc"))
                .setControl((option) -> new SliderControl(option, 16, 192, 8, ControlValueFormatter.biomeBlend()))
                .setBinding(
                        (options, value) -> {
                            int result = value * value;
                            ExtrasConfig.entityCullingDistanceX.set(result);
                            ExtrasConfig.entityCullingDistanceXCache = result;
                        },
                        (options) -> Math.toIntExact(Math.round(Math.sqrt(ExtrasConfig.entityCullingDistanceXCache))))
                .setImpact(OptionImpact.HIGH)
                .build();

        var maxEntityDistanceVertical = OptionImpl.createBuilder(int.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.culling.entity.distance.vertical.title"))
                .setTooltip(Component.translatable("xenon.extras.options.culling.entity.distance.vertical.desc"))
                .setControl((option) -> new SliderControl(option, 16, 64, 4, ControlValueFormatter.biomeBlend()))
                .setBinding(
                        (options, value) -> {
                            ExtrasConfig.entityCullingDistanceY.set(value);
                            ExtrasConfig.entityCullingDistanceYCache = value;
                        },
                        (options) -> ExtrasConfig.entityCullingDistanceYCache)
                .setImpact(OptionImpact.HIGH)
                .build();


        groups.add(OptionGroup
                .createBuilder()
                .add(enableDistanceChecks)
                .add(maxEntityDistance)
                .add(maxEntityDistanceVertical)
                .build()
        );


        var enableTileDistanceChecks = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.culling.tiles.title"))
                .setTooltip(Component.translatable("xenon.extras.options.culling.tiles.desc"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> {
                            ExtrasConfig.tileEntityDistanceCulling.set(value);
                            ExtrasConfig.tileEntityDistanceCullingCache = value;
                        },
                        (options) -> ExtrasConfig.tileEntityDistanceCullingCache)
                .setImpact(OptionImpact.HIGH)
                .build();


        var maxTileEntityDistance = OptionImpl.createBuilder(int.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.culling.tile.distance.horizontal.title"))
                .setTooltip(Component.translatable("xenon.extras.options.culling.tile.distance.horizontal.desc"))
                .setControl((option) -> new SliderControl(option, 16, 256, 8, ControlValueFormatter.biomeBlend()))
                .setBinding((options, value) -> {
                            int result = value * value;
                            ExtrasConfig.tileEntityCullingDistanceX.set(result);
                            ExtrasConfig.tileEntityCullingDistanceXCache = result;
                        },
                        (options) -> Math.toIntExact(Math.round(Math.sqrt(ExtrasConfig.tileEntityCullingDistanceXCache))))
                .setImpact(OptionImpact.HIGH)
                .build();

        var maxTileEntityDistanceVertical = OptionImpl.createBuilder(int.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.culling.tile.distance.vertical.title"))
                .setTooltip(Component.translatable("xenon.extras.options.culling.tile.distance.vertical.desc"))
                .setControl((option) -> new SliderControl(option, 16, 64, 4, ControlValueFormatter.biomeBlend()))
                .setBinding((options, value) -> {
                            ExtrasConfig.tileEntityCullingDistanceY.set(value);
                            ExtrasConfig.tileEntityCullingDistanceYCache = value;
                        },
                        (options) -> ExtrasConfig.tileEntityCullingDistanceYCache)
                .setImpact(OptionImpact.HIGH)
                .build();

        groups.add(OptionGroup
                .createBuilder()
                .add(enableTileDistanceChecks)
                .add(maxTileEntityDistance)
                .add(maxTileEntityDistanceVertical)
                .build()
        );

        return ImmutableList.copyOf(groups);
    }
}
