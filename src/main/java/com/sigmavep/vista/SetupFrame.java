package com.sigmavep.vista;

import com.sigmavep.util.ConexionMySQL;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Ventana de inicio y asistente de configuración de SIGMAVEP.
 *
 * Comportamiento:
 *  - Muestra un splash animado y verifica la conexión a MySQL.
 *  - Si la conexión es exitosa: llama al callback onConectado() y se cierra.
 *  - Si falla: muestra el panel de configuración donde el usuario puede:
 *      a) Ingresar sus credenciales y probar la conexión.
 *      b) Crear la base de datos automáticamente (ejecuta el schema SQL embebido).
 *      c) Guardar la configuración y continuar.
 *
 * @author Mateo German Ruiz Díaz
 */
public class SetupFrame extends JFrame {

    // ===== CALLBACK =====
    private Runnable onConectado;

    // ===== PANELES =====
    private JPanel panelPrincipal;
    private CardLayout cardLayout;

    // Panel de verificación
    private JLabel lblEstadoVerificacion;
    private JLabel lblIconoEstado;
    private JProgressBar progressBar;

    // Panel de configuración
    private JTextField txtHost, txtPuerto, txtBaseDatos, txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblResultadoPrueba;
    private JButton btnProbar, btnCrearBD, btnGuardar;
    private boolean conexionProbada = false;

    // ===== CONSTANTES =====
    private static final String PANEL_VERIFICANDO = "VERIFICANDO";
    private static final String PANEL_CONFIG      = "CONFIG";

    public SetupFrame() {
        super("SIGMAVEP v2.0 — Configuración Inicial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 520);
        setMinimumSize(new Dimension(500, 460));
        setLocationRelativeTo(null);
        setResizable(false);
        // Icono de la ventana (barra de título y barra de tareas)
        java.net.URL iconUrl = getClass().getClassLoader().getResource("logo_policia.png");
        if (iconUrl != null) {
            setIconImage(new javax.swing.ImageIcon(iconUrl).getImage());
        }
        buildUI();
    }

    // ==================== CONSTRUCCIÓN UI ====================

    private void buildUI() {
        cardLayout    = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setBackground(Estilo.PRIMARIO);
        setContentPane(panelPrincipal);

        panelPrincipal.add(buildPanelVerificando(), PANEL_VERIFICANDO);
        panelPrincipal.add(buildPanelConfig(),      PANEL_CONFIG);
        cardLayout.show(panelPrincipal, PANEL_VERIFICANDO);
    }

    /** Panel de splash mientras se verifica la conexión. */
    private JPanel buildPanelVerificando() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Estilo.PRIMARIO, 0, getHeight(), new Color(0x0D2137));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 60, 10, 60);

        // Ícono
        gbc.gridy = 0;
        JLabel icono = new JLabel("⚓", SwingConstants.CENTER);
        icono.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 64));
        icono.setForeground(Estilo.ACENTO);
        panel.add(icono, gbc);

        // Título
        gbc.gridy = 1;
        JLabel titulo = new JLabel("SIGMAVEP v2.0", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, gbc);

        // Subtítulo
        gbc.gridy = 2;
        JLabel sub = new JLabel("<html><center>Sistema de Gestión del Mantenimiento<br>Preventivo de Vehículos Policiales</center></html>", SwingConstants.CENTER);
        sub.setFont(Estilo.CHICO);
        sub.setForeground(new Color(0xAEC6D8));
        panel.add(sub, gbc);

        // Separador
        gbc.gridy = 3; gbc.insets = new Insets(20, 60, 10, 60);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2E4A65));
        panel.add(sep, gbc);

        // Ícono de estado
        gbc.gridy = 4; gbc.insets = new Insets(10, 60, 4, 60);
        lblIconoEstado = new JLabel("🔄", SwingConstants.CENTER);
        lblIconoEstado.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 30));
        panel.add(lblIconoEstado, gbc);

        // Label de estado
        gbc.gridy = 5;
        lblEstadoVerificacion = new JLabel("Verificando conexión a la base de datos...", SwingConstants.CENTER);
        lblEstadoVerificacion.setFont(Estilo.CUERPO);
        lblEstadoVerificacion.setForeground(new Color(0xAEC6D8));
        panel.add(lblEstadoVerificacion, gbc);

        // Barra de progreso
        gbc.gridy = 6; gbc.insets = new Insets(12, 60, 30, 60);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(Estilo.ACENTO);
        progressBar.setBackground(new Color(0x1E4A70));
        progressBar.setBorder(null);
        progressBar.setPreferredSize(new Dimension(0, 6));
        panel.add(progressBar, gbc);

        // Versión
        gbc.gridy = 7; gbc.insets = new Insets(0, 60, 20, 60);
        JLabel ver = new JLabel("Universidad Siglo 21 — Programación II", SwingConstants.CENTER);
        ver.setFont(Estilo.CHICO);
        ver.setForeground(new Color(0x3A5A76));
        panel.add(ver, gbc);

        return panel;
    }

    /** Panel de configuración cuando la conexión falla. */
    private JPanel buildPanelConfig() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Estilo.PRIMARIO, 0, getHeight(), new Color(0x0D2137));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);

        // ===== ENCABEZADO =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 28, 10, 28));

        JPanel tituloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tituloRow.setOpaque(false);
        JLabel ico = new JLabel("⚙");
        ico.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 22));
        ico.setForeground(Estilo.ALERTA);
        tituloRow.add(ico);
        JLabel lblT = new JLabel("Asistente de Configuración de Base de Datos");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblT.setForeground(Color.WHITE);
        tituloRow.add(lblT);
        header.add(tituloRow, BorderLayout.NORTH);

        JLabel lblDesc = new JLabel("<html>No se pudo conectar a MySQL. Completá los datos de conexión y presioná <b>Probar conexión</b>. Luego creá la base de datos automáticamente o indicá una existente.</html>");
        lblDesc.setFont(Estilo.CHICO);
        lblDesc.setForeground(new Color(0xAEC6D8));
        lblDesc.setBorder(new EmptyBorder(6, 0, 0, 0));
        header.add(lblDesc, BorderLayout.CENTER);
        panel.add(header, BorderLayout.NORTH);

        // ===== FORMULARIO =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(255, 255, 255, 15));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(10, 28, 10, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);

        // Fila: Host + Puerto
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(lblConfig("Servidor (host):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        txtHost = campoDB("localhost");
        form.add(txtHost, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        form.add(lblConfig("Puerto:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0;
        txtPuerto = campoDB("3306");
        txtPuerto.setPreferredSize(new Dimension(70, 36));
        form.add(txtPuerto, gbc);

        // Fila: Base de datos
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(lblConfig("Base de datos:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtBaseDatos = campoDB("sigmavep");
        form.add(txtBaseDatos, gbc);
        gbc.gridwidth = 1;

        // Fila: Usuario
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(lblConfig("Usuario MySQL:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1; gbc.gridwidth = 3;
        txtUsuario = campoDB("root");
        form.add(txtUsuario, gbc);
        gbc.gridwidth = 1;

        // Fila: Contraseña
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(lblConfig("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1; gbc.gridwidth = 3;
        txtPassword = new JPasswordField();
        estilizarCampoOscuro(txtPassword);
        form.add(txtPassword, gbc);
        gbc.gridwidth = 1;

        // Resultado de prueba
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 1; gbc.gridwidth = 4;
        lblResultadoPrueba = new JLabel(" ");
        lblResultadoPrueba.setFont(Estilo.CHICO);
        lblResultadoPrueba.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(lblResultadoPrueba, gbc);
        gbc.gridwidth = 1;

        panel.add(form, BorderLayout.CENTER);

        // ===== BOTONES =====
        JPanel botonesPanel = new JPanel(new BorderLayout());
        botonesPanel.setOpaque(false);
        botonesPanel.setBorder(new EmptyBorder(0, 28, 20, 28));

        // Fila 1: Probar + Crear BD
        JPanel fila1 = new JPanel(new GridLayout(1, 2, 10, 0));
        fila1.setOpaque(false);

        btnProbar = new JButton("🔌 Probar Conexión");
        estilizarBotonSetup(btnProbar, new Color(0x2E86C1));
        btnProbar.addActionListener(this::accionProbarConexion);
        fila1.add(btnProbar);

        btnCrearBD = new JButton("🗄  Crear Base de Datos");
        estilizarBotonSetup(btnCrearBD, new Color(0x1A8B45));
        btnCrearBD.setEnabled(false);
        btnCrearBD.addActionListener(this::accionCrearBaseDatos);
        fila1.add(btnCrearBD);

        botonesPanel.add(fila1, BorderLayout.NORTH);

        // Fila 2: Guardar y continuar
        JPanel fila2 = new JPanel(new GridLayout(1, 1));
        fila2.setOpaque(false);
        fila2.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnGuardar = new JButton("✔  Guardar configuración y continuar");
        estilizarBotonSetup(btnGuardar, Estilo.PRIMARIO);
        btnGuardar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x4A7FA5), 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(this::accionGuardarYContinuar);
        fila2.add(btnGuardar);

        botonesPanel.add(fila2, BorderLayout.CENTER);

        // Nota informativa
        JLabel nota = new JLabel("<html><center>💡 La configuración se guarda en <i>db.properties</i> junto al programa para futuras ejecuciones.</center></html>");
        nota.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        nota.setForeground(new Color(0x567A96));
        nota.setHorizontalAlignment(SwingConstants.CENTER);
        nota.setBorder(new EmptyBorder(8, 0, 0, 0));
        botonesPanel.add(nota, BorderLayout.SOUTH);

        panel.add(botonesPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ==================== ACCIONES ====================

    /** Prueba la conexión con los datos ingresados. */
    private void accionProbarConexion(ActionEvent e) {
        btnProbar.setEnabled(false);
        lblResultadoPrueba.setText("🔄 Probando conexión...");
        lblResultadoPrueba.setForeground(new Color(0xAEC6D8));

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String errorMsg = "";
            @Override
            protected Boolean doInBackground() {
                try {
                    String host = txtHost.getText().trim();
                    int port    = Integer.parseInt(txtPuerto.getText().trim());
                    String db   = txtBaseDatos.getText().trim();
                    String usr  = txtUsuario.getText().trim();
                    String pass = new String(txtPassword.getPassword());
                    ConexionMySQL.verificarConexionConParametros(host, port, db, usr, pass);
                    return true;
                } catch (NumberFormatException ex) {
                    errorMsg = "El puerto debe ser un número.";
                    return false;
                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                    // Si el error es que la BD no existe, igual el servidor funciona
                    if (ex.getMessage() != null &&
                        (ex.getMessage().contains("Unknown database") ||
                         ex.getMessage().contains("doesn't exist"))) {
                        errorMsg = "El servidor MySQL responde, pero la base de datos '" +
                                   txtBaseDatos.getText().trim() + "' no existe aún. Podés crearla automáticamente.";
                        return false; // Marcamos false pero habilitamos Crear BD
                    }
                    return false;
                }
            }
            @Override
            protected void done() {
                btnProbar.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) {
                        lblResultadoPrueba.setText("✅ Conexión exitosa con la base de datos.");
                        lblResultadoPrueba.setForeground(Estilo.EXITO);
                        conexionProbada = true;
                        btnCrearBD.setEnabled(false);
                        btnGuardar.setEnabled(true);
                    } else {
                        lblResultadoPrueba.setText("<html><center>❌ " + errorMsg + "</center></html>");
                        lblResultadoPrueba.setForeground(Estilo.ALERTA);
                        conexionProbada = false;
                        // Habilitar "Crear BD" si el servidor conectó pero la BD no existe
                        boolean servidorOk = errorMsg.contains("no existe aún");
                        btnCrearBD.setEnabled(servidorOk);
                        btnGuardar.setEnabled(false);
                    }
                } catch (Exception ex) {
                    lblResultadoPrueba.setText("❌ Error inesperado: " + ex.getMessage());
                    lblResultadoPrueba.setForeground(Estilo.PELIGRO);
                    btnGuardar.setEnabled(false);
                }
            }
        };
        worker.execute();
    }

    /** Crea la base de datos ejecutando el schema SQL embebido. */
    private void accionCrearBaseDatos(ActionEvent e) {
        btnCrearBD.setEnabled(false);
        btnProbar.setEnabled(false);
        lblResultadoPrueba.setText("🔄 Creando base de datos...");
        lblResultadoPrueba.setForeground(new Color(0xAEC6D8));

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            String errorMsg = "";

            @Override
            protected Void doInBackground() throws Exception {
                String host = txtHost.getText().trim();
                int port    = Integer.parseInt(txtPuerto.getText().trim());
                String db   = txtBaseDatos.getText().trim();
                String usr  = txtUsuario.getText().trim();
                String pass = new String(txtPassword.getPassword());

                // Cargar el SQL embebido del classpath
                InputStream sqlStream = SetupFrame.class.getClassLoader()
                        .getResourceAsStream("sigmavep_schema.sql");
                if (sqlStream == null) {
                    throw new Exception("No se encontró el archivo sigmavep_schema.sql en el paquete.");
                }
                String sql;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(sqlStream, "UTF-8"))) {
                    sql = reader.lines().collect(Collectors.joining("\n"));
                }

                // Conectar al servidor sin seleccionar BD y ejecutar script
                try (Connection conn = ConexionMySQL.getConnectionSinDB(host, port, usr, pass)) {
                    conn.setAutoCommit(false);
                    // Dividir por ";" y ejecutar sentencia a sentencia
                    String[] sentencias = sql.split(";");
                    try (Statement stmt = conn.createStatement()) {
                        for (String sentencia : sentencias) {
                            String s = sentencia.trim();
                            if (!s.isEmpty() && !s.startsWith("--") && !s.startsWith("/*")) {
                                try { stmt.execute(s); } catch (Exception ex) {
                                    // Ignorar errores menores (ej: comentarios, whitespace)
                                    System.err.println("Advertencia SQL: " + ex.getMessage());
                                }
                            }
                        }
                    }
                    conn.commit();
                }
                return null;
            }

            @Override
            protected void done() {
                btnCrearBD.setEnabled(true);
                btnProbar.setEnabled(true);
                try {
                    get();
                    lblResultadoPrueba.setText("✅ Base de datos '" + txtBaseDatos.getText().trim() + "' creada correctamente. Ahora probá la conexión.");
                    lblResultadoPrueba.setForeground(Estilo.EXITO);
                } catch (Exception ex) {
                    lblResultadoPrueba.setText("<html><center>❌ Error al crear BD: " + ex.getMessage() + "</center></html>");
                    lblResultadoPrueba.setForeground(Estilo.PELIGRO);
                }
            }
        };
        worker.execute();
    }

    /** Guarda la configuración, reconfigura ConexionMySQL y llama al callback. */
    private void accionGuardarYContinuar(ActionEvent e) {
        if (!conexionProbada) {
            lblResultadoPrueba.setText("⚠ Primero probá la conexión para confirmar que funciona.");
            lblResultadoPrueba.setForeground(Estilo.ALERTA);
            return;
        }
        try {
            String host = txtHost.getText().trim();
            int port    = Integer.parseInt(txtPuerto.getText().trim());
            String db   = txtBaseDatos.getText().trim();
            String usr  = txtUsuario.getText().trim();
            String pass = new String(txtPassword.getPassword());
            ConexionMySQL.reconfigurar(host, port, db, usr, pass);
            dispose();
            if (onConectado != null) onConectado.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== INICIO ====================

    /**
     * Lanza el proceso de verificación asíncrona.
     * Si la DB conecta → llama a onConectado.
     * Si falla → muestra el panel de configuración.
     */
    public void iniciarVerificacion() {
        setVisible(true);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String errorMsg = "";
            @Override
            protected Boolean doInBackground() throws Exception {
                Thread.sleep(1200); // Mínimo splash visible
                ConexionMySQL.verificarConexion();
                return true;
            }
            @Override
            protected void done() {
                try {
                    get();
                    // ✅ Conexión exitosa
                    lblEstadoVerificacion.setText("✅ Conexión establecida. Cargando sistema...");
                    lblIconoEstado.setText("✅");
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    Timer timer = new Timer(800, ev -> {
                        dispose();
                        if (onConectado != null) onConectado.run();
                    });
                    timer.setRepeats(false);
                    timer.start();
                } catch (Exception ex) {
                    // ❌ Conexión fallida
                    lblEstadoVerificacion.setText("❌ No se pudo conectar a MySQL.");
                    lblIconoEstado.setText("❌");
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(0);

                    // Transición al panel de configuración tras 1.5 seg
                    Timer timer = new Timer(1500, ev -> {
                        // Pre-rellenar campos con la config actual si existe
                        preRellenarCampos();
                        cardLayout.show(panelPrincipal, PANEL_CONFIG);
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        };
        worker.execute();
    }

    private void preRellenarCampos() {
        // Intentar extraer valores de la URL actual si existe
        String urlActual = ConexionMySQL.getUrl();
        if (urlActual != null) {
            try {
                // jdbc:mysql://HOST:PORT/DB?...
                String sin = urlActual.replace("jdbc:mysql://", "");
                String hostPort = sin.split("/")[0];
                String[] partes = hostPort.split(":");
                txtHost.setText(partes[0]);
                txtPuerto.setText(partes.length > 1 ? partes[1] : "3306");
                String db = sin.split("/")[1].split("\\?")[0];
                txtBaseDatos.setText(db);
            } catch (Exception ignored) {}
        }
        if (ConexionMySQL.getUser() != null) txtUsuario.setText(ConexionMySQL.getUser());
    }

    // ==================== HELPERS DE ESTILO ====================

    private JLabel lblConfig(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Estilo.CHICO);
        lbl.setForeground(new Color(0xAEC6D8));
        return lbl;
    }

    private JTextField campoDB(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        estilizarCampoOscuro(tf);
        return tf;
    }

    private void estilizarCampoOscuro(JTextField tf) {
        tf.setFont(Estilo.CUERPO);
        tf.setBackground(new Color(0x1E4A70));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x4A7FA5), 1),
            new EmptyBorder(7, 10, 7, 10)
        ));
    }

    private void estilizarBotonSetup(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(11, 16, 11, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    public void setOnConectado(Runnable r) { this.onConectado = r; }
}
