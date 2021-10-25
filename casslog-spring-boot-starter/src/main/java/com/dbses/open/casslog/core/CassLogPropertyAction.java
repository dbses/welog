package com.dbses.open.casslog.core;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

/**
 * @author yanglulu
 * @date 2021/10/22
 */
public class CassLogPropertyAction extends Action {

    private static final String SOURCE_ATTRIBUTE = "source";

    private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";
    private static final String VALUE_PREFIX = "${";
    private static final String VALUE_SUFFIX = "}";

    private final Environment environment;

    private Properties properties = new Properties();

    CassLogPropertyAction(Environment environment) {
        this.environment = environment;
        load();
    }

    @Override
    public void begin(InterpretationContext ic, String elementName, Attributes attributes) {
        String name = attributes.getValue(NAME_ATTRIBUTE);
        String source = attributes.getValue(SOURCE_ATTRIBUTE);
        ActionUtil.Scope scope = ActionUtil.stringToScope(attributes.getValue(SCOPE_ATTRIBUTE));
        String defaultValue = attributes.getValue(DEFAULT_VALUE_ATTRIBUTE);
        if (OptionHelper.isEmpty(name) || OptionHelper.isEmpty(source)) {
            addError(
                    "The \"name\" and \"source\" attributes of <springProperty> must be set");
        }

        String value = getValueFromEnvironment(source, defaultValue);

        if (value == null) {
            ActionUtil.setProperty(ic, name, getValueFromProperties(source, defaultValue), scope);
        } else {
            ActionUtil.setProperty(ic, name, value, scope);
        }
    }

    private String getValueFromProperties(String source, String defaultValue) {

        String value = properties.getProperty(source);

        if (value == null) {
            return defaultValue;
        }

        if (value.contains(VALUE_PREFIX) && value.contains(VALUE_SUFFIX)) {
            return resolveValue(value);
        }
        return value;
    }

    private String resolveValue(String value) {

        int indexOfPrefix = value.indexOf(VALUE_PREFIX);
        int indexOfSuffix = value.indexOf(VALUE_SUFFIX);

        // example: "${spring.application.name}"
        String beReplaced = value.substring(indexOfPrefix, indexOfSuffix + 1);
        // example: spring.application.name
        String key = beReplaced.substring(VALUE_PREFIX.length(), beReplaced.length() - 1);
        // example: user-service
        String toReplace = environment.getProperty(key);
        if (toReplace == null) {
            return "logs/app-" + UUID.randomUUID() + ".log";
        }
        // example: logs/user-service.log
        return value.replace(beReplaced, toReplace);
    }

    private String getValueFromEnvironment(String source, String defaultValue) {
        if (this.environment == null) {
            addWarn("No Spring Environment available to resolve " + source);
            return defaultValue;
        }
        String value = this.environment.getProperty(source);
        if (value != null) {
            return value;
        }
        int lastDot = source.lastIndexOf(".");
        if (lastDot > 0) {
            String prefix = source.substring(0, lastDot + 1);
            RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
                    this.environment, prefix);
            return resolver.getProperty(source.substring(lastDot + 1), defaultValue);
        }
        return defaultValue;
    }

    private void load() {

        if (this.environment == null) {
            addWarn("No Spring Environment available to resolve ");
            return;
        }

        String baseUrl = this.environment.getProperty("spring.cloud.config.uri");
        String label = this.environment.getProperty("spring.cloud.config.label");

        Assert.notNull(baseUrl, "Config url is empty, Please config 'spring.cloud.config.uri'.");
        Assert.notNull(label, "Config url is empty, Please config 'spring.cloud.config.label'.");

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        String url = baseUrl + "__common_log_-" + label + ".properties";

        try (InputStream inputStream = new URL(url).openStream()) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new CassLogInitializeException("Load CassLog property error.");
        }
    }

    @Override
    public void end(InterpretationContext ic, String name) {
    }

}

