package com.example.attestation.controllers;

import com.example.attestation.enumm.Status;
import com.example.attestation.models.Cart;
import com.example.attestation.models.Order;
import com.example.attestation.models.Person;
import com.example.attestation.models.Product;
import com.example.attestation.repositories.CartRepository;
import com.example.attestation.repositories.OrderRepository;
import com.example.attestation.repositories.ProductRepository;
import com.example.attestation.security.PersonDetails;
import com.example.attestation.services.PersonService;
import com.example.attestation.services.ProductService;
import com.example.attestation.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {

    private final ProductRepository productRepository;

    private final PersonValidator personValidator;
    private final PersonService personService;

    private final ProductService productService;

    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    public MainController(ProductRepository productRepository, PersonValidator personValidator, PersonService personService, ProductService productService, CartRepository cartRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.personValidator = personValidator;
        this.personService = personService;
        this.productService = productService;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }


    @GetMapping("/person_acc")
    public String index(Model model){
        //Получаем объект аутентификации, с помощью SpringContextHolder обращаемся к контексту и на нем вызываем метод аутентификации. Из сессии текущего пользователя получаем оъект, который был положен в данную сессию после аутентификации пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole();
        if (role.equals("ROLE_ADMIN")){
            return "redirect:/admin";
        }
       model.addAttribute("products", productService.getAllProduct());
       return "/user/index";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person){
        return "registration";
    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()){
            return "registration";
        }
        personService.register(person);
        return "redirect:/person_acc";
    }

    @GetMapping("/person_acc/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductId(id));
        return "/user/infoProduct";
    }

    @PostMapping("/person_acc/product/search")
    public String productSearch(@RequestParam("search") String search, @RequestParam("ot") String ot, @RequestParam("do") String Do, @RequestParam(value = "price", required = false, defaultValue = "") String price, @RequestParam(value = "contract", required = false, defaultValue = "") String contract, Model model){

        model.addAttribute("products", productService.getAllProduct());

        if (!ot.isEmpty() & !Do.isEmpty()){
            if (!price.isEmpty()){
                if (price.equals("sorted_by_ascending_price")){
                    if (!contract.isEmpty()){
                        if (contract.equals("kitchen")){
                            model.addAttribute("search_product",productRepository.findByTitleAAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                        }else if (contract.equals("house")){
                            model.addAttribute("search_product",productRepository.findByTitleAAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        } else if (contract.equals("garden")){
                            model.addAttribute("search_product",productRepository.findByTitleAAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                } else if (price.equals("sorted_by_descending_price")){
                    if (!contract.isEmpty()){
                        if (contract.equals("kitchen")){
                            model.addAttribute("search_product",productRepository.findByTitleAAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                        }else if (contract.equals("house")){
                            model.addAttribute("search_product",productRepository.findByTitleAAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        } else if (contract.equals("garden")){
                            model.addAttribute("search_product",productRepository.findByTitleAAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }
                    }else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                }
            } else {
                model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAAndPriceLessThanEqual(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
            }
        }else {
            model.addAttribute("search_product", productRepository.findByTitleContainingIgnoreCase(search));
        }

        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);
        return "/product/product";
    }

    @GetMapping("/cart/add/{id}")
    public String addProductInCart (@PathVariable("id") int id, Model model){
        //получаем продукт по id
        Product product = productService.getProductId(id);
        //извлекаем объект аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart (Model model){
        //извлекаем объект аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();

        //получаем товары из корзины по id товара
        for (Cart cart : cartList){
            productList.add(productService.getProductId(cart.getProductId()));
        }

        // вычисляем общую стоимость товаров в корзине
        float price =0;
        for (Product product: productList){
            price += product.getPrice();
        }

        model.addAttribute("price", price);
        model.addAttribute("cart_product", productList);
        return "/user/cart";
    }

    @GetMapping("/cart/delete/{id}")
    public String deleteProductFromCart (Model model, @PathVariable("id") int id){
        //извлекаем объект аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();

        //получаем товары из корзины по id товара
        for (Cart cart : cartList){
            productList.add(productService.getProductId(cart.getProductId()));
        }
        cartRepository.deleteCartByProductId(id);
        return "redirect:/cart";
    }

    @GetMapping("/order/create")
    public String order(){
        //извлекаем объект аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //извлекаем id пользователя из объекта
        int id_person = personDetails.getPerson().getId();

        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();

        //получаем товары из корзины по id товара
        for (Cart cart : cartList){
            productList.add(productService.getProductId(cart.getProductId()));
        }

        // вычисляем общую стоимость товаров в корзине
        float price =0;
        for (Product product: productList){
            price += product.getPrice();
        }

        String uuid = UUID.randomUUID().toString();
        for (Product product : productList){
            Order newOrder = new Order(uuid, product, personDetails.getPerson(), 1, product.getPrice(), Status.Получен);
            orderRepository.save(newOrder);
            cartRepository.deleteCartByProductId(product.getId());
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        model.addAttribute("orders", orderList);
        return "/user/orders";
    }

}
