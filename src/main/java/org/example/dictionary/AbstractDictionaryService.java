package org.example.dictionary;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractDictionaryService implements DictionaryService {
    protected final String resourcePath; // Путь к файлу в ресурсах
    //String resourcePath = "first_dict.txt";
    protected final File tempFile; // Временный файл для записи
    protected abstract boolean isValidKey(String key);

    public AbstractDictionaryService(String resourcePath) {
        this.resourcePath = resourcePath;
        this.tempFile = createTempFileFromResource();
    }

    // Копируем ресурс в temp-файл, чтобы с ним можно было работать
    private File createTempFileFromResource() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource file not found: " + resourcePath);
            }
            File tempFile = File.createTempFile("dictionary", ".txt");
            tempFile.deleteOnExit();

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Error copying resource to temp file", e);
        }
    }

    @Override
    public Optional<String> searchEntry(String key) {
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            return reader.lines()
                    .filter(line -> line.startsWith(key + "="))
                    .findFirst()
                    .map(line -> line.split("=", 2)[1]);
        } catch (IOException e) {
            throw new RuntimeException("Error reading dictionary file", e);
        }
    }

    @Override
    public boolean addEntry(String key, String value) {
        if (!isValidKey(key) || searchEntry(key).isPresent()) {
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true))) {
            writer.write(key + "=" + value);
            writer.newLine();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error writing to dictionary file", e);
        }
    }

    @Override
    public boolean deleteEntry(String key) {
        File newTempFile = new File(tempFile.getAbsolutePath() + ".tmp");
        boolean deleted = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(newTempFile))) {
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
            if (!tempFile.delete() || !newTempFile.renameTo(tempFile)) {
                throw new RuntimeException("Could not update dictionary file");
            }
        }
        return deleted;
    }

    @Override
    public List<String> readPage(int page, int size) {
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
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


