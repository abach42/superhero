package com.abach42.superhero.ai;

import com.abach42.superhero.shared.validation.OnCreate;
import com.abach42.superhero.shared.validation.OnUpdate;
import com.abach42.superhero.superhero.Gender;
import com.abach42.superhero.superhero.Superhero;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record SuperheroSkillDto(
        @Schema(
                title = "superhero id",
                example = "1",
                description = "Technical id of database record",
                format = "integer",
                accessMode = AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                title = "alias name",
                example = "Mr. Incredible",
                description = "Public alias name of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(groups = OnCreate.class)
        @Size(max = 20, groups = {OnCreate.class, OnUpdate.class})
        String alias,

        @Schema(
                title = "real name",
                example = "Bob Parr",
                description = "Secret real name of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(groups = OnCreate.class)
        String realName,

        @Schema(
                title = "birth date",
                example = "1970-01-01",
                description = "Birth date of superhero",
                format = "date",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(groups = OnCreate.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,

        @Schema(
                title = "gender tag",
                example = "FEMALE",
                description = "Gender of superhero",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(groups = OnCreate.class)
        Gender gender,

        @Schema(
                title = "occupation",
                example = "Insurance employee",
                description = "Current apparent profession that the public is supposed to believe is being practiced."
        )
        String occupation,
        @Schema(
                title = "origin story",
                example = "Story of superhero."
        )
        String originStory,

        List<SkillShortDto> skill
) {
    public static SuperheroSkillDto fromDomain(Superhero domain) {
        return new SuperheroSkillDto(
                domain.getId(), domain.getAlias(), domain.getRealName(), domain.getDateOfBirth(),
                domain.getGender(), domain.getOccupation(), domain.getOriginStory(),
                domain.getSkillProfiles().stream().map(SkillShortDto::fromDomain)
                        .collect(Collectors.toList())
        );
    }
}
