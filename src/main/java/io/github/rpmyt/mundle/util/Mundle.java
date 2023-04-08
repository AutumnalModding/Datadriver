package io.github.rpmyt.mundle.util;

/**
 * Example:
 * <pre>
 * {
 *   "name": "Example Mundle",
 *   "ident": "example_mundle",
 *   "features": [
 *     {
 *       "uses": [
 *         "mundle:thaumcraft$aspect"
 *       ],
 *       "name": "Explodus Aspect",
 *       "ident": "add_explodus",
 *       "description": "Adds an 'Explodus' aspect.",
 *       "requiredMods": [
 *         "Thaumcraft"
 *       ],
 *       "contents": [
 *         "ASPECT_NAME=explodus",
 *         "ASPECT_COMPOUNDS=perditio,potentia",
 *         "ASPECT_COLOR=FF0000"
 *       ]
 *     },
 *     {
 *       "uses": [
 *         "mundle:thaumcraft$shielding",
 *         "mundle:thaumcraft$vis_armor",
 *         "mundle:botania$mana_item",
 *         "mundle:botania$mana_armor",
 *         "mundle:vanilla$armor"
 *       ],
 *       "name": "Botanist's Robes",
 *       "ident": "botanist_robes",
 *       "description": "Adds robes that give a vis discount and mana proficiency.",
 *       "requiredMods": [
 *         "Thaumcraft",
 *         "Botania"
 *       ],
 *       "contents": [
 *         "SHIELDING_HEAD=2",
 *         "SHIELDING_CHEST=4",
 *         "SHIELDING_LEGS=3",
 *         "SHIELDING_FEET=1",
 *         "PROFICIENCY_SINGLE",
 *         "MANA_COST=20",
 *         "PROTECTION_HEAD=5",
 *         "PROTECTION_CHEST=5",
 *         "PROTECTION_LEGS=5",
 *         "PROTECTION_FEET=5",
 *         "DURABILITY_HEAD=500",
 *         "DURABILITY_CHEST=500",
 *         "DURABILITY_LEGS=500",
 *         "DURABILITY_FEET=500"
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 */
public class Mundle {
    public static class Feature {
        public String name;
        public String ident;
        public String[] uses;
        public String[] contents;
        public String description;
        public String[] requiredMods;
    }

    public String name;
    public String ident;
    public Feature[] features;
}
