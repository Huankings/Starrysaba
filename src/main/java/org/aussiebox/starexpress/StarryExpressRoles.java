package org.aussiebox.starexpress;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.api.shop.ShopApi;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.events.ResetPlayerEvent;
import org.aussiebox.starexpress.cca.AbilityComponent;
import org.aussiebox.starexpress.cca.AllergicComponent;
import org.aussiebox.starexpress.cca.SilenceComponent;
import org.aussiebox.starexpress.cca.StarstruckComponent;

import java.util.HashMap;

public class StarryExpressRoles {

    private static final HashMap<String, Role> ROLES = new HashMap<>();

    public static HashMap<String, Role> getRoles() {
        return ROLES;
    }

    public static Role STARSTRUCK = registerRole(new Role(
            StarryExpress.id("starstruck"),
            0x5747ff,
            true,
            false,
            Role.MoodType.REAL,
            WatheRoles.CIVILIAN.getMaxSprintTime() + 100, // Civilian sprint time + 5 seconds
            false
    ));

    public static Role MUZZLER = registerRole(new Role(
            StarryExpress.id("muzzler"),
            0x370387,
            false,
            true,
            Role.MoodType.FAKE,
            WatheRoles.KILLER.getMaxSprintTime(),
            true
    ));

    public static void init() {

        /// STARSTRUCK
        Harpymodloader.setRoleMaximum(STARSTRUCK, 1);

        /// MUZZLER
        Harpymodloader.setRoleMaximum(MUZZLER, 1);
        /*
         * Muzzler 现在只通过 Wathe 商店修改器替换默认左轮格子。
         * 这样它能继续继承默认杀手商店的商品顺序、价格和特殊购买逻辑，
         * 只把“手枪位”换成自己的胶带。
         */
        ShopApi.registerShopModifier(
                StarryExpress.id("muzzler_shop"),
                ShopApi.DEFAULT_PRIORITY,
                MuzzlerShopHandler::modifyShop
        );

        ResetPlayerEvent.EVENT.register(player -> {
            AbilityComponent.KEY.get(player).reset();
            AllergicComponent.KEY.get(player).reset();
            StarstruckComponent.KEY.get(player).reset();
            SilenceComponent.KEY.get(player).reset();
        });

    }

    public static Role registerRole(Role role) {
        WatheRoles.registerRole(role);
        ROLES.put(role.identifier().getPath(), role);
        return role;
    }

}
