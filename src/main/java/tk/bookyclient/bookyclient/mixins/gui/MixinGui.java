package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (22:49 04.01.21)

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

import java.io.Serializable;

@Mixin(Gui.class)
public class MixinGui implements Serializable {
}