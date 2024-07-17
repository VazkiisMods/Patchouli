package vazkii.patchouli.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.network.MessageReloadBookContents;

public class FabricMessageReloadBookContents {

	public static void sendToAll(MinecraftServer server) {
		PlayerLookup.all(server).forEach(FabricMessageReloadBookContents::send);
	}

	public static void send(ServerPlayer player) {
		ServerPlayNetworking.send(player, new MessageReloadBookContents());
	}

	public static void handle(MessageReloadBookContents message, ClientPlayNetworking.Context handler) {
		Minecraft client = handler.client();
		client.submit(() -> ClientBookRegistry.INSTANCE.reload());
	}
}
