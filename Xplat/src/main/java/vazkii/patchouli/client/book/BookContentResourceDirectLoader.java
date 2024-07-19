package vazkii.patchouli.client.book;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;

/**
 * BookContentLoader that directly queries the Resource Pack system when queried
 */
public final class BookContentResourceDirectLoader implements BookContentLoader {
	public static final BookContentResourceDirectLoader INSTANCE = new BookContentResourceDirectLoader();

	private BookContentResourceDirectLoader() {}

	@Override
	public void findFiles(Book book, String dir, List<ResourceLocation> list) {
		String prefix = String.format("%s/%s/%s/%s", BookRegistry.BOOKS_LOCATION, book.id.getPath(), BookContentsBuilder.DEFAULT_LANG, dir);
		Collection<ResourceLocation> files = Minecraft.getInstance().getResourceManager().listResources(prefix, p -> p.getPath().endsWith(".json")).keySet();

		files.stream()
				.distinct()
				.filter(file -> file.getNamespace().equals(book.id.getNamespace()))
				.map(file -> {
					// caller expects list to contain logical id's, not file paths.
					// we end up going from path -> id -> back to path, but it's okay as a transitional measure
					Preconditions.checkArgument(file.getPath().startsWith(prefix));
					Preconditions.checkArgument(file.getPath().endsWith(".json"));
					String newPath = file.getPath().substring(prefix.length(), file.getPath().length() - ".json".length());
					// Vanilla expects `prefix` above to not have a trailing slash, so we
					// have to remove it ourselves from the path
					if (newPath.startsWith("/")) {
						newPath = newPath.substring(1);
					}
					return ResourceLocation.fromNamespaceAndPath(file.getNamespace(), newPath);
				})
				.forEach(list::add);
	}

	@Nullable
	@Override
	public LoadResult loadJson(Book book, ResourceLocation file) {
		PatchouliAPI.LOGGER.debug("Loading {}", file);
		ResourceManager manager = Minecraft.getInstance().getResourceManager();
		try {
			var resourceOpt = manager.getResource(file);
			if (resourceOpt.isPresent()) {
				Resource resource = resourceOpt.get();
				return new LoadResult(
						BookContentLoader.streamToJson(resource.open()),
						computeAddedByText(resource.sourcePackId(), book)
				);
			} else {
				return null;
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	@Nullable
	private static String computeAddedByText(String sourcePackId, Book book) {
		// Resource packs are named by "file/<packname>"
		if (sourcePackId.startsWith("file/")) {
			sourcePackId = sourcePackId.substring(5);
		}

		// Forge and Fabric lump all mod resources into one dummy pack.
		// Unsure how to handle them for now. TODO: Fix this
		// We'd like to show only those packs that are not in the one that originally
		// declared the book.
		if (sourcePackId.equals("mod_resources") || sourcePackId.equals("fabric")) {
			return null;
		}

		return sourcePackId;
	}
}
