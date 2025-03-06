package org.example.dictionary.repositories;

import org.example.dictionary.entities.DictionaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<DictionaryEntity, Long> {
    Optional<DictionaryEntity> findByNameAndDeletedFalse(String name);
}