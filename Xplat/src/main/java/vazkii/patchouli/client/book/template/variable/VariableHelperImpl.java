package vazkii.patchouli.client.book.template.variable;

import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableSerializer;
import vazkii.patchouli.api.VariableHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class VariableHelperImpl implements VariableHelper {

	public VariableHelperImpl() {
		registerSerializer(new ItemStackVariableSerializer(), ItemStack.class);
		registerSerializer(new ItemStackArrayVariableSerializer(), ItemStack[].class);
		registerSerializer(new IngredientVariableSerializer(), Ingredient.class);
		registerSerializer(new TextComponentVariableSerializer(), Component.class);
	}

	public Map<Class<?>, IVariableSerializer<?>> serializers = new HashMap<>();

	@Override
	public <T> IVariableSerializer<T> serializerForClass(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		IVariableSerializer<T> serializer = (IVariableSerializer<T>) serializers.get(clazz);
		if (serializer == null && clazz.isArray()) {
			Class<?> componentType = clazz.getComponentType();
			IVariableSerializer<?> componentSerializer = serializerForClass(componentType);
			if (componentSerializer != null) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				IVariableSerializer<T> arraySerializer = (IVariableSerializer<T>) new GenericArrayVariableSerializer(componentSerializer, componentType);
				serializers.put(clazz, arraySerializer);
				return arraySerializer;
			}
		}
		return serializer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> IVariable createFromObject(T object, HolderLookup.Provider registries) {
		Class<?> clazz = object.getClass();
		for (Entry<Class<?>, IVariableSerializer<?>> e : serializers.entrySet()) {
			if (e.getKey().isAssignableFrom(clazz)) {
				return create(((IVariableSerializer<T>) e.getValue()).toJson(object, registries), clazz, registries);
			}
		}

		throw new IllegalArgumentException(String.format("Can't serialize object %s of type %s to IVariable", object, clazz));
	}

	@Override
	public IVariable createFromJson(JsonElement elem, HolderLookup.Provider registries) {
		return create(elem, null, registries);
	}

	private IVariable create(JsonElement elem, Class<?> originator, HolderLookup.Provider registries) {
		return new Variable(elem, originator, registries);
	}

	@Override
	public <T> VariableHelper registerSerializer(IVariableSerializer<T> serializer, Class<T> clazz) {
		serializers.put(clazz, serializer);
		return this;
	}

}
