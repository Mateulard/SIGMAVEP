package com.sigmavep.vista;

import com.sigmavep.modelo.entidad.Rol;
import com.sigmavep.modelo.entidad.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * JDialog para dar de alta o editar un usuario.
 *
 * @author Mateo German Ruiz Díaz
 */
public class UsuarioDialog extends JDialog {

    private JTextField txtNombre, txtApellido, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JCheckBox chkActivo;
    private JButton btnGuardar, btnCancelar;
    private boolean confirmado = false;
    private int[] idsRol;
    private boolean editMode = false;

    public UsuarioDialog(Frame parent, String titulo) {
        super(parent, titulo, true);
        setSize(420, 400);
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
        gbc.insets = new Insets(6, 5, 6, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(Estilo.titulo(getTitle()), gbc);
        gbc.gridwidth = 1;

        int row = 1;
        row = agregarCampoLabel(panel, gbc, "Nombre:", row);
        txtNombre = new JTextField(); Estilo.estilizarCampo(txtNombre);
        gbc.gridx = 1; gbc.gridy = row - 1; panel.add(txtNombre, gbc);

        row = agregarCampoLabel(panel, gbc, "Apellido:", row);
        txtApellido = new JTextField(); Estilo.estilizarCampo(txtApellido);
        gbc.gridx = 1; gbc.gridy = row - 1; panel.add(txtApellido, gbc);

        row = agregarCampoLabel(panel, gbc, "Username:", row);
        txtUsername = new JTextField(); Estilo.estilizarCampo(txtUsername);
        gbc.gridx = 1; gbc.gridy = row - 1; panel.add(txtUsername, gbc);

        row = agregarCampoLabel(panel, gbc, "Contraseña:", row);
        txtPassword = new JPasswordField(); Estilo.estilizarCampo(txtPassword);
        gbc.gridx = 1; gbc.gridy = row - 1; panel.add(txtPassword, gbc);

        row = agregarCampoLabel(panel, gbc, "Rol:", row);
        cmbRol = new JComboBox<>(); Estilo.estilizarCombo(cmbRol);
        gbc.gridx = 1; gbc.gridy = row - 1; panel.add(cmbRol, gbc);

        row = agregarCampoLabel(panel, gbc, "Activo:", row);
        chkActivo = new JCheckBox(); chkActivo.setSelected(true); chkActivo.setBackground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = row - 1; panel.add(chkActivo, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.insets = new Insets(16, 5, 5, 5);
        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBtns.setOpaque(false);
        btnCancelar = Estilo.botonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        panelBtns.add(btnCancelar);
        btnGuardar = Estilo.botonPrimario("Guardar");
        btnGuardar.addActionListener(e -> { if (validar()) { confirmado = true; dispose(); } });
        panelBtns.add(btnGuardar);
        panel.add(panelBtns, gbc);
    }

    private int agregarCampoLabel(JPanel panel, GridBagConstraints gbc, String etiqueta, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(etiqueta); lbl.setFont(Estilo.CUERPO); lbl.setForeground(Estilo.TEXTO);
        panel.add(lbl, gbc);
        return row + 1;
    }

    private boolean validar() {
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty()
                || txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete nombre, apellido y username.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!editMode && new String(txtPassword.getPassword()).length() < 6) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public void cargarRoles(List<Rol> roles) {
        cmbRol.removeAllItems(); idsRol = new int[roles.size()];
        for (int i = 0; i < roles.size(); i++) { cmbRol.addItem(roles.get(i).getNombre()); idsRol[i] = roles.get(i).getId(); }
    }

    public void precargar(Usuario usuario) {
        editMode = true;
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtUsername.setText(usuario.getUsername());
        chkActivo.setSelected(usuario.isActivo());
        txtPassword.setToolTipText("Dejar en blanco para no cambiar la contraseña");
    }

    public boolean isConfirmado() { return confirmado; }
    public String getNombre() { return txtNombre.getText().trim(); }
    public String getApellido() { return txtApellido.getText().trim(); }
    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public boolean isActivo() { return chkActivo.isSelected(); }
    public int getIdRolSeleccionado() {
        int idx = cmbRol.getSelectedIndex(); return (idsRol != null && idx >= 0) ? idsRol[idx] : -1;
    }
    public boolean isEditMode() { return editMode; }
}
