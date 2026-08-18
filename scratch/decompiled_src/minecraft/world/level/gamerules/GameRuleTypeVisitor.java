package net.minecraft.world.level.gamerules;

public interface GameRuleTypeVisitor {
   default void visit(final GameRule gameRule) {
   }

   default void visitBoolean(final GameRule gameRule) {
   }

   default void visitInteger(final GameRule gameRule) {
   }
}
