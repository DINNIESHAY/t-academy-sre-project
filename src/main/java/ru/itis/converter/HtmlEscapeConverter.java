package ru.itis.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class HtmlEscapeConverter implements Converter<String, String> {
    @Override
    public String convert(String source) {
        return source == null ? null : HtmlUtils.htmlEscape(source.trim());
    }
}
