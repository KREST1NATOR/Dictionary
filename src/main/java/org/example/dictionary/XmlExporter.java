package org.example.dictionary;

import java.io.*;
import java.util.stream.Collectors;

public class XmlExporter {
    public static String exportToXml(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder xml = new StringBuilder("<dictionary>\n");
            reader.lines()
                    .map(line -> {
                        String[] parts = line.split("=", 2);
                        return parts.length == 2
                                ? "  <entry><key>" + parts[0] + "</key><value>" + parts[1] + "</value></entry>\n"
                                : "";
                    })
                    .forEach(xml::append);
            xml.append("</dictionary>");
            return xml.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error exporting to XML", e);
        }
    }
}
