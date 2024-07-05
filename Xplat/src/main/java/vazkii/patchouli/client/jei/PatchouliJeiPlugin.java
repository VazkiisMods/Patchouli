package vazkii.patchouli.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.item.PatchouliDataComponents;
import vazkii.patchouli.common.item.PatchouliItems;
import vazkii.patchouli.mixin.client.AccessorKeyMapping;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@JeiPlugin
public class PatchouliJeiPlugin implements IModPlugin {
	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, PatchouliAPI.MOD_ID);

	private static final KeyMapping showRecipe, showUses;

	private static IJeiRuntime jeiRuntime;

	static {
		Map<String, KeyMapping> allKeyMappings = AccessorKeyMapping.getAllKeyMappings();
		showRecipe = allKeyMappings.get("key.jei.showRecipe");
		showUses = allKeyMappings.get("key.jei.showUses");
		if (showRecipe == null || showUses == null) {
			PatchouliAPI.LOGGER.warn("Could not locate JEI keybindings, lookups in books may not work");
		}
	}

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, PatchouliItems.BOOK, (stack, context) -> {
			if (!stack.has(PatchouliDataComponents.BOOK)) {
				return "";
			}
			return stack.get(PatchouliDataComponents.BOOK).toString();
		});
	}

	@Override
	public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
		PatchouliJeiPlugin.jeiRuntime = jeiRuntime;
	}

	public static boolean handleRecipeKeybind(int keyCode, int scanCode, ItemStack stack) {
		if (showRecipe != null && showRecipe.matches(keyCode, scanCode)) {
			var focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);
			jeiRuntime.getRecipesGui().show(focus);
			return true;
		}
		if (showUses != null && showUses.matches(keyCode, scanCode)) {
			var focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, stack);
			jeiRuntime.getRecipesGui().show(focus);
			return true;
		}
		return false;
	}
}
