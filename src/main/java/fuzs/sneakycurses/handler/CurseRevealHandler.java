package fuzs.sneakycurses.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.sneakycurses.SneakyCurses;
import fuzs.sneakycurses.init.ModRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CurseRevealHandler {
    public static final Logger LOGGER = LogManager.getLogger(SneakyCurses.MOD_NAME);
    public static final String KEY_ITEM_CURSES_REVEALED = "item." + SneakyCurses.MOD_ID + ".curses_revealed";
    public static final String TAG_CURSES_REVEALED = SneakyCurses.id("curses_revealed").toString();

    @SuppressWarnings("deprecation")
    public static EventResult onAnvilUpdate(ItemStack leftInput, ItemStack rightInput, MutableValue<ItemStack> output,
            String itemName, MutableInt enchantmentCost, MutableInt materialCost, Player player) {
        if (isAffected(leftInput) && rightInput.is(ModRegistry.REVEALS_CURSES_ITEM_TAG)
                && !allCursesRevealed(leftInput)) {
            ItemStack itemStack = leftInput.copy();
            revealAllCurses(itemStack);
            output.accept(itemStack);
            materialCost.accept(SneakyCurses.CONFIG.server().revealCursesAmethystCost);
            enchantmentCost.accept(SneakyCurses.CONFIG.server().revealCursesExperienceCost);
            return EventResult.INTERRUPT;
        }
        return EventResult.PASS;
    }

    @SuppressWarnings("deprecation")
    public static EventResult onLivingTick(LivingEntity entity) {
        Level level = entity.getLevel();
        if (!level.isClientSide && entity.tickCount % SneakyCurses.CONFIG
                .server().revealChanceTickInterval == 0
                && (!(entity instanceof Player player) || !player.getAbilities().invulnerable)) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack = entity.getItemBySlot(equipmentSlot);
                if (Mob.getEquipmentSlotForItem(itemStack).equals(equipmentSlot) && anyEnchantIsCursed(itemStack)
                        && !allCursesRevealed(itemStack)) {
                    if (entity.getRandom()
                            .nextDouble() < SneakyCurses.CONFIG.server().curseRevealChance) {
                        revealAllCurses(itemStack);
                        entity.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F,
                                entity.getRandom().nextFloat() * 0.1F + 0.9F);
                        if (entity instanceof Player player
                                && SneakyCurses.CONFIG.client().notifyClientOfDecurseByWear) {
                            player.displayClientMessage(
                                    new TranslatableComponent(KEY_ITEM_CURSES_REVEALED, itemStack.getDisplayName())
                                            .withStyle(ChatFormatting.DARK_PURPLE),
                                    false);
                        }
                        break;
                    }
                }
            }
        }
        return EventResult.PASS;
    }

    public static void revealAllCurses(ItemStack itemStack) {
        if (!anyEnchantIsCursed(itemStack)) {
            return;
        }
        CompoundTag tag = itemStack.getTag();
        if (tag == null || !tag.getBoolean(TAG_CURSES_REVEALED)) {
            tag = itemStack.getOrCreateTag();
            tag.putBoolean(TAG_CURSES_REVEALED, true);
        }
    }

    public static boolean allCursesRevealed(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        CompoundTag tag = itemStack.getTag();
        return tag != null && tag.contains(TAG_CURSES_REVEALED, Tag.TAG_BYTE)
                && tag.getBoolean(TAG_CURSES_REVEALED);
    }

    public static boolean anyEnchantIsCursed(ItemStack itemStack) {
        return !itemStack.isEmpty() && EnchantmentHelper.getEnchantments(itemStack).keySet().stream()
                .filter(Objects::nonNull).anyMatch(Enchantment::isCurse);
    }

    @SuppressWarnings("deprecation")
    public static boolean isAffected(ItemStack itemStack) {
        if (itemStack.getItem() instanceof EnchantedBookItem) {
            if (!SneakyCurses.CONFIG.server().affectBooks) {
                return false;
            }
        }
        return anyEnchantIsCursed(itemStack);
    }
}
