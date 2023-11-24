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

import java.awt.Dimension;
import java.awt.Insets;

import buoy.event.*;
import buoy.widget.*;
import artofillusion.ViewerCanvas;
import artofillusion.math.*;
import artofillusion.ui.*;
import artofillusion.view.*;

public class AlignToAxesControl implements ViewerControl
{
  public Widget createWidget(ViewerCanvas view) 
  {
    return new AlignToAxesControlWidget(view);
  }

  public String getName() 
  {
    return ("Align with axes button");
  }

  public class AlignToAxesControlWidget extends RowContainer
  {
    private ViewerCanvas view;
    private Vec3 up, fw;
    private short upZeros, fwZeros;
    private BLabel buttonLabel = new BLabel(ThemeManager.getIcon("displaymodeicons:align"));
    public  AlignToAxesControlWidget(ViewerCanvas view)
    {
      super();
      this.view = view;
      buttonLabel.addEventLink(MouseReleasedEvent.class, this, "buttonPressed");
      buttonLabel.addEventLink(ToolTipEvent.class,       this, "showToolTip");
      view.addEventLink(ViewChangedEvent.class, this, "viewChanged");
      setDefaultLayout(new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, new Insets(0, 0, 0, 0), new Dimension(0,0)));
      add(buttonLabel);
      viewChanged();
    }

    private void viewChanged()
    {
      if (viewIsAligned())
        buttonLabel.setIcon(ThemeManager.getIcon("displaymodeicons:is_aligned"));
      else
        buttonLabel.setIcon(ThemeManager.getIcon("displaymodeicons:not_aligned"));
    }

    private void buttonPressed()
    {
      // At start up ViewAnimation is still null. Therefore getting it here every time.

      ViewAnimation a = view.getViewAnimation();
      if (a == null || a.animatingMove() || a.changingPerspective())
        return;
      buttonLabel.setIcon(ThemeManager.getIcon("displaymodeicons:align_disabled"));
      view.alignWithClosestAxis();
    }

    private boolean viewIsAligned()
    {
      up = view.getCamera().getCameraCoordinates().getUpDirection();
      fw = view.getCamera().getCameraCoordinates().getZDirection();
      upZeros = 0;
      if (up.x == 0) upZeros++;
      if (up.y == 0) upZeros++;
      if (up.z == 0) upZeros++;
      fwZeros = 0;
      if (fw.x == 0) fwZeros++;
      if (fw.y == 0) fwZeros++;
      if (fw.z == 0) fwZeros++;

      if (upZeros > 1 && fwZeros > 1)
        return true;
      return false;
    }

    private void showToolTip(ToolTipEvent e)
    {
        new BToolTip(Translate.text("displaymodeicons:alignment")).processEvent(e);
    }
  }
}
