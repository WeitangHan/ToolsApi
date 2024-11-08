package com.wthan.toolapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.wthan.toolapi.model.entity.WxUser;
import com.wthan.toolapi.model.support.BaseResponse;
import com.wthan.toolapi.service.WxUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;


@RestController
@RequestMapping("/api/wx")
public class WxController {

    @Resource
    private WxUserService wxUserService;

    @PostMapping(value = "auth")
    public BaseResponse<String> wxAuth(@RequestParam(value = "js_code") String code) {
        String openId = wxUserService.getOpenId(code);
        return BaseResponse.ok("openId获取成功", openId);
    }

    /**
     * 登录、注册、获取用户信息
     *
     * @param openId
     * @return
     */
    @PostMapping(value = "login")
    public BaseResponse<WxUser> login(@RequestParam(value = "openId") String openId, String name) {
        WxUser wxUser = wxUserService.getUserInfoByOpenId(openId);
        if (wxUser == null) {
            wxUser = new WxUser();
            wxUser.setName(name);
            wxUser.setOpenId(openId);
            wxUser.setVideoNumber(999);
            wxUser.setSignInSum(1);
            wxUser.setCreateTime(new Date());
            wxUserService.insert(wxUser);
        }
        return BaseResponse.ok(wxUser);
    }

    /***
     * 签到
     * @param openId
     * @return
     */
    @PostMapping(value = "signIn")
    public BaseResponse<WxUser> sign(@RequestParam(value = "openId") String openId) {
        WxUser wxUser = wxUserService.singIn(openId);
        return BaseResponse.ok(wxUser);
    }

    /***
     * 联系作者 获取配置的作者信息
     * @param openId
     * @return
     */
    @PostMapping(value = "showQRCode")
    public BaseResponse<String[]> showQRCode(@RequestParam(value = "openId") String openId) {
        String[] strArr = new String[1];
        /* demo 测试数据 用户发布测试使用 后续替换实际的作者联系的二维码*/
        strArr[0] = "https://img2.baidu.com/it/u=1390815689,3409386485&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=400";
        return BaseResponse.ok(strArr);
    }


    /***
     * 联系作者 获取配置的作者信息
     * @param openId
     * @return
     */
    @PostMapping(value = "initConfig")
    public BaseResponse<JSONObject> initConfig(@RequestParam(value = "openId") String openId) {
        System.out.println("openId: " + openId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("topMiniTitle", "今日上新 每天十点 全网低价");
        jsonObject.put("topMiniImg", "https://p0.meituan.net/marketingcpsmedia/007ed47076777924c79e81deca52b448130615.png");
        jsonObject.put("topMiniAppId", "wxde8ac0a21135c07d");
        jsonObject.put("topMiniPath", "/index/pages/h5/h5?weburl=https%3A%2F%2Fclick.meituan.com%2Ft%3Ft%3D1%26c%3D2%26p%3DD2Qxu75zpKgi");

String s = "[{\"imgPath\": \"../../images/video-icon/logo-douyin.png\"},\n" +
        "\t{\"imgPath\": \"../../images/video-icon/logo-gitShow.png\"},\n" +
        "\t{\"imgPath\": \"../../images/video-icon/logo-zuiyou.png\"},\n" +
        "\t{\"imgPath\": \"../../images/video-icon/logo-ppx.png\"},\n" +
        "\t{\"imgPath\": \"../../images/video-icon/logo-microview.png\"},\n" +
        "\t{\"imgPath\": \"../../images/video-icon/logo-volcano.png\"},\n" +
        "\t{\"imgPath\": \"../../images/video-icon/logo-music.png\"}]";

        jsonObject.put("videoIcon", JSONObject.parseArray(s));
        return BaseResponse.ok(jsonObject);
    }
}
