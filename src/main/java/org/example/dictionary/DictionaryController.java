package org.example.dictionary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    @Value("${admin.api.key}")
    private String adminApiKey;

    @Value("${dictionary.first.path}")
    private String firstDictionaryPath;

    @Value("${dictionary.second.path}")
    private String secondDictionaryPath;

    @Value("${dictionary.third.path}")
    private String thirdDictionaryPath;

    public DictionaryController(
            @Qualifier("firstDictionaryService") DictionaryService firstDictionaryService,
            @Qualifier("secondDictionaryService") DictionaryService secondDictionaryService,
            @Qualifier("thirdDictionaryService") DictionaryService thirdDictionaryService,
            MessageSource messageSource,
            LocaleResolver localeResolver) {
        this.firstDictionaryService = firstDictionaryService;
        this.secondDictionaryService = secondDictionaryService;
        this.thirdDictionaryService = thirdDictionaryService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    private DictionaryService getDictionaryService(String type) {
        return switch (type.toLowerCase()) {
            case "first" -> firstDictionaryService;
            case "second" -> secondDictionaryService;
            case "third" -> thirdDictionaryService;
            default -> null;
        };
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(@RequestParam String type, @RequestParam String key) {
        logger.info("Searching for key: {} in dictionary type: {}", key, type);

        DictionaryService dictionaryService = getDictionaryService(type);
        if (dictionaryService == null) {
            logger.error("Invalid dictionary type: {}", type);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid dictionary type");
        }

        Optional<String> entry = dictionaryService.searchEntry(key);
        return entry.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found"));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEntry(@RequestParam String type, @RequestParam String key, @RequestParam String value, HttpServletRequest request) {
        DictionaryService dictionaryService = getDictionaryService(type);
        if (dictionaryService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid dictionary type");
        }
        if (!dictionaryService.addEntry(key, value)) {
            Locale locale = localeResolver.resolveLocale(request);
            String message = messageSource.getMessage("error.duplicate_key", null, locale);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
        return ResponseEntity.ok("Added successfully");
    }

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

    @GetMapping("/read")
    public ResponseEntity<List<String>> readPage(@RequestParam String type, @RequestParam int page, @RequestParam int size) {
        DictionaryService dictionaryService = getDictionaryService(type);
        if (dictionaryService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(dictionaryService.readPage(page, size));
    }

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
}