package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.common.MoneyUtils;
import com.garrettw011.orderflow.common.exception.InsufficientInventoryException;
import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import com.garrettw011.orderflow.customer.Customer;
import com.garrettw011.orderflow.customer.CustomerService;
import com.garrettw011.orderflow.product.Product;
import com.garrettw011.orderflow.product.ProductRepository;
import com.garrettw011.orderflow.inventory.InventoryItem;
import com.garrettw011.orderflow.inventory.InventoryReservation;
import com.garrettw011.orderflow.inventory.InventoryReservationStatus;
import com.garrettw011.orderflow.inventory.InventoryReservationRepository;
import com.garrettw011.orderflow.inventory.InventoryRepository;
import com.garrettw011.orderflow.order.dto.OrderCreateRequest;
import com.garrettw011.orderflow.order.dto.OrderLineRequest;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import com.garrettw011.orderflow.order.dto.OrderItemResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderCreationService {
    private final CustomerService customerService;
    private final ProductRepository products;
    private final InventoryRepository inventory;
    private final InventoryReservationRepository reservations;
    private final OrderRepository orders;
    private final OrderProperties orderProperties;

    public OrderCreationService(CustomerService customerService, ProductRepository products,
                                InventoryRepository inventory, InventoryReservationRepository reservations,
                                OrderRepository orders, OrderProperties orderProperties) {
        this.customerService = customerService;
        this.products = products;
        this.inventory = inventory;
        this.reservations = reservations;
        this.orders = orders;
        this.orderProperties = orderProperties;
    }

    @Transactional
    public OrderResponse create(Long userId, OrderCreateRequest req) {
        Customer customer = customerService.getByUserId(userId);

        // merge any dup line items
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderLineRequest line : req.items()) { quantities.merge(line.productId(), line.quantity(), Integer::sum); }

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.RESERVED);

        Instant expiresAt = Instant.now().plus(orderProperties.reservationTtl());
        List<InventoryReservation> pending = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            // if inactive or unknown => 404
            Product product = products.findById(productId).filter(Product::isActive)
                    .orElseThrow(() -> new ResourceNotFoundException("No product available for id: " + productId));

            // reserve items under optimistic lock guard
            InventoryItem stock = inventory.findByProductId(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("No available inventory for id: " + productId));
            if (stock.getAvailableQuantity() < quantity) {
                // TODO: Slot in behavior to reserve available quantity rather than rollback entire order?
                throw new InsufficientInventoryException("Insufficient inventory for " + product.getSku()
                        + " {requested: " + quantity + ", available: " + stock.getAvailableQuantity());
            }
            stock.setQuantityReserved(stock.getQuantityReserved() + quantity);
            inventory.saveAndFlush(stock);      // version check occurs (conflict throws ex)

            // create line item using price snapshot
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getPrice());
            item.setLineTotal(MoneyUtils.lineTotal(product.getPrice(), quantity));
            order.addItem(item);
            subtotal = subtotal.add(item.getLineTotal());

            InventoryReservation resv = new InventoryReservation();
            resv.setProduct(product);
            resv.setQuantity(quantity);
            resv.setStatus(InventoryReservationStatus.ACTIVE);
            resv.setExpiresAt(expiresAt);
            pending.add(resv);
        }

        subtotal = MoneyUtils.normalize(subtotal);
        BigDecimal tax = MoneyUtils.taxOf(subtotal, orderProperties.taxRate());
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(MoneyUtils.normalize(subtotal.add(tax)));

        Order saved = orders.save(order);
        for (InventoryReservation reservation : pending) { reservation.setOrder(saved); }
        reservations.saveAll(pending);

        return toResponse(saved);
    }

    OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProduct().getId(), i.getProduct().getSku(), i.getProduct().getName(),
                        i.getQuantity(), i.getUnitPrice(), i.getLineTotal()))
                .toList();

        return new OrderResponse(order.getId(), order.getCustomer().getId(), order.getStatus().name(),
                                 order.getSubtotal(), order.getTax(), order.getTotal(),
                                 items, order.getCreatedAt(), order.getUpdatedAt());
    }
}

