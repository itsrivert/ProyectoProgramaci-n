package src.view;

import src.dao.*;
import src.dto.PedidoDTO;
import src.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class Principal extends JFrame {

    // DAOs — uno por cada entidad
    private final DiscoDAO discoDAO = new DiscoDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final PedidoDAO pedidoDAO  = new PedidoDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private final Usuario usuarioLogueado;

    // ── Tablas y modelos ──────────────────────────────────
    private JTable tablaDiscos, tablaClientes, tablaPedidos, tablaUsuarios;
    private DefaultTableModel modeloDiscos, modeloClientes, modeloPedidos, modeloUsuarios;

    public Principal(Usuario usuario) {
        super("MILOAD — Panel Principal");
        this.usuarioLogueado = usuario;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        // Barra superior con nombre de usuario
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(Tema.AZUL);

        JLabel lblLogo = new JLabel("  MILOAD SYSTEM");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel(
            "Usuario: " + usuarioLogueado.getNombre() + " (" + usuarioLogueado.getRol() + ")  "
        );
        lblUsuario.setFont(Tema.FUENTE_NORMAL);
        lblUsuario.setForeground(Color.WHITE);

        barraSuperior.add(lblLogo,   BorderLayout.WEST);
        barraSuperior.add(lblUsuario, BorderLayout.EAST);

        // Pestañas 
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Discos", panelDiscos());
        pestanas.addTab("Clientes", panelClientes());
        pestanas.addTab("Pedidos", panelPedidos());
        pestanas.addTab("Usuarios", panelUsuarios());

        add(barraSuperior, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);

        // Cargamos los datos al abrir
        cargarDiscos();
        cargarClientes();
        cargarPedidos();
        cargarUsuarios();
    }

    // ══════════════════════════════════════════════════════
    //  PANEL DISCOS
    // ══════════════════════════════════════════════════════

    private JPanel panelDiscos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // ── Tabla ─────────────────────────────────────────
        modeloDiscos = new DefaultTableModel(
            new String[]{"ID", "Título", "Artista", "Género", "Año", "Formato", "Precio", "Stock"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDiscos = new JTable(modeloDiscos);
        panel.add(new JScrollPane(tablaDiscos), BorderLayout.CENTER);

        // ── Formulario derecho ────────────────────────────
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField tfTitulo = new JTextField();
        JTextField tfArtista = new JTextField();
        JTextField tfGenero = new JTextField();
        JTextField tfAnio = new JTextField();
        JComboBox<String> cbFormato = new JComboBox<>(
            new String[]{"Vinilo", "CD", "Cassette", "Digital"}
        );
        JTextField tfPrecio = new JTextField();
        JTextField tfStock = new JTextField();
        JTextField tfDesc = new JTextField();
        JTextField tfBuscarId = new JTextField();

        form.add(new JLabel("Título:"));      
        form.add(tfTitulo);
        form.add(new JLabel("Artista:"));     
        form.add(tfArtista);
        form.add(new JLabel("Género:"));      
        form.add(tfGenero);
        form.add(new JLabel("Año:"));         
        form.add(tfAnio);
        form.add(new JLabel("Formato:"));     
        form.add(cbFormato);
        form.add(new JLabel("Precio:"));      
        form.add(tfPrecio);
        form.add(new JLabel("Stock:"));       
        form.add(tfStock);
        form.add(new JLabel("Descripción:")); 
        form.add(tfDesc);
        form.add(new JLabel("Buscar por ID:")); 
        form.add(tfBuscarId);

        // Cuando se seleccione una fila rellena del formulario
        tablaDiscos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaDiscos.getSelectedRow();
            if (fila < 0) return;
            tfTitulo.setText(modeloDiscos.getValueAt(fila, 1).toString());
            tfArtista.setText(modeloDiscos.getValueAt(fila, 2).toString());
            tfGenero.setText(modeloDiscos.getValueAt(fila, 3).toString());
            tfAnio.setText(modeloDiscos.getValueAt(fila, 4).toString());
            cbFormato.setSelectedItem(modeloDiscos.getValueAt(fila, 5).toString());
            tfPrecio.setText(modeloDiscos.getValueAt(fila, 6).toString());
            tfStock.setText(modeloDiscos.getValueAt(fila, 7).toString());
        });

        // ── Botones ───────────────────────────────────────
        JPanel botones = new JPanel(new GridLayout(0, 1, 5, 5));

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                Disco d = new Disco();
                d.setTitulo(tfTitulo.getText().trim());
                d.setArtista(tfArtista.getText().trim());
                d.setGenero(tfGenero.getText().trim());
                d.setAnio(Integer.parseInt(tfAnio.getText().trim()));
                d.setFormato(cbFormato.getSelectedItem().toString());
                d.setPrecio(Double.parseDouble(tfPrecio.getText().trim()));
                d.setStock(Integer.parseInt(tfStock.getText().trim()));
                d.setDescripcion(tfDesc.getText().trim());

                int fila = tablaDiscos.getSelectedRow();
                if (fila < 0) {
                    // Si no hay fila seleccionada : insertar
                    discoDAO.insertar(d);
                    JOptionPane.showMessageDialog(this, "Disco añadido.");
                } else {
                    // Si hay fila seleccionada : actualizar
                    d.setId((int) modeloDiscos.getValueAt(fila, 0));
                    discoDAO.actualizar(d);
                    JOptionPane.showMessageDialog(this, "Disco actualizado.");
                }
                cargarDiscos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Año, precio y stock deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            int fila = tablaDiscos.getSelectedRow();
            if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un disco."); return; }
            int id = (int) modeloDiscos.getValueAt(fila, 0);
            int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar disco?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    discoDAO.eliminar(id);
                    JOptionPane.showMessageDialog(this, "Disco eliminado.");
                    cargarDiscos();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnBuscar = new JButton("Buscar por ID");
        btnBuscar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(tfBuscarId.getText().trim());
                Disco d = discoDAO.buscarPorId(id);
                if (d != null) {
                    tfTitulo.setText(d.getTitulo());
                    tfArtista.setText(d.getArtista());
                    tfGenero.setText(d.getGenero());
                    tfAnio.setText(String.valueOf(d.getAnio()));
                    cbFormato.setSelectedItem(d.getFormato());
                    tfPrecio.setText(String.valueOf(d.getPrecio()));
                    tfStock.setText(String.valueOf(d.getStock()));
                    tfDesc.setText(d.getDescripcion());
                    JOptionPane.showMessageDialog(this, "Disco encontrado: " + d.getTitulo());
                } else {
                    JOptionPane.showMessageDialog(this, "No existe ningún disco con ese ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            tablaDiscos.clearSelection();
            tfTitulo.setText(""); tfArtista.setText(""); tfGenero.setText("");
            tfAnio.setText(""); tfPrecio.setText(""); tfStock.setText("");
            tfDesc.setText(""); tfBuscarId.setText("");
        });

        JButton btnRecargar = new JButton("Recargar lista");
        btnRecargar.addActionListener(e -> cargarDiscos());

        botones.add(btnGuardar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);
        botones.add(btnLimpiar);
        botones.add(btnRecargar);

        JPanel derecho = new JPanel(new BorderLayout(5, 5));
        derecho.add(form,    BorderLayout.CENTER);
        derecho.add(botones, BorderLayout.SOUTH);
        derecho.setPreferredSize(new Dimension(300, 0));

        panel.add(derecho, BorderLayout.EAST);
        return panel;
    }

    private void cargarDiscos() {
        modeloDiscos.setRowCount(0);
        try {
            for (Disco d : discoDAO.listarTodos()) {
                modeloDiscos.addRow(new Object[]{
                    d.getId(), d.getTitulo(), d.getArtista(), d.getGenero(),
                    d.getAnio(), d.getFormato(), d.getPrecio(), d.getStock()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar discos: " + ex.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════
    //  PANEL CLIENTES
    // ══════════════════════════════════════════════════════

    private JPanel panelClientes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        modeloClientes = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellidos", "Username", "Email", "DNI", "Teléfono", "Dirección"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaClientes = new JTable(modeloClientes);
        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField tfNombre = new JTextField();
        JTextField tfApellidos = new JTextField();
        JTextField tfUsername = new JTextField();
        JTextField tfEmail = new JTextField();
        JTextField tfDni = new JTextField();
        JTextField tfTelefono = new JTextField();
        JTextField tfDireccion = new JTextField();
        JTextField tfBuscarId = new JTextField();

        form.add(new JLabel("Nombre:"));      
        form.add(tfNombre);
        form.add(new JLabel("Apellidos:"));   
        form.add(tfApellidos);
        form.add(new JLabel("Username:"));    
        form.add(tfUsername);
        form.add(new JLabel("Email:"));       
        form.add(tfEmail);
        form.add(new JLabel("DNI:"));         
        form.add(tfDni);
        form.add(new JLabel("Teléfono:"));    
        form.add(tfTelefono);
        form.add(new JLabel("Dirección:"));   
        form.add(tfDireccion);
        form.add(new JLabel("Buscar por ID:")); 
        form.add(tfBuscarId);

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaClientes.getSelectedRow();
            if (fila < 0) return;
            tfNombre.setText(modeloClientes.getValueAt(fila, 1).toString());
            tfApellidos.setText(modeloClientes.getValueAt(fila, 2).toString());
            tfUsername.setText(modeloClientes.getValueAt(fila, 3).toString());
            tfEmail.setText(modeloClientes.getValueAt(fila, 4).toString());
            tfDni.setText(modeloClientes.getValueAt(fila, 5).toString());
            tfTelefono.setText(modeloClientes.getValueAt(fila, 6) != null ? modeloClientes.getValueAt(fila, 6).toString() : "");
            tfDireccion.setText(modeloClientes.getValueAt(fila, 7) != null ? modeloClientes.getValueAt(fila, 7).toString() : "");
        });

        JPanel botones = new JPanel(new GridLayout(0, 1, 5, 5));

        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.addActionListener(e -> {
            int fila = tablaClientes.getSelectedRow();
            if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un cliente para editar."); return; }
            try {
                Cliente c = new Cliente();
                c.setId((int) modeloClientes.getValueAt(fila, 0));
                c.setNombre(tfNombre.getText().trim());
                c.setApellidos(tfApellidos.getText().trim());
                c.setUsername(tfUsername.getText().trim());
                c.setEmail(tfEmail.getText().trim());
                c.setDni(tfDni.getText().trim());
                c.setTelefono(tfTelefono.getText().trim());
                c.setDireccion(tfDireccion.getText().trim());
                clienteDAO.actualizar(c);
                JOptionPane.showMessageDialog(this, "Cliente actualizado.");
                cargarClientes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            int fila = tablaClientes.getSelectedRow();
            if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un cliente."); return; }
            int id = (int) modeloClientes.getValueAt(fila, 0);
            int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    clienteDAO.eliminar(id);
                    JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                    cargarClientes();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnBuscar = new JButton("Buscar por ID");
        btnBuscar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(tfBuscarId.getText().trim());
                Cliente c = clienteDAO.buscarPorId(id);
                if (c != null) {
                    tfNombre.setText(c.getNombre());
                    tfApellidos.setText(c.getApellidos());
                    tfUsername.setText(c.getUsername());
                    tfEmail.setText(c.getEmail());
                    tfDni.setText(c.getDni());
                    tfTelefono.setText(c.getTelefono());
                    tfDireccion.setText(c.getDireccion());
                    JOptionPane.showMessageDialog(this, "Cliente encontrado: " + c.getNombre());
                } else {
                    JOptionPane.showMessageDialog(this, "No existe ningún cliente con ese ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnRecargar = new JButton("Recargar lista");
        btnRecargar.addActionListener(e -> cargarClientes());

        botones.add(btnGuardar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);
        botones.add(btnRecargar);

        JPanel derecho = new JPanel(new BorderLayout(5, 5));
        derecho.add(form, BorderLayout.CENTER);
        derecho.add(botones, BorderLayout.SOUTH);
        derecho.setPreferredSize(new Dimension(300, 0));

        panel.add(derecho, BorderLayout.EAST);
        return panel;
    }

    private void cargarClientes() {
        modeloClientes.setRowCount(0);
        try {
            for (Cliente c : clienteDAO.listarTodos()) {
                modeloClientes.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getApellidos(), c.getUsername(),
                    c.getEmail(), c.getDni(), c.getTelefono(), c.getDireccion()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════
    //  PANEL PEDIDOS
    // ══════════════════════════════════════════════════════

    private JPanel panelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        modeloPedidos = new DefaultTableModel(
            new String[]{"ID", "Cliente", "Disco", "Artista", "Fecha", "Cantidad", "Total", "Estado"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPedidos = new JTable(modeloPedidos);
        panel.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

        // Para insertar un pedido nuevo necesitamos el ID del cliente y del disco
        JTextField tfClienteId = new JTextField();
        JTextField tfDiscoId   = new JTextField();
        JTextField tfCantidad  = new JTextField();
        JComboBox<String> cbEstado = new JComboBox<>(
            new String[]{"pendiente", "completado", "cancelado"}
        );

        form.add(new JLabel("ID Cliente:"));  
        form.add(tfClienteId);
        form.add(new JLabel("ID Disco:"));    
        form.add(tfDiscoId);
        form.add(new JLabel("Cantidad:"));    
        form.add(tfCantidad);
        form.add(new JLabel("Estado:"));      
        form.add(cbEstado);

        // Al seleccionar fila rellena el estado
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaPedidos.getSelectedRow();
            if (fila < 0) return;
            cbEstado.setSelectedItem(modeloPedidos.getValueAt(fila, 7).toString());
        });

        JPanel botones = new JPanel(new GridLayout(0, 1, 5, 5));

        JButton btnInsertar = new JButton("Nuevo pedido");
        btnInsertar.addActionListener(e -> {
            try {
                int clienteId = Integer.parseInt(tfClienteId.getText().trim());
                int discoId   = Integer.parseInt(tfDiscoId.getText().trim());
                int cantidad  = Integer.parseInt(tfCantidad.getText().trim());

                Pedido p = new Pedido();
                p.setClienteId(clienteId);
                p.setDiscoId(discoId);
                p.setFecha(LocalDate.now());
                p.setCantidad(cantidad);
                // Calculamos el total buscando el precio del disco
                Disco disco = discoDAO.buscarPorId(discoId);
                if (disco == null) {
                    JOptionPane.showMessageDialog(this, "No existe ningún disco con ese ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double total = disco.getPrecio() * cantidad;
                p.setTotal(total);
                p.setEstado(cbEstado.getSelectedItem().toString());

                pedidoDAO.insertar(p);
                JOptionPane.showMessageDialog(this, "Pedido creado.");
                cargarPedidos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los IDs y la cantidad deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            int fila = tablaPedidos.getSelectedRow();
            if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un pedido."); return; }
            int id = (int) modeloPedidos.getValueAt(fila, 0);
            int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar pedido?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    pedidoDAO.eliminar(id);
                    JOptionPane.showMessageDialog(this, "Pedido eliminado.");
                    cargarPedidos();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnRecargar = new JButton("Recargar lista");
        btnRecargar.addActionListener(e -> cargarPedidos());

        botones.add(btnInsertar);
        botones.add(btnEliminar);
        botones.add(btnRecargar);

        JPanel derecho = new JPanel(new BorderLayout(5, 5));
        derecho.add(form, BorderLayout.CENTER);
        derecho.add(botones, BorderLayout.SOUTH);
        derecho.setPreferredSize(new Dimension(250, 0));

        panel.add(derecho, BorderLayout.EAST);
        return panel;
    }

    private void cargarPedidos() {
        modeloPedidos.setRowCount(0);
        try {
            for (PedidoDTO p : pedidoDAO.listarTodos()) {
                modeloPedidos.addRow(new Object[]{
                    p.getPedidoId(), p.getClienteNombre(), p.getDiscoTitulo(),
                    p.getDiscoArtista(), p.getFecha(), p.getCantidad(),
                    p.getTotal(), p.getEstado()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar pedidos: " + ex.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════
    //  PANEL USUARIOS
    // ══════════════════════════════════════════════════════

    private JPanel panelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        modeloUsuarios = new DefaultTableModel(
            new String[]{"ID", "Username", "Nombre", "Apellidos", "Email", "DNI", "Rol"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaUsuarios = new JTable(modeloUsuarios);
        panel.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField tfUsername = new JTextField();
        JTextField tfNombre = new JTextField();
        JTextField tfApellidos = new JTextField();
        JTextField tfEmail = new JTextField();
        JTextField tfDni = new JTextField();
        JComboBox<String> cbRol = new JComboBox<>(new String[]{"cliente", "empleado"});

        form.add(new JLabel("Username:"));  
        form.add(tfUsername);
        form.add(new JLabel("Nombre:"));    
        form.add(tfNombre);
        form.add(new JLabel("Apellidos:")); 
        form.add(tfApellidos);
        form.add(new JLabel("Email:"));     
        form.add(tfEmail);
        form.add(new JLabel("DNI:"));       
        form.add(tfDni);
        form.add(new JLabel("Rol:"));       
        form.add(cbRol);

        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaUsuarios.getSelectedRow();
            if (fila < 0) return;
            tfUsername.setText(modeloUsuarios.getValueAt(fila, 1).toString());
            tfNombre.setText(modeloUsuarios.getValueAt(fila, 2).toString());
            tfApellidos.setText(modeloUsuarios.getValueAt(fila, 3).toString());
            tfEmail.setText(modeloUsuarios.getValueAt(fila, 4).toString());
            tfDni.setText(modeloUsuarios.getValueAt(fila, 5).toString());
            cbRol.setSelectedItem(modeloUsuarios.getValueAt(fila, 6).toString());
        });

        JPanel botones = new JPanel(new GridLayout(0, 1, 5, 5));

        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.addActionListener(e -> {
            int fila = tablaUsuarios.getSelectedRow();
            if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un usuario."); return; }
            try {
                Usuario u = new Usuario();
                u.setId((int) modeloUsuarios.getValueAt(fila, 0));
                u.setUsername(tfUsername.getText().trim());
                u.setNombre(tfNombre.getText().trim());
                u.setApellidos(tfApellidos.getText().trim());
                u.setEmail(tfEmail.getText().trim());
                u.setDni(tfDni.getText().trim());
                u.setRol(cbRol.getSelectedItem().toString());
                usuarioDAO.actualizar(u);
                JOptionPane.showMessageDialog(this, "Usuario actualizado.");
                cargarUsuarios();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            int fila = tablaUsuarios.getSelectedRow();
            if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un usuario."); return; }
            int id = (int) modeloUsuarios.getValueAt(fila, 0);
            if (id == usuarioLogueado.getId()) {
                JOptionPane.showMessageDialog(this, "No puedes eliminarte a ti mismo.");
                return;
            }
            int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    usuarioDAO.eliminar(id);
                    JOptionPane.showMessageDialog(this, "Usuario eliminado.");
                    cargarUsuarios();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnRecargar = new JButton("Recargar lista");
        btnRecargar.addActionListener(e -> cargarUsuarios());

        botones.add(btnGuardar);
        botones.add(btnEliminar);
        botones.add(btnRecargar);

        JPanel derecho = new JPanel(new BorderLayout(5, 5));
        derecho.add(form, BorderLayout.CENTER);
        derecho.add(botones, BorderLayout.SOUTH);
        derecho.setPreferredSize(new Dimension(280, 0));

        panel.add(derecho, BorderLayout.EAST);
        return panel;
    }

    private void cargarUsuarios() {
        modeloUsuarios.setRowCount(0);
        try {
            for (Usuario u : usuarioDAO.listarTodos()) {
                modeloUsuarios.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getNombre(),
                    u.getApellidos(), u.getEmail(), u.getDni(), u.getRol()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + ex.getMessage());
        }
    }
}