package band.gosrock.api.example.controller;


import band.gosrock.api.example.dto.ExampleResponse;
import band.gosrock.api.example.service.ExampleApiService;
import band.gosrock.common.annotation.DisableSwaggerSecurity;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "access-token")
public class ExampleController {

    private final ExampleApiService exampleApiService;

    @GetMapping
    @DisableSwaggerSecurity
    public ExampleResponse get() {
        return exampleApiService.getExample();
    }

    @PostMapping
    public ExampleResponse create() {
        return exampleApiService.createExample();
    }
}
