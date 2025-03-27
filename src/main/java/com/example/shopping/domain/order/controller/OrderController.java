package com.example.shopping.domain.order.controller;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.order.dto.request.CreateOrderRequestDto;
import com.example.shopping.domain.order.dto.response.CreateOrderResponseDto;
import com.example.shopping.domain.order.dto.response.GetOrderResponseDto;
import com.example.shopping.domain.order.dto.response.GetOrdersResponseDto;
import com.example.shopping.domain.order.dto.response.UpdateOrderResponseDto;
import com.example.shopping.domain.order.service.OrderService;
import com.example.shopping.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/v1/orders")
    public ResponseEntity<CreateOrderResponseDto> saveOrder(
        @AuthenticationPrincipal User user,
        CreateOrderRequestDto dto
    ) {
        return ResponseEntity.ok(orderService.saveOrder(user.getId(), dto));
    }
    
    @GetMapping("/api/v1/orders")
    public ResponseEntity<GetOrderResponseDto> getOrder(
        @AuthenticationPrincipal User user,
        @RequestParam Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(user.getId(), orderId));
    }
    
    @GetMapping("/api/v1/orderss")
    public ResponseEntity<PageResponseDto<GetOrdersResponseDto>> getOrders(
        @AuthenticationPrincipal User user,
        @PageableDefault(page = 1, size = 10) Pageable pageable
    ) {
        Pageable convertPageable =
            PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        
        return ResponseEntity.ok(orderService.getOrders(user.getId(), convertPageable));
    }
    
    @PatchMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<UpdateOrderResponseDto> updateOrder(
        @AuthenticationPrincipal User user,
        @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.updateOrder(user.getId(), orderId));
    }
}
