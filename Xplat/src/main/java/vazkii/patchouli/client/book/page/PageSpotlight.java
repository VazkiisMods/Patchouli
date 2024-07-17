package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageSpotlight extends PageWithText {

	IVariable item;
	String title;
	@SerializedName("link_recipe") boolean linkRecipe;

	transient ItemStack[] stacks;

	@Override
	public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(level, entry, builder, pageNum);
		stacks = item.as(ItemStack[].class);

		if (linkRecipe) {
			for (ItemStack stack : stacks) {
				entry.addRelevantStack(builder, stack, pageNum);
			}
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {
		int w = 66;
		int h = 26;

		RenderSystem.enableBlend();
		graphics.blit(book.craftingTexture, GuiBook.PAGE_WIDTH / 2 - w / 2, 10, 0, 128 - h, w, h, 128, 256);

		Component toDraw;
		if (title != null && !title.isEmpty()) {
			toDraw = i18nText(title);
		} else {
			toDraw = stacks[0].getHoverName();
		}

		parent.drawCenteredStringNoShadow(graphics, toDraw.getVisualOrderText(), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
		if (stacks.length > 0) {
			parent.renderItemStack(graphics, GuiBook.PAGE_WIDTH / 2 - 8, 15, mouseX, mouseY, stacks[(parent.ticksInBook / 20) % stacks.length]);
		}

		super.render(graphics, mouseX, mouseY, pticks);
	}

	@Override
	public int getTextHeight() {
		return 40;
	}

}
