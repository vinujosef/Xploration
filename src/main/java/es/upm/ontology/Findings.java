package es.upm.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Findings
* @author ontology bean generator
* @version 2016/05/26, 14:02:25
*/
public class Findings implements Concept {

   /**
* Protege name: finding
   */
   private List finding = new ArrayList();
   public void addFinding(Finding elem) { 
     List oldList = this.finding;
     finding.add(elem);
   }
   public boolean removeFinding(Finding elem) {
     List oldList = this.finding;
     boolean result = finding.remove(elem);
     return result;
   }
   public void clearAllFinding() {
     List oldList = this.finding;
     finding.clear();
   }
   public Iterator getAllFinding() {return finding.iterator(); }
   public List getFinding() {return finding; }
   public void setFinding(List l) {finding = l; }

}
