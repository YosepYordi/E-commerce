package com.example.demo.config;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            List<Product> initialToys = List.of(
                Product.builder()
                    .nombre("Oso de Peluche Gigante Huggy")
                    .descripcion("Peluche supersuave de 80 cm, hipoalergénico e ideal para abrazos.")
                    .precio(new BigDecimal("89.90"))
                    .stock(25)
                    .categoria("Peluches")
                    .imagenUrl("https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop")
                    .build(),
                Product.builder()
                    .nombre("Carro Control Remoto Monster Truck")
                    .descripcion("Vehículo todoterreno 4x4 con suspensión alta y batería recargable.")
                    .precio(new BigDecimal("129.50"))
                    .stock(15)
                    .categoria("Vehículos")
                    .imagenUrl("https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=500&auto=format&fit=crop")
                    .build(),
                Product.builder()
                    .nombre("Set de Construcción Nave Espacial Explorer")
                    .descripcion("Juego de bloques de 450 piezas para armar una nave espacial futurista.")
                    .precio(new BigDecimal("149.99"))
                    .stock(10)
                    .categoria("Construcción")
                    .imagenUrl("https://images.unsplash.com/photo-1585366119957-e9730b6d0f60?w=500&auto=format&fit=crop")
                    .build(),
                Product.builder()
                    .nombre("Robot Interactivo Programable Boty")
                    .descripcion("Robot educativo con luces LED, sonidos y control gestual por app.")
                    .precio(new BigDecimal("199.00"))
                    .stock(8)
                    .categoria("Educativos")
                    .imagenUrl("https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=500&auto=format&fit=crop")
                    .build(),
                Product.builder()
                    .nombre("Juego de Mesa Aventureros del Reino")
                    .descripcion("Juego de estrategia familiar para 2 a 5 jugadores. ¡Diversión garantizada!")
                    .precio(new BigDecimal("75.00"))
                    .stock(20)
                    .categoria("Juegos de Mesa")
                    .imagenUrl("https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=500&auto=format&fit=crop")
                    .build(),
                Product.builder()
                    .nombre("Muñeca Mágica con Accesorios")
                    .descripcion("Muñeca articulada de 30 cm con vestidos intercambiables y varita brillante.")
                    .precio(new BigDecimal("64.90"))
                    .stock(18)
                    .categoria("Muñecas")
                    .imagenUrl("https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=500&auto=format&fit=crop")
                    .build()
            );

            productRepository.saveAll(initialToys);
        }
    }
}
