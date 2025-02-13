package org.example.dictionary;

import org.springframework.stereotype.Service;

@Service
public class SecondDictionary extends AbstractDictionaryService {
    public SecondDictionary() {
        super("second_dict.txt");
    }

    @Override
    protected boolean isValidKey(String key) {
        // Ключ должен состоять ровно из 4 латинских букв
        return key.matches("^\\d{5}$");
    }
}