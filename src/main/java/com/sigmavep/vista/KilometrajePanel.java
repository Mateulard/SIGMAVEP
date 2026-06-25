package com.sigmavep.vista;

import com.sigmavep.modelo.entidad.Movil;
import com.sigmavep.modelo.entidad.RegistroKilometraje;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de registro de kilometraje de móviles.
 *
 * @author Mateo German Ruiz Díaz
 */
public class KilometrajePanel extends JPanel {

    private JComboBox<String> cmbMovil;
    private JLabel lblKmActual;
    private JTextField txtNuevoKm;
    private JButton btnRegistrar, btnRefrescar;
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    private int[] idsMovil;

    public interface KilometrajePanelListener {
        void onMovilSeleccionado(int idMovil);
        void onRegistrar(int idMovil, int kmNuevo);
    }
    private KilometrajePanelListener listener;

    public KilometrajePanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.SECUNDARIO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Título
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(Estilo.titulo("Registro de Kilometraje"), BorderLayout.WEST);
        add(panelHeader, BorderLayout.NORTH);

        // Formulario
        JPanel panelForm = Estilo.panelConBorde("Nuevo Registro");
        panelForm.setLayout(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Estilo.BORDE), "Nuevo Registro",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            Estilo.SUBTITULO, Estilo.PRIMARIO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Combo móvil
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Móvil:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbMovil = new JComboBox<>();
        Estilo.estilizarCombo(cmbMovil);
        panelForm.add(cmbMovil, gbc);

        // KM Actual
        gbc.gridx = 2; gbc.weightx = 0;
        panelForm.add(new JLabel("KM Actual:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0;
        lblKmActual = new JLabel("—");
        lblKmActual.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKmActual.setForeground(Estilo.PRIMARIO);
        panelForm.add(lblKmActual, gbc);

        // Nuevo KM
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelForm.add(new JLabel("Nuevo KM:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtNuevoKm = new JTextField();
        Estilo.estilizarCampo(txtNuevoKm);
        panelForm.add(txtNuevoKm, gbc);

        // Botón
        gbc.gridx = 2; gbc.gridwidth = 2; gbc.weightx = 0;
        btnRegistrar = Estilo.botonPrimario("Registrar KM");
        btnRegistrar.addActionListener(e -> registrar());
        panelForm.add(btnRegistrar, gbc);

        // Historial
        JPanel panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBackground(Color.WHITE);
        panelHistorial.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDE),
            new EmptyBorder(0, 0, 0, 0)
        ));

        JLabel lblHistorial = Estilo.subtitulo("Historial de Kilometraje");
        lblHistorial.setBorder(new EmptyBorder(10, 14, 10, 14));
        panelHistorial.add(lblHistorial, BorderLayout.NORTH);

        String[] cols = {"ID", "Fecha/Hora", "KM Anterior", "KM Nuevo", "Diferencia", "Usuario"};
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

        // Eventos
        cmbMovil.addActionListener(e -> {
            int idx = cmbMovil.getSelectedIndex();
            if (idx >= 0 && idsMovil != null && listener != null) {
                listener.onMovilSeleccionado(idsMovil[idx]);
            }
        });
    }

    private void registrar() {
        int idx = cmbMovil.getSelectedIndex();
        if (idx < 0 || idsMovil == null) {
            JOptionPane.showMessageDialog(this, "Seleccioná un móvil.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String kmTxt = txtNuevoKm.getText().trim();
        if (kmTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresá el nuevo kilometraje.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int km = Integer.parseInt(kmTxt);
            if (listener != null) listener.onRegistrar(idsMovil[idx], km);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El kilometraje debe ser un número entero.", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void cargarMoviles(List<Movil> moviles) {
        // Deshabilitar listener mientras se carga para evitar disparo con ID=0
        var actionListeners = cmbMovil.getActionListeners();
        for (var al : actionListeners) cmbMovil.removeActionListener(al);

        cmbMovil.removeAllItems();
        idsMovil = new int[moviles.size()];
        for (int i = 0; i < moviles.size(); i++) {
            Movil m = moviles.get(i);
            idsMovil[i] = m.getId(); // PRIMERO el ID, LUEGO el addItem
            cmbMovil.addItem("[" + m.getNumeroInterno() + "] " + m.getPatente() + " - " + m.getMarca() + " " + m.getModelo());
        }

        // Re-habilitar listeners
        for (var al : actionListeners) cmbMovil.addActionListener(al);

        // Disparar manualmente para el primer item si hay moviles
        if (!moviles.isEmpty() && listener != null) {
            listener.onMovilSeleccionado(idsMovil[0]);
        }
    }

    public void mostrarKmActual(int km) {
        lblKmActual.setText(String.format("%,d km", km));
    }

    public void cargarHistorial(List<RegistroKilometraje> registros) {
        modeloHistorial.setRowCount(0);
        for (RegistroKilometraje r : registros) {
            modeloHistorial.addRow(new Object[]{
                r.getId(),
                r.getFechaHora() != null ? r.getFechaHora().toString().replace("T", " ").substring(0, 16) : "-",
                String.format("%,d", r.getKmAnterior()),
                String.format("%,d", r.getKmNuevo()),
                String.format("+%,d", r.getKmNuevo() - r.getKmAnterior()),
                r.getUsuario() != null ? r.getUsuario().getNombreCompleto() : "-"
            });
        }
    }

    public void limpiarKm() {
        txtNuevoKm.setText("");
    }

    public void setListener(KilometrajePanelListener listener) { this.listener = listener; }
}
