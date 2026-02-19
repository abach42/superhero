package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    private final Map<String, ContentStrategy> strategies;

    public ContentService(Map<String, ContentStrategy> strategies) {
        this.strategies = strategies;
    }

    public String getContent(String type, Superhero superhero) {
        ContentStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown strategy: " + type);
        }
        return strategy.generate(superhero);
    }
}