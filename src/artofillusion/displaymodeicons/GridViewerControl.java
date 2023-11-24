/*
 *  Copyright (C) 2007 François Guillet
 *  Changes copyright (C) 2023 Petri Ihalainen
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

import buoy.event.ValueChangedEvent;
import buoy.event.*;
import buoy.widget.*;
import artofillusion.*;
import artofillusion.ui.*;
import artofillusion.view.ViewChangedEvent;
import artofillusion.view.ViewerControl;

public class GridViewerControl implements ViewerControl
{
  public Widget createWidget(ViewerCanvas canvas)
  {
    return new GridViewerControlWidget(canvas);
  }

  public String getName()
  {
    return ("Toggle Grid Button");
  }

  public class GridViewerControlWidget extends RowContainer 
  {
    private ViewerCanvas canvas;
    private ToolButtonWidget button;
    private BFrame window;

    public GridViewerControlWidget(ViewerCanvas canvas) 
    {
      super();
      this.canvas = canvas;
      window = null;
      canvas.addEventLink(ViewChangedEvent.class, this, "viewChanged");
      button = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:grid"));
      button.addEventLink(ValueChangedEvent.class, this, "buttonPressed");
      button.addEventLink(ToolTipEvent.class,      this, "showToolTip");
      viewChanged();
      setDefaultLayout(new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, new Insets(0, 0, 0, 0), new Dimension(0,0)));
      add(button);
    }

    private void viewChanged() 
    {
      button.setSelected(canvas.getShowGrid());
    }

    private void buttonPressed()
    {
      canvas.setGrid(canvas.getGridSpacing(), canvas.getSnapToSubdivisions(), button.isSelected(), canvas.getSnapToGrid());
      canvas.repaint();
    }

    private void showToolTip(ToolTipEvent e)
    {
      if (canvas.getShowGrid())
        new BToolTip(Translate.text("displaymodeicons:hideGrid")).processEvent(e);
      else
        new BToolTip(Translate.text("displaymodeicons:showGrid")).processEvent(e);
    }
  }
}
