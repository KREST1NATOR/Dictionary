package org.example.dictionary;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.persistence.EntityNotFoundException;
import org.example.dictionary.entities.DictionaryEntity;
import org.example.dictionary.entities.EntryEntity;
import org.example.dictionary.DictionaryEvent;
import org.example.dictionary.repositories.DictionaryRepository;
import org.example.dictionary.repositories.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseDictionaryService implements DictionaryService {
    private final DictionaryRepository dictionaryRepository;
    private final EntryRepository entryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public DatabaseDictionaryService(DictionaryRepository dictionaryRepository,
                                     EntryRepository entryRepository,
                                     ApplicationEventPublisher eventPublisher) {
        this.dictionaryRepository = dictionaryRepository;
        this.entryRepository = entryRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<DictionaryEntity> getDictionariesSortedByPopularity() {
        return dictionaryRepository.findAllSortedByPopularity();
    }

    @Override
    public Optional<String> searchEntry(String key) {
        Optional<EntryEntity> entry = entryRepository.findByKey(key);
        entry.ifPresent(e -> {
            e.setSearchCount(e.getSearchCount() + 1); // Увеличиваем счетчик
            entryRepository.save(e); // Сохраняем обновленное значение
        });
        return entry.map(EntryEntity::getValue);
    }

    @Override
    public boolean addEntry(Long idDictionary, String key, String value) {
        DictionaryEntity dictionary = dictionaryRepository.findById(idDictionary)
                .orElseThrow(() -> new EntityNotFoundException("Dictionary not found"));

        if (entryRepository.existsByKeyAndDictionary(key, dictionary)) {
            return false;
        }

        EntryEntity entry = new EntryEntity(dictionary, key, value);
        entryRepository.save(entry);

        // 🔥 Публикуем событие о добавлении записи
        eventPublisher.publishEvent(new DictionaryEvent(this, "ADD", key, dictionary.getName()));

        return true;
    }

    @Override
    @Transactional
    public boolean deleteEntry(String key) {
        Optional<EntryEntity> entryOptional = entryRepository.findByKey(key);
        if (entryOptional.isEmpty()) {
            return false;
        }

        EntryEntity entry = entryOptional.get();
        String dictionaryName = entry.getDictionary().getName();

        entryRepository.delete(entry);

        // 🔥 Публикуем событие об удалении записи
        eventPublisher.publishEvent(new DictionaryEvent(this, "DELETE", key, dictionaryName));

        return true;
    }

    @Override
    public List<String> readPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return entryRepository.findAll(pageable)
                .map(EntryEntity::getValue)
                .toList();
    }

    @Override
    public Page<EntryEntity> readPage(int page, int size, Long idDictionary, String key, String value) {
        Pageable pageable = PageRequest.of(page, size);

        if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
            return entryRepository.findByDictionaryIdAndKeyContainingAndValueContaining(idDictionary, key, value, pageable);
        }
        if (key != null && !key.isEmpty()) {
            return entryRepository.findByDictionaryIdAndKeyContaining(idDictionary, key, pageable);
        }
        if (value != null && !value.isEmpty()) {
            return entryRepository.findByDictionaryIdAndValueContaining(idDictionary, value, pageable);
        }

        return entryRepository.findByDictionaryId(idDictionary, pageable);
    }

    @Override
    public void exportToXml(OutputStream outputStream) {
        try {
            List<EntryEntity> entries = entryRepository.findAll();
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.writeValue(outputStream, entries);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to XML", e);
        }
    }

    @Override
    public DictionaryEntity createDictionary(DictionaryEntity dictionary) {
        return dictionaryRepository.save(dictionary);
    }

    @Override
    public List<DictionaryEntity> getAllDictionaries() {
        return dictionaryRepository.findAll();
    }
}