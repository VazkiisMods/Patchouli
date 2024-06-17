package vazkii.patchouli.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An advancement trigger for opening Patchouli books.
 */
public class BookOpenTrigger extends SimpleCriterionTrigger<BookOpenTrigger.TriggerInstance> {
	public static final ResourceLocation ID = new ResourceLocation(PatchouliAPI.MOD_ID, "open_book");
	public static final BookOpenTrigger INSTANCE = new BookOpenTrigger();

	@NotNull
	@Override
	public Codec<TriggerInstance> codec() {
		return BookOpenTrigger.TriggerInstance.CODEC;
	}

	public void trigger(@NotNull ServerPlayer player, @NotNull ResourceLocation book) {
		trigger(player, instance -> instance.matches(book, null, 0));
	}

	public void trigger(@NotNull ServerPlayer player, @NotNull ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		trigger(player, instance -> instance.matches(book, entry, page));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceLocation book, Optional<ResourceLocation> entry, MinMaxBounds.Ints page) implements SimpleInstance {

		public static Codec<BookOpenTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
				ResourceLocation.CODEC.fieldOf("book").forGetter(TriggerInstance::book),
				ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "entry").forGetter(TriggerInstance::entry),
				ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "page", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::page)
		).apply(instance, TriggerInstance::new));

		public boolean matches(@NotNull ResourceLocation book, @Nullable ResourceLocation entry, int page) {
			return this.book.equals(book) && (this.entry.isEmpty() || this.entry.get().equals(entry)) && this.page.matches(page);
		}
	}
}
