package vazkii.patchouli.fabric.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.network.MessageOpenBookGui;

import org.jetbrains.annotations.Nullable;

public class FabricMessageOpenBookGui {

	public static void send(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		ServerPlayNetworking.send(player, new MessageOpenBookGui(book, entry, page));
	}

	public static void handle(MessageOpenBookGui message, ClientPlayNetworking.Context handler) {
		Minecraft client = handler.client();
		client.submit(() -> ClientBookRegistry.INSTANCE.displayBookGui(message.book(), message.entry(), message.page()));
	}

}
