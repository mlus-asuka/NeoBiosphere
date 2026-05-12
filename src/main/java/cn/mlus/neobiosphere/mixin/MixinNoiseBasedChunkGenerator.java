package cn.mlus.neobiosphere.mixin;

import cn.mlus.neobiosphere.carver.SphereBridgeCarver;
import cn.mlus.neobiosphere.carver.SphereCarver;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Objects;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class MixinNoiseBasedChunkGenerator {
    @Inject(method = "applyCarvers", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), cancellable = true)
    private void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step, CallbackInfo ci){
        NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator)(Object)this;

        BiomeManager biomemanager = biomeManager.withDifferentSource((p_255581_, p_255582_, p_255583_) -> generator.getBiomeSource().getNoiseBiome(p_255581_, p_255582_, p_255583_, random.sampler()));
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        ChunkPos chunkpos = chunk.getPos();
        NoiseChunk noisechunk = chunk.getOrCreateNoiseChunk((p_224250_) -> createNoiseChunk(p_224250_, structureManager, Blender.of(level), random));
        Aquifer aquifer = noisechunk.aquifer();
        CarvingContext carvingcontext = new CarvingContext(generator, level.registryAccess(), chunk.getHeightAccessorForGeneration(), noisechunk, random, settings.value().surfaceRule());
        CarvingMask carvingmask = ((ProtoChunk)chunk).getOrCreateCarvingMask(step);

        int i = 0;
        int p = 0;

        for(int j = -8; j <= 8; ++j) {
            for (int k = -8; k <= 8; ++k) {
                ChunkPos chunkpos1 = new ChunkPos(chunkpos.x + j, chunkpos.z + k);
                ChunkAccess chunkaccess = level.getChunk(chunkpos1.x, chunkpos1.z);
                BiomeGenerationSettings biomegenerationsettings = chunkaccess.carverBiome(() -> generator.getBiomeGenerationSettings(generator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(chunkpos1.getMinBlockX()), 0, QuartPos.fromBlock(chunkpos1.getMinBlockZ()), random.sampler())));
                Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = biomegenerationsettings.getCarvers(step);
                int l = 0;

                for(Iterator<Holder<ConfiguredWorldCarver<?>>> var24 = iterable.iterator(); var24.hasNext(); ++l) {
                    Holder<ConfiguredWorldCarver<?>> holder = var24.next();
                    ConfiguredWorldCarver<?> configuredworldcarver = holder.value();
                    worldgenrandom.setLargeFeatureSeed(seed + (long)l, chunkpos1.x, chunkpos1.z);

                    if(configuredworldcarver.worldCarver() instanceof SphereCarver){
                        if(level.getLevel().dimension() != Level.OVERWORLD)
                            ci.cancel();

                        if(configuredworldcarver.worldCarver() instanceof SphereBridgeCarver){
                            if(p > 0)
                                continue;
                            p++;
                        }else if(i > 0){
                            continue;
                        }else {
                            i++;
                        }

                        ChunkPos chunkPosCenter = new ChunkPos(chunkpos.x, chunkpos.z);
                        if (configuredworldcarver.isStartChunk(worldgenrandom)) {
                            Objects.requireNonNull(biomemanager);
                            configuredworldcarver.carve(carvingcontext, chunk, biomemanager::getBiome, worldgenrandom, aquifer, chunkPosCenter, carvingmask);
                        }
                    }
                }
            }
        }

        if(i > 0 && p > 0){
            ci.cancel();
        }
    }

    @Shadow protected abstract NoiseChunk createNoiseChunk(ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState random);

    @Shadow @Final private Holder<NoiseGeneratorSettings> settings;
}
