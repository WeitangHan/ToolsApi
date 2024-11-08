package com.wthan.toolapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wthan.toolapi.exception.Asserts;
import com.wthan.toolapi.mapper.ParsingInfoMapper;
import com.wthan.toolapi.model.dto.VideoInfoDto;
import com.wthan.toolapi.model.entity.ParsingInfo;
import com.wthan.toolapi.model.entity.WxUser;
import com.wthan.toolapi.service.VideoService;
import com.wthan.toolapi.service.WxUserService;
import com.wthan.toolapi.utils.RestTemplateUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;


@Service
public class VideoServiceImpl implements VideoService {
    @Resource(name = "wxUserService")
    private WxUserService wxUserService;

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Resource
    private ParsingInfoMapper parsingInfoMapper;

    @Override
    public VideoInfoDto getVideoInfo(String openid, String url) {
        WxUser wxUser = wxUserService.getUserInfoByOpenId(openid);
        if (wxUser == null) {
            Asserts.wxInfoFail("未查到用户信息");
        } else if (wxUser.getVideoNumber() < 1) {
            Asserts.wxInfoFail("解析次数已用完");
        }
        isContainsStrings(url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Mobile Safari/537.36");
        httpHeaders.set("Referer", url);
        VideoInfoDto videoInfoDto;
        //抖音快手Java解析其余短视频平台Java版本暂时没时间写
        videoInfoDto = parsingVideoInfo(url, httpHeaders);
        wxUser.setVideoNumber(wxUser.getVideoNumber() - 1);
        wxUser.setLastParsingTime(new Date());
        wxUserService.updateById(wxUser);
        ParsingInfo parsingInfo = new ParsingInfo();
        parsingInfo.setTitle(videoInfoDto.getTitle());
        parsingInfo.setDownloadUrl(videoInfoDto.getUrl());
        parsingInfo.setAuthor(videoInfoDto.getAuthor());
        parsingInfo.setCover(videoInfoDto.getCover());
        parsingInfo.setUserOpenId(wxUser.getOpenId());
        parsingInfo.setCreateTime(new Date());
        parsingInfoMapper.insert(parsingInfo);
        return videoInfoDto;
    }

    @Override
    public VideoInfoDto parsingVideoInfo(String url, HttpHeaders httpHeaders) {
        //调用第三方解析接口
        String dyWebApi = "https://tenapi.cn/v2/video?url=" + url;
        String content = restTemplateUtil.getForObject(dyWebApi, httpHeaders, String.class);
        Asserts.urlInfoNotNull(content, "API请求异常");
        JSONObject videoInfo = JSON.parseObject(content).getJSONObject("data");
        VideoInfoDto videoInfoDto = new VideoInfoDto();
        videoInfoDto.setAuthor(videoInfo.getString("author"));
        videoInfoDto.setUid(videoInfo.getString("uid"));
        videoInfoDto.setAvatar(videoInfo.getString("avatar"));
        videoInfoDto.setLike(videoInfo.getString("like"));
        videoInfoDto.setTime(videoInfo.getString("time"));
        videoInfoDto.setTitle(videoInfo.getString("title"));
        videoInfoDto.setCover(videoInfo.getString("cover"));
        videoInfoDto.setUrl(videoInfo.getString("url"));
        return videoInfoDto;
    }

    void isContainsStrings(String url) {
        String[] strings = new String[]{"pipix", "douyin", "huoshan", "h5.weishi", "isee.weishi", "weibo.com", "oasis.weibo", "zuiyou",
                "bbq.bilibili", "kuaishou", "quanmin", "moviebase", "hanyuhl", "eyepetizer", "immomo", "vuevideo",
                "xiaokaxiu", "ippzone", "qq.com", "ixigua.com"
        };
        for (String s : strings) {
            if (url.contains(s)) {
                return;
            }
        }
        Asserts.urlParsingFail("链接格式错误");
    }
}
