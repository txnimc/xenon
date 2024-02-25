package org.embeddedt.embeddium.extras;

import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import me.jellysquid.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ExtrasOptions {
    public static Option<ExtrasConfig.FullScreenMode> getFullscreenOption(MinecraftOptionsStorage options) {
        return OptionImpl.createBuilder(ExtrasConfig.FullScreenMode.class, options)
                .setName(Component.translatable("xenon.extras.options.screen.title"))
                .setTooltip(Component.translatable("xenon.extras.options.screen.desc"))
                .setControl((opt) -> new CyclingControl<>(opt, ExtrasConfig.FullScreenMode.class, new Component[] {
                        Component.translatable("xenon.extras.options.screen.windowed"),
                        Component.translatable("xenon.extras.options.screen.borderless"),
                        Component.translatable("options.fullscreen")
                }))
                .setBinding(ExtrasConfig::setFullScreenMode, (opts) -> ExtrasConfig.fullScreen.get()).build();
    }


    public static void setFPSOptions(List<OptionGroup> groups, SodiumOptionsStorage sodiumOpts) {
        var builder = OptionGroup.createBuilder();

        builder.add(OptionImpl.createBuilder(ExtrasConfig.FPSDisplayMode.class, sodiumOpts)
                .setName(Component.translatable("xenon.extras.options.displayfps.title"))
                .setTooltip(Component.translatable("xenon.extras.options.displayfps.desc"))
                .setControl((option) -> new CyclingControl<>(option, ExtrasConfig.FPSDisplayMode.class, new Component[]{
                        Component.translatable("xenon.extras.options.common.off"),
                        Component.translatable("xenon.extras.options.common.simple"),
                        Component.translatable("xenon.extras.options.common.advanced"),
                        Component.translatable("xenon.extras.options.common.frametime")
                }))
                .setBinding(
                        (opts, value) -> ExtrasConfig.fpsDisplayMode.set(value),
                        (opts) -> ExtrasConfig.fpsDisplayMode.get())
                .setImpact(OptionImpact.LOW)
                .build()
        );

        builder.add(OptionImpl.createBuilder(ExtrasConfig.FPSDisplaySystemMode.class, sodiumOpts)
                .setName(Component.translatable("xenon.extras.options.displayfps.system.title"))
                .setTooltip(Component.translatable("xenon.extras.options.displayfps.system.desc"))
                .setControl((option) -> new CyclingControl<>(option, ExtrasConfig.FPSDisplaySystemMode.class, new Component[]{
                        Component.translatable("xenon.extras.options.common.off"),
                        Component.translatable("xenon.extras.options.common.on"),
                        Component.translatable("xenon.extras.options.displayfps.system.ram")
                }))
                .setBinding((options, value) -> ExtrasConfig.fpsDisplaySystemMode.set(value),
                        (options) -> ExtrasConfig.fpsDisplaySystemMode.get())
                .build()
        );

        var components = new Component[ExtrasConfig.FPSDisplayGravity.values().length];
        for (int i = 0; i < components.length; i++) {
            components[i] = Component.translatable("xenon.extras.options.displayfps.gravity." + ExtrasConfig.FPSDisplayGravity.values()[i].name().toLowerCase());
        }

        builder.add(OptionImpl.createBuilder(ExtrasConfig.FPSDisplayGravity.class, sodiumOpts)
                .setName(Component.translatable("xenon.extras.options.displayfps.gravity.title"))
                .setTooltip(Component.translatable("xenon.extras.options.displayfps.gravity.desc"))
                .setControl((option) -> new CyclingControl<>(option, ExtrasConfig.FPSDisplayGravity.class, components))
                .setBinding(
                        (opts, value) -> ExtrasConfig.fpsDisplayGravity.set(value),
                        (opts) -> ExtrasConfig.fpsDisplayGravity.get())
                .build()
        );


        builder.add(OptionImpl.createBuilder(Integer.TYPE, sodiumOpts)
                .setName(Component.translatable("xenon.extras.options.displayfps.margin.title"))
                .setTooltip(Component.translatable("xenon.extras.options.displayfps.margin.desc"))
                .setControl((option) -> new SliderControl(option, 4, 64, 1, (v) -> Component.literal(v + "px")))
                .setImpact(OptionImpact.LOW)
                .setBinding(
                        (opts, value) -> {
                            ExtrasConfig.fpsDisplayMargin.set(value);
                            ExtrasConfig.fpsDisplayMarginCache = value;
                        },
                        (opts) -> ExtrasConfig.fpsDisplayMarginCache)
                .build()
        );

        builder.add(OptionImpl.createBuilder(boolean.class, sodiumOpts)
                .setName(Component.translatable("xenon.extras.options.displayfps.shadow.title"))
                .setTooltip(Component.translatable("xenon.extras.options.displayfps.shadow.desc"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> {
                            ExtrasConfig.fpsDisplayShadow.set(value);
                            ExtrasConfig.fpsDisplayShadowCache = value;
                        },
                        (options) -> ExtrasConfig.fpsDisplayShadowCache)
                .build()
        );

        groups.add(builder.build());
    }

    public static void setPerformanceOptions(List<OptionGroup> groups, SodiumOptionsStorage sodiumOpts) {
        var builder = OptionGroup.createBuilder();
        var fontShadow = OptionImpl.createBuilder(boolean.class, sodiumOpts)
                .setName(Component.translatable("xenon.extras.options.fontshadow.title"))
                .setTooltip(Component.translatable("xenon.extras.options.fontshadow.desc"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> {
                            ExtrasConfig.fontShadows.set(value);
                            ExtrasConfig.fontShadowsCache = value;
                        },
                        (options) -> ExtrasConfig.fontShadowsCache)
                .setImpact(OptionImpact.VARIES)
                .build();


        var hideJEI = OptionImpl.createBuilder(boolean.class, sodiumOpts)
                .setName(Component.translatable("xenon.extras.options.jei.title"))
                .setTooltip(Component.translatable("xenon.extras.options.jei.desc"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (options, value) -> {
                            ExtrasConfig.hideJREI.set(value);
                            ExtrasConfig.hideJREICache = value;
                        },
                        (options) -> ExtrasConfig.hideJREICache)
                .setImpact(OptionImpact.LOW)
                .setEnabled(ExtrasTools.isModInstalled("jei"))
                .build();

        builder.add(fontShadow);
        builder.add(hideJEI);

        groups.add(builder.build());
    }


}
