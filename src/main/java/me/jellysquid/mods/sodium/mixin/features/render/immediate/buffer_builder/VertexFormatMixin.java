package me.jellysquid.mods.sodium.mixin.features.render.immediate.buffer_builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import me.jellysquid.mods.sodium.client.buffer.ExtendedVertexFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Thanks to Maximum for this optimization, taken from Fireblanket.
 */
@Mixin(VertexFormat.class)
public class VertexFormatMixin implements ExtendedVertexFormat {
    @Shadow
    @Final
    private ImmutableList<VertexFormatElement> elements;

    private ExtendedVertexFormat.Element[] xenon$extendedElements;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void xenon$createElementArray(ImmutableMap<String, VertexFormatElement> immutableList, CallbackInfo ci) {
        this.xenon$extendedElements = new ExtendedVertexFormat.Element[this.elements.size()];

        if (this.elements.size() == 0)
            return; // prevent crash with mods that create empty VertexFormats

        VertexFormatElement currentElement = elements.get(0);
        int id = 0;
        for (VertexFormatElement element : this.elements) {
            if (element.getUsage() == VertexFormatElement.Usage.PADDING) continue;

            int oldId = id;
            int byteLength = 0;

            do {
                if (++id >= this.xenon$extendedElements.length)
                    id -= this.xenon$extendedElements.length;
                byteLength += currentElement.getByteSize();
                currentElement = this.elements.get(id);
            } while (currentElement.getUsage() == VertexFormatElement.Usage.PADDING);

            this.xenon$extendedElements[oldId] = new ExtendedVertexFormat.Element(element, id - oldId, byteLength);
        }
    }

    @Override
    public ExtendedVertexFormat.Element[] xenon$getExtendedElements() {
        return this.xenon$extendedElements;
    }
}
