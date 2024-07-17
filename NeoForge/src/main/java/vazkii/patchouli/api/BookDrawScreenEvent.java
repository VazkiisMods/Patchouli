package vazkii.patchouli.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

/**
 * This event is fired after any
 * book gui draws the content of a book with
 * the book gui scale still applied to the transformation state. This is useful if additional
 * custom components should be drawn independently of what page a book
 * is currently on.
 */
public class BookDrawScreenEvent extends Event {
	private final ResourceLocation book;
	private final Screen screen;
	private final int mouseX;
	private final int mouseY;
	private final float partialTicks;
	private final GuiGraphics graphics;

	public BookDrawScreenEvent(ResourceLocation book, Screen screen, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics) {
		this.book = book;
		this.screen = screen;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.partialTicks = partialTicks;
		this.graphics = graphics;
	}

	public ResourceLocation getBook() {
		return book;
	}

	public Screen getScreen() {
		return screen;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public GuiGraphics getGraphics() {
		return graphics;
	}
}
