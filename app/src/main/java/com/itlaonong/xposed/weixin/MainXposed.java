package com.itlaonong.xposed.weixin;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import com.itlaonong.xposed.weixin.util.WxHelper;

public class MainXposed implements IXposedHookLoadPackage {

    //微信主进程名
    private static final String WECHAT_PROCESS_NAME = "com.tencent.mm";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.processName.equals(WECHAT_PROCESS_NAME)) {
            return;
        }
        XposedBridge.log("weixin_hook" + "进入微信进程：" + loadPackageParam.processName);
        if (!WxHelper.checkInit()) {
            WxHelper.init(loadPackageParam);
        }

    }

}