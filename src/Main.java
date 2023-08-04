import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main
{

    /**
     * @author Tom Wulf Tom.Wlf@uc.edu
     *
     * An example Streams program that Allows the user to pick a file
     * the file is stripped of noise words and then a sorted index of the keywords
     * is created like an index
     *
     *
     */
    public static Set<String> noiseWords = new TreeSet<>();
    public static Set<String> keySet = new TreeSet<>();


    public static void main(String[] args)
    {
        // Use the JFileChooser to pick a file from the system
        // A later version should take a URL parameter!!! TODO!!!

        // stream the file as a stream of String lines
        // break each line into a group of words
        // remove the noise words and add the keywords to the
        // map structure which associates each initial letter with a list
        JFileChooser chooser = new JFileChooser();
        File selectedFile;



        String rec = "";

        Map<String, List<String>> indexMap = new TreeMap<>();
        File workingDirectory = new File(System.getProperty("user.dir"));



        // Typiacally, we want the user to pick the file so we use a file chooser
        // kind of ugly code to make the chooser work with NIO.
        // Because the chooser is part of Swing it should be thread safe.
        chooser.setCurrentDirectory(workingDirectory);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
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
        Path file = Paths.get(workingDirectory.getPath() + "\\src\\" + "English Stop Words.txt");


        try(Stream<String> lines = Files.lines(file))
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