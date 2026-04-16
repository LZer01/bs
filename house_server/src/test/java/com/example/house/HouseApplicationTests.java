package com.example.house;

import com.example.house.entity.HouseListing;
import com.example.house.service.HouseListingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class HouseListingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HouseListingService houseListingService;

    @Autowired
    private ObjectMapper objectMapper; // 用于序列化对象为 JSON

    @Test
    void testCreateHouseListingSuccess() throws Exception {
        // 准备测试数据
        HouseListing house = new HouseListing();
        house.setHouseName("测试房源");
        house.setAddress("北京市朝阳区");
        house.setArea(new BigDecimal("120.50"));
        house.setLayout("2室1厅");
        house.setPrice(new BigDecimal("500.00"));
        house.setDescription("这是一个测试房源");
        house.setStatus("available");
        house.setIsPublished(true);
        house.setImageUrl("/uploads/test.jpg");

        // 发送 POST 请求
        mockMvc.perform(MockMvcRequestBuilders.post("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("房源创建成功"));

    }

    @Test
    void testCreateHouseListingInvalidInput() throws Exception {
        // 准备无效数据（缺少必填字段）
        HouseListing house = new HouseListing();
        house.setHouseName(""); // 空名称，应导致验证失败
        house.setAddress("北京市朝阳区");

        // 发送 POST 请求
        mockMvc.perform(MockMvcRequestBuilders.post("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(house)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // 期望 400 错误
    }

    @Test
    void testCreateHouseListingWrongContentType() throws Exception {
        // 准备测试数据
        HouseListing house = new HouseListing();
        house.setHouseName("测试房源");
        house.setAddress("北京市朝阳区");

        // 发送 POST 请求，使用错误的 Content-Type
        mockMvc.perform(MockMvcRequestBuilders.post("/create")
                        .contentType(MediaType.TEXT_PLAIN) // 错误的 Content-Type
                        .content("Invalid content"))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType()); // 期望 415 错误
    }
}