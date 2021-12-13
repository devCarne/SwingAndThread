package swing;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SwingProgressBar extends JFrame {

    JTextField textFile;
    JProgressBar progressBar;
    JButton btnStart;

    SwingProgressBar() {
        super("MySQL query progress indicator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocation(500, 300);
        Container container = getContentPane();
        container.setLayout(null);

        final JLabel labelTitle = new JLabel("Data Getter", JLabel.CENTER);
        labelTitle.setBounds(61, 24, 370, 14);
        container.add(labelTitle);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true); // 진행율 % 표시
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setBounds(162, 98, 189, 20);
        container.add(progressBar);

        btnStart = new JButton(" Start ");
        btnStart.setBounds(209, 144, 100, 23);
        btnStart.addActionListener(e -> {
            btnStart.setEnabled(false);
            EventQueue.invokeLater(() -> {
                new MySQLInsertWorkerThread().start();
            });
        });
        container.add(btnStart);

        JLabel labelFileName = new JLabel("File name : ");
        labelFileName.setBounds(99, 70, 57, 14);
        container.add(labelFileName);

        textFile = new JTextField();
        textFile.setBounds(162, 67, 182, 20);
        container.add(textFile);
        textFile.setColumns(15);

        JButton btnFileChooser = new JButton("...");
        btnFileChooser.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int ret = fileChooser.showDialog(null, "Choose File");
            if (ret == JFileChooser.APPROVE_OPTION) {
                textFile.setText(fileChooser.getSelectedFile().toString());
            }
        });
        btnFileChooser.setBounds(354, 66, 26, 24);
        container.add(btnFileChooser);
    }

    class MySQLInsertWorkerThread extends Thread {
        @Override
        public void run() {
            File file = new File(textFile.getText().trim());
            ArrayList<String> mySqlList = new ArrayList<>();

            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = bufferedReader.readLine()) != null){
                    mySqlList.add(line);
                }

                final String mySQLDriver = "com.mysql.cj.jdbc.Driver";
                final String mySQLUrl = "jdbc:mysql://localhost:3306/mysql/";
                Class.forName(mySQLDriver);
                connection = DriverManager.getConnection(mySQLUrl, "mysql", "mysql");

                String sql = "INSERT INTO kms.data (data) VALUES ( ? );";
                preparedStatement = connection.prepareStatement(sql);

                for (int i = 0; i < mySqlList.size(); i++) {
                    String s = mySqlList.get(i);
                    preparedStatement.setString(1, s);
                    preparedStatement.executeUpdate();
                    progressBar.setValue((i * 100) / mySqlList.size());

                    Thread.sleep(10);
                }

            } catch (IOException | ClassNotFoundException | SQLException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
