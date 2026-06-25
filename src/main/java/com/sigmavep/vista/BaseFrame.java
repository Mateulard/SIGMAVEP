package com.sigmavep.vista;

import javax.swing.*;
import java.awt.*;

/**
 * Clase abstracta base para todos los JFrames del sistema SIGMAVEP.
 * Aplica la configuración visual común: título, tamaño, centrado, icono, fondo.
 * Implementa herencia en la capa de vista.
 *
 * @author Mateo German Ruiz Díaz
 */
public abstract class BaseFrame extends JFrame {

    protected static final int ANCHO_DEFECTO  = 1200;
    protected static final int ALTO_DEFECTO   = 720;

    /**
     * Constructor base. Aplica título, tamaño, Look & Feel, fondo.
     *
     * @param titulo Título de la ventana.
     */
    protected BaseFrame(String titulo) {
        super("SIGMAVEP v2.0 — " + titulo);
        configurar();
    }

    private void configurar() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO_DEFECTO, ALTO_DEFECTO);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null); // centrar en pantalla
        getContentPane().setBackground(Estilo.SECUNDARIO);
        // Icono de la ventana (barra de título y barra de tareas)
        java.net.URL iconUrl = getClass().getClassLoader().getResource("logo_policia.png");
        if (iconUrl != null) {
            setIconImage(new javax.swing.ImageIcon(iconUrl).getImage());
        }
    }

    /**
     * Muestra la ventana y la trae al frente.
     */
    public void mostrar() {
        setVisible(true);
        toFront();
    }

    /**
     * Cierra y libera la ventana.
     */
    public void cerrar() {
        dispose();
    }
}
