package net.minecraft.client.multiplayer;

import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.Identifier;

public record TransferState(Map cookies, Map seenPlayers, boolean seenInsecureChatWarning) {
}
