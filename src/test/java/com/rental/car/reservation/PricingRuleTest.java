package com.rental.car.reservation;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

import java.math.BigDecimal;

public class PricingRuleTest {

    private PricingRule rule;

    @BeforeMethod
    public void setUp() {
        rule = new PricingRule();
    }

    @Test
    public void testPricingRuleCreation() {
        PricingRule r = new PricingRule(
            1L, "TAX_10", "Sales Tax 10%", PricingRule.PricingRuleType.TAX,
            new BigDecimal("10.00"), null, null, true
        );

        assertEquals(r.getId(), Long.valueOf(1L));
        assertEquals(r.getRuleCode(), "TAX_10");
        assertEquals(r.getRuleType(), PricingRule.PricingRuleType.TAX);
        assertEquals(r.getPercentageValue(), new BigDecimal("10.00"));
        assertTrue(r.isActive());
    }

    @Test
    public void testSettersAndGetters() {
        rule.setId(2L);
        rule.setRuleCode("ONE_WAY");
        rule.setDescription("One-way rental fee");
        rule.setRuleType(PricingRule.PricingRuleType.ONE_WAY_FEE);
        rule.setFixedAmount(new BigDecimal("50.00"));
        rule.setActive(true);

        assertEquals(rule.getId(), Long.valueOf(2L));
        assertEquals(rule.getRuleCode(), "ONE_WAY");
        assertEquals(rule.getRuleType(), PricingRule.PricingRuleType.ONE_WAY_FEE);
        assertEquals(rule.getFixedAmount(), new BigDecimal("50.00"));
        assertTrue(rule.isActive());
    }

    @Test
    public void testPricingRuleTypeEnum() {
        assertEquals(PricingRule.PricingRuleType.TAX.name(), "TAX");
        assertEquals(PricingRule.PricingRuleType.ONE_WAY_FEE.name(), "ONE_WAY_FEE");
        assertEquals(PricingRule.PricingRuleType.LENGTH_DISCOUNT.name(), "LENGTH_DISCOUNT");
    }

    @Test
    public void testPercentageBasedRule() {
        rule.setRuleType(PricingRule.PricingRuleType.TAX);
        rule.setPercentageValue(new BigDecimal("8.50"));
        rule.setFixedAmount(null);

        assertEquals(rule.getPercentageValue(), new BigDecimal("8.50"));
        assertNull(rule.getFixedAmount());
    }

    @Test
    public void testFixedAmountRule() {
        rule.setRuleType(PricingRule.PricingRuleType.AIRPORT_FEE);
        rule.setFixedAmount(new BigDecimal("25.00"));
        rule.setPercentageValue(null);

        assertEquals(rule.getFixedAmount(), new BigDecimal("25.00"));
        assertNull(rule.getPercentageValue());
    }

    @Test
    public void testMinDaysDiscount() {
        rule.setRuleType(PricingRule.PricingRuleType.LENGTH_DISCOUNT);
        rule.setMinDays(7);
        rule.setPercentageValue(new BigDecimal("15.00"));

        assertEquals(rule.getMinDays(), Integer.valueOf(7));
        assertEquals(rule.getPercentageValue(), new BigDecimal("15.00"));
    }
}
