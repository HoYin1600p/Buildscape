package net.minecraft.client.resources.server;

import java.util.Map;
import java.util.function.Consumer;

public interface PackDownloader {
   void download(Map requests, Consumer output);
}
