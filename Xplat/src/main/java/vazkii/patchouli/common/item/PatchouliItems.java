package vazkii.patchouli.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.BiConsumer;

public class PatchouliItems {

	public static final ResourceLocation BOOK_ID = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "guide_book");
	public static final Item BOOK = new ItemModBook();

	public static void submitItemRegistrations(BiConsumer<ResourceLocation, Item> consumer) {
		consumer.accept(BOOK_ID, BOOK);
	}
}
