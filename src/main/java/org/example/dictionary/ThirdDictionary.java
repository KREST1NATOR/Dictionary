package org.example.dictionary;

import org.example.dictionary.entities.DictionaryEntity;
import org.example.dictionary.entities.EntryEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("thirdDictionaryService")
public class ThirdDictionary extends AbstractDictionaryService {
    public ThirdDictionary() {
        super("third_dict.txt");
    }

    @Override
    protected boolean isValidKey(String key) {
        return key.matches("^[a-z#]+$");
    }

    @Override
    public Page<EntryEntity> readPage(int page, int size, Long idDictionary, String key, String value) {
        // Заглушка или делегирование вызова другому сервису
        return readPage(page, size, idDictionary, key, value);
    }

    @Override
    public DictionaryEntity createDictionary(DictionaryEntity dictionary) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public List<DictionaryEntity> getAllDictionaries() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean addEntry(Long idDictionary, String key, String value) {
        throw new UnsupportedOperationException("");
    }
}