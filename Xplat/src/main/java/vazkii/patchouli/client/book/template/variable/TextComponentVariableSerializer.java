package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.chat.ComponentSerialization;

import vazkii.patchouli.api.IVariableSerializer;

public class TextComponentVariableSerializer implements IVariableSerializer<Component> {
	@Override
	public Component fromJson(JsonElement json, HolderLookup.Provider registries) {
		if (json.isJsonNull()) {
			return Component.literal("");
		}
		if (json.isJsonPrimitive()) {
			return Component.literal(json.getAsString());
		}
		return Serializer.fromJson(json, registries);
	}

	@Override
	public JsonElement toJson(Component stack, HolderLookup.Provider registries) {
		return ComponentSerialization.CODEC.encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), stack).getOrThrow(JsonParseException::new);
	}
}
