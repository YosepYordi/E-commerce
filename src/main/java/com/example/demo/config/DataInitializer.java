package com.example.demo.config;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            List<Product> initialToys = List.of(
                new Product(
                    "Oso de Peluche Gigante Huggy",
                    "Peluche supersuave de 80 cm, hipoalergénico e ideal para abrazos.",
                    new BigDecimal("89.90"),
                    25,
                    "Peluches",
                    "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=500&auto=format&fit=crop"
                ),
                new Product(
                    "Carro Control Remoto Monster Truck",
                    "Vehículo todoterreno 4x4 con suspensión alta y batería recargable.",
                    new BigDecimal("129.50"),
                    15,
                    "Vehículos",
                    "https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=500&auto=format&fit=crop"
                ),
                new Product(
                    "Set de Construcción Nave Espacial Explorer",
                    "Juego de bloques de 450 piezas para armar una nave espacial futurista.",
                    new BigDecimal("149.99"),
                    10,
                    "Construcción",
                    "https://images.unsplash.com/photo-1585366119957-e9730b6d0f60?w=500&auto=format&fit=crop"
                ),
                new Product(
                    "Robot Interactivo Programable Boty",
                    "Robot educativo con luces LED, sonidos y control gestual por app.",
                    new BigDecimal("199.00"),
                    8,
                    "Educativos",
                    "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=500&auto=format&fit=crop"
                ),
                new Product(
                    "Juego de Mesa Aventureros del Reino",
                    "Juego de estrategia familiar para 2 a 5 jugadores. ¡Diversión garantizada!",
                    new BigDecimal("75.00"),
                    20,
                    "Juegos de Mesa",
                    "https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=500&auto=format&fit=crop"
                ),
                new Product(
                    "Muñeca Mágica con Accesorios",
                    "Muñeca articulada de 30 cm con vestidos intercambiables y varita brillante.",
                    new BigDecimal("64.90"),
                    18,
                    "Muñecas",
                    "https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=500&auto=format&fit=crop"
                )
            );

            productRepository.saveAll(initialToys);
        }
    }
}
