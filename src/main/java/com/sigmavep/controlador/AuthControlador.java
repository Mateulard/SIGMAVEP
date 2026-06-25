package com.sigmavep.controlador;

import com.sigmavep.dao.impl.AuthDAOImpl;
import com.sigmavep.dao.impl.AlertaDAOImpl;
import com.sigmavep.exepcion.AutenticacionException;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.Usuario;
import com.sigmavep.util.Session;
import com.sigmavep.vista.LoginFrame;
import com.sigmavep.vista.MenuPrincipalFrame;

import javax.swing.*;

/**
 * Controlador de autenticación. Coordina LoginFrame ↔ AuthDAOImpl ↔ Session.
 *
 * @author Mateo German Ruiz Díaz
 */
public class AuthControlador implements Controller {

    private final LoginFrame loginFrame;
    private final AuthDAOImpl authDAO;
    private final AlertaDAOImpl alertaDAO;

    public AuthControlador() {
        this.loginFrame = new LoginFrame();
        this.authDAO = new AuthDAOImpl();
        this.alertaDAO = new AlertaDAOImpl();
        configurarListener();
    }

    private void configurarListener() {
        loginFrame.setLoginListener((username, password) -> {
            loginFrame.mostrarInfo("Verificando credenciales...");
            SwingUtilities.invokeLater(() -> procesarLogin(username, password));
        });
    }

    private void procesarLogin(String username, String password) {
        try {
            Usuario usuario = authDAO.autenticar(username, password);
            Session.setUsuarioActual(usuario);
            loginFrame.dispose();
            abrirMenuPrincipal();
        } catch (AutenticacionException e) {
            loginFrame.mostrarError(e.getMessage());
        } catch (SIGMAVEPException e) {
            loginFrame.mostrarError("Error del sistema: " + e.getMessage());
        }
    }

    private void abrirMenuPrincipal() {
        MenuPrincipalFrame menu = new MenuPrincipalFrame();
        MenuPrincipalControlador ctrl = new MenuPrincipalControlador(menu);

        menu.setOnCerrarSesion(() -> {
            int resp = JOptionPane.showConfirmDialog(menu, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                Session.cerrar();
                menu.dispose();
                ejecutar();
            }
        });

        ctrl.inicializar();
        menu.mostrar();
    }

    @Override
    public void ejecutar() {
        loginFrame.limpiar();
        loginFrame.mostrar();
    }
}
