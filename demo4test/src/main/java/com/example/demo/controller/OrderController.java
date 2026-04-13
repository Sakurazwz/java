package com.example.demo.controller;

import com.example.demo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {
    private final Map<Long, OrderItem> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @GetMapping("/{id}")
    @Operation(summary = "查询订单", description = "根据订单ID查询订单详情")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    public Result<OrderItem> getOrder(
            @Parameter(name = "id", description = "订单ID", in = ParameterIn.PATH, required = true, example = "1")
            @PathVariable @Min(value = 1, message = "订单ID必须大于0") Long id) {
        OrderItem order = orderStore.get(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/list")
    @Operation(summary = "查询订单列表", description = "查询所有订单")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    public Result<List<OrderItem>> listOrders() {
        return Result.success(orderStore.values().stream().toList());
    }

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public Result<OrderItem> createOrder(
            @Parameter(description = "订单创建参数", required = true)
            @Valid @RequestBody OrderCreateDTO dto) {
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
    @Operation(summary = "更新订单", description = "根据订单ID更新订单信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    public Result<OrderItem> updateOrder(
            @Parameter(name = "id", description = "订单ID", in = ParameterIn.PATH, required = true, example = "1")
            @PathVariable @Min(value = 1, message = "订单ID必须大于0") Long id,
            @Parameter(description = "订单更新参数", required = true)
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
    @Operation(summary = "删除订单", description = "根据订单ID删除订单")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    public Result<String> deleteOrder(
            @Parameter(name = "id", description = "订单ID", in = ParameterIn.PATH, required = true, example = "1")
            @PathVariable @Min(value = 1, message = "订单ID必须大于0") Long id) {
        if (orderStore.remove(id) == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success("删除成功");
    }

    @GetMapping("/secure/list")
    @Operation(summary = "查询订单列表（需认证）", description = "模拟需要 Bearer Token 认证的订单查询接口")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(
                    responseCode = "401",
                    description = "未提供或未正确提供 Bearer Token",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            name = "未认证示例",
                            value = "{\"code\":401,\"message\":\"未授权\",\"data\":null}"
                    ))
            )
    })
    public Result<List<OrderItem>> secureListOrders(
            @Parameter(name = "Authorization", description = "Bearer Token，不包含该请求头将返回401", in = ParameterIn.HEADER,
                    required = true, example = "Bearer test-token")
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error(401, "未授权");
        }
        return Result.success(orderStore.values().stream().toList());
    }

    @PostMapping("/upload")
    @Operation(
        summary = "上传订单附件",
        description = """
            支持上传订单相关附件文件。
            限制条件：
            - 文件类型：jpg/png/pdf
            - 单个文件大小：≤5MB
            - 需要Bearer Token认证
            """
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
            @ApiResponse(
                responseCode = "200",
                description = "上传成功",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "成功示例",
                        value = "{\"code\":200,\"message\":\"上传成功: invoice.pdf\",\"data\":\"上传成功: invoice.pdf\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "文件格式或大小不符合要求",
                content = @Content(
                    mediaType = "application/json",
                    examples = {
                        @ExampleObject(
                            name = "文件过大示例",
                            value = "{\"code\":400,\"message\":\"文件大小不能超过5MB\",\"data\":null}"
                        ),
                        @ExampleObject(
                            name = "文件类型错误示例",
                            value = "{\"code\":400,\"message\":\"仅支持 jpg/png/pdf 文件\",\"data\":null}"
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "未授权",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "未授权示例",
                        value = "{\"code\":401,\"message\":\"未授权\",\"data\":null}"
                    )
                )
            )
    })
    public Result<String> uploadOrderAttachment(
            @Parameter(name = "Authorization", description = "Bearer Token", in = ParameterIn.HEADER, required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(
                description = "附件文件",
                required = true,
                content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(
                        type = "string",
                        format = "binary",
                        description = "支持的文件类型：image/jpeg, image/png, application/pdf"
                    )
                )
            )
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

    @Schema(description = "订单创建请求")
    public static class OrderCreateDTO {
        @NotBlank(message = "商品名称不能为空")
        @Schema(description = "商品名称", example = "Java 编程课程")
        private String productName;

        @NotNull(message = "订单金额不能为空")
        @Schema(description = "订单金额", example = "199.00")
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

    @Schema(description = "订单信息")
    public static class OrderItem {
        @Schema(description = "订单ID", example = "1")
        private Long id;
        @Schema(description = "订单编号", example = "ORD1743250000000")
        private String orderNo;
        @Schema(description = "商品名称", example = "Java 编程课程")
        private String productName;
        @Schema(description = "订单金额", example = "199.00")
        private BigDecimal amount;
        @Schema(description = "订单状态", example = "CREATED")
        private String status;
        @Schema(description = "创建时间")
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
