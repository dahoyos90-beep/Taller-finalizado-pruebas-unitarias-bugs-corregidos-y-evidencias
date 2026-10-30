package co.edu.notas;

/*
 * =====================================================================
 * ENTREGA DEL TALLER - CALCULADORA DE NOTAS
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

public class CalculadoraNotas {

    public static final double NOTA_MINIMA = 0.0;
    public static final double NOTA_MAXIMA = 5.0;
    public static final double NOTA_APROBATORIA = 3.0;

    /** Calcula la nota final: 30% seguimiento + 30% parcial + 40% proyecto. */
    public double calcularNotaFinal(double seguimiento, double parcial, double proyecto) {
        validarNota(seguimiento);
        validarNota(parcial);
        validarNota(proyecto);
        double resultado = seguimiento * 0.30 + parcial * 0.30 + proyecto * 0.40;
        return Math.round(resultado * 10) / 10.0; // redondea a una décima
    }

    /** Aprueba quien obtiene 3.0 o más. */
    public boolean estaAprobado(double notaFinal) {
        validarNota(notaFinal);
        
        /* 
         * =====================================================================
         * 🐛 BUG #1 ENCONTRADO Y CORREGIDO
         * =====================================================================
         * Línea original con el error: 
         * return notaFinal > NOTA_APROBATORIA;
         * 
         * ¿Por qué fallaba? 
         * El operador ">" significa "estrictamente mayor". Si un estudiante sacaba 
         * exactamente 3.0, el programa decía que había reprobado. Pero la regla 
         * de negocio dice claramente que se aprueba con 3.0 "o más".
         * 
         * ¿Cómo lo atrapamos? 
         * Con la prueba unitaria "estaAprobado_notaExactamenteTres_retornaTrue". 
         * La prueba se puso ROJA (falló) y nos mostró que el código no cumplía la regla.
         * 
         * Solución: Cambiamos el ">" por ">=".
         * =====================================================================
         */
        return notaFinal >= NOTA_APROBATORIA;
    }

    /** Escala de desempeño: Superior, Alto, Básico, Bajo. */
    public String desempeno(double notaFinal) {
        validarNota(notaFinal);
        if (notaFinal >= 4.6)
            return "Superior";
        if (notaFinal >= 4.0)
            return "Alto";
        if (notaFinal >= 3.0)
            return "Básico";
        return "Bajo";
    }

    private void validarNota(double nota) {
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new IllegalArgumentException("La nota debe estar entre 0.0 y 5.0: " + nota);
        }
    }
}