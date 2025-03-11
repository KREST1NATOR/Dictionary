package org.example.dictionary;

import org.example.dictionary.entities.DictionaryEntity;
import org.example.dictionary.entities.EntryEntity;
import org.springframework.data.domain.Page;

import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public interface DictionaryService {
    Optional<String> searchEntry(String key);
    //boolean addEntry(String key, String value);
    boolean addEntry(Long idDictionary, String key, String value);
    boolean deleteEntry(String key);
    List<String> readPage(int page, int size);
    Page<EntryEntity> readPage(int page, int size, Long idDictionary, String key, String value);
    void exportToXml(OutputStream outputStream);
    DictionaryEntity createDictionary(DictionaryEntity dictionary);
    List<DictionaryEntity> getAllDictionaries();
}

