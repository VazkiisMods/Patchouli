package vazkii.patchouli.neoforge.client;

import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import vazkii.patchouli.xplat.IClientXplatAbstractions;

public class NeoForgeClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand) {
		final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		if (state.getRenderShape() != RenderShape.INVISIBLE) {
			final BakedModel model = blockRenderer.getBlockModel(state);
			for (RenderType layer : model.getRenderTypes(state, rand, ModelData.EMPTY)) {
				final VertexConsumer buffer = buffers.getBuffer(layer);
				blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, false, rand, ModelData.EMPTY, layer);
			}
		}
	}
}
