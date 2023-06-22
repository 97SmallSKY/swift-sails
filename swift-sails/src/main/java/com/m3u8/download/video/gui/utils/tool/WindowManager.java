package com.m3u8.download.video.gui.utils.tool;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Small_Tsk
 * @create 2023-06-11
 **/
public class WindowManager {

    private static List<JPanel> LIST = new ArrayList<>();


    public static void showWindow(Class< ? extends JComponent> ...components) {
        hideOrShowWindow(true, components);
    }

    public static void hideWindow(Class< ? extends JComponent> ...components) {
            hideOrShowWindow(false, components);
    }

    private static void hideOrShowWindow(boolean flag, Class< ? extends JComponent> ...components) {
        for (Class< ? extends JComponent>  component : components) {
            try {
                component.getDeclaredConstructor().newInstance().setVisible(flag);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
