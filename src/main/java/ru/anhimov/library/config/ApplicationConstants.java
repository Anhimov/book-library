package ru.anhimov.library.config;

public final class ApplicationConstants {
    public static final String BASE_PACKAGE = "ru.anhimov.library";
    public static final String MODELS_PACKAGE = BASE_PACKAGE + ".models";
    public static final String REPOSITORIES_PACKAGE = BASE_PACKAGE + ".repositories";
    public static final String PROD_HIBERNATE_PROPERTIES = "classpath:hibernate.properties";
    public static final String TEST_HIBERNATE_PROPERTIES = "classpath:hibernate-test.properties";

    public static final String VIEWS_PREFIX = "/WEB-INF/views/";
    public static final String VIEWS_SUFFIX = ".html";

    public static final String CHARACTER_ENCODING = "UTF-8";

    public static final String HIDDEN_HTTP_METHOD_FILTER_NAME = "hiddenHttpMethodFilter";
    public static final String CHARACTER_ENCODING_FILTER_NAME = "characterEncoding";

    public static final String ROOT_MAPPING = "/";
    public static final String FILTER_URL_PATTERN = "/*";

    public static final String PROD_PROFILE = "prod";
    public static final String TEST_PROFILE = "test";

    private ApplicationConstants() {
    }
}

