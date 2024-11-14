package vazkii.patchouli.client.book.page;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageDoubleRecipeRegistry;
import vazkii.patchouli.mixin.AccessorSmithingTransformRecipe;
import vazkii.patchouli.mixin.AccessorSmithingTrimRecipe;

public class PageSmithing extends PageDoubleRecipeRegistry<SmithingRecipe> {

	public PageSmithing() {
		super(RecipeType.SMITHING);
	}

	@Override
	protected void drawRecipe(GuiGraphics graphics, SmithingRecipe recipe, int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

		RenderSystem.enableBlend();
		graphics.blit(book.craftingTexture, recipeX, recipeY, 11, 135, 96, 43, 128, 256);
		parent.drawCenteredStringNoShadow(graphics, getTitle(second).getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, recipeY - 10, book.headerColor);

		parent.renderIngredient(graphics, recipeX + 4, recipeY + 4, mouseX, mouseY, getBase(recipe));
		parent.renderIngredient(graphics, recipeX + 4, recipeY + 23, mouseX, mouseY, getAddition(recipe));
		parent.renderIngredient(graphics, recipeX + 40, recipeY + 4, mouseX, mouseY, getTemplate(recipe));
		parent.renderItemStack(graphics, recipeX + 40, recipeY + 20, mouseX, mouseY, recipe.getToastSymbol());
		parent.renderItemStack(graphics, recipeX + 76, recipeY + 13, mouseX, mouseY, recipe.getResultItem(level.registryAccess()));
	}

	private Ingredient getBase(SmithingRecipe recipe) {
		if (recipe instanceof SmithingTrimRecipe) {
			return ((AccessorSmithingTrimRecipe) recipe).getBase();
		}
		if (recipe instanceof SmithingTransformRecipe) {
			return ((AccessorSmithingTransformRecipe) recipe).getBase();
		}
		return Ingredient.EMPTY;
	}

	private Ingredient getAddition(SmithingRecipe recipe) {
		if (recipe instanceof SmithingTrimRecipe) {
			return ((AccessorSmithingTrimRecipe) recipe).getAddition();
		}
		if (recipe instanceof SmithingTransformRecipe) {
			return ((AccessorSmithingTransformRecipe) recipe).getAddition();
		}
		return Ingredient.EMPTY;
	}

	private Ingredient getTemplate(SmithingRecipe recipe) {
		if (recipe instanceof SmithingTrimRecipe) {
			return ((AccessorSmithingTrimRecipe) recipe).getTemplate();
		}
		if (recipe instanceof SmithingTransformRecipe) {
			return ((AccessorSmithingTransformRecipe) recipe).getTemplate();
		}
		return Ingredient.EMPTY;
	}

	@Override
	protected ItemStack getRecipeOutput(Level level, SmithingRecipe recipe) {
		if (recipe == null || level == null) {
			return ItemStack.EMPTY;
		}

		return recipe.getResultItem(level.registryAccess());
	}

	@Override
	protected int getRecipeHeight() {
		return 60;
	}
}
