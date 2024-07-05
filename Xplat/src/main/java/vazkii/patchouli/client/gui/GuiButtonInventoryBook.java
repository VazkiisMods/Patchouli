package vazkii.patchouli.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.EntryDisplayState;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

public class GuiButtonInventoryBook extends Button {

	private final Book book;

	public GuiButtonInventoryBook(Book book, int x, int y) {
		super(x, y, 20, 20, Component.empty(), (b) -> {
			BookContents contents = book.getContents();
			contents.openLexiconGui(contents.getCurrentGui(), false);
		}, DEFAULT_NARRATION);
		this.book = book;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {
		graphics.setColor(1F, 1F, 1F, 1F);

		boolean hovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
		graphics.blit(ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "textures/gui/inventory_button.png"), getX(), getY(), (hovered ? 20 : 0), 0, width, height, 64, 64);

		ItemStack stack = book.getBookItem();
		graphics.renderItem(stack, getX() + 2, getY() + 2);
		graphics.renderItemDecorations(Minecraft.getInstance().font, stack, getX() + 2, getY() + 2);

		EntryDisplayState readState = book.getContents().getReadState();
		if (readState.hasIcon && readState.showInInventory) {
			GuiBook.drawMarking(graphics, book, getX(), getY(), 0, readState);
		}
	}

	public Book getBook() {
		return book;
	}

}
