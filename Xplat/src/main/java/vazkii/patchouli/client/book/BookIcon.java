package vazkii.patchouli.client.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.util.ItemStackUtil;

public sealed interface BookIcon permits BookIcon.StackIcon, BookIcon.TextureIcon {
	void render(GuiGraphics graphics, int x, int y);

	record StackIcon(ItemStack stack) implements BookIcon {
		@Override
		public void render(GuiGraphics graphics, int x, int y) {
			graphics.renderItem(stack(), x, y);
			graphics.renderItemDecorations(Minecraft.getInstance().font, stack(), x, y);
		}
	}

	record TextureIcon(ResourceLocation texture) implements BookIcon {
		@Override
		public void render(GuiGraphics graphics, int x, int y) {
			graphics.setColor(1F, 1F, 1F, 1F);
			graphics.blit(texture(), x, y, 0, 0, 16, 16, 16, 16);
		}
	}

	static BookIcon from(String str) {
		if (str.endsWith(".png")) {
			return new TextureIcon(ResourceLocation.tryParse(str));
		} else {
			try {
				ItemStack stack = ItemStackUtil.loadStackFromString(str, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
				return new StackIcon(stack);
			} catch (Exception e) {
				PatchouliAPI.LOGGER.warn("Invalid icon item stack: {}", e.getMessage());
				return new StackIcon(ItemStack.EMPTY);
			}
		}
	}

}
