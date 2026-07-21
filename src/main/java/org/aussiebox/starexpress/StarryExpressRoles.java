package org.aussiebox.starexpress;

import org.agmas.harpymodloader.events.ResetPlayerEvent;
import org.aussiebox.starexpress.cca.AllergicComponent;

public class StarryExpressRoles {

    public static void init() {
        ResetPlayerEvent.EVENT.register(player -> {
            /*
             * StarryExpress 现在不再拥有星界使者/静语者组件。
             * 重置入口只清理仍属于本模组的 allergic 词条状态，避免误碰已经搬到 NoellesRoles 的职业组件。
             */
            AllergicComponent.KEY.get(player).reset();
        });
    }

}
