package com.anime.shop.config;

import com.anime.shop.util.JwtInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${avatar.local.upload-path}")
    private String avatarUploadPath;

    @Value("${avatar.local.access-path}")
    private String avatarAccessPath;

    @Value("${product.local.upload-path}")
    private String productUploadPath;

    @Value("${product.local.access-path}")
    private String productAccessPath;

    /** 静态资源根目录（feedback/comment/communityPost 等子目录均在此下） */
    @Value("${static.resources.base-path}")
    private String staticBasePath;

    private final JwtInterceptor jwtInterceptor;

    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/login",
                        "/api/register",
                        "/api/captcha/**",
                        "/feedback/**",
                        "/api/admin/users/avatar/upload",
                        "/api/admin/order/updateStatus"
                        // 可选认证路径已移至 JwtInterceptor 内部处理，此处不再 exclude
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(avatarAccessPath + "**")
                .addResourceLocations("file:" + avatarUploadPath + "/avatar/")
                .setCachePeriod(0);

        registry.addResourceHandler(productAccessPath + "**")
                .addResourceLocations("file:" + productUploadPath)
                .setCachePeriod(3600);

        // 各静态资源子目录统一用 staticBasePath 拼接
        String base = "file:" + staticBasePath + "/";
        registry.addResourceHandler("/feedback/**").addResourceLocations(base + "feedback/").setCachePeriod(3600);
        registry.addResourceHandler("/comment/**").addResourceLocations(base + "comment/").setCachePeriod(3600);
        registry.addResourceHandler("/communityPost/**").addResourceLocations(base + "communityPost/").setCachePeriod(3600);
        registry.addResourceHandler("/communityPostComment/**").addResourceLocations(base + "communityPostComment/").setCachePeriod(3600);

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/public/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
