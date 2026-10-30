package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/*
 * =====================================================================
 * ENTREGA DEL TALLER - PRUEBAS DE CARRITO DE COMPRAS
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

class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() {
        carrito = new CarritoCompras(); // Objeto nuevo para cada prueba (Independencia)
    }

    // =====================================================================
    // PARTE A: PRUEBAS DE VALIDACIÓN Y SUBTOTAL (R1 y R2)
    // =====================================================================

    @Test
    @DisplayName("Carrito vacío retorna subtotal 0 y total 0")
    void calcularSubtotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        // Arrange
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        // Act & Assert: (8000*3) + (2000*5) = 24000 + 10000 = 34000
        assertEquals(34_000, carrito.calcularSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Rechaza nombre nulo o vacío")
    void agregarProducto_nombreInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto(null, 1000, 1));
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("", 1000, 1));
    }

    @Test
    @DisplayName("Rechaza precio cero o negativo")
    void agregarProducto_precioInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("Lápiz", 0, 1));
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("Lápiz", -500, 1));
    }

    /* 
     * =====================================================================
     * 🚨 LA PRUEBA QUE ATRAPÓ EL BUG #2 (La cantidad en cero) 🚨
     * =====================================================================
     * ¿Por qué es importante esta prueba?
     * La regla de negocio (R1) dice claramente: "Un producto necesita... 
     * cantidad mayor a 0". 
     * 
     * ¿Cómo se encontró el error?
     * Al ejecutar esta prueba enviando cantidad 0, la prueba se puso en ROJO. 
     * El código fuente original tenía esta línea:
     * 
     * if (cantidad < 0) { throw new IllegalArgumentException... }
     * 
     * Al usar el operador "<" (menor estricto), el número 0 no era rechazado. 
     * La prueba nos demostró que el código no estaba validando correctamente 
     * la regla de negocio.
     * 
     * Solución: Se cambió el "<" por "<=" en CarritoCompras.java.
     * =====================================================================
     */
    @Test
    @DisplayName("Rechaza cantidad cero o negativa")
    void agregarProducto_cantidadInvalida_lanzaExcepcion() {
        // Falla si el código permite cantidad = 0
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("Lápiz", 1000, 0));
        // Falla si el código permite cantidad < 0
        assertThrows(IllegalArgumentException.class, () -> carrito.agregarProducto("Lápiz", 1000, -1));
    }

    // =====================================================================
    // PARTE B: CUPONES Y TOTALES (R3 a R6)
    // =====================================================================

    @Test
    @DisplayName("Cupón SENA10 aplica 10% de descuento")
    void calcularDescuento_cuponSENA10_retornaDiezPorCiento() {
        carrito.agregarProducto("Producto", 100_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(10_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Cupón BIENVENIDO aplica $5.000 justo en el límite $50.000")
    void calcularDescuento_bienvenidoExacto50k_retorna5000() {
        carrito.agregarProducto("Producto", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Cupón BIENVENIDO NO aplica si el subtotal es menor a $50.000")
    void calcularDescuento_bienvenidoMenos50k_retornaCero() {
        carrito.agregarProducto("Producto", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(strings = {"OTROCUPON", "SENA20", "DESCUENTO"})
    @DisplayName("Rechaza cupones inválidos")
    void aplicarCupon_cuponInvalido_lanzaExcepcion(String cuponInvalido) {
        assertThrows(IllegalArgumentException.class, () -> carrito.aplicarCupon(cuponInvalido));
    }

    @Test
    @DisplayName("Total con envío pagado (menor a $150.000 con IVA)")
    void calcularTotal_envioPagado_retornaTotalConEnvio() {
        // $100.000 * 1.19 = $119.000. Como es menor a $150.000, suma envío de $12.000.
        // Total = $131.000
        carrito.agregarProducto("Libro", 100_000, 1);
        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con envío gratis (>= $150.000 con IVA) y cupón SENA10")
    void calcularTotal_envioGratisYCupon_retornaTotalCorrecto() {
        // $500.000 - 10% descuento = $450.000. 
        // $450.000 * 1.19 (IVA) = $535.500.
        // Como $535.500 > $150.000, el envío es $0. Total = $535.500.
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }
}