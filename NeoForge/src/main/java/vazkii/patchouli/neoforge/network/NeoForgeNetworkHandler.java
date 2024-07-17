package vazkii.patchouli.neoforge.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.neoforge.network.handler.NeoForgeClientPayloadHandler;
import vazkii.patchouli.network.MessageOpenBookGui;
import vazkii.patchouli.network.MessageReloadBookContents;

import org.jetbrains.annotations.Nullable;

public class NeoForgeNetworkHandler {

	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(PatchouliAPI.MOD_ID);
		registrar.playToClient(MessageOpenBookGui.TYPE, MessageOpenBookGui.CODEC, NeoForgeClientPayloadHandler.getInstance()::handleData);
		registrar.playToClient(MessageReloadBookContents.TYPE, MessageReloadBookContents.CODEC, NeoForgeClientPayloadHandler.getInstance()::handleData);
	}

	public static void sendOpenBook(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		player.connection.send(new MessageOpenBookGui(book, entry, page));
	}

	public static void sendReloadBookContents(MinecraftServer server) {
		PacketDistributor.sendToAllPlayers(new MessageReloadBookContents());
	}
}
