package src.view;

import src.dao.UsuarioDAO;
import src.dao.UsuarioDAOImpl;
import src.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class Login extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private JTextField     campoUsuario;
    private JPasswordField campoPassword;
    private JLabel         lblError;

    public Login() {
        super("MILOAD — Iniciar sesión");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal en vertical, todo centrado
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        add(panel);

        panel.add(Box.createVerticalStrut(20));

        // Título
        JLabel lblTitulo = new JLabel("MILOAD - Iniciar Sesión");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblTitulo);

        panel.add(Box.createVerticalStrut(20));

        // Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblUsuario);

        panel.add(Box.createVerticalStrut(5));

        campoUsuario = new JTextField(20);
        campoUsuario.setMaximumSize(new Dimension(250, 30));
        campoUsuario.setAlignmentX(CENTER_ALIGNMENT);
        campoUsuario.addActionListener(e -> intentarLogin());
        panel.add(campoUsuario);

        panel.add(Box.createVerticalStrut(10));

        // Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblPassword);

        panel.add(Box.createVerticalStrut(5));

        campoPassword = new JPasswordField(20);
        campoPassword.setMaximumSize(new Dimension(250, 30));
        campoPassword.setAlignmentX(CENTER_ALIGNMENT);
        campoPassword.addActionListener(e -> intentarLogin());
        panel.add(campoPassword);

        panel.add(Box.createVerticalStrut(15));

        // Botón entrar
        JButton btnEntrar = new JButton("ENTRAR");
        btnEntrar.setMaximumSize(new Dimension(250, 35));
        btnEntrar.setAlignmentX(CENTER_ALIGNMENT);
        btnEntrar.addActionListener(e -> intentarLogin());
        panel.add(btnEntrar);

        panel.add(Box.createVerticalStrut(8));

        // Etiqueta de error
        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        lblError.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lblError);

        panel.add(Box.createVerticalStrut(5));

        // Enlace registro
        JLabel lblRegistro = new JLabel(
            "<html><u>¿No tienes cuenta? Regístrate</u></html>"
        );
        lblRegistro.setForeground(Color.BLUE);
        lblRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegistro.setAlignmentX(CENTER_ALIGNMENT);
        lblRegistro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
                new Registro(Login.this).setVisible(true);
            }
        });
        panel.add(lblRegistro);
    }

    private void intentarLogin() {
        String user = campoUsuario.getText().trim();
        String pass = new String(campoPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Completa todos los campos.");
            return;
        }

        try {
            Usuario u = usuarioDAO.validar(user, pass);

            if (u != null) {
                dispose();
                new Principal(u).setVisible(true);
            } else {
                lblError.setText("Usuario o contraseña incorrectos.");
                campoPassword.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error de conexión:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}