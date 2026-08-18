package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;

public class IntersectionIterator extends AbstractIterator {
   private final PeekingIterator firstIterator;
   private final PeekingIterator secondIterator;
   private final Comparator comparator;

   public IntersectionIterator(final Iterator firstIterator, final Iterator secondIterator, final Comparator comparator) {
      this.firstIterator = Iterators.peekingIterator(firstIterator);
      this.secondIterator = Iterators.peekingIterator(secondIterator);
      this.comparator = comparator;
   }

   protected Object computeNext() {
      while(this.firstIterator.hasNext() && this.secondIterator.hasNext()) {
         int compare = this.comparator.compare(this.firstIterator.peek(), this.secondIterator.peek());
         if (compare == 0) {
            this.secondIterator.next();
            return this.firstIterator.next();
         }

         if (compare < 0) {
            this.firstIterator.next();
         } else {
            this.secondIterator.next();
         }
      }

      return this.endOfData();
   }
}
