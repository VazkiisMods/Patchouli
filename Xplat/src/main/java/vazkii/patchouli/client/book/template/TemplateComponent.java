package vazkii.patchouli.client.book.template;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.IVariablesAvailableCallback;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.base.PatchouliConfig;

import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public abstract class TemplateComponent implements IVariablesAvailableCallback {

	public String group = "";
	public int x, y;

	public String flag = "";

	public String advancement = "";
	@SerializedName("negate_advancement") boolean negateAdvancement = false;

	public String guard = null;

	transient boolean guardPass = false;
	transient boolean isVisible = true;
	private transient boolean compiled = false;

	public transient JsonObject sourceObject;

	public final void compile(Level level, IVariableProvider variables, IComponentProcessor processor, @Nullable TemplateInclusion encapsulation) {
		if (compiled) {
			return;
		}

		if (encapsulation != null) {
			x += encapsulation.x;
			y += encapsulation.y;
		}

		VariableAssigner.assignVariableHolders(level, this, variables, processor, encapsulation);
		compiled = true;
	}

	public boolean getVisibleStatus(IComponentProcessor processor) {
		if (processor != null && group != null && !group.isEmpty() && !processor.allowRender(group)) {
			return false;
		}

		if (!guardPass) {
			return false;
		}

		if (!flag.isEmpty() && !PatchouliConfig.getConfigFlag(flag)) {
			return false;
		}

		if (!advancement.isEmpty()) {
			return ClientAdvancements.hasDone(advancement) != negateAdvancement;
		}

		return true;
	}

	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		// NO-OP
	}

	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		// NO-OP
	}

	public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
		// NO-OP
	}

	public boolean mouseClicked(BookPage page, double mouseX, double mouseY, int mouseButton) {
		return false;
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
		// TODO fix this up to use IVariable more intelligently
		group = lookup.apply(IVariable.wrap(group, registries)).asString();
		flag = lookup.apply(IVariable.wrap(flag, registries)).asString();
		advancement = lookup.apply(IVariable.wrap(advancement, registries)).asString();
		guardPass = (guard == null || lookup.apply(IVariable.wrap(guard, registries)).asBoolean());
	}
}
