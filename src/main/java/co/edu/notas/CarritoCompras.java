package co.edu.notas;

import java.util.ArrayList;
import java.util.List;

/*
 * =====================================================================
 * ENTREGA DEL TALLER - CARRITO DE COMPRAS
 * Nombre completo: Daniel Alejandro Hoyos Rios
 * Ficha: 3231666
 * =====================================================================
 * 
 * 1. BUG ENCONTRADO (Método y línea):
 *    - En el método `agregarProducto`, validación de la cantidad. 
 *      Se usaba el operador `<` en lugar de `<=`.
 * 
 * 2. PRUEBA QUE LO DETECTÓ:
 *    - `agregarProducto_cantidadInvalida_lanzaExcepcion` (al evaluar cantidad 0).
 * 
 * 3. USO CRÍTICO DE LA IA Y AJUSTES PARA VISUAL STUDIO CODE:
 *    - Le pedí a la IA que generara las pruebas unitarias dándole las reglas 
 *      de negocio (R1 a R6), NO el código.
 *    - Ajuste en el entorno (Visual Studio Code): Como usé VS Code en lugar de 
 *      IntelliJ, tuve que instalar la extensión "Extension Pack for Java" y 
 *      "Test Runner for Java" para poder ver el botón de ejecutar pruebas (►) 
 *      y el panel de cobertura (Coverage). Además, usé la terminal integrada 
 *      de VS Code para correr `mvn clean test jacoco:report` y ver el reporte 
 *      de JaCoCo.
 * =====================================================================
 */

public class CarritoCompras {

    public static final double IVA = 0.19;
    public static final double MINIMO_ENVIO_GRATIS = 150_000;
    public static final double COSTO_ENVIO = 12_000;

    private final List<Item> items = new ArrayList<>();
    private String cupon = null;

    public record Item(String nombre, double precio, int cantidad) { }

    public void agregarProducto(String nombre, double precio, int cantidad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser positivo");
        }
        
        /* 
         * =====================================================================
         * 🐛 BUG #2 ENCONTRADO Y CORREGIDO
         * =====================================================================
         * Error original: `if (cantidad < 0)`
         * 
         * ¿Por qué estaba mal? 
         * La regla de negocio (R1) exige que la cantidad sea "mayor a cero". 
         * Al usar `< 0`, el sistema permitía agregar productos con cantidad 0 
         * (ej. un cuaderno con cantidad 0), lo cual no tiene sentido y debería ser rechazado.
         * 
         * ¿Cómo se descubrió? 
         * Con la prueba unitaria `agregarProducto_cantidadInvalida_lanzaExcepcion`. 
         * La prueba se puso ROJA porque enviaba cantidad 0 y el código no lanzaba la excepción.
         * 
         * Solución: Cambiar `<` por `<=` para rechazar también el cero.
         * =====================================================================
         */
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        items.add(new Item(nombre, precio, cantidad));
    }

    public int cantidadDeItems() {
        return items.size();
    }

    public double calcularSubtotal() {
        double subtotal = 0;
        for (Item item : items) {
            subtotal += item.precio() * item.cantidad();
        }
        return subtotal;
    }

    /** Cupones válidos: "SENA10" (10 % de descuento) y "BIENVENIDO" ($5.000 si el subtotal es ≥ $50.000). */
    public void aplicarCupon(String codigo) {
        if (!"SENA10".equals(codigo) && !"BIENVENIDO".equals(codigo)) {
            throw new IllegalArgumentException("Cupón inválido: " + codigo);
        }
        this.cupon = codigo;
    }

    public double calcularDescuento() {
        double subtotal = calcularSubtotal();
        if ("SENA10".equals(cupon)) return subtotal * 0.10;
        if ("BIENVENIDO".equals(cupon) && subtotal >= 50_000) return 5_000;
        return 0;
    }

    /** Total = (subtotal − descuento) + IVA + envío (gratis desde $150.000 antes de envío). */
    public double calcularTotal() {
        if (items.isEmpty()) return 0;
        double base = calcularSubtotal() - calcularDescuento();
        double conIva = base * (1 + IVA);
        double envio = conIva >= MINIMO_ENVIO_GRATIS ? 0 : COSTO_ENVIO;
        return Math.round(conIva + envio);
    }
}