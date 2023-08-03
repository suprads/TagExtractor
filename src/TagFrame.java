import javax.accessibility.AccessibleIcon;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
    private ActionListener listener;


    /**
     * Constructs the frame.
     */
    public TagFrame() {
        // This listener is shared among all components
        listener = new ChoiceListener();

        createControlPanel();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    class ChoiceListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
           loadFile();
        }
    }

    public void createControlPanel() {
        JPanel fileReadPanel = createFileRead();
        JPanel stopWordPanel = createStopWord();
        JPanel extractedTagsPanel = createExtractedTags();

        // Line up component panels

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 2));
        controlPanel.add(fileReadPanel);
        controlPanel.add(stopWordPanel);
        controlPanel.add(extractedTagsPanel);

        // Add panels to content pane

        add(controlPanel, BorderLayout.SOUTH);
    }

    public JPanel createFileRead() {
        JPanel panel = new JPanel();
        textFileButton = new JButton("Choose Text File");

        textFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });
        panel.add(textFileButton);

        panel.setBorder(new TitledBorder(new EtchedBorder(), "Read File"));

        return panel;
    }


    public JPanel createStopWord() {
        JPanel panel = new JPanel();
        stopWordButton = new JButton("Choose Stop Word File");
        panel.add(stopWordButton);
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Stop Word"));

        return panel;
    }

    public JPanel createExtractedTags() {
        JPanel panel = new JPanel();
        extractedTagsTextArea = new JTextArea();
        extractedTagsTextArea.setSize(200, 200);
        panel.add(extractedTagsTextArea);
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Extracted"));

        return panel;
    }

    public void loadFile()
    {
        selectedFile = chooser.getSelectedFile();
        Path file = selectedFile.toPath();
        try (Stream<String> lines = Files.lines(file))
        {
            // Map<BigDecimal, List<Item>> groupByPriceMap =
            //			items.stream().collect(Collectors.groupingBy(Item::getPrice));

            lines.forEach(l ->
                    {
                        String[] words = l.split(" ");
                        String w;
                        for (String x : words) {

                            w = x.toLowerCase().trim();  // Nomalize the words to lower case
                            w = w.replaceAll("_", " ").trim();
                            w = w.replaceAll("[\\W]", "");  // should delete non Alhpanumberics
                            w = w.replaceAll("[\\d]", "");  // should delete digits

                            System.out.println("Scrapped: " + w);

                            if (!isNoiseWord(w)) {
                                //indexMap.add(w.substring(0, 1));
                                // Maybe use MERGE here instead
                                if (indexMap.containsKey(w.substring(0, 1))) {
                                    // get the sublist and add the new instance to it
                                    indexMap.get(w.substring(0, 1)).add(w);
                                } else {
                                    // add a new key and list with one element
                                    List<String> tl = new LinkedList<String>();
                                    tl.add(w);
                                    indexMap.put(w.substring(0, 1), tl);
                                }
                            }
                        }
                    }
            );

        }  // file is closed here
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // System.out.println(indexMap);

           /*
              Display the map instead of dumping it

              Get the set of keys and traverse them
              for each key Display the key as a heading
              Then display the list stored for this key.
             */
        keySet = indexMap.keySet();
        for(String k:keySet)
        {
            System.out.println("Group " + k);

            for(String s: indexMap.get(k))
            {
                System.out.println("\t" + s);
            }
        }





    }
        else
    {
        System.out.println("Must choose a file to process!");
    }

}