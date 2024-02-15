package org.embeddedt.embeddium.extras.pages;

import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.network.chat.Component;
import org.embeddedt.embeddium.extras.ExtrasConfig;

import java.util.ArrayList;
import java.util.List;

public class OthersPage extends OptionPage {
    private static final SodiumOptionsStorage mixinsOptionsStorage = new SodiumOptionsStorage();

    public OthersPage() {
        super(Component.translatable("xenon.extras.options.others.page"), create());
    }

    private static ImmutableList<OptionGroup> create() {
        final List<OptionGroup> groups = new ArrayList<>();

        groups.add(OptionGroup.createBuilder()
                .add(OptionImpl.createBuilder(ExtrasConfig.AttachMode.class, mixinsOptionsStorage)
                        .setName(Component.translatable("xenon.extras.options.others.borderless.attachmode.title"))
                        .setTooltip(Component.translatable("xenon.extras.options.others.borderless.attachmode.desc"))
                        .setControl(option -> new CyclingControl<>(option, ExtrasConfig.AttachMode.class, new Component[] {
                                Component.translatable("xenon.extras.options.common.attach"),
                                Component.translatable("xenon.extras.options.common.replace"),
                                Component.translatable("xenon.extras.options.common.off")
                        }))
                        .setBinding((options, value) -> ExtrasConfig.borderlessAttachModeF11.set(value),
                                (options) -> ExtrasConfig.borderlessAttachModeF11.get())
                        .build())
                .build()
        );

        return ImmutableList.copyOf(groups);
    }
}
