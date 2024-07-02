package vazkii.patchouli.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;

public record MessageReloadBookContents() implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(PatchouliAPI.MOD_ID, "reload_books");
	public static final StreamCodec<FriendlyByteBuf, MessageReloadBookContents> CODEC = CustomPacketPayload.codec(
			MessageReloadBookContents::write,
			MessageReloadBookContents::new);
	public static final Type<MessageReloadBookContents> TYPE = CustomPacketPayload.createType(ID.toString());

	public MessageReloadBookContents(final FriendlyByteBuf packetBuffer) {
		this();
	}

	public void write(FriendlyByteBuf buf) {}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
