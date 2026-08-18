package net.minecraft.network.protocol;

import java.util.function.Function;
import net.minecraft.network.ProtocolInfo;

public interface SimpleUnboundProtocol extends ProtocolInfo.DetailsProvider {
   ProtocolInfo bind(Function contextWrapper);
}
