package com.credit.creditmanagement.service;

import com.credit.creditmanagement.entity.AmountCategory;
import org.springframework.stereotype.Service;

@Service
public interface AmountCategoryService {

    AmountCategory queryAmountCategoryByType(String amountType);
}
