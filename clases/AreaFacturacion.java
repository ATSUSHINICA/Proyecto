/**
 * Clase que maneja todas las facturas del hospital
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 */

import java.time.LocalDate;
import java.util.*;

public class AreaFacturacion {
    
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

    /**
     * Constructor de factura
     * 
     * @param idFactura Identificador único de la factura
     * @param concepto Concepto de la factura
     * @param importe Cantidad a pagar (no puede ser negativa)
     * @param fechaFactura Fecha de emisión
     */
    public AreaFacturacion(int idFactura, String concepto, double importe, LocalDate fechaFactura) {
        if (importe < 0) {
            throw new IllegalArgumentException("El importe no puede ser negativo");
        }
        this.idFactura = idFactura;
        this.concepto = concepto;
        this.importe = importe;
        this.fechaFactura = fechaFactura;
    }
    
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
     * OPERACIÓN AGREGADA: sumar todas las facturas del sistema
     * 
     * @return Total facturado
     */
    public static double getFacturacionTotal() {
        double total = 0;
        for (AreaFacturacion f : facturas) {
            total += f.importe;
        }
        return total;
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
     * POLIMORFISMO (SOBRECARGA): notificar un procedimiento con importe por defecto (50€)
     * 
     * @param paciente Paciente al que se le notifica
     * @param concepto Concepto del procedimiento
     */
    public static void notificarProcedimiento(Paciente paciente, String concepto) {
        notificarProcedimiento(paciente, concepto, 50.0);
    }
    
    /**
     * POLIMORFISMO (SOBRECARGA): notificar un procedimiento con importe específico
     * 
     * @param paciente Paciente al que se le notifica
     * @param concepto Concepto del procedimiento
     * @param importe Importe a cobrar
     */
    public static void notificarProcedimiento(Paciente paciente, String concepto, double importe) {
        try {
            // Uso el contador estático para generar un ID único
            int id = contadorFacturas++;
            AreaFacturacion factura = new AreaFacturacion(id, concepto, importe, LocalDate.now());
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
        System.out.println("Total facturado: " + getFacturacionTotal() + " €");
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
        
        System.out.println("\n=== FACTURAS DE " + paciente.getNombreCompleto() + " (HC: " + hc + ") ===");
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
    
    // Getters
    public int getIdFactura() { return idFactura; }
    public String getConcepto() { return concepto; }
    public double getImporte() { return importe; }
    public LocalDate getFechaFactura() { return fechaFactura; }
}