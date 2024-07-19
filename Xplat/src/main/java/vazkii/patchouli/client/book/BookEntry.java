package vazkii.patchouli.client.book;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.BookData;
import vazkii.patchouli.client.book.page.PageEmpty;
import vazkii.patchouli.client.book.page.PageQuest;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;
import vazkii.patchouli.common.util.ItemStackUtil.StackWrapper;
import vazkii.patchouli.common.util.SerializationUtil;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BookEntry extends AbstractReadStateHolder implements Comparable<BookEntry> {
	private final String name;
	private final String flag;

	private final boolean priority;
	private final boolean secret;
	private final boolean readByDefault;
	private final BookPage[] pages;
	@Nullable private final ResourceLocation advancement;
	@Nullable private final ResourceLocation turnin;
	private final int sortnum;
	private final int entryColor;

	private final Map<String, Integer> extraRecipeMappings;

	private final ResourceLocation id;
	// Logical book we belong to
	private final Book book;
	@Nullable private final String addedBy;
	private final ResourceLocation categoryId;
	private BookCategory category;
	private final BookIcon icon;

	// Mutable state
	private final List<BookPage> realPages = new ArrayList<>();
	private final List<StackWrapper> relevantStacks = new LinkedList<>();
	private boolean locked;
	private boolean built;
	// End mutable state

	public BookEntry(JsonObject root, ResourceLocation id, Book book, @Nullable String addedBy) {
		this.id = id;
		this.book = book;
		this.addedBy = addedBy;

		var categoryId = GsonHelper.getAsString(root, "category");
		if (categoryId.contains(":")) { // full category ID
			this.categoryId = ResourceLocation.tryParse(categoryId);
		} else {
			String hint = String.format("`%s:%s`", book.id.getNamespace(), categoryId);
			throw new IllegalArgumentException("`category` must be fully qualified (domain:name). Hint: Try " + hint);
		}

		this.name = GsonHelper.getAsString(root, "name");
		this.flag = GsonHelper.getAsString(root, "flag", "");
		this.icon = BookIcon.from(GsonHelper.getAsString(root, "icon"));
		this.priority = GsonHelper.getAsBoolean(root, "priority", false);
		this.secret = GsonHelper.getAsBoolean(root, "secret", false);
		this.readByDefault = GsonHelper.getAsBoolean(root, "read_by_default", false);
		this.advancement = SerializationUtil.getAsResourceLocation(root, "advancement", null);
		this.turnin = SerializationUtil.getAsResourceLocation(root, "turnin", null);
		this.sortnum = GsonHelper.getAsInt(root, "sortnum", 0);
		var entryColor = GsonHelper.getAsString(root, "entry_color", null);
		if (entryColor != null) {
			this.entryColor = Integer.parseInt(entryColor, 16);
		} else {
			this.entryColor = book.textColor;
		}
		this.pages = ClientBookRegistry.INSTANCE.gson.fromJson(GsonHelper.getAsJsonArray(root, "pages"), BookPage[].class);

		var extraRecipeMap = GsonHelper.getAsJsonObject(root, "extra_recipe_mappings", null);
		if (extraRecipeMap == null) {
			extraRecipeMappings = Collections.emptyMap();
		} else {
			extraRecipeMappings = ClientBookRegistry.INSTANCE.gson.fromJson(extraRecipeMap, new TypeToken<Map<String, Integer>>() {}.getType());
		}

	}

	public MutableComponent getName() {
		return book.i18n ? Component.translatable(name) : Component.literal(name);
	}

	public List<BookPage> getPages() {
		List<BookPage> pages = !getBook().advancementsEnabled() ? realPages : realPages.stream().filter(BookPage::isPageUnlocked).collect(Collectors.toList());

		return pages.isEmpty() ? NO_PAGE : pages;
	}

	public int getPageFromAnchor(String anchor) {
		List<BookPage> pages = getPages();
		for (int i = 0; i < pages.size(); i++) {
			BookPage page = pages.get(i);
			if (anchor.equals(page.anchor)) {
				return i;
			}
		}

		return -1;
	}

	private static final List<BookPage> NO_PAGE = ImmutableList.of(new PageEmpty());

	public boolean isPriority() {
		return priority;
	}

	public BookIcon getIcon() {
		return icon;
	}

	public void initCategory(ResourceLocation file, Function<ResourceLocation, BookCategory> categories) {
		this.category = categories.apply(this.categoryId);
		if (this.category == null) {
			String msg = String.format("Entry in file %s does not have a valid category.", file);
			throw new RuntimeException(msg);
		} else {
			this.category.addEntry(this);
		}
	}

	public BookCategory getCategory() {
		return category;
	}

	public void updateLockStatus() {
		boolean currLocked = locked;
		locked = advancement != null && !ClientAdvancements.hasDone(advancement.toString());

		boolean dirty = false;
		if (!locked && currLocked) {
			dirty = true;
			book.markUpdated();
		}

		if (!dirty && !readStateDirty && getReadState() == EntryDisplayState.PENDING && ClientAdvancements.hasDone(turnin.toString())) {
			dirty = true;
		}

		if (dirty) {
			markReadStateDirty();
		}
	}

	public boolean isLocked() {
		if (isSecret()) {
			return locked;
		}
		return getBook().advancementsEnabled() && locked;
	}

	public boolean isSecret() {
		return secret;
	}

	public boolean shouldHide() {
		return isSecret() && isLocked();
	}

	public int getEntryColor() {
		return entryColor;
	}

	public ResourceLocation getId() {
		return id;
	}

	public boolean canAdd() {
		return (flag.isEmpty() || PatchouliConfig.getConfigFlag(flag));
	}

	public boolean isFoundByQuery(String query) {
		if (getName().getString().toLowerCase().contains(query)) {
			return true;
		}

		for (StackWrapper wrapper : relevantStacks) {
			if (wrapper.stack.getHoverName().getString().toLowerCase().contains(query)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int compareTo(BookEntry o) {
		if (o.locked != this.locked) {
			return this.locked ? 1 : -1;
		}

		EntryDisplayState ourState = getReadState();
		EntryDisplayState otherState = o.getReadState();

		if (ourState != otherState) {
			return ourState.compareTo(otherState);
		}

		if (o.priority != this.priority) {
			return this.priority ? -1 : 1;
		}

		int sort = this.sortnum - o.sortnum;

		return sort == 0 ? this.getName().getString().compareTo(o.getName().getString()) : sort;
	}

	public void build(Level level, BookContentsBuilder builder) {
		if (built) {
			return;
		}

		for (int i = 0; i < pages.length; i++) {
			if (pages[i].canAdd(book)) {
				try {
					pages[i].build(level, this, builder, i);
					realPages.add(pages[i]);
				} catch (Exception e) {
					throw new RuntimeException("Error while building entry %s page %d of book %s"
							.formatted(id, i, book.id), e);
				}
			}
		}

		if (extraRecipeMappings != null) {
			for (Entry<String, Integer> entry : extraRecipeMappings.entrySet()) {
				String key = entry.getKey();
				List<ItemStack> stacks;
				int pageNumber = entry.getValue();
				try {
					stacks = ItemStackUtil.loadStackListFromString(key, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
				} catch (Exception e) {
					PatchouliAPI.LOGGER.warn("Invalid extra recipe mapping: {} to page {} in entry {}: {}", key, pageNumber, id, e.getMessage());
					continue;
				}
				if (!stacks.isEmpty() && pageNumber < pages.length) {
					for (ItemStack stack : stacks) {
						addRelevantStack(builder, stack, pageNumber);
					}
				} else {
					PatchouliAPI.LOGGER.warn("Invalid extra recipe mapping: {} to page {} in entry {}: Empty entry or page out of bounds", key, pageNumber, id);
				}
			}
		}

		built = true;
	}

	public void addRelevantStack(BookContentsBuilder builder, ItemStack stack, int page) {
		if (stack.isEmpty()) {
			return;
		}
		StackWrapper wrapper = ItemStackUtil.wrapStack(stack);
		relevantStacks.add(wrapper);

		builder.addRecipeMapping(wrapper, this, page / 2);
	}

	/**
	 * @return The logical book this entry belongs to.
	 *         For entries added by extension books, this is the book being extended.
	 */
	public Book getBook() {
		return book;
	}

	@Nullable
	public String getAddedBy() {
		return this.addedBy;
	}

	@Override
	protected EntryDisplayState computeReadState() {
		BookData data = PersistentData.data.getBookData(book);
		if (data != null && getId() != null && !readByDefault && !isLocked() && !data.viewedEntries.contains(getId())) {
			return EntryDisplayState.UNREAD;
		}

		if (turnin != null && !ClientAdvancements.hasDone(turnin.toString())) {
			return EntryDisplayState.PENDING;
		}

		for (BookPage page : pages) {
			if (page instanceof PageQuest && ((PageQuest) page).isCompleted(book)) {
				return EntryDisplayState.COMPLETED;
			}
		}

		return EntryDisplayState.NEUTRAL;
	}

	@Override
	public void markReadStateDirty() {
		super.markReadStateDirty();
		getCategory().markReadStateDirty();
	}

}
