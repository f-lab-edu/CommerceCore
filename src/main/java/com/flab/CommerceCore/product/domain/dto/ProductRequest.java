package com.flab.CommerceCore.product.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

  @NotBlank(message = "상품명은 필수 입력 항목입니다.")
  private String productName;

  @NotBlank(message = "상품 설명은 필수 입력 항목입니다.")
  private String description;

  @NotNull(message = "가격은 필수 입력 항목입니다.")
  @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
  private BigDecimal price;

  @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
  private int quantity;

}
