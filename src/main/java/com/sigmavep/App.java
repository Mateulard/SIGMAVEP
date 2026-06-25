package com.sigmavep;

import com.sigmavep.controlador.AuthControlador;
import com.sigmavep.vista.SetupFrame;

import javax.swing.*;

/**
 * Punto de entrada principal de SIGMAVEP v2.0.
 *
 * Flujo de inicio:
 *   1. Configura el Look & Feel Nimbus.
 *   2. Abre SetupFrame que verifica la conexión a MySQL:
 *      - Si conecta:  muestra LoginFrame directamente.
 *      - Si falla:    muestra el Asistente de Configuración (formulario,
 *                     botón "Probar conexión", botón "Crear base de datos").
 *
 * @author Mateo German Ruiz Díaz
 */
public class App {

    public static void main(String[] args) {
        // 1. Configurar Look & Feel Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus no disponible, usando L&F del sistema.");
        }

        // 2. Ajustes de color Nimbus para consistencia con Estilo.java
        UIManager.put("nimbusBase",       new java.awt.Color(0x1B3A5C));
        UIManager.put("nimbusBlueGrey",   new java.awt.Color(0x8BABBE));
        UIManager.put("nimbusFocus",      new java.awt.Color(0x2E86C1));
        UIManager.put("control",          new java.awt.Color(0xF0F2F5));

        // 3. Lanzar en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SetupFrame setup = new SetupFrame();
            setup.setOnConectado(() -> {
                // Cuando la conexión es confirmada, lanzar el login
                AuthControlador auth = new AuthControlador();
                auth.ejecutar();
            });
            setup.iniciarVerificacion();
        });
    }
}
