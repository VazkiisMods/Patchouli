package vazkii.patchouli.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.BookModel;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookContentResourceListenerLoader;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.handler.BookRightClickHandler;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.handler.TooltipHandler;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;
import vazkii.patchouli.common.item.PatchouliItems;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@EventBusSubscriber(modid = PatchouliAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoForgeClientInitializer {
	/**
	 * Why are these necessary?
	 * BookRegistry.init is called from CommonSetupEvent. We need the models to be known in ModelRegistryEvent.
	 * However, there is no defined ordering for those events. They all run concurrently during the initial resource
	 * reload.
	 * We need a way of waiting for the books to become known.
	 * Another critical point to note is that loading runs on a fixed-size ForkJoinPool.
	 * Blocking the thread can starve loading completely.
	 * Fortunately, the implementation of Condition.await for ReentrantLock uses ForkJoinPool.managedBlock,
	 * which is aware of potentially blocking operations and can resize the pool accordingly.
	 * If parallel mod loading didn't exist we wouldn't need any of this, but here we are :))))
	 */
	private static final Lock BOOK_LOAD_LOCK = new ReentrantLock();
	private static final Condition BOOK_LOAD_CONDITION = BOOK_LOAD_LOCK.newCondition();
	private static boolean booksLoaded = false;

	public static void signalBooksLoaded() {
		BOOK_LOAD_LOCK.lock();
		booksLoaded = true;
		BOOK_LOAD_CONDITION.signalAll();
		BOOK_LOAD_LOCK.unlock();
	}

	private static List<ResourceLocation> getBookModels() {
		BOOK_LOAD_LOCK.lock();
		try {
			while (!booksLoaded) {
				BOOK_LOAD_CONDITION.awaitUninterruptibly();
			}
			return BookRegistry.INSTANCE.books.values().stream().map(b -> b.model).toList();
		} finally {
			BOOK_LOAD_LOCK.unlock();
		}
	}

	@SubscribeEvent
	public static void modelRegistry(ModelEvent.RegisterAdditional e) {
		getBookModels()
				.stream()
				.map(ModelResourceLocation::standalone)
				.forEach(e::register);

		ItemPropertyFunction prop = (stack, world, entity, seed) -> ItemModBook.getCompletion(stack);
		ItemProperties.register(PatchouliItems.BOOK, ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "completion"), prop);
	}

	@SubscribeEvent
	public static void registerReloadListeners(RegisterClientReloadListenersEvent e) {
		e.registerReloadListener(BookContentResourceListenerLoader.INSTANCE);

		e.registerReloadListener((ResourceManagerReloadListener) manager -> {
			if (Minecraft.getInstance().level != null) {
				PatchouliAPI.LOGGER.info("Reloading resource pack-based books");
				ClientBookRegistry.INSTANCE.reload();
			} else {
				PatchouliAPI.LOGGER.debug("Not reloading resource pack-based books as client world is missing");
			}
		});
	}

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiLayersEvent evt) {
		evt.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "book_overlay"),
				BookRightClickHandler::onRenderHUD
		);
		evt.registerBelow(VanillaGuiLayers.BOSS_OVERLAY, ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, "multiblock_progress"),
				MultiblockVisualizationHandler::onRenderHUD
		);
	}

	@SubscribeEvent
	public static void onInitializeClient(FMLClientSetupEvent evt) {
		ClientBookRegistry.INSTANCE.init();
		PersistentData.setup();
		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> {
			ClientTicker.endClientTick(Minecraft.getInstance());
		});
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> BookRightClickHandler.onRightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec()));
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			InteractionResult result = MultiblockVisualizationHandler.onPlayerInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
			if (result.consumesAction()) {
				e.setCanceled(true);
				e.setCancellationResult(result);
			}
		});
		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> {
			MultiblockVisualizationHandler.onClientTick(Minecraft.getInstance());
		});

		NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Pre e) -> {
			ClientTicker.renderTickStart(e.getPartialTick().getGameTimeDeltaPartialTick(false));
		});
		NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Post e) -> {
			ClientTicker.renderTickEnd();
		});

		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> {
			ClientAdvancements.playerLogout();
		});

		NeoForge.EVENT_BUS.addListener((RenderTooltipEvent.Pre e) -> {
			TooltipHandler.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY());
		});
	}

	@SubscribeEvent
	public static void replaceBookModel(ModelEvent.ModifyBakingResult evt) {
		ModelResourceLocation key = ModelResourceLocation.inventory(PatchouliItems.BOOK_ID);
		evt.getModels().computeIfPresent(key, (k, oldModel) -> new BookModel(oldModel, evt.getModelBakery(), (model) -> {
			ModelResourceLocation modelPath = ModelResourceLocation.standalone(model);
			return Minecraft.getInstance().getModelManager().getModel(modelPath);
		}));
	}
}
