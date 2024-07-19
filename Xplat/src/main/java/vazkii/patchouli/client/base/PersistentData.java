package vazkii.patchouli.client.base;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.SerializationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PersistentData {

	private static final Path saveFile = Paths.get("patchouli_data.json");

	public static DataHolder data = new DataHolder(new JsonObject());

	public static void setup() {
		try (var r = Files.newBufferedReader(saveFile, Charsets.UTF_8)) {
			var root = SerializationUtil.RAW_GSON.fromJson(r, JsonObject.class);
			data = new DataHolder(root);
		} catch (IOException e) {
			if (!(e instanceof NoSuchFileException)) {
				PatchouliAPI.LOGGER.warn("Unable to load patchouli_data.json, replacing with default", e);
			}
			data = new DataHolder(new JsonObject());
			save();
		} catch (Exception e) {
			PatchouliAPI.LOGGER.warn("Corrupted patchouli_data.json, replacing with default", e);
			data = new DataHolder(new JsonObject());
			save();
		}
	}

	public static void save() {
		var json = data.serialize();
		try (var w = Files.newBufferedWriter(saveFile, Charsets.UTF_8)) {
			SerializationUtil.PRETTY_GSON.toJson(json, w);
		} catch (IOException e) {
			PatchouliAPI.LOGGER.warn("Unable to save patchouli_data.json", e);
		}
	}

	public static final class DataHolder {
		public int bookGuiScale;
		public boolean clickedVisualize;

		private final Map<ResourceLocation, PersistentData.BookData> bookData = new HashMap<>();

		public DataHolder(JsonObject root) {
			this.bookGuiScale = GsonHelper.getAsInt(root, "bookGuiScale", 0);
			this.clickedVisualize = GsonHelper.getAsBoolean(root, "clickedVisualize", false);
			var obj = GsonHelper.getAsJsonObject(root, "bookData", new JsonObject());

			for (var e : obj.entrySet()) {
				this.bookData.put(ResourceLocation.tryParse(e.getKey()), new BookData(e.getValue().getAsJsonObject()));
			}
		}

		public PersistentData.BookData getBookData(Book book) {
			return bookData.computeIfAbsent(book.id, k -> new BookData(new JsonObject()));
		}

		public JsonObject serialize() {
			var ret = new JsonObject();
			ret.addProperty("bookGuiScale", this.bookGuiScale);
			ret.addProperty("clickedVisualize", this.clickedVisualize);

			var books = new JsonObject();
			for (var e : bookData.entrySet()) {
				books.add(e.getKey().toString(), e.getValue().serialize());
			}
			ret.add("bookData", books);
			return ret;
		}
	}

	public static final class Bookmark {
		public final ResourceLocation entry;
		public final int spread;

		public Bookmark(ResourceLocation entry, int spread) {
			this.entry = entry;
			this.spread = spread;
		}

		public Bookmark(JsonObject root) {
			this.entry = ResourceLocation.tryParse(GsonHelper.getAsString(root, "entry"));
			this.spread = GsonHelper.getAsInt(root, "page"); // Serialized as page for legacy reasons
		}

		public BookEntry getEntry(Book book) {
			return book.getContents().entries.get(entry);
		}

		public JsonObject serialize() {
			var ret = new JsonObject();
			ret.addProperty("entry", this.entry.toString());
			ret.addProperty("page", this.spread); // Serialized as page for legacy reasons
			return ret;
		}
	}

	public static final class BookData {
		public final List<ResourceLocation> viewedEntries = new ArrayList<>();
		public final List<Bookmark> bookmarks = new ArrayList<>();
		public final List<ResourceLocation> history = new ArrayList<>();
		public final List<ResourceLocation> completedManualQuests = new ArrayList<>();

		public BookData(JsonObject root) {
			var emptyArray = new JsonArray();
			for (var e : GsonHelper.getAsJsonArray(root, "viewedEntries", emptyArray)) {
				viewedEntries.add(ResourceLocation.tryParse(e.getAsString()));
			}
			for (var e : GsonHelper.getAsJsonArray(root, "bookmarks", emptyArray)) {
				bookmarks.add(new Bookmark(e.getAsJsonObject()));
			}
			for (var e : GsonHelper.getAsJsonArray(root, "history", emptyArray)) {
				history.add(ResourceLocation.tryParse(e.getAsString()));
			}
			for (var e : GsonHelper.getAsJsonArray(root, "completedManualQuests", emptyArray)) {
				completedManualQuests.add(ResourceLocation.tryParse(e.getAsString()));
			}
		}

		public JsonObject serialize() {
			var ret = new JsonObject();
			var viewed = new JsonArray();
			this.viewedEntries.stream().map(Object::toString).forEach(viewed::add);
			ret.add("viewedEntries", viewed);

			var bookmarks = new JsonArray();
			this.bookmarks.stream().map(Bookmark::serialize).forEach(bookmarks::add);
			ret.add("bookmarks", bookmarks);

			var completed = new JsonArray();
			this.completedManualQuests.stream().map(Object::toString).forEach(completed::add);
			ret.add("completedManualQuests", completed);

			return ret;
		}
	}
}
