package com.sigmavep.vista;

import com.sigmavep.modelo.entidad.Movil;
import com.sigmavep.modelo.entidad.Novedad;
import com.sigmavep.modelo.entidad.TipoNovedad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel para registrar novedades de móviles.
 *
 * @author Mateo German Ruiz Díaz
 */
public class NovedadPanel extends JPanel {

    private JComboBox<String> cmbMovil, cmbTipo;
    private JTextArea txtDescripcion;
    private JTextField txtKm;
    private JButton btnRegistrar;
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private int[] idsMovil, idsTipo;

    public interface NovedadPanelListener {
        void onRegistrar(int idMovil, int idTipo, String descripcion, int km);
        void onMovilSeleccionado(int idMovil);
    }
    private NovedadPanelListener listener;

    public NovedadPanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.SECUNDARIO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(Estilo.titulo("Novedades de Moviles"), BorderLayout.WEST);
        add(panelHeader, BorderLayout.NORTH);

        // Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDE), new EmptyBorder(16, 16, 16, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelForm.add(lbl("Móvil:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbMovil = new JComboBox<>(); Estilo.estilizarCombo(cmbMovil);
        panelForm.add(cmbMovil, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelForm.add(lbl("Tipo Novedad:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbTipo = new JComboBox<>(); Estilo.estilizarCombo(cmbTipo);
        panelForm.add(cmbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panelForm.add(lbl("KM actual:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtKm = new JTextField(); Estilo.estilizarCampo(txtKm);
        panelForm.add(txtKm, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panelForm.add(lbl("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.ipady = 60; gbc.anchor = GridBagConstraints.WEST;
        txtDescripcion = new JTextArea();
        Estilo.estilizarArea(txtDescripcion);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        panelForm.add(scrollDesc, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.ipady = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        btnRegistrar = Estilo.botonPrimario("Registrar Novedad");
        btnRegistrar.addActionListener(e -> registrar());
        panelForm.add(btnRegistrar, gbc);

        // Historial
        JPanel panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBackground(Color.WHITE);
        panelHistorial.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        JLabel lblH = Estilo.subtitulo("Historial de Novedades");
        lblH.setBorder(new EmptyBorder(10, 14, 10, 14));
        panelHistorial.add(lblH, BorderLayout.NORTH);

        String[] cols = {"ID", "Tipo", "KM", "Descripción", "Fecha", "Usuario"};
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
            if (idx >= 0 && idsMovil != null && listener != null) listener.onMovilSeleccionado(idsMovil[idx]);
        });
    }

    private void registrar() {
        int idxM = cmbMovil.getSelectedIndex();
        int idxT = cmbTipo.getSelectedIndex();
        if (idxM < 0 || idxT < 0) { JOptionPane.showMessageDialog(this, "Seleccioná móvil y tipo.", "Validación", JOptionPane.WARNING_MESSAGE); return; }
        String desc = txtDescripcion.getText().trim();
        if (desc.isEmpty()) { JOptionPane.showMessageDialog(this, "Escribí una descripción.", "Validación", JOptionPane.WARNING_MESSAGE); return; }
        String kmTxt = txtKm.getText().trim();
        if (kmTxt.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingresá el KM.", "Validación", JOptionPane.WARNING_MESSAGE); return; }
        try {
            int km = Integer.parseInt(kmTxt);
            if (listener != null) listener.onRegistrar(idsMovil[idxM], idsTipo[idxT], desc, km);
            txtDescripcion.setText(""); txtKm.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El KM debe ser un número.", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JLabel lbl(String t) { JLabel l = new JLabel(t); l.setFont(Estilo.CUERPO); l.setForeground(Estilo.TEXTO); return l; }

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

    public void cargarTipos(List<TipoNovedad> tipos) {
        cmbTipo.removeAllItems(); idsTipo = new int[tipos.size()];
        for (int i = 0; i < tipos.size(); i++) { cmbTipo.addItem(tipos.get(i).getNombre()); idsTipo[i] = tipos.get(i).getId(); }
    }

    public void cargarHistorial(List<Novedad> novedades) {
        modeloHistorial.setRowCount(0);
        for (Novedad n : novedades) {
            modeloHistorial.addRow(new Object[]{
                n.getId(),
                n.getTipoNovedad() != null ? n.getTipoNovedad().getNombre() : "-",
                String.format("%,d km", n.getKmNovedad()),
                n.getDescripcion(),
                n.getFechaHora() != null ? n.getFechaHora().toLocalDate().toString() : "-",
                n.getUsuario() != null ? n.getUsuario().getNombreCompleto() : "-"
            });
        }
    }

    public void setListener(NovedadPanelListener listener) { this.listener = listener; }
}
