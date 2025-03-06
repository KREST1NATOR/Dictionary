package org.example.dictionary;

import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public interface DictionaryService {
    Optional<String> searchEntry(String key);
    boolean addEntry(String key, String value);
    boolean deleteEntry(String key);
    List<String> readPage(int page, int size);
    List<String> readPage(int page, int size, String key, String value);
    void exportToXml(OutputStream outputStream);
}

