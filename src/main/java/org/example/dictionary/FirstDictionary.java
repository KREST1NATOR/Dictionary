package org.example.dictionary;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class FirstDictionary extends AbstractDictionaryService {
    public FirstDictionary() {
        super("first_dict.txt");
    }

    @Override
    protected boolean isValidKey(String key) {
        // Ключ должен состоять ровно из 4 латинских букв
        return key.matches("^[A-Za-z]{4}$");
    }
}