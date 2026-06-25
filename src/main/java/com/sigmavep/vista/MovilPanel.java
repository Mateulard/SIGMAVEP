package com.sigmavep.vista;

import com.sigmavep.modelo.entidad.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de vehículos (móviles) policiales.
 * Muestra tabla de móviles con filtros y botones CRUD.
 *
 * @author Mateo German Ruiz Díaz
 */
public class MovilPanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private JComboBox<String> cmbZona;
    private JComboBox<String> cmbEstado;
    private JButton btnNuevo, btnEditar, btnBaja, btnActualizarKm, btnRefrescar;

    // Listeners
    public interface MovilPanelListener {
        void onNuevo();
        void onEditar(int idMovil);
        void onBaja(int idMovil);
        void onActualizarKm(int idMovil);
        void onFiltrar(String patente, int idZona, int idEstado);
        void onRefrescar();
    }

    private MovilPanelListener listener;

    public MovilPanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Estilo.SECUNDARIO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // ===== ENCABEZADO =====
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel lblTitulo = Estilo.titulo("Gestion de Moviles");
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        btnNuevo = Estilo.botonExito("+ Nuevo Móvil");
        btnNuevo.addActionListener(e -> { if (listener != null) listener.onNuevo(); });
        panelHeader.add(btnNuevo, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // ===== FILTROS =====
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panelFiltros.setBackground(Color.WHITE);
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDE, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));

        panelFiltros.add(new JLabel("Patente:"));
        txtBusqueda = new JTextField(10);
        Estilo.estilizarCampo(txtBusqueda);
        panelFiltros.add(txtBusqueda);

        panelFiltros.add(new JLabel("Zona:"));
        cmbZona = new JComboBox<>(new String[]{"Todas"});
        Estilo.estilizarCombo(cmbZona);
        panelFiltros.add(cmbZona);

        panelFiltros.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{"Todos"});
        Estilo.estilizarCombo(cmbEstado);
        panelFiltros.add(cmbEstado);

        JButton btnFiltrar = Estilo.botonPrimario("Buscar");
        btnFiltrar.addActionListener(e -> filtrar());
        panelFiltros.add(btnFiltrar);

        btnRefrescar = Estilo.botonSecundario("[F5] Refrescar");
        btnRefrescar.addActionListener(e -> { if (listener != null) listener.onRefrescar(); });
        panelFiltros.add(btnRefrescar);

        // ===== TABLA =====
        String[] columnas = {"ID", "N° Interno", "Patente", "Marca", "Modelo", "Año", "KM Actual", "Dependencia", "Zona", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        Estilo.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Ocultar columna ID
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));
        scroll.setBackground(Color.WHITE);

        // ===== BOTONES DE ACCIÓN =====
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE),
            new EmptyBorder(8, 10, 8, 10)
        ));

        btnEditar = Estilo.botonPrimario(">> Editar");
        btnEditar.addActionListener(e -> {
            int id = getIdSeleccionado();
            if (id > 0 && listener != null) listener.onEditar(id);
        });
        panelAcciones.add(btnEditar);

        btnBaja = Estilo.botonPeligro("[X] Dar de Baja");
        btnBaja.addActionListener(e -> {
            int id = getIdSeleccionado();
            if (id > 0 && listener != null) listener.onBaja(id);
        });
        panelAcciones.add(btnBaja);

        btnActualizarKm = Estilo.botonAlerta("[KM] Actualizar KM");
        btnActualizarKm.addActionListener(e -> {
            int id = getIdSeleccionado();
            if (id > 0 && listener != null) listener.onActualizarKm(id);
        });
        panelAcciones.add(btnActualizarKm);

        // ===== PANEL CENTRAL =====
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));
        panelCentral.add(scroll, BorderLayout.CENTER);
        panelCentral.add(panelAcciones, BorderLayout.SOUTH);

        JPanel panelContenido = new JPanel(new BorderLayout(0, 10));
        panelContenido.setOpaque(false);
        panelContenido.add(panelFiltros, BorderLayout.NORTH);
        panelContenido.add(panelCentral, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);
    }

    public void cargarMoviles(List<Movil> moviles) {
        modeloTabla.setRowCount(0);
        for (Movil m : moviles) {
            modeloTabla.addRow(new Object[]{
                m.getId(),
                m.getNumeroInterno(),
                m.getPatente(),
                m.getMarca(),
                m.getModelo(),
                m.getAnio(),
                String.format("%,d km", m.getKmActual()),
                m.getDependencia() != null ? m.getDependencia().getNombre() : "-",
                m.getDependencia() != null && m.getDependencia().getZona() != null
                    ? m.getDependencia().getZona().getNombre() : "-",
                m.getEstadoMovil() != null ? m.getEstadoMovil().getNombre() : "-"
            });
        }
    }

    public void cargarZonas(List<Zona> zonas) {
        cmbZona.removeAllItems();
        cmbZona.addItem("Todas");
        for (Zona z : zonas) cmbZona.addItem(z.getNombre());
    }

    public void cargarEstados(List<EstadoMovil> estados) {
        cmbEstado.removeAllItems();
        cmbEstado.addItem("Todos");
        for (EstadoMovil e : estados) cmbEstado.addItem(e.getNombre());
    }

    private void filtrar() {
        if (listener != null) {
            String patente = txtBusqueda.getText().trim();
            int idZona = cmbZona.getSelectedIndex(); // 0 = todos
            int idEstado = cmbEstado.getSelectedIndex();
            listener.onFiltrar(patente, idZona, idEstado);
        }
    }

    private int getIdSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccioná un móvil de la tabla primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) modeloTabla.getValueAt(fila, 0);
    }

    public void setListener(MovilPanelListener listener) {
        this.listener = listener;
    }

    public JTable getTabla() { return tabla; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
}
