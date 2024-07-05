package vazkii.patchouli.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.Nullable;

public record MessageOpenBookGui(ResourceLocation book, @Nullable ResourceLocation entry, int page) implements CustomPacketPayload {

	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "open_book");
	public static final StreamCodec<FriendlyByteBuf, MessageOpenBookGui> CODEC = CustomPacketPayload.codec(
			MessageOpenBookGui::write,
			MessageOpenBookGui::new);
	public static final Type<MessageOpenBookGui> TYPE = new Type<>(ID);

	public MessageOpenBookGui(FriendlyByteBuf buf) {
		this(buf.readResourceLocation(), getEntry(buf), buf.readVarInt());
	}

	private static ResourceLocation getEntry(FriendlyByteBuf buf) {
		String entry = buf.readUtf();
		return entry.isEmpty() ? null : ResourceLocation.tryParse(entry);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeResourceLocation(book);
		buf.writeUtf(entry == null ? "" : entry.toString());
		buf.writeVarInt(page);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
