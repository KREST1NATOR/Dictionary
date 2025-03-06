package org.example.dictionary.repositories;

import org.example.dictionary.entities.DictionaryEntity;
import org.example.dictionary.entities.EntryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<EntryEntity, Long> {
    List<EntryEntity> findByDictionaryAndKeyAndDeletedFalse(DictionaryEntity dictionary, String key);

    Page<EntryEntity> findByKeyAndDeletedFalse(String key, Pageable pageable);

    Page<EntryEntity> findByValueContainingAndDeletedFalse(String value, Pageable pageable);
}

