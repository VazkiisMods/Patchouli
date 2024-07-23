package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.function.UnaryOperator;

public class ComponentHeader extends TemplateComponent {

	public IVariable text;

	@SerializedName("color") public IVariable colorStr;

	boolean centered = true;
	float scale = 1F;

	transient Component actualText;
	transient int color;

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		try {
			color = Integer.parseInt(colorStr.asString(""), 16);
		} catch (NumberFormatException e) {
			color = page.book.headerColor;
		}

		if (x == -1) {
			x = GuiBook.PAGE_WIDTH / 2;
		}
		if (y == -1) {
			y = 0;
		}
	}

	@Override
	public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
		graphics.pose().pushPose();
		graphics.pose().translate(x, y, 0);
		graphics.pose().scale(scale, scale, scale);

		if (centered) {
			page.parent.drawCenteredStringNoShadow(graphics, page.i18n(actualText.getString()), 0, 0, color);
		} else {
			graphics.drawString(page.fontRenderer, page.i18n(actualText.getString()), 0, 0, color, false);
		}
		graphics.pose().popPose();
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
		super.onVariablesAvailable(lookup, registries);
		actualText = lookup.apply(text).as(Component.class);
		colorStr = lookup.apply(colorStr);
	}
}
