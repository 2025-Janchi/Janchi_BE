package com.springboot.janchi.servey.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeminiResDto {
    private List<Candidate> candidates;

    @Getter @Setter
    public static class Candidate {
        private Content content;
    }

    @Getter @Setter
    public static class Content {
        private List<Part> parts;
    }

    @Getter @Setter
    public static class Part {
        private String text;
    }

    public String getFirstText() {
        if (candidates == null || candidates.isEmpty()) return null;
        Candidate c = candidates.get(0);
        if (c.getContent() == null || c.getContent().getParts() == null || c.getContent().getParts().isEmpty())
            return null;
        return c.getContent().getParts().get(0).getText();
    }
}