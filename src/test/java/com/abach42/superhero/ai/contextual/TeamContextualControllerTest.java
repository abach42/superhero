package com.abach42.superhero.ai.contextual;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamContextualControllerTest {

    @Mock
    private TeamContextualService teamContextualService;

    @Test
    @DisplayName("Should delegate contextual team generation to service")
    void shouldDelegateToService() {
        TeamContextualController subject = new TeamContextualController(teamContextualService);
        SuperheroRagTeamDto expected = new SuperheroRagTeamDto("rescue",
                List.of(TestStubs.getSuperheroShortDtoStub()), "ok");
        given(teamContextualService.generateTeamByRag("rescue", 5))
                .willReturn(expected);

        SuperheroRagTeamDto result = subject.generateTeam("rescue", 5);

        assertThat(result).isEqualTo(expected);
    }
}
