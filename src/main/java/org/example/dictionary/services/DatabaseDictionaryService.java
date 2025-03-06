package org.example.dictionary.services;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.example.dictionary.DictionaryService;
import org.example.dictionary.entities.DictionaryEntity;
import org.example.dictionary.entities.EntryEntity;
import org.example.dictionary.repositories.DictionaryRepository;
import org.example.dictionary.repositories.EntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatabaseDictionaryService implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final EntryRepository entryRepository;

    public DatabaseDictionaryService(DictionaryRepository dictionaryRepository, EntryRepository entryRepository) {
        this.dictionaryRepository = dictionaryRepository;
        this.entryRepository = entryRepository;
    }

    @Override
    public Optional<String> searchEntry(String key) {
        List<EntryEntity> entries = entryRepository.findByDictionaryAndKeyAndDeletedFalse(getDefaultDictionary(), key);
        return entries.isEmpty() ? Optional.empty()
                : Optional.of(entries.stream().map(EntryEntity::getValue).collect(Collectors.joining(", ")));
    }

    @Override
    public boolean addEntry(String key, String value) {
        DictionaryEntity dictionary = getDefaultDictionary();
        if (!key.matches(dictionary.getValidationRule())) {
            throw new IllegalArgumentException("Invalid key format");
        }

        EntryEntity entry = new EntryEntity();
        entry.setDictionary(dictionary);
        entry.setKey(key);
        entry.setValue(value);
        entryRepository.save(entry);
        return true;
    }

    @Override
    public boolean deleteEntry(String key) {
        List<EntryEntity> entries = entryRepository.findByDictionaryAndKeyAndDeletedFalse(getDefaultDictionary(), key);
        if (entries.isEmpty()) {
            return false;
        }
        entries.forEach(entry -> entry.setDeleted(true));
        entryRepository.saveAll(entries);
        return true;
    }

    @Override
    public List<String> readPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntryEntity> entries = entryRepository.findAll(pageable);
        return entries.getContent().stream()
                .map(e -> e.getKey() + " -> " + e.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> readPage(int page, int size, String key, String value) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EntryEntity> entries;

        if (key != null && !key.isEmpty()) {
            entries = entryRepository.findByKeyAndDeletedFalse(key, pageable);
        } else if (value != null && !value.isEmpty()) {
            entries = entryRepository.findByValueContainingAndDeletedFalse(value, pageable);
        } else {
            entries = entryRepository.findAll(pageable);
        }

        return entries.getContent().stream()
                .map(e -> e.getKey() + " -> " + e.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public void exportToXml(OutputStream outputStream) {
        try {
            // Получаем все записи из базы данных (например, все словарные записи)
            List<DictionaryEntity> entries = dictionaryRepository.findAll();

            // Создаем объект XmlMapper для конвертации в XML
            XmlMapper xmlMapper = new XmlMapper();

            // Конвертируем список объектов в XML и записываем в OutputStream
            xmlMapper.writeValue(outputStream, entries);
        } catch (IOException e) {
            // Обработка ошибки записи в OutputStream
            e.printStackTrace();
            throw new RuntimeException("Failed to export data to XML", e);
        }
    }

    private DictionaryEntity getDefaultDictionary() {
        return dictionaryRepository.findByNameAndDeletedFalse("default")
                .orElseThrow(() -> new RuntimeException("Default dictionary not found"));
    }
}