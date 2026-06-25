package com.sigmavep.vista;

import com.sigmavep.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventana principal con sidebar de navegación lateral.
 * Colores institucionales Policía de la Provincia de Santa Fe.
 * NOTA: No se usan emoji en los botones porque Swing en Windows no los renderiza.
 * Se usa texto plano con etiquetas cortas y una barra lateral de color como indicador.
 *
 * @author Mateo German Ruiz Díaz
 */
public class MenuPrincipalFrame extends BaseFrame {

    public static final String MOD_MOVILES        = "Moviles";
    public static final String MOD_ALERTAS        = "Alertas";
    public static final String MOD_KILOMETRAJE    = "Kilometraje";
    public static final String MOD_MANTENIMIENTO  = "Mantenimiento";
    public static final String MOD_NOVEDADES      = "Novedades";
    public static final String MOD_FLOTA          = "Flota";
    public static final String MOD_REPORTES       = "Reportes";
    public static final String MOD_USUARIOS       = "Usuarios";

    private JPanel panelContenido;
    private CardLayout cardLayout;
    private String moduloActivo = "";
    private final Map<String, JButton> botonesNav = new LinkedHashMap<>();

    private Runnable onCerrarSesion;

    public MenuPrincipalFrame() {
        super("Sistema Principal");
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Estilo.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210, 0));

        // Cabecera del sidebar: logo + nombre
        JPanel panelLogo = new JPanel(new BorderLayout(8, 0)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Estilo.PRIMARIO); g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelLogo.setOpaque(false);
        panelLogo.setBorder(new EmptyBorder(14, 12, 14, 12));

        ImageIcon logo = Estilo.getLogo(38, 38);
        if (logo != null) {
            panelLogo.add(new JLabel(logo), BorderLayout.WEST);
        }

        JPanel textosLogo = new JPanel(new GridLayout(2, 1));
        textosLogo.setOpaque(false);
        JLabel lblNombre = new JLabel("SIGMAVEP");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setForeground(Estilo.ACENTO);
        JLabel lblProv = new JLabel("Pcia. de Santa Fe");
        lblProv.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblProv.setForeground(new Color(0x8899BB));
        textosLogo.add(lblNombre);
        textosLogo.add(lblProv);
        panelLogo.add(textosLogo, BorderLayout.CENTER);
        sidebar.add(panelLogo, BorderLayout.NORTH);

        // Navegación
        JPanel panelNav = new JPanel();
        panelNav.setLayout(new BoxLayout(panelNav, BoxLayout.Y_AXIS));
        panelNav.setBackground(Estilo.SIDEBAR_BG);
        panelNav.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Categoría Operaciones
        agregarSeparadorCategoria(panelNav, "OPERACIONES");
        agregarBotonNav(panelNav, "Moviles",        MOD_MOVILES);
        agregarBotonNav(panelNav, "Alertas",        MOD_ALERTAS);
        agregarBotonNav(panelNav, "Kilometraje",    MOD_KILOMETRAJE);
        agregarBotonNav(panelNav, "Mantenimiento",  MOD_MANTENIMIENTO);
        agregarBotonNav(panelNav, "Novedades",      MOD_NOVEDADES);

        // Categoría Consultas
        agregarSeparadorCategoria(panelNav, "CONSULTAS");
        agregarBotonNav(panelNav, "Flota",          MOD_FLOTA);
        agregarBotonNav(panelNav, "Reportes",       MOD_REPORTES);

        // Categoría Admin
        agregarSeparadorCategoria(panelNav, "ADMINISTRACION");
        agregarBotonNav(panelNav, "Usuarios",       MOD_USUARIOS);

        filtrarPorRol();
        sidebar.add(panelNav, BorderLayout.CENTER);

        // Versión al pie del sidebar
        JLabel lblVer = new JLabel("v2.0");
        lblVer.setFont(Estilo.CHICO);
        lblVer.setForeground(new Color(0x2A3E5A));
        lblVer.setHorizontalAlignment(SwingConstants.CENTER);
        lblVer.setBorder(new EmptyBorder(8, 0, 8, 0));
        sidebar.add(lblVer, BorderLayout.SOUTH);

        // ===== BARRA SUPERIOR =====
        JPanel barraSuperior = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Estilo.PRIMARIO); g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        barraSuperior.setOpaque(false);
        barraSuperior.setBorder(new EmptyBorder(10, 20, 10, 20));
        barraSuperior.setPreferredSize(new Dimension(0, 52));

        // Info usuario
        String nombreCompleto = Session.estaAutenticado() ? Session.getUsuarioActual().getNombreCompleto() : "Usuario";
        String rolNombre = Session.estaAutenticado() && Session.getUsuarioActual().getRol() != null
            ? Session.getUsuarioActual().getRol().getNombre() : "";
        JLabel lblUsuario = new JLabel(nombreCompleto + "  |  " + rolNombre);
        lblUsuario.setFont(Estilo.CUERPO);
        lblUsuario.setForeground(Color.WHITE);
        barraSuperior.add(lblUsuario, BorderLayout.WEST);

        // Logo pequeño en la barra
        ImageIcon logoBar = Estilo.getLogo(32, 32);
        if (logoBar != null) {
            JLabel lblLogoBar = new JLabel(logoBar);
            lblLogoBar.setBorder(new EmptyBorder(0, 0, 0, 10));
            barraSuperior.add(lblLogoBar, BorderLayout.CENTER);
        }

        JButton btnLogout = new JButton("Cerrar Sesion");
        btnLogout.setFont(Estilo.CHICO);
        btnLogout.setForeground(Estilo.ACENTO);
        btnLogout.setBackground(new Color(0x1A3060));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.ACENTO, 1),
            new EmptyBorder(5, 12, 5, 12)
        ));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> { if (onCerrarSesion != null) onCerrarSesion.run(); });
        barraSuperior.add(btnLogout, BorderLayout.EAST);

        // ===== CONTENIDO =====
        cardLayout     = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(Estilo.SECUNDARIO);
        panelContenido.add(crearPanelBienvenida(), "BIENVENIDA");

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(barraSuperior, BorderLayout.NORTH);
        panelDerecho.add(panelContenido, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);

        cardLayout.show(panelContenido, "BIENVENIDA");
    }

    private void agregarSeparadorCategoria(JPanel panel, String label) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Estilo.SIDEBAR_BG);
        fila.setBorder(new EmptyBorder(12, 14, 4, 14));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(0x3A5070));
        fila.add(lbl);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        panel.add(fila);
    }

    private void agregarBotonNav(JPanel panel, String etiqueta, String modulo) {
        JButton btn = new JButton(etiqueta) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                if (modulo.equals(moduloActivo)) {
                    // Seleccionado: barra dorada izquierda + fondo suave
                    g2.setColor(new Color(0x112240));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Estilo.ACENTO);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(0x8899BB));
        btn.setBackground(Estilo.SIDEBAR_BG);
        btn.setBorder(new EmptyBorder(11, 20, 11, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!modulo.equals(moduloActivo)) {
                    btn.setForeground(Color.WHITE);
                    btn.repaint();
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!modulo.equals(moduloActivo)) {
                    btn.setForeground(new Color(0x8899BB));
                    btn.repaint();
                }
            }
        });

        btn.addActionListener(e -> seleccionarModulo(modulo));
        botonesNav.put(modulo, btn);
        panel.add(btn);
    }

    private void seleccionarModulo(String modulo) {
        JButton prevBtn = botonesNav.get(moduloActivo);
        if (prevBtn != null) {
            prevBtn.setForeground(new Color(0x8899BB));
            prevBtn.repaint();
        }
        moduloActivo = modulo;
        JButton activo = botonesNav.get(modulo);
        if (activo != null) {
            activo.setForeground(Estilo.ACENTO);
            activo.repaint();
        }
        if (moduloListener != null) moduloListener.onModuloSeleccionado(modulo);
    }

    private void filtrarPorRol() {
        boolean esAdmin = Session.esAdministrador();
        boolean esSupervisor = Session.esSupervisorOAdmin();

        botonesNav.get(MOD_MOVILES).setVisible(esSupervisor);
        botonesNav.get(MOD_ALERTAS).setVisible(esSupervisor);
        botonesNav.get(MOD_KILOMETRAJE).setVisible(esSupervisor);
        botonesNav.get(MOD_MANTENIMIENTO).setVisible(esSupervisor);
        botonesNav.get(MOD_NOVEDADES).setVisible(esSupervisor);
        botonesNav.get(MOD_FLOTA).setVisible(true);
        botonesNav.get(MOD_REPORTES).setVisible(true);
        botonesNav.get(MOD_USUARIOS).setVisible(esAdmin);

        if (esAdmin) botonesNav.values().forEach(b -> b.setVisible(true));
    }

    private JPanel crearPanelBienvenida() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Estilo.SECUNDARIO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(8, 8, 8, 8);

        ImageIcon logo = Estilo.getLogo(120, 120);
        if (logo != null) {
            panel.add(new JLabel(logo, SwingConstants.CENTER), gbc);
        }

        gbc.gridy = 1;
        JLabel lblTitulo = new JLabel("Bienvenido a SIGMAVEP", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Estilo.PRIMARIO);
        panel.add(lblTitulo, gbc);

        gbc.gridy = 2;
        JLabel lblInst = new JLabel("Policia de la Provincia de Santa Fe", SwingConstants.CENTER);
        lblInst.setFont(Estilo.CUERPO);
        lblInst.setForeground(Estilo.AZUL_REF);
        panel.add(lblInst, gbc);

        gbc.gridy = 3;
        String usuario = Session.estaAutenticado() ? Session.getUsuarioActual().getNombreCompleto() : "";
        JLabel lblSub = new JLabel("Hola, " + usuario + ". Selecciona una opcion del menu lateral.", SwingConstants.CENTER);
        lblSub.setFont(Estilo.CHICO);
        lblSub.setForeground(new Color(0x888888));
        panel.add(lblSub, gbc);

        return panel;
    }

    public void mostrarPanel(String clave, JPanel panel) {
        panelContenido.add(panel, clave);
        cardLayout.show(panelContenido, clave);
    }

    public void mostrarPanelExistente(String clave) { cardLayout.show(panelContenido, clave); }

    public interface ModuloListener { void onModuloSeleccionado(String modulo); }
    private ModuloListener moduloListener;
    public void setModuloListener(ModuloListener l) { this.moduloListener = l; }
    public void setOnCerrarSesion(Runnable r) { this.onCerrarSesion = r; }
    public JPanel getPanelContenido() { return panelContenido; }
    public CardLayout getCardLayout() { return cardLayout; }
}
