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

    // Campos Comunes (Tabla usuarios)
    private JTextField     campoUsername, campoEmail, campoNombre, campoApellidos, campoDni;
    private JPasswordField campoPassword;
    private JComboBox<String> comboRol;

    // Campos específicos de Cliente
    private JTextField campoTelefono, campoDireccion;

    // Campos específicos de Empleado
    private JTextField campoCargo, campoSalario;

    private JLabel lblError;

    public Registro(Login login) {
        super("MILOAD — Crear cuenta");
        this.ventanaLogin = login;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 680); // Un pelín más alta para que quepa todo limpio
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                ventanaLogin.setVisible(true);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        panel.setBackground(Color.WHITE);
        
        // JScrollPane por si la pantalla del ordenador es pequeña, para poder hacer scroll cómodamente
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        add(scrollPane);

        JLabel lblTitulo = new JLabel("Formulario de Registro");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(10));

        // 1. Selector de tipo de cuenta prioritario
        panel.add(new JLabel("Tipo de cuenta * :"));
        comboRol = new JComboBox<>(new String[]{"Cliente", "Empleado"});
        comboRol.setMaximumSize(new Dimension(400, 28));
        comboRol.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(comboRol);
        panel.add(Box.createVerticalStrut(10));

        // 2. Datos generales
        campoUsername = crearCampo(panel, "Nombre de usuario * :");
        campoPassword = new JPasswordField();
        agregarComponente(panel, "Contraseña * :", campoPassword);
        campoEmail = crearCampo(panel, "Email * :");
        campoNombre = crearCampo(panel, "Nombre * :");
        campoApellidos = crearCampo(panel, "Apellidos * :");
        campoDni = crearCampo(panel, "DNI * :");

        // 3. Secciones específicas (Se quedan fijas y visibles para evitar código complejo)
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(5));
        
        campoTelefono = crearCampo(panel, "Teléfono (Solo Clientes):");
        campoDireccion = crearCampo(panel, "Dirección (Solo Clientes):");
        campoCargo = crearCampo(panel, "Cargo (Solo Empleados):");
        campoSalario = crearCampo(panel, "Salario (Solo Empleados):");

        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        lblError.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(5));

        JButton btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setBackground(new Color(0, 102, 204));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setAlignmentX(CENTER_ALIGNMENT);
        btnRegistrar.setMaximumSize(new Dimension(200, 35));
        
        btnRegistrar.addActionListener(e -> realizarRegistro());
        panel.add(btnRegistrar);
    }

    private JTextField crearCampo(JPanel panel, String label) {
        JTextField tf = new JTextField();
        agregarComponente(panel, label, tf);
        return tf;
    }

    private void agregarComponente(JPanel panel, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lbl);
        comp.setMaximumSize(new Dimension(400, 25));
        comp.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(comp);
        panel.add(Box.createVerticalStrut(6));
    }

    private void realizarRegistro() {
        lblError.setText(" ");

        String user = campoUsername.getText().trim();
        String pass = new String(campoPassword.getPassword()).trim();
        String email = campoEmail.getText().trim();
        String nombre = campoNombre.getText().trim();
        String apellidos = campoApellidos.getText().trim();
        String dni = campoDni.getText().trim();

        // Validamos campos base obligatorios
        if (user.isEmpty() || pass.isEmpty() || email.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            lblError.setText("Por favor, rellene todos los campos obligatorios (*).");
            return;
        }

        try {
            if (comboRol.getSelectedIndex() == 0) {
                // Registro como CLIENTE
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
                // Registro como EMPLEADO
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
                emp.setSalario(salStr.isEmpty() ? 0.0 : Double.parseDouble(salStr));
                emp.setFechaAlta(LocalDate.now());

                usuarioDAO.registrar(emp);
            }

            JOptionPane.showMessageDialog(this,
                "¡Cuenta creada con éxito!",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            ventanaLogin.setVisible(true);

        } catch (NumberFormatException ex) {
            lblError.setText("El salario del empleado debe ser un número decimal válido.");
        } catch (SQLException ex) {
            lblError.setText("Error en BD: " + ex.getMessage());
        }
    }
}