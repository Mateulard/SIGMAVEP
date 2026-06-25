package com.sigmavep.vista;

import com.sigmavep.modelo.entidad.Rol;
import com.sigmavep.modelo.entidad.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de usuarios del sistema (solo Administrador).
 *
 * @author Mateo German Ruiz Díaz
 */
public class UsuarioPanel extends JPanel {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevo, btnEditar, btnDesactivar;

    public interface UsuarioPanelListener {
        void onNuevo();
        void onEditar(int idUsuario);
        void onDesactivar(int idUsuario);
    }
    private UsuarioPanelListener listener;

    public UsuarioPanel() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Estilo.SECUNDARIO);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(Estilo.titulo("Gestion de Usuarios"), BorderLayout.WEST);
        btnNuevo = Estilo.botonExito("+ Nuevo Usuario");
        btnNuevo.addActionListener(e -> { if (listener != null) listener.onNuevo(); });
        panelHeader.add(btnNuevo, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"ID", "Username", "Nombre", "Apellido", "Rol", "Activo"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        Estilo.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));

        // Botones de acción
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE),
            new EmptyBorder(8, 10, 8, 10)
        ));

        btnEditar = Estilo.botonPrimario("✎ Editar");
        btnEditar.addActionListener(e -> {
            int id = getIdSeleccionado(); if (id > 0 && listener != null) listener.onEditar(id);
        });
        panelAcciones.add(btnEditar);

        btnDesactivar = Estilo.botonPeligro("✖ Desactivar");
        btnDesactivar.addActionListener(e -> {
            int id = getIdSeleccionado(); if (id > 0 && listener != null) listener.onDesactivar(id);
        });
        panelAcciones.add(btnDesactivar);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        panelTabla.add(scroll, BorderLayout.CENTER);
        panelTabla.add(panelAcciones, BorderLayout.SOUTH);
        panelTabla.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        add(panelTabla, BorderLayout.CENTER);
    }

    public void cargarUsuarios(List<Usuario> usuarios) {
        modeloTabla.setRowCount(0);
        for (Usuario u : usuarios) {
            modeloTabla.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getNombre(), u.getApellido(),
                u.getRol() != null ? u.getRol().getNombre() : "-",
                u.isActivo() ? "✔ Activo" : "✖ Inactivo"
            });
        }
    }

    private int getIdSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Seleccioná un usuario.", "Sin selección", JOptionPane.WARNING_MESSAGE); return -1; }
        return (int) modeloTabla.getValueAt(fila, 0);
    }

    public void setListener(UsuarioPanelListener listener) { this.listener = listener; }
}
