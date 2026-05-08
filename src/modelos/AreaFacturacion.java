package modelos;

/*
===================================
|             REVISADO            |
===================================

*/
/**
 * Clase que maneja todas las facturas del hospital
 * 
 * @author: Laura Leciñena, Alejandro Díaz 
 */

import excepciones.EstadoPacienteException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class AreaFacturacion implements Serializable {

    private static final long serialVersionUID = 4L;
    
    private final int idFactura;
    private String concepto;
    private double importe;
    private LocalDate fechaFactura;
    
    /**
     * LISTA ESTÁTICA: aquí guardo TODAS las facturas que se han creado
     * 
     * ¿Por qué static?
     * - Si no fuera static, cada factura tendría su propia lista
     * - Con static, TODAS las facturas comparten la MISMA lista
     * - Así puedo listar todas las facturas del sistema fácilmente
     */
    private static List<AreaFacturacion> facturas = new ArrayList<>();
    
    /**
     * MAPA ESTÁTICO (HashMap) para buscar rápido las facturas de un paciente
     * Clave = número de historia clínica del paciente (int)
     * Valor = lista de sus facturas
     * 
     * ¿Por qué static?
     * - El mapa debe ser el mismo para todas las facturas
     * - Si no fuera static, cada factura tendría su propio mapa vacío
     * - Con static, todas las facturas se añaden al MISMO mapa
     */
    private static Map<Integer, List<AreaFacturacion>> facturasPorPaciente = new HashMap<>();
    
    /**
     * CONTADOR ESTÁTICO para generar IDs de factura automáticos
     * 
     * ¿Por qué static?
     * - Cada factura necesita un ID ÚNICO en todo el sistema
     * - Si no fuera static, cada factura empezaría a contar desde 1 otra vez
     * - Con static, el contador es GLOBAL y siempre aumenta
     */
    private static int contadorFacturas = 1;


    //============================================== CONSTRUCTOR =====================================================

    /**
     * Constructor de factura
     * 
     * @param idFactura Identificador único de la factura
     * @param concepto Concepto de la factura
     * @param importe Cantidad a pagar (no puede ser negativa)
     * @param fechaFactura Fecha de emisión
     */
    public AreaFacturacion(String concepto, double importe, LocalDate fechaFactura) {

        this.idFactura = validarIdFactura(contadorFacturas++);
        this.concepto = validarConcepto(concepto);
        this.importe = validarImporte(importe);
        this.fechaFactura = validarFecha(fechaFactura);
    }
    
    //============================================= METODOS DE VALIDACION PRIVADOS =============================================

    /**
     * Valida que el ID de factura sea mayor que 0.
     * @param idFactura Identificador único de la factura
     * @return idFactura validado
     */

    private int validarIdFactura(int idFactura) {

        if (idFactura <= 0) {
            throw new IllegalArgumentException("El ID de la factura debe ser mayor que 0");
        }

        return idFactura;
    }

    /**
     * Valida que el concepto no sea nulo ni vacío.
     * @param concepto Concepto de la factura
     * @return concepto validado
     */

    private String validarConcepto(String concepto) {

        if (concepto == null) {
            throw new NullPointerException("El concepto no puede ser nulo");
        }

        boolean soloEspacios = true;

        /* Recorremos carácter a carácter para comprobar si hay algo distinto de espacio */

        for (int i = 0; i < concepto.length(); i++) {
            if (concepto.charAt(i) != ' ') {
                soloEspacios = false;
            }
        }

        if (concepto.length() == 0 || soloEspacios) {
            throw new IllegalArgumentException("El concepto no puede estar vacío");
        }

        return concepto;
    }

    /**
     * Valida que el importe sea mayor que 0.
     * @param importe Cantidad económica de la factura
     * @return importe validado
     */

    private double validarImporte(double importe) {

        if (importe <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor que 0");
        }

        return importe;
    }


    /**
     * Valida que la fecha no sea nula ni futura.
     * @param fechaFactura Fecha de emisión de la factura
     * @return fechaFactura validada
     */

    private LocalDate validarFecha(LocalDate fechaFactura) {

        if (fechaFactura == null) {
            throw new NullPointerException("La fecha de la factura no puede ser nula");
        }

        if (fechaFactura.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de la factura no puede ser futura");
            }
        return fechaFactura;
    }

    //============================================= METODOS PUBLICOS ======================================================== 
    /**
     * Generar una factura y asociarla al paciente
     * 
     * @param paciente El paciente al que se le factura
     * @throws EstadoPacienteException Si el paciente está de baja temporal
     */
    public void generarFactura(Paciente paciente) throws EstadoPacienteException {

        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
        // VALIDACIÓN: No se puede facturar a un paciente de baja temporal
        if ("BAJA_TEMPORAL".equals(paciente.getEstado())) {
            throw new EstadoPacienteException("No se puede facturar a un paciente de baja temporal");
        }

        // VALIDACIÓN: No se puede facturar a un paciente de baja permanente
        if ("BAJA_PERMANENTE".equals(paciente.getEstado())) {
            throw new EstadoPacienteException("No se puede facturar a un paciente de baja permanente");
        }
        
        
        // Añado la factura a la lista general (static)
        facturas.add(this);
        
        // Añado la factura al mapa, organizada por paciente (static)
        int hc = paciente.getNumeroHistoriaClinica();
        facturasPorPaciente.putIfAbsent(hc, new ArrayList<>());
        facturasPorPaciente.get(hc).add(this);
        
        System.out.println("===================================");
        System.out.println("FACTURA GENERADA");
        System.out.println("  Paciente: " + paciente.getNombreCompleto());
        System.out.println("  HC Nº: " + hc);
        System.out.println("  Nº Factura: " + idFactura);
        System.out.println("  Concepto: " + concepto);
        System.out.println("  Importe: " + importe + " €");
        System.out.println("  Fecha: " + fechaFactura);
        System.out.println("===================================");
    }
    
    /**
     * BÚSQUEDA RÁPIDA usando el mapa estático
     * Busco las facturas de un paciente específico por su número de historia
     * 
     * @param numeroHistoria Número de historia clínica del paciente (int)
     * @return Lista de facturas del paciente (vacía si no tiene)
     */

    public static List<AreaFacturacion> buscarFacturasPorPaciente(int numeroHistoria) {
        // getOrDefault: si el paciente no tiene facturas, devuelvo una lista vacía (evito errores)
        return facturasPorPaciente.getOrDefault(numeroHistoria, new ArrayList<>());
    }

    /**
     * notificar un procedimiento con importe específico
     * 
     * @param paciente Paciente al que se le notifica
     * @param concepto Concepto del procedimiento
     * @param importe Importe a cobrar
     */
    public static void notificarProcedimiento(Paciente paciente, String concepto, double importe) {
        try {

            AreaFacturacion factura = new AreaFacturacion(concepto, importe, LocalDate.now());
            factura.generarFactura(paciente);
        } catch (EstadoPacienteException e) {
            System.out.println("ERROR AL FACTURAR: " + e.getMessage());
        }
    }
    
    /**
     * Muestra todas las facturas que hay en el sistema (usando la lista estática)
     */
    public static void listarTodasFacturas() {
        System.out.println("\n=== LISTADO COMPLETO DE FACTURAS ===");
        if (facturas.isEmpty()) {
            System.out.println("No hay facturas registradas");
            return;
        }
        
        for (AreaFacturacion f : facturas) {
            System.out.println("  " + f);
        }
        
    }
    
    /**
     * Muestra las facturas de un paciente específico (usando el mapa estático)
     * 
     * @param paciente El paciente a consultar
     */
    public static void mostrarFacturasDePaciente(Paciente paciente) {
        if (paciente == null) {
            System.out.println("Paciente no válido");
            return;
        }
        
        int hc = paciente.getNumeroHistoriaClinica();
        List<AreaFacturacion> facturasPaciente = facturasPorPaciente.getOrDefault(hc, new ArrayList<>());
        
        System.out.println("\n=== FACTURAS DE " + paciente.getNombreCompleto() + " (NHC: " + hc + ") ===");
        if (facturasPaciente.isEmpty()) {
            System.out.println("No tiene facturas");
        } else {
            for (AreaFacturacion f : facturasPaciente) {
                System.out.println("  " + f);
            }
        }
    }
    
    @Override
    public String toString() {
        return idFactura + " | " + concepto + " | " + importe + "€ | " + fechaFactura;
    }

    //======================================= GETTERS ==================================================
    
    // Getters
    public int getIdFactura() { return idFactura; }
    public String getConcepto() { return concepto; }
    public double getImporte() { return importe; }
    public LocalDate getFechaFactura() { return fechaFactura; }
}