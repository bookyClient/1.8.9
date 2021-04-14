package tk.bookyclient.bookyclient.mixins.client;
// Created by booky10 in bookyClient (18:11 12.04.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor("defaultResourcePacks") List<IResourcePack> getDefaultResourcePacks();

    @Accessor("session") void setSession(Session session);
}