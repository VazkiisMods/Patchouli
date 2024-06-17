package vazkii.patchouli.fabric.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import vazkii.patchouli.common.advancement.PatchouliCriteriaTriggers;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.command.OpenBookCommand;
import vazkii.patchouli.common.handler.LecternEventHandler;
import vazkii.patchouli.common.handler.ReloadContentsHandler;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

public class FabricModInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		PatchouliSounds.submitRegistrations((id, e) -> Registry.register(BuiltInRegistries.SOUND_EVENT, id, e));
		PatchouliItems.submitItemRegistrations((id, e) -> Registry.register(BuiltInRegistries.ITEM, id, e));
		PatchouliItems.submitRecipeSerializerRegistrations((id, e) -> Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, e));
		PatchouliCriteriaTriggers.submitTriggerRegistrations((id, e) -> Registry.register(BuiltInRegistries.TRIGGER_TYPES, id, e));
		FiberPatchouliConfig.setup();
		CommandRegistrationCallback.EVENT.register((disp, buildCtx, selection) -> OpenBookCommand.register(disp));
		UseBlockCallback.EVENT.register(LecternEventHandler::rightClick);

		BookRegistry.INSTANCE.init();

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, _r, success) -> {
			if (success) {
				ReloadContentsHandler.dataReloaded(server);
			}
		});

		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook) {
				if (b.creativeTab != null) {
					ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, b.creativeTab);
					ItemGroupEvents.modifyEntriesEvent(key)
							.register(entries -> entries.accept(ItemModBook.forBook(b)));
				}
				ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SEARCH).register(entries -> {
					entries.accept(ItemModBook.forBook(b));
				});
			}
		});
	}
}
