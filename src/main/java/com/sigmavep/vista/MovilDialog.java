package com.sigmavep.vista;

import com.sigmavep.modelo.entidad.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * JDialog para dar de alta o editar un móvil.
 *
 * @author Mateo German Ruiz Díaz
 */
public class MovilDialog extends JDialog {

    private JTextField txtNumeroInterno, txtPatente, txtMarca, txtModelo, txtAnio;
    private JComboBox<String> cmbDependencia, cmbEstado;
    private JButton btnGuardar, btnCancelar;
    private boolean confirmado = false;

    // IDs para mapeo
    private int[] idsDependencia;
    private int[] idsEstado;

    public MovilDialog(Frame parent, String titulo) {
        super(parent, titulo, true);
        setSize(480, 460);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));
        setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título del diálogo
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitulo = Estilo.titulo(getTitle());
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        // Campos
        row = agregarCampo(panel, gbc, "N° Interno:", row);
        txtNumeroInterno = new JTextField();
        Estilo.estilizarCampo(txtNumeroInterno);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(txtNumeroInterno, gbc);

        row = agregarCampo(panel, gbc, "Patente:", row);
        txtPatente = new JTextField();
        Estilo.estilizarCampo(txtPatente);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(txtPatente, gbc);

        row = agregarCampo(panel, gbc, "Marca:", row);
        txtMarca = new JTextField();
        Estilo.estilizarCampo(txtMarca);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(txtMarca, gbc);

        row = agregarCampo(panel, gbc, "Modelo:", row);
        txtModelo = new JTextField();
        Estilo.estilizarCampo(txtModelo);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(txtModelo, gbc);

        row = agregarCampo(panel, gbc, "Año:", row);
        txtAnio = new JTextField();
        Estilo.estilizarCampo(txtAnio);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(txtAnio, gbc);

        row = agregarCampo(panel, gbc, "Dependencia:", row);
        cmbDependencia = new JComboBox<>();
        Estilo.estilizarCombo(cmbDependencia);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(cmbDependencia, gbc);

        row = agregarCampo(panel, gbc, "Estado:", row);
        cmbEstado = new JComboBox<>();
        Estilo.estilizarCombo(cmbEstado);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(cmbEstado, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 5, 5, 5);
        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBtns.setOpaque(false);

        btnCancelar = Estilo.botonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        panelBtns.add(btnCancelar);

        btnGuardar = Estilo.botonPrimario("Guardar");
        btnGuardar.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                dispose();
            }
        });
        panelBtns.add(btnGuardar);
        panel.add(panelBtns, gbc);
    }

    private int agregarCampo(JPanel panel, GridBagConstraints gbc, String etiqueta, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Estilo.CUERPO);
        lbl.setForeground(Estilo.TEXTO);
        panel.add(lbl, gbc);
        return row + 1;
    }

    private boolean validar() {
        if (txtNumeroInterno.getText().trim().isEmpty() || txtPatente.getText().trim().isEmpty()
                || txtMarca.getText().trim().isEmpty() || txtModelo.getText().trim().isEmpty()
                || txtAnio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtAnio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El año debe ser un número entero.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public void cargarDependencias(List<Dependencia> dependencias) {
        cmbDependencia.removeAllItems();
        idsDependencia = new int[dependencias.size()];
        for (int i = 0; i < dependencias.size(); i++) {
            cmbDependencia.addItem(dependencias.get(i).getNombre());
            idsDependencia[i] = dependencias.get(i).getId();
        }
    }

    public void cargarEstados(List<EstadoMovil> estados) {
        cmbEstado.removeAllItems();
        idsEstado = new int[estados.size()];
        for (int i = 0; i < estados.size(); i++) {
            cmbEstado.addItem(estados.get(i).getNombre());
            idsEstado[i] = estados.get(i).getId();
        }
    }

    public void precargar(Movil movil) {
        txtNumeroInterno.setText(movil.getNumeroInterno());
        txtPatente.setText(movil.getPatente());
        txtMarca.setText(movil.getMarca());
        txtModelo.setText(movil.getModelo());
        txtAnio.setText(String.valueOf(movil.getAnio()));
    }

    public boolean isConfirmado() { return confirmado; }
    public String getNumeroInterno() { return txtNumeroInterno.getText().trim(); }
    public String getPatente() { return txtPatente.getText().trim().toUpperCase(); }
    public String getMarca() { return txtMarca.getText().trim(); }
    public String getModelo() { return txtModelo.getText().trim(); }
    public int getAnio() { return Integer.parseInt(txtAnio.getText().trim()); }
    public int getIdDependenciaSeleccionada() {
        int idx = cmbDependencia.getSelectedIndex();
        return (idsDependencia != null && idx >= 0) ? idsDependencia[idx] : -1;
    }
    public int getIdEstadoSeleccionado() {
        int idx = cmbEstado.getSelectedIndex();
        return (idsEstado != null && idx >= 0) ? idsEstado[idx] : -1;
    }
}
