package com.sigmavep.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Pantalla de inicio de sesión — Policía de la Provincia de Santa Fe.
 *
 * @author Mateo German Ruiz Díaz
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JLabel lblMensaje;
    private LoginListener listener;

    public interface LoginListener {
        void onLogin(String username, String password);
    }

    public LoginFrame() {
        super("SIGMAVEP v2.0 — Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 640);
        setMinimumSize(new Dimension(420, 580));
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        // Fondo azul marino con gradiente
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Estilo.PRIMARIO, 0, getHeight(), new Color(0x040E24));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(panelPrincipal);

        // ===== SECCIÓN SUPERIOR: LOGO + TÍTULO =====
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
        panelTop.setOpaque(false);
        panelTop.setBorder(new EmptyBorder(36, 40, 20, 40));

        // Logo
        ImageIcon logo = Estilo.getLogo(100, 100);
        if (logo != null) {
            JLabel lblLogo = new JLabel(logo, SwingConstants.CENTER);
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelTop.add(lblLogo);
            panelTop.add(Box.createVerticalStrut(14));
        }

        // Título
        JLabel lblSistema = new JLabel("SIGMAVEP v2.0", SwingConstants.CENTER);
        lblSistema.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblSistema.setForeground(Estilo.ACENTO);
        lblSistema.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTop.add(lblSistema);
        panelTop.add(Box.createVerticalStrut(6));

        JLabel lblInst = new JLabel("<html><center>Policía de la Provincia de Santa Fe<br>Sistema de Mantenimiento de Vehículos</center></html>", SwingConstants.CENTER);
        lblInst.setFont(Estilo.CHICO);
        lblInst.setForeground(new Color(0x8899BB));
        lblInst.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTop.add(lblInst);

        // Línea dorada separadora
        panelTop.add(Box.createVerticalStrut(18));
        JPanel lineaDorada = new JPanel();
        lineaDorada.setOpaque(false);
        lineaDorada.setMaximumSize(new Dimension(200, 2));
        lineaDorada.setPreferredSize(new Dimension(200, 2));
        lineaDorada.setBackground(Estilo.ACENTO);
        lineaDorada.setOpaque(true);
        lineaDorada.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTop.add(lineaDorada);

        // ===== FORMULARIO =====
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setOpaque(false);
        panelForm.setBorder(new EmptyBorder(20, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridwidth = 1;

        // Usuario
        gbc.gridy = 0;
        JLabel lblU = new JLabel("USUARIO");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblU.setForeground(Estilo.ACENTO);
        lblU.setHorizontalAlignment(SwingConstants.LEFT);
        panelForm.add(lblU, gbc);

        gbc.gridy = 1;
        txtUsuario = new JTextField();
        txtUsuario.setFont(Estilo.CUERPO);
        txtUsuario.setPreferredSize(new Dimension(340, 42));
        estilizarCampoLogin(txtUsuario);
        panelForm.add(txtUsuario, gbc);

        // Contraseña
        gbc.gridy = 2;
        JLabel lblP = new JLabel("CONTRASENA");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblP.setForeground(Estilo.ACENTO);
        lblP.setHorizontalAlignment(SwingConstants.LEFT);
        panelForm.add(lblP, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField();
        txtPassword.setFont(Estilo.CUERPO);
        txtPassword.setPreferredSize(new Dimension(340, 42));
        estilizarCampoLogin(txtPassword);
        panelForm.add(txtPassword, gbc);

        // Mensaje
        gbc.gridy = 4;
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(Estilo.CHICO);
        lblMensaje.setForeground(Estilo.ALERTA);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        panelForm.add(lblMensaje, gbc);

        // Botón Ingresar — dorado institucional
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 0, 0);
        btnIngresar = new JButton("INGRESAR AL SISTEMA") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? Estilo.ACENTO_HOVER.darker()
                         : getModel().isRollover() ? Estilo.ACENTO_HOVER
                         : Estilo.ACENTO;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
            }
        };
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIngresar.setForeground(Estilo.PRIMARIO);  // texto azul oscuro sobre dorado
        btnIngresar.setPreferredSize(new Dimension(340, 46));
        btnIngresar.setContentAreaFilled(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelForm.add(btnIngresar, gbc);

        // ===== PIE =====
        JPanel panelBottom = new JPanel();
        panelBottom.setOpaque(false);
        panelBottom.setBorder(new EmptyBorder(0, 0, 18, 0));
        JLabel lblVersion = new JLabel("SIGMAVEP v2.0 - Universidad Siglo 21");
        lblVersion.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblVersion.setForeground(new Color(0x344A66));
        panelBottom.add(lblVersion);

        panelPrincipal.add(panelTop, BorderLayout.NORTH);
        panelPrincipal.add(panelForm, BorderLayout.CENTER);
        panelPrincipal.add(panelBottom, BorderLayout.SOUTH);

        // ===== EVENTOS =====
        btnIngresar.addActionListener(e -> disparar());
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) disparar(); }
        });
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocus(); }
        });
    }

    private void estilizarCampoLogin(JTextField tf) {
        tf.setBackground(new Color(0x122040));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Estilo.ACENTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x2A4A80), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void disparar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            mostrarError("Ingrese usuario y contraseña.");
            return;
        }
        if (listener != null) listener.onLogin(user, pass);
    }

    public void setLoginListener(LoginListener l) { this.listener = l; }

    public void mostrarError(String msg) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(Estilo.ALERTA);
        txtPassword.setText("");
        txtPassword.requestFocus();
    }

    public void mostrarInfo(String msg) {
        lblMensaje.setText(msg);
        lblMensaje.setForeground(Estilo.EXITO);
    }

    public void limpiar() {
        txtUsuario.setText("");
        txtPassword.setText("");
        lblMensaje.setText(" ");
        txtUsuario.requestFocus();
    }

    public void mostrar() {
        setVisible(true);
        txtUsuario.requestFocus();
    }
}
