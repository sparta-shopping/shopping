package com.example.shopping.domain.order.controller;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.order.dto.request.CreateOrderRequestDto;
import com.example.shopping.domain.order.dto.response.CreateOrderResponseDto;
import com.example.shopping.domain.order.dto.response.GetOrderResponseDto;
import com.example.shopping.domain.order.dto.response.GetOrdersResponseDto;
import com.example.shopping.domain.order.dto.response.UpdateOrderResponseDto;
import com.example.shopping.domain.order.service.OrderService;
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
        @AuthenticationPrincipal AuthUser authUser,
        @RequestBody CreateOrderRequestDto dto
    ) {
        return ResponseEntity.ok(orderService.saveOrder(authUser.getId(), dto));
    }
    
    @GetMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<GetOrderResponseDto> getOrder(
        @AuthenticationPrincipal AuthUser authUser,
        @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(authUser.getId(), orderId));
    }
    
    @GetMapping("/api/v1/orders")
    public ResponseEntity<PageResponseDto<GetOrdersResponseDto>> getOrders(
        @AuthenticationPrincipal AuthUser authUser,
        @PageableDefault(page = 1, size = 10) Pageable pageable
    ) {
        Pageable convertPageable =
            PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        
        return ResponseEntity.ok(orderService.getOrders(authUser.getId(), convertPageable));
    }
    
    @PatchMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<UpdateOrderResponseDto> updateOrder(
        @AuthenticationPrincipal AuthUser authUser,
        @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.updateOrder(authUser.getId(), orderId));
    }
}
