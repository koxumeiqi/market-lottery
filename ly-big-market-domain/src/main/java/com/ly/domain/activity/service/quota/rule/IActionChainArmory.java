package com.ly.domain.activity.service.quota.rule;

public interface IActionChainArmory {

    IActionChain addAction(IActionChain action);

    IActionChain next();

}
