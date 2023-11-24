/*
 *  Copyright (C) 2007 François Guillet
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

import buoy.event.ValueChangedEvent;
import buoy.event.ToolTipEvent;
import buoy.widget.*;
import artofillusion.ViewerCanvas;
import artofillusion.ui.*;
import artofillusion.view.ViewChangedEvent;
import artofillusion.view.ViewerControl;
import artofillusion.object.SceneCamera;

public class PerspectiveViewerControl implements ViewerControl 
{
  public Widget createWidget(ViewerCanvas view) 
  {
    return new PerspectiveViewerControlWidget(view);
  }

  public String getName()
  {
    return ("Perspective Toggle Button");
  }

  public class PerspectiveViewerControlWidget extends RowContainer 
  {
    private boolean enabled = true;
    private ViewerCanvas view;
    private ToolButtonWidget button;

    public PerspectiveViewerControlWidget(ViewerCanvas view)
    {
      super();
      this.view = view;
      setDefaultLayout(new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, new Insets(0, 0, 0, 0), new Dimension(0,0)));
      view.addEventLink(ViewChangedEvent.class, this, "viewChanged");
      button = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:perspective"));
      button.addEventLink(ValueChangedEvent.class, this, "buttonClicked");
      button.addEventLink(ToolTipEvent.class,      this, "showToolTip");
      add(button);
      viewChanged();
    }

    private void viewChanged() 
    {
      button.setSelected(view.isPerspective());
    }

    private void buttonClicked() 
    {
      // ViewerCanvas takes care of whether the perspective change is allowed or not.

      view.setPerspective(! view.isPerspective());
      viewChanged();
    }
    private void showToolTip(ToolTipEvent e)
    {
      if (view.isPerspective())
        new BToolTip(Translate.text("displaymodeicons:toOrthographic")).processEvent(e);
      else
        new BToolTip(Translate.text("displaymodeicons:toPerspective")).processEvent(e);
    }
  }
}

