 package infrastructure;



import java.util.ArrayList;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;




 public class HTMLTableParser
   extends HTMLEditorKit.ParserCallback
 {
   private boolean encounteredATableRow = false;
   
   public static ArrayList<String> htmlString = new ArrayList<String>();
   
 
   public void handleText(char[] data, int pos)
   {
     if (this.encounteredATableRow)
     {
       htmlString.add(new String(data));
     }
   } 
 
   public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos)
   {
     if (t == HTML.Tag.TR) this.encounteredATableRow = true;
   }
   
   public void handleEndTag(HTML.Tag t, int pos)
   {
     if (t == HTML.Tag.TR) this.encounteredATableRow = false;
   }
}