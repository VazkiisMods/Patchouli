package vazkii.patchouli;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EphemeralTestServerProvider.class)
public class ItemParsingTest {
	private static final String ITEM_STRING = "minecraft:diamond_axe[minecraft:can_break={predicates:[{blocks:\"minecraft:oak_log\"}]},minecraft:unbreakable={show_in_tooltip:0b},minecraft:enchantments={levels:{\"minecraft:efficiency\":1}}]";
	private static final ResourceKey<Item> ITEM_KEY = ResourceKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("diamond_axe"));

	private static Holder.Reference<Item> ITEM;
	private static DataComponentPatch COMPONENTS;

	@BeforeAll
	public static void setup(MinecraftServer server) {
		HolderLookup.Provider lookup = server.registryAccess();
		HolderLookup.RegistryLookup<Item> items = lookup.lookupOrThrow(Registries.ITEM);
		HolderLookup.RegistryLookup<Enchantment> enchantments = lookup.lookupOrThrow(Registries.ENCHANTMENT);
		ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		mutable.set(enchantments.getOrThrow(Enchantments.EFFICIENCY), 1);
		ITEM = items.getOrThrow(ITEM_KEY);
		COMPONENTS = DataComponentPatch.builder()
				.set(DataComponents.CAN_BREAK, new AdventureModePredicate(List.of(BlockPredicate.Builder.block().of(Blocks.OAK_LOG).build()), true))
				.set(DataComponents.UNBREAKABLE, new Unbreakable(false))
				.set(DataComponents.ENCHANTMENTS, mutable.toImmutable())
				.build();
	}

	@Test
	public void testItemStackParsing(MinecraftServer server) {
		List<ItemStack> result = ItemStackUtil.loadStackListFromString(ITEM_STRING, server.registryAccess());
		assertEquals(1, result.size());
		assertEquals(ITEM, result.getFirst().getItemHolder());
		assertEquals(COMPONENTS, result.getFirst().getComponentsPatch());
		assertEquals(1, result.getFirst().getCount());
	}

	@Test
	public void testItemStackSerialization(MinecraftServer server) {
		ItemInput input = new ItemInput(ITEM, COMPONENTS);
		assertEquals(ITEM_STRING, input.serialize(server.registryAccess()));
	}
}
