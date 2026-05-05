/**
 * Clase Medico que hereda de Persona
 * Un médico es una persona con atributos específicos: número de colegiado y especialidad
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class Medico extends Persona {
    
    private final int numeroColegiado;  // int en lugar de String (según diagrama actualizado)
    private String especialidad;
    
    /**
     * ArrayList para guardar a los pacientes que ha atendido este médico
     * Uso ArrayList porque es dinámico: crece solo cuando añado pacientes
     * No sé cuántos pacientes va a atender en total, así que no puedo usar un array normal de tamaño fijo
     */
    private List<Paciente> pacientesAtendidos = new ArrayList<>();

    /**
     * Constructor del médico
     * Llama al constructor de Persona con super() para heredar dni, nombre, fecha y sexo
     * Luego asigna los atributos propios del médico
     * 
     * @param dni DNI del médico
     * @param nombreCompleto Nombre completo
     * @param fechaNacimiento Fecha de nacimiento (LocalDate)
     * @param sexo Sexo del médico
     * @param numeroColegiado Número de colegiado (único e inmutable)
     * @param especialidad Especialidad médica (Cardiología, Pediatría, etc.)
     */
    public Medico(String dni, String nombreCompleto, LocalDate fechaNacimiento, String sexo, int numeroColegiado, String especialidad) {
        // Llamo al constructor de la clase padre (Persona)
        super(dni, nombreCompleto, fechaNacimiento, sexo);
        this.numeroColegiado = numeroColegiado;
        this.especialidad = especialidad;
    }
    
    /**
     * Consultar el historial clínico completo de un paciente
     * 
     * @param paciente El paciente a consultar
     */
    public void consultarHistorial(Paciente paciente) {
        // Valido que el paciente no sea nulo
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
        System.out.println("\n=== HISTORIAL CLÍNICO COMPLETO ===");
        System.out.println("Médico: " + this.getNombreCompleto() + " (" + this.especialidad + ")");
        System.out.println("Paciente: " + paciente.getNombreCompleto());
        System.out.println("Historia Clínica Nº: " + paciente.getNumeroHistoriaClinica());
        
        // El paciente tiene su propio historial (composición), lo muestro
        paciente.getHistorial().mostrarHistorial();
    }
    
    /**
     * Añadir un diagnóstico al historial del paciente
     * 
     * @param paciente El paciente a diagnosticar
     * @param diagnostico El diagnóstico a añadir
     */
    public void añadirDiagnostico(Paciente paciente, String diagnostico) {
        // Valido que los datos no sean nulos o vacíos
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (diagnostico == null || diagnostico.isEmpty()) {
            throw new IllegalArgumentException("El diagnóstico no puede estar vacío");
        }
        
        // Añado el diagnóstico al historial del paciente
        paciente.getHistorial().agregarDiagnostico(diagnostico);
        System.out.println("Diagnóstico añadido: " + diagnostico);
        
        /**
         * NOTIFICACIÓN A FACTURACIÓN
         * Cada vez que se añade un diagnóstico, se avisa a facturación automáticamente
         * Es una operación agregada: el sistema integra la atención médica con la facturación
         */
        AreaFacturacion.notificarProcedimiento(paciente, "Consulta médica con diagnóstico", 50.0);
    }
    
    /**
     * Añadir un tratamiento al historial del paciente
     * 
     * @param paciente El paciente a tratar
     * @param tratamiento El tratamiento a añadir
     */
    public void añadirTratamiento(Paciente paciente, String tratamiento) {
        // Valido los datos
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (tratamiento == null || tratamiento.isEmpty()) {
            throw new IllegalArgumentException("El tratamiento no puede estar vacío");
        }
        
        // Añado el tratamiento al historial del paciente
        paciente.getHistorial().agregarTratamiento(tratamiento);
        System.out.println("Tratamiento añadido: " + tratamiento);
        
        /**
         * Añado el paciente a la lista de atendidos por este médico
         * Verifico si ya existe para no duplicarlo
         */
        if (!pacientesAtendidos.contains(paciente)) {
            pacientesAtendidos.add(paciente);
            System.out.println("Paciente añadido a lista de atendidos por este médico");
        }
    }
    
    /**
     * BÚSQUEDA EN VECTORES (ArrayList)
     * Buscar un paciente por su número de historia clínica en la lista de atendidos
     * Recorro toda la lista elemento por elemento hasta encontrar la coincidencia
     * Si no lo encuentra, devuelve null
     * 
     * @param numeroHistoria Número de historia clínica a buscar
     * @return El paciente encontrado, o null si no existe
     */
    public Paciente buscarPacienteAtendido(int numeroHistoria) {
        for (Paciente p : pacientesAtendidos) {
            if (p.getNumeroHistoriaClinica() == numeroHistoria) {
                System.out.println("Paciente encontrado: " + p.getNombreCompleto());
                return p;
            }
        }
        System.out.println("Paciente con HC " + numeroHistoria + " NO encontrado en la lista de atendidos");
        return null;  // Devuelve null si no lo encuentra
    }
    
    /**
     * Mostrar todos los pacientes que ha atendido este médico
     */
    public void listarPacientesAtendidos() {
        System.out.println("\n=== PACIENTES ATENDIDOS POR " + this.getNombreCompleto() + " ===");
        
        if (pacientesAtendidos.isEmpty()) {
            System.out.println("No ha atendido a ningún paciente todavía");
            return;
        }
        
        // Recorro la lista y muestro cada paciente
        for (Paciente p : pacientesAtendidos) {
            System.out.println("- " + p.getNombreCompleto() + " (HC: " + p.getNumeroHistoriaClinica() + ")");
        }
        System.out.println("Total de pacientes atendidos: " + pacientesAtendidos.size());
    }
    
    /**
     * Sobrescribo el método mostrarDatos() de Persona
     * Muestro los datos de persona + los datos específicos de médico
     */
    @Override
    public void mostrarDatos() {
        super.mostrarDatos();  // Primero muestro los datos de Persona
        System.out.println("Número Colegiado: " + numeroColegiado);
        System.out.println("Especialidad: " + especialidad);
        System.out.println("Pacientes atendidos: " + pacientesAtendidos.size());
    }
    
    // Getters
    public int getNumeroColegiado() { return numeroColegiado; }  
    public String getEspecialidad() { return especialidad; }
}
