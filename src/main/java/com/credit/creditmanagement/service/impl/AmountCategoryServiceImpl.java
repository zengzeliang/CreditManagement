package com.credit.creditmanagement.service.impl;

import com.credit.creditmanagement.entity.AmountCategory;
import com.credit.creditmanagement.mapper.AmountCategoryMapper;
import com.credit.creditmanagement.service.AmountCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmountCategoryServiceImpl implements AmountCategoryService {

    @Autowired
    private AmountCategoryMapper amountCategoryMapper;

    @Override
    public AmountCategory queryAmountCategoryByType(String amountType) {
        AmountCategory amountCategory = amountCategoryMapper.selectAmountCategoryByType(amountType);
        return amountCategory;
    }
}
