package com.apirestspringboot.apirest.product;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
	
    private ProductService productService;
	
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @GetMapping
    public List<Product> listAll(){
        return productService.listAll();
    }

    @PreAuthorize("hasAuthority('SCOPE_MODERATOR')")
    @PostMapping
    public Product create(@RequestBody Product product){
        return productService.create(product);
    }

    @PreAuthorize("hasAuthority('SCOPE_MODERATOR')")
    @PutMapping
    public Product update(@RequestBody Product product){
        return productService.update(product);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping
    public void delete(@RequestParam("id") Long id){
        productService.delete(id);
    }
}
