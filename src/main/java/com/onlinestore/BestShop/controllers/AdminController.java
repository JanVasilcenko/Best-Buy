package com.onlinestore.BestShop.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin")
@RestController()
@RequestMapping("api/v1/admin")
public class AdminController {

    @GetMapping("/metrics")
    public String getGreet(){
        return "Metrics just for admin";
    }
}
