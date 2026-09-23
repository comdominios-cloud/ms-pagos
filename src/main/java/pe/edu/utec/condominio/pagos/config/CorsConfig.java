package pe.edu.utec.condominio.pagos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Permite que el frontend llame a este microservicio desde el navegador.
 *
 * Antes de cada peticion con cabecera Authorization, el navegador manda un
 * OPTIONS de comprobacion. Sin esta configuracion Spring lo rechazaba con 403
 * y el navegador cancelaba la peticion, aunque desde curl la misma ruta
 * respondiera 200. Los otros microservicios ya lo tenian resuelto: FastAPI con
 * su CORSMiddleware y Express con el paquete cors.
 *
 * Se permite cualquier origen porque el microservicio no es accesible desde
 * internet: solo lo alcanza el balanceador interno, que a su vez solo recibe
 * trafico del API Gateway.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registro) {
        registro.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Type")
                .maxAge(600);
    }
}
