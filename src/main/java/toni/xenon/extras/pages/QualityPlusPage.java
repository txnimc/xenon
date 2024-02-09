package toni.xenon.extras.pages;

import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.network.chat.Component;
import toni.xenon.extras.ExtrasConfig;

import java.util.ArrayList;
import java.util.List;

public class QualityPlusPage extends OptionPage {
    private static final SodiumOptionsStorage qualityOptionsStorage = new SodiumOptionsStorage();

    public QualityPlusPage() {
        super(Component.translatable("sodium.options.pages.quality").append("++"), create());
    }

    private static ImmutableList<OptionGroup> create() {
        final List<OptionGroup> groups = new ArrayList<>();

        final var fog = OptionImpl.createBuilder(boolean.class, qualityOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.fog.title"))
                .setTooltip(Component.translatable("xenon.extras.options.fog.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> {
                            ExtrasConfig.fog.set(value);
                            ExtrasConfig.fogCache = value;
                        },
                        (options) -> ExtrasConfig.fogCache)
                .setImpact(OptionImpact.LOW)
                .build();

        final var fadeInQuality = OptionImpl.createBuilder(ExtrasConfig.ChunkFadeSpeed.class, qualityOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.fadein.title"))
                .setTooltip(Component.translatable("xenon.extras.options.fadein.desc"))
                .setControl((option) -> new CyclingControl<>(option, ExtrasConfig.ChunkFadeSpeed.class, new Component[]{
                        Component.translatable("options.off"),
                        Component.translatable("options.graphics.fast"),
                        Component.translatable("options.graphics.fancy")
                }))
                .setBinding((opts, value) -> ExtrasConfig.chunkFadeSpeed.set(value),
                        (opts) -> ExtrasConfig.chunkFadeSpeed.get())
                .setImpact(OptionImpact.LOW)
                .setEnabled(false)
                .build();

        groups.add(OptionGroup.createBuilder()
                .add(fog)
                .add(fadeInQuality)
                .build()
        );

        final var cloudHeight = OptionImpl.createBuilder(int.class, qualityOptionsStorage)
                .setName(Component.translatable("xenon.extras.options.clouds.height.title"))
                .setTooltip(Component.translatable("xenon.extras.options.clouds.height.desc"))
                .setControl((option) -> new SliderControl(option, 64, 364, 4, ControlValueFormatter.biomeBlend()))
                .setBinding((options, value) -> {
                            ExtrasConfig.cloudsHeight.set(value);
                            ExtrasConfig.cloudsHeightCache = value;
                        },
                        (options) -> ExtrasConfig.cloudsHeightCache)
                .build();

        groups.add(OptionGroup.createBuilder()
                .add(cloudHeight)
                .build()
        );

        return ImmutableList.copyOf(groups);
    }
}
