package net.minecraft.world.item.component;

import java.util.List;

public interface BookContent {
   List pages();

   Object withReplacedPages(List newPages);
}
