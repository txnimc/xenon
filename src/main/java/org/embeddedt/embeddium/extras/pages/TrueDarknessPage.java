package org.embeddedt.embeddium.extras.pages;

import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.network.chat.Component;
import org.embeddedt.embeddium.extras.ExtrasConfig;

import java.util.ArrayList;
import java.util.List;

public class TrueDarknessPage extends OptionPage {
    private static final SodiumOptionsStorage performanceOptionsStorage = new SodiumOptionsStorage();

    public TrueDarknessPage() {
        super(Component.translatable("xenon.extras.options.darkness.page"), create());
    }

    private static ImmutableList<OptionGroup> create() {
        final List<OptionGroup> groups = new ArrayList<>();

        // GENERAL ENABLE
        final var darknessMode = OptionImpl.createBuilder(ExtrasConfig.DarknessMode.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.mode.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.mode.desc"))
                .setControl((option) -> new CyclingControl<>(option, ExtrasConfig.DarknessMode.class, new Component[]{
                        Component.translatable("xenon.extras.options.darkness.mode.pitchblack"),
                        Component.translatable("xenon.extras.options.darkness.mode.reallydark"),
                        Component.translatable("xenon.extras.options.darkness.mode.dark"),
                        Component.translatable("xenon.extras.options.darkness.mode.dim"),
                        Component.translatable("options.off")
                }))
                .setBinding((opts, value) -> ExtrasConfig.darknessMode.set(value),
                        (opts) -> ExtrasConfig.darknessMode.get())
                .build();

        var noSkylight = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.noskylight.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.noskylight.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> {
                            ExtrasConfig.darknessOnNoSkyLight.set(value);
                            ExtrasConfig.darknessOnNoSkyLightCache = value;
                        },
                        (options) -> ExtrasConfig.darknessOnNoSkyLightCache)
                .build();

        groups.add(OptionGroup.createBuilder()
                .add(darknessMode)
                .add(noSkylight)
                .build()
        );

        // OVERWORLD
        var darknessOtherDim = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.others.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.others.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> {
                            ExtrasConfig.darknessByDefault.set(value);
                            ExtrasConfig.darknessByDefaultCache = value;
                        },
                        (options) -> ExtrasConfig.darknessByDefaultCache)
                .build();
        var darknessOnOverworld = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.overworld.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.overworld.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> {
                            ExtrasConfig.darknessOnOverworld.set(value);
                            ExtrasConfig.darknessOnOverworldCache = value;
                        },
                        (options) -> ExtrasConfig.darknessOnOverworldCache)
                .build();

        var darknessOnNether = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.nether.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.nether.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> {
                            ExtrasConfig.darknessOnNether.set(value);
                            ExtrasConfig.darknessOnNetherCache = value;
                        },
                        (options) -> ExtrasConfig.darknessOnNetherCache)
                .build();

        final var netherFogBright = OptionImpl.createBuilder(int.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.nether.brightness.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.nether.brightness.desc"))
                .setControl((option) -> new SliderControl(option, 0, 100, 1, ControlValueFormatter.percentage()))
                .setBinding((options, current) -> {
                            var value = current / 100d;
                            ExtrasConfig.darknessNetherFogBright.set(value);
                            ExtrasConfig.darknessNetherFogBrightCache = value;
                        },
                        (options) -> Math.toIntExact(Math.round(ExtrasConfig.darknessNetherFogBrightCache * 100)))
                .build();

        var darknessOnEnd = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.end.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.end.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> {
                            ExtrasConfig.darknessOnEnd.set(value);
                            ExtrasConfig.darknessOnEndCache = value;
                        },
                        (options) -> ExtrasConfig.darknessOnEndCache)
                .build();

        final var endFogBright = OptionImpl.createBuilder(int.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.end.brightness.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.end.brightness.desc"))
                .setControl((option) -> new SliderControl(option, 0, 100, 1, ControlValueFormatter.percentage()))
                .setBinding((options, current) -> {
                            var value = current / 100d;
                            ExtrasConfig.darknessEndFogBright.set(value);
                            ExtrasConfig.darknessEndFogBrightCache = value;
                        },
                        (options) -> Math.toIntExact(Math.round(ExtrasConfig.darknessEndFogBrightCache * 100)))
                .build();

        groups.add(OptionGroup.createBuilder()
                .add(darknessOtherDim)
                .add(darknessOnOverworld)
                .build()
        );

        groups.add(OptionGroup.createBuilder()
                .add(darknessOnNether)
                .add(netherFogBright)
                .build()
        );

        groups.add(OptionGroup.createBuilder()
                .add(darknessOnEnd)
                .add(endFogBright)
                .build()
        );

        var blockLightOnly = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.blocklightonly.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.blocklightonly.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> {
                            ExtrasConfig.darknessBlockLightOnly.set(value);
                            ExtrasConfig.darknessBlockLightOnlyCache = value;
                        },
                        (options) -> ExtrasConfig.darknessBlockLightOnlyCache)
                .setEnabled(false)
                .build();


        var affectedByMoonPhase = OptionImpl.createBuilder(boolean.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.moonphase.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.moonphase.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> {
                            ExtrasConfig.darknessAffectedByMoonPhase.set(value);
                            ExtrasConfig.darknessAffectedByMoonPhaseCache = value;
                        },
                        (options) -> ExtrasConfig.darknessAffectedByMoonPhaseCache)
                .build();

        final var newMoonBright = OptionImpl.createBuilder(int.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.moonphase.fresh.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.moonphase.fresh.desc"))
                .setControl((option) -> new SliderControl(option, 0, 100, 1, ControlValueFormatter.percentage()))
                .setBinding((options, current) -> {
                            var value = current / 100d;
                            ExtrasConfig.darknessNewMoonBright.set(value);
                            ExtrasConfig.darknessNewMoonBrightCache = value;
                        },
                        (options) -> Math.toIntExact(Math.round(ExtrasConfig.darknessNewMoonBrightCache * 100d)))
                .build();

        final var fullMoonBright = OptionImpl.createBuilder(int.class, performanceOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.darkness.moonphase.full.title"))
                .setTooltip(Component.translatable("xenon.extras.options.darkness.moonphase.full.desc"))
                .setControl((option) -> new SliderControl(option, 0, 100, 1, ControlValueFormatter.percentage()))
                .setBinding((options, current) -> {
                            var value = current / 100d;
                            ExtrasConfig.darknessFullMoonBright.set(value);
                            ExtrasConfig.darknessFullMoonBrightCache = value;
                        },
                        (options) -> Math.toIntExact(Math.round(ExtrasConfig.darknessFullMoonBrightCache * 100)))
                .build();

        groups.add(OptionGroup.createBuilder()
                .add(blockLightOnly)
                .add(affectedByMoonPhase)
                .add(newMoonBright)
                .add(fullMoonBright)
                .build()
        );


        return ImmutableList.copyOf(groups);
    }
}
