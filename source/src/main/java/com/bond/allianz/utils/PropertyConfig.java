package com.bond.allianz.utils;

import org.springframework.context.EnvironmentAware;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class PropertyConfig implements EnvironmentAware {

    private static Environment env;

    /**
     * 获取配置属性
     * @param name
     * @return
     */
    public static String getProperty(String name) {
        return env.getProperty(name);
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
