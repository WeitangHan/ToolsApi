package com.wthan.toolapi.service;

import com.wthan.toolapi.model.dto.VideoInfoDto;
import org.springframework.http.HttpHeaders;



public interface VideoService {
    /**
     * 解析视频并保存解析信息
     * @param openid
     * @param url
     * @return
     */
    VideoInfoDto getVideoInfo(String openid, String url);

    /**
     * d调用第三方接口解析
     * @param url 分享链接
     * @return
     */
    VideoInfoDto parsingVideoInfo(String url, HttpHeaders httpHeaders);

}
