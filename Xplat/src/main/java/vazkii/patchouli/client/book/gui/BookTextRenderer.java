package vazkii.patchouli.client.book.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;

import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.TextLayouter;
import vazkii.patchouli.client.book.text.Word;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import java.util.List;

public class BookTextRenderer implements Renderable {
	private final Book book;

	private final BookTextParser parser;
	private final TextLayouter layouter;
	private List<Word> words;
	private float scale;

	public BookTextRenderer(GuiBook gui, Component text, int x, int y) {
		this(gui, text, x, y, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT, gui.book.textColor);
	}

	public BookTextRenderer(GuiBook gui, Component text, int x, int y, int width, int lineHeight, int baseColor) {
		this.book = gui.book;
		Style baseStyle = book.getFontStyle().withColor(TextColor.fromRgb(baseColor));

		this.parser = new BookTextParser(gui, this.book, x, y, width, lineHeight, baseStyle);
		var overflowMode = this.book.overflowMode;
		if (overflowMode == null) {
			overflowMode = PatchouliConfig.get().overflowMode();
		}
		this.layouter = new TextLayouter(gui, x, y, lineHeight, width, overflowMode);
		setText(text);
	}

	void setText(Component text) {
		Component text1;
		if (this.book.i18n && text.getContents() instanceof PlainTextContents.LiteralContents lc) {
			text1 = Component.literal(I18n.get(lc.text()));
		} else {
			text1 = text;
		}
		this.layouter.layout(Minecraft.getInstance().font, this.parser.parse(text1));
		this.scale = this.layouter.getScale();
		this.words = this.layouter.getWords();
	}

	private double rescale(double in, double origin) {
		return origin + (in - origin) / scale;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (!words.isEmpty()) {
			Font font = Minecraft.getInstance().font;
			Style style = book.getFontStyle();
			Word first = words.get(0);
			graphics.pose().pushPose();
			graphics.pose().translate(first.x, first.y, 0);
			graphics.pose().scale(scale, scale, 1.0f);
			graphics.pose().translate(-first.x, -first.y, 0);
			int scaledX = (int) rescale(mouseX, first.x);
			int scaledY = (int) rescale(mouseY, first.y);
			words.forEach(word -> word.render(graphics, font, style, scaledX, scaledY));
			graphics.pose().popPose();
		}
	}

	public boolean click(double mouseX, double mouseY, int mouseButton) {
		if (!words.isEmpty()) {
			Word first = words.get(0);
			double scaledX = rescale(mouseX, first.x);
			double scaledY = rescale(mouseY, first.y);
			for (Word word : words) {
				if (word.click(scaledX, scaledY, mouseButton)) {
					return true;
				}
			}
		}

		return false;
	}
}
