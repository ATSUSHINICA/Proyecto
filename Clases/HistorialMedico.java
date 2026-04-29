/**
 * Clase que guarda todo el historial médico de un paciente
 * Esta clase es POSEE por Paciente (composición)
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 */

import java.util.ArrayList;
import java.util.List;

public class HistorialMedico {
    
    /**
     * Strings con formato (separador ;) para guardar los datos
     * Ejemplo: "Asma;Diabetes;" 
     * Uso String porque así lo indica el diagrama de clases
     * Los separadores permiten guardar múltiples valores en un solo String
     */
    private String enfermedadesPrevias = "";     // Formato: "enfermedad1;enfermedad2;"
    private String alergias = "";               // Formato: "alergia1;alergia2;"
    private String intervencionesQuirurgicas = "";
    private String enfermedadesCronicas = "";
    private String medicacion = "";
    private String diagnosticos = "";
    private String tratamientos = "";

    /**
     * Método auxiliar: añade un elemento a un String con separadores
     * 
     * @param campo String actual (ej: "Asma;Diabetes;")
     * @param nuevo Elemento a añadir (ej: "Hipertension")
     * @return String actualizado (ej: "Asma;Diabetes;Hipertension;")
     */
    private String agregarAString(String campo, String nuevo) {
        if (campo == null || campo.isEmpty()) {
            return nuevo + ";";
        } else {
            return campo + nuevo + ";";
        }
    }
    
    /**
     * Método auxiliar: cuenta cuántos elementos hay en un String con separadores
     * Recorre el String contando los ;
     * 
     * @param texto String con formato "dato1;dato2;dato3;"
     * @return Número de elementos
     */
    private int contarElementos(String texto) {
        if (texto == null || texto.isEmpty()) {
            return 0;
        }
        
        int contador = 0;
        int posicion = 0;
        
        while (posicion < texto.length()) {
            if (texto.charAt(posicion) == ';') {
                contador++;
            }
            posicion++;
        }
        
        return contador;
    }
    
    /**
     * Método auxiliar: imprime todos los elementos de un String con separadores
     * Recorre el String extrayendo substrings entre ;
     * 
     * @param texto String con formato "dato1;dato2;dato3;"
     * @param titulo Título a mostrar antes de la lista
     */
    private void imprimirElementos(String texto, String titulo) {
        System.out.println(titulo);
        
        if (texto == null || texto.isEmpty()) {
            System.out.println("  (Ninguno registrado)");
            return;
        }
        
        int inicio = 0;
        int fin = texto.indexOf(";");
        boolean hayAlguno = false;
        
        while (fin != -1) {
            String parte = texto.substring(inicio, fin);
            if (!parte.isEmpty()) {
                System.out.println("  - " + parte);
                hayAlguno = true;
            }
            inicio = fin + 1;
            fin = texto.indexOf(";", inicio);
        }
        
        if (!hayAlguno) {
            System.out.println("  (Ninguno registrado)");
        }
    }
    
    /**
     * Método auxiliar: busca si un String contiene un elemento concreto
     * Recorre el String comparando cada parte
     * 
     * @param texto String con formato "dato1;dato2;dato3;"
     * @param buscar El elemento a buscar
     * @return true si lo encuentra, false si no
     */
    private boolean contieneElemento(String texto, String buscar) {
        if (texto == null || texto.isEmpty() || buscar == null) {
            return false;
        }
        
        int inicio = 0;
        int fin = texto.indexOf(";");
        
        while (fin != -1) {
            String parte = texto.substring(inicio, fin);
            if (parte.equalsIgnoreCase(buscar)) {
                return true;
            }
            inicio = fin + 1;
            fin = texto.indexOf(";", inicio);
        }
        
        return false;
    }
    
    /**
     * Método auxiliar: busca elementos que contengan una palabra
     * 
     * @param texto String con formato "dato1;dato2;dato3;"
     * @param palabra La palabra a buscar
     * @return Lista con los elementos que coinciden
     */
    private List<String> buscarElementosPorPalabra(String texto, String palabra) {
        List<String> resultados = new ArrayList<>();
        
        if (texto == null || texto.isEmpty() || palabra == null) {
            return resultados;
        }
        
        int inicio = 0;
        int fin = texto.indexOf(";");
        
        while (fin != -1) {
            String parte = texto.substring(inicio, fin);
            if (!parte.isEmpty() && parte.toLowerCase().contains(palabra.toLowerCase())) {
                resultados.add(parte);
            }
            inicio = fin + 1;
            fin = texto.indexOf(";", inicio);
        }
        
        return resultados;
    }

    // MÉTODOS PARA AÑADIR DATOS
    
    public void agregarEnfermedadPrevia(String enfermedad) { 
        enfermedadesPrevias = agregarAString(enfermedadesPrevias, enfermedad); 
    }
    
    public void agregarAlergia(String alergia) { 
        alergias = agregarAString(alergias, alergia); 
    }
    
    public void agregarIntervencion(String intervencion) { 
        intervencionesQuirurgicas = agregarAString(intervencionesQuirurgicas, intervencion); 
    }
    
    public void agregarEnfermedadCronica(String enfermedad) { 
        enfermedadesCronicas = agregarAString(enfermedadesCronicas, enfermedad); 
    }
    
    public void agregarMedicacion(String medicamento) { 
        medicacion = agregarAString(medicacion, medicamento); 
    }
    
    public void agregarDiagnostico(String diagnostico) { 
        diagnosticos = agregarAString(diagnosticos, diagnostico); 
    }
    
    public void agregarTratamiento(String tratamiento) { 
        tratamientos = agregarAString(tratamientos, tratamiento); 
    }
    
    // BÚSQUEDA 
    
    /**
     * BÚSQUEDA LINEAL: comprueba si el paciente tiene cierta alergia
     * 
     * @param alergia La alergia a buscar
     * @return true si la tiene, false si no
     */
    public boolean contieneAlergia(String alergia) {
        return contieneElemento(alergias, alergia);
    }
    
    /**
     * BÚSQUEDA CON FILTRO: busco diagnósticos que contengan cierta palabra
     * 
     * @param palabra La palabra a buscar
     * @return Lista con los diagnósticos que coinciden
     */
    public List<String> buscarDiagnosticosPorPalabra(String palabra) {
        return buscarElementosPorPalabra(diagnosticos, palabra);
    }
    
    // OPERACIÓN AGREGADA
    
    /**
     * OPERACIÓN AGREGADA: cuenta cuántos antecedentes médicos tiene en total
     * 
     * @return Número total de antecedentes
     */
    public int getTotalAntecedentes() {
        return contarElementos(enfermedadesPrevias) + 
               contarElementos(alergias) + 
               contarElementos(intervencionesQuirurgicas) + 
               contarElementos(enfermedadesCronicas) + 
               contarElementos(medicacion);
    }
    
    // MOSTRAR HISTORIAL 
    
    /**
     * Muestra todo el historial clínico de forma ordenada
     */
    public void mostrarHistorial() {
        imprimirElementos(enfermedadesPrevias, "--- ENFERMEDADES PREVIAS ---");
        imprimirElementos(alergias, "--- ALERGIAS ---");
        imprimirElementos(intervencionesQuirurgicas, "--- INTERVENCIONES QUIRÚRGICAS ---");
        imprimirElementos(enfermedadesCronicas, "--- ENFERMEDADES CRÓNICAS ---");
        imprimirElementos(medicacion, "--- MEDICACIÓN ---");
        imprimirElementos(diagnosticos, "--- DIAGNÓSTICOS ---");
        imprimirElementos(tratamientos, "--- TRATAMIENTOS ---");
        
        System.out.println("Total de antecedentes médicos: " + getTotalAntecedentes());
    }
}