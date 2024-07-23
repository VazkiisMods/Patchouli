package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.TemplateComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ComponentTooltip extends TemplateComponent {

	@SerializedName("tooltip") public IVariable[] tooltipRaw;

	int width, height;

	transient List<Component> tooltip;

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
		super.onVariablesAvailable(lookup, registries);
		for (int i = 0; i < tooltipRaw.length; i++) {
			tooltipRaw[i] = lookup.apply(tooltipRaw[i]);
		}
	}

	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		tooltip = new ArrayList<>();

		// todo 1.16 expand this into actual text components
		for (IVariable s : tooltipRaw) {
			//s = I18n.translate(s).replaceAll("&", "\u00A7");
			//if (!s.isEmpty()) {
			//	tooltip.add(new LiteralText(s));
			//}
			tooltip.add(s.as(Component.class));
		}
	}

	@Override
	public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
		if (page.parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)) {
			page.parent.setTooltip(tooltip);
		}
	}

}
