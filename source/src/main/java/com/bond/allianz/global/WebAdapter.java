package com.bond.allianz.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 网站适配器 注册拦截器
 */
@Configuration
public class WebAdapter implements WebMvcConfigurer {
    @Value("${domain.enter}")
    public String domainenter;
    @Value("${upload.wximage}")
    public String uploadwximage;

    /**
     * interceptor默认是不被spring context掌管的。所以还添加@bean ，加入的spring 容器下，才可以在interceptor读取的spring容器内的变量值
     * @return
     */
    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        LoginInterceptor loginInterceptor = loginInterceptor();
        InterceptorRegistration loginRegistry = registry.addInterceptor(loginInterceptor);
        // 拦截路径
        loginRegistry.addPathPatterns("/**");
        // 排除路径
        loginRegistry.excludePathPatterns("/");
        loginRegistry.excludePathPatterns("/weixin");
        loginRegistry.excludePathPatterns("/pay/**");
        loginRegistry.excludePathPatterns("/download/**");
        loginRegistry.excludePathPatterns("/wxweb/**");
        loginRegistry.excludePathPatterns("/login/**");
        loginRegistry.excludePathPatterns("/ibsapi/**");
        loginRegistry.excludePathPatterns("/api/**");
        loginRegistry.excludePathPatterns("/redis/**");
        loginRegistry.excludePathPatterns("/wxcheck/**");
        loginRegistry.excludePathPatterns("/dms/**");
        loginRegistry.excludePathPatterns("/notice/**");
        loginRegistry.excludePathPatterns("/card/**");
        loginRegistry.excludePathPatterns("/import/**");
        loginRegistry.excludePathPatterns("/cop/**");
        loginRegistry.excludePathPatterns("/App/**");
        loginRegistry.excludePathPatterns("/error/**");
        //loginRegistry.excludePathPatterns("/admin/**");
        loginRegistry.excludePathPatterns("/mtadmin/**");
        loginRegistry.excludePathPatterns("/mtcar/**");
        loginRegistry.excludePathPatterns("/active/**");

        //loginRegistry.excludePathPatterns("/loginout");
        // 排除资源请求
        loginRegistry.excludePathPatterns("/*.txt");
        loginRegistry.excludePathPatterns("/css/**");
        loginRegistry.excludePathPatterns("/js/**");
        loginRegistry.excludePathPatterns("/images/**");
        loginRegistry.excludePathPatterns("/image/**");
        loginRegistry.excludePathPatterns("/img/**");
        loginRegistry.excludePathPatterns("/background/**");
        loginRegistry.excludePathPatterns("/wximg/**");
        loginRegistry.excludePathPatterns("/wximage/**");
        loginRegistry.excludePathPatterns("/media/**");
        loginRegistry.excludePathPatterns("/manage/agencyuserlistp2");
        loginRegistry.excludePathPatterns("/manage/useredit2");
        loginRegistry.excludePathPatterns("/manage/usereditread");

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName(("0".equals(domainenter)?"login/login":("1".equals(domainenter)?"login/index":("2".equals(domainenter)?"login/index3":"login/index4"))));
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/wximage/**").addResourceLocations("file:" + uploadwximage);
    }
}
