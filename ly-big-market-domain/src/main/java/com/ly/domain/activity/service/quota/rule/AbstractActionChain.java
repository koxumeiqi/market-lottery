package com.ly.domain.activity.service.quota.rule;

public abstract class AbstractActionChain implements IActionChain {

    private IActionChain next;

    @Override
    public IActionChain addAction(IActionChain action) {
        this.next = action;
        return next;
    }

    @Override
    public IActionChain next() {
        return this.next;
    }
}
