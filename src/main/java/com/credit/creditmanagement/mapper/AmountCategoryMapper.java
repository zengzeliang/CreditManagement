package com.credit.creditmanagement.mapper;

import com.credit.creditmanagement.entity.AmountCategory;
import org.apache.ibatis.annotations.Param;

public interface AmountCategoryMapper {
    AmountCategory selectAmountCategoryByType(@Param("amountType") String amountType);

}
