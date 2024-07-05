package vazkii.patchouli.common.book;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.xplat.IXplatAbstractions;
import vazkii.patchouli.xplat.XplatModContainer;

import java.io.File;
import java.io.FileInputStream;

public class BookFolderLoader {

	public static File loadDir;

	private static void setup() {
		loadDir = new File(BookRegistry.BOOKS_LOCATION);
		if (!loadDir.exists()) {
			loadDir.mkdir();
		} else if (!loadDir.isDirectory()) {
			throw new RuntimeException(loadDir.getAbsolutePath() + " is a file, not a folder, aborting. Please delete this file or move it elsewhere if it has important contents.");
		}
	}

	public static void findBooks() {
		if (loadDir == null) {
			setup();
		}

		XplatModContainer self = IXplatAbstractions.INSTANCE.getModContainer(PatchouliAPI.MOD_ID);
		File[] subdirs = loadDir.listFiles(File::isDirectory);
		if (subdirs == null) {
			PatchouliAPI.LOGGER.warn("Failed to list external books in {}, not loading external books",
					loadDir.getAbsolutePath());
			return;
		}

		for (File dir : subdirs) {
			ResourceLocation res;
			try {
				res = ResourceLocation.fromNamespaceAndPath(PatchouliAPI.MOD_ID, dir.getName());
			} catch (ResourceLocationException ex) {
				PatchouliAPI.LOGGER.error("Invalid external book folder name {}, skipping", dir.getName(), ex);
				continue;
			}

			File bookJson = new File(dir, "book.json");
			try (FileInputStream stream = new FileInputStream(bookJson)) {
				BookRegistry.INSTANCE.loadBook(self, res, stream, true);
			} catch (Exception e) {
				PatchouliAPI.LOGGER.error("Failed to load external book json from {}, skipping", bookJson, e);
			}
		}
	}

}
