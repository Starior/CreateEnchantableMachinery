package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

/**
 * {@link SmartBlockEntityEnchantmentsMixin} only applies to {@link com.simibubi.create.foundation.blockEntity.SmartBlockEntity}.
 * Vanilla Create (and Create Encased) use {@link HarvesterBlockEntity} which extends
 * {@link com.simibubi.create.foundation.blockEntity.CachedRenderBBBlockEntity} instead, so enchantments from
 * {@link BlockItemMixin} were never stored and goggles had nothing to show.
 * <p>
 */
@Mixin(value = HarvesterBlockEntity.class, remap = false)
public abstract class HarvesterBlockEntityEnchantmentsMixin implements EnchantableBlockEntity, IHaveGoggleInformation {
    @Unique
    private ItemEnchantments cem$enchantments = ItemEnchantments.EMPTY;
    @Unique
    private Item cem$sourceItem = null;

    @Unique
    private boolean cem$isPlainCreateHarvester() {
        return ((Object) this).getClass() == HarvesterBlockEntity.class;
    }

    @Override
    public ItemEnchantments getEnchantments() {
        return this.cem$enchantments;
    }

    @Override
    public void setEnchantment(ItemEnchantments enchantments) {
        this.cem$enchantments = enchantments;
    }

    @Override
    public Item getSourceItem() {
        return this.cem$sourceItem;
    }

    @Override
    public void setSourceItem(Item item) {
        this.cem$sourceItem = item;
    }

    @Override
    public int getEnchantmentLevel(Holder<Enchantment> enchantment) {
        return this.cem$enchantments.getLevel(enchantment);
    }

    @Override
    public void readEnchantments(CompoundTag tag, HolderLookup.Provider provider) {
        if (tag.contains("Enchantments")) {
            var registryOps = provider.createSerializationContext(NbtOps.INSTANCE);
            ItemEnchantments.CODEC
                    .parse(registryOps, tag.get("Enchantments"))
                    .resultOrPartial()
                    .ifPresent(this::setEnchantment);
        } else {
            setEnchantment(ItemEnchantments.EMPTY);
        }

        if (tag.contains("SourceItem")) {
            String itemId = tag.getString("SourceItem");
            ResourceLocation location = ResourceLocation.tryParse(itemId);
            if (location != null && BuiltInRegistries.ITEM.containsKey(location)) {
                setSourceItem(BuiltInRegistries.ITEM.get(location));
            } else {
                setSourceItem(null);
            }
        } else {
            setSourceItem(null);
        }
    }

    @Override
    public void writeEnchantments(CompoundTag tag, HolderLookup.Provider provider) {
        if (!getEnchantments().isEmpty()) {
            var registryOps = provider.createSerializationContext(NbtOps.INSTANCE);
            ItemEnchantments.CODEC
                    .encodeStart(registryOps, getEnchantments())
                    .resultOrPartial()
                    .ifPresent(encoded -> tag.put("Enchantments", encoded));
        }

        Item sourceItem = getSourceItem();
        if (sourceItem != null) {
            tag.putString("SourceItem", BuiltInRegistries.ITEM.getKey(sourceItem).toString());
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!cem$isPlainCreateHarvester()) {
            return false;
        }
        if (!((Object) this instanceof EnchantableBlockEntity enchantable)) {
            return false;
        }
        if (enchantable.getEnchantments().isEmpty()) {
            return false;
        }
        for (var entry : enchantable.getEnchantments().entrySet()) {
            CreateLang.builder()
                    .add(Enchantment.getFullname(entry.getKey(), entry.getIntValue()))
                    .forGoggles(tooltip, 1);
        }
        return true;
    }
}
