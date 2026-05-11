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
import java.time.format.DateTimeFormatter;
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

    private static List<Paciente>      pacientes  = new ArrayList<>();
    private static List<Medico>        medicos    = new ArrayList<>();
    private static List<PersonalAdmin> admins     = new ArrayList<>();

    // ============================================================================================
    //                              RUTAS DE LOS FICHEROS
    // ============================================================================================

    /*
        Los ficheros .dat son binarios serializados (ObjectOutputStream / ObjectInputStream).
        Se guardan en la carpeta "datos/" dentro del proyecto.

        Son static porque se usan dentro de métodos estáticos, y final porque
        la ruta no cambia en ningún momento de la ejecución.
    */

    private static final String RUTA_PACIENTES = "datos/pacientes.dat";
    private static final String RUTA_MEDICOS   = "datos/medicos.dat";
    private static final String RUTA_ADMINS    = "datos/admins.dat";


    // ============================================================================================
    //                                    CREDENCIALES DE ACCESO
    // ============================================================================================

    /*
        Los usuarios y contraseñas se guardan en un HashMap estático.
        La clave es el nombre de usuario y el valor es un array con:

            [0] = contraseña
            [1] = rol (MEDICO / ADMIN / FACTURACION)

        En un sistema real esto estaría en base de datos, pero aún no lo hemos visto en clase.
    */

    private static final Map<String, String[]> usuariosSistema = new HashMap<>();

    // Scanner global para toda la clase, incluyendo métodos fuera del main
    private static Scanner teclado = new Scanner(System.in);


    // ============================================================================================
    //                                          MAIN
    // ============================================================================================

    public static void main(String[] args) {

        // Creamos la carpeta de datos si no existe al iniciar el programa
        new File("datos").mkdirs();

        // Inicializamos los usuarios del sistema
        inicializarUsuarios();

        // Cargamos todos los datos persistidos desde fichero
        cargarDatos();

        // Mostramos la bienvenida
        mostrarBienvenida();

        // Bucle de sesión: permite múltiples logins sin cerrar el programa
        boolean ejecutando = true;

        while (ejecutando) {

            String[] sesion = iniciarSesion();

            // null significa que el usuario eligió salir
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
                    System.out.println("Rol desconocido. Contacte con el administrador.");
            }
        }

        // Guardamos todos los datos antes de cerrar
        guardarDatos();

        System.out.println("\nSesión cerrada. Datos guardados correctamente.");
        System.out.println("Hasta pronto.");
        teclado.close();
    }


    // ============================================================================================
    //                              INICIALIZACIÓN DE USUARIOS
    // ============================================================================================

    /**
     * Carga los usuarios predefinidos del sistema en cada ejecución.
     *
     * Formato: usuariosSistema.put("usuario", new String[]{"contraseña", "ROL"})
     *
     * Roles posibles: MEDICO, ADMIN, FACTURACION
     */
    private static void inicializarUsuarios() {

        // Médicos
        usuariosSistema.put("dr.garcia",   new String[]{"garcia1234",   "MEDICO"});
        usuariosSistema.put("dr.martinez", new String[]{"martinez1234", "MEDICO"});

        // Personal administrativo
        usuariosSistema.put("admin.lopez", new String[]{"lopez1234", "ADMIN"});
        usuariosSistema.put("admin.perez", new String[]{"perez1234", "ADMIN"});

        // Personal de facturación
        usuariosSistema.put("fact.ruiz",   new String[]{"ruiz1234",   "FACTURACION"});
        usuariosSistema.put("fact.torres", new String[]{"torres1234", "FACTURACION"});
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
        System.out.println("  Autores: Laura Leciñena, Alejandro Díaz     ");
        System.out.println("=============================================================\n");
    }


    // ============================================================================================
    //                                  INICIO DE SESIÓN (LOGIN)
    // ============================================================================================

    /**
     * Gestiona el proceso de inicio de sesión del sistema.
     *
     * El usuario tiene un máximo de 3 intentos antes de bloquearse temporalmente.
     * Escribir "0" en el campo usuario cierra el programa.
     *
     * @return String[] con [usuario, rol] si el login fue exitoso, null si quiere salir
     */
    private static String[] iniciarSesion() {

        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.println("(Escriba '0' en usuario para salir del sistema)\n");

            System.out.print("Usuario: ");
            String usuario = teclado.nextLine().trim();

            if (usuario.equals("0")) return null;

            System.out.print("Contraseña: ");
            String contrasena = teclado.nextLine().trim();

            if (usuariosSistema.containsKey(usuario)) {
                String[] datos = usuariosSistema.get(usuario);

                if (contrasena.equals(datos[0])) {
                    System.out.println("\n[OK] Acceso concedido. Bienvenido/a, " + usuario + " (" + datos[1] + ")");
                    return new String[]{usuario, datos[1]};
                }
            }

        return iniciarSesion();
    }


    // ============================================================================================
    //                                  MENÚ DEL MÉDICO
    // ============================================================================================

    /**
     * Menú principal para usuarios con rol MÉDICO.
     *
     * Funcionalidades según el diagrama de casos de uso:
     *   - Consultar historial del paciente
     *   - Añadir diagnósticos y tratamientos
     *   - Dar alta médica a un paciente
     *   - Notificar procedimiento de pago al área de facturación
     *
     * @param usuario Nombre de usuario del médico logueado
     */
    private static void menuMedico(String usuario) {

        Medico medicoActual = buscarMedicoPorUsuario(usuario);
        boolean enMenu = true;

        while (enMenu) {

            System.out.println("\n=============================================================");
            System.out.println("  MENÚ MÉDICO | " + usuario);
            System.out.println("=============================================================");
            System.out.println("  [1] Buscar paciente y ver su historial");
            System.out.println("  [2] Añadir diagnóstico a un paciente");
            System.out.println("  [3] Añadir tratamiento a un paciente");
            System.out.println("  [4] Dar alta médica a un paciente");
            System.out.println("  [5] Notificar procedimiento de pago a facturación");
            System.out.println("  [0] Cerrar sesión");
            System.out.println("=============================================================");
            System.out.print("  Seleccione una opción: ");

            switch (teclado.nextLine().trim()) {
                case "1": verHistorialPaciente(medicoActual); break;
                case "2": añadirAlHistorial(medicoActual, "DIAGNOSTICO"); break;
                case "3": añadirAlHistorial(medicoActual, "TRATAMIENTO"); break;
                case "4": cambiarEstadoPaciente(null, "ALTA_MEDICA"); break;
                case "5": notificarProcedimiento(); break;
                case "0":
                    System.out.println("\nCerrando sesión de " + usuario + "...");
                    enMenu = false;
                    break;
                default:
                    System.out.println("[AVISO] Opción no válida.");
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
     *   - Registrar nuevo paciente (gestiona admisión, incluye antecedentes)
     *   - Modificar datos personales
     *   - Dar de baja permanente o temporal
     *   - Listar y buscar pacientes
     *
     * Nota: el alta médica la concede únicamente el médico, no el personal administrativo.
     *
     * @param usuario Nombre de usuario del administrativo logueado
     */
    private static void menuAdmin(String usuario) {

        PersonalAdmin adminActual = buscarAdminPorUsuario(usuario);
        boolean enMenu = true;

        while (enMenu) {

            System.out.println("\n=============================================================");
            System.out.println("  MENÚ ADMINISTRATIVO | " + usuario);
            System.out.println("=============================================================");
            System.out.println("  [1] Registrar nuevo paciente");
            System.out.println("  [2] Modificar datos personales de un paciente");
            System.out.println("  [3] Registrar antecedentes médicos de un paciente");
            System.out.println("  [4] Dar baja permanente a un paciente");
            System.out.println("  [5] Suspender registro temporal (baja temporal)");
            System.out.println("  [6] Listar todos los pacientes");
            System.out.println("  [7] Buscar paciente por DNI");
            System.out.println("  [0] Cerrar sesión");
            System.out.println("=============================================================");
            System.out.print("  Seleccione una opción: ");

            switch (teclado.nextLine().trim()) {
                case "1": registrarPaciente(adminActual);                          break;
                case "2": modificarDatosPaciente(adminActual);                     break;
                case "3": añadirAntecedentesMedicos();                             break;
                case "4": cambiarEstadoPaciente(adminActual, "BAJA_PERMANENTE");   break;
                case "5": cambiarEstadoPaciente(adminActual, "BAJA_TEMPORAL");     break;
                case "6": listarTodosPacientes();                                  break;
                case "7": buscarPacientePorDni();                                  break;
                case "0":
                    System.out.println("\nCerrando sesión de " + usuario + "...");
                    enMenu = false;
                    break;
                default:
                    System.out.println("[AVISO] Opción no válida.");
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
     *   - Ver notificaciones de pago enviadas por los médicos
     *
     * Nota: las notificaciones de pago las envía el médico, aquí solo se consultan.
     *
     * @param usuario Nombre de usuario del personal de facturación logueado
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
            System.out.println("  [4] Ver notificaciones de pago pendientes");
            System.out.println("  [0] Cerrar sesión");
            System.out.println("=============================================================");
            System.out.print("  Seleccione una opción: ");

            switch (teclado.nextLine().trim()) {
                case "1": generarFactura();                              break;
                case "2": AreaFacturacion.listarTodasFacturas();         break;
                case "3": mostrarFacturasPaciente();                     break;
                case "4": verNotificacionesFacturacion();                break;
                case "0":
                    System.out.println("\nCerrando sesión de " + usuario + "...");
                    enMenu = false;
                    break;
                default:
                    System.out.println("[AVISO] Opción no válida.");
            }
        }
    }


    // ============================================================================================
    //                          FUNCIONALIDADES DEL MÉDICO
    // ============================================================================================

    /**
     * Permite al médico buscar un paciente por DNI y consultar su historial completo.
     * Si el médico existe como objeto usa su método consultarHistorial(), si no,
     * muestra el historial directamente desde el paciente.
     *
     * @param medico El médico que realiza la consulta (puede ser null)
     */
    private static void verHistorialPaciente(Medico medico) {

        System.out.println("\n--- CONSULTA DE HISTORIAL ---");
        Paciente paciente = buscarPacienteInteractivo();
        if (paciente == null) return;

        if (medico != null) {
            medico.consultarHistorial(paciente);
        } else {
            System.out.println("\n=== HISTORIAL CLÍNICO ===");
            System.out.println("Paciente: " + paciente.getNombreCompleto());
            paciente.getHistorial().mostrarHistorial();
        }
    }

    /**
     * Añade un diagnóstico o un tratamiento al historial de un paciente.
     * Ambas operaciones son idénticas en estructura, por eso usamos un único método
     * con el tipo como parámetro para no repetir código.
     *
     * @param medico El médico que realiza la acción (puede ser null)
     * @param tipo   "DIAGNOSTICO" o "TRATAMIENTO"
     */
    private static void añadirAlHistorial(Medico medico, String tipo) {

        boolean esDiagnostico = tipo.equals("DIAGNOSTICO");

        System.out.println("\n--- AÑADIR " + (esDiagnostico ? "DIAGNÓSTICO" : "TRATAMIENTO") + " ---");
        Paciente paciente = buscarPacienteInteractivo();
        if (paciente == null) return;

        System.out.print("Introduce el " + (esDiagnostico ? "diagnóstico" : "tratamiento") + ": ");
        String texto = teclado.nextLine().trim();

        if (texto.isEmpty()) {
            System.out.println("[ERROR] El campo no puede estar vacío.");
            return;
        }

        try {
            if (medico != null) {
                if (esDiagnostico) medico.añadirDiagnostico(paciente, texto);
                else               medico.añadirTratamiento(paciente, texto);
            } else {
                if (esDiagnostico) paciente.getHistorial().agregarDiagnostico(texto);
                else               paciente.getHistorial().agregarTratamiento(texto);
                System.out.println((esDiagnostico ? "Diagnóstico" : "Tratamiento") + " añadido correctamente.");
            }
            guardarDatos();

        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Permite añadir antecedentes médicos al historial de un paciente.
     * Incluye: enfermedades previas, alergias, intervenciones, enf. crónicas y medicación.
     */
    private static void añadirAntecedentesMedicos() {

        System.out.println("\n--- REGISTRAR ANTECEDENTES MÉDICOS ---");
        Paciente paciente = buscarPacienteInteractivo();
        if (paciente == null) return;

        boolean enSubmenu = true;

        while (enSubmenu) {

            System.out.println("\n  [1] Enfermedad previa");
            System.out.println("  [2] Alergia");
            System.out.println("  [3] Intervención quirúrgica");
            System.out.println("  [4] Enfermedad crónica");
            System.out.println("  [5] Medicación habitual");
            System.out.println("  [0] Volver");
            System.out.print("  Opción: ");

            String opcion = teclado.nextLine().trim();

            if (opcion.equals("0")) { enSubmenu = false; continue; }

            // Mapeamos la opción a la etiqueta y al método correspondiente
            String etiqueta;
            switch (opcion) {
                case "1": etiqueta = "Enfermedad previa";       break;
                case "2": etiqueta = "Alergia";                 break;
                case "3": etiqueta = "Intervención quirúrgica"; break;
                case "4": etiqueta = "Enfermedad crónica";      break;
                case "5": etiqueta = "Medicación habitual";     break;
                default:
                    System.out.println("[AVISO] Opción no válida.");
                    continue;
            }

            System.out.print(etiqueta + ": ");
            String valor = teclado.nextLine().trim();

            if (valor.isEmpty()) continue;

            switch (opcion) {
                case "1": paciente.getHistorial().agregarEnfermedadPrevia(valor);  break;
                case "2": paciente.getHistorial().agregarAlergia(valor);            break;
                case "3": paciente.getHistorial().agregarIntervencion(valor);       break;
                case "4": paciente.getHistorial().agregarEnfermedadCronica(valor);  break;
                case "5": paciente.getHistorial().agregarMedicacion(valor);         break;
            }

            System.out.println("[OK] " + etiqueta + " añadida.");
            guardarDatos();
        }
    }

    /**
     * Muestra las notificaciones de procedimientos de pago enviadas por los médicos.
     * El personal de facturación las consulta desde aquí para saber qué facturas
     * hay pendientes de generar.
     */
    private static void verNotificacionesFacturacion() {
        System.out.println("\n--- NOTIFICACIONES DE PROCEDIMIENTOS DE PAGO ---");
        AreaFacturacion.listarTodasFacturas();
    }


    // ============================================================================================
    //                          FUNCIONALIDADES DEL PERSONAL ADMINISTRATIVO
    // ============================================================================================

    /**
     * Registra un nuevo paciente en el sistema solicitando todos sus datos.
     * El número de historia clínica se asigna automáticamente al crear el objeto.
     * Comprueba previamente que el DNI no esté ya registrado.
     *
     * @param admin El personal administrativo que realiza el registro
     */
    private static void registrarPaciente(PersonalAdmin admin) {

        System.out.println("\n--- REGISTRAR NUEVO PACIENTE ---");

        try {
            System.out.print("DNI: ");
            String dni = teclado.nextLine().trim();

            for (Paciente p : pacientes) {
                if (p.getDni().equalsIgnoreCase(dni)) {
                    System.out.println("[AVISO] Ya existe un paciente con ese DNI.");
                    return;
                }
            }

            System.out.print("Nombre completo: ");
            String nombre = teclado.nextLine().trim();

            System.out.print("Fecha de nacimiento (DD/MM/YYYY): ");
            LocalDate fechaNac = leerFecha();
            if (fechaNac == null) return;

            System.out.print("Sexo (M/F): ");
            String sexo = teclado.nextLine().trim();

            System.out.print("Dirección: ");
            String direccion = teclado.nextLine().trim();

            System.out.print("Teléfono (9 dígitos): ");
            int telefono = leerEntero();
            if (telefono == -1) return;

            System.out.print("Correo electrónico: ");
            String correo = teclado.nextLine().trim();

            System.out.print("Teléfono de emergencia (9 dígitos): ");
            int telefonoEmergencia = leerEntero();
            if (telefonoEmergencia == -1) return;

            System.out.print("Antecedentes médicos (o 'Ninguno'): ");
            String antecedentes = teclado.nextLine().trim();

            Paciente nuevoPaciente = new Paciente(
                dni, nombre, fechaNac, sexo,
                direccion, telefono, correo, telefonoEmergencia, antecedentes
            );

            pacientes.add(nuevoPaciente);

            if (admin != null) admin.registrarPaciente(nuevoPaciente);

            guardarDatos();

            System.out.println("[OK] Paciente registrado. Historia Clínica Nº: " + nuevoPaciente.getNumeroHistoriaClinica());

        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Modifica el nombre completo y el sexo de un paciente ya registrado.
     * Si se deja un campo en blanco, se mantiene el valor actual.
     *
     * @param admin El personal administrativo que realiza la modificación
     */
    private static void modificarDatosPaciente(PersonalAdmin admin) {

        System.out.println("\n--- MODIFICAR DATOS PERSONALES ---");
        Paciente paciente = buscarPacienteInteractivo();
        if (paciente == null) return;

        System.out.println("\nDatos actuales → Nombre: " + paciente.getNombreCompleto() + " | Sexo: " + paciente.getSexo());
        System.out.println("(Deje en blanco para no modificar)");

        System.out.print("Nuevo nombre [" + paciente.getNombreCompleto() + "]: ");
        String nuevoNombre = teclado.nextLine().trim();

        System.out.print("Nuevo sexo (M/F) [" + paciente.getSexo() + "]: ");
        String nuevoSexo = teclado.nextLine().trim();

        if (nuevoNombre.isEmpty()) nuevoNombre = paciente.getNombreCompleto();
        if (nuevoSexo.isEmpty())   nuevoSexo   = paciente.getSexo();

        try {
            if (admin != null) {
                admin.modificarDatosPersonalesPaciente(paciente, nuevoNombre, nuevoSexo);
            } else {
                paciente.setNombreCompleto(nuevoNombre);
                paciente.setSexo(nuevoSexo);
                System.out.println("[OK] Datos actualizados.");
            }
            guardarDatos();

        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Cambia el estado de un paciente a ALTA_MEDICA, BAJA_PERMANENTE o BAJA_TEMPORAL.
     * Pide confirmación antes de aplicar cualquier cambio.
     * En baja permanente también desactiva el flag perteneceSistema.
     *
     * @param admin  El personal administrativo que gestiona el cambio
     * @param estado El nuevo estado a aplicar
     */
    private static void cambiarEstadoPaciente(PersonalAdmin admin, String estado) {

        // Etiquetas legibles para mostrar al usuario
        String etiqueta;
        switch (estado) {
            case "ALTA_MEDICA":     etiqueta = "ALTA MÉDICA";       break;
            case "BAJA_PERMANENTE": etiqueta = "BAJA PERMANENTE";   break;
            default:                etiqueta = "BAJA TEMPORAL";     break;
        }

        System.out.println("\n--- DAR " + etiqueta + " ---");
        Paciente paciente = buscarPacienteInteractivo();
        if (paciente == null) return;

        if (estado.equals(paciente.getEstado())) {
            System.out.println("[AVISO] El paciente ya tiene el estado " + estado + ".");
            return;
        }

        if (estado.equals("BAJA_PERMANENTE")) {
            System.out.println("[ATENCIÓN] Esta acción es PERMANENTE e irreversible.");
        }

        System.out.print("¿Confirma " + etiqueta + " para " + paciente.getNombreCompleto() + "? (S/N): ");
        if (!teclado.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            if (admin != null) {
                switch (estado) {
                    case "ALTA_MEDICA":     admin.darAltaPaciente(paciente);           break;
                    case "BAJA_PERMANENTE": admin.darBajaPermanentePaciente(paciente); break;
                    default:                admin.darBajatemporalPaciente(paciente);   break;
                }
            } else {
                paciente.setEstado(estado);
                if (estado.equals("BAJA_PERMANENTE")) paciente.setPerteneceSistema(false);
                System.out.println("[OK] Estado cambiado a " + estado + ".");
            }
            guardarDatos();

        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Lista todos los pacientes registrados con sus datos básicos.
     */
    private static void listarTodosPacientes() {

        System.out.println("\n--- LISTADO DE PACIENTES ---");

        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes registrados en el sistema.");
            return;
        }

        System.out.println("Total: " + pacientes.size());
        System.out.println("--------------------------------------------------------------");

        for (Paciente p : pacientes) {
            System.out.println("HC Nº: " + p.getNumeroHistoriaClinica()
                + " | DNI: "    + p.getDni()
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
        System.out.print("DNI: ");
        String dni = teclado.nextLine().trim().toUpperCase();

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
     * Lanza EstadoPacienteException si el paciente está de baja temporal o permanente.
     */
    private static void generarFactura() {

        System.out.println("\n--- GENERAR FACTURA ---");
        Paciente paciente = buscarPacienteInteractivo();
        if (paciente == null) return;

        System.out.print("Concepto: ");
        String concepto = teclado.nextLine().trim();

        if (concepto.isEmpty()) {
            System.out.println("[ERROR] El concepto no puede estar vacío.");
            return;
        }

        double importe = leerImporte();
        if (importe == -1) return;

        try {
            AreaFacturacion factura = new AreaFacturacion(concepto, importe, LocalDate.now());
            factura.generarFactura(paciente);
            System.out.println("[OK] Factura generada correctamente.");

        } catch (EstadoPacienteException | IllegalArgumentException | NullPointerException e) {
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
     * Permite al médico notificar un procedimiento de pago al área de facturación.
     *
     * Primero se pregunta si el diagnóstico o tratamiento realizado requiere cobro.
     * Si el médico confirma que sí, se introduce el concepto e importe y se envía
     * la notificación. Si dice que no, no se genera ninguna notificación.
     */
    private static void notificarProcedimiento() {

        System.out.println("\n--- NOTIFICAR PROCEDIMIENTO DE PAGO ---");
        Paciente paciente = buscarPacienteInteractivo();
        if (paciente == null) return;

        // El médico decide si la atención prestada requiere cobro o no
        System.out.print("¿La atención prestada requiere procedimiento de pago? (S/N): ");
        if (!teclado.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("[INFO] No se enviará ninguna notificación a facturación.");
            return;
        }

        System.out.print("Concepto: ");
        String concepto = teclado.nextLine().trim();

        if (concepto.isEmpty()) {
            System.out.println("[ERROR] El concepto no puede estar vacío.");
            return;
        }

        double importe = leerImporte();
        if (importe == -1) return;

        // notificarProcedimiento gestiona internamente la EstadoPacienteException
        AreaFacturacion.notificarProcedimiento(paciente, concepto, importe);
    }


    // ============================================================================================
    //                          MÉTODOS AUXILIARES / UTILIDADES
    // ============================================================================================

    /**
     * Solicita un DNI al usuario y devuelve el paciente correspondiente.
     * Escribir "0" cancela la operación.
     *
     * @return El paciente encontrado, o null si no existe o se canceló
     */
    private static Paciente buscarPacienteInteractivo() {

        System.out.print("DNI del paciente (o '0' para cancelar): ");
        String dni = teclado.nextLine().trim().toUpperCase();

        if (dni.equals("0")) {
            System.out.println("Operación cancelada.");
            return null;
        }

        for (Paciente p : pacientes) {
            if (p.getDni().equals(dni)) {
                System.out.println("[OK] Paciente: " + p.getNombreCompleto() + " (HC Nº " + p.getNumeroHistoriaClinica() + ")");
                return p;
            }
        }

        System.out.println("[AVISO] No se encontró ningún paciente con DNI: " + dni);
        return null;
    }

    /**
     * Busca el objeto Medico en la lista a partir del nombre de usuario.
     * La correspondencia se hace por apellido (ej: "dr.garcia" → busca "garcia").
     *
     * @param usuario Nombre de usuario del médico logueado
     * @return El objeto Medico encontrado, o null si no hay coincidencia
     */
    private static Medico buscarMedicoPorUsuario(String usuario) {

        String apellido = usuario.replace("dr.", "").replace("dra.", "").trim();

        for (Medico m : medicos) {
            if (m.getNombreCompleto().toLowerCase().contains(apellido.toLowerCase())) {
                return m;
            }
        }
        return null;
    }

    /**
     * Busca el objeto PersonalAdmin en la lista a partir del nombre de usuario.
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

        String entrada = teclado.nextLine().trim();

        try {
            return LocalDate.parse(entrada, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            System.out.println("[ERROR] Fecha no válida. Use el formato DD/MM/YYYY.");
            return null;
        }
    }

    /**
     * Lee un número entero por consola.
     * Devuelve -1 si la entrada no es un número válido.
     *
     * @return El entero leído, o -1 si hubo error
     */
    private static int leerEntero() {

        String entrada = teclado.nextLine().trim();
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] El valor introducido no es un número entero válido.");
            return -1;
        }
    }

    /**
     * Lee un importe económico por consola (acepta coma o punto como separador decimal).
     * Devuelve -1 si la entrada no es un número válido.
     *
     * @return El importe como double, o -1 si hubo error
     */
    private static double leerImporte() {

        System.out.print("Importe (€): ");
        try {
            return Double.parseDouble(teclado.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] El importe introducido no es válido.");
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
     */
    private static void guardarDatos() {
        guardarLista(pacientes, RUTA_PACIENTES);
        guardarLista(medicos,   RUTA_MEDICOS);
        guardarLista(admins,    RUTA_ADMINS);
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
     * Si es la primera ejecución (ficheros vacíos), carga datos de ejemplo.
     */
    private static void cargarDatos() {

        List<Paciente>      pacientesCargados = cargarLista(RUTA_PACIENTES);
        List<Medico>        medicosCargados   = cargarLista(RUTA_MEDICOS);
        List<PersonalAdmin> adminsCargados    = cargarLista(RUTA_ADMINS);

        if (pacientesCargados != null) pacientes = pacientesCargados;
        if (medicosCargados   != null) medicos   = medicosCargados;
        if (adminsCargados    != null) admins    = adminsCargados;

        if (pacientes.isEmpty() && medicos.isEmpty() && admins.isEmpty()) {
            System.out.println("[SISTEMA] Primera ejecución. Cargando datos de ejemplo...");
            inicializarDatosEjemplo();
            guardarDatos();
        } else {
            System.out.println("[SISTEMA] Datos cargados. Pacientes: " + pacientes.size()
                + " | Médicos: " + medicos.size()
                + " | Admins: "  + admins.size());
        }
    }

    /**
     * Carga una lista serializable desde un fichero .dat.
     *
     * @param ruta Ruta del fichero a leer
     * @return La lista deserializada, o null si el fichero no existía
     */

    private static <T> List<T> cargarLista(String ruta) {

        File fichero = new File(ruta);
        if (!fichero.exists()) return null;

        try (ObjectInputStream leer = new ObjectInputStream(new FileInputStream(fichero))) {
            return (List<T>) leer.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[AVISO] Error al leer " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Inicializa datos de ejemplo para la primera ejecución del sistema.
     * Crea médicos, admins y pacientes de prueba para que no arranque vacío.
     */
    private static void inicializarDatosEjemplo() {

        try {
            medicos.add(new Medico("12345678A", "Carlos García López",
                LocalDate.of(1980, 3, 15), "M", 123456789, "Cardiología"));

            medicos.add(new Medico("87654321B", "Ana Martínez Ruiz",
                LocalDate.of(1985, 7, 22), "F", 987654321, "Pediatría"));

            admins.add(new PersonalAdmin("11223344C", "María López Torres",
                LocalDate.of(1990, 1, 10), "F", 100));

            Paciente p1 = new Paciente("99887766D", "Pedro Sánchez Gómez",
                LocalDate.of(1975, 6, 8), "M",
                "Calle Mayor 10, Zaragoza", 612345678,
                "pedro.sanchez@email.com", 698765432, "Ninguno");
            p1.getHistorial().agregarEnfermedadPrevia("Gastritis");
            p1.getHistorial().agregarAlergia("Penicilina");
            pacientes.add(p1);

            pacientes.add(new Paciente("55443322E", "Laura Fernández Díaz",
                LocalDate.of(1992, 11, 25), "F",
                "Av. Goya 45, Zaragoza", 623456789,
                "laura.fernandez@email.com", 677654321, "Ninguno"));

            System.out.println("[SISTEMA] Datos de ejemplo cargados: "
                + medicos.size() + " médicos, "
                + admins.size()  + " admins, "
                + pacientes.size() + " pacientes.");

        } catch (Exception e) {
            System.out.println("[AVISO] Error al inicializar datos de ejemplo: " + e.getMessage());
        }
    }
}