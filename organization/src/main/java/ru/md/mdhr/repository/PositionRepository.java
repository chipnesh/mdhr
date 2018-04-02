package ru.md.mdhr.repository;

import ru.md.mdhr.domain.Position;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Position entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

}
