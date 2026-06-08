package src.view;

import src.dao.UsuarioDAO;
import src.dao.UsuarioDAOImpl;
import src.model.Cliente;
import src.model.Empleado;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class Registro extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final Login ventanaLogin;

    private JTextField campoUsername, campoEmail, campoNombre, campoApellidos, campoDni;
    private JPasswordField campoPassword;
    private JComboBox<String> comboRol;

    private JPanel panelCliente, panelEmpleado;
    private JTextField campoTelefono, campoDireccion;
    private JTextField campoCargo, campoSalario;

    private JLabel lblError;

    private static final int FIELD_H = 34;

    public Registro(Login login) {
        super("MILOAD — Crear cuenta");
        this.ventanaLogin = login;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(440, 660);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                ventanaLogin.setVisible(true);
            }
        });

        // ── Fondo atrás ──────────────────────────────
        JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        root.setBackground(Tema.FONDO_PANEL);

        // ── Tarjeta blanca ───────────────────────
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.FONDO_CAMPO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                // Franja azul superior
                g2.setColor(Tema.AZUL);
                g2.fillRoundRect(0, 0, getWidth(), 2, 2, 2);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(28, 36, 28, 36));
        // El alto lo calcula solo con el BoxLayout
        // Solo controla el ancho mínimo
        card.setMinimumSize(new Dimension(400, 0));
        card.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        root.add(card);

        // Scroll
        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Tema.FONDO_PANEL);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        add(scroll);

        // ── Título ────────────────────────────────────────
        card.add(Box.createVerticalStrut(8));
        JLabel titulo = new JLabel("Crear cuenta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Tema.TEXTO_PRIMARIO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(titulo);

        JLabel subtitulo = new JLabel("Completa los datos para registrarte");
        subtitulo.setFont(Tema.FUENTE_SMALL);
        subtitulo.setForeground(Tema.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(subtitulo);
        card.add(Box.createVerticalStrut(20));

        // ── Tipo de cuenta ────────────────────────────────
        comboRol = new JComboBox<>(new String[]{"Cliente", "Empleado"});
        comboRol.setFont(Tema.FUENTE_NORMAL);
        comboRol.setBackground(Tema.FONDO_CAMPO);
        comboRol.setForeground(Tema.TEXTO_PRIMARIO);
        comboRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_H));
        comboRol.setAlignmentX(LEFT_ALIGNMENT);
        agregarSeccion(card, "Tipo de cuenta", comboRol, null);

        // ── Separador ─────────────────────────────────────
        card.add(separador("Datos de acceso"));

        // ── Campos comunes ────────────────────────────────
        campoUsername = nuevoCampo(" ");
        campoPassword = new JPasswordField();
        estilizar(campoPassword);
        campoEmail = nuevoCampo(" ");
        campoNombre = nuevoCampo(" ");
        campoApellidos = nuevoCampo(" ");
        campoDni = nuevoCampo(" ");

        agregarSeccion(card, "Nombre de usuario *", campoUsername, null);
        agregarSeccion(card, "Contraseña *", campoPassword, null);
        agregarSeccion(card, "Email *", campoEmail, null);
        agregarSeccion(card, "Nombre *", campoNombre, null);
        agregarSeccion(card, "Apellidos *", campoApellidos, null);
        agregarSeccion(card, "DNI *", campoDni, null);

        // ── Separador ─────────────────────────────────────
        card.add(separador("Datos adicionales"));

        // ── Panel CLIENTE ─────────────────────────────────
        panelCliente = new JPanel();
        panelCliente.setLayout(new BoxLayout(panelCliente, BoxLayout.Y_AXIS));
        panelCliente.setOpaque(false);
        panelCliente.setAlignmentX(LEFT_ALIGNMENT);
        campoTelefono = nuevoCampo(" ");
        campoDireccion = nuevoCampo(" ");
        agregarSeccion(panelCliente, "Teléfono", campoTelefono, "Opcional");
        agregarSeccion(panelCliente, "Dirección", campoDireccion, "Opcional");
        card.add(panelCliente);

        // ── Panel EMPLEADO ────────────────────────────────
        panelEmpleado = new JPanel();
        panelEmpleado.setLayout(new BoxLayout(panelEmpleado, BoxLayout.Y_AXIS));
        panelEmpleado.setOpaque(false);
        panelEmpleado.setAlignmentX(LEFT_ALIGNMENT);
        panelEmpleado.setVisible(false);
        campoCargo = nuevoCampo(" ");
        campoSalario = nuevoCampo(" ");
        agregarSeccion(panelEmpleado, "Cargo", campoCargo,  "Opcional");
        agregarSeccion(panelEmpleado, "Salario", campoSalario, "Opcional — número decimal");
        card.add(panelEmpleado);

        // ── Listener combo ────────────────────────────────
        comboRol.addActionListener(e -> {
            boolean esCliente = comboRol.getSelectedIndex() == 0;
            panelCliente.setVisible(esCliente);
            panelEmpleado.setVisible(!esCliente);
            if (esCliente) { campoCargo.setText(""); campoSalario.setText(""); }
            else { campoTelefono.setText(""); campoDireccion.setText(""); }
            card.revalidate();
        });

        // ── Error ─────────────────────────────────────────
        card.add(Box.createVerticalStrut(8));
        lblError = new JLabel(" ");
        lblError.setFont(Tema.FUENTE_SMALL);
        lblError.setForeground(Tema.ROJO);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblError);
        card.add(Box.createVerticalStrut(10));

        // ── Botón ─────────────────────────────────────────
        JButton btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setFont(Tema.FUENTE_BOLD);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBackground(Tema.AZUL);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegistrar.setAlignmentX(LEFT_ALIGNMENT);
        btnRegistrar.addActionListener(e -> realizarRegistro());
        card.add(btnRegistrar);
        card.add(Box.createVerticalStrut(12));

        // ── Link volver ───────────────────────────────────
        JLabel lnkVolver = new JLabel("<html><u>← Volver al inicio de sesión</u></html>");
        lnkVolver.setFont(Tema.FUENTE_SMALL);
        lnkVolver.setForeground(Tema.AZUL);
        lnkVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lnkVolver.setAlignmentX(LEFT_ALIGNMENT);
        lnkVolver.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                ventanaLogin.setVisible(true);
            }
        });
        card.add(lnkVolver);
        card.add(Box.createVerticalStrut(8));
    }

    // ── Sección: label + campo + hint opcional ────────────
    private void agregarSeccion(JPanel panel, String label, JComponent comp, String hint) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Tema.FUENTE_BOLD);
        lbl.setForeground(Tema.TEXTO_PRIMARIO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(3));

        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_H));
        comp.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(comp);

        if (hint != null) {
            JLabel hintLbl = new JLabel(hint);
            hintLbl.setFont(Tema.FUENTE_SMALL);
            hintLbl.setForeground(Tema.TEXTO_SECUNDARIO);
            hintLbl.setAlignmentX(LEFT_ALIGNMENT);
            panel.add(Box.createVerticalStrut(2));
            panel.add(hintLbl);
        }
        panel.add(Box.createVerticalStrut(10));
    }

    // ── Campo de texto con placeholder y borde azul al foco
    private JTextField nuevoCampo(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Tema.BORDE);
                    g2.setFont(Tema.FUENTE_SMALL);
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, 8, y);
                    g2.dispose();
                }
            }
        };
        estilizar(tf);
        return tf;
    }

    private void estilizar(JComponent comp) {
        comp.setFont(Tema.FUENTE_NORMAL);
        comp.setBackground(Tema.FONDO_CAMPO);
        comp.setForeground(Tema.TEXTO_PRIMARIO);
        comp.setBorder(new CompoundBorder(
            new LineBorder(Tema.BORDE, 1),
            new EmptyBorder(0, 8, 0, 8)
        ));
        comp.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                comp.setBorder(new CompoundBorder(
                    new LineBorder(Tema.AZUL, 2),
                    new EmptyBorder(0, 7, 0, 7)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                comp.setBorder(new CompoundBorder(
                    new LineBorder(Tema.BORDE, 1),
                    new EmptyBorder(0, 8, 0, 8)
                ));
            }
        });
    }

    // ── Separador con etiqueta ────────────────────────────
    private JPanel separador(String texto) {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);
        wrap.setAlignmentX(LEFT_ALIGNMENT);
        wrap.add(Box.createVerticalStrut(4));

        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(Tema.TEXTO_SECUNDARIO);

        JSeparator sep = new JSeparator();
        sep.setForeground(Tema.BORDE);

        fila.add(lbl, BorderLayout.WEST);
        fila.add(sep, BorderLayout.CENTER);

        wrap.add(fila);
        wrap.add(Box.createVerticalStrut(12));
        return wrap;
    }

    // ── Lógica de registro ────────────────────────────────
    private void realizarRegistro() {
        lblError.setText(" ");

        String user = campoUsername.getText().trim();
        String pass = new String(campoPassword.getPassword()).trim();
        String email = campoEmail.getText().trim();
        String nombre = campoNombre.getText().trim();
        String apellidos = campoApellidos.getText().trim();
        String dni = campoDni.getText().trim();

        if (user.isEmpty() || pass.isEmpty() || email.isEmpty()
                || nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            lblError.setText("Rellena todos los campos obligatorios (*).");
            return;
        }

        try {
            if (comboRol.getSelectedIndex() == 0) {
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
                String salStr = campoSalario.getText().trim();
                Empleado emp = new Empleado();
                emp.setUsername(user);       
                emp.setPassword(pass);
                emp.setEmail(email);         
                emp.setNombre(nombre);
                emp.setApellidos(apellidos); 
                emp.setDni(dni);
                emp.setRol("empleado");
                emp.setCargo(campoCargo.getText().trim());
                emp.setSalario(salStr.isEmpty() ? 0.0 : Double.parseDouble(salStr));
                emp.setFechaAlta(LocalDate.now());
                usuarioDAO.registrar(emp);
            }

            JOptionPane.showMessageDialog(this,
                "¡Cuenta creada con éxito!",
                "Registro completado", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            ventanaLogin.setVisible(true);

        } catch (NumberFormatException ex) {
            lblError.setText("El salario debe ser un número decimal (ej: 1400.00).");
        } catch (SQLException ex) {
            lblError.setText("Error en BD: " + ex.getMessage());
        }
    }
}