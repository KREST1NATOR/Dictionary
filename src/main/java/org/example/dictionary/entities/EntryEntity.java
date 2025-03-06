package org.example.dictionary.entities;

import jakarta.persistence.*;

@Entity
public class EntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private DictionaryEntity dictionary;

    private String key;

    private String value;

    private boolean deleted;

    // Геттер для value
    public String getValue() {
        return value;
    }

    // Сеттер для value
    public void setValue(String value) {
        this.value = value;
    }

    // Геттер для key
    public String getKey() {
        return key;
    }

    // Сеттер для key
    public void setKey(String key) {
        this.key = key;
    }

    // Геттер и сеттер для deleted
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Геттер и сеттер для dictionary
    public DictionaryEntity getDictionary() {
        return dictionary;
    }

    public void setDictionary(DictionaryEntity dictionary) {
        this.dictionary = dictionary;
    }
}