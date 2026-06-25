package com.sigmavep.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de reportes con 4 tabs y exportación a CSV.
 *
 * @author Mateo German Ruiz Díaz
 */
public class ReportePanel extends JPanel {

    private DefaultTableModel modeloMovilesPorZona, modeloAlertasPendientes, modeloHistorialServicios, modeloKmRecorridos;
    private JTabbedPane tabs;

    public interface ReportePanelListener {
        void onExportarMovilesPorZona();
        void onExportarAlertasPendientes();
        void onExportarHistorialServicios();
        void onExportarKmRecorridos();
        void onRefrescar();
    }
    private ReportePanelListener listener;

    public ReportePanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Estilo.SECUNDARIO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(Estilo.titulo("Reportes del Sistema"), BorderLayout.WEST);
        JButton btnRefrescar = Estilo.botonSecundario("↻ Refrescar Todo");
        btnRefrescar.addActionListener(e -> { if (listener != null) listener.onRefrescar(); });
        panelHeader.add(btnRefrescar, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(Estilo.CUERPO);
        tabs.setBackground(Color.WHITE);

        // Tab 1: Móviles por Zona
        String[] colsZona = {"Zona", "Sede", "N° Interno", "Patente", "Marca", "Modelo", "KM", "Estado"};
        modeloMovilesPorZona = crearModelo(colsZona);
        tabs.addTab("Móviles por Zona", crearTabPanel(new JTable(modeloMovilesPorZona), "Exportar CSV", e -> { if (listener != null) listener.onExportarMovilesPorZona(); }));

        // Tab 2: Alertas Pendientes
        String[] colsAlertas = {"ID", "Patente", "Móvil", "Tipo Mantenimiento", "KM Disparo", "Fecha Generación"};
        modeloAlertasPendientes = crearModelo(colsAlertas);
        tabs.addTab("Alertas Pendientes", crearTabPanel(new JTable(modeloAlertasPendientes), "Exportar CSV", e -> { if (listener != null) listener.onExportarAlertasPendientes(); }));

        // Tab 3: Historial Servicios
        String[] colsServ = {"Patente", "Móvil", "Tipo Mantenimiento", "KM Servicio", "Fecha", "Observaciones", "Usuario"};
        modeloHistorialServicios = crearModelo(colsServ);
        tabs.addTab("Historial de Servicios", crearTabPanel(new JTable(modeloHistorialServicios), "Exportar CSV", e -> { if (listener != null) listener.onExportarHistorialServicios(); }));

        // Tab 4: KM Recorridos
        String[] colsKm = {"N° Interno", "Patente", "Marca", "Modelo", "KM Actual"};
        modeloKmRecorridos = crearModelo(colsKm);
        tabs.addTab("KM Recorridos", crearTabPanel(new JTable(modeloKmRecorridos), "Exportar CSV", e -> { if (listener != null) listener.onExportarKmRecorridos(); }));

        add(tabs, BorderLayout.CENTER);
    }

    private DefaultTableModel crearModelo(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JPanel crearTabPanel(JTable tabla, String btnTexto, java.awt.event.ActionListener accion) {
        Estilo.estilizarTabla(tabla);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE));
        JButton btn = Estilo.botonExito("⬇ " + btnTexto);
        btn.addActionListener(accion);
        barra.add(btn);
        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // ===== MÉTODOS DE CARGA =====

    public void cargarMovilesPorZona(List<String[]> datos) { cargarEnModelo(modeloMovilesPorZona, datos); }
    public void cargarAlertasPendientes(List<String[]> datos) { cargarEnModelo(modeloAlertasPendientes, datos); }
    public void cargarHistorialServicios(List<String[]> datos) { cargarEnModelo(modeloHistorialServicios, datos); }
    public void cargarKmRecorridos(List<String[]> datos) { cargarEnModelo(modeloKmRecorridos, datos); }

    private void cargarEnModelo(DefaultTableModel modelo, List<String[]> datos) {
        modelo.setRowCount(0);
        for (String[] fila : datos) modelo.addRow(fila);
    }

    public void setListener(ReportePanelListener listener) { this.listener = listener; }
}
