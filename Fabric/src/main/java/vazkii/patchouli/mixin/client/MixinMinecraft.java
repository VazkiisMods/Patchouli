package vazkii.patchouli.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	@Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;)V")
	public void patchouli_onLogout(Screen screen, CallbackInfo info) {
		ClientAdvancements.playerLogout();
	}

}
