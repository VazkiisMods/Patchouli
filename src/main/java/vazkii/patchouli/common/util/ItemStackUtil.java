package vazkii.patchouli.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.datafixer.NbtOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import vazkii.patchouli.common.mixin.MixinIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class ItemStackUtil {
	private static final Gson GSON = new GsonBuilder().create();

	public static String serializeStack(ItemStack stack) {
		StringBuilder builder = new StringBuilder();
		builder.append(Registry.ITEM.getId(stack.getItem()).toString());

		int count = stack.getCount();
		if (count > 1) {
			builder.append("#");
			builder.append(count);
		}

		if (stack.hasTag()) {
			Dynamic<?> dyn = new Dynamic<>(NbtOps.INSTANCE, stack.getTag());
			JsonElement j = dyn.convert(JsonOps.INSTANCE).getValue();
			builder.append(GSON.toJson(j));
		}

		return builder.toString();
	}

	public static ItemStack loadStackFromString(String res) {
		String nbt = "";
		int nbtStart = res.indexOf("{");
		if (nbtStart > 0) {
			nbt = res.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
			res = res.substring(0, nbtStart);
		}

		String[] upper = res.split("#");
		String count = "1";
		if (upper.length > 1) {
			res = upper[0];
			count = upper[1];
		}

		String[] tokens = res.split(":");
		if (tokens.length < 2) {
			return ItemStack.EMPTY;
		}

		int countn = Integer.parseInt(count);
		Identifier key = new Identifier(tokens[0], tokens[1]);
		Optional<Item> maybeItem = Registry.ITEM.getOrEmpty(key);
		if (!maybeItem.isPresent()) {
			throw new RuntimeException("Unknown item ID: " + key);
		}
		Item item = maybeItem.get();
		ItemStack stack = new ItemStack(item, countn);

		if (!nbt.isEmpty()) {
			try {
				stack.setTag(StringNbtReader.parse(nbt));
			} catch (CommandSyntaxException e) {
				throw new RuntimeException("Failed to parse ItemStack JSON", e);
			}
		}

		return stack;
	}

	public static String serializeIngredient(Ingredient ingredient) {
		((MixinIngredient) (Object) ingredient).callCacheMatchingStacks();
		ItemStack[] stacks = ((MixinIngredient) (Object) ingredient).getMatchingStacks();
		String[] stacksSerialized = new String[stacks.length];
		for (int i = 0; i < stacks.length; i++) {
			stacksSerialized[i] = serializeStack(stacks[i]);
		}

		return String.join(",", stacksSerialized);
	}

	public static Ingredient loadIngredientFromString(String ingredientString) {
		return Ingredient.ofStacks(loadStackListFromString(ingredientString).toArray(new ItemStack[0]));
	}

	public static String serializeStackList(List<ItemStack> stacks) {
		StringJoiner joiner = new StringJoiner(",");
		for (ItemStack stack : stacks) {
			joiner.add(serializeStack(stack));
		}
		return joiner.toString();
	}

	public static List<ItemStack> loadStackListFromString(String ingredientString) {
		String[] stacksSerialized = splitStacksFromSerializedIngredient(ingredientString);
		List<ItemStack> stacks = new ArrayList<>();
		for (int i = 0; i < stacksSerialized.length; i++) {
			if (stacksSerialized[i].startsWith("tag:")) {
				Tag<Item> tag = ItemTags.getContainer().get(new Identifier(stacksSerialized[i].substring(4)));
				if (tag != null) {
					for (Item item : tag.values()) {
						stacks.add(new ItemStack(item));
					}
				}
			} else {
				stacks.add(loadStackFromString(stacksSerialized[i]));
			}
		}
		return stacks;
	}

	public static StackWrapper wrapStack(ItemStack stack) {
		return stack.isEmpty() ? StackWrapper.EMPTY_WRAPPER : new StackWrapper(stack);
	}

	public static class StackWrapper {

		public static final StackWrapper EMPTY_WRAPPER = new StackWrapper(ItemStack.EMPTY);

		public final ItemStack stack;

		public StackWrapper(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this || (obj instanceof StackWrapper && ItemStack.areItemsEqual(stack, ((StackWrapper) obj).stack));
		}

		@Override
		public int hashCode() {
			return stack.getItem().hashCode();
		}

		@Override
		public String toString() {
			return "Wrapper[" + stack.toString() + "]";
		}

	}

	private static String[] splitStacksFromSerializedIngredient(String ingredientSerialized) {
		final List<String> result = new ArrayList<>();

		int lastIndex = 0;
		int braces = 0;
		Character insideString = null;
		for (int i = 0; i < ingredientSerialized.length(); i++) {
			switch (ingredientSerialized.charAt(i)) {
			case '{':
				if (insideString == null) {
					braces++;
				}
				break;
			case '}':
				if (insideString == null) {
					braces--;
				}
				break;
			case '\'':
				insideString = insideString == null ? '\'' : null;
				break;
			case '"':
				insideString = insideString == null ? '"' : null;
				break;
			case ',':
				if (braces <= 0) {
					result.add(ingredientSerialized.substring(lastIndex, i));
					lastIndex = i + 1;
					break;
				}
			}
		}

		result.add(ingredientSerialized.substring(lastIndex));

		return result.toArray(new String[0]);
	}

	public static ItemStack loadStackFromJson(JsonObject json) {
		// Adapted from net.minecraftforge.common.crafting.CraftingHelper::getItemStack
		String itemName = json.get("item").getAsString();

		Item item = Registry.ITEM.getOrEmpty(new Identifier(itemName)).orElseThrow(() -> new IllegalArgumentException("Unknown item '" + itemName + "'")
		);

		ItemStack stack = new ItemStack(item, JsonHelper.getInt(json, "count", 1));

		if (json.has("nbt")) {
			try {
				JsonElement element = json.get("nbt");
				CompoundTag nbt;
				if (element.isJsonObject()) {
					nbt = StringNbtReader.parse(GSON.toJson(element));
				} else {
					nbt = StringNbtReader.parse(element.getAsString());
				}
				stack.setTag(nbt);
			} catch (CommandSyntaxException e) {
				throw new IllegalArgumentException("Invalid NBT Entry: " + e.toString());
			}
		}

		return stack;
	}

	public static List<ItemStack> loadStackListFromJson(JsonObject json) {
		List<ItemStack> stacks = new ArrayList<>();
		if (json.has("item")) {
			stacks.add(loadStackFromJson(json));
		} else if (json.has("items") && json.get("items").isJsonArray()) {
			JsonArray items = json.getAsJsonArray("items");
			return loadStackListFromJson(items);
		}
		return stacks;
	}

	public static List<ItemStack> loadStackListFromJson(JsonArray json) {
		List<ItemStack> stacks = new ArrayList<>();
		for (JsonElement item : json) {
			if (item.isJsonObject()) {
				stacks.add(loadStackFromJson((JsonObject) item));
			}
		}
		return stacks;
	}

}
