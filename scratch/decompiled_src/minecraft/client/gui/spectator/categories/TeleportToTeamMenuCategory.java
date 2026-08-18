package net.minecraft.client.gui.spectator.categories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.TeamColor;

public class TeleportToTeamMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final Identifier TELEPORT_TO_TEAM_SPRITE = Identifier.withDefaultNamespace("spectator/teleport_to_team");
   private static final Component TELEPORT_TEXT = Component.translatable("spectatorMenu.team_teleport");
   private static final Component TELEPORT_PROMPT = Component.translatable("spectatorMenu.team_teleport.prompt");
   private final List items;

   public TeleportToTeamMenuCategory() {
      Minecraft minecraft = Minecraft.getInstance();
      this.items = createTeamEntries(minecraft, minecraft.level.getScoreboard());
   }

   private static List createTeamEntries(final Minecraft minecraft, final Scoreboard scoreboard) {
      return scoreboard.getPlayerTeams().stream().flatMap((team) -> TeleportToTeamMenuCategory.TeamSelectionItem.create(minecraft, team).stream()).toList();
   }

   public List getItems() {
      return this.items;
   }

   public Component getPrompt() {
      return TELEPORT_PROMPT;
   }

   public void selectItem(final SpectatorMenu menu) {
      menu.selectCategory(this);
   }

   public Component getName() {
      return TELEPORT_TEXT;
   }

   public void extractIcon(final GuiGraphicsExtractor graphics, final float brightness, final float alpha) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TELEPORT_TO_TEAM_SPRITE, 0, 0, 16, 16, ARGB.colorFromFloat(alpha, brightness, brightness, brightness));
   }

   public boolean isEnabled() {
      return !this.items.isEmpty();
   }

   private static class TeamSelectionItem implements SpectatorMenuItem {
      private final PlayerTeam team;
      private final Supplier iconSkin;
      private final List players;

      private TeamSelectionItem(final PlayerTeam team, final List players, final Supplier iconSkin) {
         this.team = team;
         this.players = players;
         this.iconSkin = iconSkin;
      }

      public static Optional create(final Minecraft minecraft, final PlayerTeam team) {
         List players = new ArrayList();

         for(String name : team.getPlayers()) {
            PlayerInfo info = minecraft.getConnection().getPlayerInfo(name);
            if (info != null && info.getGameMode() != GameType.SPECTATOR) {
               players.add(info);
            }
         }

         if (players.isEmpty()) {
            return Optional.empty();
         } else {
            PlayerInfo playerInfo = (PlayerInfo)players.get(RandomSource.createThreadLocalInstance().nextInt(players.size()));
            return Optional.of(new TeleportToTeamMenuCategory.TeamSelectionItem(team, players, playerInfo::getSkin));
         }
      }

      public void selectItem(final SpectatorMenu menu) {
         menu.selectCategory(new TeleportToPlayerMenuCategory(this.players));
      }

      public Component getName() {
         return this.team.getDisplayName();
      }

      public void extractIcon(final GuiGraphicsExtractor graphics, final float brightness, final float alpha) {
         Optional teamColor = this.team.getColor();
         if (teamColor.isPresent()) {
            graphics.fill(1, 1, 15, 15, ARGB.scaleRGB(((TeamColor)teamColor.get()).rgb(), brightness));
         }

         PlayerFaceExtractor.extractRenderState(graphics, (PlayerSkin)this.iconSkin.get(), 2, 2, 12, ARGB.colorFromFloat(alpha, brightness, brightness, brightness));
      }

      public boolean isEnabled() {
         return true;
      }
   }
}
