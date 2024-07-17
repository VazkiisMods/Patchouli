package vazkii.patchouli.client.gui;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.client.base.ClientAdvancements;

public class GuiAdvancementsExt extends AdvancementsScreen {

	Screen parent;

	public GuiAdvancementsExt(net.minecraft.client.multiplayer.ClientAdvancements manager, Screen parent, ResourceLocation tab) {
		super(manager);
		this.parent = parent;

		AdvancementHolder start = manager.get(tab);
		if (start != null && ClientAdvancements.hasDone(start.id().toString())) {
			manager.setSelectedTab(start, false);
		}
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (minecraft.options.keyAdvancements.matches(key, scanCode) || scanCode == 1) {
			minecraft.setScreen(parent);
			return true;
		} else {
			return super.keyPressed(key, scanCode, modifiers);
		}
	}

}
