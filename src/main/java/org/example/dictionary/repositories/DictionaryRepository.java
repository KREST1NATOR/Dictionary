package org.example.dictionary.repositories;

import org.example.dictionary.entities.DictionaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<DictionaryEntity, Long> {
    Optional<DictionaryEntity> findByNameAndDeletedFalse(String name);
    @Query("SELECT d FROM DictionaryEntity d LEFT JOIN d.entries e GROUP BY d ORDER BY SUM(e.searchCount) DESC")
    List<DictionaryEntity> findAllSortedByPopularity();
}