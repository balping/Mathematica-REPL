package repl.simple.mathematica.Actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import repl.simple.mathematica.MathSessionWrapper;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by alex on 2/2/14.
 */
public class MathREPLEvaluateSelectionAction extends MathREPLBaseAction {
    public MathREPLEvaluateSelectionAction() {
        super();
    }

    public void actionPerformed(AnActionEvent e) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(DataKeys.PROJECT.getData(e.getDataContext()));

        // One of the ERROR/INFO/WARNING
        com.intellij.openapi.ui.MessageType messageType = com.intellij.openapi.ui.MessageType.INFO;
        // Get selected text
        final Editor editor = e.getData(DataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        final SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null || selectedText.trim().length() == 0) {
            return;
        }
        final String text = selectedText.trim();
        //TODO: find Mathematica panel and paste&&run it, now just show in popup

        //msp.setInput()
        ToolWindow tw = null;

        tw = ToolWindowManager.getInstance(DataKeys.PROJECT.getData(e.getDataContext())).getToolWindow("Mathematica REPL");

        //for(String s : ToolWindowManager.getInstance(DataKeys.PROJECT.getData(e.getDataContext())).getToolWindowIds())
        //{
        //    System.err.println(s);
        //}


        final MathSessionWrapper msw =  MathSessionWrapper.adopt(tw.getContentManager().getSelectedContent().getComponent());
        JScrollPane c = (JScrollPane)msw.getRootPanel();
        JTextPane j = null;
        try {
            j = (JTextPane)c.getClass().getMethod("getTextPane").invoke(c);
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
        if(null!=j)
        {
            final JTextPane jc = j;
            // On some platforms (e.g., Linux) we need to explicitly give the text pane the focus
            // so it is ready to accept keystrokes. We want to do this after the pane has finished
            // preparing itself (which happens on the Swing UI thread via invokeLater()), so we
            // need to use invokeLater() ourselves.
            ApplicationManager.getApplication().invokeLater(
                    new Runnable() {
                        public void run() {
                            jc.requestFocus();
                            try {
                                jc.getDocument().insertString(jc.getDocument().getLength(), text, null);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                            try {
                                msw.call("evaluateInput");
                            } catch (NoSuchMethodException e1) {
                                e1.printStackTrace();
                            } catch (IllegalAccessException e1) {
                                e1.printStackTrace();
                            } catch (InvocationTargetException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
            );
        }
    }
}