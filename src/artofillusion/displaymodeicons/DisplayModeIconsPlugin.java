/*
 *  Copyright (C) 2007 François Guillet
 *  Copyright (C) 2009 Nik Trevallyn-Jones
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

import java.util.*;
import buoy.widget.*;
import buoy.event.*;
import artofillusion.ui.*;
import artofillusion.*;
import artofillusion.Plugin;
import artofillusion.ViewerCanvas;
import artofillusion.view.*;

public class DisplayModeIconsPlugin implements Plugin
{
  /**
   *  Process messages sent to plugin by AoI (see AoI API description)
   *
   *  @param  message  The message
   *  @param  args   Arguments depending on the message
   */

  Widget[] orientationWidget, scalingWidget;
  EditingWindow window;

  public void processMessage(int message, Object args[]) 
  {
    if (message == Plugin.APPLICATION_STARTING) 
    {  
      // Remove the built-in perspective and navigation controls
      
      List controls = ViewerCanvas.getViewerControls();
      for (int i = 0; i < controls.size(); i++) 
      {
        ViewerControl c = (ViewerControl)controls.get(i);
        if (c instanceof ViewerPerspectiveControl) 
          ViewerCanvas.removeViewerControl(c);
        try
        {
          if (c instanceof ViewerNavigationControl) 
            ViewerCanvas.removeViewerControl(c);
        }
        catch(NoClassDefFoundError error) // If this plugin is on an older AoI version than 3.1, there is no navigation control
        {} 
        catch(Exception exception)
        {}
      }

      // Then add the icon controls

      ViewerCanvas.addViewerControl(1, new AlignToAxesControl());
      ViewerCanvas.addViewerControl(2, new PerspectiveViewerControl());
      ViewerCanvas.addViewerControl(new NavigationModeViewerControl());
      ViewerCanvas.addViewerControl(new VerticalSeparator());
      ViewerCanvas.addViewerControl(new GridViewerControl());
      ViewerCanvas.addViewerControl(new AxesViewerControl());
      ViewerCanvas.addViewerControl(new VerticalSeparator());
      ViewerCanvas.addViewerControl(new DisplayModeViewerControl());
   }

    else if (message == Plugin.SCENE_WINDOW_CREATED || message == Plugin.OBJECT_WINDOW_CREATED) 
    {
      BFrame window = (BFrame) args[0];
      BMenu menu = getDisplayModeMenu(window);
      if (menu != null)
      {
        AllViewsDisplayMenuManager manager = new AllViewsDisplayMenuManager(window);
        
        for (int child = 0; child < menu.getChildCount(); child++)
        {
          ((BCheckBoxMenuItem)(menu.getChild(child))).removeEventLink(CommandEvent.class, window);
          ((BCheckBoxMenuItem)(menu.getChild(child))).addEventLink(CommandEvent.class, manager, "selectionChanged");
        }
     
        // Removing the 'rendered' mode from the AllViews-menu, because 
        // only one view at a time can be in rendered mode.
        // At least the boolean modelling window does not have 'rendered' available option to begin with.

        if (menu.getChildCount() > 5) 
          menu.remove((Widget)menu.getChild(5));
      }

      // Add tool tip texts to the core AoI widgets if we are on an Editing Window.
      // This bit was tricky, because direct access to the ViewControWidets is not provided.

      this.window = (EditingWindow)window; // This will be needed in methods. So far it had been good as BFrame.
      ViewerCanvas[] view = this.window.getAllViews();

      orientationWidget = new Widget[view.length];
      scalingWidget     = new Widget[view.length];
      int sw = 0, ow = 0;

      for (ViewerCanvas v : view)
      {
        List          controls  = v.getViewerControls();
        Map           widgetMap = v.getViewerControlWidgets();
        ViewerControl control;
        Widget        widget;
        for(int c = 0; c < controls.size(); c++)
        {
            control = (ViewerControl)controls.get(c);
            widget  = (Widget)widgetMap.get(control);

            if (control instanceof ViewerOrientationControl)
            {
                widget.addEventLink(ToolTipEvent.class, this, "showOrientationToolTip");
                if (ow < scalingWidget.length)
                    orientationWidget[ow] = widget;
                ow++;
            }
            if (control instanceof ViewerScaleControl)
            {
                widget.addEventLink(ToolTipEvent.class, this, "showScalingToolTip");
                if (sw < scalingWidget.length)
                    scalingWidget[sw] = widget;
                sw++;
           }
        }
      } 
    }
  }

  private void showOrientationToolTip(ToolTipEvent e)
  {
    Widget ew  = e.getWidget();
    for (int w = 0; w < orientationWidget.length; w++)
    for (Widget ow : orientationWidget)
    {
      if (ew == ow)
      {
        new BToolTip(Translate.text("displaymodeicons:orientation")).processEvent(e);
        break;
      }
    }
  }

  private void showScalingToolTip(ToolTipEvent e)
  {
    System.out.println("SCA");
    Widget ew  = e.getWidget();
    for (int w = 0; w < scalingWidget.length; w++)
      if (ew == scalingWidget[w])
      {
        if(window.getAllViews()[w].isPerspective())
          new BToolTip(Translate.text("displaymodeicons:distToTarget")).processEvent(e);
        else
          new BToolTip(Translate.text("displaymodeicons:magnification")).processEvent(e);
        break;
      }
  }

  /**
    AllViewsDisplayMenuManager changes the commands in the DisplayMode menu
    to affect all views except for the "Rendered" mode, which can only be set to 
    one view at at a time.

    We trust, that the menu is arranged in the expected numerical order. It would 
    be safer to use MenuItem names or translation texts.
  */
  
  private class AllViewsDisplayMenuManager 
  {
    BFrame window;

    public AllViewsDisplayMenuManager(BFrame w) 
    {
      window = w;
    }

    private void selectionChanged(CommandEvent ev) {
      
      BMenu menu = getDisplayModeMenu(window);
      if (menu == null) return;
      
      BCheckBoxMenuItem[] items = new BCheckBoxMenuItem[5];
      for (int i = 0; i < 5; i++)
      {
        items[i] = (BCheckBoxMenuItem)(menu.getChild(i));
        items[i].setState(false);
      }
      Widget w = ev.getWidget();
      int mode = -1;
      for (int i = 0; i < 5; i++) 
      {
        if (items[i] == w) 
        {
          switch(i) 
          {
            case 0:
              mode = ViewerCanvas.RENDER_WIREFRAME;
              break;
            case 1:
              mode = ViewerCanvas.RENDER_FLAT;
              break;
            case 2:
              mode = ViewerCanvas.RENDER_SMOOTH;
              break;
            case 3:
              mode = ViewerCanvas.RENDER_TEXTURED;
              break;
            case 4:
              mode = ViewerCanvas.RENDER_TRANSPARENT;
              break;
          }
          items[i].setState(true);
        }
      }
      ViewerCanvas[] views = ((EditingWindow)window).getAllViews();
      if (mode > -1) 
        for (int i = 0; i < 4; i++)
          views[i].setRenderMode(mode);
    }
  }

  public BMenu getDisplayModeMenu(BFrame window)
  {
    // This should find the DisplayMode-menu anywhere in the menuBar
    // (directly in the menuBar or under a sub-menu -- this is not recursive any further.)

    BMenuBar menuBar = window.getMenuBar();
    BMenu barMenu, menu=null;

    for (int i = 0; i < menuBar.getChildCount(); i++)
    {
      barMenu = menuBar.getChild(i);
      if (barMenu instanceof BMenu && ((BMenu)barMenu).getName().endsWith("displayMode"))
        return barMenu;
      else
      {
        for (int j = 0; j < barMenu.getChildCount(); j++)
          if (barMenu.getChild(j) instanceof BMenu)
            if (((BMenu)barMenu.getChild(j)).getName().endsWith("displayMode"))
              menu = (BMenu)barMenu.getChild(j);
      }
    }    
    return menu;
  }
}
