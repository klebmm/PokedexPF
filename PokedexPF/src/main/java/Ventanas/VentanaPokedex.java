/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ventanas;

import Modelo.FavoritosJDBC;
import Modelo.ClsConexion;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Hashtable;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Caleb
 */
    public class VentanaPokedex extends javax.swing.JFrame {
    private final String relleno = "- - - - - -";
    private final BufferedImage buffer1;
    private int limit;
    private int contador = 0;//-1
    private String USER = "";
    private Image imagen1;
    private ResultSet resultadoConsulta;
    private Hashtable hash;
    private String banderaFiltro = "";
    private Statement estado;
    String neimPokemon = "";

    Connection con = null;
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
        public void setUSER(String USER) {
        this.USER = USER;
    }
    
    private void filtrarResultados(int columna, String tabla, String consulta) {
        hash.clear();
        int cont = 0;
        try {
            resultadoConsulta = FavoritosJDBC.ejecutarConsulta(tabla, consulta);
            while(resultadoConsulta.next()) {
                if (cont+1 != resultadoConsulta.getInt(columna) && "TODOS".equals(banderaFiltro)) {
                    hash.put(cont, "|" + (cont+1));
                } else {
                    hash.put(cont, resultadoConsulta.getInt(columna));
                }
                cont++;
            }
            limit = cont - 1;
            contador = -1;
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    private void fillALL() {
        banderaFiltro = "TODOS";
        int columna = 1;
        String tabla = "pokemon";
        String consulta = "";
        filtrarResultados(columna, tabla, consulta);
        btnBuscarNombre.setEnabled(true);
        txtBuscarNombre.setEnabled(true);
        resultadoVacio();
    }
    
    
    private void resultadoEncontrado(ResultSet rs) {
        try {
            jLabelID.setText(rs.getString(1));
            nombrePokemon.setText(rs.getString(2));
            jLabelGene.setText(rs.getString(5));
            jLabelAltura.setText(rs.getString(10));
            jLabelPeso.setText(rs.getString(11));
            jLabelEspecie.setText(rs.getString(12));
            jLabelColor.setText(rs.getString(13));
            jLabelHabitad.setText(rs.getString(15));
            jLabelCaptura.setText(rs.getString(17));
            jLabelEXP.setText(rs.getString(18));
            jLabelFelicida.setText(rs.getString(19));
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    private void resultadoNoEncontrado(String relleno) {
        jLabelID.setText(relleno);
        nombrePokemon.setText(relleno);
        jLabelGene.setText(relleno);
        jLabelAltura.setText(relleno);
        jLabelPeso.setText(relleno);
        jLabelEspecie.setText(relleno);
        jLabelColor.setText(relleno);
        jLabelHabitad.setText(relleno);
        jLabelCaptura.setText(relleno);
        jLabelEXP.setText(relleno);
        jLabelFelicida.setText(relleno);
        dibujaElPokemonQueEstaEnLaPosicion(-1);
    }
    
    private void Fulllabels(ResultSet rs) {
        try {
            if(rs.next()) {
                resultadoEncontrado(rs);
            } else {
                resultadoNoEncontrado(relleno);
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }   
    }
    
    private void resultadoVacio() {
        dibujaElPokemonQueEstaEnLaPosicion(-1);
        resultadoNoEncontrado(relleno);
    }
    
    private void ejecutarPagineo(int contador) {
        String pokemon = String.valueOf(hash.get(contador));
        pokemon = pokemon.replace("|", "");
        String tabla = "pokemon";
        String consulta = "WHERE id = " + pokemon;
        
        if (!Objects.equals(pokemon, "null")) {
            resultadoConsulta = FavoritosJDBC.ejecutarConsulta(tabla, consulta);
            Fulllabels(resultadoConsulta);
            dibujaElPokemonQueEstaEnLaPosicion(Integer.parseInt(pokemon)-1);
        }
    }
    
    public void dibujaElPokemonQueEstaEnLaPosicion(int posicion){
        int fila = posicion / 31;
        int columna = posicion % 31;
        Graphics2D g2 = (Graphics2D) buffer1.getGraphics();
        g2.setColor(Color.black);
        g2.fillRect(0, 0, //pinta el fondo del jpanel negro
                imagenPokemon.getWidth(),
                imagenPokemon.getHeight()); 
                g2.drawImage(imagen1,
                0,  //posicion X inicial dentro del jpanel 
                0,  // posicion Y inicial dentro del jpanel
                imagenPokemon.getWidth(), //ancho del jpanel
                imagenPokemon.getHeight(), //alto del jpanel
                columna*96, //posicion inicial X dentro de la imagen de todos los pokemon
                fila*96, //posicion inicial Y dentro de la imagen de todos los pokemon
                columna*96 + 96, //posicion final X
                fila*96 + 96, //posicion final Y
                null  //si no lo pones no va
                );
        repaint();
    }
    
    @Override
    public void paint(Graphics g){
        super.paintComponents(g);
        Graphics2D  g2 = (Graphics2D) imagenPokemon.getGraphics();
        g2.drawImage(buffer1,0,0,imagenPokemon.getWidth(), imagenPokemon.getHeight(),null);
    }
        
    /**
     * Creates new form VentanaPokedex
     */
    public VentanaPokedex() {
        this.hash = new Hashtable();
        initComponents();
        try {
            //imagen1 = ImageIO.read(getClass().getResource("/imagenes/black-white.png"));*/
            imagen1 = ImageIO.read(new File("C:\\Users\\Caleb\\Desktop\\progra pokemon\\datos\\imagenes\\black-white.png"));
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        buffer1 = (BufferedImage) imagenPokemon.createImage(imagenPokemon.getWidth(), imagenPokemon.getHeight());
        Graphics2D g2 = buffer1.createGraphics();
        fillALL(); 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabelColor = new javax.swing.JLabel();
        jLabelHabitad = new javax.swing.JLabel();
        nombrePokemon = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelGene = new javax.swing.JLabel();
        txtBuscarNombre = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        btnBuscarNombre = new javax.swing.JButton();
        jLabelCaptura = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabelFelicida = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        rbtnTodos = new javax.swing.JRadioButton();
        jLabelEXP = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnFiltros = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabelID = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabelAltura = new javax.swing.JLabel();
        jLabelPeso = new javax.swing.JLabel();
        imagenPokemon = new javax.swing.JPanel();
        jLabelEspecie = new javax.swing.JLabel();
        der = new javax.swing.JButton();
        izq = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 102, 102));

        jLabelColor.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelColor.setText("color");

        jLabelHabitad.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelHabitad.setText("habitad");

        nombrePokemon.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        nombrePokemon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nombrePokemon.setText("nombre...");
        nombrePokemon.setToolTipText("");

        jLabel9.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel9.setText("Generación:");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Nombre de pokemon:");

        jLabelGene.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelGene.setText("gene");

        txtBuscarNombre.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtBuscarNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarNombreActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel10.setText("Captura:");

        btnBuscarNombre.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnBuscarNombre.setText("Buscar");
        btnBuscarNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarNombreActionPerformed(evt);
            }
        });

        jLabelCaptura.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelCaptura.setText("rcaptura");

        jLabel2.setFont(new java.awt.Font("Monospaced", 1, 36)); // NOI18N
        jLabel2.setText("Pokedex");

        jLabel11.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel11.setText("Experiencia:");

        jLabelFelicida.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelFelicida.setText("felicidad");

        jLabel12.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel12.setText("Felicidad:");

        rbtnTodos.setBackground(new java.awt.Color(255, 102, 102));
        rbtnTodos.setSelected(true);
        rbtnTodos.setText("Todos");
        rbtnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnTodosActionPerformed(evt);
            }
        });

        jLabelEXP.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelEXP.setText("exp");

        btnLogout.setBackground(new java.awt.Color(102, 102, 102));
        btnLogout.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(204, 204, 204));
        btnLogout.setText("Cerrar Sesión");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnFiltros.setBackground(new java.awt.Color(102, 102, 102));
        btnFiltros.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnFiltros.setForeground(new java.awt.Color(204, 204, 204));
        btnFiltros.setText("FILTRADO");
        btnFiltros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrosActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel3.setText("Id:");

        jLabelID.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelID.setText("id");

        jLabel4.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel4.setText("Altura:");

        jLabel5.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel5.setText("Peso:");

        jLabel6.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel6.setText("Especie:");

        jLabel7.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel7.setText("Color:");

        jLabel8.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        jLabel8.setText("Habitat:");

        jLabelAltura.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelAltura.setText("altura");

        jLabelPeso.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelPeso.setText("peso");

        imagenPokemon.setBorder(new javax.swing.border.MatteBorder(null));

        javax.swing.GroupLayout imagenPokemonLayout = new javax.swing.GroupLayout(imagenPokemon);
        imagenPokemon.setLayout(imagenPokemonLayout);
        imagenPokemonLayout.setHorizontalGroup(
            imagenPokemonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 212, Short.MAX_VALUE)
        );
        imagenPokemonLayout.setVerticalGroup(
            imagenPokemonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 151, Short.MAX_VALUE)
        );

        jLabelEspecie.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabelEspecie.setText("especie");

        der.setBackground(new java.awt.Color(51, 51, 51));
        der.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        der.setForeground(new java.awt.Color(204, 204, 204));
        der.setText("Derecha >");
        der.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                derActionPerformed(evt);
            }
        });

        izq.setBackground(new java.awt.Color(51, 51, 51));
        izq.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        izq.setForeground(new java.awt.Color(204, 204, 204));
        izq.setText("< Izquierda");
        izq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                izqActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelEspecie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelPeso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelAltura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelID, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(63, 63, 63)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelHabitad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelGene, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelCaptura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelEXP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelFelicida, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(52, 52, 52))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscarNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(imagenPokemon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nombrePokemon, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(izq)
                        .addGap(18, 18, 18)
                        .addComponent(der, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(rbtnTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(123, 123, 123))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnLogout)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnFiltros))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(btnBuscarNombre))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelID))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabelAltura))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabelPeso)
                                        .addGap(32, 32, 32))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabelEspecie))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelColor)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabelHabitad))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabelGene))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabelCaptura))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabelEXP))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabelFelicida)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(nombrePokemon)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(imagenPokemon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(81, 81, 81)
                                .addComponent(rbtnTodos)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(der)
                            .addComponent(izq))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogout)
                    .addComponent(btnFiltros))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarNombreActionPerformed

      
        neimPokemon = txtBuscarNombre.getText();
        
        dibujaElPokemonQueEstaEnLaPosicion(contador);
        
        String cuerito = "Select * from pokemon where name=?";
        
        
        
        
        try {
            
            conn = ClsConexion.getConnection();
            stmt = conn.prepareStatement(cuerito);
            stmt.setString(1,txtBuscarNombre.getText());
            
            rs = stmt.executeQuery();
            
            if(rs.next()){
                
                jLabelGene.setText(rs.getNString("generation_id"));
                jLabelEspecie.setText(rs.getNString("species"));
                jLabelAltura.setText(rs.getNString("height"));
                jLabelPeso.setText(rs.getNString("weight"));
                nombrePokemon.setText(rs.getString("name"));
                jLabelID.setText(rs.getNString("id"));
                jLabelColor.setText(rs.getNString("color"));
                jLabelHabitad.setText(rs.getNString("habitat"));
                jLabelCaptura.setText(rs.getNString("capture_rate"));
                jLabelEXP.setText(rs.getNString("base_experience"));
                jLabelFelicida.setText(rs.getNString("base_happiness"));
                
            }else{
                
                JOptionPane.showMessageDialog(null,"Este pokemon no existe en la pokedex");
            }
      
      
      
      
              } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            JOptionPane.showMessageDialog(null,"SQL Problem");
        }
    }//GEN-LAST:event_btnBuscarNombreActionPerformed

    private void rbtnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnTodosActionPerformed
        fillALL();
    }//GEN-LAST:event_rbtnTodosActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        Login ventanaLogin = new Login();
        ventanaLogin.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnFiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrosActionPerformed
        FiltrosReport ventanaFiltros = new FiltrosReport();
        ventanaFiltros.setUSER(USER);
        ventanaFiltros.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnFiltrosActionPerformed

    private void derActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_derActionPerformed

        
       contador++;
        if (contador >= limit){
            contador = limit;
        }
        
        dibujaElPokemonQueEstaEnLaPosicion(contador);
        
        String cuerito = "Select * from pokemon where id="+(contador+1);
        
        
        try {
            conn = ClsConexion.getConnection();
            stmt = conn.prepareStatement(cuerito);
            
            rs = stmt.executeQuery();
            if (rs.next()){
                jLabelGene.setText(rs.getNString("generation_id"));
                jLabelEspecie.setText(rs.getNString("species"));
                jLabelAltura.setText(rs.getNString("height"));
                jLabelPeso.setText(rs.getNString("weight"));
                nombrePokemon.setText(rs.getString("name"));
                jLabelID.setText(rs.getNString("id"));
                jLabelColor.setText(rs.getNString("color"));
                jLabelHabitad.setText(rs.getNString("habitat"));
                jLabelCaptura.setText(rs.getNString("capture_rate"));
                jLabelEXP.setText(rs.getNString("base_experience"));
                jLabelFelicida.setText(rs.getNString("base_happiness"));

            } else {
                nombrePokemon.setText("Este pokemon no existe en la pokedex");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            JOptionPane.showMessageDialog(null,"SQL Problem");
        }

            dibujaElPokemonQueEstaEnLaPosicion(contador);
    }//GEN-LAST:event_derActionPerformed

    
    private void izqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_izqActionPerformed
        contador--;
        if(contador <= 0){
            contador = 0;
        }
       // ejecutarPagineo(contador)
        dibujaElPokemonQueEstaEnLaPosicion(contador);
        
        String cuerito = "Select * from pokemon where id="+(contador+1);
        
        
        try {
            
            conn = ClsConexion.getConnection();
            stmt = conn.prepareStatement(cuerito);
            
            rs = stmt.executeQuery();
            
            if (rs.next()){     
                jLabelGene.setText(rs.getNString("generation_id"));
                jLabelEspecie.setText(rs.getNString("species"));
                jLabelAltura.setText(rs.getNString("height"));
                jLabelPeso.setText(rs.getNString("weight"));
                nombrePokemon.setText(rs.getString("name"));
                jLabelID.setText(rs.getNString("id"));
                jLabelColor.setText(rs.getNString("color"));
                jLabelHabitad.setText(rs.getNString("habitat"));
                jLabelCaptura.setText(rs.getNString("capture_rate"));
                jLabelEXP.setText(rs.getNString("base_experience"));
                jLabelFelicida.setText(rs.getNString("base_happiness"));       
                
            } else {
                nombrePokemon.setText("Este pokemon no existe enla pokedex");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
            JOptionPane.showMessageDialog(null,"SQL Problem");
        }

    }//GEN-LAST:event_izqActionPerformed

    private void txtBuscarNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarNombreActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarNombre;
    private javax.swing.JButton btnFiltros;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton der;
    private javax.swing.JPanel imagenPokemon;
    private javax.swing.JButton izq;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAltura;
    private javax.swing.JLabel jLabelCaptura;
    private javax.swing.JLabel jLabelColor;
    private javax.swing.JLabel jLabelEXP;
    private javax.swing.JLabel jLabelEspecie;
    private javax.swing.JLabel jLabelFelicida;
    private javax.swing.JLabel jLabelGene;
    private javax.swing.JLabel jLabelHabitad;
    private javax.swing.JLabel jLabelID;
    private javax.swing.JLabel jLabelPeso;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel nombrePokemon;
    private javax.swing.JRadioButton rbtnTodos;
    private javax.swing.JTextField txtBuscarNombre;
    // End of variables declaration//GEN-END:variables
}
