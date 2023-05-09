package com.example.attestation.controllers;

import com.example.attestation.models.Product;
import com.example.attestation.repositories.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/authentication")
    public String login(){
        return "authentication";
    }



}
