import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class ServidorImpresion extends JFrame {

    // Donde se escriben los datos
    private JTextField campoNombre;
    private JTextField campoTamano;
    private JTextField campoUsuario;

    // Botones de la ventana
    private JButton botonEnviar;
    private JButton botonImprimir;

    // Lista donde mostramos los documentos pendientes
    private DefaultListModel<String> modeloLista;
    private JList<String> listaDocumentos;

    private JLabel etiquetaEstado;

    // Cola FIFO, primero entrar, primero salir (first in, first out)
    private Queue<Documento> cola = new LinkedList<>();

    public ServidorImpresion() {

        setTitle("Simulador de Servidor de Impresión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Panel con tres filas y dos columnas
        JPanel panelDatos = new JPanel(new GridLayout(3, 2, 10, 10));

        campoNombre = new JTextField();
        campoTamano = new JTextField();
        campoUsuario = new JTextField();

        panelDatos.add(new JLabel("Nombre del archivo:"));
        panelDatos.add(campoNombre);

        panelDatos.add(new JLabel("Tamaño en MB:"));
        panelDatos.add(campoTamano);

        panelDatos.add(new JLabel("Usuario que lo envía:"));
        panelDatos.add(campoUsuario);

        panelPrincipal.add(panelDatos, BorderLayout.NORTH);

        // Lista de documentos pendientes
        modeloLista = new DefaultListModel<>();
        listaDocumentos = new JList<>(modeloLista);

        JScrollPane panelLista = new JScrollPane(listaDocumentos);

        panelLista.setBorder(
                BorderFactory.createTitledBorder(
                        "Documentos pendientes: el primero está arriba"
                )
        );

        panelLista.setPreferredSize(new Dimension(600, 230));

        panelPrincipal.add(panelLista, BorderLayout.CENTER);

        // Botones
        botonEnviar = new JButton("Enviar a Imprimir");
        botonImprimir = new JButton("Simular Impresión");

        JPanel panelBotones = new JPanel();

        panelBotones.add(botonEnviar);
        panelBotones.add(botonImprimir);

        etiquetaEstado = new JLabel("No hay documentos pendientes.");

        JPanel panelInferior = new JPanel(new BorderLayout());

        panelInferior.add(panelBotones, BorderLayout.NORTH);
        panelInferior.add(etiquetaEstado, BorderLayout.SOUTH);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);

        // Boton enviar a imprimir
        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarDocumento();
            }
        });

        // Boton simular impresion
        botonImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imprimirDocumento();
            }
        });

        actualizarLista();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void agregarDocumento() {

        // Leemos los datos
        String nombre = campoNombre.getText().trim();
        String usuario = campoUsuario.getText().trim();
        String textoTamano = campoTamano.getText().trim();

        // Revisamos campos completos
        if (nombre.isEmpty() || usuario.isEmpty() || textoTamano.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Completa todos los campos."
            );
            return;
        }

        double tamano;

        try {
            // Permite escribir decimales
            textoTamano = textoTamano.replace(',', '.');
            tamano = Double.parseDouble(textoTamano);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "El tamaño debe ser un número. Ejemplo: 2.5"
            );
            return;
        }

        // El tamaño debe ser válido y mayor que cero
        if (!Double.isFinite(tamano) || tamano <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Escribe un tamaño válido mayor que cero."
            );
            return;
        }

        // Creamos el documento con los datos
        Documento documento = new Documento(nombre, tamano, usuario);

        // offer agrega el documento al FINAL de la cola.
        cola.offer(documento);

        // Limpiamos los campos
        campoNombre.setText("");
        campoTamano.setText("");
        campoUsuario.setText("");

        actualizarLista();

        campoNombre.requestFocusInWindow();
    }

    private void imprimirDocumento() {

        if (cola.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay documentos para imprimir."
            );
            return;
        }

        // poll retira el primer documento de la cola
        Documento documento = cola.poll();

        actualizarLista();

        JOptionPane.showMessageDialog(
                this,
                "Impresión simulada completada:\n" + documento
        );
    }

    private void actualizarLista() {

        // Limpiamos solo la lista visual (solo la visual no la cola)
        modeloLista.clear();

        int posicion = 1;

        // Mostramos los documentos en su orden de llegada
        for (Documento documento : cola) {

            if (posicion == 1) {
                modeloLista.addElement(
                        posicion + ". [PRIMERO] " + documento
                );
            } else {
                modeloLista.addElement(
                        posicion + ". " + documento
                );
            }

            posicion++;
        }

        // Solo permite imprimir cuando existen documentos
        botonImprimir.setEnabled(!cola.isEmpty());

        if (cola.isEmpty()) {
            etiquetaEstado.setText("No hay documentos pendientes.");
        } else {
            etiquetaEstado.setText(
                    "Pendientes: " + cola.size()
                            + " | Siguiente: " + cola.peek().getNombre()
            );
        }
    }
}