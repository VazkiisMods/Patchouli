package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GuiButtonBook extends Button {

	protected final GuiBook parent;
	protected final int u, v;
	private final Supplier<Boolean> displayCondition;
	protected final List<Component> tooltip;

	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, OnPress onPress, Component... tooltip) {
		this(parent, x, y, u, v, w, h, () -> true, onPress, tooltip);
	}

	public GuiButtonBook(GuiBook parent, int x, int y, int u, int v, int w, int h, Supplier<Boolean> displayCondition, OnPress onPress, Component... tooltip) {
		super(x, y, w, h, tooltip[0], onPress, DEFAULT_NARRATION);
		this.parent = parent;
		this.u = u;
		this.v = v;
		this.displayCondition = displayCondition;
		this.tooltip = Arrays.asList(tooltip);
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		active = displayCondition.get();
		if (!active) {
			return;
		}
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		GuiBook.drawFromTexture(graphics, parent.book, getX(), getY(), u + (isHoveredOrFocused() ? width : 0), v, width, height);
		if (isHoveredOrFocused()) {
			parent.setTooltip(getTooltipLines());
		}
	}

	@Override
	public void playDownSound(SoundManager soundHandlerIn) {
		GuiBook.playBookFlipSound(parent.book);
	}

	public List<Component> getTooltipLines() {
		return tooltip;
	}

}
