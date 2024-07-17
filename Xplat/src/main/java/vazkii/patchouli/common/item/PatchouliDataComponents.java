package vazkii.patchouli.common.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.BiConsumer;

public class PatchouliDataComponents {

	public static final ResourceLocation COMPONENT_ID = new ResourceLocation(PatchouliAPI.MOD_ID, "book");
	public static final DataComponentType<ResourceLocation> BOOK = DataComponentType.<ResourceLocation>builder()
			.persistent(ResourceLocation.CODEC)
			.networkSynchronized(ResourceLocation.STREAM_CODEC)
			.build();

	public static void submitDataComponentRegistrations(BiConsumer<ResourceLocation, DataComponentType<?>> consumer) {
		consumer.accept(COMPONENT_ID, BOOK);
	}
}
