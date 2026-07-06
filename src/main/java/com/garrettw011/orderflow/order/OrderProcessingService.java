package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.common.exception.InvalidStateTransitionException;
import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import com.garrettw011.orderflow.customer.Customer;
import com.garrettw011.orderflow.customer.CustomerService;
import com.garrettw011.orderflow.inventory.InventoryItem;
import com.garrettw011.orderflow.inventory.InventoryRepository;
import com.garrettw011.orderflow.inventory.InventoryReservation;
import com.garrettw011.orderflow.inventory.InventoryReservationRepository;
import com.garrettw011.orderflow.inventory.InventoryReservationStatus;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import com.garrettw011.orderflow.payment.Payment;
import com.garrettw011.orderflow.payment.PaymentResult;
import com.garrettw011.orderflow.payment.PaymentStatus;
import com.garrettw011.orderflow.payment.PaymentGateway;
import com.garrettw011.orderflow.payment.PaymentRepository;
import com.garrettw011.orderflow.payment.dto.PaymentRequest;
import com.garrettw011.orderflow.payment.dto.PaymentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderProcessingService {
    private final OrderRepository orders;
    private final InventoryRepository inventory;
    private final InventoryReservationRepository reservations;
    private final PaymentRepository payments;
    private final PaymentGateway gateway;
    private final CustomerService customerService;
    private final OrderCreationService creationService;

    public OrderProcessingService(OrderRepository orders, InventoryRepository inventory,
                               InventoryReservationRepository reservations, PaymentRepository payments,
                               PaymentGateway gateway, CustomerService customerService,
                               OrderCreationService creationService) {
        this.orders = orders;
        this.inventory = inventory;
        this.reservations = reservations;
        this.payments = payments;
        this.gateway = gateway;
        this.customerService = customerService;
        this.creationService = creationService;
    }

    @Transactional
    public PaymentResponse pay(Long userId, Long orderId, PaymentRequest req, boolean isAdmin) {
        Order order = loadOwned(userId, orderId, isAdmin);
        requireStatus(order, OrderStatus.RESERVED);

        PaymentResult result = gateway.charge(order.getTotal(), req.paymentToken(), "order-" + order.getId());
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(req.provider());
        payment.setAmount(order.getTotal());

        if (result.success()) {
            payment.setStatus(PaymentStatus.CAPTURED);
            payment.setTransactionReference(result.reference());
            consume(order);
            order.setStatus(OrderStatus.PAID);
        }
        else {
            payment.setStatus(PaymentStatus.FAILED);
            release(order, InventoryReservationStatus.RELEASED);
            order.setStatus(OrderStatus.FAILED);
        }
        Payment saved = payments.save(payment);
        return toPaymentResponse(saved, order);
    }

    @Transactional
    public OrderResponse cancel(Long userId, Long orderId, boolean isAdmin) {
        Order order = loadOwned(userId, orderId, isAdmin);
        requireStatus(order, OrderStatus.RESERVED);
        release(order, InventoryReservationStatus.RELEASED);
        order.setStatus(OrderStatus.CANCELED);
        return creationService.toResponse(order);
    }

    @Transactional
    public OrderResponse startFulfillment(Long orderId) {
        Order order = loadDetail(orderId);
        requireStatus(order, OrderStatus.PAID);
        order.setStatus(OrderStatus.FULFILLING);
        return creationService.toResponse(order);
    }

    @Transactional
    public OrderResponse ship(Long orderId) {
        Order order = loadDetail(orderId);
        requireStatus(order, OrderStatus.FULFILLING);
        order.setStatus(OrderStatus.SHIPPED);
        return creationService.toResponse(order);
    }

    @Transactional
    public void expireOrder(Long orderId) {
        Order order = orders.findDetailById(orderId).orElse(null);
        if (order == null || order.getStatus() != OrderStatus.RESERVED) { return; }
        release(order, InventoryReservationStatus.EXPIRED);
        order.setStatus(OrderStatus.CANCELED);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listPayments(Long userId, Long orderId, boolean isAdmin) {
        Order order = loadOwned(userId, orderId, isAdmin);
        return payments.findByOrderId(order.getId()).stream()
                .map(p -> toPaymentResponse(p, order))
                .toList();
    }

    private void consume(Order order) {
        for (InventoryReservation r : activeReservations(order)) {
            InventoryItem stock = stockFor(r);
            stock.setQuantityOnHand(stock.getQuantityOnHand() - r.getQuantity());
            stock.setQuantityReserved(stock.getQuantityReserved() - r.getQuantity());
            inventory.saveAndFlush(stock);
            r.setStatus(InventoryReservationStatus.CONSUMED);
            reservations.save(r);
        }
    }

    private void release(Order order, InventoryReservationStatus finalStatus) {
        for (InventoryReservation r : activeReservations(order)) {
            InventoryItem stock = stockFor(r);
            stock.setQuantityReserved(stock.getQuantityReserved() - r.getQuantity());
            inventory.saveAndFlush(stock);
            r.setStatus(finalStatus);
            reservations.save(r);
        }
    }

    private List<InventoryReservation> activeReservations(Order order) {
        return reservations.findByOrderId(order.getId()).stream()
                .filter(r -> r.getStatus() == InventoryReservationStatus.ACTIVE)
                .toList();
    }

    private InventoryItem stockFor(InventoryReservation r) {
        return inventory.findByProductId(r.getProduct().getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("No product found for id: " + r.getProduct().getId()));
    }

    private Order loadOwned(Long userId, Long orderId, boolean isAdmin) {
        Order order = loadDetail(orderId);
        if (!isAdmin) {
            Customer customer = customerService.getByUserId(userId);
            if (!order.getCustomer().getId().equals(customer.getId())) {
                throw new ResourceNotFoundException("No order found for id: " + orderId);
            }
        }
        return order;
    }

    private Order loadDetail(Long orderId) {
        return orders.findDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found for id: " + orderId));
    }

    private void requireStatus(Order order, OrderStatus expected) {
        if (order.getStatus() != expected) {
            throw new InvalidStateTransitionException(
                    "Order " + order.getId() + " status: " + order.getStatus() + "(expected: " + expected + ")");
        }
    }

    private PaymentResponse toPaymentResponse(Payment pay, Order ord) {
        return new PaymentResponse(pay.getId(), ord.getId(), pay.getProvider(), pay.getStatus().name(),
                                   pay.getAmount(), pay.getTransactionReference(), ord.getStatus().name(),
                                   pay.getCreatedAt());
    }
}

