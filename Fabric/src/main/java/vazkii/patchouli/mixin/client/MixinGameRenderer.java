package vazkii.patchouli.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientTicker;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	@Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/DeltaTracker;Z)V")
	public void patchouli_renderStart(DeltaTracker deltaTracker, boolean tick, CallbackInfo info) {
		ClientTicker.renderTickStart(deltaTracker.getGameTimeDeltaPartialTick(false));
	}

	@Inject(at = @At("RETURN"), method = "render(Lnet/minecraft/client/DeltaTracker;Z)V")
	public void patchouli_renderEnd(DeltaTracker deltaTracker, boolean tick, CallbackInfo info) {
		ClientTicker.renderTickEnd();
	}

}
