package net.njay;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {
    private Menu currentMenu = null, previousMenu = null;
    private final List<Menu> menus;

    public MenuManager() {
        menus = new ArrayList<>();
    }

    /**
     * Sets a menu instance as the current menu, if the menu has not been loaded before, it is sent to the MenuRegistry to be generated
     *
     * @param menu Menu instance that you which to set as the current menu
     */
    public void setActiveMenu(Menu menu) {
        previousMenu = currentMenu;
        if (hasMenu(menu.getClass()))
            currentMenu = getMenu(menu.getClass());
        else {
            currentMenu = menu;
            currentMenu.setInventory(MenuFramework.getRegistry().generateFreshMenu(menu, menu.getClass()));
            menus.add(currentMenu);
        }
    }

    /**
     * Sets a menu instance as the current menu and removes any other saved instances
     *
     * @param menu Menu instance that you which to set as the current menu
     */
    public void setActiveMenuAndReplace(Menu menu) {
        previousMenu = currentMenu;
        currentMenu = menu;
        currentMenu.setInventory(MenuFramework.getRegistry().generateFreshMenu(menu, menu.getClass()));
        Menu toRemove = getMenu(menu.getClass());
        if (toRemove != null)
            menus.remove(toRemove);
        menus.add(currentMenu);
    }

    /**
     * Set the instance of a previously opened menu as the current active menu
     * <p/>
     * </p>(You must be sure that the menu has previously been opened or else the method does not perform anything)
     *
     * @param clazz Class of the menu that you which to set as the active menus
     */
    public void setPreviouslyOpenedActiveMenu(Class clazz) {
        if (hasMenu(clazz)) setActiveMenu(getMenu(clazz));
    }

    private Menu getMenu(Class clazz) {
        for (Menu m : menus)
            if (m.getClass().equals(clazz))
                return m;
        return null;
    }

    private boolean hasMenu(Class clazz) {
        for (Menu m : menus)
            if (clazz.equals(m.getClass()))
                return true;
        return false;
    }

    /**
     * Gets the currently opened menu
     *
     * @return Menu instance
     */
    public Menu getCurrentMenu() {
        return this.currentMenu;
    }

    /**
     * Gets the previously opened menu
     *
     * @return Previous menu
     */
    @SuppressWarnings("unused")
    public Menu getPreviousMenu() {
        return this.previousMenu;
    }
}