/**
 * Clase principal del sistema de Gestión Clínica al Paciente
 * 
 * Este es el punto de entrada del sistema. Desde aquí se controla:
 *   - La autenticación de usuarios (login)
 *   - El menú principal según el rol del usuario
 *   - La carga y guardado de datos mediante ficheros (persistencia)
 * 
 * Flujo general:
 *   1. Cargar datos desde ficheros
 *   2. Mostrar login
 *   3. Verificar usuario y redirigir a su menú
 *   4. Guardar datos al salir
 * 
 * Roles del sistema (según diagrama de casos de uso):
 *   - Médico (verificado)
 *   - PersonalAdministrativo (verificado)
 *   - PersonalFacturación (verificado)
 * 
 * @author: Laura Leciñena, Alejandro Díaz
 * @since 2026/04/16
 */

import excepciones.EstadoPacienteException;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import modelos.*;


public class Main {

    // ============================================================================================
    //                              DATOS DEL SISTEMA (LISTAS GLOBALES)
    // ============================================================================================

    /*
        Guardamos todos los datos en ArrayLists estáticos para que sean accesibles
        desde cualquier método sin necesidad de pasarlos como parámetro.
        
        Se cargan desde fichero al iniciar y se guardan al cerrar.
    */

    private static List<Paciente>       pacientes      = new ArrayList<>();
    private static List<Medico>         medicos        = new ArrayList<>();
    private static List<PersonalAdmin>  admins         = new ArrayList<>();
    private static List<AreaFacturacion>    facturacion = new ArrayList<>();

    // ============================================================================================
    //                              RUTAS DE LOS FICHEROS
    // ============================================================================================

    /*
        Los ficheros .dat son binarios serializados (ObjectOutputStream / ObjectInputStream)
        Se guardarám en la carpeta "datos/" dentro del proyecto, dentro de cada archivo se guardara la información 
        correspondiente de los usuarios creados ya dentro del sistema.

        Es static porque se utiliza dentro del propio main y final porque será la dirección en donde permaneceran los datos 
    */

    private static final String RUTA_PACIENTES = "datos/pacientes.dat";
    private static final String RUTA_MEDICOS   = "datos/medicos.dat";
    private static final String RUTA_ADMINS    = "datos/admins.dat";
    private static final String RUTA_FACTURACION = "datos/facturacion.dat";


    // ============================================================================================
    //                                    CREDENCIALES DE ACCESO
    // ============================================================================================

    /*
        Para este subsistema, los usuarios y contraseñas se guardan en un HashMap estático.
        La clave es el usuario y el valor es un array de dos posiciones:
            
            [0] = contraseña
            
            [1] = rol (MEDICO / ADMIN / FACTURACION)
            Todo esto dentro del Array que será la informacion que acompaña la clave 
        
        En un sistema real esto estaría en base de datos pero aún no lo hemos visto en clases.
    */

    private static final Map<String, String[]> usuariosSistema = new HashMap<>();

    // Scanner global para toda la clase entera, incluyendo metodos fuera del main
    private static Scanner scanner = new Scanner(System.in);


    // ============================================================================================
    //                                          MAIN
    // ============================================================================================

    public static void main(String[] args) {

        // Creamos la carpeta de datos si no existe al iniciar el programa

        new File("datos").mkdirs();

        // Inicializamos los usuarios del sistema 
        inicializarUsuarios();

        // Cargamos todos los datos persistidos de fichero
        cargarDatos();

        // Mostramos la bienvenida
        mostrarBienvenida();

        // Bucle de sesión: permite múltiples logins sin cerrar el programa
        boolean ejecutando = true;

        while (ejecutando) {

            // Pedimos las credenciales al usuario
            String[] sesion = iniciarSesion();

            // null significa que el usuario eligió salir del login
            if (sesion == null) {
                ejecutando = false;
                continue;
            }

            String usuario = sesion[0];
            String rol     = sesion[1];

            // Redirigimos al menú correspondiente según el rol
            switch (rol) {
                case "MEDICO":
                    menuMedico(usuario);
                    break;
                case "ADMIN":
                    menuAdmin(usuario);
                    break;
                case "FACTURACION":
                    menuFacturacion(usuario);
                    break;
                default:
                    System.out.println("Rol desconocido. Contacte con el administrador del sistema.");
            }
        }

        // Guardamos todos los datos antes de cerrar
        guardarDatos();

        System.out.println("\nSesión cerrada. Datos guardados correctamente.");
        System.out.println("Hasta pronto.");
        scanner.close();
    }


    // ============================================================================================
    //                              INICIALIZACIÓN DE USUARIOS
    // ============================================================================================

    /**
     * Carga los usuarios que hemos colocado por defecto en nuestro sistema cada vez que inicia la 
     * ejecución del sistema
     * 
     * Formato: usuariosSistema.put("usuario", new String[]{"contraseña", "ROL"})
     * 
     * Roles posibles: MEDICO, ADMIN, FACTURACION
     */

    private static void inicializarUsuarios() {

        // Médicos del sistema
        usuariosSistema.put("dr.garcia",    new String[]{"garcia1234",  "MEDICO"});
        usuariosSistema.put("dr.martinez",  new String[]{"martinez1234", "MEDICO"});

        // Personal administrativo
        usuariosSistema.put("admin.lopez",  new String[]{"lopez1234",   "ADMIN"});
        usuariosSistema.put("admin.perez",  new String[]{"perez1234",   "ADMIN"});

        // Personal de facturación
        usuariosSistema.put("fact.ruiz",    new String[]{"ruiz1234",    "FACTURACION"});
        usuariosSistema.put("fact.torres",  new String[]{"torres1234",  "FACTURACION"});
    }


    // ============================================================================================
    //                                  PANTALLA DE BIENVENIDA
    // ============================================================================================

    /**
     * Muestra el banner de bienvenida del sistema al iniciar.
     */
    private static void mostrarBienvenida() {
        System.out.println("=============================================================");
        System.out.println("         SISTEMA DE GESTIÓN CLÍNICA AL PACIENTE             ");
        System.out.println("                    I.E.S Pablo Serrano                     ");
        System.out.println("=============================================================");
        System.out.println("  Autores: Julia Amoros, Laura Leciñena, Alejandro Díaz     ");
        System.out.println("=============================================================\n");
    }


    // ============================================================================================
    //                                  INICIO DE SESIÓN (LOGIN)
    // ============================================================================================

    /**
     * Gestiona el proceso de inicio de sesión del sistema.
     * 
     * El usuario tiene un máximo de 3 intentos antes de bloquearse.
     * Si el usuario no quiere iniciar sesión, puede escribir "0" para salir.
     * 
     * @return String[] con [usuario, rol] si el login fue exitoso, null si quiere salir
     */
    private static String[] iniciarSesion() {

        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.println("(Escriba '0' en usuario para salir del sistema)\n");

        int intentos = 0;
        int maxIntentos = 3;

        while (intentos < maxIntentos) {

            System.out.print("Usuario: ");
            String usuario = scanner.nextLine().trim();

            // El usuario quiere salir
            if (usuario.equals("0")) {
                return null;
            }

            System.out.print("Contraseña: ");
            String contrasena = scanner.nextLine().trim();

            // Verificamos si el usuario existe en el sistema
            if (usuariosSistema.containsKey(usuario)) {

                String[] datos = usuariosSistema.get(usuario);
                String contrasenaCorrecta = datos[0];
                String rol = datos[1];

                // Comprobamos la contraseña
                if (contrasena.equals(contrasenaCorrecta)) {
                    System.out.println("\n[OK] Acceso concedido. Bienvenido/a, " + usuario + " (" + rol + ")");
                    return new String[]{usuario, rol};
                }
            }

            // Si llegamos aquí, las credenciales eran incorrectas
            intentos++;
            int restantes = maxIntentos - intentos;

            if (restantes > 0) {
                System.out.println("[ERROR] Usuario o contraseña incorrectos. Intentos restantes: " + restantes);
            } else {
                System.out.println("[ERROR] Número máximo de intentos alcanzado. Acceso bloqueado temporalmente.");
            }
        }

        // Esperamos un momento antes de volver a mostrar el login
        System.out.println("\nEsperando 3 segundos antes de permitir un nuevo acceso...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Llamada recursiva: volvemos a mostrar el login
        return iniciarSesion();
    }


    // ============================================================================================
    //                                  MENÚ DEL MÉDICO
    // ============================================================================================

    /**
     * Menú principal para los usuarios con rol MÉDICO.
     * 
     * Funcionalidades según el diagrama de casos de uso:
     *   - Acceso y modificación al historial digital del paciente
     *   - Añadir diagnósticos y tratamientos
     *   - Cambio de médico o unidad (extiende a añadir diagnósticos)
     *   - Ver notificaciones de procedimientos de pago
     *   - Sugerir historial automáticamente
     * 
     * @param usuario Nombre de usuario del médico logueado
     */
    private static void menuMedico(String usuario) {

        // Buscamos el médico correspondiente al usuario logueado
        // En este sistema el nombre de usuario contiene "dr." seguido del apellido
        Medico medicoActual = buscarMedicoPorUsuario(usuario);

        boolean enMenu = true;

        while (enMenu) {

            System.out.println("\n=============================================================");
            System.out.println("  MENÚ MÉDICO | " + usuario);
            System.out.println("=============================================================");
            System.out.println("  [1] Buscar paciente y ver su historial");
            System.out.println("  [2] Añadir diagnóstico a un paciente");
            System.out.println("  [3] Añadir tratamiento a un paciente");
            System.out.println("  [4] Añadir antecedentes médicos a un paciente");
            System.out.println("  [5] Sugerir historial automáticamente (pacientes recurrentes)");
            System.out.println("  [6] Ver notificaciones de facturación pendientes");
            System.out.println("  [0] Cerrar sesión");
            System.out.println("=============================================================");
            System.out.print("  Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {

                case "1":
                    verHistorialPaciente(medicoActual);
                    break;

                case "2":
                    añadirDiagnostico(medicoActual);
                    break;

                case "3":
                    añadirTratamiento(medicoActual);
                    break;

                case "4":
                    añadirAntecedentesMedicos();
                    break;

                case "5":
                    sugerirHistorialAutomatico();
                    break;

                case "6":
                    verNotificacionesFacturacion();
                    break;

                case "0":
                    System.out.println("\nCerrando sesión de " + usuario + "...");
                    enMenu = false;
                    break;

                default:
                    System.out.println("[AVISO] Opción no válida. Por favor, elija entre las opciones del menú.");
            }
        }
    }


    // ============================================================================================
    //                              MENÚ DEL PERSONAL ADMINISTRATIVO
    // ============================================================================================

    /**
     * Menú principal para usuarios con rol ADMIN.
     * 
     * Funcionalidades según el diagrama de casos de uso:
     *   - Registrar paciente (gestiona admisión/acceso del paciente)
     *   - Modificar datos personales de paciente
     *   - Dar de alta médica a un paciente
     *   - Dar de baja permanente a un paciente
     *   - Suspender registro temporal (baja temporal)
     *   - Asignación de Nº de historia clínica (se hace automáticamente al registrar)
     * 
     * @param usuario Nombre de usuario del administrativo logueado
     */
    private static void menuAdmin(String usuario) {

        // Buscamos el objeto PersonalAdmin correspondiente
        PersonalAdmin adminActual = buscarAdminPorUsuario(usuario);

        boolean enMenu = true;

        while (enMenu) {

            System.out.println("\n=============================================================");
            System.out.println("  MENÚ ADMINISTRATIVO | " + usuario);
            System.out.println("=============================================================");
            System.out.println("  [1] Registrar nuevo paciente");
            System.out.println("  [2] Modificar datos personales de un paciente");
            System.out.println("  [3] Dar alta médica a un paciente");
            System.out.println("  [4] Dar baja permanente a un paciente");
            System.out.println("  [5] Suspender registro temporal (baja temporal)");
            System.out.println("  [6] Listar todos los pacientes del sistema");
            System.out.println("  [7] Buscar paciente por DNI");
            System.out.println("  [0] Cerrar sesión");
            System.out.println("=============================================================");
            System.out.print("  Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {

                case "1":
                    registrarPaciente(adminActual);
                    break;

                case "2":
                    modificarDatosPaciente(adminActual);
                    break;

                case "3":
                    darAltaPaciente(adminActual);
                    break;

                case "4":
                    darBajaPermanentePaciente(adminActual);
                    break;

                case "5":
                    darBajaTemporal(adminActual);
                    break;

                case "6":
                    listarTodosPacientes();
                    break;

                case "7":
                    buscarPacientePorDni();
                    break;

                case "0":
                    System.out.println("\nCerrando sesión de " + usuario + "...");
                    enMenu = false;
                    break;

                default:
                    System.out.println("[AVISO] Opción no válida. Por favor, elija entre las opciones del menú.");
            }
        }
    }


    // ============================================================================================
    //                              MENÚ DE FACTURACIÓN
    // ============================================================================================

    /**
     * Menú principal para usuarios con rol FACTURACIÓN.
     * 
     * Funcionalidades:
     *   - Generar factura para un paciente
     *   - Ver todas las facturas del sistema
     *   - Ver facturas de un paciente concreto
     *   - Notificar procedimiento de pago
     * 
     * @param usuario Nombre de usuario del personal de facturación
     */
    private static void menuFacturacion(String usuario) {

        boolean enMenu = true;

        while (enMenu) {

            System.out.println("\n=============================================================");
            System.out.println("  MENÚ FACTURACIÓN | " + usuario);
            System.out.println("=============================================================");
            System.out.println("  [1] Generar factura para un paciente");
            System.out.println("  [2] Ver todas las facturas del sistema");
            System.out.println("  [3] Ver facturas de un paciente concreto");
            System.out.println("  [4] Notificar procedimiento de pago");
            System.out.println("  [0] Cerrar sesión");
            System.out.println("=============================================================");
            System.out.print("  Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {

                case "1":
                    generarFactura();
                    break;

                case "2":
                    AreaFacturacion.listarTodasFacturas();
                    break;

                case "3":
                    mostrarFacturasPaciente();
                    break;

                case "4":
                    notificarProcedimiento();
                    break;

                case "0":
                    System.out.println("\nCerrando sesión de " + usuario + "...");
                    enMenu = false;
                    break;

                default:
                    System.out.println("[AVISO] Opción no válida. Por favor, elija entre las opciones del menú.");
            }
        }
    }


    // ============================================================================================
    //                          FUNCIONALIDADES DEL MÉDICO
    // ============================================================================================

    /**
     * Permite al médico buscar un paciente por DNI y ver su historial completo.
     * Usa el método consultarHistorial() de la clase Medico.
     * 
     * @param medico El médico que realiza la consulta
     */
    private static void verHistorialPaciente(Medico medico) {

        System.out.println("\n--- CONSULTA DE HISTORIAL ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        // Si el médico es null (no está registrado como objeto), mostramos el historial directamente
        if (medico != null) {
            medico.consultarHistorial(paciente);
        } else {
            System.out.println("\n=== HISTORIAL CLÍNICO ===");
            System.out.println("Paciente: " + paciente.getNombreCompleto());
            paciente.getHistorial().mostrarHistorial();
        }
    }

    /**
     * Permite al médico añadir un diagnóstico al historial de un paciente.
     * Usa el método añadirDiagnostico() de la clase Medico.
     * 
     * @param medico El médico que añade el diagnóstico
     */
    private static void añadirDiagnostico(Medico medico) {

        System.out.println("\n--- AÑADIR DIAGNÓSTICO ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        System.out.print("Introduce el diagnóstico: ");
        String diagnostico = scanner.nextLine().trim();

        if (diagnostico.isEmpty()) {
            System.out.println("[ERROR] El diagnóstico no puede estar vacío.");
            return;
        }

        try {
            if (medico != null) {
                medico.añadirDiagnostico(paciente, diagnostico);
            } else {
                // Si el médico no está como objeto, lo añadimos directamente al historial
                paciente.getHistorial().agregarDiagnostico(diagnostico);
                System.out.println("Diagnóstico añadido correctamente.");
            }

            // Guardamos después de cada modificación para no perder datos
            guardarDatos();

        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Permite al médico añadir un tratamiento al historial de un paciente.
     * Usa el método añadirTratamiento() de la clase Medico.
     * 
     * @param medico El médico que añade el tratamiento
     */
    private static void añadirTratamiento(Medico medico) {

        System.out.println("\n--- AÑADIR TRATAMIENTO ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        System.out.print("Introduce el tratamiento: ");
        String tratamiento = scanner.nextLine().trim();

        if (tratamiento.isEmpty()) {
            System.out.println("[ERROR] El tratamiento no puede estar vacío.");
            return;
        }

        try {
            if (medico != null) {
                medico.añadirTratamiento(paciente, tratamiento);
            } else {
                paciente.getHistorial().agregarTratamiento(tratamiento);
                System.out.println("Tratamiento añadido correctamente.");
            }

            guardarDatos();

        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Permite añadir antecedentes médicos al historial de un paciente.
     * Esto incluye: enfermedades previas, alergias, intervenciones, enf. crónicas y medicación.
     */
    private static void añadirAntecedentesMedicos() {

        System.out.println("\n--- REGISTRAR ANTECEDENTES MÉDICOS ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        boolean enSubmenu = true;

        while (enSubmenu) {

            System.out.println("\nSeleccione qué tipo de antecedente desea añadir:");
            System.out.println("  [1] Enfermedad previa");
            System.out.println("  [2] Alergia");
            System.out.println("  [3] Intervención quirúrgica");
            System.out.println("  [4] Enfermedad crónica");
            System.out.println("  [5] Medicación habitual");
            System.out.println("  [0] Volver al menú anterior");
            System.out.print("  Opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    System.out.print("Enfermedad previa: ");
                    String enfPrevia = scanner.nextLine().trim();
                    if (!enfPrevia.isEmpty()) {
                        paciente.getHistorial().agregarEnfermedadPrevia(enfPrevia);
                        System.out.println("Enfermedad previa añadida.");
                        guardarDatos();
                    }
                    break;

                case "2":
                    System.out.print("Alergia: ");
                    String alergia = scanner.nextLine().trim();
                    if (!alergia.isEmpty()) {
                        paciente.getHistorial().agregarAlergia(alergia);
                        System.out.println("Alergia añadida.");
                        guardarDatos();
                    }
                    break;

                case "3":
                    System.out.print("Intervención quirúrgica: ");
                    String intervencion = scanner.nextLine().trim();
                    if (!intervencion.isEmpty()) {
                        paciente.getHistorial().agregarIntervencion(intervencion);
                        System.out.println("Intervención añadida.");
                        guardarDatos();
                    }
                    break;

                case "4":
                    System.out.print("Enfermedad crónica: ");
                    String enfCronica = scanner.nextLine().trim();
                    if (!enfCronica.isEmpty()) {
                        paciente.getHistorial().agregarEnfermedadCronica(enfCronica);
                        System.out.println("Enfermedad crónica añadida.");
                        guardarDatos();
                    }
                    break;

                case "5":
                    System.out.print("Medicación habitual: ");
                    String medicacion = scanner.nextLine().trim();
                    if (!medicacion.isEmpty()) {
                        paciente.getHistorial().agregarMedicacion(medicacion);
                        System.out.println("Medicación añadida.");
                        guardarDatos();
                    }
                    break;

                case "0":
                    enSubmenu = false;
                    break;

                default:
                    System.out.println("[AVISO] Opción no válida.");
            }
        }
    }

    /**
     * Sugiere automáticamente el historial de un paciente recurrente.
     * Busca pacientes con estado ACTIVO y muestra su historial de forma resumida.
     * Simula la funcionalidad de "sugerir historial automáticamente" del diagrama.
     */
    private static void sugerirHistorialAutomatico() {

        System.out.println("\n--- SUGERENCIA AUTOMÁTICA DE HISTORIAL (PACIENTES RECURRENTES) ---");

        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados en el sistema.");
            return;
        }

        System.out.print("Introduce el DNI del paciente: ");
        String dni = scanner.nextLine().trim().toUpperCase();

        Paciente encontrado = null;

        for (Paciente p : pacientes) {
            if (p.getDni().equals(dni)) {
                encontrado = p;
                break;
            }
        }

        if (encontrado == null) {
            System.out.println("[AVISO] No se encontró ningún paciente con ese DNI.");
            return;
        }

        System.out.println("\n[SISTEMA] Cargando historial automáticamente para: " + encontrado.getNombreCompleto());
        System.out.println("[SISTEMA] Estado actual del paciente: " + encontrado.getEstado());
        System.out.println("[SISTEMA] Historia Clínica Nº: " + encontrado.getNumeroHistoriaClinica());
        System.out.println();

        encontrado.getHistorial().mostrarHistorial();
    }

    /**
     * Muestra las notificaciones de facturación pendientes.
     * En este sistema, mostramos la lista de facturas generadas como notificaciones.
     */
    private static void verNotificacionesFacturacion() {
        System.out.println("\n--- NOTIFICACIONES DE PROCEDIMIENTOS DE PAGO ---");
        AreaFacturacion.listarTodasFacturas();
    }


    // ============================================================================================
    //                          FUNCIONALIDADES DEL PERSONAL ADMINISTRATIVO
    // ============================================================================================

    /**
     * Registra un nuevo paciente en el sistema.
     * 
     * Se solicitan todos los datos necesarios: DNI, nombre, fecha de nacimiento, sexo,
     * dirección, teléfono, correo y teléfono de emergencia.
     * 
     * El número de historia clínica se asigna automáticamente.
     * 
     * @param admin El personal administrativo que realiza el registro
     */
    private static void registrarPaciente(PersonalAdmin admin) {

        System.out.println("\n--- REGISTRAR NUEVO PACIENTE ---");

        try {
            System.out.print("DNI: ");
            String dni = scanner.nextLine().trim();

            // Comprobamos si el DNI ya existe en el sistema
            for (Paciente p : pacientes) {
                if (p.getDni().equalsIgnoreCase(dni)) {
                    System.out.println("[AVISO] Ya existe un paciente con ese DNI en el sistema.");
                    return;
                }
            }

            System.out.print("Nombre completo: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Fecha de nacimiento (DD/MM/YYYY): ");
            LocalDate fechaNac = leerFecha();
            if (fechaNac == null) return;

            System.out.print("Sexo (M/F): ");
            String sexo = scanner.nextLine().trim();

            System.out.print("Dirección: ");
            String direccion = scanner.nextLine().trim();

            System.out.print("Número de teléfono (9 dígitos): ");
            int telefono = leerEntero();
            if (telefono == -1) return;

            System.out.print("Correo electrónico: ");
            String correo = scanner.nextLine().trim();

            System.out.print("Teléfono de emergencia (9 dígitos): ");
            int telefonoEmergencia = leerEntero();
            if (telefonoEmergencia == -1) return;

            // Creamos el paciente con todos los datos
            Paciente nuevoPaciente = new Paciente(
                dni, nombre, fechaNac, sexo,
                direccion, telefono, correo, telefonoEmergencia
            );

            // Asignamos el número de historia clínica manualmente usando reflexión
            // ya que en Paciente el contador es de instancia (++ dentro del constructor)
            // Lo hacemos así para mantener la coherencia con el diseño original de la clase

            // Añadimos el paciente a la lista del sistema
            pacientes.add(nuevoPaciente);

            // Llamamos al método de registro del admin
            if (admin != null) {
                admin.registrarPaciente(nuevoPaciente);
            }

            guardarDatos();

            System.out.println("[OK] Paciente registrado correctamente.");
            System.out.println("     Historia Clínica Nº: " + nuevoPaciente.getNumeroHistoriaClinica());

        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("[ERROR] Error al registrar el paciente: " + e.getMessage());
        }
    }

    /**
     * Modifica los datos personales de un paciente (nombre y sexo).
     * 
     * Según el enunciado, solo el personal administrativo puede realizar
     * ajustes en los datos personales para mantener la precisión de la información.
     * 
     * @param admin El personal administrativo que realiza la modificación
     */
    private static void modificarDatosPaciente(PersonalAdmin admin) {

        System.out.println("\n--- MODIFICAR DATOS PERSONALES DE PACIENTE ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        System.out.println("\nDatos actuales:");
        System.out.println("  Nombre: " + paciente.getNombreCompleto());
        System.out.println("  Sexo: " + paciente.getSexo());

        System.out.println("\nIntroduzca los nuevos datos (deje en blanco para no modificar):");

        System.out.print("Nuevo nombre completo [" + paciente.getNombreCompleto() + "]: ");
        String nuevoNombre = scanner.nextLine().trim();

        System.out.print("Nuevo sexo (M/F) [" + paciente.getSexo() + "]: ");
        String nuevoSexo = scanner.nextLine().trim();

        // Si se dejó en blanco, mantenemos el valor actual
        if (nuevoNombre.isEmpty()) nuevoNombre = paciente.getNombreCompleto();
        if (nuevoSexo.isEmpty())  nuevoSexo  = paciente.getSexo();

        try {
            if (admin != null) {
                admin.modificarDatosPersonalesPaciente(paciente, nuevoNombre, nuevoSexo);
            } else {
                paciente.setNombreCompleto(nuevoNombre);
                paciente.setSexo(nuevoSexo);
                System.out.println("Datos del paciente actualizados correctamente.");
            }

            guardarDatos();

        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Da el alta médica a un paciente, cambiando su estado a ALTA_MEDICA.
     * 
     * Según el enunciado: "cuando el paciente termina su tratamiento y se le da el alta,
     * se actualiza su historial clínico, cerrando el registro de atención."
     * 
     * @param admin El personal administrativo que gestiona el alta
     */
    private static void darAltaPaciente(PersonalAdmin admin) {

        System.out.println("\n--- DAR ALTA MÉDICA ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        if ("ALTA_MEDICA".equals(paciente.getEstado())) {
            System.out.println("[AVISO] El paciente ya tiene el estado ALTA_MEDICA.");
            return;
        }

        System.out.println("¿Está seguro de dar el alta a " + paciente.getNombreCompleto() + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (!confirmacion.equals("S")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            if (admin != null) {
                admin.darAltaPaciente(paciente);
            } else {
                paciente.setEstado("ALTA_MEDICA");
                System.out.println("Paciente dado de ALTA médica.");
            }

            guardarDatos();

        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Da de baja permanente a un paciente.
     * 
     * Según el enunciado: "si el paciente decide no continuar con el tratamiento o
     * ha sido dado de baja por motivos administrativos, se realiza la baja del paciente."
     * 
     * @param admin El personal administrativo que gestiona la baja
     */
    private static void darBajaPermanentePaciente(PersonalAdmin admin) {

        System.out.println("\n--- DAR BAJA PERMANENTE ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        System.out.println("[ATENCIÓN] Esta acción es PERMANENTE e irreversible.");
        System.out.println("¿Confirma la baja permanente de " + paciente.getNombreCompleto() + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (!confirmacion.equals("S")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            if (admin != null) {
                admin.darBajaPermanentePaciente(paciente);
            } else {
                paciente.setEstado("BAJA_PERMANENTE");
                paciente.setActivo(false);
                System.out.println("Baja PERMANENTE aplicada.");
            }

            guardarDatos();

        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Da de baja temporal a un paciente (suspensión del registro).
     * 
     * Según el enunciado: "Esta baja puede incluir la eliminación o la suspensión
     * temporal del registro del paciente, dependiendo de las políticas del hospital."
     * 
     * @param admin El personal administrativo que gestiona la suspensión
     */
    private static void darBajaTemporal(PersonalAdmin admin) {

        System.out.println("\n--- SUSPENDER REGISTRO TEMPORAL (BAJA TEMPORAL) ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        System.out.println("¿Confirma la baja temporal de " + paciente.getNombreCompleto() + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (!confirmacion.equals("S")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            if (admin != null) {
                admin.darBajatemporalPaciente(paciente);
            } else {
                paciente.setEstado("BAJA_TEMPORAL");
                System.out.println("Baja TEMPORAL aplicada.");
            }

            guardarDatos();

        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Lista todos los pacientes registrados en el sistema con sus datos básicos.
     */
    private static void listarTodosPacientes() {

        System.out.println("\n--- LISTADO DE PACIENTES ---");

        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados en el sistema.");
            return;
        }

        System.out.println("Total de pacientes: " + pacientes.size());
        System.out.println("--------------------------------------------------------------");

        for (Paciente p : pacientes) {
            System.out.println("HC Nº: " + p.getNumeroHistoriaClinica()
                + " | DNI: " + p.getDni()
                + " | Nombre: " + p.getNombreCompleto()
                + " | Estado: " + p.getEstado());
        }

        System.out.println("--------------------------------------------------------------");
    }

    /**
     * Busca un paciente por DNI y muestra todos sus datos.
     */
    private static void buscarPacientePorDni() {

        System.out.println("\n--- BUSCAR PACIENTE POR DNI ---");
        System.out.print("DNI del paciente: ");
        String dni = scanner.nextLine().trim().toUpperCase();

        for (Paciente p : pacientes) {
            if (p.getDni().equals(dni)) {
                System.out.println("\nPaciente encontrado:");
                p.mostrarDatos();
                return;
            }
        }

        System.out.println("[AVISO] No se encontró ningún paciente con DNI: " + dni);
    }


    // ============================================================================================
    //                          FUNCIONALIDADES DE FACTURACIÓN
    // ============================================================================================

    /**
     * Genera una nueva factura para un paciente.
     * 
     * Valida que el paciente no esté de baja temporal o permanente
     * (lanza EstadoPacienteException si lo está).
     */
    private static void generarFactura() {

        System.out.println("\n--- GENERAR FACTURA ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        System.out.print("Concepto de la factura: ");
        String concepto = scanner.nextLine().trim();

        if (concepto.isEmpty()) {
            System.out.println("[ERROR] El concepto no puede estar vacío.");
            return;
        }

        System.out.print("Importe (€): ");
        String importeStr = scanner.nextLine().trim();

        double importe;
        try {
            importe = Double.parseDouble(importeStr.replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] El importe introducido no es un número válido.");
            return;
        }

        try {
            AreaFacturacion factura = new AreaFacturacion(concepto, importe, LocalDate.now());
            factura.generarFactura(paciente);
            System.out.println("[OK] Factura generada y registrada correctamente.");

        } catch (EstadoPacienteException e) {
            System.out.println("[ERROR] " + e.getMessage());

        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Muestra todas las facturas asociadas a un paciente concreto.
     */
    private static void mostrarFacturasPaciente() {

        System.out.println("\n--- FACTURAS DE UN PACIENTE ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        AreaFacturacion.mostrarFacturasDePaciente(paciente);
    }

    /**
     * Notifica un procedimiento de pago a un paciente.
     * Usa el método estático notificarProcedimiento() de AreaFacturacion.
     */
    private static void notificarProcedimiento() {

        System.out.println("\n--- NOTIFICAR PROCEDIMIENTO DE PAGO ---");
        Paciente paciente = buscarPacienteInteractivo();

        if (paciente == null) return;

        System.out.print("Concepto del procedimiento: ");
        String concepto = scanner.nextLine().trim();

        System.out.print("Importe (€): ");
        String importeStr = scanner.nextLine().trim();

        double importe;
        try {
            importe = Double.parseDouble(importeStr.replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] El importe introducido no es un número válido.");
            return;
        }

        // notificarProcedimiento ya gestiona internamente la excepción EstadoPacienteException
        AreaFacturacion.notificarProcedimiento(paciente, concepto, importe);
    }


    // ============================================================================================
    //                          MÉTODOS AUXILIARES / UTILIDADES
    // ============================================================================================

    /**
     * Método auxiliar que solicita un DNI al usuario y busca el paciente correspondiente
     * en la lista. Muy usado en todos los menús para evitar repetir código.
     * 
     * @return El paciente encontrado, o null si no existe o se canceló
     */
    private static Paciente buscarPacienteInteractivo() {

        System.out.print("DNI del paciente (o '0' para cancelar): ");
        String dni = scanner.nextLine().trim().toUpperCase();

        if (dni.equals("0")) {
            System.out.println("Operación cancelada.");
            return null;
        }

        for (Paciente p : pacientes) {
            if (p.getDni().equals(dni)) {
                System.out.println("[OK] Paciente encontrado: " + p.getNombreCompleto() + " (HC Nº " + p.getNumeroHistoriaClinica() + ")");
                return p;
            }
        }

        System.out.println("[AVISO] No se encontró ningún paciente con DNI: " + dni);
        return null;
    }

    /**
     * Busca el objeto Medico en la lista de médicos a partir del nombre de usuario.
     * Si no encuentra correspondencia exacta, devuelve null (el menú seguirá funcionando).
     * 
     * @param usuario Nombre de usuario del médico logueado
     * @return El objeto Medico encontrado, o null si no hay coincidencia
     */
    private static Medico buscarMedicoPorUsuario(String usuario) {

        /* 
            La correspondencia la hacemos por el apellido que aparece en el usuario.
            Ejemplo: "dr.garcia" -> buscamos un médico cuyo nombre contenga "garcia" (ignorando mayúsculas)
        */
        String apellido = usuario.replace("dr.", "").replace("dra.", "").trim();

        for (Medico m : medicos) {
            if (m.getNombreCompleto().toLowerCase().contains(apellido.toLowerCase())) {
                return m;
            }
        }

        // Si no encontramos coincidencia, devolvemos null
        // El menú seguirá funcionando pero sin el objeto Medico concreto
        return null;
    }

    /**
     * Busca el objeto PersonalAdmin en la lista de admins a partir del nombre de usuario.
     * 
     * @param usuario Nombre de usuario del admin logueado
     * @return El objeto PersonalAdmin encontrado, o null si no hay coincidencia
     */
    private static PersonalAdmin buscarAdminPorUsuario(String usuario) {

        String apellido = usuario.replace("admin.", "").trim();

        for (PersonalAdmin a : admins) {
            if (a.getNombreCompleto().toLowerCase().contains(apellido.toLowerCase())) {
                return a;
            }
        }

        return null;
    }

    /**
     * Lee una fecha por consola en formato DD/MM/YYYY y la convierte a LocalDate.
     * 
     * @return LocalDate válido, o null si el formato era incorrecto
     */
    private static LocalDate leerFecha() {
        String entrada = scanner.nextLine().trim();

        try {
            String[] partes = entrada.split("/");

            if (partes.length != 3) {
                System.out.println("[ERROR] Formato de fecha incorrecto. Use DD/MM/YYYY.");
                return null;
            }

            int dia  = Integer.parseInt(partes[0]);
            int mes  = Integer.parseInt(partes[1]);
            int anio = Integer.parseInt(partes[2]);

            return LocalDate.of(anio, mes, dia);

        } catch (Exception e) {
            System.out.println("[ERROR] Fecha no válida: " + entrada);
            return null;
        }
    }

    /**
     * Lee un número entero por consola.
     * Devuelve -1 si el usuario introduce algo que no es un número.
     * 
     * @return El número entero leído, o -1 si hubo error
     */
    private static int leerEntero() {
        String entrada = scanner.nextLine().trim();
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] El valor introducido no es un número entero válido.");
            return -1;
        }
    }


    // ============================================================================================
    //                              PERSISTENCIA DE DATOS (FICHEROS)
    // ============================================================================================

    /**
     * Guarda todas las listas del sistema en sus ficheros correspondientes.
     * 
     * Usamos serialización de objetos (ObjectOutputStream) porque:
     *   - Los objetos ya implementan Serializable
     *   - Permite guardar el estado completo incluyendo el HistorialMedico (composición)
     *   - Es el método más directo dado el diseño del proyecto
     * 
     * Se guardan tres ficheros:
     *   - pacientes.dat  → Lista<Paciente>
     *   - medicos.dat    → Lista<Medico>
     *   - admins.dat     → Lista<PersonalAdmin>
     *   - contador.dat   → int con el último número de HC asignado
     */
    @SuppressWarnings("unchecked")
    private static void guardarDatos() {

        guardarLista(pacientes, RUTA_PACIENTES);
        guardarLista(medicos,   RUTA_MEDICOS);
        guardarLista(admins,    RUTA_ADMINS);
        guardarContador();
    }

    /**
     * Guarda una lista serializable en un fichero .dat.
     * 
     * @param lista La lista a guardar
     * @param ruta  Ruta del fichero destino
     */
    private static <T extends Serializable> void guardarLista(List<T> lista, String ruta) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            System.out.println("[AVISO] No se pudo guardar en " + ruta + ": " + e.getMessage());
        }
    }


    /**
     * Carga todos los datos desde los ficheros al arrancar el sistema.
     * 
     * Si los ficheros no existen (primera ejecución), inicializa los datos de ejemplo
     * para que el sistema no arranque completamente vacío.
     */
    
    private static void cargarDatos() {

        // Intentamos cargar cada fichero
        List<Paciente>      pacientesCargados = cargarLista(RUTA_PACIENTES);
        List<Medico>        medicosCargados   = cargarLista(RUTA_MEDICOS);
        List<PersonalAdmin> adminsCargados    = cargarLista(RUTA_ADMINS);

        if (pacientesCargados != null) pacientes = pacientesCargados;
        if (medicosCargados   != null) medicos   = medicosCargados;
        if (adminsCargados    != null) admins    = adminsCargados;

        // Cargamos el contador de HC
        cargarContador();

        // Si es la primera ejecución (ficheros vacíos), cargamos datos de ejemplo
        if (pacientes.isEmpty() && medicos.isEmpty() && admins.isEmpty()) {
            System.out.println("[SISTEMA] Primera ejecución detectada. Cargando datos de ejemplo...");
            inicializarDatosEjemplo();
            guardarDatos();
        } else {
            System.out.println("[SISTEMA] Datos cargados correctamente desde ficheros.");
            System.out.println("          Pacientes: " + pacientes.size()
                + " | Médicos: " + medicos.size()
                + " | Admins: "  + admins.size());
        }
    }

    /**
     * Carga una lista serializable desde un fichero .dat.
     * 
     * @param ruta Ruta del fichero a leer
     * @return La lista deserializada, o null si no existía el fichero
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> cargarLista(String ruta) {
        File fichero = new File(ruta);

        if (!fichero.exists()) {
            return null;    // Primera ejecución: no existe el fichero todavía
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[AVISO] Error al leer " + ruta + ": " + e.getMessage());
            return null;
        }
    }


    /**
     * Inicializa datos de ejemplo para la primera ejecución del sistema.
     * 
     * Crea algunos médicos, admins y pacientes de prueba para que el sistema
     * no arranque completamente vacío y se pueda probar de inmediato.
     */
    private static void inicializarDatosEjemplo() {

        try {
            // Médicos de ejemplo
            Medico medico1 = new Medico(
                "12345678A", "Carlos García López",
                LocalDate.of(1980, 3, 15), "M",
                123456789, "Cardiología"
            );
            medicos.add(medico1);

            Medico medico2 = new Medico(
                "87654321B", "Ana Martínez Ruiz",
                LocalDate.of(1985, 7, 22), "F",
                987654321, "Pediatría"
            );
            medicos.add(medico2);

            // Personal administrativo de ejemplo
            PersonalAdmin admin1 = new PersonalAdmin(
                "11223344C", "María López Torres",
                LocalDate.of(1990, 1, 10), "F",
                100
            );
            admins.add(admin1);

            // Pacientes de ejemplo
            Paciente paciente1 = new Paciente(
                "99887766D", "Pedro Sánchez Gómez",
                LocalDate.of(1975, 6, 8), "M",
                "Calle Mayor 10, Zaragoza", 612345678,
                "pedro.sanchez@email.com", 698765432
            );
            // Añadimos algunos datos al historial para que tenga contenido
            paciente1.getHistorial().agregarEnfermedadPrevia("Gastritis");
            paciente1.getHistorial().agregarAlergia("Penicilina");
            pacientes.add(paciente1);

            Paciente paciente2 = new Paciente(
                "55443322E", "Laura Fernández Díaz",
                LocalDate.of(1992, 11, 25), "F",
                "Av. Goya 45, Zaragoza", 623456789,
                "laura.fernandez@email.com", 677654321
            );
            pacientes.add(paciente2);

            System.out.println("[SISTEMA] Datos de ejemplo cargados correctamente:");
            System.out.println("          Médicos: " + medicos.size() + " | Admins: " + admins.size() + " | Pacientes: " + pacientes.size());

        } catch (Exception e) {
            System.out.println("[AVISO] Error al inicializar datos de ejemplo: " + e.getMessage());
        }
    }
}