package us.ichun.mods.tabula.gui;

import ichun.client.render.RendererHelper;
import net.minecraft.util.StatCollector;
import us.ichun.mods.tabula.gui.element.Element;
import us.ichun.mods.tabula.gui.element.ElementMinimize;

import java.util.ArrayList;

public class GuiWindow
{
    public int level;//not sure if I need this

    public final GuiWorkspace workspace;

    public int posX;
    public int posY;
    public int width;
    public int height;
    public int clickX;
    public int clickY;
    public int clickId;

    public int minWidth;
    public int minHeight;

    public String titleLocale;
    public boolean hasTitle; //if it has title, it can minimize.

    public boolean docked;
    public boolean minimized;

    public ArrayList<Element> elements = new ArrayList<Element>();

    public final int BORDER_SIZE = 3;

    public GuiWindow(GuiWorkspace parent, int x, int y, int w, int h, int minW, int minH, String title, boolean hasTit)
    {
        workspace = parent;

        posX = x;
        posY = y;
        width = w;
        height = h;
        minWidth = minW;
        minHeight = minH;

        titleLocale = title;
        hasTitle = hasTit;

        if(hasTitle)
        {
            elements.add(new ElementMinimize(this, width - 13, 2, 10, 10, 0));
        }
    }

    public void draw(int mouseX, int mouseY) //4 pixel border?
    {
        //TODO remember to draw the title
        if(!minimized)
        {
            if(docked)
            {
                RendererHelper.drawColourOnScreen(34, 34, 34, 255, posX + 1, posY + 1, getWidth() - 2, getHeight() - 2, 0);
            }
            else
            {
                RendererHelper.drawColourOnScreen(150, 150, 150, 255, posX + 1, posY + 1, getWidth() - 2, getHeight() - 2, 0);
                RendererHelper.drawColourOnScreen(34, 34, 34, 255, posX + BORDER_SIZE, posY + BORDER_SIZE, getWidth() - (BORDER_SIZE * 2), getHeight() - (BORDER_SIZE * 2), 0);
            }
        }
        RendererHelper.drawColourOnScreen(150, 150, 150, 255, posX + 1, posY + 1, getWidth() - 2, 12, 0);
        String titleToRender = StatCollector.translateToLocal(titleLocale);
        while(titleToRender.length() > 1 && workspace.getFontRenderer().getStringWidth(titleToRender) > getWidth() - (BORDER_SIZE * 2) - workspace.getFontRenderer().getStringWidth(" _"))
        {
            if(titleToRender.endsWith("..."))
            {
                titleToRender = titleToRender.substring(0, titleToRender.length() - 4) + "...";
            }
            else
            {
                titleToRender = titleToRender.substring(0, titleToRender.length() - 1) + "...";
            }
        }
        workspace.getFontRenderer().drawString(titleToRender, posX + 4, posY + 3, 0xffffff, false);

        for(Element element : elements)
        {
            if(element.ignoreMinimized && minimized || !minimized)
            {
                element.draw(mouseX, mouseY, mouseX >= element.posX && mouseX <= element.posX + element.width && mouseY >= element.posY && mouseY <= element.posY + element.height);
            }
        }
    }

    public int onClick(int mouseX, int mouseY, int id) //returns > 0 if clicked on title//border with LMB.
    {
        clickX = mouseX;
        clickY = mouseY;
        clickId = id;

        boolean clickedElement = false;
        for(Element element : elements)
        {
            if(mouseX >= element.posX && mouseX <= element.posX + element.width && mouseY >= element.posY && mouseY <= element.posY + element.height && (minimized && element.ignoreMinimized || !minimized))
            {
                element.onClick(mouseX, mouseY, id);
                clickedElement = true;
            }
        }
        if(!clickedElement)
        {
            int borderClick = clickedOnBorder(mouseX, mouseY, id);
            if(borderClick > 1)
            {
                return borderClick + 2;
            }
            else if(clickedOnTitle(mouseX, mouseY, id))
            {
                return 1;
            }
        }
        return 0;
    }

    public int clickedOnBorder(int mouseX, int mouseY, int id)//only left clicks
    {
        if(!docked && id == 0 && !minimized)
        {
            return ((mouseY <= BORDER_SIZE + 1) ? 1 : 0) + (((mouseX <= BORDER_SIZE + 1) ? 1 : 0) << 1) + (((mouseY >= getHeight() - BORDER_SIZE - 1) ? 1 : 0) << 2) + (((mouseX >= getWidth() - BORDER_SIZE - 1) ? 1 : 0) << 3) + 1;
        }
        return 0;
    }

    public boolean clickedOnTitle(int mouseX, int mouseY, int id)
    {
        return mouseX >= 0 && mouseX <= getWidth() && mouseY >= 0 && mouseY <= 10;
    }

    public void resized()
    {
        for(Element element : elements)
        {
            element.resized();
        }
    }

    public void toggleMinimize()
    {
        minimized = !minimized;
    }

    public int getHeight()
    {
        return minimized ? 13 : height;
    }

    public int getWidth()
    {
        return width;
    }
}
