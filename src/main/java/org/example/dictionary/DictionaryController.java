package org.example.dictionary;

import org.example.dictionary.entities.DictionaryEntity;
import org.example.dictionary.entities.EntryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {

    private final DictionaryService firstDictionaryService;
    private final DictionaryService secondDictionaryService;
    private final DictionaryService thirdDictionaryService;
    private final DatabaseDictionaryService databaseDictionaryService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @Value("${admin.api.key}")
    private String adminApiKey;

    private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    public DictionaryController(
            @Qualifier("firstDictionaryService") DictionaryService firstDictionaryService,
            @Qualifier("secondDictionaryService") DictionaryService secondDictionaryService,
            @Qualifier("thirdDictionaryService") DictionaryService thirdDictionaryService,
            @Qualifier("databaseDictionaryService") DatabaseDictionaryService databaseDictionaryService,
            MessageSource messageSource,
            LocaleResolver localeResolver) {
        this.firstDictionaryService = firstDictionaryService;
        this.secondDictionaryService = secondDictionaryService;
        this.thirdDictionaryService = thirdDictionaryService;
        this.databaseDictionaryService = databaseDictionaryService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    private DictionaryService getDictionaryService(String type) {
        return switch (type.toLowerCase()) {
            case "first" -> firstDictionaryService;
            case "second" -> secondDictionaryService;
            case "third" -> thirdDictionaryService;
            case "db" -> databaseDictionaryService;
            default -> null;
        };
    }

    /**
     * Поиск записи в словаре
     */
    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam String type, @RequestParam String key) {
        DictionaryService dictionaryService = getDictionaryService(type);
        if (dictionaryService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid dictionary type");
        }

        Optional<String> entry = dictionaryService.searchEntry(key);
        return entry.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found"));
    }

    /**
     * Добавление записи в словарь
     */
    @PostMapping("/add")
    public ResponseEntity<String> addEntry(
            @RequestParam String type,
            @RequestParam Long idDictionary,
            @RequestParam String key,
            @RequestParam String value,
            HttpServletRequest request) {

        DictionaryService dictionaryService = getDictionaryService(type);
        if (dictionaryService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid dictionary type");
        }

        if (!dictionaryService.addEntry(idDictionary, key, value)) {
            Locale locale = localeResolver.resolveLocale(request);
            String message = messageSource.getMessage("error.duplicate_key", null, locale);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        return ResponseEntity.ok("Added successfully");
    }

    /**
     * Удаление записи из словаря
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteEntry(@RequestParam String type, @RequestParam String key, @RequestHeader("X-API-KEY") String apiKey, HttpServletRequest request) {
        if (!adminApiKey.equals(apiKey)) {
            Locale locale = localeResolver.resolveLocale(request);
            String message = messageSource.getMessage("error.unauthorized", null, locale);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }
        DictionaryService dictionaryService = getDictionaryService(type);
        if (dictionaryService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid dictionary type");
        }
        boolean deleted = dictionaryService.deleteEntry(key);
        return deleted ? ResponseEntity.ok("Deleted successfully") : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    /**
     * Чтение страницы словаря
     */
    @GetMapping("/read")
    public ResponseEntity<List<String>> readPage(@RequestParam String type, @RequestParam int page, @RequestParam int size) {
        DictionaryService dictionaryService = getDictionaryService(type);
        if (dictionaryService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(dictionaryService.readPage(page, size));
    }

    /**
     * Экспорт словаря в XML
     */
    @GetMapping(value = "/export", produces = "application/xml")
    public ResponseEntity<StreamingResponseBody> exportToXml(@RequestParam String type) {
        DictionaryService dictionaryService = getDictionaryService(type);
        if (dictionaryService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=dictionary_" + type + ".xml")
                .body(outputStream -> dictionaryService.exportToXml(outputStream));
    }

    // ========= Методы работы с базой данных ===========

    /**
     * Создание нового словаря в базе данных
     */
    @PostMapping("/create")
    public ResponseEntity<DictionaryEntity> createDictionary(@RequestBody DictionaryEntity dictionary) {
        DictionaryEntity savedDictionary = databaseDictionaryService.createDictionary(dictionary);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDictionary);
    }

    /**
     * Получение списка всех словарей из БД
     */
    @GetMapping("/list")
    public ResponseEntity<List<DictionaryEntity>> getDictionaries() {
        List<DictionaryEntity> dictionaries = databaseDictionaryService.getAllDictionaries();
        return ResponseEntity.ok(dictionaries);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DictionaryEntity>> getAllDictionaries() {
        return ResponseEntity.ok(databaseDictionaryService.getAllDictionaries());
    }

    /**
     * Получение записей словаря с фильтрацией и пагинацией
     */
    /*@GetMapping("/entries")
    public ResponseEntity<List<EntryEntity>> readPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "") String key,
            @RequestParam(required = false, defaultValue = "") String value) {

        List<EntryEntity> entries = databaseDictionaryService.readPage(page, size, key, value);
        return ResponseEntity.ok(entries);
    }*/

    @GetMapping("/entries")
    public ResponseEntity<List<EntryEntity>> readPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam Long idDictionary,  // Добавить idDictionary как параметр
            @RequestParam(required = false, defaultValue = "") String key,
            @RequestParam(required = false, defaultValue = "") String value) {

        // Получаем записи с фильтрацией
        Page<EntryEntity> entries = databaseDictionaryService.readPage(page, size, idDictionary, key, value);

        // Возвращаем результат
        return ResponseEntity.ok(entries.getContent());
    }
}