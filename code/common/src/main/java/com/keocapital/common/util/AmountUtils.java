package com.keocapital.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AmountUtils {

  private AmountUtils() {}

  public static double roundToCents(Number amount) {
    return new BigDecimal(amount.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}
