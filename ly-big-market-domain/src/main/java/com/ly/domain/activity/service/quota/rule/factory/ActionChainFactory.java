package com.ly.domain.activity.service.quota.rule.factory;


import com.ly.domain.activity.service.quota.rule.IActionChain;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ActionChainFactory {

    private final IActionChain actionChain;

    public ActionChainFactory(Map<String, IActionChain> actionChainMap) {
        actionChain = actionChainMap.get(ActionChainType.ACTIVITY_BASE_ACTION_CHAIN.actionChainName);
        IActionChain skuStockActionChain
                = actionChainMap.get(ActionChainType.ACTIVITY_SKU_STOCK_ACTION_CHAIN.actionChainName);
        actionChain.addAction(skuStockActionChain);
    }

    public IActionChain getActionChain() {
        return actionChain;
    }

    public enum ActionChainType {
        ACTIVITY_BASE_ACTION_CHAIN("activity_base_action", "活动的库存、时间校验"),
        ACTIVITY_SKU_STOCK_ACTION_CHAIN("activity_sku_stock_action", "活动sku库存");

        private String actionChainName;

        private String msg;

        ActionChainType(String actionChainName, String msg) {
            this.actionChainName = actionChainName;
            this.msg = msg;
        }

        public String getActionChainName() {
            return actionChainName;
        }

        public String getMsg() {
            return msg;
        }
    }
}
