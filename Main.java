/**
 * Clase principal del Sistema de Gestión Clínica
 * Aquí se ejecuta el programa y se muestran los menús interactivos
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz
 */

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    
    // Atributos Estáticos
    
    private static List<Paciente> listaPacientes = new ArrayList<>();
    private static List<Medico> listaMedicos = new ArrayList<>();
    private static PersonalAdmin admin;
    
    // Método Principal
    
    public static void main(String[] args) {
        
        try (Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("=== SISTEMA DE GESTIÓN CLÍNICA ===\n");
            
            inicializarDatosEjemplo();
            
            int opcion;
            do {
                mostrarMenuPrincipal();
                opcion = leerOpcion(scanner, 1, 8);
                
                switch (opcion) {
                    case 1:
                        menuPacientes(scanner);
                        break;
                    case 2:
                        menuMedicos(scanner);
                        break;
                    case 3:
                        menuAtencionMedica(scanner);
                        break;
                    case 4:
                        menuFacturacion(scanner);
                        break;
                    case 5:
                        menuHistorial(scanner);
                        break;
                    case 6:
                        menuColaPila(scanner);
                        break;
                    case 7:
                        mostrarEstadisticas();
                        break;
                    case 8:
                        System.out.println("\n¡Hasta pronto!");
                        break;
                }
            } while (opcion != 8);
            
        }
    }
    
    // Inicialización de los datos 
    
    private static void inicializarDatosEjemplo() {
        System.out.println("--- CARGANDO DATOS DE EJEMPLO ---");
        
        // Usamos LocalDate.of(año, mes, día) con números fijos
        // Esta es la ÚNICA forma de crear fechas sin usar parse
        
        admin = new PersonalAdmin("12345678A", "Carlos López", 
                LocalDate.of(1980, 5, 15), "M", 1);
        System.out.println("Administrador creado: Carlos López");
        
        Medico medico1 = new Medico("87654321B", "Laura Gómez", 
                LocalDate.of(1975, 8, 20), "F", 1001, "Cardiología");
        
        Medico medico2 = new Medico("11111111H", "Roberto Martínez", 
                LocalDate.of(1982, 3, 10), "M", 1002, "Pediatría");
        
        listaMedicos.add(medico1);
        listaMedicos.add(medico2);
        System.out.println("Médicos creados: Laura Gómez (Cardiología), Roberto Martínez (Pediatría)");
        
        Paciente p1 = new Paciente("11111111C", "Juan Pérez", 
                LocalDate.of(1990, 3, 10), "M", 1,
                "Calle Mayor 1", 600111222, "juan@email.com", 600999888);
        
        Paciente p2 = new Paciente("22222222D", "María García", 
                LocalDate.of(1985, 7, 25), "F", 2,
                "Calle Luna 5", 600333444, "maria@email.com", 600777666);
        
        Paciente p3 = new Paciente("33333333E", "Pedro Sánchez", 
                LocalDate.of(1995, 12, 1), "M", 3,
                "Calle Sol 8", 600555666, "pedro@email.com", 600444555);
        
        listaPacientes.add(p1);
        listaPacientes.add(p2);
        listaPacientes.add(p3);
        System.out.println("Pacientes creados: Juan Pérez, María García, Pedro Sánchez");
        
        p1.getHistorial().agregarEnfermedadPrevia("Asma");
        p1.getHistorial().agregarAlergia("Penicilina");
        p1.getHistorial().agregarMedicacion("Salbutamol");
        
        p2.getHistorial().agregarEnfermedadCronica("Hipertensión");
        p2.getHistorial().agregarMedicacion("Enalapril");
        
        p3.getHistorial().agregarEnfermedadPrevia("Diabetes tipo 2");
        p3.getHistorial().agregarMedicacion("Metformina");
        System.out.println("Antecedentes médicos añadidos");
        
        System.out.println("\nDatos cargados correctamente\n");
    }
    
    // Menú Principal
    
    private static void mostrarMenuPrincipal() {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("        MENÚ PRINCIPAL");
        System.out.println("═══════════════════════════════════");
        System.out.println("1. Gestión de Pacientes");
        System.out.println("2. Gestión de Médicos");
        System.out.println("3. Atención Médica");
        System.out.println("4. Facturación");
        System.out.println("5. Consultar Historial");
        System.out.println("6. Cola de Registro / Historial Acciones");
        System.out.println("7. Estadísticas");
        System.out.println("8. Salir");
        System.out.println("═══════════════════════════════════");
        System.out.print("Seleccione una opción: ");
    }
    
    // Menú Pacientes
    
    private static void menuPacientes(Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE PACIENTES ---");
            System.out.println("1. Registrar nuevo paciente");
            System.out.println("2. Modificar datos de paciente");
            System.out.println("3. Dar de alta médica");
            System.out.println("4. Dar de baja administrativa");
            System.out.println("5. Buscar paciente");
            System.out.println("6. Listar todos los pacientes");
            System.out.println("7. Volver al menú principal");
            System.out.print("Opción: ");
            
            opcion = leerOpcion(scanner, 1, 7);
            
            switch (opcion) {
                case 1:
                    registrarPaciente(scanner);
                    break;
                case 2:
                    modificarPaciente(scanner);
                    break;
                case 3:
                    darAltaPaciente(scanner);
                    break;
                case 4:
                    darBajaPaciente(scanner);
                    break;
                case 5:
                    buscarPaciente(scanner);
                    break;
                case 6:
                    listarPacientes();
                    break;
            }
        } while (opcion != 7);
    }
    
    /**
     * Registrar un nuevo paciente en el sistema
     * Pide año, mes y día por separado para construir la fecha
     */
    private static void registrarPaciente(Scanner scanner) {
        try {
            System.out.println("\n--- REGISTRO DE NUEVO PACIENTE ---");
            
            System.out.print("DNI: ");
            String dni = scanner.nextLine();
            
            System.out.print("Nombre completo: ");
            String nombre = scanner.nextLine();
            
            System.out.println("--- FECHA DE NACIMIENTO ---");
            System.out.print("Año: ");
            int anio = leerEntero(scanner);
            System.out.print("Mes (1-12): ");
            int mes = leerEntero(scanner);
            System.out.print("Día: ");
            int dia = leerEntero(scanner);
            LocalDate fecha = LocalDate.of(anio, mes, dia);
            
            System.out.print("Sexo (M/F): ");
            String sexo = scanner.nextLine().toUpperCase();
            
            System.out.print("Nº Historia Clínica (número entero): ");
            int hc = leerEntero(scanner);
            
            if (buscarPacientePorHC(hc) != null) {
                System.out.println("Ya existe un paciente con ese número de historia");
                return;
            }
            
            System.out.print("Dirección: ");
            String direccion = scanner.nextLine();
            
            System.out.print("Teléfono: ");
            int telefono = leerEntero(scanner);
            
            System.out.print("Email: ");
            String email = scanner.nextLine();
            
            System.out.print("Teléfono emergencia: ");
            int telefonoEmer = leerEntero(scanner);
            
            Paciente nuevo = new Paciente(dni, nombre, fecha, sexo, hc, direccion, telefono, email, telefonoEmer);
            listaPacientes.add(nuevo);
            admin.registrarPaciente(nuevo);
            
            System.out.println("Paciente registrado correctamente");
            
        } catch (Exception e) {
            System.out.println("Error al registrar: " + e.getMessage());
        }
    }
    
    private static void modificarPaciente(Scanner scanner) {
        System.out.print("\nIngrese número de historia clínica: ");
        int hc = leerEntero(scanner);
        Paciente p = buscarPacientePorHC(hc);
        
        if (p == null) {
            System.out.println("Paciente no encontrado");
            return;
        }
        
        System.out.println("\n--- DATOS ACTUALES ---");
        p.mostrarDatos();
        
        System.out.println("\n--- MODIFICAR DATOS (dejar vacío para no cambiar) ---");
        
        System.out.print("Nueva dirección (" + p.getDireccion() + "): ");
        String direccion = scanner.nextLine();
        if (!direccion.isEmpty()) p.setDireccion(direccion);
        
        System.out.print("Nuevo teléfono (" + p.getNumeroTelefono() + "): ");
        String telStr = scanner.nextLine();
        if (!telStr.isEmpty()) p.setNumeroTelefono(Integer.parseInt(telStr));
        
        System.out.print("Nuevo email (" + p.getCorreoElectronico() + "): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) p.setCorreoElectronico(email);
        
        System.out.print("Nuevo teléfono emergencia (" + p.getTelefonoEmergencia() + "): ");
        String telEmerStr = scanner.nextLine();
        if (!telEmerStr.isEmpty()) p.setTelefonoEmergencia(Integer.parseInt(telEmerStr));
        
        admin.modificarDatosPaciente(p, p.getDireccion(), p.getNumeroTelefono(), p.getCorreoElectronico(), p.getTelefonoEmergencia());
        System.out.println("Datos modificados correctamente");
    }
    
    private static void darAltaPaciente(Scanner scanner) {
        System.out.print("\nIngrese número de historia clínica: ");
        int hc = leerEntero(scanner);
        Paciente p = buscarPacientePorHC(hc);
        
        if (p == null) {
            System.out.println("Paciente no encontrado");
            return;
        }
        
        admin.darAltaPaciente(p);
        System.out.println("Paciente dado de ALTA médica");
    }
    
    private static void darBajaPaciente(Scanner scanner) {
        System.out.print("\nIngrese número de historia clínica: ");
        int hc = leerEntero(scanner);
        Paciente p = buscarPacientePorHC(hc);
        
        if (p == null) {
            System.out.println("Paciente no encontrado");
            return;
        }
        
        System.out.print("¿Baja temporal? (S/N): ");
        String resp = scanner.nextLine().toUpperCase();
        boolean temporal = resp.equals("S");
        
        admin.darBajaPaciente(p, temporal);
    }
    
    private static void buscarPaciente(Scanner scanner) {
        System.out.print("\nIngrese número de historia clínica: ");
        int hc = leerEntero(scanner);
        Paciente p = buscarPacientePorHC(hc);
        
        if (p == null) {
            System.out.println("Paciente no encontrado");
        } else {
            p.mostrarDatos();
        }
    }
    
    private static void listarPacientes() {
        if (listaPacientes.isEmpty()) {
            System.out.println("\nNo hay pacientes registrados");
            return;
        }
        System.out.println("\n=== LISTA DE PACIENTES ===");
        for (Paciente p : listaPacientes) {
            System.out.println("HC: " + p.getNumeroHistoriaClinica() + " | " + p.getNombreCompleto() + " | " + p.getEstado());
        }
        System.out.println("Total: " + listaPacientes.size() + " pacientes");
    }
    
    // Menú Médicos
    
    private static void menuMedicos(Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE MÉDICOS ---");
            System.out.println("1. Registrar nuevo médico");
            System.out.println("2. Modificar especialidad de médico");
            System.out.println("3. Listar todos los médicos");
            System.out.println("4. Volver al menú principal");
            System.out.print("Opción: ");
            
            opcion = leerOpcion(scanner, 1, 4);
            
            switch (opcion) {
                case 1:
                    registrarMedico(scanner);
                    break;
                case 2:
                    modificarEspecialidadMedico(scanner);
                    break;
                case 3:
                    listarMedicos();
                    break;
            }
        } while (opcion != 4);
    }
    
    private static void registrarMedico(Scanner scanner) {
        try {
            System.out.println("\n--- REGISTRO DE NUEVO MÉDICO ---");
            
            System.out.print("DNI: ");
            String dni = scanner.nextLine();
            
            System.out.print("Nombre completo: ");
            String nombre = scanner.nextLine();
            
            System.out.println("--- FECHA DE NACIMIENTO ---");
            System.out.print("Año: ");
            int anio = leerEntero(scanner);
            System.out.print("Mes (1-12): ");
            int mes = leerEntero(scanner);
            System.out.print("Día: ");
            int dia = leerEntero(scanner);
            LocalDate fecha = LocalDate.of(anio, mes, dia);
            
            System.out.print("Sexo (M/F): ");
            String sexo = scanner.nextLine().toUpperCase();
            
            System.out.print("Nº Colegiado (número entero): ");
            int col = leerEntero(scanner);
            
            System.out.print("Especialidad: ");
            String especialidad = scanner.nextLine();
            
            Medico nuevo = new Medico(dni, nombre, fecha, sexo, col, especialidad);
            listaMedicos.add(nuevo);
            
            System.out.println("Médico registrado correctamente");
            
        } catch (Exception e) {
            System.out.println("Error al registrar: " + e.getMessage());
        }
    }
    
    private static void modificarEspecialidadMedico(Scanner scanner) {
        System.out.print("\nIngrese número de colegiado: ");
        int col = leerEntero(scanner);
        Medico m = buscarMedicoPorId(col);
        
        if (m == null) {
            System.out.println("Médico no encontrado");
            return;
        }
        
        System.out.print("Nueva especialidad (" + m.getEspecialidad() + "): ");
        String esp = scanner.nextLine();
        if (!esp.isEmpty()) {
            m.setEspecialidad(esp);
            System.out.println("Especialidad actualizada");
        }
    }
    
    private static void listarMedicos() {
        if (listaMedicos.isEmpty()) {
            System.out.println("\nNo hay médicos registrados");
            return;
        }
        System.out.println("\n=== LISTA DE MÉDICOS ===");
        for (Medico m : listaMedicos) {
            System.out.println("Nº Col: " + m.getNumeroColegiado() + " | " + m.getNombreCompleto() + " | " + m.getEspecialidad());
        }
        System.out.println("Total: " + listaMedicos.size() + " médicos");
    }
    
    // Mwnú Atención Médica
    
    private static void menuAtencionMedica(Scanner scanner) {
        System.out.println("\n--- ATENCIÓN MÉDICA ---");
        
        if (listaMedicos.isEmpty()) {
            System.out.println("No hay médicos registrados. Registre un médico primero.");
            return;
        }
        
        System.out.print("Seleccione número de colegiado del médico: ");
        int idMedico = leerEntero(scanner);
        Medico medico = buscarMedicoPorId(idMedico);
        
        if (medico == null) {
            System.out.println("Médico no encontrado");
            return;
        }
        
        System.out.print("Ingrese número de historia clínica del paciente: ");
        int hc = leerEntero(scanner);
        Paciente paciente = buscarPacientePorHC(hc);
        
        if (paciente == null) {
            System.out.println("Paciente no encontrado");
            return;
        }
        
        if (!paciente.isActivo() || "BAJA_TEMPORAL".equals(paciente.getEstado()) || "BAJA_PERMANENTE".equals(paciente.getEstado())) {
            System.out.println("El paciente no está activo o está de baja");
            return;
        }
        
        int opcion;
        do {
            System.out.println("\n--- ATENCIÓN PARA " + paciente.getNombreCompleto() + " ---");
            System.out.println("Médico: " + medico.getNombreCompleto() + " (" + medico.getEspecialidad() + ")");
            System.out.println("1. Consultar historial");
            System.out.println("2. Añadir diagnóstico");
            System.out.println("3. Añadir tratamiento");
            System.out.println("4. Volver");
            System.out.print("Opción: ");
            
            opcion = leerOpcion(scanner, 1, 4);
            
            switch (opcion) {
                case 1:
                    medico.consultarHistorial(paciente);
                    break;
                case 2:
                    System.out.print("Ingrese diagnóstico: ");
                    String diag = scanner.nextLine();
                    medico.añadirDiagnostico(paciente, diag);
                    break;
                case 3:
                    System.out.print("Ingrese tratamiento: ");
                    String trata = scanner.nextLine();
                    medico.añadirTratamiento(paciente, trata);
                    break;
            }
        } while (opcion != 4);
    }
    
    // Menú Facturación
    
    private static void menuFacturacion(Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n--- FACTURACIÓN ---");
            System.out.println("1. Ver todas las facturas");
            System.out.println("2. Buscar facturas por paciente");
            System.out.println("3. Generar factura de prueba");
            System.out.println("4. Volver al menú principal");
            System.out.print("Opción: ");
            
            opcion = leerOpcion(scanner, 1, 4);
            
            switch (opcion) {
                case 1:
                    AreaFacturacion.listarTodasFacturas();
                    break;
                case 2:
                    System.out.print("Ingrese número de historia clínica: ");
                    int hc = leerEntero(scanner);
                    List<AreaFacturacion> facturas = AreaFacturacion.buscarFacturasPorPaciente(hc);
                    if (facturas.isEmpty()) {
                        System.out.println("No hay facturas para este paciente");
                    } else {
                        System.out.println("\n=== FACTURAS DEL PACIENTE ===");
                        for (AreaFacturacion f : facturas) {
                            System.out.println("  " + f);
                        }
                    }
                    break;
                case 3:
                    System.out.print("Ingrese número de historia clínica del paciente: ");
                    int hcFact = leerEntero(scanner);
                    Paciente p = buscarPacientePorHC(hcFact);
                    if (p == null) {
                        System.out.println("Paciente no encontrado");
                    } else {
                        System.out.print("Ingrese concepto: ");
                        String concepto = scanner.nextLine();
                        System.out.print("Ingrese importe: ");
                        double importe = Double.parseDouble(scanner.nextLine());
                        AreaFacturacion.notificarProcedimiento(p, concepto, importe);
                    }
                    break;
            }
        } while (opcion != 4);
    }
    
    // Menú Historial
    
    private static void menuHistorial(Scanner scanner) {
        System.out.print("\nIngrese número de historia clínica: ");
        int hc = leerEntero(scanner);
        Paciente p = buscarPacientePorHC(hc);
        
        if (p == null) {
            System.out.println("Paciente no encontrado");
        } else {
            System.out.println("\n=== HISTORIAL CLÍNICO DE " + p.getNombreCompleto() + " ===");
            p.getHistorial().mostrarHistorial();
        }
    }
    
    // Menú, Cola y Pila
    
    private static void menuColaPila(Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n--- COLA DE REGISTRO / HISTORIAL DE ACCIONES ---");
            System.out.println("1. Ver pacientes en cola de registro");
            System.out.println("2. Atender siguiente paciente (sacar de la cola)");
            System.out.println("3. Ver historial de acciones (pila LIFO)");
            System.out.println("4. Volver al menú principal");
            System.out.print("Opción: ");
            
            opcion = leerOpcion(scanner, 1, 4);
            
            switch (opcion) {
                case 1:
                    System.out.println("Pacientes en cola de espera: " + PersonalAdmin.getTamanoCola());
                    break;
                case 2:
                    Paciente p = admin.atenderSiguientePaciente();
                    if (p != null) {
                        System.out.println("Atendiendo a: " + p.getNombreCompleto());
                    }
                    break;
                case 3:
                    PersonalAdmin.mostrarHistorialAcciones();
                    break;
            }
        } while (opcion != 4);
    }
    
    // Estadísticas
    
    private static void mostrarEstadisticas() {
        System.out.println("\n=== ESTADÍSTICAS DEL SISTEMA ===");
        System.out.println("=== DATOS GENERALES ===");
        System.out.println("Total pacientes registrados: " + listaPacientes.size());
        System.out.println("Total médicos registrados: " + listaMedicos.size());
        
        System.out.println("\n=== PACIENTES POR ESTADO ===");
        
        int activos = 0;
        for (Paciente p : listaPacientes) {
            if (p.isActivo()) {
                activos++;
            }
        }
        System.out.println("  - Pacientes activos: " + activos);
        
        int altas = 0;
        for (Paciente p : listaPacientes) {
            if ("ALTA_MEDICA".equals(p.getEstado())) {
                altas++;
            }
        }
        System.out.println("  - Pacientes de alta médica: " + altas);
        
        int bajasTemp = 0;
        for (Paciente p : listaPacientes) {
            if ("BAJA_TEMPORAL".equals(p.getEstado())) {
                bajasTemp++;
            }
        }
        System.out.println("  - Bajas temporales: " + bajasTemp);
        
        int bajasPerm = 0;
        for (Paciente p : listaPacientes) {
            if ("BAJA_PERMANENTE".equals(p.getEstado())) {
                bajasPerm++;
            }
        }
        System.out.println("  - Bajas permanentes: " + bajasPerm);
        
        System.out.println("\n=== FACTURACIÓN ===");
        System.out.println("Facturación total del sistema: " + AreaFacturacion.getFacturacionTotal() + " €");
        
        System.out.println("\n=== ESTRUCTURAS DE DATOS ===");
        System.out.println("Pacientes en cola de registro (FIFO): " + PersonalAdmin.getTamanoCola());
        
        System.out.println("\n=== HISTORIAL DE ACCIONES (PILA LIFO) ===");
        PersonalAdmin.mostrarHistorialAcciones();
    }
    
    // Métodos de Búsqueda
    
    private static Paciente buscarPacientePorHC(int hc) {
        for (Paciente p : listaPacientes) {
            if (p.getNumeroHistoriaClinica() == hc) {
                return p;
            }
        }
        return null;
    }
    
    private static Medico buscarMedicoPorId(int id) {
        for (Medico m : listaMedicos) {
            if (m.getNumeroColegiado() == id) {
                return m;
            }
        }
        return null;
    }
    
    // Métodos de Validación
    
    private static int leerOpcion(Scanner scanner, int min, int max) {
        int opcion = leerEntero(scanner);
        while (opcion < min || opcion > max) {
            System.out.print("Opción inválida. Intente de nuevo (" + min + "-" + max + "): ");
            opcion = leerEntero(scanner);
        }
        return opcion;
    }
    
    private static int leerEntero(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Debe ingresar un número entero. Intente de nuevo: ");
            }
        }
    }
}