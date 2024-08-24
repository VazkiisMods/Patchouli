package vazkii.patchouli.client.base;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class BookModel implements BakedModel {
	private final BakedModel original;
	private final ItemOverrides itemHandler;

	public BookModel(BakedModel original, Function<ResourceLocation, BakedModel> modelGetter) {
		this.original = original;

		this.itemHandler = new ItemOverrides(DummyModelBaker.INSTANCE, null, Collections.emptyList()) {
			@Override
			public BakedModel resolve(@NotNull BakedModel original, @NotNull ItemStack stack,
					@Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				Book book = ItemModBook.getBook(stack);
				if (book != null) {
					return modelGetter.apply(book.model);
				}
				return original;
			}
		};
	}

	@NotNull
	@Override
	public ItemOverrides getOverrides() {
		return itemHandler;
	}

	@NotNull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
		return original.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return original.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return original.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return original.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return original.isCustomRenderer();
	}

	@NotNull
	@Override
	public TextureAtlasSprite getParticleIcon() {
		return original.getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		return original.getTransforms();
	}

	private static class DummyModelBaker implements ModelBaker {
		static ModelBaker INSTANCE = new DummyModelBaker();

		// soft implement IModelBakerExtension
		public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
			return null;
		}

		// soft implement IModelBakerExtension
		public BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
			return null;
		}

		// soft implement IModelBakerExtension
		public BakedModel bakeUncached(UnbakedModel model, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
			return null;
		}

		// soft implement IModelBakerExtension
		public UnbakedModel getTopLevelModel(ModelResourceLocation location) {
			return null;
		}

		@Override
		public UnbakedModel getModel(ResourceLocation resourceLocation) {
			return null;
		}

		@Nullable
		@Override
		public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
			return null;
		}
	}
}
