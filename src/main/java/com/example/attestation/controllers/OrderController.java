package com.example.attestation.controllers;

import com.example.attestation.enumm.Status;
import com.example.attestation.models.Order;
import com.example.attestation.repositories.OrderRepository;
import com.example.attestation.services.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class OrderController {
    private final OrderRepository orderRepository;

    private final OrderService orderService;

    public OrderController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping("/infoOrders")
    public String getAllOrder (Model model){
        model.addAttribute("orders", orderService.getAllOrder());
        return "/infoOrders";
    }

    @PostMapping("/infoOrders/search")
    public String orderSearch(@RequestParam("search") String search, Model model){
        model.addAttribute("orders", orderService.getAllOrder());
        if (!search.isEmpty()){
            model.addAttribute("search_order",orderRepository.findByNumberEndingWith(search));
        }
        model.addAttribute("value_search", search);
        return "/infoOrders";
    }

    @GetMapping("/editOrder/{id}")
    public String editOrder(@PathVariable("id") int id, Model model){
        model.addAttribute("orders", orderService.getOrderId(id));
        return "/editOrder";
    }

    @PostMapping("/editOrder/{id}")
    public String updateOrder(@ModelAttribute("orders") Order order, BindingResult bindingResult, @PathVariable("id") int id, Model model, @RequestParam("status") String status){
        if (bindingResult.hasErrors()){
            model.addAttribute("orders", orderService.getOrderId(id));
            return "/editOrder";
        }
        model.addAttribute("orders", orderService.getOrderId(id));
        Order orderEdit = orderRepository.findById(id).orElseThrow();
        orderEdit.setStatus(Status.valueOf(status));
        orderService.updateOrder(id, orderEdit);
        return "redirect:/admin";
    }

    @GetMapping("/editOrder/delete/{id}")
    public String deleteOrder(@PathVariable("id") int id){
        orderService.deleteOrder(id);
        return "redirect:/infoOrders";
    }

}
