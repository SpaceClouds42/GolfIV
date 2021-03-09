package org.samo_lego.golfiv.mixin_checks.illegals;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import org.samo_lego.golfiv.casts.ItemStackChecker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

/**
 * Additional methods for checking ItemStack's legality.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixinCast_itemStackChecker implements ItemStackChecker {

    @Shadow private int count;

    @Shadow public abstract ListTag getEnchantments();

    @Shadow public abstract void removeSubTag(String key);

    @Shadow public abstract int getMaxCount();

    private final ItemStack itemStack = (ItemStack) (Object) this;

    /**
     * Sets the appropriate ItemStack size,
     * removes disallowed enchantments.
     *
     * @return false if item was already legal, otherwise true
     */
    @Override
    public boolean makeLegal() {
        boolean illegal = false;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(this.getEnchantments());

        for(Map.Entry<Enchantment, Integer> ench : enchantments.entrySet()) {
            Enchantment enchantment = ench.getKey();
            int level = ench.getValue();

            Set<Enchantment> otherEnchants = EnchantmentHelper.get(this.itemStack).keySet();
            otherEnchants.remove(enchantment);

            if(!enchantment.isAcceptableItem(this.itemStack) || !EnchantmentHelper.isCompatible(otherEnchants, enchantment) || level > enchantment.getMaxLevel()) {
                this.removeSubTag("Enchantments");
                illegal = true;
                break;
            }
        }
        if(this.count > this.getMaxCount()) {
            illegal = true;
            this.count = 1;
        }
        return illegal;
    }
}
