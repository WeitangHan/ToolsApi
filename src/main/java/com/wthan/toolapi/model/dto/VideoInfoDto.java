package com.wthan.toolapi.model.dto;

import lombok.Data;

@Data
public class VideoInfoDto {
    private String author;//视频作者
    private String uid;//作者ID
    private String avatar;//作者头像
    private String like;//点赞数
    private String time;
    private String title; //视频标题
    private String cover;//视频封面
    private String url;//视频无水印链接
}
