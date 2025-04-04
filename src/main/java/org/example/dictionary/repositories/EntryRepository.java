package org.example.dictionary.repositories;

import org.example.dictionary.entities.DictionaryEntity;
import org.example.dictionary.entities.EntryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository<EntryEntity, Long> {
    Optional<EntryEntity> findByKey(String key);
    boolean existsByKey(String key);
    void deleteByKey(String key);
    Page<EntryEntity> findAll(Pageable pageable);
    Page<EntryEntity> findByKeyContainingAndValueContaining(String key, String value, Pageable pageable);
    boolean existsByKeyAndDictionary(String key, DictionaryEntity dictionary);
    Page<EntryEntity> findByDictionaryIdAndKeyContaining(Long idDictionary, String key, Pageable pageable);
    Page<EntryEntity> findByDictionaryIdAndValueContaining(Long idDictionary, String value, Pageable pageable);
    Page<EntryEntity> findByDictionaryIdAndKeyContainingAndValueContaining(Long idDictionary, String key, String value, Pageable pageable);
    Page<EntryEntity> findByDictionaryId(Long idDictionary, Pageable pageable);
    List<EntryEntity> findAllByDictionaryIdOrderBySearchCountDesc(Long dictionaryId, Pageable pageable);
}
