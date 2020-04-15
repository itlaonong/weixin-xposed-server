package com.itlaonong.xposed.weixin.httpd;

import com.alibaba.fastjson.JSON;
import com.itlaonong.framework.wechat.GoogleAuthenticator;
import com.itlaonong.framework.wechat.WxRequest;
import com.itlaonong.framework.wechat.WxResponse;
import com.itlaonong.framework.wechat.WxTextMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import fi.iki.elonen.NanoHTTPD;

import com.itlaonong.xposed.weixin.Constants;
import com.itlaonong.xposed.weixin.util.WxHelper;

public class WxHttpServer extends NanoHTTPD {

    private static String MINE_JSON = "application/json";

    public WxHttpServer(int port) {
        super(port);
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            XposedBridge.log(e);
        }
        XposedBridge.log("Running! Point your browsers to http://localhost:" + port + "/");
    }

    public WxHttpServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        WxResponse result = new WxResponse("ok", "处理成功");
        if (session.getMethod().equals(Method.POST)) {
            Map<String, String> files = new HashMap<>();
            WxRequest request = null;
            WxTextMessage message = null;
            try {
                session.parseBody(files);
                String param = files.get("postData");
                XposedBridge.log(JSON.toJSONString(param));
                request = JSON.parseObject(param, WxRequest.class);
                if (GoogleAuthenticator.authCode(request.getToken(), Constants.seed())) {
                    message = request.getMessage();
                } else {
                    result = new WxResponse("error", "处理异常，请检查参数");
                }

            } catch (Exception e) {
                result = new WxResponse("error", "处理异常，请检查参数");
                e.printStackTrace();
            }
            if (message != null) {
                WxHelper.sendTextMessage(message.getUserId(), message.getContent());
            }
        } else {
            result = new WxResponse("error", "请使用post方法调用");
        }
        return newFixedLengthResponse(Response.Status.OK, MINE_JSON, JSON.toJSONString(result));
    }


}
