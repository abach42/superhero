package com.abach42.superhero.ai;

import com.abach42.superhero.superhero.Superhero;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component(AllContentStrategy.ALL_CONTENT)
public class AllContentStrategy extends ContentStrategy {

    public static final String ALL_CONTENT = "allContent";

    @Override
    public String generate(Superhero superhero) {
        StringBuilder sb = new StringBuilder();

        sb.append("Real Name: ").append(superhero.getRealName()).append(", \n\n");

        sb.append("Skills: \n");
        sb.append(generateSkills(superhero));
        sb.append("\n");

        sb.append("Alias: ").append(superhero.getAlias()).append(", \n");

        if (superhero.getDateOfBirth() != null) {
            sb.append("Born: ")
                    .append(superhero.getDateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .append(", \n");
        }

        sb.append("Occupation: ").append(superhero.getOccupation()).append(", \n");

        sb.append("Origin Story: ").append(superhero.getOriginStory()).append("\n\n");

        return sb.toString();
    }
}
