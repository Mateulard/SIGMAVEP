package com.sigmavep.controlador;

import com.sigmavep.dao.impl.*;
import com.sigmavep.exepcion.SIGMAVEPException;
import com.sigmavep.modelo.entidad.*;
import com.sigmavep.util.Session;
import com.sigmavep.vista.*;

import javax.swing.*;
import java.util.List;

/**
 * Controlador principal: gestiona la navegación por módulos en MenuPrincipalFrame.
 *
 * @author Mateo German Ruiz Díaz
 */
public class MenuPrincipalControlador {

    private final MenuPrincipalFrame menu;

    // DAOs
    private final MovilDAOImpl movilDAO = new MovilDAOImpl();
    private final AlertaDAOImpl alertaDAO = new AlertaDAOImpl();
    private final ZonaDAOImpl zonaDAO = new ZonaDAOImpl();
    private final EstadoMovilDAOImpl estadoMovilDAO = new EstadoMovilDAOImpl();
    private final EstadoAlertaDAOImpl estadoAlertaDAO = new EstadoAlertaDAOImpl();
    private final TipoMantenimientoDAOImpl tipoMantDAO = new TipoMantenimientoDAOImpl();
    private final TipoNovedadDAOImpl tipoNovDAO = new TipoNovedadDAOImpl();
    private final RegistroKilometrajeDAOImpl regKmDAO = new RegistroKilometrajeDAOImpl();
    private final ServicioRealizadoDAOImpl servicioDAO = new ServicioRealizadoDAOImpl();
    private final NovedadDAOImpl novedadDAO = new NovedadDAOImpl();
    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private final RolDAOImpl rolDAO = new RolDAOImpl();
    private final DependenciaDAOImpl dependenciaDAO = new DependenciaDAOImpl();

    // Panels
    private MovilPanel movilPanel;
    private AlertaPanel alertaPanel;
    private KilometrajePanel kmPanel;
    private MantenimientoPanel mantenimientoPanel;
    private NovedadPanel novedadPanel;
    private FlotaPanel flotaPanel;
    private ReportePanel reportePanel;
    private UsuarioPanel usuarioPanel;

    public MenuPrincipalControlador(MenuPrincipalFrame menu) {
        this.menu = menu;
    }

    public void inicializar() {
        crearPaneles();
        menu.setModuloListener(modulo -> {
            switch (modulo) {
                case MenuPrincipalFrame.MOD_MOVILES:       cargarMoviles();       break;
                case MenuPrincipalFrame.MOD_ALERTAS:       cargarAlertas();       break;
                case MenuPrincipalFrame.MOD_KILOMETRAJE:   cargarKilometraje();   break;
                case MenuPrincipalFrame.MOD_MANTENIMIENTO: cargarMantenimiento(); break;
                case MenuPrincipalFrame.MOD_NOVEDADES:     cargarNovedades();     break;
                case MenuPrincipalFrame.MOD_FLOTA:         cargarFlota();         break;
                case MenuPrincipalFrame.MOD_REPORTES:      cargarReportes();      break;
                case MenuPrincipalFrame.MOD_USUARIOS:      cargarUsuarios();      break;
            }
        });
    }

    private void crearPaneles() {
        // MÓVILES
        movilPanel = new MovilPanel();
        movilPanel.setListener(new MovilPanel.MovilPanelListener() {
            @Override public void onNuevo() { nuevoMovil(); }
            @Override public void onEditar(int id) { editarMovil(id); }
            @Override public void onBaja(int id) { bajaMovil(id); }
            @Override public void onActualizarKm(int id) { actualizarKmRapido(id); }
            @Override public void onFiltrar(String p, int z, int e) { filtrarMoviles(p, z, e); }
            @Override public void onRefrescar() { cargarMoviles(); }
        });
        menu.mostrarPanel(MenuPrincipalFrame.MOD_MOVILES, movilPanel);

        // ALERTAS
        alertaPanel = new AlertaPanel();
        alertaPanel.setListener(new AlertaPanel.AlertaPanelListener() {
            @Override public void onProcesar(int id) { procesarAlerta(id); }
            @Override public void onPostergar(int id) { postergarAlerta(id); }
            @Override public void onFiltrar(int idEstado) { filtrarAlertas(idEstado); }
            @Override public void onRefrescar() { cargarAlertas(); }
        });
        menu.mostrarPanel(MenuPrincipalFrame.MOD_ALERTAS, alertaPanel);

        // KILOMETRAJE
        kmPanel = new KilometrajePanel();
        kmPanel.setListener(new KilometrajePanel.KilometrajePanelListener() {
            @Override public void onMovilSeleccionado(int id) { onMovilKmSeleccionado(id); }
            @Override public void onRegistrar(int idMovil, int kmNuevo) { registrarKm(idMovil, kmNuevo); }
        });
        menu.mostrarPanel(MenuPrincipalFrame.MOD_KILOMETRAJE, kmPanel);

        // MANTENIMIENTO
        mantenimientoPanel = new MantenimientoPanel();
        mantenimientoPanel.setListener(new MantenimientoPanel.MantenimientoPanelListener() {
            @Override public void onRegistrar(int idM, int idT, int km, String obs) { registrarServicio(idM, idT, km, obs); }
            @Override public void onMovilSeleccionado(int id) { onMovilMantenimientoSeleccionado(id); }
        });
        menu.mostrarPanel(MenuPrincipalFrame.MOD_MANTENIMIENTO, mantenimientoPanel);

        // NOVEDADES
        novedadPanel = new NovedadPanel();
        novedadPanel.setListener(new NovedadPanel.NovedadPanelListener() {
            @Override public void onRegistrar(int idM, int idT, String desc, int km) { registrarNovedad(idM, idT, desc, km); }
            @Override public void onMovilSeleccionado(int id) { onMovilNovedadSeleccionado(id); }
        });
        menu.mostrarPanel(MenuPrincipalFrame.MOD_NOVEDADES, novedadPanel);

        // FLOTA
        flotaPanel = new FlotaPanel();
        flotaPanel.setListener(() -> cargarFlota());
        menu.mostrarPanel(MenuPrincipalFrame.MOD_FLOTA, flotaPanel);

        // REPORTES
        reportePanel = new ReportePanel();
        reportePanel.setListener(new ReportePanel.ReportePanelListener() {
            @Override public void onExportarMovilesPorZona()     { exportarCSV("moviles_por_zona"); }
            @Override public void onExportarAlertasPendientes()  { exportarCSV("alertas_pendientes"); }
            @Override public void onExportarHistorialServicios() { exportarCSV("historial_servicios"); }
            @Override public void onExportarKmRecorridos()       { exportarCSV("km_recorridos"); }
            @Override public void onRefrescar()                  { cargarReportes(); }
        });
        menu.mostrarPanel(MenuPrincipalFrame.MOD_REPORTES, reportePanel);

        // USUARIOS (solo admin)
        usuarioPanel = new UsuarioPanel();
        usuarioPanel.setListener(new UsuarioPanel.UsuarioPanelListener() {
            @Override public void onNuevo()              { nuevoUsuario(); }
            @Override public void onEditar(int id)       { editarUsuario(id); }
            @Override public void onDesactivar(int id)   { desactivarUsuario(id); }
        });
        menu.mostrarPanel(MenuPrincipalFrame.MOD_USUARIOS, usuarioPanel);
    }

    // ===== MÓVILES =====

    private void cargarMoviles() {
        menu.mostrarPanelExistente(MenuPrincipalFrame.MOD_MOVILES);
        ejecutarEnHilo(() -> {
            try {
                List<Movil> moviles = movilDAO.listarActivos();
                List<Zona> zonas = zonaDAO.listarTodos();
                List<EstadoMovil> estados = estadoMovilDAO.listarTodos();
                SwingUtilities.invokeLater(() -> {
                    movilPanel.cargarMoviles(moviles);
                    movilPanel.cargarZonas(zonas);
                    movilPanel.cargarEstados(estados);
                });
            } catch (SIGMAVEPException e) { mostrarError("Error al cargar móviles: " + e.getMessage()); }
        });
    }

    private void nuevoMovil() {
        try {
            List<Dependencia> deps = dependenciaDAO.listarTodos();
            List<EstadoMovil> estados = estadoMovilDAO.listarTodos();
            MovilDialog dlg = new MovilDialog(menu, "Alta de Móvil");
            dlg.cargarDependencias(deps);
            dlg.cargarEstados(estados);
            dlg.setVisible(true);
            if (dlg.isConfirmado()) {
                Dependencia dep = deps.stream().filter(d -> d.getId() == dlg.getIdDependenciaSeleccionada()).findFirst().orElse(null);
                EstadoMovil est = estados.stream().filter(e -> e.getId() == dlg.getIdEstadoSeleccionado()).findFirst().orElse(null);
                Movil m = new Movil(dlg.getNumeroInterno(), dlg.getPatente(), dlg.getMarca(), dlg.getModelo(), dlg.getAnio(), dep, est);
                movilDAO.insertar(m);
                JOptionPane.showMessageDialog(menu, "Móvil dado de alta exitosamente.", "Alta", JOptionPane.INFORMATION_MESSAGE);
                cargarMoviles();
            }
        } catch (SIGMAVEPException e) { mostrarError("Error al dar de alta: " + e.getMessage()); }
    }

    private void editarMovil(int id) {
        try {
            Movil movil = movilDAO.buscarPorId(id);
            List<Dependencia> deps = dependenciaDAO.listarTodos();
            List<EstadoMovil> estados = estadoMovilDAO.listarTodos();
            MovilDialog dlg = new MovilDialog(menu, "Editar Móvil");
            dlg.cargarDependencias(deps);
            dlg.cargarEstados(estados);
            dlg.precargar(movil);
            dlg.setVisible(true);
            if (dlg.isConfirmado()) {
                movil.setNumeroInterno(dlg.getNumeroInterno());
                movil.setPatente(dlg.getPatente());
                movil.setMarca(dlg.getMarca());
                movil.setModelo(dlg.getModelo());
                movil.setAnio(dlg.getAnio());
                Dependencia dep = deps.stream().filter(d -> d.getId() == dlg.getIdDependenciaSeleccionada()).findFirst().orElse(null);
                EstadoMovil est = estados.stream().filter(e -> e.getId() == dlg.getIdEstadoSeleccionado()).findFirst().orElse(null);
                movil.setDependencia(dep);
                movil.setEstadoMovil(est);
                movilDAO.actualizar(movil);
                JOptionPane.showMessageDialog(menu, "Móvil actualizado correctamente.", "Edición", JOptionPane.INFORMATION_MESSAGE);
                cargarMoviles();
            }
        } catch (SIGMAVEPException e) { mostrarError("Error al editar: " + e.getMessage()); }
    }

    private void bajaMovil(int id) {
        int conf = JOptionPane.showConfirmDialog(menu, "¿Confirmar baja del móvil?", "Baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                movilDAO.eliminar(id);
                JOptionPane.showMessageDialog(menu, "Móvil dado de baja.", "Baja", JOptionPane.INFORMATION_MESSAGE);
                cargarMoviles();
            } catch (SIGMAVEPException e) { mostrarError("Error al dar de baja: " + e.getMessage()); }
        }
    }

    private void actualizarKmRapido(int id) {
        String input = JOptionPane.showInputDialog(menu, "Ingresá el nuevo kilometraje:", "Actualizar KM", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.isEmpty()) {
            try {
                int km = Integer.parseInt(input.trim());
                Movil movil = movilDAO.buscarPorId(id);
                if (km <= movil.getKmActual()) {
                    mostrarError("El nuevo KM (" + km + ") no puede ser menor o igual al actual (" + movil.getKmActual() + ").");
                    return;
                }
                movilDAO.actualizarKm(id, km);
                verificarAlertas(movil, km);
                JOptionPane.showMessageDialog(menu, "Kilometraje actualizado a " + km + " km.", "KM", JOptionPane.INFORMATION_MESSAGE);
                cargarMoviles();
            } catch (NumberFormatException e) { mostrarError("Ingresá un número entero."); }
            catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        }
    }

    private void filtrarMoviles(String patente, int idxZona, int idxEstado) {
        ejecutarEnHilo(() -> {
            try {
                List<Movil> resultado;
                if (idxZona > 0) {
                    List<Zona> zonas = zonaDAO.listarTodos();
                    int idZona = zonas.get(idxZona - 1).getId();
                    resultado = movilDAO.buscarPorZona(idZona);
                } else if (idxEstado > 0) {
                    List<EstadoMovil> estados = estadoMovilDAO.listarTodos();
                    int idEst = estados.get(idxEstado - 1).getId();
                    resultado = movilDAO.buscarPorEstado(idEst);
                } else {
                    resultado = movilDAO.listarActivos();
                }
                if (!patente.isEmpty()) {
                    final String p = patente.toUpperCase();
                    resultado = resultado.stream().filter(m -> m.getPatente().contains(p))
                        .collect(java.util.stream.Collectors.toList());
                }
                final List<Movil> final_r = resultado;
                SwingUtilities.invokeLater(() -> movilPanel.cargarMoviles(final_r));
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    // ===== ALERTAS =====

    private void cargarAlertas() {
        menu.mostrarPanelExistente(MenuPrincipalFrame.MOD_ALERTAS);
        ejecutarEnHilo(() -> {
            try {
                List<Alerta> alertas = alertaDAO.listarTodos();
                int pendientes = alertaDAO.contarPendientes();
                SwingUtilities.invokeLater(() -> {
                    alertaPanel.cargarAlertas(alertas);
                    alertaPanel.actualizarContador(pendientes);
                });
            } catch (SIGMAVEPException e) { mostrarError("Error al cargar alertas: " + e.getMessage()); }
        });
    }

    private void procesarAlerta(int id) {
        String obs = JOptionPane.showInputDialog(menu, "Observaciones del procesamiento (opcional):", "Procesar Alerta", JOptionPane.QUESTION_MESSAGE);
        if (obs == null) return;
        try {
            Alerta alerta = alertaDAO.buscarPorId(id);
            EstadoAlerta estadoProcesada = new EstadoAlerta(2, "Procesada");
            alerta.setEstadoAlerta(estadoProcesada);
            alerta.setObservaciones(obs.trim().isEmpty() ? null : obs.trim());
            alerta.setUsuario(Session.getUsuarioActual());
            alertaDAO.actualizar(alerta);
            JOptionPane.showMessageDialog(menu, "Alerta procesada correctamente.", "Procesada", JOptionPane.INFORMATION_MESSAGE);
            cargarAlertas();
        } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
    }

    private void postergarAlerta(int id) {
        String obs = JOptionPane.showInputDialog(menu, "Motivo de la postergación:", "Postergar Alerta", JOptionPane.QUESTION_MESSAGE);
        if (obs == null || obs.trim().isEmpty()) { mostrarError("Debés indicar un motivo."); return; }
        try {
            Alerta alerta = alertaDAO.buscarPorId(id);
            EstadoAlerta estadoPostergada = new EstadoAlerta(3, "Postergada");
            alerta.setEstadoAlerta(estadoPostergada);
            alerta.setObservaciones(obs.trim());
            alerta.setUsuario(Session.getUsuarioActual());
            alertaDAO.actualizar(alerta);
            JOptionPane.showMessageDialog(menu, "Alerta postergada.", "Postergada", JOptionPane.INFORMATION_MESSAGE);
            cargarAlertas();
        } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
    }

    private void filtrarAlertas(int idxEstado) {
        ejecutarEnHilo(() -> {
            try {
                List<Alerta> alertas;
                if (idxEstado == 0) alertas = alertaDAO.listarTodos();
                else alertas = alertaDAO.listarPorEstado(idxEstado);
                int pendientes = alertaDAO.contarPendientes();
                SwingUtilities.invokeLater(() -> {
                    alertaPanel.cargarAlertas(alertas);
                    alertaPanel.actualizarContador(pendientes);
                });
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    // ===== KILOMETRAJE =====

    private void cargarKilometraje() {
        menu.mostrarPanelExistente(MenuPrincipalFrame.MOD_KILOMETRAJE);
        ejecutarEnHilo(() -> {
            try {
                List<Movil> moviles = movilDAO.listarActivos();
                SwingUtilities.invokeLater(() -> kmPanel.cargarMoviles(moviles));
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    private void onMovilKmSeleccionado(int id) {
        ejecutarEnHilo(() -> {
            try {
                Movil m = movilDAO.buscarPorId(id);
                List<RegistroKilometraje> hist = regKmDAO.listarPorMovil(id);
                SwingUtilities.invokeLater(() -> {
                    kmPanel.mostrarKmActual(m.getKmActual());
                    kmPanel.cargarHistorial(hist);
                });
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    private void registrarKm(int idMovil, int kmNuevo) {
        try {
            Movil movil = movilDAO.buscarPorId(idMovil);
            if (kmNuevo <= movil.getKmActual()) {
                mostrarError("El nuevo KM (" + kmNuevo + ") no puede ser menor o igual al actual (" + movil.getKmActual() + ").");
                return;
            }
            RegistroKilometraje reg = new RegistroKilometraje(movil, movil.getKmActual(), kmNuevo, Session.getUsuarioActual());
            regKmDAO.insertar(reg);
            movilDAO.actualizarKm(idMovil, kmNuevo);
            verificarAlertas(movil, kmNuevo);
            JOptionPane.showMessageDialog(menu, "KM registrado: " + kmNuevo + " km.", "KM Registrado", JOptionPane.INFORMATION_MESSAGE);
            kmPanel.limpiarKm();
            onMovilKmSeleccionado(idMovil);
        } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
    }

    // ===== MANTENIMIENTO =====

    private void cargarMantenimiento() {
        menu.mostrarPanelExistente(MenuPrincipalFrame.MOD_MANTENIMIENTO);
        ejecutarEnHilo(() -> {
            try {
                List<Movil> moviles = movilDAO.listarActivos();
                List<TipoMantenimiento> tipos = tipoMantDAO.listarTodos();
                SwingUtilities.invokeLater(() -> {
                    mantenimientoPanel.cargarMoviles(moviles);
                    mantenimientoPanel.cargarTipos(tipos);
                });
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    private void onMovilMantenimientoSeleccionado(int id) {
        ejecutarEnHilo(() -> {
            try {
                List<ServicioRealizado> hist = servicioDAO.listarPorMovil(id);
                SwingUtilities.invokeLater(() -> mantenimientoPanel.cargarHistorial(hist));
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    private void registrarServicio(int idMovil, int idTipo, int km, String obs) {
        try {
            Movil movil = movilDAO.buscarPorId(idMovil);
            TipoMantenimiento tipo = tipoMantDAO.buscarPorId(idTipo);
            ServicioRealizado serv = new ServicioRealizado(movil, tipo, km, obs.isEmpty() ? null : obs, Session.getUsuarioActual());
            servicioDAO.insertar(serv);

            // Cerrar alertas pendientes de ese tipo para ese móvil
            List<Alerta> alertas = alertaDAO.listarPorMovil(idMovil);
            EstadoAlerta procesada = new EstadoAlerta(2, "Procesada");
            for (Alerta a : alertas) {
                if (a.getEstadoAlerta().getId() == 1 && a.getTipoMantenimiento().getId() == idTipo) {
                    a.setEstadoAlerta(procesada);
                    a.setObservaciones("Procesada automáticamente al registrar servicio.");
                    a.setUsuario(Session.getUsuarioActual());
                    alertaDAO.actualizar(a);
                }
            }
            JOptionPane.showMessageDialog(menu, "Servicio registrado. Las alertas pendientes del tipo han sido procesadas.", "Servicio", JOptionPane.INFORMATION_MESSAGE);
            onMovilMantenimientoSeleccionado(idMovil);
        } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
    }

    // ===== NOVEDADES =====

    private void cargarNovedades() {
        menu.mostrarPanelExistente(MenuPrincipalFrame.MOD_NOVEDADES);
        ejecutarEnHilo(() -> {
            try {
                List<Movil> moviles = movilDAO.listarActivos();
                List<TipoNovedad> tipos = tipoNovDAO.listarTodos();
                SwingUtilities.invokeLater(() -> {
                    novedadPanel.cargarMoviles(moviles);
                    novedadPanel.cargarTipos(tipos);
                });
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    private void onMovilNovedadSeleccionado(int id) {
        ejecutarEnHilo(() -> {
            try {
                List<Novedad> hist = novedadDAO.listarPorMovil(id);
                SwingUtilities.invokeLater(() -> novedadPanel.cargarHistorial(hist));
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    private void registrarNovedad(int idMovil, int idTipo, String desc, int km) {
        try {
            Movil movil = movilDAO.buscarPorId(idMovil);
            TipoNovedad tipo = tipoNovDAO.buscarPorId(idTipo);
            Novedad n = new Novedad(movil, tipo, desc, km, Session.getUsuarioActual());
            novedadDAO.insertar(n);
            JOptionPane.showMessageDialog(menu, "Novedad registrada.", "Novedad", JOptionPane.INFORMATION_MESSAGE);
            onMovilNovedadSeleccionado(idMovil);
        } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
    }

    // ===== FLOTA =====

    private void cargarFlota() {
        menu.mostrarPanelExistente(MenuPrincipalFrame.MOD_FLOTA);
        ejecutarEnHilo(() -> {
            try {
                List<Movil> activos = movilDAO.listarActivos();
                int total = activos.size();
                int enServicio = (int) activos.stream().filter(m -> m.getEstadoMovil() != null && m.getEstadoMovil().getId() == 1).count();
                int fueraServicio = total - enServicio;
                int alertasPendientes = alertaDAO.contarPendientes();

                List<Zona> zonas = zonaDAO.listarTodos();
                java.util.List<String[]> filas = new java.util.ArrayList<>();
                for (Zona z : zonas) {
                    List<Movil> movZona = movilDAO.buscarPorZona(z.getId());
                    long enServ = movZona.stream().filter(m -> m.getEstadoMovil() != null && m.getEstadoMovil().getId() == 1).count();
                    filas.add(new String[]{z.getNombre(), z.getSede(), String.valueOf(movZona.size()),
                        String.valueOf(enServ), String.valueOf(movZona.size() - enServ)});
                }

                final int t = total, es = enServicio, fs = fueraServicio, ap = alertasPendientes;
                SwingUtilities.invokeLater(() -> {
                    flotaPanel.actualizarEstadisticas(t, es, fs, ap);
                    flotaPanel.cargarResumenZonas(filas);
                });
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    // ===== REPORTES =====

    private void cargarReportes() {
        menu.mostrarPanelExistente(MenuPrincipalFrame.MOD_REPORTES);
        ejecutarEnHilo(() -> {
            try {
                // Móviles por zona
                List<Movil> todosActivos = movilDAO.listarActivos();
                java.util.List<String[]> filasZ = new java.util.ArrayList<>();
                for (Movil m : todosActivos) {
                    filasZ.add(new String[]{
                        m.getDependencia() != null && m.getDependencia().getZona() != null ? m.getDependencia().getZona().getNombre() : "-",
                        m.getDependencia() != null && m.getDependencia().getZona() != null ? m.getDependencia().getZona().getSede() : "-",
                        m.getNumeroInterno(), m.getPatente(), m.getMarca(), m.getModelo(),
                        String.format("%,d", m.getKmActual()),
                        m.getEstadoMovil() != null ? m.getEstadoMovil().getNombre() : "-"
                    });
                }

                // Alertas pendientes
                List<Alerta> alertas = alertaDAO.listarPendientes();
                java.util.List<String[]> filasA = new java.util.ArrayList<>();
                for (Alerta a : alertas) {
                    filasA.add(new String[]{
                        String.valueOf(a.getId()),
                        a.getMovil() != null ? a.getMovil().getPatente() : "-",
                        a.getMovil() != null ? a.getMovil().getNumeroInterno() : "-",
                        a.getTipoMantenimiento() != null ? a.getTipoMantenimiento().getNombre() : "-",
                        String.format("%,d", a.getKmDisparo()),
                        a.getFechaGeneracion() != null ? a.getFechaGeneracion().toLocalDate().toString() : "-"
                    });
                }

                // Historial servicios
                List<ServicioRealizado> servicios = servicioDAO.listarTodos();
                java.util.List<String[]> filasS = new java.util.ArrayList<>();
                for (ServicioRealizado s : servicios) {
                    filasS.add(new String[]{
                        s.getMovil() != null ? s.getMovil().getPatente() : "-",
                        s.getMovil() != null ? s.getMovil().getNumeroInterno() : "-",
                        s.getTipoMantenimiento() != null ? s.getTipoMantenimiento().getNombre() : "-",
                        String.format("%,d", s.getKmServicio()),
                        s.getFechaServicio() != null ? s.getFechaServicio().toLocalDate().toString() : "-",
                        s.getObservaciones() != null ? s.getObservaciones() : "-",
                        s.getUsuario() != null ? s.getUsuario().getNombreCompleto() : "-"
                    });
                }

                // KM Recorridos (ordenado por burbuja)
                List<Movil> movilesOrdenados = com.sigmavep.util.OrdenamientoUtil.ordenarMovilesPorKm(todosActivos);
                java.util.List<String[]> filasK = new java.util.ArrayList<>();
                for (Movil m : movilesOrdenados) {
                    filasK.add(new String[]{m.getNumeroInterno(), m.getPatente(), m.getMarca(), m.getModelo(), String.format("%,d", m.getKmActual())});
                }

                SwingUtilities.invokeLater(() -> {
                    reportePanel.cargarMovilesPorZona(filasZ);
                    reportePanel.cargarAlertasPendientes(filasA);
                    reportePanel.cargarHistorialServicios(filasS);
                    reportePanel.cargarKmRecorridos(filasK);
                });
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    private void exportarCSV(String tipo) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("sigmavep_" + tipo + ".csv"));
        int ret = chooser.showSaveDialog(menu);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                String ruta = chooser.getSelectedFile().getAbsolutePath();
                switch (tipo) {
                    case "alertas_pendientes": {
                        List<Alerta> alertas = alertaDAO.listarPendientes();
                        java.util.List<String[]> datos = new java.util.ArrayList<>();
                        for (Alerta a : alertas) datos.add(new String[]{String.valueOf(a.getId()), a.getMovil().getPatente(), a.getTipoMantenimiento().getNombre(), String.valueOf(a.getKmDisparo()), a.getFechaGeneracion().toLocalDate().toString()});
                        com.sigmavep.util.ArchivoUtil.exportarCSV(ruta, new String[]{"ID", "Patente", "Tipo", "KM", "Fecha"}, datos);
                        break;
                    }
                    default: {
                        List<Movil> moviles = movilDAO.listarActivos();
                        java.util.List<String[]> datos = new java.util.ArrayList<>();
                        for (Movil m : moviles) datos.add(new String[]{m.getNumeroInterno(), m.getPatente(), m.getMarca(), m.getModelo(), String.valueOf(m.getAnio()), String.valueOf(m.getKmActual())});
                        com.sigmavep.util.ArchivoUtil.exportarCSV(ruta, new String[]{"N°Int", "Patente", "Marca", "Modelo", "Año", "KM"}, datos);
                    }
                }
                JOptionPane.showMessageDialog(menu, "CSV exportado en: " + ruta, "Exportado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) { mostrarError("Error al exportar: " + e.getMessage()); }
        }
    }

    // ===== USUARIOS =====

    private void cargarUsuarios() {
        menu.mostrarPanelExistente(MenuPrincipalFrame.MOD_USUARIOS);
        ejecutarEnHilo(() -> {
            try {
                List<Usuario> usuarios = usuarioDAO.listarTodos();
                SwingUtilities.invokeLater(() -> usuarioPanel.cargarUsuarios(usuarios));
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        });
    }

    private void nuevoUsuario() {
        try {
            List<Rol> roles = rolDAO.listarTodos();
            UsuarioDialog dlg = new UsuarioDialog(menu, "Nuevo Usuario");
            dlg.cargarRoles(roles);
            dlg.setVisible(true);
            if (dlg.isConfirmado()) {
                Rol rol = roles.stream().filter(r -> r.getId() == dlg.getIdRolSeleccionado()).findFirst().orElse(null);
                String hash = com.sigmavep.util.HashUtil.sha256(dlg.getPassword());
                Usuario u = new Usuario(dlg.getNombre(), dlg.getApellido(), dlg.getUsername(), hash, rol);
                usuarioDAO.insertar(u);
                JOptionPane.showMessageDialog(menu, "Usuario creado.", "Alta", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            }
        } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
    }

    private void editarUsuario(int id) {
        try {
            Usuario usuario = usuarioDAO.buscarPorId(id);
            List<Rol> roles = rolDAO.listarTodos();
            UsuarioDialog dlg = new UsuarioDialog(menu, "Editar Usuario");
            dlg.cargarRoles(roles);
            dlg.precargar(usuario);
            dlg.setVisible(true);
            if (dlg.isConfirmado()) {
                Rol rol = roles.stream().filter(r -> r.getId() == dlg.getIdRolSeleccionado()).findFirst().orElse(null);
                usuario.setNombre(dlg.getNombre());
                usuario.setApellido(dlg.getApellido());
                usuario.setUsername(dlg.getUsername());
                usuario.setRol(rol);
                usuario.setActivo(dlg.isActivo());
                usuarioDAO.actualizar(usuario);
                String pass = dlg.getPassword();
                if (!pass.isEmpty()) usuarioDAO.actualizarPassword(id, com.sigmavep.util.HashUtil.sha256(pass));
                JOptionPane.showMessageDialog(menu, "Usuario actualizado.", "Edición", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            }
        } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
    }

    private void desactivarUsuario(int id) {
        if (id == Session.getUsuarioActual().getId()) { mostrarError("No podés desactivar tu propio usuario."); return; }
        int conf = JOptionPane.showConfirmDialog(menu, "¿Desactivar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                usuarioDAO.eliminar(id);
                JOptionPane.showMessageDialog(menu, "Usuario desactivado.", "Baja", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            } catch (SIGMAVEPException e) { mostrarError(e.getMessage()); }
        }
    }

    // ===== LÓGICA DE ALERTAS AUTOMÁTICAS =====

    /**
     * Verifica si el nuevo KM de un móvil supera el límite de algún tipo de mantenimiento
     * y genera alertas automáticamente si corresponde.
     */
    private void verificarAlertas(Movil movil, int kmNuevo) {
        try {
            List<TipoMantenimiento> tipos = tipoMantDAO.listarTodos();
            EstadoAlerta pendiente = new EstadoAlerta(1, "Pendiente");
            for (TipoMantenimiento tipo : tipos) {
                ServicioRealizado ultimo = servicioDAO.buscarUltimoServicio(movil.getId(), tipo.getId());
                int kmBaseRef = (ultimo != null) ? ultimo.getKmServicio() : 0;
                if ((kmNuevo - kmBaseRef) >= tipo.getLimiteKm()) {
                    boolean yaExiste = alertaDAO.existeAlertaPendiente(movil.getId(), tipo.getId());
                    if (!yaExiste) {
                        Alerta nueva = new Alerta(movil, tipo, pendiente, kmNuevo);
                        alertaDAO.insertar(nueva);
                    }
                }
            }
        } catch (SIGMAVEPException e) {
            System.err.println("Error al verificar alertas: " + e.getMessage());
        }
    }

    // ===== UTILIDADES =====

    private void ejecutarEnHilo(Runnable tarea) {
        Thread t = new Thread(tarea);
        t.setDaemon(true);
        t.start();
    }

    private void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(menu, mensaje, "Error", JOptionPane.ERROR_MESSAGE));
    }
}
