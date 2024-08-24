package vazkii.patchouli.neoforge.network.handler;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.network.MessageOpenBookGui;
import vazkii.patchouli.network.MessageReloadBookContents;

public class NeoForgeClientPayloadHandler {
	private static final NeoForgeClientPayloadHandler INSTANCE = new NeoForgeClientPayloadHandler();

	public static NeoForgeClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleData(final MessageOpenBookGui data, final IPayloadContext context) {
		try {
			ClientBookRegistry.INSTANCE.displayBookGui(data.book(), data.entry(), data.page());
		} catch (Exception e) {
			context.disconnect(Component.translatable("patchouli.networking.open_book.failed", e.getMessage()));
		}
	}

	public void handleData(final MessageReloadBookContents data, final IPayloadContext context) {
		try {
			ClientBookRegistry.INSTANCE.reload();
		} catch (Exception e) {
			context.disconnect(Component.translatable("patchouli.networking.reload_contents.failed", e.getMessage()));
		}
	}
}
