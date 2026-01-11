package com.rental.car.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {

    Optional<PricingRule> findByRuleCodeAndActiveTrue(String ruleCode);
    
    List<PricingRule> findByRuleTypeAndActiveTrue(PricingRule.PricingRuleType ruleType);
    
    List<PricingRule> findByActiveTrue();
}
