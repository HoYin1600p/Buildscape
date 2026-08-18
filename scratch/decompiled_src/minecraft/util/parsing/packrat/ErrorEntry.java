package net.minecraft.util.parsing.packrat;

public record ErrorEntry(int cursor, SuggestionSupplier suggestions, Object reason) {
}
