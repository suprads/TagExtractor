import javax.swing.*;

public class TagRunner
{
    public static void main(String[] args)
    {
        JFrame frame = new TagFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Tag Extractor");
        frame.setVisible(true);
    }
}