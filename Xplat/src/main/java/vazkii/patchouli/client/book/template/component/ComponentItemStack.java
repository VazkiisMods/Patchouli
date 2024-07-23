package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.function.UnaryOperator;

public class ComponentItemStack extends TemplateComponent {

	public IVariable item;

	private boolean framed;
	@SerializedName("link_recipe") private boolean linkedRecipe;

	private transient ItemStack[] items;

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		if (linkedRecipe) {
			for (ItemStack stack : items) {
				entry.addRelevantStack(builder, stack, pageNum);
			}
		}
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
		super.onVariablesAvailable(lookup, registries);
		items = lookup.apply(item).as(ItemStack[].class);
	}

	@Override
	public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
		if (items.length == 0) {
			return;
		}

		if (framed) {
			RenderSystem.enableBlend();
			graphics.setColor(1F, 1F, 1F, 1F);
			graphics.blit(page.book.craftingTexture, x - 5, y - 5, 20, 102, 26, 26, 128, 256);
		}

		page.parent.renderItemStack(graphics, x, y, mouseX, mouseY, items[(page.parent.ticksInBook / 20) % items.length]);
	}

}
