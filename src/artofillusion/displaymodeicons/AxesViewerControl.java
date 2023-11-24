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
import buoy.event.ToolTipEvent;
import buoy.widget.BToolTip;
import buoy.widget.LayoutInfo;
import buoy.widget.RowContainer;
import buoy.widget.Widget;
import artofillusion.ViewerCanvas;
import artofillusion.ui.ThemeManager;
import artofillusion.ui.ToolButtonWidget;
import artofillusion.ui.Translate;
import artofillusion.view.ViewChangedEvent;
import artofillusion.view.ViewerControl;

public class AxesViewerControl implements ViewerControl
{
  public Widget createWidget(ViewerCanvas canvas) 
  {
    return new AxesViewerControlWidget(canvas);
  }

  public String getName() 
  {
    return ("Axes Toggle Button");
  }
  
  public class AxesViewerControlWidget extends RowContainer
  {
    private ViewerCanvas canvas;
    private ToolButtonWidget button;

    public AxesViewerControlWidget(ViewerCanvas canvas)
    {
      super();
      this.canvas = canvas;
      canvas.addEventLink(ViewChangedEvent.class, this, "viewCanged");
      button = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:axis"));
      button.addEventLink(ValueChangedEvent.class, this, "buttonPressed");
      button.addEventLink(ToolTipEvent.class,      this, "showToolTip");
      setDefaultLayout(new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, new Insets(0, 0, 0, 0), new Dimension(0,0)));
      add(button);
      viewCanged();
    }

    private void viewCanged()
    {
      button.setSelected(canvas.getShowAxes());
    }
    
    private void buttonPressed()
    {
      canvas.setShowAxes(button.isSelected());
      canvas.repaint();
    }

    private void showToolTip(ToolTipEvent e)
    {
      if (canvas.getShowAxes())
        new BToolTip(Translate.text("displaymodeicons:hideAxes")).processEvent(e);
      else
        new BToolTip(Translate.text("displaymodeicons:showAxes")).processEvent(e);
    }

  }
}
