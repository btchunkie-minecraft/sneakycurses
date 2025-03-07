package fuzs.sneakycurses.config;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;

@SuppressWarnings("removal")
public class ServerConfig extends AbstractConfig {
    @Config(name = "obfuscate_curses_on_tooltips", description = "Obfuscate curse enchantments with enchantment runes on item tooltips.")
    public boolean obfuscateCurses = true;
    @Config(name = "tint_enchantment_glint_when_cursed", description = "Tint the enchantment glint in a red shade when the item is enchanted with curses.")
    public boolean cursedItemGlint = true;
    @Config(description = "Obfuscate curses on enchanted books.")
    public boolean affectBooks = true;
    @Config(name = "shift_shows_curses_in_creative", description = "Temporarily show curses as normal text while any shift key is held in creative mode.")
    public boolean shiftShows = true;
    @Config(name = "experience_cost_for_revealing_curses_in_anvil", description = "Amount of experience levels required to make curses on an item visible by combining with an item from the 'sneakycurses:reveal_curses' tag (amethyst shards by default) in an anvil.")
    @Config.IntRange(min = 1)
    public int revealCursesExperienceCost = 5;
    @Config(name = "material_cost_for_revealing_curses_in_anvil", description = "Amount of 'sneakycurses:reveal_curses' tag (amethyst shards by default) required to make curses on an item visible.")
    @Config.IntRange(min = 1, max = 64)
    public int revealCursesAmethystCost = 32;
    @Config(name = "tick_interval_for_possible_reveal_during_usage", description = "The tick interval at which a piece of equipement is attempted to be decursed. The probability that an item is decursed during an attempt depends on the 'curse_reveal_chance' value.")
    @Config.IntRange(min = 1)
    public int revealChanceTickInterval = 1200;
    @Config(name = "curse_reveal_chance", description = "Chance wearing or using a cursed piece of equipment will trigger the curses to be revealed. Set to 0.0 to disable revealing curses this way.")
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double curseRevealChance = 0.05;

    @SuppressWarnings("deprecation")
    public ServerConfig() {
        super("");
    }
}
