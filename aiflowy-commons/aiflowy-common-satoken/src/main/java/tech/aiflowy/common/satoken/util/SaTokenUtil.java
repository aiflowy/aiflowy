package tech.aiflowy.common.satoken.util;

import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.entity.LoginAccount;
import cn.dev33.satoken.stp.StpUtil;

public class SaTokenUtil {

    public static LoginAccount getLoginAccount() {
        return StpUtil.getSession().getModel(Constants.LOGIN_USER_KEY, LoginAccount.class);
    }
}
