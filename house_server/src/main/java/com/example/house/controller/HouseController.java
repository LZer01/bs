package com.example.house.controller;

import com.example.house.common.Result; // 假设你有统一返回类
import com.example.house.entity.HouseDetail;
import com.example.house.entity.dto.HouseUploadDTO;
import com.example.house.service.HouseDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/house")
public class HouseController {

    @Autowired
    private HouseDetailService houseDetailService;

    // 标签映射表
    private static final Map<String, Integer> TAG_MAP = new HashMap<>();
    static {
        TAG_MAP.put("阳台", 1);
        TAG_MAP.put("独立卫浴", 2);
        TAG_MAP.put("近地铁", 4);
        TAG_MAP.put("精装修", 8);
        TAG_MAP.put("拎包入住", 16);
    }


    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("文件不能为空");

        try {
            // 1. 获取并校验扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) return Result.error("无效的文件名");

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

            if (!allowedExtensions.contains(extension)) {
                return Result.error("不支持的文件格式");
            }

            // 2. 路径安全：只在程序目录下的 uploads 文件夹
            String folderPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            // 3. 彻底重命名：UUID + 纯粹的后缀名，杜绝路径穿越
            String fileName = UUID.randomUUID().toString() + "." + extension;
            File dest = new File(folder, fileName);

            // 4. 保存文件
            file.transferTo(dest);

            // 返回路径
            return Result.success("/uploads/" + fileName);
        } catch (IOException e) {
            return Result.error("图片保存失败");
        }
    }
    @PostMapping("/upload")
    public Result<String> uploadHouse(@RequestBody HouseUploadDTO uploadDTO) {
        // 1. 基础拷贝 (注意：price 字段因为类型不匹配会拷贝失败，保持为 null)
        HouseDetail houseDetail = new HouseDetail();
        BeanUtils.copyProperties(uploadDTO, houseDetail);

        // 2. 【核心修复】手动转换价格类型
        if (uploadDTO.getPrice() != null) {
            // 将 Integer 显式转为 BigDecimal
            houseDetail.setPrice(new java.math.BigDecimal(uploadDTO.getPrice()));
        } else {
            return Result.error("房源价格不能为空");
        }

        // 3. 处理照片列表：将 List<String> 转为 "url1,url2"
        if (uploadDTO.getPhotos() != null && !uploadDTO.getPhotos().isEmpty()) {
            houseDetail.setPhotos(String.join(",", uploadDTO.getPhotos()));
        }

        // 4. 计算 TagMask
        int mask = 0;
        if (uploadDTO.getTags() != null) {
            for (String tag : uploadDTO.getTags()) {
                mask |= TAG_MAP.getOrDefault(tag, 0);
            }
        }
        houseDetail.setTagMask(mask);

        // 5. 设置初始状态和时间
        houseDetail.setStatus(1); // 1-在售
        houseDetail.setCreateTime(LocalDateTime.now());
        houseDetail.setUpdateTime(LocalDateTime.now());

        // 6. 保存到数据库
        boolean saved = houseDetailService.save(houseDetail);

        return saved ? Result.success("发布成功") : Result.error("发布失败");
    }
}