/*
 *  Copyright (C) 2007 François Guillet
 *  Copyright (C) 2015 - 2023 Petri Ihalainen
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
import artofillusion.ui.ThemeManager;
import artofillusion.ui.ToolButtonWidget;
import artofillusion.ui.EditingWindow;
import artofillusion.ui.Translate;
import artofillusion.view.ViewChangedEvent;
import artofillusion.view.ViewerControl;
import artofillusion.*;

public class DisplayModeViewerControl implements ViewerControl
{
  ViewerCanvas currentRendedred = null;
  int beforeRenderMode = ViewerCanvas.RENDER_FLAT;
  boolean renderedBefore = false;
  
  public Widget createWidget(ViewerCanvas view) 
  {
    return new DisplayModeButtons(view);
  }

  public String getName() 
  {
    return ("Display Mode Buttons");
  }
  
  public class DisplayModeButtons extends RowContainer
  {
    private ViewerCanvas view;
    private ToolButtonWidget[] button;

    public DisplayModeButtons(ViewerCanvas view)
    {
      super();
      this.view = view;
      setDefaultLayout(new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, new Insets(0, 0, 0, 0), new Dimension(0,0)));
      view.addEventLink(ViewChangedEvent.class, this, "checkViewState");
      button    = new ToolButtonWidget[6];
      button[0] = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:wireframe"));
      button[1] = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:shaded"));
      button[2] = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:smooth"));
      button[3] = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:textured"));
      button[4] = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:transparent"));
      button[5] = new ToolButtonWidget(ThemeManager.getToolButton(this, "displaymodeicons:rendered"));

      add(button[0], new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, new Insets(0, 3, 0, 0), new Dimension(0,0)));
      for (int i = 1; i < 5; i++) 
        add(button[i]);
      add(button[5], new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, new Insets(0, 0, 0, 3), new Dimension(0,0)));
      
      for (int i = 0; i < 6; i++)
      {
        button[i].addEventLink(ValueChangedEvent.class, this, "buttonPressed");
        button[i].addEventLink(ToolTipEvent.class,      this, "showToolTip");
      }

      checkViewState();
    }

    private void checkViewState() 
    {
      checkRenderMode(view); // Check if this view is in 'rendered' mode
      
      for (int i = 0; i < 6; i++) 
        button[i].setSelected(false);
      switch (view.getRenderMode()) 
      {
        case ViewerCanvas.RENDER_WIREFRAME :
          button[0].setSelected(true);
          break;
        case ViewerCanvas.RENDER_FLAT :
          button[1].setSelected(true);
          break;
        case ViewerCanvas.RENDER_SMOOTH :
          button[2].setSelected(true);
          break;
        case ViewerCanvas.RENDER_TEXTURED :
          button[3].setSelected(true);
          break;
        case ViewerCanvas.RENDER_TRANSPARENT:
          button[4].setSelected(true);
          break;
        case ViewerCanvas.RENDER_RENDERED:
          button[5].setSelected(true);
          break;
      }
    }

    private void buttonPressed(ValueChangedEvent ev) {
      
      ToolButtonWidget pressed = (ToolButtonWidget) ev.getWidget();
      if (!pressed.isSelected()) 
        pressed.setSelected(true);
      else 
      {
        for (int i = 0; i < 6; i++) 
        {
          if (button[i] != pressed && button[i].isSelected()) 
            button[i].setSelected(false);
          else if (button[i] == pressed) 
          {
            switch(i) 
            {
              case 0:
                unsetThisCanvasRendered(view);
                view.setRenderMode(ViewerCanvas.RENDER_WIREFRAME);
                break;
              case 1:
                unsetThisCanvasRendered(view);
                view.setRenderMode(ViewerCanvas.RENDER_FLAT);
                break;
              case 2:
                unsetThisCanvasRendered(view);
                view.setRenderMode(ViewerCanvas.RENDER_SMOOTH);
                break;
              case 3:
                unsetThisCanvasRendered(view);
                view.setRenderMode(ViewerCanvas.RENDER_TEXTURED);
                break;
              case 4:
                unsetThisCanvasRendered(view);
                view.setRenderMode(ViewerCanvas.RENDER_TRANSPARENT);
                break;
              case 5:
                if (currentRendedred != view)
                {
                  if (! renderedBefore) 
                  {
                    // if the view does not have a "before mode" then use the one of the next "rendered" view
                    beforeRenderMode = view.getRenderMode(); 
                    renderedBefore = true;
                  }
                  if (currentRendedred != null)
                    currentRendedred.setRenderMode(beforeRenderMode);
                  beforeRenderMode = view.getRenderMode();
                }
                view.setRenderMode(ViewerCanvas.RENDER_RENDERED);
                break;
            }
          }
        }
      }
    }

    private void unsetThisCanvasRendered(ViewerCanvas view)
    {
      if (currentRendedred == view)
        currentRendedred = null;
    }
    
    private void checkRenderMode(ViewerCanvas view)
    {
      if(view.getRenderMode() == ViewerCanvas.RENDER_RENDERED)
        currentRendedred = view;
    }

    private void showToolTip(ToolTipEvent ev)
    {
      Widget wid = ev.getWidget();
      String tip;
	  
	  // Switch does not work directly on widgets
	  
	  int w;
	  for (w = 0; w < button.length; w++)
		if (wid == button[w])
			break;

	  switch(w)
	  {
		case 0:
		  tip = Translate.text("displaymodeicons:Wireframe");
		  break;
		case 1:
		  tip = Translate.text("displaymodeicons:Shaded");
		  break;
		case 2:
		  tip = Translate.text("displaymodeicons:Smoothed");
		  break;
		case 3:
		  tip = Translate.text("displaymodeicons:Textured");
		  break;
		case 4:
		  tip = Translate.text("displaymodeicons:Transparent");
		  break;
		case 5:
		  tip = Translate.text("displaymodeicons:Rendered");
		  break;
		default:
		  tip = new String();  
	  }

	  if (! tip.isEmpty())
        new BToolTip(tip).processEvent(ev);
    }
  }
}
