package vazkii.patchouli.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.opengl.GL11;

import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

public class TooltipHandler {
	private static float lexiconLookupTime = 0;

	public static void onTooltip(GuiGraphics graphics, ItemStack stack, int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getInstance();
		int tooltipX = mouseX;
		int tooltipY = mouseY - 4;

		if (mc.player != null && !(mc.screen instanceof GuiBook)) {
			int lexSlot = -1;
			ItemStack lexiconStack = ItemStack.EMPTY;
			Pair<BookEntry, Integer> lexiconEntry = null;

			for (int i = 0; i < Inventory.getSelectionSize(); i++) {
				ItemStack stackAt = mc.player.getInventory().getItem(i);
				if (!stackAt.isEmpty()) {
					Book book = ItemStackUtil.getBookFromStack(stackAt);
					if (book != null) {
						Pair<BookEntry, Integer> entry = book.getContents().getEntryForStack(stack);

						if (entry != null && !entry.getFirst().isLocked()) {
							lexiconStack = stackAt;
							lexSlot = i;
							lexiconEntry = entry;
							break;
						}
					}
				}
			}

			if (lexSlot > -1) {
				int x = tooltipX - 34;
				RenderSystem.disableDepthTest();

				graphics.fill(x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
				graphics.fill(x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);

				if (PatchouliConfig.get().useShiftForQuickLookup() ? Screen.hasShiftDown() : Screen.hasControlDown()) {
					lexiconLookupTime += ClientTicker.delta;

					int cx = x + 8;
					int cy = tooltipY + 8;
					float r = 12;
					float requiredTime = PatchouliConfig.get().quickLookupTime();
					float angles = lexiconLookupTime / requiredTime * 360F;

					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					BufferBuilder buf = Tesselator.getInstance().begin(Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

					float a = 0.5F + 0.2F * ((float) Math.cos(ClientTicker.total / 10) * 0.5F + 0.5F);
					buf.addVertex(cx, cy, 0).setColor(0F, 0.5F, 0F, a);

					for (float i = angles; i > 0; i--) {
						double rad = (i - 90) / 180F * Math.PI;
						buf.addVertex((float) (cx + Math.cos(rad) * r), (float) (cy + Math.sin(rad) * r), 0).setColor(0F, 1F, 0F, 1F);
					}

					buf.addVertex(cx, cy, 0).setColor(0F, 1F, 0F, 0F);
					BufferUploader.drawWithShader(buf.buildOrThrow());

					RenderSystem.disableBlend();

					if (lexiconLookupTime >= requiredTime) {
						mc.player.getInventory().selected = lexSlot;
						int spread = lexiconEntry.getSecond();
						ClientBookRegistry.INSTANCE.displayBookGui(lexiconEntry.getFirst().getBook().id, lexiconEntry.getFirst().getId(), spread * 2);
					}
				} else {
					lexiconLookupTime = 0F;
				}

				graphics.pose().pushPose();
				graphics.pose().translate(0, 0, 300);
				graphics.renderItem(lexiconStack, x, tooltipY);
				graphics.renderItemDecorations(mc.font, lexiconStack, x, tooltipY);
				graphics.pose().popPose();

				graphics.pose().pushPose();
				graphics.pose().translate(0, 0, 500);
				graphics.drawString(mc.font, "?", x + 10, tooltipY + 8, 0xFFFFFFFF, true);

				graphics.pose().scale(0.5F, 0.5F, 1F);
				boolean mac = Minecraft.ON_OSX;
				Component key = Component.literal(PatchouliConfig.get().useShiftForQuickLookup() ? "Shift" : mac ? "Cmd" : "Ctrl")
						.withStyle(ChatFormatting.BOLD);
				graphics.drawString(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF, true);
				graphics.pose().popPose();

				RenderSystem.enableDepthTest();
			} else {
				lexiconLookupTime = 0F;
			}
		} else {
			lexiconLookupTime = 0F;
		}
	}
}
