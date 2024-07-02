package vazkii.patchouli.neoforge.xplat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

import vazkii.patchouli.api.BookContentsReloadEvent;
import vazkii.patchouli.api.BookDrawScreenEvent;
import vazkii.patchouli.neoforge.client.NeoForgeClientInitializer;
import vazkii.patchouli.neoforge.network.NeoForgeNetworkHandler;
import vazkii.patchouli.xplat.IXplatAbstractions;
import vazkii.patchouli.xplat.XplatModContainer;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NeoForgeXplatImpl implements IXplatAbstractions {
	@Override
	public void fireDrawBookScreen(ResourceLocation book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics) {
		NeoForge.EVENT_BUS.post(new BookDrawScreenEvent(book, gui, mouseX, mouseY, partialTicks, graphics));
	}

	@Override
	public void fireBookReload(ResourceLocation book) {
		NeoForge.EVENT_BUS.post(new BookContentsReloadEvent(book));
	}

	@Override
	public void sendReloadContentsMessage(MinecraftServer server) {
		NeoForgeNetworkHandler.sendReloadBookContents(server);
	}

	@Override
	public void sendOpenBookGui(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		NeoForgeNetworkHandler.sendOpenBook(player, book, entry, page);
	}

	@Override
	public Collection<XplatModContainer> getAllMods() {
		List<XplatModContainer> ret = new ArrayList<>();
		for (var info : ModList.get().getMods()) {
			ret.add(new NeoForgeXplatModContainer(ModList.get().getModContainerById(info.getModId()).get()));
		}
		return ret;
	}

	@Override
	public XplatModContainer getModContainer(String modId) {
		return new NeoForgeXplatModContainer(ModList.get().getModContainerById(modId).get());
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevEnvironment() {
		return !FMLEnvironment.production;
	}

	@Override
	public boolean isPhysicalClient() {
		return FMLEnvironment.dist == Dist.CLIENT;
	}

	@Override
	public void signalBooksLoaded() {
		if (isPhysicalClient()) {
			NeoForgeClientInitializer.signalBooksLoaded();
		}
	}

	@Override
	public boolean handleRecipeKeybind(int keyCode, int scanCode, @Nullable ItemStack stack) {
		return false;
	}
}
