package org.example.dictionary;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service("thirdDictionaryService")
public class ThirdDictionary extends AbstractDictionaryService {
    public ThirdDictionary() {
        //super(loadDictionaryFromResources("third_dict.txt"));
        super("third_dict.txt");
    }

    @Override
    protected boolean isValidKey(String key) {
        return key.matches("^[a-z#]+$");
    }

    /*private static String loadDictionaryFromResources(String fileName) {
        try (InputStream inputStream = FirstDictionary.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Файл словаря не найден: " + fileName);
            }
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки словаря: " + fileName, e);
        }
    }*/
}