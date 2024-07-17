package vazkii.patchouli.api;

import net.minecraft.core.HolderLookup;

/**
 * A provider of variables to a template. Probably a JSON.
 */
public interface IVariableProvider {

	/**
	 * Gets the value assigned to the variable passed in.
	 * May throw an exception if it doesn't exist.
	 */
	IVariable get(String key, HolderLookup.Provider registries);

	/**
	 * Returns if a variable exists or not.
	 */
	boolean has(String key);

}
