package tk.bookyclient.bookyclient.mixins.forge;
// Created by booky10 in bookyClient (13:34 30.12.20)

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(FMLCommonHandler.class)
public class MixinFMLCommonHandler {

    @Redirect(remap = false, method = "computeBranding", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;build()Lcom/google/common/collect/ImmutableList;"))
    public ImmutableList<String> onModsAdd(ImmutableList.Builder<String> builder) {
        builder.add(String.format("%s %s (MC %s)", Constants.MOD_NAME, Constants.VERSION, Minecraft.getMinecraft().getVersion()));
        return builder.build();
    }
}