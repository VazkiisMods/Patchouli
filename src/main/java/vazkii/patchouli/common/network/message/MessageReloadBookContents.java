package vazkii.patchouli.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.network.NetworkHandler;

import java.util.function.Supplier;

public class MessageReloadBookContents {
	public static final ResourceLocation ID = new ResourceLocation(Patchouli.MOD_ID, "reload_books");

	public static void sendToAll() {
		NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new MessageReloadBookContents());
	}

	public static void send(ServerPlayer player) {
		NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReloadBookContents());
	}

	public void encode(FriendlyByteBuf buf) {}

	public static MessageReloadBookContents decode(FriendlyByteBuf buf) {
		return new MessageReloadBookContents();
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ClientBookRegistry.INSTANCE.reload(false));
		ctx.get().setPacketHandled(true);
	}
}
