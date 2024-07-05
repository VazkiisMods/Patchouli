package vazkii.patchouli.mixin.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.SequencedMap;

@Mixin(MultiBufferSource.BufferSource.class)
public interface AccessorMultiBufferSource {
	@Accessor("sharedBuffer")
	ByteBufferBuilder getFallbackBuffer();

	@Accessor("fixedBuffers")
	SequencedMap<RenderType, ByteBufferBuilder> getFixedBuffers();
}
