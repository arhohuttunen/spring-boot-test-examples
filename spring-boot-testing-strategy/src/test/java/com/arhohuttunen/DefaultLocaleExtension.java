package com.arhohuttunen;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class DefaultLocaleExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AfterEachCallback {
    private static final Namespace NAMESPACE = Namespace.create(DefaultLocaleExtension.class);

    private static final String KEY = "DefaultLocale";

    @Override
    public void beforeAll(ExtensionContext context) {
        setDefaultLocale(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        if (annotationPresentOnTestMethod(context)) {
            setDefaultLocale(context);
        }
    }

    private boolean annotationPresentOnTestMethod(ExtensionContext context) {
        return context.getTestMethod()
                .map(testMethod -> AnnotationSupport.isAnnotated(testMethod, DefaultLocale.class))
                .orElse(false);
    }

    private void setDefaultLocale(ExtensionContext context) {
        storeDefaultLocale(context);
        Locale configuredLocale = readLocaleFromAnnotation(context);
        Locale.setDefault(configuredLocale);
        LocaleContextHolder.setDefaultLocale(configuredLocale);
    }

    private void storeDefaultLocale(ExtensionContext context) {
        context.getStore(NAMESPACE).put(KEY, Locale.getDefault());
    }

    private Locale readLocaleFromAnnotation(ExtensionContext context) {
        return AnnotationSupport
                .findAnnotation(context.getElement(), DefaultLocale.class)
                .map(DefaultLocaleExtension::createLocale)
                .orElseThrow(() -> new ExtensionConfigurationException("@DefaultLocale annotation missing"));
    }

    private static Locale createLocale(DefaultLocale annotation) {
        return new Locale(annotation.language(), annotation.country());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (annotationPresentOnTestMethod(context)) {
            resetDefaultLocale(context);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        resetDefaultLocale(context);
    }

    private void resetDefaultLocale(ExtensionContext context) {
        Locale storedLocale = context.getStore(NAMESPACE).get(KEY, Locale.class);
        Locale.setDefault(storedLocale);
        LocaleContextHolder.setDefaultLocale(storedLocale);
    }
}