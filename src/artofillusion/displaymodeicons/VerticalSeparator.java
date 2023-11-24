/*
 *  Copyright (C) 2019 Petri Ihalainen
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
import javax.swing.*;
import buoy.widget.*;
import artofillusion.ViewerCanvas;
import artofillusion.view.ViewerControl;
import artofillusion.ui.ThemeManager;

/**
	This is a vertical separator especially designed for the DMI.
	It's appearance is defined by the 'verticalbar' image, whose height should match
	the rest of the icons.
*/

public class VerticalSeparator implements ViewerControl
{
  public Widget createWidget(ViewerCanvas view) 
  {
    return new VerticalSeparatorBar(view);
  }
  
  public String getName() 
  {
    return ("Vertical Separator");
  }
  
  public class VerticalSeparatorBar extends RowContainer
  {
    public VerticalSeparatorBar(ViewerCanvas view)
    {
      super();
      BLabel label = new BLabel(ThemeManager.getIcon("displaymodeicons:verticalbar"));
      label.setFocusable(false);
      add(label);
      setDefaultLayout(new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, new Insets(0, 0, 0, 0), new Dimension(0,0)));
    }
  }
}
