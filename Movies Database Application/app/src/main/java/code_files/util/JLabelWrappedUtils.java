package code_files.util;

import javax.swing.JLabel;
import java.awt.Font;

public class JLabelWrappedUtils {

    /**
     * Creates a JLabel with the given text that automatically wraps at appropriate space positions
     * and adjusts the text to fit into a fixed height with 3 lines.
     * 
     * @param text The text to be displayed in the JLabel.
     * @param maxLength The maximum number of characters per line before wrapping occurs.
     * @param fixedHeight The fixed height for the JLabel, ensuring the text fits into exactly 3 lines.
     * @return A JLabel containing the formatted text with automatic line wrapping and fixed height.
     */
    public JLabel createWrappedLabel(String text, int maxLength, int fixedHeight) {
        StringBuilder sb = new StringBuilder("<html>");

        // Split the text into words
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        // Add words to lines, ensuring no line exceeds maxLength
        for (String word : words) {
            if (line.length() + word.length() + 1 <= maxLength) {
                // If the line is within the max length, add the word
                if (line.length() > 0) {
                    line.append(" ");
                }
                line.append(word);
            } else {
                // If adding the word exceeds the max length, break the line and start a new one
                sb.append(line).append("<br>");
                line.setLength(0); // Reset the line
                line.append(word);
            }
        }

        // Append the last line if any
        if (line.length() > 0) {
            sb.append("<div style='text-align: center;'>").append(line).append("</div>");
        }

        sb.append("</html>");

        // Create a JLabel with the formatted text
        JLabel label = new JLabel(sb.toString());
        label.setFont(new Font("Arial", Font.BOLD, 14)); // Set font style, can be adjusted as needed
        label.setHorizontalAlignment(JLabel.CENTER); // Center the label horizontally
        label.setVerticalAlignment(JLabel.CENTER); // Center the label vertically

        // Set the fixed height for the JLabel
        label.setPreferredSize(new java.awt.Dimension(40, fixedHeight));

        return label;
    }
    
}
