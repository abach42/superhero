package com.abach42.superhero.ai;

import com.abach42.superhero.config.api.PathConfig;
import com.abach42.superhero.login.methodsecurity.IsAdmin;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Superhero API")
@RestController
@RequestMapping(path = PathConfig.SUPERHEROES)
@SecurityRequirement(name = "Bearer Authentication")
@IsAdmin
public class SimilarController {

    private final SimilarService similarService;

    public SimilarController(
            SimilarService similarService) {
        this.similarService = similarService;
    }

    @GetMapping("/search")
    public List<SemanticMatch> searchSimilar(@RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {
        return similarService.searchSimilarHeroes(query, topK);
    }
}
