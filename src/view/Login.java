package src.view;

import src.dao.UsuarioDAO;
import src.dao.UsuarioDAOImpl;
import src.model.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class Login extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JLabel lblError;

    private static final int FIELD_H = 34;

    public Login() {
        super("MILOAD — Iniciar sesión");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Fondo gris  ──────────────────────────────
        JPanel root = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        root.setBackground(Tema.FONDO_PANEL);

        // ── Tarjeta blanca ────────────────────────────────
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
        card.setMinimumSize(new Dimension(320, 0));
        card.setMaximumSize(new Dimension(320, Integer.MAX_VALUE));

        root.add(card);
        add(root);

        // ── Título ────────────────────────────────────────
        card.add(Box.createVerticalStrut(8));
        JLabel titulo = new JLabel("Iniciar sesión");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Tema.TEXTO_PRIMARIO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(titulo);

        JLabel subtitulo = new JLabel("Bienvenido a MILOAD");
        subtitulo.setFont(Tema.FUENTE_SMALL);
        subtitulo.setForeground(Tema.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(subtitulo);
        card.add(Box.createVerticalStrut(24));

        // ── Usuario ───────────────────────────────────────
        campoUsuario = nuevoCampo(" ");
        agregarSeccion(card, "Usuario", campoUsuario);
        campoUsuario.addActionListener(e -> intentarLogin());

        // ── Contraseña ────────────────────────────────────
        campoPassword = new JPasswordField();
        estilizar(campoPassword);
        agregarSeccion(card, "Contraseña", campoPassword);
        campoPassword.addActionListener(e -> intentarLogin());

        // ── Error ─────────────────────────────────────────
        card.add(Box.createVerticalStrut(4));
        lblError = new JLabel(" ");
        lblError.setFont(Tema.FUENTE_SMALL);
        lblError.setForeground(Tema.ROJO);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblError);
        card.add(Box.createVerticalStrut(10));

        // ── Botón ─────────────────────────────────────────
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(Tema.FUENTE_BOLD);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setBackground(Tema.AZUL);
        btnEntrar.setOpaque(true);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnEntrar.setAlignmentX(LEFT_ALIGNMENT);
        btnEntrar.addActionListener(e -> intentarLogin());
        card.add(btnEntrar);
        card.add(Box.createVerticalStrut(14));

        // ── Link registro ─────────────────────────────────
        JLabel lblRegistro = new JLabel("<html><u>¿No tienes cuenta? Regístrate</u></html>");
        lblRegistro.setFont(Tema.FUENTE_SMALL);
        lblRegistro.setForeground(Tema.AZUL);
        lblRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegistro.setAlignmentX(LEFT_ALIGNMENT);
        lblRegistro.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                setVisible(false);
                new Registro(Login.this).setVisible(true);
            }
        });
        card.add(lblRegistro);
        card.add(Box.createVerticalStrut(8));
    }

    private void agregarSeccion(JPanel panel, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Tema.FUENTE_BOLD);
        lbl.setForeground(Tema.TEXTO_PRIMARIO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(3));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_H));
        comp.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(comp);
        panel.add(Box.createVerticalStrut(12));
    }

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