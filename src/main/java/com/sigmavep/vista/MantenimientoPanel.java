package com.sigmavep.vista;

import com.sigmavep.modelo.entidad.Movil;
import com.sigmavep.modelo.entidad.ServicioRealizado;
import com.sigmavep.modelo.entidad.TipoMantenimiento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel para registrar servicios de mantenimiento realizados.
 *
 * @author Mateo German Ruiz Díaz
 */
public class MantenimientoPanel extends JPanel {

    private JComboBox<String> cmbMovil, cmbTipo;
    private JTextField txtKmServicio, txtObservaciones;
    private JButton btnRegistrar;
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private int[] idsMovil, idsTipo;

    public interface MantenimientoPanelListener {
        void onRegistrar(int idMovil, int idTipo, int kmServicio, String observaciones);
        void onMovilSeleccionado(int idMovil);
    }
    private MantenimientoPanelListener listener;

    public MantenimientoPanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.SECUNDARIO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(Estilo.titulo("Servicios de Mantenimiento"), BorderLayout.WEST);
        add(panelHeader, BorderLayout.NORTH);

        // Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDE),
            new EmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(labelField("Móvil:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbMovil = new JComboBox<>();
        Estilo.estilizarCombo(cmbMovil);
        panelForm.add(cmbMovil, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelForm.add(labelField("Tipo Mantenimiento:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbTipo = new JComboBox<>();
        Estilo.estilizarCombo(cmbTipo);
        panelForm.add(cmbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelForm.add(labelField("KM al servicio:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtKmServicio = new JTextField();
        Estilo.estilizarCampo(txtKmServicio);
        panelForm.add(txtKmServicio, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panelForm.add(labelField("Observaciones:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtObservaciones = new JTextField();
        Estilo.estilizarCampo(txtObservaciones);
        panelForm.add(txtObservaciones, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        btnRegistrar = Estilo.botonPrimario("Registrar Servicio");
        btnRegistrar.addActionListener(e -> registrar());
        panelForm.add(btnRegistrar, gbc);

        // Historial
        JPanel panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBackground(Color.WHITE);
        panelHistorial.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));

        JLabel lblH = Estilo.subtitulo("Historial de Servicios");
        lblH.setBorder(new EmptyBorder(10, 14, 10, 14));
        panelHistorial.add(lblH, BorderLayout.NORTH);

        String[] cols = {"ID", "Tipo Mantenimiento", "KM Servicio", "Fecha", "Observaciones", "Usuario"};
        modeloHistorial = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        Estilo.estilizarTabla(tablaHistorial);
        tablaHistorial.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaHistorial.getColumnModel().getColumn(0).setMinWidth(0);
        panelHistorial.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        JPanel panelContenido = new JPanel(new BorderLayout(0, 12));
        panelContenido.setOpaque(false);
        panelContenido.add(panelForm, BorderLayout.NORTH);
        panelContenido.add(panelHistorial, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);

        cmbMovil.addActionListener(e -> {
            int idx = cmbMovil.getSelectedIndex();
            if (idx >= 0 && idsMovil != null && listener != null) {
                listener.onMovilSeleccionado(idsMovil[idx]);
            }
        });
    }

    private void registrar() {
        int idxM = cmbMovil.getSelectedIndex();
        int idxT = cmbTipo.getSelectedIndex();
        if (idxM < 0 || idxT < 0) { JOptionPane.showMessageDialog(this, "Seleccioná móvil y tipo.", "Validación", JOptionPane.WARNING_MESSAGE); return; }
        String kmTxt = txtKmServicio.getText().trim();
        if (kmTxt.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresá el KM.", "Validación", JOptionPane.WARNING_MESSAGE); return; }
        try {
            int km = Integer.parseInt(kmTxt);
            if (listener != null) listener.onRegistrar(idsMovil[idxM], idsTipo[idxT], km, txtObservaciones.getText().trim());
            txtKmServicio.setText(""); txtObservaciones.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El KM debe ser un número entero.", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JLabel labelField(String txt) {
        JLabel lbl = new JLabel(txt); lbl.setFont(Estilo.CUERPO); lbl.setForeground(Estilo.TEXTO); return lbl;
    }

    public void cargarMoviles(List<Movil> moviles) {
        var als = cmbMovil.getActionListeners();
        for (var al : als) cmbMovil.removeActionListener(al);
        cmbMovil.removeAllItems();
        idsMovil = new int[moviles.size()];
        for (int i = 0; i < moviles.size(); i++) {
            Movil m = moviles.get(i);
            idsMovil[i] = m.getId();
            cmbMovil.addItem("[" + m.getNumeroInterno() + "] " + m.getPatente());
        }
        for (var al : als) cmbMovil.addActionListener(al);
        if (!moviles.isEmpty() && listener != null) listener.onMovilSeleccionado(idsMovil[0]);
    }

    public void cargarTipos(List<TipoMantenimiento> tipos) {
        cmbTipo.removeAllItems(); idsTipo = new int[tipos.size()];
        for (int i = 0; i < tipos.size(); i++) {
            cmbTipo.addItem(tipos.get(i).getNombre() + " (c/" + tipos.get(i).getLimiteKm() + " km)"); idsTipo[i] = tipos.get(i).getId();
        }
    }

    public void cargarHistorial(List<ServicioRealizado> servicios) {
        modeloHistorial.setRowCount(0);
        for (ServicioRealizado s : servicios) {
            modeloHistorial.addRow(new Object[]{
                s.getId(),
                s.getTipoMantenimiento() != null ? s.getTipoMantenimiento().getNombre() : "-",
                String.format("%,d km", s.getKmServicio()),
                s.getFechaServicio() != null ? s.getFechaServicio().toLocalDate().toString() : "-",
                s.getObservaciones() != null ? s.getObservaciones() : "-",
                s.getUsuario() != null ? s.getUsuario().getNombreCompleto() : "-"
            });
        }
    }

    public void setListener(MantenimientoPanelListener listener) { this.listener = listener; }
}
