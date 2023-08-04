import javax.accessibility.AccessibleIcon;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagFrame extends JFrame {
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 1000;
    public static Set<String> noiseWords = new TreeSet<>();
    public static Set<String> keySet = new TreeSet<>();
    JFileChooser chooser = new JFileChooser();
    File selectedFile;
    String rec = "";

    Map<String, List<String>> indexMap = new TreeMap<>();
    File workingDirectory = new File(System.getProperty("user.dir"));

    private JTextArea extractedTagsTextArea;
    private JButton textFileButton;
    private JButton stopWordButton;
    private JButton runExtractButton;
    private ActionListener listener;


    /**
     * Constructs the frame.
     */
    public TagFrame() {
        listener = new ChoiceListener();

        createControlPanel();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    class ChoiceListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            loadTextFile();
            loadStopFile();
        }
    }

    public void createControlPanel() {
        JPanel fileReadPanel = createFileRead();
        JPanel stopWordPanel = createStopWord();
        JPanel runExtractPanel = createRunExtract();
        JPanel extractedTagsPanel = createExtractedTags();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 2));
        controlPanel.add(fileReadPanel);
        controlPanel.add(stopWordPanel);
        controlPanel.add(runExtractPanel);
        controlPanel.add(extractedTagsPanel);

        add(controlPanel, BorderLayout.SOUTH);
    }

    public JPanel createFileRead() {
        JPanel panel = new JPanel();
        textFileButton = new JButton("Load Text File");

        textFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTextFile();
            }
        });
        panel.add(textFileButton);

        panel.setBorder(new TitledBorder(new EtchedBorder(), "Load Text File"));

        return panel;
    }


    public JPanel createStopWord() {
        JPanel panel = new JPanel();
        stopWordButton = new JButton("Load Stop Word File");
        stopWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStopFile();
            }
        });

        panel.add(stopWordButton);
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Stop Word"));

        return panel;
    }

    public JPanel createExtractedTags() {
        JPanel panel = new JPanel();
        extractedTagsTextArea = new JTextArea();
        extractedTagsTextArea.setSize(200, 200);
        panel.add(extractedTagsTextArea);
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Extracted Tags"));

        return panel;
    }

    public JPanel createRunExtract() {
        JPanel panel = new JPanel();
        runExtractButton = new JButton("Extract Tags");
        runExtractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runExtract();
            }
        });

        panel.add(runExtractButton);
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Run"));
        return panel;
    }

    public void runExtract()
    {
        selectedFile = chooser.getSelectedFile();
        Path file = Paths.get(workingDirectory.getPath() + "\\src\\");

        try (Stream<String> lines = Files.lines(file))
        {
            lines.forEach(l ->
                    {
                        String[] words = l.split(" ");
                        String w;
                        for (String x : words) {

                            w = x.toLowerCase().trim();
                            w = w.replaceAll("_", " ").trim();
                            w = w.replaceAll("[\\W]", "");
                            w = w.replaceAll("[\\d]", "");

                            System.out.println("Scrapped: " + w);

                            if (!isNoiseWord(w)) {
                                if (indexMap.containsKey(w.substring(0, 1))) {
                                    indexMap.get(w.substring(0, 1)).add(w);
                                } else {
                                    List<String> tl = new LinkedList<String>();
                                    tl.add(w);
                                    indexMap.put(w.substring(0, 1), tl);
                                }
                            }
                        }
                    }
            );

        }
        catch (IOException e) {
            e.printStackTrace();
        }


        keySet = indexMap.keySet();
        for (String k : keySet) {
            System.out.println("Group " + k);

            for (String s : indexMap.get(k)) {
                System.out.println("\t" + s);
            }
        }

        extractedTagsTextArea.append(indexMap.toString());


    }

    public void loadTextFile() {
        chooser.setCurrentDirectory(workingDirectory);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            Path textFile = Paths.get(workingDirectory.getPath() + "\\src\\");
        }
        else{
            System.out.println("Must choose a file to process!");
        }


    }

    public void loadStopFile() {
        chooser.setCurrentDirectory(workingDirectory);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            Path stopFile = Paths.get(workingDirectory.getPath() + "\\src\\");
        }
        else{
            System.out.println("Must choose a file to process!");
        }


    }

    public static boolean isNoiseWord(String word)
    {
        if(noiseWords.isEmpty())
        {
            loadNoiseWords();
        }

        System.out.print("\tTesting : " + word + "\t");

        if(word.length() < 2)
        {
            System.out.println(" len < 2");
            return true;
        }
        else if(noiseWords.contains(word))
        {
            System.out.println(" in Lexicon");
            return true;
        }
        else
        {
            System.out.println("Keyword!");
            return false;
        }

    }

    public static void loadNoiseWords()
    {
        File workingDirectory = new File(System.getProperty("user.dir"));
        Path stopFile = Paths.get(workingDirectory.getPath() + "\\src\\" + "English Stop Words.txt");


        try(Stream<String> lines = Files.lines(stopFile))
        {
            noiseWords = lines.collect(Collectors.toSet());
        }
        catch (IOException e)
        {
            System.out.println("Failed to open the moise word file");
            e.printStackTrace();
        }


        for(String n:noiseWords)
            System.out.println();

        if(noiseWords.isEmpty()) {
            System.out.println("Error Noise word filter is empty.");
        }
        else
        {
            System.out.println("Noise Words File Loaded");
        }
    }
}