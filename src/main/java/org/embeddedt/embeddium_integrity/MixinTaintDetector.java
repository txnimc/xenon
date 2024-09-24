package org.embeddedt.embeddium_integrity;

import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.Restriction;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class detects mods that apply mixins to Embeddium internals, and applies various levels of enforcement,
 * depending on configuration (see {@link EnforceLevel}).
 * <p>
 * Any attempts by mods to disable this detection will result in increasingly strengthened measures, up to and including
 * an explicit incompatible dependency on that mod being added. Please use supported means to interact with Embeddium, and/or
 * contribute your own. That benefits the whole community, while writing a bespoke mixin only benefits you.
 */
public class MixinTaintDetector implements IExtension {

    /**
     * Mods which have injected into Embeddium with a mixin.
     */
    private static final Set<String> TAINTING_MODS = ConcurrentHashMap.newKeySet();

    public enum EnforceLevel {
        /**
         * Allow and do not report on any mixins being applied to Embeddium internals.
         */
        IGNORE,
        /**
         * Print a warning in the log when a mod applies mixins to Embeddium internals.
         */
        WARN,
        /**
         * Like {@link EnforceLevel#WARN}, but also throws an exception, which will usually result in the game crashing.
         * This is the strictest level of enforcement.
         */
        CRASH
    }


    public static Collection<String> getTaintingMods() {
        return Collections.unmodifiableCollection(TAINTING_MODS);
    }

    /**
     * Bootstrap the taint detector. This should be called from a mixin plugin before any mixins are applied.
     */
    public static void initialize() {

    }

    @Override
    public boolean checkActive(MixinEnvironment environment) {
        return true;
    }




    @Override
    public void preApply(ITargetClassContext context) {

    }

    @Override
    public void postApply(ITargetClassContext context) {

    }

    @Override
    public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {

    }
}
