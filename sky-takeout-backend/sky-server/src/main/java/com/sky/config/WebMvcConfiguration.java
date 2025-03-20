package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 配置类，注册 web 层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/login")
                .excludePathPatterns("/user/shop/status"); // 用户可以在不登陆的状态下查看店铺营业状态
    }

    /**
     * 通过 knife4j 生成接口文档
     *
     * @return
     */
    /*@Bean
    public Docket docket() {
        log.info("开始创建接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller")) // 指定生成接口需要扫描的包
                .paths(PathSelectors.any())
                .build();
        return docket;
    }*/
    // Spring Boot3 配置方式
    @Bean
    public OpenAPI publicApi() {
        log.info("开始创建接口文档...");
        return new OpenAPI()
                .info(new Info()
                        .title("苍穹外卖项目接口文档")
                        .version("1.0")
                        .description("苍穹外卖项目接口文档"));
    }

    /**
     * 设置静态资源映射；WebMvcConfigurer 接口中有默认映射逻辑
     *
     * @param registry
     */
    /*public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }*/

    /**
     * 此方法用于：扩展 Spring MVC 框架的消息转换器，对后端返回的数据，进行统一处理
     *
     * @param converters 消息转换器集合
     */
    @Override
    public void extendMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器：{}", converters);

        // 创建一个消息转换器对象
        MappingJackson2HttpMessageConverter covert = new MappingJackson2HttpMessageConverter();
        // 为消息转换器对象，设置对象转换器，底层使用 Jackson 将 Java 对象转为 json
        covert.setObjectMapper(new JacksonObjectMapper());
        // 将自己的消息转化器加入容器中
        //converters.add(0, covert);
    }
}
