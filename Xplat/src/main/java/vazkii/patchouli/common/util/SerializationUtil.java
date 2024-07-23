package vazkii.patchouli.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import vazkii.patchouli.api.IVariable;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class SerializationUtil {

	public static final IVariable.Serializer VARIABLE_SERIALIZER = new IVariable.Serializer();
	public static final Gson RAW_GSON = new GsonBuilder()
			.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
			.registerTypeAdapter(IVariable.class, VARIABLE_SERIALIZER)
			.create();
	public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

	private SerializationUtil() {}

	public static ResourceLocation getAsResourceLocation(JsonObject object, String key, @Nullable ResourceLocation fallback) {
		if (object.has(key)) {
			return ResourceLocation.tryParse(GsonHelper.convertToString(object.get(key), key));
		} else {
			return fallback;
		}
	}

	@Nullable
	public static <T extends Enum<T>> T getAsEnum(JsonObject object, String key, Class<T> clz, @Nullable T fallback) {
		if (object.has(key)) {
			var str = GsonHelper.convertToString(object.get(key), key).toUpperCase(Locale.ROOT);
			return Enum.valueOf(clz, str);
		} else {
			return fallback;
		}
	}
}
