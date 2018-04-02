package ru.md.mdhr.web.rest;

import ru.md.mdhr.OrganizationApp;

import ru.md.mdhr.config.SecurityBeanOverrideConfiguration;

import ru.md.mdhr.domain.Position;
import ru.md.mdhr.repository.PositionRepository;
import ru.md.mdhr.service.PositionService;
import ru.md.mdhr.repository.search.PositionSearchRepository;
import ru.md.mdhr.service.dto.PositionDTO;
import ru.md.mdhr.service.mapper.PositionMapper;
import ru.md.mdhr.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static ru.md.mdhr.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PositionResource REST controller.
 *
 * @see PositionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrganizationApp.class, SecurityBeanOverrideConfiguration.class})
public class PositionResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionSearchRepository positionSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPositionMockMvc;

    private Position position;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PositionResource positionResource = new PositionResource(positionService);
        this.restPositionMockMvc = MockMvcBuilders.standaloneSetup(positionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Position createEntity(EntityManager em) {
        Position position = new Position()
            .name(DEFAULT_NAME);
        return position;
    }

    @Before
    public void initTest() {
        positionSearchRepository.deleteAll();
        position = createEntity(em);
    }

    @Test
    @Transactional
    public void createPosition() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().size();

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);
        restPositionMockMvc.perform(post("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isCreated());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate + 1);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Position in Elasticsearch
        Position positionEs = positionSearchRepository.findOne(testPosition.getId());
        assertThat(positionEs).isEqualToIgnoringGivenFields(testPosition);
    }

    @Test
    @Transactional
    public void createPositionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().size();

        // Create the Position with an existing ID
        position.setId(1L);
        PositionDTO positionDTO = positionMapper.toDto(position);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPositionMockMvc.perform(post("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().size();
        // set the field null
        position.setName(null);

        // Create the Position, which fails.
        PositionDTO positionDTO = positionMapper.toDto(position);

        restPositionMockMvc.perform(post("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isBadRequest());

        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPositions() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getPosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get the position
        restPositionMockMvc.perform(get("/api/positions/{id}", position.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(position.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPosition() throws Exception {
        // Get the position
        restPositionMockMvc.perform(get("/api/positions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);
        positionSearchRepository.save(position);
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Update the position
        Position updatedPosition = positionRepository.findOne(position.getId());
        // Disconnect from session so that the updates on updatedPosition are not directly saved in db
        em.detach(updatedPosition);
        updatedPosition
            .name(UPDATED_NAME);
        PositionDTO positionDTO = positionMapper.toDto(updatedPosition);

        restPositionMockMvc.perform(put("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isOk());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Position in Elasticsearch
        Position positionEs = positionSearchRepository.findOne(testPosition.getId());
        assertThat(positionEs).isEqualToIgnoringGivenFields(testPosition);
    }

    @Test
    @Transactional
    public void updateNonExistingPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPositionMockMvc.perform(put("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isCreated());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);
        positionSearchRepository.save(position);
        int databaseSizeBeforeDelete = positionRepository.findAll().size();

        // Get the position
        restPositionMockMvc.perform(delete("/api/positions/{id}", position.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean positionExistsInEs = positionSearchRepository.exists(position.getId());
        assertThat(positionExistsInEs).isFalse();

        // Validate the database is empty
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);
        positionSearchRepository.save(position);

        // Search the position
        restPositionMockMvc.perform(get("/api/_search/positions?query=id:" + position.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Position.class);
        Position position1 = new Position();
        position1.setId(1L);
        Position position2 = new Position();
        position2.setId(position1.getId());
        assertThat(position1).isEqualTo(position2);
        position2.setId(2L);
        assertThat(position1).isNotEqualTo(position2);
        position1.setId(null);
        assertThat(position1).isNotEqualTo(position2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PositionDTO.class);
        PositionDTO positionDTO1 = new PositionDTO();
        positionDTO1.setId(1L);
        PositionDTO positionDTO2 = new PositionDTO();
        assertThat(positionDTO1).isNotEqualTo(positionDTO2);
        positionDTO2.setId(positionDTO1.getId());
        assertThat(positionDTO1).isEqualTo(positionDTO2);
        positionDTO2.setId(2L);
        assertThat(positionDTO1).isNotEqualTo(positionDTO2);
        positionDTO1.setId(null);
        assertThat(positionDTO1).isNotEqualTo(positionDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(positionMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(positionMapper.fromId(null)).isNull();
    }
}
