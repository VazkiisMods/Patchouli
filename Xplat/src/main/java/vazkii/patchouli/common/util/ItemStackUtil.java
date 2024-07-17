package vazkii.patchouli.common.util;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;

import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import org.apache.commons.lang3.tuple.Triple;

import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.ItemModBook;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ItemStackUtil {
	private ItemStackUtil() {}

	public static Triple<Holder<Item>, DataComponentMap, Integer> deserializeStack(String string, HolderLookup.Provider registries) {
		StringReader reader = new StringReader(string.trim());
		ItemParser itemParser = new ItemParser(registries);
		try {
			ItemParser.ItemResult result = itemParser.parse(reader);
			int count = 1;
			if (reader.canRead()) {
				reader.expect('#');
				count = reader.readInt();
			}
			return Triple.of(result.item(), result.components(), count);
		} catch (CommandSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static ItemStack loadFromParsed(Triple<Holder<Item>, DataComponentMap, Integer> parsed) {
		var holder = parsed.getLeft();
		var components = parsed.getMiddle();
		var count = parsed.getRight();
		if (!holder.isBound() && holder.unwrapKey().isPresent()) {
			throw new RuntimeException("Unknown item ID: " + holder.unwrapKey().get().location());
		}
		Item item = holder.value();
		ItemStack stack = new ItemStack(item, count);

		if (!components.isEmpty()) {
			stack.applyComponents(components);
		}
		return stack;
	}

	public static ItemStack loadStackFromString(String res, HolderLookup.Provider registries) {
		return loadFromParsed(deserializeStack(res, registries));
	}

	public static Ingredient loadIngredientFromString(String ingredientString, HolderLookup.Provider registries) {
		return Ingredient.of(loadStackListFromString(ingredientString, registries).toArray(new ItemStack[0]));
	}

	public static List<ItemStack> loadStackListFromString(String ingredientString, HolderLookup.Provider registries) {
		String[] stacksSerialized = splitStacksFromSerializedIngredient(ingredientString);
		List<ItemStack> stacks = new ArrayList<>();
		for (String s : stacksSerialized) {
			if (s.startsWith("tag:")) {
				var key = TagKey.create(Registries.ITEM, new ResourceLocation(s.substring(4)));
				BuiltInRegistries.ITEM.getTag(key).ifPresent(tag -> tag.stream().forEach(item -> stacks.add(new ItemStack(item))));
			} else {
				stacks.add(loadStackFromString(s, registries));
			}
		}
		return stacks;
	}

	public static StackWrapper wrapStack(ItemStack stack) {
		return stack.isEmpty() ? StackWrapper.EMPTY_WRAPPER : new StackWrapper(stack);
	}

	@Nullable
	public static Book getBookFromStack(ItemStack stack) {
		if (stack.getItem() instanceof ItemModBook) {
			return ItemModBook.getBook(stack);
		}

		Collection<Book> books = BookRegistry.INSTANCE.books.values();
		for (Book b : books) {
			if (ItemStack.isSameItem(b.getBookItem(), stack)) {
				return b;
			}
		}

		return null;
	}

	public static class StackWrapper {

		public static final StackWrapper EMPTY_WRAPPER = new StackWrapper(ItemStack.EMPTY);

		public final ItemStack stack;

		public StackWrapper(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this || (obj instanceof StackWrapper && ItemStack.isSameItem(stack, ((StackWrapper) obj).stack));
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

	public static ItemStack loadStackFromJson(JsonObject json, HolderLookup.Provider registries) {
		String itemName = json.get("item").getAsString();

		Item item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(itemName)).orElseThrow(() -> new IllegalArgumentException("Unknown item '" + itemName + "'")
		);

		ItemStack stack = new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));

		if (json.has("components")) {
			DataComponentMap.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), json.get("components")).resultOrPartial(e -> {
				throw new IllegalArgumentException("Failed to parse components: " + e);
			}).ifPresent(stack::applyComponents);
		}

		return stack;
	}
}
