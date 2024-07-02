package vazkii.patchouli.api;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;

import net.minecraft.core.HolderLookup;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * API endpoint for serializing/deserializing arbitrary objects to/from JsonElements.
 */
public interface VariableHelper {

	Supplier<VariableHelper> INSTANCE = Suppliers.memoize(() -> {
		try {
			return (VariableHelper) Class.forName("vazkii.patchouli.client.book.template.variable.VariableHelperImpl").newInstance();
		} catch (ReflectiveOperationException e) {
			return new VariableHelper() {};
		}
	});

	/**
	 * Get the singleton instance of this VariableHelper.
	 */
	static VariableHelper instance() {
		return INSTANCE.get();
	}

	/**
	 * Create an {@link IVariable} from a given object.
	 */
	default <T> IVariable createFromObject(T object, HolderLookup.Provider registries) {
		return null;
	}

	/**
	 * Create an {@link IVariable} backed by the given {@link JsonElement}.
	 */
	default IVariable createFromJson(JsonElement elem, HolderLookup.Provider registries) {
		return null;
	}

	/**
	 * Get an {@link IVariableSerializer} for the given class.
	 * If we don't know how to deserialize this class, return {@code null}.
	 */
	@Nullable
	default <T> IVariableSerializer<T> serializerForClass(Class<T> clazz) {
		return null;
	}

	/**
	 * Register an {@link IVariableSerializer} that can deserialize an object of the given class.
	 * Note that a sane serializer should probably cope with subclasses gracefully.
	 */
	default <T> VariableHelper registerSerializer(IVariableSerializer<T> serializer, Class<T> clazz) {
		return instance();
	}
}
