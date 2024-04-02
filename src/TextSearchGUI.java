import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TextSearchGUI extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;

    private Path selectedFilePath;

    public TextSearchGUI() {
        super("Text Search");

        originalTextArea = new JTextArea(20, 30);
        filteredTextArea = new JTextArea(20, 30);
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane originalScrollPane = new JScrollPane(originalTextArea);
        JScrollPane filteredScrollPane = new JScrollPane(filteredTextArea);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Original Text"), BorderLayout.NORTH);
        leftPanel.add(originalScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Filtered Text"), BorderLayout.NORTH);
        rightPanel.add(filteredScrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search String: "));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(loadButton);
        bottomPanel.add(quitButton);

        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> search());
        quitButton.addActionListener(e -> quit());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFilePath = fileChooser.getSelectedFile().toPath();
            try {
                String content = Files.readString(selectedFilePath);
                originalTextArea.setText(content);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void search() {
        if (selectedFilePath == null) {
            JOptionPane.showMessageDialog(this, "Please load a file first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String searchTerm = searchField.getText().trim().toLowerCase();
        try (Stream<String> lines = Files.lines(selectedFilePath)) {
            StringBuilder filteredContent = new StringBuilder();
            lines.filter(line -> line.toLowerCase().contains(searchTerm))
                    .forEach(filteredContent::append);
            filteredTextArea.setText(filteredContent.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error searching file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quit() {
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TextSearchGUI::new);
    }
}
