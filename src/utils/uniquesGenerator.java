package utils;

import Dao.PedidoDAO;
import Dao.EnvioDAO;
import java.util.Random;

/**
 *
 * @author Esteban Rivarola, Daniela Romero, Agustín Rivarola
 */

public class uniquesGenerator {

    private final PedidoDAO pedidoDAO;
    private final EnvioDAO envioDAO;
    private final Random random = new Random();
    private static final int TRACKING_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 10;

    public uniquesGenerator(PedidoDAO pedidoDAO, EnvioDAO envioDAO) {
        if (pedidoDAO == null || envioDAO == null) {
            throw new IllegalArgumentException("Los DAOs de Pedido y Envío no pueden ser nulos.");
        }
        this.pedidoDAO = pedidoDAO;
        this.envioDAO = envioDAO;
    }
    
    /**
     * Genera el siguiente número de pedido basado en el último registro de la DB.
     * Formato: PXXXX (ej. P0000, P0001, P0010, P1000). Si no hay pedidos, inicia en P0000.
     * @return El siguiente número de pedido.
     * @throws Exception Si hay un error al consultar la base de datos.
     */
    public String generarNumeroPedido() throws Exception {
        String lastNumber = pedidoDAO.getLastPedidoNumber();

        // Si no hay pedidos, el contador empieza en -1 para que la primera vez sea 0 (P0001)
        long counter = 0; 
        
        if (lastNumber != null && lastNumber.startsWith("P") && lastNumber.length() > 1) {
            try {
                // Extraer el valor numérico
                String numPart = lastNumber.substring(1);
                counter = Long.parseLong(numPart);
            } catch (NumberFormatException e) {
                System.err.println("Advertencia: Formato de número de pedido inesperado: " + lastNumber);
            }
        }
        counter++;
        return String.format("P%04d", counter);
    }
    
   /**
     * Genera un código de tracking alfanumérico único, verificando en la DB en un bucle.
     * @return String con formato TRK + 5 dígitos aleatorios.
     * @throws Exception Si no se puede generar un código único después de MAX_ATTEMPTS.
     */
    public String generarTrackingCode() throws Exception {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            StringBuilder sb = new StringBuilder("TRK");
            for (int i = 0; i < TRACKING_LENGTH; i++) {
                sb.append(random.nextInt(10)); // Genera dígitos de 0 a 9
            }
            String trackingCode = sb.toString();

            // Verificar la unicidad en la DB
            if (envioDAO.isTrackingUnique(trackingCode)) {
                return trackingCode;
            }
        }
        throw new Exception("Error al generar un código de tracking único después de " + MAX_ATTEMPTS + " intentos.");
    }

}
