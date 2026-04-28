package io.zershyan.sccore.patchouli.datagen.create.data.format;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityFormat implements IFormat {
    private final String entity;
    private CompoundTag nbt;
    EntityFormat(String entity) {
        this.entity = entity;
    }

    public static EntityFormat of(ResourceLocation id) {
        return new EntityFormat(id.toString());
    }

    public static EntityFormat of(EntityType<?> entityType) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        if(key == null) throw new RuntimeException("Entity  " + entityType + " has no key");
        return of(key);
    }

    public static EntityFormat of(Entity entity) {
        EntityType<?> type = entity.getType();
        EntityFormat entityFormat = of(type);
        entityFormat.nbt = entity.saveWithoutId(new CompoundTag());
        return entityFormat;
    }

    public EntityFormat nbt(CompoundTag nbt) {
        this.nbt = nbt;
        return this;
    }

    public String parse() {
        StringBuilder sb = new StringBuilder(entity);
        if(nbt != null) sb.append(nbt);
        return sb.toString();
    }
}
