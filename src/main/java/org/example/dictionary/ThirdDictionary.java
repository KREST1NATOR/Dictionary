package org.example.dictionary;

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
    public List<String> readPage(int page, int size, String key, String value) {
        // Заглушка или делегирование вызова другому сервису
        return readPage(page, size);
    }
}