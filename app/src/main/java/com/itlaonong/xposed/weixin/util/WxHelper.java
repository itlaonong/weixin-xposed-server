package com.itlaonong.xposed.weixin.util;

import com.itlaonong.xposed.weixin.httpd.WxHttpServer;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WxHelper {

    private static ClassLoader classLoader;

    public static synchronized void init(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (checkInit()) {
            return;
        }
        classLoader = loadPackageParam.classLoader;
        WxHttpServer server = new WxHttpServer(9000);
        XposedBridge.log("微信助手初始化完成");

    }

    public static boolean checkInit() {
        if (classLoader != null) {
            return true;
        }
        return false;
    }

    //发送文本消息
    public static void sendTextMessage(final String strChatroomId, String strContent) {
        //WebLogUtil.log("准备回复消息内容：content:" + strContent + ",chatroomId:" + strChatroomId);
        if (!checkInit()) {
            XposedBridge.log("微信助手为初始化");
            return;
        }
        if (StringUtil.isEmpty(strContent) || StringUtil.isEmpty(strChatroomId)) {
            return;
        }
        //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
        Class<?> classiVar = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.h", classLoader);
        Object objectiVar = XposedHelpers.newInstance(classiVar, strChatroomId, strContent, 1);
        //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.model.av", classLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "Pw");
        //调用发消息方法
        try {
            XposedHelpers.callMethod(objectG, "a", objectiVar, 0);
            XposedBridge.log("{\"log\":\"invokeOriginalMethod()执行成功\"");
        } catch (Exception e) {
            XposedBridge.log("调用微信消息回复方法异常");
            XposedBridge.log(e.getMessage());
        }
    }

}
