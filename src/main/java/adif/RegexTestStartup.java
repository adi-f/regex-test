package adif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.text.StringEscapeUtils;

class RegexTestStartup {
  enum Result {
    MATCH("matches", Color.GREEN.darker()),
    NO_MATCH("no match", Color.ORANGE),
    INVALID_REGEX("invalid regex", Color.RED);

    private final String text;
    private final Color color;

    Result(String text, Color color) {
      this.text = text;
      this.color = color;
    }

    public String getText() {
      return text;
    }

    public Color getColor() {
      return color;
    }
  }

  public static void main(String[] args) {
    Font monospaced = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    JFrame window = new JFrame();
    GridBagLayout layout = new GridBagLayout();
    window.setTitle("RegEx Tester");
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    window.setLayout(layout);
    window.setSize(500, 150);

    JTextField regexInput = new JTextField();
    regexInput.setFont(monospaced);
    regexInput.setText("");

    JCheckBox stringEscapeInput = new JCheckBox();
    JCheckBox matchAllInput = new JCheckBox();

    JTextField sampleInput = new JTextField();
    sampleInput.setFont(monospaced);
    sampleInput.setText("");

    JLabel resultOutput = new JLabel();

    window.add(new JLabel("RegEx:"), constraint(0, 0));
    window.add(regexInput, constraint(1, 0, 3));

    window.add(flow(
        stringEscapeInput, new JLabel("escape string"),
        gap(30),
        matchAllInput, new JLabel("match all")
      ), constraint(1, 1));

    window.add(new JLabel("Sample:"), constraint(0, 2));
    window.add(sampleInput, constraint(1, 2, 3));

    window.add(new JLabel("Result:"), constraint(0, 3));
    window.add(resultOutput, constraint(1, 3, 3));

    Listener onInput = evaluateAndShowResult(regexInput, stringEscapeInput, matchAllInput, sampleInput, resultOutput);
    regexInput.getDocument().addDocumentListener(onInput);
    stringEscapeInput.addActionListener(onInput);
    matchAllInput.addActionListener(onInput);
    sampleInput.getDocument().addDocumentListener(onInput);

    window.setVisible(true);
  }

  private static Listener evaluateAndShowResult(
      JTextField regexInput,
      JCheckBox stringEscapeInput,
      JCheckBox matchAllInput,
      JTextField sampleInput,
      JLabel resultOutput) {

    return new Listener(() -> {
      String regex = regexInput.getText();
      boolean escapeString = stringEscapeInput.isSelected();
      boolean matchAll = matchAllInput.isSelected();
      String sample = sampleInput.getText();

      Result result = evaluate(regex, escapeString, matchAll, sample);

      resultOutput.setText(result.getText());
      resultOutput.setForeground(result.getColor());
    });
  }

  private static Result evaluate(String regex, boolean escapeString, boolean matchAll, String sample) {
    if(escapeString) {
      regex = StringEscapeUtils.unescapeJava(regex);
    }
    try {
      if(matchAll) {
        return Pattern.compile(regex).matcher(sample).matches() ? Result.MATCH : Result.NO_MATCH;
      } else {
        return Pattern.compile(regex).matcher(sample).find() ? Result.MATCH : Result.NO_MATCH;
      }
    } catch (PatternSyntaxException e) {
      return Result.INVALID_REGEX;
    }
  }

  private static GridBagConstraints constraint(int col, int row) {
    return constraint(col, row, 0);
  }

  private static GridBagConstraints constraint(int col, int row, double weightx) {
    GridBagConstraints constraint = new GridBagConstraints();
    constraint.fill = GridBagConstraints.HORIZONTAL;
    constraint.gridx = col;
    constraint.gridy = row;
    constraint.weightx = weightx;
    constraint.insets = new Insets(3, 10, 3, 10);
    return constraint;
  }

  private static JComponent flow(JComponent ... components) {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 3));
    for (JComponent component : components) {
      panel.add(component);
    }
    return panel;
  }

  private static JComponent gap(int width) {
    JPanel panel = new JPanel();
    panel.setLayout(null);
    panel.setPreferredSize(new Dimension(width, 1));
    return panel;
  }

  private static class Listener implements ActionListener, DocumentListener {

    private final Runnable callback;

    private Listener(Runnable callback) {
      this.callback = callback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      callback.run();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      callback.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      callback.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      callback.run();
    }
  }
}
