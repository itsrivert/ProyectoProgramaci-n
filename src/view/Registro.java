package src.view;

import src.dao.UsuarioDAO;
import src.dao.UsuarioDAOImpl;
import src.model.Cliente;
import src.model.Empleado;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class Registro extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final Login      ventanaLogin;

    // Campos comunes
    private JTextField     campoUsername, campoEmail, campoNombre, campoApellidos, campoDni;
    private JPasswordField campoPassword;
    private JComboBox<String> comboRol;

    // Campos extra cliente
    private JTextField campoTelefono, campoDireccion;
    private JPanel     panelCliente;

    // Campos extra empleado
    private JTextField campoCargo, campoSalario;
    private JPanel     panelEmpleado;

    private JLabel lblError;

    public Registro(Login login) {
        super("MILOAD — Crear cuenta");
        this.ventanaLogin = login;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Al cerrar con la X vuelve al login
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                ventanaLogin.setVisible(true);
            }
        });

        // Panel principal en vertical
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        // Título
        JLabel titulo = new JLabel("Crear Nueva Cuenta", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));

        // Campos comunes
        panel.add(new JLabel("Usuario *"));
        campoUsername = new JTextField();
        panel.add(campoUsername);

        panel.add(new JLabel("Contraseña *"));
        campoPassword = new JPasswordField();
        panel.add(campoPassword);

        panel.add(new JLabel("Email *"));
        campoEmail = new JTextField();
        panel.add(campoEmail);

        panel.add(new JLabel("Nombre *"));
        campoNombre = new JTextField();
        panel.add(campoNombre);

        panel.add(new JLabel("Apellidos *"));
        campoApellidos = new JTextField();
        panel.add(campoApellidos);

        panel.add(new JLabel("DNI *"));
        campoDni = new JTextField();
        panel.add(campoDni);

        panel.add(new JLabel("Rol *"));
        comboRol = new JComboBox<>(new String[]{"cliente", "empleado"});
        panel.add(comboRol);

        panel.add(Box.createVerticalStrut(5));

        // Panel campos cliente
        panelCliente = new JPanel();
        panelCliente.setLayout(new BoxLayout(panelCliente, BoxLayout.Y_AXIS));
        panelCliente.add(new JLabel("Teléfono"));
        campoTelefono = new JTextField();
        panelCliente.add(campoTelefono);
        panelCliente.add(new JLabel("Dirección"));
        campoDireccion = new JTextField();
        panelCliente.add(campoDireccion);
        panel.add(panelCliente);

        // Panel campos empleado (oculto por defecto)
        panelEmpleado = new JPanel();
        panelEmpleado.setLayout(new BoxLayout(panelEmpleado, BoxLayout.Y_AXIS));
        panelEmpleado.add(new JLabel("Cargo"));
        campoCargo = new JTextField();
        panelEmpleado.add(campoCargo);
        panelEmpleado.add(new JLabel("Salario (€)"));
        campoSalario = new JTextField();
        panelEmpleado.add(campoSalario);
        panelEmpleado.setVisible(false);
        panel.add(panelEmpleado);

        // Cuando cambia el rol, mostramos u ocultamos los campos extra
        comboRol.addActionListener(e -> {
            String rol = (String) comboRol.getSelectedItem();
            panelCliente.setVisible("cliente".equals(rol));
            panelEmpleado.setVisible("empleado".equals(rol));
            pack();
        });

        panel.add(Box.createVerticalStrut(5));

        // Etiqueta de error
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        lblError.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblError);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnRegistrar = new JButton("REGISTRAR");
        btnRegistrar.addActionListener(e -> ejecutarRegistro());
        JButton btnCancelar = new JButton("CANCELAR");
        btnCancelar.addActionListener(e -> {
            dispose();
            ventanaLogin.setVisible(true);
        });
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        panel.add(panelBotones);
    }

    private void ejecutarRegistro() {
        String user      = campoUsername.getText().trim();
        String pass      = new String(campoPassword.getPassword()).trim();
        String email     = campoEmail.getText().trim();
        String nombre    = campoNombre.getText().trim();
        String apellidos = campoApellidos.getText().trim();
        String dni       = campoDni.getText().trim();
        String rol       = (String) comboRol.getSelectedItem();

        // Validación básica
        if (user.isEmpty() || pass.isEmpty() || email.isEmpty()
                || nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            lblError.setText("Todos los campos con * son obligatorios.");
            return;
        }

        try {
            if ("cliente".equals(rol)) {
                Cliente c = new Cliente();
                c.setUsername(user);
                c.setPassword(pass);
                c.setEmail(email);
                c.setNombre(nombre);
                c.setApellidos(apellidos);
                c.setDni(dni);
                c.setRol("cliente");
                c.setTelefono(campoTelefono.getText().trim());
                c.setDireccion(campoDireccion.getText().trim());
                c.setFechaRegistro(LocalDate.now());
                usuarioDAO.registrar(c);

            } else {
                Empleado emp = new Empleado();
                emp.setUsername(user);
                emp.setPassword(pass);
                emp.setEmail(email);
                emp.setNombre(nombre);
                emp.setApellidos(apellidos);
                emp.setDni(dni);
                emp.setRol("empleado");
                emp.setCargo(campoCargo.getText().trim());
                String salStr = campoSalario.getText().trim();
                // Si el campo salario está vacío ponemos 0.0, si no lo convertimos
                emp.setSalario(salStr.isEmpty() ? 0.0 : Double.parseDouble(salStr));
                emp.setFechaAlta(LocalDate.now());
                usuarioDAO.registrar(emp);
            }

            JOptionPane.showMessageDialog(this,
                "¡Usuario registrado correctamente!",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            ventanaLogin.setVisible(true);

        } catch (NumberFormatException ex) {
            lblError.setText("El salario debe ser un número (ej: 1500.0)");
        } catch (SQLException ex) {
            lblError.setText("Error: " + ex.getMessage());
        }
    }
}