package vazkii.patchouli.api.stub;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

import vazkii.patchouli.api.IMultiblock;

import java.util.Collection;
import java.util.Collections;

public class StubMultiblock implements IMultiblock {

	public static final StubMultiblock INSTANCE = new StubMultiblock();

	private StubMultiblock() {}

	@Override
	public IMultiblock offset(int x, int y, int z) {
		return this;
	}

	@Override
	public IMultiblock offsetView(int x, int y, int z) {
		return this;
	}

	@Override
	public IMultiblock setSymmetrical(boolean symmetrical) {
		return this;
	}

	@Override
	public ResourceLocation getID() {
		return ResourceLocation.fromNamespaceAndPath("patchouli", "stub");
	}

	@Override
	public IMultiblock setId(ResourceLocation res) {
		return this;
	}

	@Override
	public boolean isSymmetrical() {
		return false;
	}

	@Override
	public void place(Level world, BlockPos pos, Rotation rotation) {
		// NO-OP
	}

	@Override
	public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView) {
		return Pair.of(BlockPos.ZERO, Collections.emptyList());
	}

	@Override
	public Rotation validate(Level world, BlockPos pos) {
		return null;
	}

	@Override
	public boolean validate(Level world, BlockPos pos, Rotation rotation) {
		return false;
	}

	@Override
	public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
		return false;
	}

	@Override
	public Vec3i getSize() {
		return Vec3i.ZERO;
	}

}
