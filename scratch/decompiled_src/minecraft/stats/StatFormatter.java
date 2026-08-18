package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public interface StatFormatter {
   DecimalFormat DECIMAL_FORMAT = new DecimalFormat("########0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
   StatFormatter DEFAULT = NumberFormat.getIntegerInstance(Locale.US)::format;
   StatFormatter DIVIDE_BY_TEN = (value) -> DECIMAL_FORMAT.format((double)value * 0.1D);
   StatFormatter DISTANCE = (cm) -> {
      double meters = (double)cm / 100.0D;
      double kilometers = meters / 1000.0D;
      if (kilometers > 0.5D) {
         return DECIMAL_FORMAT.format(kilometers) + " km";
      } else {
         return meters > 0.5D ? DECIMAL_FORMAT.format(meters) + " m" : cm + " cm";
      }
   };
   StatFormatter TIME = (value) -> {
      double seconds = (double)value / 20.0D;
      double minutes = seconds / 60.0D;
      double hours = minutes / 60.0D;
      double days = hours / 24.0D;
      double years = days / 365.0D;
      if (years > 0.5D) {
         return DECIMAL_FORMAT.format(years) + " y";
      } else if (days > 0.5D) {
         return DECIMAL_FORMAT.format(days) + " d";
      } else if (hours > 0.5D) {
         return DECIMAL_FORMAT.format(hours) + " h";
      } else {
         return minutes > 0.5D ? DECIMAL_FORMAT.format(minutes) + " min" : seconds + " s";
      }
   };

   String format(int value);
}
