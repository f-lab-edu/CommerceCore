package com.flab.CommerceCore.product.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

  @NotBlank(message = "가격은 필수 입력 항목입니다.")
  @Min(0)
  private BigDecimal price;

  @NotBlank
  private int quantity;

}
