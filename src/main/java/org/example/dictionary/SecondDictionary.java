package org.example.dictionary;

import org.springframework.stereotype.Service;
import java.util.List;

@Service("secondDictionaryService")
public class SecondDictionary extends AbstractDictionaryService {
    public SecondDictionary() {
        super("second_dict.txt");
    }

    @Override
    protected boolean isValidKey(String key) {
        return key.matches("^\\d{5}$");
    }

    @Override
    public List<String> readPage(int page, int size, String key, String value) {
        // Заглушка или делегирование вызова другому сервису
        return readPage(page, size);
    }
}