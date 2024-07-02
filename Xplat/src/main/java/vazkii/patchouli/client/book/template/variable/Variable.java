package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Variable implements IVariable {
	private final JsonElement value;

	@Nullable private final Class<?> sourceClass;
	private final HolderLookup.Provider registries;

	public Variable(JsonElement elem, Class<?> c, HolderLookup.Provider provider) {
		value = Objects.requireNonNull(elem);
		sourceClass = c;
		registries = provider;
	}

	@Override
	public <T> T as(Class<T> clazz) {
		IVariableSerializer<T> serializer = VariableHelper.instance().serializerForClass(clazz);

		if (serializer == null) {
			throw new IllegalArgumentException(String.format("Can't deserialize object of class %s from IVariable", clazz));
		}

		return serializer.fromJson(value, registries);
	}

	@Override
	public JsonElement unwrap() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
