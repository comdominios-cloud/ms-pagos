package pe.edu.utec.condominio.pagos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de ms-pagos.
 *
 * ANDAMIAJE: solo arranca el contexto de Spring Boot y expone Swagger-UI.
 * Controladores, entidades y logica de negocio se implementan mas adelante.
 */
@SpringBootApplication
public class MsPagosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPagosApplication.class, args);
    }
}
