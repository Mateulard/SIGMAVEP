package com.sigmavep.vista;

import com.sigmavep.modelo.entidad.Alerta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de alertas de mantenimiento.
 * Las alertas pendientes se resaltan en naranja.
 *
 * @author Mateo German Ruiz Díaz
 */
public class AlertaPanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbEstado;
    private JButton btnProcesar, btnPostergar, btnRefrescar;
    private JLabel lblContador;

    public interface AlertaPanelListener {
        void onProcesar(int idAlerta);
        void onPostergar(int idAlerta);
        void onFiltrar(int idEstadoAlerta);
        void onRefrescar();
    }

    private AlertaPanelListener listener;

    public AlertaPanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Estilo.SECUNDARIO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // ===== ENCABEZADO =====
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lblTitulo = Estilo.titulo("Alertas de Mantenimiento");
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        // Contador de alertas pendientes
        lblContador = new JLabel("0 alertas pendientes");
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblContador.setForeground(Estilo.ALERTA);
        panelHeader.add(lblContador, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // ===== FILTROS =====
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panelFiltros.setBackground(Color.WHITE);
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDE, 1),
            new EmptyBorder(8, 14, 8, 14)
        ));

        panelFiltros.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "Procesada", "Postergada"});
        Estilo.estilizarCombo(cmbEstado);
        panelFiltros.add(cmbEstado);

        JButton btnFiltrar = Estilo.botonPrimario("Filtrar");
        btnFiltrar.addActionListener(e -> {
            if (listener != null) listener.onFiltrar(cmbEstado.getSelectedIndex());
        });
        panelFiltros.add(btnFiltrar);

        btnRefrescar = Estilo.botonSecundario("[F5] Refrescar");
        btnRefrescar.addActionListener(e -> { if (listener != null) listener.onRefrescar(); });
        panelFiltros.add(btnRefrescar);

        // ===== TABLA =====
        String[] columnas = {"ID", "Móvil", "Patente", "Tipo Mantenimiento", "KM Disparo", "Fecha", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        Estilo.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ocultar columna ID
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);

        // Renderer: resaltar pendientes en naranja
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String estado = (String) modeloTabla.getValueAt(row, 6);
                    if ("Pendiente".equals(estado)) {
                        c.setBackground(new Color(0xFEF9E7));
                        c.setForeground(new Color(0xD35400));
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : Estilo.TABLA_ALT);
                        c.setForeground(Estilo.TEXTO);
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));

        // ===== BOTONES ACCIÓN =====
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE),
            new EmptyBorder(8, 10, 8, 10)
        ));

        btnProcesar = Estilo.botonExito("[OK] Procesar Alerta");
        btnProcesar.addActionListener(e -> {
            int id = getIdSeleccionado();
            if (id > 0 && listener != null) listener.onProcesar(id);
        });
        panelAcciones.add(btnProcesar);

        btnPostergar = Estilo.botonAlerta("[>>] Postergar Alerta");
        btnPostergar.addActionListener(e -> {
            int id = getIdSeleccionado();
            if (id > 0 && listener != null) listener.onPostergar(id);
        });
        panelAcciones.add(btnPostergar);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        panelCentral.add(scroll, BorderLayout.CENTER);
        panelCentral.add(panelAcciones, BorderLayout.SOUTH);

        JPanel panelContenido = new JPanel(new BorderLayout(0, 10));
        panelContenido.setOpaque(false);
        panelContenido.add(panelFiltros, BorderLayout.NORTH);
        panelContenido.add(panelCentral, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);
    }

    public void cargarAlertas(List<Alerta> alertas) {
        modeloTabla.setRowCount(0);
        for (Alerta a : alertas) {
            modeloTabla.addRow(new Object[]{
                a.getId(),
                a.getMovil() != null ? "[" + a.getMovil().getNumeroInterno() + "] " + a.getMovil().getMarca() + " " + a.getMovil().getModelo() : "-",
                a.getMovil() != null ? a.getMovil().getPatente() : "-",
                a.getTipoMantenimiento() != null ? a.getTipoMantenimiento().getNombre() : "-",
                String.format("%,d km", a.getKmDisparo()),
                a.getFechaGeneracion() != null ? a.getFechaGeneracion().toLocalDate().toString() : "-",
                a.getEstadoAlerta() != null ? a.getEstadoAlerta().getNombre() : "-"
            });
        }
    }

    public void actualizarContador(int pendientes) {
        lblContador.setText(pendientes + (pendientes == 1 ? " alerta pendiente" : " alertas pendientes"));
        lblContador.setForeground(pendientes > 0 ? Estilo.ALERTA : Estilo.EXITO);
    }

    private int getIdSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccioná una alerta de la tabla primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) modeloTabla.getValueAt(fila, 0);
    }

    public void setListener(AlertaPanelListener listener) { this.listener = listener; }
}
