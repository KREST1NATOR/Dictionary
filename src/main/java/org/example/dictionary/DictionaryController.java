package org.example.dictionary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @Value("${admin.api.key}")
    private String adminApiKey;

    public DictionaryController(DictionaryService dictionaryService, MessageSource messageSource, LocaleResolver localeResolver) {
        this.dictionaryService = dictionaryService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @GetMapping("/search/{key}")
    public ResponseEntity<String> search(@PathVariable String key) {
        Optional<String> entry = dictionaryService.searchEntry(key);
        return entry.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found"));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEntry(@RequestParam String key, @RequestParam String value, HttpServletRequest request) {
        if (!dictionaryService.addEntry(key, value)) {
            Locale locale = localeResolver.resolveLocale(request);
            String message = messageSource.getMessage("error.duplicate_key", null, locale);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
        return ResponseEntity.ok("Added successfully");
    }

    @DeleteMapping("/delete/{key}")
    public ResponseEntity<String> deleteEntry(@PathVariable String key, @RequestHeader("X-API-KEY") String apiKey, HttpServletRequest request) {
        if (!adminApiKey.equals(apiKey)) {
            Locale locale = localeResolver.resolveLocale(request);
            String message = messageSource.getMessage("error.unauthorized", null, locale);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }
        boolean deleted = dictionaryService.deleteEntry(key);
        return deleted ? ResponseEntity.ok("Deleted successfully") : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @GetMapping(value = "/export", produces = "application/xml")
    public ResponseEntity<StreamingResponseBody> exportToXml() {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=dictionary.xml")
                .body(outputStream -> dictionaryService.exportToXml(outputStream));
    }
}
