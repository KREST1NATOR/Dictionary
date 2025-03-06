package org.example.dictionary;

import org.springframework.stereotype.Service;
import java.util.List;

@Service("firstDictionaryService")
public class FirstDictionary extends AbstractDictionaryService {
    public FirstDictionary() {
        super("first_dict.txt");
    }

    @Override
    protected boolean isValidKey(String key) {
        return key.matches("^[A-Za-z]{4}$");
    }

    @Override
    public List<String> readPage(int page, int size, String key, String value) {
        // Заглушка или делегирование вызова другому сервису
        return readPage(page, size);
    }
}
