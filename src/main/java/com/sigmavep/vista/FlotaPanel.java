package com.sigmavep.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de resumen de flota policial (dashboard).
 * Muestra tarjetas de estadísticas y tabla por zona.
 *
 * @author Mateo German Ruiz Díaz
 */
public class FlotaPanel extends JPanel {

    private JLabel lblTotalMoviles, lblEnServicio, lblFueraServicio, lblAlertasPendientes;
    private DefaultTableModel modeloTabla;
    private JButton btnRefrescar;

    public interface FlotaPanelListener {
        void onRefrescar();
    }
    private FlotaPanelListener listener;

    public FlotaPanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.SECUNDARIO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Encabezado
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.setBorder(new EmptyBorder(0, 0, 12, 0));
        panelHeader.add(Estilo.titulo("Estado General de Flota"), BorderLayout.WEST);
        btnRefrescar = Estilo.botonSecundario("↻ Actualizar");
        btnRefrescar.addActionListener(e -> { if (listener != null) listener.onRefrescar(); });
        panelHeader.add(btnRefrescar, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // Tarjetas de resumen
        JPanel panelTarjetas = new JPanel(new GridLayout(1, 4, 16, 0));
        panelTarjetas.setOpaque(false);

        lblTotalMoviles = new JLabel("—");
        lblEnServicio = new JLabel("—");
        lblFueraServicio = new JLabel("—");
        lblAlertasPendientes = new JLabel("—");

        panelTarjetas.add(crearTarjeta("Total Móviles Activos", lblTotalMoviles, Estilo.PRIMARIO));
        panelTarjetas.add(crearTarjeta("En Servicio", lblEnServicio, Estilo.EXITO));
        panelTarjetas.add(crearTarjeta("Fuera de Servicio", lblFueraServicio, Estilo.PELIGRO));
        panelTarjetas.add(crearTarjeta("Alertas Pendientes", lblAlertasPendientes, Estilo.ALERTA));

        // Tabla resumen por zona
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));

        JLabel lblResumen = Estilo.subtitulo("Resumen por Zona");
        lblResumen.setBorder(new EmptyBorder(12, 16, 12, 16));
        panelTabla.add(lblResumen, BorderLayout.NORTH);

        String[] cols = {"Zona", "Sede", "Total Móviles", "En Servicio", "Fuera de Servicio"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloTabla);
        Estilo.estilizarTabla(tabla);
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelContenido = new JPanel(new BorderLayout(0, 16));
        panelContenido.setOpaque(false);
        panelContenido.add(panelTarjetas, BorderLayout.NORTH);
        panelContenido.add(panelTabla, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);
    }

    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDE),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Barra de color superior
        JPanel barra = new JPanel();
        barra.setBackground(color);
        barra.setPreferredSize(new Dimension(0, 5));
        card.add(barra, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Estilo.CHICO);
        lblTitulo.setForeground(new Color(0x7F8C8D));
        contenido.add(lblTitulo, BorderLayout.NORTH);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValor.setForeground(color);
        contenido.add(lblValor, BorderLayout.CENTER);

        card.add(contenido, BorderLayout.CENTER);
        return card;
    }

    public void actualizarEstadisticas(int total, int enServicio, int fueraServicio, int alertasPendientes) {
        lblTotalMoviles.setText(String.valueOf(total));
        lblEnServicio.setText(String.valueOf(enServicio));
        lblFueraServicio.setText(String.valueOf(fueraServicio));
        lblAlertasPendientes.setText(String.valueOf(alertasPendientes));
    }

    public void cargarResumenZonas(List<String[]> filas) {
        modeloTabla.setRowCount(0);
        for (String[] fila : filas) modeloTabla.addRow(fila);
    }

    public void setListener(FlotaPanelListener listener) { this.listener = listener; }
}
