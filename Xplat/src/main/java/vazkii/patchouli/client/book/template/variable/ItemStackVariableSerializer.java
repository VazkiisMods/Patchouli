package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.common.util.ItemStackUtil;

public class ItemStackVariableSerializer implements IVariableSerializer<ItemStack> {
	@Override
	public ItemStack fromJson(JsonElement json, HolderLookup.Provider registries) {
		if (json.isJsonNull()) {
			return ItemStack.EMPTY;
		}
		if (json.isJsonPrimitive()) {
			return ItemStackUtil.loadStackFromString(json.getAsString(), registries);
		}
		if (json.isJsonObject()) {
			return ItemStackUtil.loadStackFromJson(json.getAsJsonObject(), registries);
		}
		throw new IllegalArgumentException("Can't make an ItemStack from an array!");
	}

	@Override
	public JsonElement toJson(ItemStack stack, HolderLookup.Provider registries) {
		// Adapted from net.minecraftforge.common.crafting.StackList::toJson
		JsonObject ret = new JsonObject();
		ret.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
		if (stack.getCount() != 1) {
			ret.addProperty("count", stack.getCount());
		}
		if (!stack.getComponents().isEmpty()) {
			DataComponentMap data = stack.getComponents();
			DataComponentMap.CODEC.encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), data).result().ifPresent(e -> ret.add("components", e));
		}
		return ret;
	}

}
