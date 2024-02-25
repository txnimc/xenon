package org.embeddedt.embeddium.extras.fps;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.embeddedt.embeddium.extras.ExtrasConfig;
import org.embeddedt.embeddium.extras.ExtrasTools;

@Mod.EventBusSubscriber(modid = SodiumClientMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DebugOverlayEvent {
    private static final FPSDisplay DISPLAY = new FPSDisplay();

    private static final Component MSG_FPS = Component.translatable("xenon.extras.options.displayfps.fps");
    private static final Component MSG_MIN = Component.translatable("xenon.extras.options.displayfps.min");
    private static final Component MSG_AVG = Component.translatable("xenon.extras.options.displayfps.avg");
    private static final Component MSG_GPU = Component.translatable("xenon.extras.options.displayfps.gpu");
    private static final Component MSG_MEM = Component.translatable("xenon.extras.options.displayfps.mem");

    private static final AverageQueue AVERAGE = new AverageQueue();

    private static int fps = -1;
    private static int minFPS = -1;
    private static int lastAvgFps = -1; // NEEDED
    private static int avgFPS = -1;
    private static int gpuPercent = -1;
    private static int memUsage = -1;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderOverlayItem(RenderGuiOverlayEvent.Pre event) {
        if (!event.getOverlay().id().getPath().equals("debug_text")) return;

        // cancel rendering text if chart is displaying
        if (Minecraft.getInstance().options.renderFpsChart) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Pre event) {
        var minecraft = Minecraft.getInstance();
        renderFPSChar(minecraft, event.getGuiGraphics(), minecraft.font, event.getWindow().getGuiScale());
    }

    private static void renderFPSChar(Minecraft mc, GuiGraphics graphics, Font font, double scale) {
        if (mc.options.renderDebug || mc.options.renderFpsChart) return; // No render when F3 is open

        final var mode = ExtrasConfig.fpsDisplayMode.get();
        final var systemMode = ExtrasConfig.fpsDisplaySystemMode.get();

        if (mode.off() && systemMode.off()) return; // NOTHING TO DO HERE, BACK TO WORK

        DISPLAY.release();

        // FPS
        switch (mode) {
            case SIMPLE -> DISPLAY.append(calculateFPS$getColor(mc)).add(fix(fps)).add(" ").add(MSG_FPS.getString()).add(ChatFormatting.RESET);
            case FRAMETIME, ADVANCED -> {
                DISPLAY.append(calculateFPS$getColor(mc)).add(fix(fps)).add(ChatFormatting.RESET);
                DISPLAY.append(calculateMinFPS$getColor(mc)).add(MSG_MIN).add(" ").add(fix(minFPS)).add(ChatFormatting.RESET);
                DISPLAY.append(calculateAvgFPS$getColor(mc)).add(MSG_AVG).add(" ").add(fix(avgFPS)).add(ChatFormatting.RESET);
            }
        }
        if (!DISPLAY.isEmpty()) DISPLAY.split();

        // GPU AND RAM
        switch (systemMode) {
            //case GPU -> DISPLAY.append(calculateGPUPercent$getColor(mc)).add(MSG_GPU).add(" ").add(fix(gpuPercent)).add("%").add(ChatFormatting.RESET);
            case RAM -> DISPLAY.append(calculateMemPercent$getColor()).add(MSG_MEM).add(" ").add(fix(memUsage)).add("%").add(ChatFormatting.RESET);
            case ON -> {
//DISPLAY.append(calculateGPUPercent$getColor(mc)).add(MSG_GPU).add(" ").add(fix(gpuPercent)).add("%").add(ChatFormatting.RESET);
                DISPLAY.append(calculateMemPercent$getColor()).add(MSG_MEM).add(" ").add(fix(memUsage)).add("%").add(ChatFormatting.RESET);
            }
        }

        if (DISPLAY.isEmpty()) DISPLAY.add("FATAL ERROR");

        float margin = (scale > 0) ? ExtrasConfig.fpsDisplayMarginCache / (float) scale : ExtrasConfig.fpsDisplayMarginCache;

        // Prevent FPS-Display to render outside screenspace
        String displayString = DISPLAY.toString();
        float maxPosX = graphics.guiWidth() - font.width(displayString);
        float posX, posY;

        posX = switch (ExtrasConfig.fpsDisplayGravity.get()) {
            case LEFT -> margin;
            case CENTER -> (maxPosX / 2);
            case RIGHT -> maxPosX - margin;
        };
        posY = margin;

        graphics.pose().pushPose();
        if (ExtrasConfig.fpsDisplayShadowCache) {
            graphics.fill((int) posX - 2, (int) posY - 2, (int) posX + font.width(displayString) + 2, (int) (posY + font.lineHeight) + 1, -1873784752);
            graphics.flush();
        }

        graphics.drawString(font, displayString, posX, posY, 0xffffffff, true);

        if (mode == ExtrasConfig.FPSDisplayMode.FRAMETIME)
            drawChart(graphics, Minecraft.getInstance().getFrameTimer(), 0, (int) (graphics.guiWidth() / 5.5f), (int) (posY + font.lineHeight + 1));

        DISPLAY.release();
        graphics.pose().popPose();
    }


    private static void drawChart(GuiGraphics graphics, FrameTimer timer, int x, int width, int height) {
        var mc = Minecraft.getInstance();

        int logStart = timer.getLogStart();
        int logEnd = timer.getLogEnd();
        long[] along = timer.getLog();
        int xPos = x;
        int widthDiff = Math.max(0, along.length - width);
        int wrapped = timer.wrapIndex(logStart + widthDiff);

        int guiHeight = height;//graphics.guiHeight();

        while(wrapped != logEnd) {
            int barHeight = timer.scaleSampleTo(along[wrapped], 30, 60) / 4;
            int heightMax = 30;
            int color = getSampleColor(Mth.clamp(barHeight, 0, heightMax), 0, heightMax / 2, heightMax);
            graphics.fill(RenderType.guiOverlay(), xPos, guiHeight, xPos + 1, guiHeight + barHeight, color);

            ++xPos;
            wrapped = timer.wrapIndex(wrapped + 1);
        }

    }

    private static int getSampleColor(int height, int heightMin, int heightMid, int heightMax) {
        return height < heightMid
                ? colorLerp(1712789271, -1140870125, (float)height / (float)heightMid)
                : colorLerp(-1140870125, -1140908238, (float)(height - heightMid) / (float)(heightMax - heightMid));
    }

    private static int colorLerp(int col1, int col2, float factor) {
        int i = col1 >> 24 & 255;
        int j = col1 >> 16 & 255;
        int k = col1 >> 8 & 255;
        int l = col1 & 255;
        int m = col2 >> 24 & 255;
        int n = col2 >> 16 & 255;
        int o = col2 >> 8 & 255;
        int p = col2 & 255;
        int q = Mth.clamp((int)Mth.lerp(factor, (float)i, (float)m), 0, 255);
        int r = Mth.clamp((int)Mth.lerp(factor, (float)j, (float)n), 0, 255);
        int s = Mth.clamp((int)Mth.lerp(factor, (float)k, (float)o), 0, 255);
        int t = Mth.clamp((int)Mth.lerp(factor, (float)l, (float)p), 0, 255);
        return q << 24 | r << 16 | s << 8 | t;
    }

    private static ChatFormatting calculateFPS$getColor(Minecraft mc) {
        fps = mc.getFps();
        return ExtrasTools.colorByLow(fps);
    }

    private static ChatFormatting calculateMinFPS$getColor(Minecraft mc) {
        FrameTimer timer = mc.getFrameTimer();

        int start = timer.getLogStart();
        int end = timer.getLogEnd();

        if (end == start) return ExtrasTools.colorByLow(minFPS);

        int fps = mc.getFps();
        if (fps <= 0) fps = 1;

        long[] frames = timer.getLog();
        long maxNS = (long) (1 / (double) fps * 1000000000);
        long totalNS = 0;

        int index = Math.floorMod(end - 1, frames.length);
        while (index != start && (double) totalNS < 1000000000) {
            long timeNs = frames[index];
            if (timeNs > maxNS) {
                maxNS = timeNs;
            }

            totalNS += timeNs;
            index = Math.floorMod(index - 1, frames.length);
        }

        minFPS = (int) (1 / ((double) maxNS / 1000000000));
        return ExtrasTools.colorByLow(minFPS);
    }

    private static ChatFormatting calculateAvgFPS$getColor(Minecraft mc) {
        if (mc.getFps() != lastAvgFps) { // DON'T BLOOD AVG
            AVERAGE.push(lastAvgFps = mc.getFps());
            avgFPS = AVERAGE.calculate();
        }
        return ExtrasTools.colorByLow(avgFPS);
    }

//    private static ChatFormatting calculateGPUPercent$getColor(Minecraft mc) {
//        int value = (int) ((IUsageGPU) mc).embPlus$getSyncGpu();
//        gpuPercent = (value > 0) ? Math.min(value, 100) : -1;
//        return ExtrasTools.colorByPercent(gpuPercent);
//    }

    private static ChatFormatting calculateMemPercent$getColor() {
        memUsage = (int) ((ExtrasTools.ramUsed() * 100) / Runtime.getRuntime().maxMemory());
        return ExtrasTools.colorByPercent(memUsage);
    }

    private static String fix(int value) {
        return (value == -1) ? "--" : "" + value;
    }

    public static class AverageQueue {
        private final int[] QUEUE = new int[14];
        private int used = 0;

        void push(int value) {
            if (used == QUEUE.length) used = 0;
            QUEUE[used] = value;
            used++;
        }

        int calculate() {
            int times = 0;
            for (int i = 0; i < used; i++) {
                times += QUEUE[i];
            }

            return times / used;
        }
    }
}