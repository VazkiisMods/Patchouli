package vazkii.patchouli.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
	@Inject(at = @At("RETURN"), method = "renderLevel")
	public void onRender(float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f pose, Matrix4f matrix4f, CallbackInfo info) {
		MultiblockVisualizationHandler.onWorldRenderLast(new PoseStack(), pose);
	}
}
