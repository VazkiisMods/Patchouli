package vazkii.patchouli.client.book;

import com.google.common.collect.Streams;
import com.google.gson.JsonObject;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class BookCategory extends AbstractReadStateHolder implements Comparable<BookCategory> {

	private final ResourceLocation id;
	private final String name;
	private final String description;
	private final BookIcon icon;
	@Nullable private final String parent;
	private final String flag;
	private final int sortnum;
	private final boolean secret;

	// Logical book we belong to
	private final Book book;

	// Mutable state
	@Nullable private BookCategory parentCategory;
	private final List<BookCategory> children = new ArrayList<>();
	private final List<BookEntry> entries = new ArrayList<>();
	private boolean locked;
	private boolean built;
	// End mutable state

	public BookCategory(JsonObject root, ResourceLocation id, Book book) {
		this.book = book;
		this.id = id;
		this.name = GsonHelper.getAsString(root, "name");
		this.description = GsonHelper.getAsString(root, "description");
		this.icon = BookIcon.from(GsonHelper.getAsString(root, "icon"));
		this.parent = GsonHelper.getAsString(root, "parent", null);
		this.flag = GsonHelper.getAsString(root, "flag", "");
		this.sortnum = GsonHelper.getAsInt(root, "sortnum", 0);
		this.secret = GsonHelper.getAsBoolean(root, "secret", false);
	}

	public MutableComponent getName() {
		return book.i18n ? Component.translatable(name) : Component.literal(name);
	}

	public String getDescription() {
		return description;
	}

	public BookIcon getIcon() {
		return icon;
	}

	public void addEntry(BookEntry entry) {
		this.entries.add(entry);
	}

	public void addChildCategory(BookCategory category) {
		children.add(category);
	}

	public List<BookEntry> getEntries() {
		return entries;
	}

	@Nullable
	public BookCategory getParentCategory() {
		return parentCategory;
	}

	public void updateLockStatus(boolean rootOnly) {
		if (rootOnly && !isRootCategory()) {
			return;
		}

		children.forEach((c) -> c.updateLockStatus(false));

		boolean wasLocked = locked;

		updateLocked: {
			locked = !children.isEmpty() || !entries.isEmpty(); // empty categories are unlocked by default
			for (BookCategory c : children) {
				if (!c.isLocked()) {
					locked = false;
					break updateLocked;
				}
			}

			for (BookEntry e : entries) {
				if (!e.isLocked()) {
					locked = false;
					break updateLocked;
				}
			}
		}

		if (!locked && wasLocked) {
			book.markUpdated();
		}
	}

	public boolean isSecret() {
		return secret;
	}

	public boolean shouldHide() {
		return isSecret() && isLocked();
	}

	public boolean isLocked() {
		return getBook().advancementsEnabled() && locked;
	}

	public boolean isRootCategory() {
		return parent == null || parent.isEmpty();
	}

	public ResourceLocation getId() {
		return id;
	}

	public boolean canAdd() {
		return flag.isEmpty() || PatchouliConfig.getConfigFlag(flag);
	}

	@Override
	public int compareTo(BookCategory o) {
		if (book.advancementsEnabled() && o.locked != this.locked) {
			return this.locked ? 1 : -1;
		}

		if (this.sortnum != o.sortnum) {
			return Integer.compare(this.sortnum, o.sortnum);
		}

		return this.getName().getString().compareTo(o.getName().getString());
	}

	public void build(BookContentsBuilder builder) {
		if (built) {
			return;
		}

		if (!isRootCategory()) {
			if (parent.contains(":")) {
				var parentCat = builder.getCategory(ResourceLocation.tryParse(parent));
				if (parentCat == null) {
					var msg = String.format("Category %s specifies parent %s, but it could not be found", id, parent);
					throw new RuntimeException(msg);
				} else {
					parentCat.addChildCategory(this);
					this.parentCategory = parentCat;
				}
			} else {
				String hint = String.format("`%s:%s`", book.id.getNamespace(), parent);
				throw new IllegalArgumentException("`parent` must be fully qualified (domain:name). Hint: Try " + hint);
			}
		}

		built = true;
	}

	public Book getBook() {
		return book;
	}

	@Override
	protected EntryDisplayState computeReadState() {
		Stream<EntryDisplayState> entryStream = entries.stream().filter(e -> !e.isLocked()).map(BookEntry::getReadState);
		Stream<EntryDisplayState> childrenStream = children.stream().map(BookCategory::getReadState);
		return mostImportantState(Streams.concat(entryStream, childrenStream));
	}

	@Override
	public void markReadStateDirty() {
		super.markReadStateDirty();

		if (parentCategory != null) {
			parentCategory.markReadStateDirty();
		} else {
			book.getContents().markReadStateDirty();
		}
	}

}
