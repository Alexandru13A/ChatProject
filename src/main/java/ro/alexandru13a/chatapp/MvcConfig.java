package ro.alexandru13a.chatapp;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class MvcConfig implements WebMvcConfigurer {

  @SuppressWarnings("null")
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    exposeDirectory("user-photos", registry);

  }

  public void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry){
    Path path = Paths.get(pathPattern);
    String absolutePath = path.toFile().getAbsolutePath();
    String logicalPath = pathPattern+"/**";

    registry.addResourceHandler(logicalPath).addResourceLocations("file:/"+absolutePath+"/");
  }
  
}
