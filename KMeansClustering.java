import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.FlowLayout;
import java.awt.BorderLayout;

import java.awt.Color;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import java.io.IOException;

import java.awt.Graphics;
import java.util.Random;


public class KMeansClustering extends JFrame implements ActionListener {

    public static final int FRAME_WIDTH = 1280;   // koordinatlarin 1280*720'lik ekrana sigacagi varsayildi.
    public static final int FRAME_HEIGHT = 920;
    public static final int NUMBER_OF_DIGITS = 30;
    

    private JTextField textField;
    private JList<String> liste;
    private JScrollPane scrollPane;
    private JPanel infoPanel;

    private Scanner inputStream;
    private JFileChooser dosya_secici;
    private File dosya;

    private static int[][] koordinatlar;
    private static int nokta_sayisi;
    private static int merkez_sayisi;   // K
    private static int iterasyon_sayisi; 
    private int[][] merkez_koordinatlar;


    public KMeansClustering() {
        setTitle("K-Means Clustering");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLayout(new BorderLayout());

        infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Iterasyon:");
        infoPanel.add(label);

        textField = new JTextField(NUMBER_OF_DIGITS);
        infoPanel.add(textField);

        JLabel label2 = new JLabel("K sayisi:(Center)");
        infoPanel.add(label2);

        
        String[] sayilar = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        liste = new JList<String>(sayilar);

        scrollPane = new JScrollPane(liste);
        infoPanel.add(scrollPane);
 
        JButton selectionButton = new JButton("Dosyadan sec");
        selectionButton.addActionListener(this);
        infoPanel.add(selectionButton);

        JButton actionButton = new JButton("K-Means Clustering");
        actionButton.addActionListener(this);
        infoPanel.add(actionButton);

        add(infoPanel, BorderLayout.SOUTH);

    }

    public KMeansClustering(String s) { // hata ekrani icin
        setTitle(s);
        setSize(FRAME_WIDTH/4, FRAME_HEIGHT/4);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Hatali dosya tipi!");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);

        add(label, BorderLayout.CENTER);
    }

    public void setK(int i) {
        merkez_sayisi = i;
    }
    public static int getK() {
        return merkez_sayisi;
    }
    public void setIterasyonSayisi(int i) {
        iterasyon_sayisi = i;
    }
    public static int getIterasyonSayisi() {
        return iterasyon_sayisi;
    }
    public void setNoktaSayisi(int i) {
        nokta_sayisi = i;
    }
    public static int getNoktaSayisi() {
        return nokta_sayisi;
    }
    

    public void actionPerformed(ActionEvent e) {

        String actionCommand = e.getActionCommand();

        if (actionCommand.equals("Dosyadan sec")) {

            String path = System.getProperty("user.dir");

            dosya_secici = new JFileChooser(path);
            
            int return_val = dosya_secici.showOpenDialog(this);

            if (return_val == JFileChooser.APPROVE_OPTION) {
                dosya = dosya_secici.getSelectedFile();

                String dosya_adi = dosya.getName();
                if (!(dosya_adi.substring(dosya_adi.lastIndexOf("."), dosya_adi.length()).equals(".csv"))) {    // csv tipi haricinde dosya secildiginde hata ekrani olustur
                    KMeansClustering hata_ekrani = new KMeansClustering("Hata Ekrani");
                    hata_ekrani.setVisible(true);
                }

                else {  // koordinatlari oku
                   
                    String metin="";

                    try {
                        inputStream = new Scanner(new FileInputStream(dosya));

                            String line = null;

                        while (inputStream.hasNext()) {

                            line = inputStream.nextLine();

                            metin += line;
                            metin += ",";
                        }
                    
                        inputStream.close();
                    }
                    catch (IOException a) {
                        System.out.println(a.getMessage());
                    }

                    String[] koordinatlar_string = metin.split(",");

                    koordinatlar = new int[koordinatlar_string.length/2][3];

                    int index=0;

                    for (int i=0; i<koordinatlar_string.length; i++) {

                        if (!koordinatlar_string[i].equals("")) {

                        if (i%2==0) {   //x koordinatini belirle
                            koordinatlar[index][0] = Integer.parseInt(koordinatlar_string[i]);
                        }
                        else {  //y koordinatini belirle
                            koordinatlar[index][1] = Integer.parseInt(koordinatlar_string[i]);
                            index++;
                        }

                        }
                    }

                    setNoktaSayisi(koordinatlar.length);
                }
      
            }

        }   // Dosyadan sec sonu

        else if (actionCommand.equals("K-Means Clustering")) {

            String s = liste.getSelectedValue();    // merkez nokta sayisi alindi
            int k_sayisi = Integer.parseInt(s);
            setK(k_sayisi);

            String t = textField.getText();    //iterasyon sayisi alindi
            int i_sayisi = Integer.parseInt(t);
            setIterasyonSayisi(i_sayisi);

            Noktalar p = new Noktalar();
            getContentPane().add(p);

            revalidate();
            repaint();
        }

    }   //actionperformed sonu


    private class Noktalar extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            merkezleriRandomBelirle();  // ilk adimda merkez noktalarinin koordinatlari rastgele belirleniyor.

            for (int a=2; a<=iterasyon_sayisi; a++) {

                    merkezleriBelirle();    // ...diger adimlarda ise kumede bulunan noktalarin koordinatlarina bagli olarak belirleniyor vs.
            }


            g.setColor(Color.BLACK);
            for (int i=0; i<merkez_sayisi; i++) {   // Merkez noktalar cizildi
                g.fillOval(merkez_koordinatlar[i][0], merkez_koordinatlar[i][1], 11, 11);
            }
            
            for (int i=0; i<merkez_sayisi; i++) {

                g.setColor(renkler(i));

                for (int j=0; j<nokta_sayisi; j++) {    // her nokta kendi kumesinin renginde olacak sekilde cizildi
                    
                    if (koordinatlar[j][2] == i) {
                        g.fillOval(koordinatlar[j][0], koordinatlar[j][1], 7, 7);
                    }
                }
            }
            
        }   // paintComponent sonu
    }   // Noktalar classi sonu


    public void merkezleriRandomBelirle() {

        merkez_koordinatlar = new int[merkez_sayisi][2];                        

        for (int i=0; i<merkez_sayisi; i++) {        
            Random r = new Random();

            // koordinatlarin 1280*720'lik ekrana sigacagi varsayildi.
            int x = Math.abs(r.nextInt()) % 1280;    // FRAME_WIDTH
            int y = Math.abs(r.nextInt()) % 720;   // FRAME_HEIGHT

            merkez_koordinatlar[i][0] = x;
            merkez_koordinatlar[i][1] = y;      
          }

          gruplandir();
    }

    public void merkezleriBelirle() {

        for (int i=0; i<merkez_sayisi; i++) {

            merkez_koordinatlar[i][0] = getOrtalamaX(i);
            merkez_koordinatlar[i][1] = getOrtalamaY(i);
          }

          gruplandir();
    }

    public void gruplandir() {

        for (int i=0; i<nokta_sayisi; i++) {

            int hangiMerkezNoktasi = 0;
            double enSonUzaklik = getUzaklik(merkez_koordinatlar[0][0], merkez_koordinatlar[0][1], koordinatlar[i][0], koordinatlar[i][1]);

            if (merkez_sayisi > 1) {

                for (int j=1; j<merkez_sayisi; j++) { // merkezlere olan uzakliklar olculecek, en yakin oldugu merkez noktasi belirlenecek
                    if (getUzaklik(merkez_koordinatlar[j][0], merkez_koordinatlar[j][1], koordinatlar[i][0], koordinatlar[i][1]) < enSonUzaklik) {
                        enSonUzaklik = getUzaklik(merkez_koordinatlar[j][0], merkez_koordinatlar[j][1], koordinatlar[i][0], koordinatlar[i][1]);
                        hangiMerkezNoktasi = j;
                    }
                }
            }
      
            koordinatlar[i][2] = hangiMerkezNoktasi;
      
          }
    }


    public static int getUzaklik(int merkez_x, int merkez_y, int nokta_x, int nokta_y) {
        return (int)Math.sqrt((merkez_x - nokta_x)*(merkez_x - nokta_x) + (merkez_y - nokta_y)*(merkez_y - nokta_y));
    }

    public static int getOrtalamaX(int grup) {

        int sayac=0;
        int sum=0;

        for (int j=0; j<koordinatlar.length; j++) {
            if (koordinatlar[j][2] == grup) {
                sayac++;
                sum+= koordinatlar[j][0];
            }
        }

            return sum/sayac;
    }

    public static int getOrtalamaY(int grup) {

        int sayac=0;
        int sum=0;

        for (int j=0; j<koordinatlar.length; j++) {
            if (koordinatlar[j][2] == grup) {
                sayac++;
                sum+= koordinatlar[j][1];
            }
        }

            return sum/sayac; 
    }


    public static Color renkler(int i) {  // her rengin bir int degeri var

        if (i==0) 
            return Color.ORANGE;
        else if (i==1)
            return Color.GREEN;
        else if (i==2)
            return Color.MAGENTA;
        else if (i==3)
            return Color.CYAN;
        else if (i==4)
            return Color.PINK;
        else if (i==5)
            return Color.RED;
        else if (i==6)
            return Color.YELLOW;
        else if (i==7)
            return Color.BLUE;
        else if (i==8)
            return Color.LIGHT_GRAY;
        else // i==9
            return Color.GRAY;              
    }

}
