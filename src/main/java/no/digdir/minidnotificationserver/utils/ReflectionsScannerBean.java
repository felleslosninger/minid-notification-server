package no.digdir.minidnotificationserver.utils;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

@Component
public class ReflectionsScannerBean {
    private Reflections reflections;

    @PostConstruct
    public void init() {
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("no.digdir.minidnotificationserver"))
                .setScanners(new FieldAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage("no.digdir.minidnotificationserver"))
        );
    }

    public Set<Field> getFieldsAnnotatedWith(final Class<? extends Annotation> fieldAnnotation) {
        return reflections.getFieldsAnnotatedWith(fieldAnnotation);
    }

}
