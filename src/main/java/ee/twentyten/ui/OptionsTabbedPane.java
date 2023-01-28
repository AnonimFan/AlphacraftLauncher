package ee.twentyten.ui;

import javax.swing.JTabbedPane;
import lombok.Getter;

@Getter
public class OptionsTabbedPane extends JTabbedPane {

  private final VersionsOptionsPanel versionsOptionsPanel;

  public OptionsTabbedPane() {
    super();

    this.setTabPlacement(JTabbedPane.TOP);

    this.versionsOptionsPanel = new VersionsOptionsPanel();
    this.add("Versions", this.versionsOptionsPanel);
  }
}