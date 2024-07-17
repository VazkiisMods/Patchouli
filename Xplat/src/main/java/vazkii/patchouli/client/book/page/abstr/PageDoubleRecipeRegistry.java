package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;

import org.jetbrains.annotations.Nullable;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>> extends PageDoubleRecipe<T> {
	private final RecipeType<? extends T> recipeType;

	public PageDoubleRecipeRegistry(RecipeType<? extends T> recipeType) {
		this.recipeType = recipeType;
	}

	@Nullable
	private T getRecipe(Level level, ResourceLocation id) {
		RecipeManager manager = level.getRecipeManager();
		var recipeHolder = manager.byKey(id).filter(recipe -> recipe.value().getType() == recipeType).orElse(null);
		return recipeHolder != null ? (T) recipeHolder.value() : null;
	}

	@Override
	protected T loadRecipe(Level level, BookContentsBuilder builder, BookEntry entry, ResourceLocation res) {
		if (res == null || level == null) {
			return null;
		}

		T tempRecipe = getRecipe(level, res);
		if (tempRecipe == null) { // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
			tempRecipe = getRecipe(level, new ResourceLocation("crafttweaker", res.getPath()));
		}

		if (tempRecipe != null) {
			entry.addRelevantStack(builder, tempRecipe.getResultItem(level.registryAccess()), pageNum);
			return tempRecipe;
		}

		PatchouliAPI.LOGGER.warn("Recipe {} (of type {}) not found", res, BuiltInRegistries.RECIPE_TYPE.getKey(recipeType));
		return null;
	}

}
