/*
 *  Copyright (C) 2019 - 2023 Petri Ihalainen
 *
 *  This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 2 of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */

package artofillusion.displaymodeicons;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;

import buoy.event.*;
import buoy.widget.*;
import artofillusion.ViewerCanvas;
import artofillusion.ui.ThemeManager;
import artofillusion.ui.ThemeManager.*;
import artofillusion.ui.Translate;
import artofillusion.ui.ToolButtonWidget;
import artofillusion.view.ViewChangedEvent;
import artofillusion.view.ViewerControl;
import artofillusion.ui.EditingWindow;
import artofillusion.*;
import java.net.URL;

public class NavigationModeViewerControl implements ViewerControl
{ 
  public Widget createWidget(ViewerCanvas view) 
  {
    return new NavigationModeButtons(view);
  }

  public String getName() 
  {
    return ("Navigation Mode Menu");
  }

  public class NavigationModeButtons extends ExplicitContainer
  {
    private ViewerCanvas view;
    private boolean enabled = true;
    private BLabel button;
    private BLabel ddArrow;
    private int w, h, wa, ha, selectedMode = 0;
    private LayoutInfo left, right;
    private ImageIcon buttonIcon[], arrowIconLo, arrowIconHi;
    private BLabel arrowLabel, selectedLabel;
    private BPopupMenu menu;
 
    public NavigationModeButtons(ViewerCanvas view)
    {
      super();
      this.view = view;
      view.addEventLink(ViewChangedEvent.class, this, "viewChanged");

      buttonIcon    = new ImageIcon[4];
      buttonIcon[0] = ThemeManager.getIcon("displaymodeicons:navigation_space");
      buttonIcon[1] = ThemeManager.getIcon("displaymodeicons:navigation_tray");
      buttonIcon[2] = ThemeManager.getIcon("displaymodeicons:navigation_fly");
      buttonIcon[3] = ThemeManager.getIcon("displaymodeicons:navigation_drive");
      arrowIconLo   = ThemeManager.getIcon("displaymodeicons:ddArrowLo");
      arrowIconHi   = ThemeManager.getIcon("displaymodeicons:ddArrowHi");
      arrowLabel    = new BLabel(arrowIconLo);
      selectedLabel = new BLabel(buttonIcon[0]);
  
      // Measuring the first buttoIcon and arrowIcon. Assuming the resr are the same size.
      
      w  = buttonIcon[0].getImage().getWidth(null);
      h  = buttonIcon[0].getImage().getHeight(null);
      wa = arrowIconLo.getImage().getWidth(null);
      ha = arrowIconLo.getImage().getHeight(null);

      add(arrowLabel,    new Rectangle (1, 1, wa, ha));
      add(selectedLabel, new Rectangle (0, 0, w, h));

      menu = new BPopupMenu();
      menu.add(Translate.menuItem("displaymodeicons:FreeRotate", this, "setSelection"));
      menu.add(Translate.menuItem("displaymodeicons:Tray",       this, "setSelection"));
      menu.add(Translate.menuItem("displaymodeicons:Fly",        this, "setSelection"));
      menu.add(Translate.menuItem("displaymodeicons:Drive",      this, "setSelection"));
      for (int b = 0; b < buttonIcon.length; b++)
        ((BMenuItem)menu.getChild(b)).setIcon(buttonIcon[b]);

      // This did not happen automatically

      Dimension d = menu.getPreferredSize();
      menu.getComponent().setPreferredSize(d);

      addEventLink(MousePressedEvent.class, 
                   new Object(){void processEvent(MousePressedEvent pressed){
                                menu.show(pressed.getWidget(), 0, h);}});
      addEventLink(MouseEnteredEvent.class, this, "mouseIn");
      addEventLink(MouseExitedEvent.class,  this, "mouseOut");
      addEventLink(ToolTipEvent.class,      this, "showToolTip");
    }

    private void viewChanged()
    {
      try
      {
        int mode = view.getNavigationMode(); // To catch the right error
        selectedLabel.setIcon(buttonIcon[mode]);
      }
      catch(java.lang.NoSuchMethodError error)
      {
        if (enabled)
        {
          loadDisabledIcons();
          enabled = false;
          selectedMode = ((view instanceof SceneViewer)? 1 : 0);
        }
        selectedLabel.setIcon(buttonIcon[selectedMode]);
      }
    }

    private void mouseIn(MouseEnteredEvent e)
    {
      arrowLabel.setIcon(arrowIconLo);
    }

    private void mouseOut(MouseExitedEvent e)
    {
      arrowLabel.setIcon(arrowIconLo);
    }

    private void showToolTip(ToolTipEvent e)
    {
      new BToolTip(Translate.text("displaymodeicons:navigation")).processEvent(e);
    }

    private void setSelection(WidgetEvent e)
    {
      Widget selected = e.getWidget();
      for (int w = 0; w < menu.getChildCount(); w++)
        if (menu.getChild(w) == selected)
          selectedMode = w;

        try
        {
          if (selectedMode > 1)
            view.perspectiveControlEnabled = false;
          else
            view.perspectiveControlEnabled = true;
          view.lastSetNavigation = selectedMode;
          view.setNavigationMode(selectedMode);
        }
        catch(java.lang.NoSuchFieldError error)
        {
          selectedLabel.setIcon(buttonIcon[selectedMode]);
        }
    }

    private void loadDisabledIcons()
    {
      buttonIcon[0] = ThemeManager.getIcon("displaymodeicons:disabled/navigation_space");
      buttonIcon[1] = ThemeManager.getIcon("displaymodeicons:disabled/navigation_tray");
      buttonIcon[2] = ThemeManager.getIcon("displaymodeicons:disabled/navigation_fly");
      buttonIcon[3] = ThemeManager.getIcon("displaymodeicons:disabled/navigation_drive");
      for (int b = 0; b < buttonIcon.length; b++)
        ((BMenuItem)menu.getChild(b)).setIcon(buttonIcon[b]);
    }
  }
}
