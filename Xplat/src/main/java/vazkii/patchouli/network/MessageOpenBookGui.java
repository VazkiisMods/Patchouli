package vazkii.patchouli.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.Nullable;

public record MessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry, int page) implements CustomPacketPayload {

	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "open_book");
	public static final StreamCodec<FriendlyByteBuf, MessageOpenBookGui> CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC,
			MessageOpenBookGui::book,
			ByteBufCodecs.STRING_UTF8.map(entry -> entry.isEmpty() ? null : ResourceLocation.tryParse(entry), entry -> entry == null ? "" : entry.toString()),
			MessageOpenBookGui::entry,
			ByteBufCodecs.VAR_INT,
			MessageOpenBookGui::page,
			MessageOpenBookGui::new
	);
	public static final Type<MessageOpenBookGui> TYPE = new Type<>(ID);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
