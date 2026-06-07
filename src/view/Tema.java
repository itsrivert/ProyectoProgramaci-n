package src.view;

import java.awt.Color;
import java.awt.Font;

public class Tema {
    // Colores estándar y limpios
    public static final Color FONDO_OSCURO       = new Color(30, 30, 30);
    public static final Color FONDO_PANEL        = new Color(245, 245, 245);
    public static final Color FONDO_CAMPO        = Color.WHITE;
    public static final Color TEXTO_PRIMARIO     = Color.BLACK;
    public static final Color TEXTO_SECUNDARIO   = Color.DARK_GRAY;
    public static final Color BORDE              = Color.LIGHT_GRAY;
    
    public static final Color ACENTO             = new Color(0, 102, 204); // Azul vistoso
    public static final Color AZUL               = new Color(0, 102, 204);
    public static final Color ROJO               = new Color(204, 0, 0);

    // Fuentes estándar del sistema
    public static final Font FUENTE_NORMAL    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_BOLD      = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_SMALL     = new Font("Segoe UI", Font.PLAIN, 12);

    public static void aplicar() {
        // Método vacío por si en el futuro añado LookAndFeel,
        // de momento evita que falle la llamada al constructor del Login.
    }
}
