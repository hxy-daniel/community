package com.cqupt.community.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * JSON响应工具类
 */
public class JsonResponseUtils {

    public static String toJsonResponse(int code, String msg, Map<String, Object> data) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (data != null) {
            for (String key : data.keySet()) {
                json.put(key, data.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String toJsonResponse(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String toJsonResponse(int code) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }
}
