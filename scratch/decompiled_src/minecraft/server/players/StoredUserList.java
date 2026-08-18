package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.server.notifications.NotificationService;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class StoredUserList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final File file;
   private final Map map = Maps.newHashMap();
   protected final NotificationService notificationService;

   public StoredUserList(final File file, final NotificationService notificationService) {
      this.file = file;
      this.notificationService = notificationService;
   }

   public File getFile() {
      return this.file;
   }

   public boolean add(final StoredUserEntry infos) {
      String keyForUser = this.getKeyForUser(infos.getUser());
      StoredUserEntry previous = (V)((StoredUserEntry)this.map.get(keyForUser));
      if (infos.equals(previous)) {
         return false;
      } else {
         this.map.put(keyForUser, infos);

         try {
            this.save();
         } catch (IOException var5) {
            LOGGER.warn("Could not save the list after adding a user.", var5);
         }

         return true;
      }
   }

   public @Nullable StoredUserEntry get(final Object user) {
      this.removeExpired();
      return (StoredUserEntry)this.map.get(this.getKeyForUser(user));
   }

   public boolean remove(final Object user) {
      StoredUserEntry removed = (V)((StoredUserEntry)this.map.remove(this.getKeyForUser(user)));
      if (removed == null) {
         return false;
      } else {
         try {
            this.save();
         } catch (IOException var4) {
            LOGGER.warn("Could not save the list after removing a user.", var4);
         }

         return true;
      }
   }

   public boolean remove(final StoredUserEntry infos) {
      return this.remove(Objects.requireNonNull(infos.getUser()));
   }

   public void clear() {
      this.map.clear();

      try {
         this.save();
      } catch (IOException var2) {
         LOGGER.warn("Could not save the list after removing a user.", var2);
      }

   }

   public String[] getUserList() {
      return (String[])this.map.keySet().toArray(new String[0]);
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   protected String getKeyForUser(final Object user) {
      return user.toString();
   }

   protected boolean contains(final Object user) {
      return this.map.containsKey(this.getKeyForUser(user));
   }

   private void removeExpired() {
      List toRemove = Lists.newArrayList();

      for(StoredUserEntry entry : this.map.values()) {
         if (entry.hasExpired()) {
            toRemove.add(entry.getUser());
         }
      }

      for(Object user : toRemove) {
         this.map.remove(this.getKeyForUser(user));
      }

   }

   protected abstract StoredUserEntry createEntry(final JsonObject object);

   public Collection getEntries() {
      return this.map.values();
   }

   public void save() throws IOException {
      JsonArray result = new JsonArray();
      this.map.values().stream().map((entry) -> (JsonObject)Util.make(new JsonObject(), entry::serialize)).forEach(result::add);
      BufferedWriter writer = Files.newWriter(this.file, StandardCharsets.UTF_8);

      try {
         GSON.toJson(result, GSON.newJsonWriter(writer));
      } catch (Throwable var6) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (writer != null) {
         writer.close();
      }

   }

   public void load() throws IOException {
      if (this.file.exists()) {
         BufferedReader reader = Files.newReader(this.file, StandardCharsets.UTF_8);

         label54: {
            try {
               this.map.clear();
               JsonArray contents = (JsonArray)GSON.fromJson(reader, JsonArray.class);
               if (contents == null) {
                  break label54;
               }

               for(JsonElement element : contents) {
                  JsonObject object = GsonHelper.convertToJsonObject(element, "entry");
                  StoredUserEntry entry = this.createEntry(object);
                  if (entry.getUser() != null) {
                     this.map.put(this.getKeyForUser(entry.getUser()), entry);
                  }
               }
            } catch (Throwable var8) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (reader != null) {
               reader.close();
            }

            return;
         }

         if (reader != null) {
            reader.close();
         }

      }
   }
}
