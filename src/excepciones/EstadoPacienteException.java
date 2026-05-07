package excepciones;
/*
===================================
|             REVISADA            |
===================================

*/

/**
 * Excepción PERSONALIZADA para manejar errores relacionados con el estado del paciente
 * Hereda de Exception para que sea "checked" (obliga a manejarla con try-catch)
 * 
 * ¿Dónde se usa esta excepción?
 * - En AreaFacturacion.generarFactura() se LANZA cuando el paciente está de BAJA_TEMPORAL
 * - En AreaFacturacion.notificarProcedimiento() se CAPTURA con try-catch
 * 
 * ¿Por qué es checked (hereda de Exception y no de RuntimeException)?
 * - Para FORZAR a que quien la use la maneje con try-catch o throws
 * - Así nos aseguramos de que no se nos olvide validar el estado del paciente antes de facturar
 * 
 * @author: Julia Amoros, Laura Leciñena, Alejandro Díaz 
 */

public class EstadoPacienteException extends RuntimeException {
    
    /**
     * Constructor que recibe el mensaje de error
     * 
     * @param mensaje Texto explicativo del error
     */
    public EstadoPacienteException(String mensaje) {
        super(mensaje);  // Le paso el mensaje a la clase padre (RuntimeException)
    }
    
    /**
     * Método que devuelve el mensaje de error formateado
     * Sobrescribe el método getMessage() de Exception
     * 
     * @return Mensaje de error con formato
     */
    @Override
    public String getMessage() {
        return "[ERROR ESTADO PACIENTE] " + super.getMessage();
    }
    
    /**
     * Método que devuelve el mensaje de error sin el prefijo
     * 
     * @return Mensaje de error original
     */
    public String getMensajeOriginal() {
        return super.getMessage();
    }
}