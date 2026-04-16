package com.example.house.entity.dto;

import lombok.Data;
import java.util.List;


@Data // 如果没有安装 Lombok 插件，请手动生成 Getter/Setter
public class HouseUploadDTO {
    private String title;
    private String communityId;
    private Integer price;
    private Integer area;
    private Integer roomCount;
    private Integer hallCount;
    private Integer floor;
    private Integer totalFloor;
    private String direction;
    private String description;

    // 关键点：这个字段必须和前端 JSON 中的 key 一致
    private List<String> photos;

    // 标签字段
    private List<String> tags;

    /* 如果不用 Lombok，请务必手动添加：
    public List<String> getPhotos() {
        return photos;
    }
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
    */
}