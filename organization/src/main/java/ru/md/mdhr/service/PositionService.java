package ru.md.mdhr.service;

import ru.md.mdhr.domain.Position;
import ru.md.mdhr.repository.DepartmentRepository;
import ru.md.mdhr.repository.PositionRepository;
import ru.md.mdhr.repository.search.PositionSearchRepository;
import ru.md.mdhr.service.dto.PositionDTO;
import ru.md.mdhr.service.mapper.PositionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Position.
 */
@Service
@Transactional
public class PositionService {

    private final Logger log = LoggerFactory.getLogger(PositionService.class);

    private final PositionRepository positionRepository;

    private final PositionMapper positionMapper;

    private final PositionSearchRepository positionSearchRepository;

    private final DepartmentRepository departmentRepository;

    public PositionService(PositionRepository positionRepository,
                           PositionMapper positionMapper,
                           PositionSearchRepository positionSearchRepository,
                           DepartmentRepository departmentRepository) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
        this.positionSearchRepository = positionSearchRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * Save a position.
     *
     * @param positionDTO the entity to save
     * @return the persisted entity
     */
    public PositionDTO save(PositionDTO positionDTO) {
        log.debug("Request to save Position : {}", positionDTO);
        Position position = positionMapper.toEntity(positionDTO);
        ofNullable(positionDTO.getDepartmentId())
            .map(departmentRepository::findOne)
            .ifPresent(position::setDepartment);
        position = positionRepository.save(position);
        PositionDTO result = positionMapper.toDto(position);
        positionSearchRepository.save(position);
        return result;
    }

    /**
     * Get all the positions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PositionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Positions");
        return positionRepository.findAll(pageable)
            .map(positionMapper::toDto);
    }

    /**
     * Get one position by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public PositionDTO findOne(Long id) {
        log.debug("Request to get Position : {}", id);
        Position position = positionRepository.findOne(id);
        return positionMapper.toDto(position);
    }

    /**
     * Delete the position by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Position : {}", id);
        positionRepository.delete(id);
        positionSearchRepository.delete(id);
    }

    /**
     * Search for the position corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PositionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Positions for query {}", query);
        Page<Position> result = positionSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(positionMapper::toDto);
    }
}
