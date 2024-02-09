package me.jellysquid.mods.sodium.client.gui.reesesoptions.client.gui.frame.tab;

import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.reesesoptions.client.gui.frame.AbstractFrame;
import me.jellysquid.mods.sodium.client.gui.reesesoptions.client.gui.frame.OptionPageFrame;
import me.jellysquid.mods.sodium.client.gui.reesesoptions.client.gui.frame.ScrollableFrame;
import me.jellysquid.mods.sodium.client.util.Dim2i;
import net.minecraft.network.chat.Component;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public record Tab<T extends AbstractFrame>(Component title, Function<Dim2i, T> frameFunction) {

    public static Builder<?> createBuilder() {
        return new Builder<>();
    }

    public Component getTitle() {
        return title;
    }

    public Function<Dim2i, T> getFrameFunction() {
        return this.frameFunction;
    }

    public static class Builder<T extends AbstractFrame> {
        private Component title;
        private Function<Dim2i, T> frameFunction;

        public Builder<T> setTitle(Component title) {
            this.title = title;
            return this;
        }

        public Builder<T> setFrameFunction(Function<Dim2i, T> frameFunction) {
            this.frameFunction = frameFunction;
            return this;
        }

        public Tab<T> build() {
            return new Tab<T>(this.title, this.frameFunction);
        }

        public Tab<ScrollableFrame> from(OptionPage page, AtomicReference<Integer> verticalScrollBarOffset) {
            return new Tab<>(page.getName(), dim2i -> ScrollableFrame
                    .createBuilder()
                    .setDimension(dim2i)
                    .setFrame(OptionPageFrame
                            .createBuilder()
                            .setDimension(new Dim2i(dim2i.x(), dim2i.y(), dim2i.width(), dim2i.height()))
                            .setOptionPage(page)
                            .build())
                    .setVerticalScrollBarOffset(verticalScrollBarOffset)
                    .build());
        }
    }
}