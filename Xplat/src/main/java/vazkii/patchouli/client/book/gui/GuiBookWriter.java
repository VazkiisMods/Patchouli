package vazkii.patchouli.client.book.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.button.GuiButtonBook;
import vazkii.patchouli.common.book.Book;

public class GuiBookWriter extends GuiBook {

	private BookTextRenderer text, editableText;
	private EditBox textfield;

	private static String savedText = "";
	private static boolean drawHeader;

	public GuiBookWriter(Book book) {
		super(book, Component.empty());
	}

	@Override
	public void init() {
		super.init();

		this.text = new BookTextRenderer(this, Component.translatable("patchouli.gui.lexicon.editor.info"), LEFT_PAGE_X, TOP_PADDING + 20);
		this.textfield = new EditBox(font, 15, FULL_HEIGHT - 40, PAGE_WIDTH, 20, textfield, Component.empty());
		this.textfield.setMaxLength(Integer.MAX_VALUE);
		if (this.textfield.getValue().isEmpty()) {
			this.textfield.setValue(savedText);
		}
		this.editableText = new BookTextRenderer(this, Component.literal(""), RIGHT_PAGE_X, TOP_PADDING + (drawHeader ? 22 : -4));

		addRenderableWidget(new GuiButtonBook(this, bookLeft + 115, bookTop + PAGE_HEIGHT - 36, 330, 9, 11, 11, this::handleToggleHeaderButton, Component.translatable("patchouli.gui.lexicon.button.toggle_mock_header")));
		refreshText();
	}

	@Override
	void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.drawForegroundElements(graphics, mouseX, mouseY, partialTicks);

		drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.editor"), LEFT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
		drawSeparator(graphics, book, LEFT_PAGE_X, TOP_PADDING + 12);

		if (drawHeader) {
			drawCenteredStringNoShadow(graphics, I18n.get("patchouli.gui.lexicon.editor.mock_header"), RIGHT_PAGE_X + PAGE_WIDTH / 2, TOP_PADDING, book.headerColor);
			drawSeparator(graphics, book, RIGHT_PAGE_X, TOP_PADDING + 12);
		}

		textfield.render(graphics, mouseX, mouseY, partialTicks);
		text.render(graphics, mouseX, mouseY, partialTicks);
		editableText.render(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClickedScaled(double mouseX, double mouseY, int mouseButton) {
		if (textfield.mouseClicked(getRelativeX(mouseX), getRelativeY(mouseY), mouseButton)) {
			textfield.setFocused(true);
			return true;
		}
		if (text.click(mouseX, mouseY, mouseButton)) {
			return true;
		}
		if (editableText.click(mouseX, mouseY, mouseButton)) {
			return true;
		}
		return super.mouseClickedScaled(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (textfield.keyPressed(key, scanCode, modifiers)) {
			refreshText();
			return true;
		}

		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (textfield.charTyped(c, i)) {
			refreshText();
			return true;
		}

		return super.charTyped(c, i);
	}

	private void handleToggleHeaderButton(Button button) {
		drawHeader = !drawHeader;
		init();
	}

	private void refreshText() {
		savedText = textfield.getValue();
		try {
			editableText.setText(Component.literal(savedText));
		} catch (Throwable e) {
			editableText.setText(Component.literal("[ERROR]"));
			PatchouliAPI.LOGGER.catching(e);
		}
	}
}
