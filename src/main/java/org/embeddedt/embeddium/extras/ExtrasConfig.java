package org.embeddedt.embeddium.extras;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.mojang.blaze3d.platform.Window;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.mixin.extras.borderless.accessors.MainWindowAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = SodiumClientMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ExtrasConfig {
    public static final Marker IT = MarkerManager.getMarker("Config");

    public static final ForgeConfigSpec SPECS;

    // GENERAL
    public static final EnumValue<FullScreenMode> fullScreen;
    public static final EnumValue<FPSDisplayMode> fpsDisplayMode;
    public static final EnumValue<FPSDisplayGravity> fpsDisplayGravity;
    public static final EnumValue<FPSDisplaySystemMode> fpsDisplaySystemMode;
    public static final IntValue fpsDisplayMargin;
    public static final BooleanValue fpsDisplayShadow;
    public static volatile int fpsDisplayMarginCache;
    public static volatile boolean fpsDisplayShadowCache;

    // QUALITY
    public static final BooleanValue fog;
    public static final IntValue cloudsHeight;
    public static final EnumValue<ChunkFadeSpeed> chunkFadeSpeed;
    public static volatile boolean fogCache;
    public static volatile int cloudsHeightCache;

    // QUALITY: TRUE DARKNESS
    public static final EnumValue<DarknessMode> darknessMode;
    public static final BooleanValue darknessOnOverworld;
    public static final BooleanValue darknessOnNether;
    public static final DoubleValue darknessNetherFogBright;
    public static final BooleanValue darknessOnEnd;
    public static final DoubleValue darknessEndFogBright;
    public static final BooleanValue darknessByDefault;
    public static final ConfigValue<List<? extends String>> darknessDimensionWhiteList;
    public static final BooleanValue darknessOnNoSkyLight;
    public static final BooleanValue darknessBlockLightOnly;
    public static final BooleanValue darknessAffectedByMoonPhase;
    public static final DoubleValue darknessNewMoonBright;
    public static final DoubleValue darknessFullMoonBright;
    public static volatile boolean darknessOnOverworldCache = true;
    public static volatile boolean darknessOnNetherCache;
    public static volatile double darknessNetherFogBrightCache;
    public static volatile boolean darknessOnEndCache;
    public static volatile double darknessEndFogBrightCache;
    public static volatile boolean darknessByDefaultCache;
    public static volatile boolean darknessOnNoSkyLightCache;
    public static volatile boolean darknessBlockLightOnlyCache;
    public static volatile boolean darknessAffectedByMoonPhaseCache;
    public static volatile double darknessNewMoonBrightCache;
    public static volatile double darknessFullMoonBrightCache;

    // PERFORMANCE;
    public static final BooleanValue hideJREI;
    public static final BooleanValue fontShadows;
    public static volatile boolean hideJREICache;
    public static volatile boolean fontShadowsCache;
    public static final BooleanValue tileEntityDistanceCulling;
    public static final IntValue tileEntityCullingDistanceX;
    public static final IntValue tileEntityCullingDistanceY;
    public static final BooleanValue entityDistanceCulling;
    public static final IntValue hostileEntityModifier;
    public static final IntValue entityCullingDistanceX;
    public static final IntValue entityCullingDistanceY;
    public static final ConfigValue<List<? extends String>> entityWhitelist; // QUICK CHECK
    public static final ConfigValue<List<? extends String>> tileEntityWhitelist; // QUICK CHECK
    public static volatile boolean tileEntityDistanceCullingCache;
    public static volatile int tileEntityCullingDistanceXCache;
    public static volatile int tileEntityCullingDistanceYCache;
    public static volatile boolean entityDistanceCullingCache;
    public static volatile int entityCullingDistanceXCache;
    public static volatile int entityCullingDistanceYCache;
    public static volatile int hostileDistanceXCache;
    public static volatile int hostileDistanceYCache;

    // OTHERS
    public static final EnumValue<AttachMode> borderlessAttachModeF11;
    public static boolean fastLanguageReloadCache; // this theoretically wasn't needed,
    // but I am seeing people complaining why fast language reload wasn't cached
    // and takes 0.0001ms extra to update lang by an idiotic check

    public static volatile boolean dynLightsOnEntitiesCache;
    public static volatile boolean dynLightsOnTileEntitiesCache;
    public static volatile boolean dynLightsUpdateOnPositionChangeCache;

    private static final String[] DEFAULT_TILE_ENTITIES_WHITELIST = new String[] {
            "waterframes:*", // WaterFrames includes their own code
    };

    private static final String[] DEFAULT_ENTITIES_WHITELIST = new String[] {
            "minecraft:ghast",
            "minecraft:ender_dragon",
            "iceandfire:*",
            "create:*",
    };

    static {
        var BUILDER = new Builder();

        // xenonextras ->
        BUILDER.push("xenonextras");

        // xenonextras -> general ->
        BUILDER.push("general");
        fullScreen = BUILDER
                .comment("Set Fullscreen mode", "Borderless let you change between screens more faster and move your mouse across monitors")
                .defineEnum("fullscreen", FullScreenMode.WINDOWED);

        fpsDisplayMode = BUILDER
                .comment("Configure FPS Display mode", "Complete mode gives you min FPS count and average count")
                .defineEnum("fpsDisplay", FPSDisplayMode.FRAMETIME);

        fpsDisplayGravity = BUILDER
                .comment("Configure FPS Display gravity", "Places counter on specified corner of your screen")
                .defineEnum("fpsDisplayGravity", FPSDisplayGravity.LEFT);

        fpsDisplaySystemMode = BUILDER
                .comment("Shows GPU and memory usage onto FPS display")
                .defineEnum("fpsDisplaySystem", FPSDisplaySystemMode.OFF);

        fpsDisplayMargin = BUILDER
                .comment("Configure FPS Display margin", "Give some space between corner and text")
                .defineInRange("fpsDisplayMargin", 12, 0, 48);

        fpsDisplayShadow = BUILDER
                .comment("Toggle FPS Display shadow", "In case sometimes you can't see the text")
                .define("fpsDisplayShadow", false);

        // xenonextras ->
        BUILDER.pop();

        // xenonextras -> quality
        BUILDER.push("quality");
        fog = BUILDER
                .comment("Toggle fog feature", "Fog was a vanilla feature, toggling off may increases performance")
                .define("fog", true);
        cloudsHeight = BUILDER
                .comment("Raise clouds", "Modify clouds height perfect for a adaptative world experience")
                .defineInRange("cloudsHeight", 192, 0, 512);

        chunkFadeSpeed = BUILDER
                .comment("Chunks fade in speed", "This option doesn't affect performance, just changes speed")
                .defineEnum("chunkFadeSpeed", ChunkFadeSpeed.SLOW);

        // xenonextras -> quality -> darkness
        BUILDER.push("darkness");
        darknessMode = BUILDER
                .comment("Configure Darkness Mode", "Each config changes what is considered 'true darkness'")
                .defineEnum("mode", DarknessMode.OFF);

        darknessOnOverworld = BUILDER
                .comment("Toggle Darkness on Overworld dimension")
                .define("enableOnOverworld", true);

        darknessOnNether = BUILDER
                .comment("Toggle Darkness on Nether dimension")
                .define("enableOnNether", false);

        darknessNetherFogBright = BUILDER
                .comment("Configure fog brightness on nether when darkness is enabled")
                .defineInRange("netherFogBright", 0.5f, 0d, 1d);

        darknessOnEnd = BUILDER
                .comment("Toggle Darkness on End dimension")
                .define("enableOnEnd", false);

        darknessEndFogBright = BUILDER
                .comment("Configure fog brightness on nether when darkness is enabled")
                .defineInRange("endFogBright", 0.5f, 0d, 1d);

        darknessByDefault = BUILDER
                .comment("Toggle Darkness default mode for modded dimensions")
                .define("valueByDefault", false);

        darknessDimensionWhiteList = BUILDER
                .comment("List of all dimensions to use True Darkness", "This option overrides 'valueByDefault' state")
                .defineListAllowEmpty(Collections.singletonList("dimensionWhitelist"), Collections::emptyList, s -> s.toString().contains(":"));

        darknessOnNoSkyLight = BUILDER
                .comment("Toggle darkness when dimension has no SkyLight")
                .define("enableOnNoSkyLight", false);

        darknessBlockLightOnly = BUILDER
                .comment("Disables all bright sources of darkness like moon or fog", "Only affects darkness effect")
                .define("enableBlockLightOnly", false);

        darknessAffectedByMoonPhase = BUILDER
                .comment("Toggles if moon phases affects darkness in the overworld")
                .define("affectedByMoonPhase", true);

        darknessFullMoonBright = BUILDER
                .comment("Configure max moon brightness level with darkness")
                .defineInRange("fullMoonBright",0.25d, 0, 1d);

        darknessNewMoonBright = BUILDER
                .comment("Configure min moon brightness level with darkness")
                .defineInRange("newMoonBright",0, 0, 1d);


        // xenonextras ->
        BUILDER.pop(2);

        // xenonextras -> performance
        BUILDER.push("performance");

        hideJREI = BUILDER
                .comment("Toggles JREI item rendering until searching", "Increases performance a little bit and cleans your screen when you don't want to use it")
                .define("hideJREI", false);

        fontShadows = BUILDER
                .comment("Toggles Minecraft Fonts shadows", "Depending of the case may increase performance", "Gives a flat style text")
                .define("fontShadows", true);

        // xenonextras -> performance -> distanceCulling
        BUILDER.push("distanceCulling");

        // xenonextras -> performance -> distanceCulling -> tileEntities
        BUILDER.push("tileEntities");
        tileEntityDistanceCulling = BUILDER
                .comment("Toggles distance culling for Block Entities", "Maybe you use another mod for that :(")
                .define("enable", true);

        tileEntityCullingDistanceX = BUILDER
                .comment("Configure horizontal max distance before cull Block entities", "Value is squared, default was 64^2 (or 64x64)")
                .defineInRange("cullingMaxDistanceX", 4096, 0, Integer.MAX_VALUE);

        tileEntityCullingDistanceY = BUILDER
                .comment("Configure vertical max distance before cull Block entities", "Value is raw")
                .defineInRange("cullingMaxDistanceY", 32, 0, 512);

        tileEntityWhitelist = BUILDER
                .comment("List of all Block Entities to be ignored by distance culling", "Uses ResourceLocation to identify it", "Example 1: \"minecraft:chest\" - Ignores chests only", "Example 2: \"ae2:*\" - ignores all Block entities from Applied Energetics 2")
                .defineListAllowEmpty(Collections.singletonList("whitelist"), Arrays.asList(DEFAULT_TILE_ENTITIES_WHITELIST), s -> s.toString().contains(":"));

        // xenonextras -> performance -> distanceCulling ->
        BUILDER.pop();

        // xenonextras -> performance -> distanceCulling -> entities
        BUILDER.push("entities");
        entityDistanceCulling = BUILDER
                .comment("Toggles distance culling for entities")
                .define("enable", true);

        entityCullingDistanceX = BUILDER
                .comment("Configure horizontal max distance before cull entities", "Value is squared, default was 64^2 (or 64x64)")
                .defineInRange("cullingMaxDistanceX", 4096, 0, Integer.MAX_VALUE);

        entityCullingDistanceY = BUILDER
                .comment("Configure vertical max distance before cull entities", "Value is raw")
                .defineInRange("cullingMaxDistanceY", 32, 0, 512);

        hostileEntityModifier = BUILDER
                .comment("Configure modifier applied to hostile entities", "Value is raw, 50% - 200%")
                .defineInRange("hostileEntityModifier", 100, 25, 200);

        entityWhitelist = BUILDER
                .comment("List of all Entities to be ignored by distance culling", "Uses ResourceLocation to identify it", "Example 1: \"minecraft:bat\" - Ignores bats only", "Example 2: \"alexsmobs:*\" - ignores all entities for alexmobs mod")
                .defineListAllowEmpty(Collections.singletonList("whitelist"), Arrays.asList(DEFAULT_ENTITIES_WHITELIST), (s) -> s.toString().contains(":"));

        // xenonextras ->
        BUILDER.pop(3);

        // xenonextras -> others
        BUILDER.push("others");
        borderlessAttachModeF11 = BUILDER
                .comment("Configure if borderless fullscreen option should be attached to F11 or replace vanilla fullscreen")
                .defineEnum("borderlessAttachModeOnF11", AttachMode.ATTACH);

        BUILDER.pop();


        SPECS = BUILDER.build();
    }

    public static boolean isLoaded() {
        return SPECS.isLoaded();
    }

    public static void load() {
        if (isLoaded()) return;
        SodiumClientMod.logger().warn("Loading xenon++Config");

        // FORCE LOAD
        var path = FMLPaths.CONFIGDIR.get().resolve("xenon++.toml");
        try {
            final var configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

            configData.load();
            SPECS.setConfig(configData);
            updateCache(null);
        } catch (Exception e) {
            var file = path.toFile();
            if (!file.exists()) throw new RuntimeException("Failed to read configuration file");
            if (!file.delete()) throw new RuntimeException("Failed to remove corrupted configuration file");
            load();
        }
    }

    @SubscribeEvent
    public static void updateCache(ModConfigEvent ignored) {
        SodiumClientMod.logger().warn("Updating config cache");

        fpsDisplayMarginCache = fpsDisplayMargin.get();
        fpsDisplayShadowCache = fpsDisplayShadow.get();

        fogCache = fog.get();
        cloudsHeightCache = cloudsHeight.get();

        darknessOnOverworldCache = darknessOnOverworld.get();
        darknessOnNetherCache = darknessOnNether.get();
        darknessNetherFogBrightCache = darknessNetherFogBright.get();
        darknessOnEndCache = darknessOnEnd.get();
        darknessEndFogBrightCache = darknessEndFogBright.get();
        darknessByDefaultCache = darknessByDefault.get();
        darknessOnNoSkyLightCache = darknessOnNoSkyLight.get();
        darknessBlockLightOnlyCache = darknessBlockLightOnly.get();
        darknessAffectedByMoonPhaseCache = darknessAffectedByMoonPhase.get();
        darknessNewMoonBrightCache = darknessNewMoonBright.get();
        darknessFullMoonBrightCache = darknessFullMoonBright.get();

        hideJREICache = hideJREI.get();
        fontShadowsCache = fontShadows.get();

        tileEntityDistanceCullingCache = tileEntityDistanceCulling.get();
        tileEntityCullingDistanceXCache = tileEntityCullingDistanceX.get();
        tileEntityCullingDistanceYCache = tileEntityCullingDistanceY.get();
        entityDistanceCullingCache = entityDistanceCulling.get();
        entityCullingDistanceXCache = entityCullingDistanceX.get();
        entityCullingDistanceYCache = entityCullingDistanceY.get();

        var x = Mth.sqrt(entityCullingDistanceXCache) * (hostileEntityModifier.get() / 100f);
        hostileDistanceXCache = (int) (x * x);
        hostileDistanceYCache = (int) (entityCullingDistanceYCache * (hostileEntityModifier.get() / 100f));

        SodiumClientMod.logger().warn("Cache updated successfully");
    }

    public static void setFullScreenMode(Options opts, FullScreenMode value) {
        fullScreen.set(value);
        opts.fullscreen().set(value != FullScreenMode.WINDOWED);

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();

        if (window.isFullscreen() != opts.fullscreen().get()) {
            window.toggleFullScreen();
            opts.fullscreen().set(window.isFullscreen());
        }

        if (opts.fullscreen().get()) {
            ((MainWindowAccessor) (Object) window).setDirty(true);
            window.changeFullscreenVideoMode();
        }
    }

    public enum AttachMode {
        ATTACH, REPLACE, OFF;
    }

    /* CONFIG VALUES */
    public enum FPSDisplayMode {
        OFF, SIMPLE, ADVANCED, FRAMETIME;

        public boolean off() {
            return this == OFF;
        }
    }
    public enum FPSDisplayGravity { LEFT, CENTER, RIGHT; }
    public enum ChunkFadeSpeed { OFF, FAST, SLOW; }
    public enum FPSDisplaySystemMode {
        OFF, ON, RAM;

        public boolean ram() { return this == RAM || this == ON; }
        public boolean off() { return this == OFF; }
    }
    public enum DynLightsSpeed {
        OFF(-1),
        SLOW(750),
        NORMAL(500),
        FAST(250),
        SUPERFAST(100),
        FASTESTS(50),
        REALTIME(-1);
        private final int delay;

        DynLightsSpeed(int delay) {
            this.delay = delay;
        }
        public int getDelay() { return delay; }

        public boolean off() {
            return this == OFF;
        }
    }
    public enum DarknessMode {
        TOTAL_DARKNESS(0.04f),
        PITCH_BLACK(0f),
        DARK(0.08f),
        DIM(0.12f),
        OFF(-1);

        public final float value;
        DarknessMode(float value) { this.value = value; }
    }
    public enum FullScreenMode {
        WINDOWED, BORDERLESS, FULLSCREEN;

        public static FullScreenMode nextOf(FullScreenMode current) {
            return switch (current) {
                case WINDOWED -> BORDERLESS;
                case BORDERLESS -> FULLSCREEN;
                case FULLSCREEN -> WINDOWED;
            };
        }

        public static FullScreenMode nextBorderless(FullScreenMode current) {
            return switch (current) {
                case FULLSCREEN, BORDERLESS -> WINDOWED;
                case WINDOWED -> BORDERLESS;
            };
        }

        public static FullScreenMode nextFullscreen(FullScreenMode current) {
            return switch (current) {
                case FULLSCREEN, BORDERLESS -> WINDOWED;
                case WINDOWED -> FULLSCREEN;
            };
        }

        public static FullScreenMode getVanillaConfig() {
            return Minecraft.getInstance().options.fullscreen().get() ? BORDERLESS : WINDOWED;
        }

        public boolean isBorderless() {
            return this == BORDERLESS;
        }
    }
}
