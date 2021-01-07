package tk.bookyclient.bookyclient.mixins.forge;
// Created by booky10 in bookyClient (13:34 30.12.20)

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.List;

@Mixin(FMLCommonHandler.class)
public class MixinFMLCommonHandler {

    @Shadow(remap = false)
    private List<String> brandings;

    @SuppressWarnings("unused")
    @Shadow(remap = false)
    private List<String> brandingsNoMC;

    @Inject(method = "computeBranding", at = @At(value = "RETURN", shift = At.Shift.BY, by = -1), remap = false)
    public void onComputeBranding(CallbackInfo callbackInfo) {
        ImmutableList.Builder<String> brd = ImmutableList.builder();
        brd.addAll(brandings);
        brd.add("bookyClient " + Constants.VERSION + ", MC " + Minecraft.getMinecraft().getVersion());

        brandings = brd.build();
        brandingsNoMC = brandings.subList(1, brandings.size());
    }
}