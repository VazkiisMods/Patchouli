package vazkii.patchouli.neoforge.xplat;

import net.neoforged.fml.ModContainer;

import vazkii.patchouli.xplat.XplatModContainer;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class NeoForgeXplatModContainer implements XplatModContainer {
	private final ModContainer container;

	public NeoForgeXplatModContainer(ModContainer container) {
		this.container = container;
	}

	@Override
	public String getId() {
		return container.getModId();
	}

	@Override
	public String getName() {
		return container.getModInfo().getDisplayName();
	}

	@Override
	public Path getPath(String s) {
		return container.getModInfo().getOwningFile().getFile().findResource(s);
	}

	@Override
	public List<Path> getRootPaths() {
		return Collections.singletonList(container.getModInfo().getOwningFile().getFile().getSecureJar().getRootPath());
	}
}
