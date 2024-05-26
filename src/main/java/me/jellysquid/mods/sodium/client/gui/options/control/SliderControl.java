package me.jellysquid.mods.sodium.client.gui.options.control;

import com.mojang.blaze3d.platform.InputConstants;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.reesesoptions.client.gui.Point2i;
import me.jellysquid.mods.sodium.client.gui.reesesoptions.client.gui.SliderControlElementExtended;
import me.jellysquid.mods.sodium.client.util.Dim2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;

public class SliderControl implements Control<Integer> {
    private final Option<Integer> option;

    private final int min, max, interval;

    private final ControlValueFormatter mode;

    public SliderControl(Option<Integer> option, int min, int max, int interval, ControlValueFormatter mode) {
        Validate.isTrue(max > min, "The maximum value must be greater than the minimum value");
        Validate.isTrue(interval > 0, "The slider interval must be greater than zero");
        Validate.isTrue(((max - min) % interval) == 0, "The maximum value must be divisable by the interval");
        Validate.notNull(mode, "The slider mode must not be null");

        this.option = option;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.mode = mode;
    }

    @Override
    public ControlElement<Integer> createElement(Dim2i dim) {
        return new Button(this.option, dim, this.min, this.max, this.interval, this.mode);
    }

    @Override
    public Option<Integer> getOption() {
        return this.option;
    }

    @Override
    public int getMaxWidth() {
        return 130;
    }

    private static class Button extends ControlElement<Integer> implements SliderControlElementExtended {
        private static final int THUMB_WIDTH = 2, TRACK_HEIGHT = 1;

        private final Rect2i sliderBounds;
        private final ControlValueFormatter formatter;

        private final int min;
        private int max;
        private final int range;
        private final int interval;

        private double thumbPosition;

        private boolean sliderHeld;
        private boolean editMode;

        public Button(Option<Integer> option, Dim2i dim, int min, int max, int interval, ControlValueFormatter formatter) {
            super(option, dim);

            this.min = min;
            this.max = max;
            this.range = max - min;
            this.interval = interval;
            this.thumbPosition = this.getThumbPositionForValue(option.getValue());
            this.formatter = formatter;

            this.sliderBounds = new Rect2i(dim.getLimitX() - 96, dim.getCenterY() - 5, 90, 10);
            this.sliderHeld = false;
        }

        @Override
        public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
            super.render(drawContext, mouseX, mouseY, delta);

            if (this.option.isAvailable() && (this.hovered || this.isFocused())) {
                this.renderSlider(drawContext);
            } else {
                this.renderStandaloneValue(drawContext);
            }
        }

        private void renderStandaloneValue(GuiGraphics drawContext) {
            int sliderX = this.getSliderBounds().x();
            int sliderY = this.getSliderBounds().y();
            int sliderWidth = this.sliderBounds.getWidth();
            int sliderHeight = this.sliderBounds.getHeight();

            Component label = this.formatter.format(this.option.getValue());
            int labelWidth = this.font.width(label);

            this.drawString(drawContext, label, sliderX + sliderWidth - labelWidth, sliderY + (sliderHeight / 2) - 4, 0xFFFFFFFF);
        }

        private void renderSlider(GuiGraphics drawContext) {
            int sliderX = this.getSliderBounds().x();
            int sliderY = this.getSliderBounds().y();
            int sliderWidth = this.sliderBounds.getWidth();
            int sliderHeight = this.sliderBounds.getHeight();

            this.thumbPosition = this.getThumbPositionForValue(this.option.getValue());

            double thumbOffset = Mth.clamp((double) (this.getIntValue() - this.min) / this.range * sliderWidth, 0, sliderWidth);

            int thumbX = (int) (sliderX + thumbOffset - THUMB_WIDTH);
            int trackY = (int) (sliderY + (sliderHeight / 2f) - ((double) TRACK_HEIGHT / 2));

            this.drawRect(drawContext, thumbX, sliderY, thumbX + (THUMB_WIDTH * 2), sliderY + sliderHeight, 0xFFFFFFFF);
            this.drawRect(drawContext, sliderX, trackY, sliderX + sliderWidth, trackY + TRACK_HEIGHT, 0xFFFFFFFF);

            String label = this.formatter.format(this.getIntValue()).getString();

            int labelWidth = this.font.width(label);

            this.drawString(drawContext, label, sliderX - labelWidth - 6, sliderY + (sliderHeight / 2) - 4, 0xFFFFFFFF);

            renderSliderExtended(drawContext);
        }

        public void renderSliderExtended(GuiGraphics drawContext) {
            int sliderX = this.getSliderBounds().x();
            int sliderY = this.getSliderBounds().y();
            int sliderWidth = this.getSliderBounds().width();
            int sliderHeight = this.getSliderBounds().height();
            this.thumbPosition = this.getThumbPositionForValue(this.option.getValue());
            double thumbOffset = Mth.clamp((double) (this.getIntValue() - this.min) / (double) this.range * (double) sliderWidth, 0.0, sliderWidth);
            double thumbX = (double) sliderX + thumbOffset - 2.0;
            if (this.isFocused() && this.isEditMode()) {
                this.drawRect(drawContext, (int) (thumbX - 1), sliderY - 1, (int) (thumbX + 5), sliderY + sliderHeight + 1, 0xFFFFFFFF);
            }
        }

        public int getIntValue() {
            return this.min + (this.interval * (int) Math.round(this.getSnappedThumbPosition() / this.interval));
        }

        public double getSnappedThumbPosition() {
            return this.thumbPosition / (1.0D / this.range);
        }

        public double getThumbPositionForValue(int value) {
            return (value - this.min) * (1.0D / this.range);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.sliderHeld = false;

            if (this.option.isAvailable() && button == 0 && this.dim.containsCursor(mouseX, mouseY)) {
                if (this.getSliderBounds().containsCursor((int) mouseX, (int) mouseY)) {
                    this.setValueFromMouse(mouseX);
                    this.sliderHeld = true;
                }

                return true;
            }

            return false;
        }

        private void setValueFromMouse(double d) {
            this.setValue((d - (double)this.getSliderBounds().x()) / (double)this.getSliderBounds().width());
        }

        public void setValue(double d) {
            this.thumbPosition = Mth.clamp(d, 0.0D, 1.0D);

            int value = this.getIntValue();

            if (this.option.getValue() != value) {
                this.option.setValue(value);
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!isFocused()) return false;

            if (keyCode == InputConstants.KEY_RETURN) {
                this.setEditMode(!this.isEditMode());;
                return true;
            }

            if (this.isEditMode()) {
                if (keyCode == InputConstants.KEY_LEFT) {
                    this.option.setValue(Mth.clamp(this.option.getValue() - interval, min, max));
                    return true;
                } else if (keyCode == InputConstants.KEY_RIGHT) {
                    this.option.setValue(Mth.clamp(this.option.getValue() + interval, min, max));
                    return true;
                }
            }

            return false;
//            if (!isFocused()) return false;
//
//            if (keyCode == InputConstants.KEY_LEFT) {
//                this.option.setValue(Mth.clamp(this.option.getValue() - this.interval, this.min, this.max));
//                return true;
//            } else if (keyCode == InputConstants.KEY_RIGHT) {
//                this.option.setValue(Mth.clamp(this.option.getValue() + this.interval, this.min, this.max));
//                return true;
//            }
//
//            return false;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (this.option.isAvailable() && button == 0) {
                if (this.sliderHeld) {
                    this.setValueFromMouse(mouseX);
                }

                return true;
            }

            return false;
        }

        private void setValueFromMouseScroll(double amount) {
            if (this.option.getValue() + this.interval * (int) amount <= this.max && this.option.getValue() + this.interval * (int) amount >= this.min) {
                this.option.setValue(this.option.getValue() + this.interval * (int) amount);
                this.thumbPosition = this.getThumbPositionForValue(this.option.getValue());
            }
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
            if (this.option.isAvailable() && this.getSliderBounds().containsCursor(mouseX, mouseY) && Screen.hasShiftDown()) {
                this.setValueFromMouseScroll(verticalAmount); // todo: horizontal separation

                return true;
            }

            return false;
        }

        private Dim2i getSliderBounds() {
            return new Dim2i(this.dim.getLimitX() - 96, this.dim.getCenterY() - 5, 90, 10);
        }

        @Override
        public boolean isEditMode() {
            return this.editMode;
        }

        @Override
        public void setEditMode(boolean editMode) {
            this.editMode = editMode;
        }
    }

}
