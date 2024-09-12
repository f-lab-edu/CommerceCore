package com.flab.CommerceCore.common.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryConstant {

  public static final String FIND_INVENTORY_BY_PRODUCT_ID =
      "select i from Inventory i where i.product.productId = :productId";
  public static final String FIND_ALL_INVENTORY_BY_PRODUCT_ID =
      "select i from Inventory i where i.product.productId in :productIds";
}
