//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.config;

import io.minio.MinioClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



//#######################################################################
//#######################################################################
//#######################################################################
// class
@Configuration
public class MinioConfig {

    @Value("$minio.host")
    private String host;

    @Value("$minio.port")
    private int port;

    @Value("$minio.accessKey")
    private String accessKey;

    @Value("$minio.secretKey")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://" + host + ":" + port)
                .credentials(accessKey, secretKey)
                .build();
    }
}
