package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.BookTextRenderer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.function.UnaryOperator;

public class ComponentText extends TemplateComponent {

	public IVariable text;

	@SerializedName("color") public IVariable colorStr;

	@SerializedName("max_width") int maxWidth = GuiBook.PAGE_WIDTH;
	@SerializedName("line_height") int lineHeight = GuiBook.TEXT_LINE_HEIGHT;

	transient Component actualText;
	transient BookTextRenderer textRenderer;
	transient int color;

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		try {
			color = Integer.parseInt(colorStr.asString(""), 16);
		} catch (NumberFormatException e) {
			color = page.book.textColor;
		}
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		super.onVariablesAvailable(lookup);
		actualText = lookup.apply(text).as(Component.class);
		colorStr = lookup.apply(colorStr);
	}

	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		textRenderer = new BookTextRenderer(parent, actualText, x, y, maxWidth, lineHeight, color);
	}

	@Override
	public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
		textRenderer.render(graphics, mouseX, mouseY, pticks);
	}

	@Override
	public boolean mouseClicked(BookPage page, double mouseX, double mouseY, int mouseButton) {
		return textRenderer.click(mouseX, mouseY, mouseButton);
	}

}
