package toni.xenon.extras.fps;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FrameTimer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod; 
import toni.xenon.extras.ExtrasConfig;
import toni.xenon.extras.ExtrasTools;

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
            case ADVANCED -> {
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
        DISPLAY.release();
        graphics.pose().popPose();
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