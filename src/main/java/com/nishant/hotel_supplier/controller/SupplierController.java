package com.nishant.hotel_supplier.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/supplier")
public class SupplierController {

    @GetMapping(path = "/")
    public Mono<String> test(){
        return Mono.just("Supplier Test Endpoint !");
    }
}
