package io.github.cotrin8672.cem.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity;
import io.github.cotrin8672.cem.util.EnchantableRules;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmartBlockEntity.class, remap = false)
public abstract class SmartBlockEntityEnchantmentsMixin implements EnchantableBlockEntity {
    @Unique
    private ItemEnchantments cem$enchantments = ItemEnchantments.EMPTY;
    @Unique
    private Item cem$sourceItem = null;

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

    @Inject(method = "read", at = @At("HEAD"))
    private void cem$readEnchantments(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket, CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        if (!EnchantableRules.isEnchantableBlockState(blockEntity.getBlockState())) return;
        this.readEnchantments(tag, provider);
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void cem$writeEnchantments(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket, CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        if (!EnchantableRules.isEnchantableBlockState(blockEntity.getBlockState())) return;
        this.writeEnchantments(tag, provider);
    }
}
