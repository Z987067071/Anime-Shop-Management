package com.anime.shop.config;

import com.anime.shop.util.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射配置：让前端能访问本地avatar文件夹
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //头像
    @Value("${avatar.local.upload-path}")
    private String avatarUploadPath;

    @Value("${avatar.local.access-path}")
    private String avatarAccessPath;

    //商品图片
    @Value("${product.local.upload-path}")
    private String productUploadPath;

    @Value("${product.local.access-path}")
    private String productAccessPath;

    @Autowired
    private JwtInterceptor jwtInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有/api请求，排除登录/注册接口（无需Token）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")          // 拦截所有/api开头的接口
                .excludePathPatterns(                // 排除无需校验的接口
                        "/api/login",                // 登录
                        "/api/register",             // 注册
                        "/api/captcha/**",
                        "/feedback/**",
                        "/api/admin/users/avatar/upload",
                        "/api/admin/order/updateStatus"

                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 头像映射（适配你的原有配置）
        // 注意：你的avatar.upload-path是 resources目录，需拼接/avatar/
        String avatarResourcePath = "file:" + avatarUploadPath + "/avatar/";
        registry.addResourceHandler(avatarAccessPath + "**")
                .addResourceLocations(avatarResourcePath)
                .setCachePeriod(0);

        // 2. 商品图片映射（核心修复）
        String productResourcePath = "file:" + productUploadPath;
        registry.addResourceHandler(productAccessPath + "**") // 访问路径：/upload/**
                .addResourceLocations(productResourcePath) // 本地路径：D:/cxdownload/bishe/anime-shop/upload/
                .setCachePeriod(3600);

        // 3. 工单图片映射（classpath 路径，从 resources/feedback 目录加载）
        registry.addResourceHandler("/feedback/**")
                .addResourceLocations("file:D:/cxdownload/bishe/anime-shop/src/main/resources/feedback/")
                .setCachePeriod(3600);

        // 3. 保留Spring Boot默认静态资源
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");

        registry.addResourceHandler("/comment/**")
                .addResourceLocations("file:D:/cxdownload/bishe/anime-shop/src/main/resources/comment/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/communityPost/**")
                .addResourceLocations("file:D:/cxdownload/bishe/anime-shop/src/main/resources/communityPost/")
                .setCachePeriod(3600);
        registry.addResourceHandler("/communityPostComment/**")
                .addResourceLocations("file:D:/cxdownload/bishe/anime-shop/src/main/resources/communityPostComment/")
                .setCachePeriod(3600);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 开发环境允许所有域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600); // 预检请求缓存1小时
    }
}