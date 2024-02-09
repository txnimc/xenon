package me.jellysquid.mods.sodium.mixin.extras.borderless.accessors;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Window.class)
public interface MainWindowAccessor {
    @Accessor
    void setDirty(boolean value);
}