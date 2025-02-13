package org.example.dictionary;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.stream.*;

public abstract class AbstractDictionaryService implements DictionaryService {
    protected final File file;

    public AbstractDictionaryService(String filePath) {
        this.file = new File(filePath);
    }

    // Метод валидации ключа для конкретного словаря
    protected abstract boolean isValidKey(String key);

    @Override
    public Optional<String> searchEntry(String key) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines()
                    .filter(line -> line.startsWith(key + "="))
                    .findFirst()
                    .map(line -> {
                        String[] parts = line.split("=", 2);
                        return parts.length == 2 ? parts[1] : null;
                    });
        } catch (IOException e) {
            throw new RuntimeException("Error reading dictionary file", e);
        }
    }

    @Override
    public boolean addEntry(String key, String value) {
        if (!isValidKey(key) || searchEntry(key).isPresent()) {
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(key + "=" + value);
            writer.newLine();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error writing to dictionary file", e);
        }
    }

    @Override
    public boolean deleteEntry(String key) {
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        boolean deleted = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(key + "=")) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    deleted = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error modifying dictionary file", e);
        }
        if (deleted) {
            if (!file.delete()) {
                throw new RuntimeException("Could not delete original file");
            }
            if (!tempFile.renameTo(file)) {
                throw new RuntimeException("Could not rename temp file");
            }
        }
        return deleted;
    }

    @Override
    public List<String> readPage(int page, int size) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines()
                    .skip((long) (page - 1) * size)
                    .limit(size)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading dictionary file", e);
        }
    }

    @Override
    public void exportToXml(OutputStream outputStream) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
            writer.writeStartDocument("1.0");
            writer.writeStartElement("dictionary");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    writer.writeStartElement("entry");
                    writer.writeStartElement("key");
                    writer.writeCharacters(parts[0]);
                    writer.writeEndElement();
                    writer.writeStartElement("value");
                    writer.writeCharacters(parts[1]);
                    writer.writeEndElement();
                    writer.writeEndElement();
                }
            }

            writer.writeEndElement(); // </dictionary>
            writer.writeEndDocument();
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error exporting to XML", e);
        }
    }
}
