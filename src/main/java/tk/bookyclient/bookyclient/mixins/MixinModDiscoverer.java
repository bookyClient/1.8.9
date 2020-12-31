package tk.bookyclient.bookyclient.mixins;
// Created by booky10 in bookyClient (16:44 30.12.20)

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ContainerType;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.ModDiscoverer;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.BookyClientMod;
import tk.bookyclient.bookyclient.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;

@Mixin(ModDiscoverer.class)
public class MixinModDiscoverer {

    @Shadow
    private ASMDataTable dataTable;

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(CallbackInfo callbackInfo) {
        try {
            Field mcDirField = CoreModManager.class.getDeclaredField("mcDir");
            mcDirField.setAccessible(true);
            File mcDir = (File) mcDirField.get(null);

            BookyClientMod.mcDir=mcDir;
            for (String ignoredMod : CoreModManager.getIgnoredMods()) {
                if (!ignoredMod.startsWith(Constants.MOD_ID + "-" + Constants.VERSION)) continue;
                if (!ignoredMod.endsWith(".jar")) continue;
                Constants.LOGGER.info("Trying to inject bookyClient into mods...");

                File file = new File(mcDir + File.separator + "mods", ignoredMod);
                if (!file.exists()) file = new File(mcDir + File.separator + "mods" + File.separator + "1.8.9", ignoredMod);
                if (!file.exists()) throw new FileNotFoundException(ignoredMod + " has not been found!");

                ModCandidate candidate = new ModCandidate(file, file, ContainerType.JAR);
                candidate.explore(dataTable);
                Loader loader = Loader.instance();

                Field modsField = Loader.class.getDeclaredField("mods");
                modsField.setAccessible(true);
                List<ModContainer> mods = (List<ModContainer>) modsField.get(loader);

                mods.addAll(candidate.getContainedMods());
                modsField.set(loader, mods);
                Constants.LOGGER.info("Injected bookyClient into mods!");
                break;
            }
        } catch (Throwable throwable) {
            Constants.LOGGER.error("Error while injecting bookyClient into mods!", throwable);
        }
    }
}