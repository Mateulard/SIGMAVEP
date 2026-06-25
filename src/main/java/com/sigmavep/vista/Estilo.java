package com.sigmavep.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

/**
 * Sistema de diseño visual de SIGMAVEP — Paleta institucional
 * Policía de la Provincia de Santa Fe.
 *
 * @author Mateo German Ruiz Díaz
 */
public final class Estilo {

    // ===== COLORES INSTITUCIONALES =====
    /** Azul Marino Institucional — fondo de barras, sidebar */
    public static final Color PRIMARIO       = new Color(0x0B1F4D);
    /** Fondo claro — paneles de contenido */
    public static final Color SECUNDARIO     = new Color(0xF5F5F5);
    /** Dorado Principal — botones, acentos */
    public static final Color ACENTO         = new Color(0xD4AF37);
    /** Dorado Oscuro — hover de botones */
    public static final Color ACENTO_HOVER   = new Color(0xB8860B);
    /** Azul Provincia — indicadores de estado */
    public static final Color AZUL_PROV      = new Color(0x3A75C4);
    /** Rojo Provincia — alertas críticas, eliminar */
    public static final Color PELIGRO        = new Color(0xC62828);
    /** Verde — éxito, conexión OK */
    public static final Color EXITO          = new Color(0x2E7D32);
    /** Texto principal */
    public static final Color TEXTO          = new Color(0x212121);
    /** Texto sobre fondo oscuro */
    public static final Color TEXTO_CLARO    = Color.WHITE;
    /** Azul Referencia — subtítulos secundarios */
    public static final Color AZUL_REF       = new Color(0x5F6281);
    /** Borde sutil */
    public static final Color BORDE          = new Color(0xDDDDDD);
    /** Sidebar ligeramente más oscura que PRIMARIO */
    public static final Color SIDEBAR_BG     = new Color(0x071530);
    /** Selección en sidebar: dorado */
    public static final Color SIDEBAR_SEL    = new Color(0xD4AF37);
    /** Fila alternada de tabla */
    public static final Color TABLA_ALT      = new Color(0xFAFAFA);
    /** Alerta naranja */
    public static final Color ALERTA         = new Color(0xE65100);

    // ===== TIPOGRAFÍAS =====
    public static final Font TITULO    = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font SUBTITULO = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font CUERPO    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOTON     = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font CHICO     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font LOGO      = new Font("Segoe UI", Font.BOLD,  28);

    // ===== PADDING =====
    public static final EmptyBorder PADDING       = new EmptyBorder(15, 20, 15, 20);
    public static final EmptyBorder PADDING_CHICO = new EmptyBorder(8,  12,  8, 12);

    private Estilo() {}

    // ===== LOGO =====

    /**
     * Carga el logo de la Policía de Santa Fe desde el classpath.
     * @param ancho ancho deseado en píxeles
     * @param alto  alto deseado en píxeles
     * @return ImageIcon escalado, o null si no se puede cargar
     */
    public static ImageIcon getLogo(int ancho, int alto) {
        try {
            URL url = Estilo.class.getClassLoader().getResource("logo_policia.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar logo_policia.png: " + e.getMessage());
        }
        return null;
    }

    // ===== BOTONES FACTORY =====

    /** Botón primario: dorado institucional */
    public static JButton botonPrimario(String texto) {
        return crearBoton(texto, ACENTO, TEXTO);
    }

    /** Botón de peligro: rojo provincia */
    public static JButton botonPeligro(String texto) {
        return crearBoton(texto, PELIGRO, TEXTO_CLARO);
    }

    /** Botón secundario: borde gris, texto oscuro */
    public static JButton botonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXTO);
        btn.setFont(BOTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1),
            new EmptyBorder(8, 18, 8, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    /** Botón de éxito: verde */
    public static JButton botonExito(String texto) {
        return crearBoton(texto, EXITO, TEXTO_CLARO);
    }

    /** Botón de alerta: naranja */
    public static JButton botonAlerta(String texto) {
        return crearBoton(texto, ALERTA, TEXTO_CLARO);
    }

    private static JButton crearBoton(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(BOTON);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 22, 9, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    // ===== LABELS FACTORY =====

    public static JLabel titulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(TITULO);
        lbl.setForeground(PRIMARIO);
        return lbl;
    }

    public static JLabel subtitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(SUBTITULO);
        lbl.setForeground(TEXTO);
        return lbl;
    }

    // ===== TABLA =====

    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(CUERPO);
        tabla.setRowHeight(30);
        tabla.setSelectionBackground(new Color(0xFFF8E1));
        tabla.setSelectionForeground(TEXTO);
        tabla.setGridColor(BORDE);
        tabla.setBackground(Color.WHITE);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.getTableHeader().setFont(BOTON);
        tabla.getTableHeader().setBackground(PRIMARIO);
        tabla.getTableHeader().setForeground(TEXTO_CLARO);
        tabla.getTableHeader().setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        tabla.setIntercellSpacing(new Dimension(0, 1));
    }

    // ===== COMBO =====

    public static <E> void estilizarCombo(JComboBox<E> combo) {
        combo.setFont(CUERPO);
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXTO);
    }

    // ===== CAMPOS =====

    public static void estilizarCampo(JTextField campo) {
        campo.setFont(CUERPO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }

    public static void estilizarArea(JTextArea area) {
        area.setFont(CUERPO);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(6, 10, 6, 10));
    }

    // ===== PANEL CON BORDE TITULADO =====

    public static JPanel panelConBorde(String titulo) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDE, 1), titulo,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            SUBTITULO, PRIMARIO
        ));
        return p;
    }

    public static JSeparator separador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDE);
        return sep;
    }
}
