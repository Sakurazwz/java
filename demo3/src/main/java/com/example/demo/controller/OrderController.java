package com.example.demo.controller;

import com.example.demo.common.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final Map<Long, OrderItem> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @GetMapping("/{id}")
    public Result<OrderItem> getOrder(@PathVariable @Min(value = 1, message = "订单ID必须大于0") Long id) {
        OrderItem order = orderStore.get(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/list")
    public Result<List<OrderItem>> listOrders() {
        return Result.success(orderStore.values().stream().toList());
    }

    @PostMapping
    public Result<OrderItem> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        OrderItem order = new OrderItem();
        order.setId(idGenerator.getAndIncrement());
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setProductName(dto.getProductName());
        order.setAmount(dto.getAmount());
        order.setStatus("CREATED");
        order.setCreateTime(LocalDateTime.now());
        orderStore.put(order.getId(), order);
        return Result.success(order);
    }

    @PutMapping("/{id}")
    public Result<OrderItem> updateOrder(
            @PathVariable @Min(value = 1, message = "订单ID必须大于0") Long id,
            @Valid @RequestBody OrderCreateDTO dto) {
        OrderItem order = orderStore.get(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        order.setProductName(dto.getProductName());
        order.setAmount(dto.getAmount());
        return Result.success(order);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteOrder(@PathVariable @Min(value = 1, message = "订单ID必须大于0") Long id) {
        if (orderStore.remove(id) == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success("删除成功");
    }

    @GetMapping("/secure/list")
    public Result<List<OrderItem>> secureListOrders(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error(401, "未授权");
        }
        return Result.success(orderStore.values().stream().toList());
    }

    @PostMapping("/upload")
    public Result<String> uploadOrderAttachment(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestPart("file") MultipartFile file) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error(401, "未授权");
        }
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("文件大小不能超过5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("application/pdf"))) {
            return Result.error("仅支持 jpg/png/pdf 文件");
        }
        return Result.success("上传成功: " + file.getOriginalFilename());
    }

    public static class OrderCreateDTO {
        @NotBlank(message = "商品名称不能为空")
        private String productName;

        @NotNull(message = "订单金额不能为空")
        private BigDecimal amount;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class OrderItem {
        private Long id;
        private String orderNo;
        private String productName;
        private BigDecimal amount;
        private String status;
        private LocalDateTime createTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }
    }
}
