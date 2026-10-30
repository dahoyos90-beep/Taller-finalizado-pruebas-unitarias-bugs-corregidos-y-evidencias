package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/*
 * =====================================================================
 * ENTREGA DEL TALLER - PRUEBAS DE CALCULADORA DE NOTAS
 * Nombre completo: Daniel Alejandro Hoyos Rios
 * Ficha: 3231666
 * =====================================================================
 * 
 * 1. BUG ENCONTRADO (Método y línea):
 *    - En el método `estaAprobado`, línea de retorno, se usaba el operador `>` 
 *      en lugar de `>=`.
 * 
 * 2. PRUEBA QUE LO DETECTÓ:
 *    - `estaAprobado_notaExactamenteTres_retornaTrue` (al evaluar el caso límite 3.0).
 * 
 * 3. USO CRÍTICO DE LA IA Y AJUSTES PARA VISUAL STUDIO CODE:
 *    - Le pedí a la IA que generara las pruebas unitarias dándole las reglas 
 *      de negocio, NO el código.
 *    - Ajuste en el entorno (Visual Studio Code): Como usé VS Code en lugar de 
 *      IntelliJ, tuve que instalar la extensión "Extension Pack for Java" y 
 *      "Test Runner for Java" para poder ver el botón de ejecutar pruebas (►) 
 *      y el panel de cobertura (Coverage). Además, usé la terminal integrada 
 *      de VS Code para correr `mvn clean test jacoco:report` y ver el reporte 
 *      de JaCoCo.
 * =====================================================================
 */

class CalculadoraNotasTest {

    private CalculadoraNotas calculadora;

    @BeforeEach
    void setUp() {
        calculadora = new CalculadoraNotas(); // Objeto nuevo para cada prueba (Independencia)
    }

    // =====================================================================
    // 1. CAMINO FELIZ
    // =====================================================================
    @Test
    @DisplayName("Calcula la nota final ponderada 30/30/40")
    void calcularNotaFinal_notasValidas_retornaPromedioPonderado() {
        // Arrange
        double seguimiento = 4.0, parcial = 3.0, proyecto = 5.0;
        // Act
        double resultado = calculadora.calcularNotaFinal(seguimiento, parcial, proyecto);
        // Assert
        assertEquals(4.1, resultado, 0.001); 
        // (4.0 * 0.30) + (3.0 * 0.30) + (5.0 * 0.40) = 1.2 + 0.9 + 2.0 = 4.1
    }

    // =====================================================================
    // 2. PRUEBAS DE APROBACIÓN (Aquí vive el Bug principal)
    // =====================================================================
    @Test
    @DisplayName("Un estudiante con 3.5 aprueba")
    void estaAprobado_notaMayorATres_retornaTrue() {
        assertTrue(calculadora.estaAprobado(3.5));
    }

    @Test
    @DisplayName("Un estudiante con 2.9 reprueba")
    void estaAprobado_notaMenorATres_retornaFalse() {
        assertFalse(calculadora.estaAprobado(2.9));
    }

    /* 
     * =====================================================================
     * 🚨 LA PRUEBA QUE ATRAPÓ EL BUG #1 (El caso del aprendiz con 3.0) 🚨
     * =====================================================================
     * ¿Por qué es importante esta prueba?
     * Porque prueba la "frontera" exacta. Al principio, esta prueba se puso 
     * en ROJO (falló). ¿Por qué? Porque en el código fuente original 
     * (CalculadoraNotas.java) la línea decía:
     * 
     * return notaFinal > NOTA_APROBATORIA; 
     * 
     * Al usar el operador ">" (mayor estricto), el número 3.0 NO era 
     * considerado, dando como resultado "false" (reprobado). 
     * 
     * La prueba nos demostró que el código no cumplía la regla de negocio 
     * que dice: "Un estudiante aprueba con 3.0 O MÁS".
     * 
     * Solución aplicada: Se cambió el ">" por ">=" en el código fuente.
     * Al hacerlo, la prueba pasó a VERDE.
     * =====================================================================
     */
    @Test
    @DisplayName("Caso límite: exactamente 3.0 aprueba")
    void estaAprobado_notaExactamenteTres_retornaTrue() {
        // Arrange
        double nota = 3.0;
        
        // Act
        boolean resultado = calculadora.estaAprobado(nota);
        
        // Assert
        assertTrue(resultado, "Un estudiante con 3.0 debería aprobar");
    }

    // =====================================================================
    // 3. ESCALA DE DESEMPEÑO (Pruebas parametrizadas)
    // =====================================================================
    @ParameterizedTest(name = "Nota {0} -> {1}")
    @CsvSource({
        "5.0, Superior", "4.6, Superior", // Frontera Superior
        "4.5, Alto",     "4.0, Alto",     // Frontera Alto
        "3.9, Básico",   "3.0, Básico",   // Frontera Básico
        "2.9, Bajo",     "0.0, Bajo"      // Frontera Bajo
    })
    @DisplayName("Clasifica el desempeño según la escala")
    void desempeno_segunNota_retornaCategoria(double nota, String esperado) {
        assertEquals(esperado, calculadora.desempeno(nota));
    }

    // =====================================================================
    // 4. ENTRADAS INVÁLIDAS (Validaciones)
    // =====================================================================
    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 5.1, 10.0})
    @DisplayName("Rechaza notas fuera del rango 0.0 – 5.0")
    void calcularNotaFinal_notaFueraDeRango_lanzaExcepcion(double notaInvalida) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> calculadora.calcularNotaFinal(notaInvalida, 3.0, 3.0));
        assertTrue(ex.getMessage().contains("entre 0.0 y 5.0"));
    }
}