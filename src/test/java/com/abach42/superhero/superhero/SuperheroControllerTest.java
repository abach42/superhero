package com.abach42.superhero.superhero;

import static com.abach42.superhero.config.api.PathConfig.SUPERHEROES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abach42.superhero.config.validation.OnCreate;
import com.abach42.superhero.testconfiguration.ObjectMapperSerializerHelper;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(SuperheroController.class)
@ContextConfiguration
@WebAppConfiguration
@Import(ObjectMapperSerializerHelper.class)
@WithMockUser
public class SuperheroControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SuperheroService superheroService;

    private SuperheroDto superheroDtoStub;

    @Autowired
    private ObjectMapper superheroObjectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        this.superheroDtoStub = TestStubs.getSuperheroDtoStub();
    }

    @Test
    @DisplayName("GET /superheroes returns first page of superheroes")
    public void testListSuperheroes() throws Exception {
        SuperheroListDto expected = SuperheroListDto.fromPage(
                new PageImpl<>(List.of(superheroDtoStub),
                        PageRequest.ofSize(1), 1L), 1L);

        given(superheroService.retrieveSuperheroList(null)).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(SUPERHEROES)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SuperheroListDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                SuperheroListDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /superheroes/0" + " returns a superhero")
    public void testShowSuperhero() throws Exception {
        SuperheroDto expected = superheroDtoStub;

        given(superheroService.retrieveSuperhero(0L)).willReturn(expected);

        MvcResult mvcResult = mockMvc.perform(
                        get(SUPERHEROES + "/" + 0)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    //notice: `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY` for password is not considered
    // another test does the job.
    @Test
    @DisplayName("POST /superheroes results created")
    public void testCreateSuperhero() throws Exception {
        SuperheroDto input = TestStubs.getSuperheroDtoStubWithPassword();

        given(superheroService.addSuperhero(any(SuperheroDto.class))).willReturn(input);

        MvcResult mvcResult = mockMvc.perform(
                        post(SUPERHEROES)
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(superheroObjectMapper.writeValueAsString(input))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(input);

        String locationHeader = mvcResult.getResponse().getHeader("Location");
        assertThat(locationHeader).isNotNull();
    }

    /*
     * Info: This test expected to fail runs here, because on WebMvcTest/mockMvc-level
     * jakarta validation constraint IS executed,
     * and on usage of validation *groups* it is executed IF annotation is
     * related directly to method parameter, and NOT as usual above method.
     */
    @Test
    @DisplayName("POST /superheroes returns 422 on missing field in payload")
    @Validated(OnCreate.class)
    public void testCreateSuperheroFailsOnMissingField() throws Exception {
        SuperheroDto failedSuperheroDto = TestStubs.getSuperheroDtoStubEmpty();

        mockMvc.perform(post(SUPERHEROES)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .content(superheroObjectMapper.writeValueAsString(failedSuperheroDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Controller action to soft delete superhero result ok")
    public void testSoftDeleteSuperhero() throws Exception {
        given(superheroService.markSuperheroAsDeleted(anyLong())).willReturn(superheroDtoStub);

        MvcResult mvcResult = mockMvc.perform(
                        delete(SUPERHEROES + "/" + 0)
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                                .content(superheroObjectMapper.writeValueAsString(superheroDtoStub))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SuperheroDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                SuperheroDto.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(superheroDtoStub);
    }
}