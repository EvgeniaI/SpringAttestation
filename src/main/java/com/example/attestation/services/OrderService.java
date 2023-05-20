package com.example.attestation.services;

import com.example.attestation.enumm.Status;
import com.example.attestation.models.Order;
import com.example.attestation.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //возвращаем лист всех заказов
    public List<Order> getAllOrder(){
        return orderRepository.findAll();
    }

    public Order getOrderId(int id){
        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder.orElse(null);
    }

    @Transactional
    //обновляем заказ по id
    public void updateOrder(int id, Order order){
        order.setId(id);
        orderRepository.save(order);
    }


}
