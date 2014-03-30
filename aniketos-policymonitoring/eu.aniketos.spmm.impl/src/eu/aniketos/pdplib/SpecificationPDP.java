/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.aniketos.pdplib;
//package eu.aniketos.spec;

import eu.aniketos.spec.Reaction;
import eu.aniketos.spec.Specification;
import eu.aniketos.spec.Rule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


/**
 * Represent a Conspec policy, made by some attributes tag, Declarations and rules
 * @author Administrator
 */
public class SpecificationPDP extends Specification {

//	protected Scope scope;
//    protected Vector<DeclarationPDP> security_statePDP;
    protected Vector<RulePDP> rulesPDP;
	
	
/*
	protected List<Attribute> attributesPDP;
	protected int maxintPDP, maxlenPDP;
	protected Scope scopePDP;
    protected Vector<Declaration> security_statePDP;
    protected Vector<Rule> rulesPDP;
*/
    /**
     * Creates a new empty policy
     *
     */
	
	public SpecificationPDP() {
		super();
		rulesPDP = new Vector<RulePDP>();
		for(int i = 0; i<rules.size(); i++) {
			rulesPDP.add(new RulePDP(rules.get(i)));
		}
	}
	
    public RulePDP[] getRulesPDP() {
        RulePDP[] r = new RulePDP[rules.size()];
        rulesPDP.copyInto(r);
        return r;
    }

	/*
	

    public Specification() {
    	security_state = new Vector<Declaration>();
        rules = new Vector<Rule>();
        scope = new Scope(Scope.SESSION);
        attributes = new ArrayList<Attribute>();
        
        //These limits are not used for now
        maxint = Integer.MAX_VALUE;
        maxlen = 1000;
    }
    */

    /**
     * Adds @r to the set of rules
     *
     * @param r
     */

    public void add(Rule r) {
    	super.add(r);
    	RulePDP rPDP= new RulePDP(r);
    	rulesPDP.addElement(rPDP);
    }

/*ART
    public String getAttribute(String key) {
    	return null;
    }

    public void setAttribute(String key, String name) {
    }

    public Rule[] getRules() {
        Rule[] r = new Rule[rules.size()];
        rules.copyInto(r);
        return r;
    }
ART*/
/*ART    
    public Declaration[] getDeclarations() {
    	Declaration[] d = new Declaration[security_state.size()];
    	security_state.copyInto(d);
        return d;
    }
ART*/
    /**
     * Adds @decl to the existing policy declarations
     *
     * @param decl
     * @throws PolicyFormatException
     */
/*ART
    public void add(Declaration decl) throws PolicyFormatException {
    	security_state.addElement(decl);
    }
ART*/
    /**
     * Deletes rule @r from the current rules list
     *
     * @param r
     */

    public void remove(Rule r) {
    	super.remove(r);
    	RulePDP rPDP= new RulePDP(r);
    	rulesPDP.removeElement(rPDP);
        //rules.removeElement(r);
    }

    /**
     * Deletes @var from the current declarations
     *
     * @param var
     */
/*ART
    public void remove(Declaration decl) {
    	security_state.removeElement(decl);
    }

ART*/


    /**
     * Initializes the policy reading information from @file
     *
     * @param file
     * @throws IOException
     * @throws PolicyFormatException 
     */
    
/*ART 
   
    public void load(String file) throws IOException, PolicyFormatException {
        try {
            SAXBuilder parser = new SAXBuilder ();
            
            Document dom = parser.build(file);
            loader(dom);
        } 
        catch (JDOMException ex) {
        	ex.printStackTrace();
            // Reactions?
        }catch (IOException ex) {
            ex.printStackTrace();
            // Reactions?
        }
    }
ART*/
    
    /**
     * load a policy from an array of bytes
     * @param policy: the policy
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws JDOMException
     * @throws PolicyFormatException
     */
/*ART
    public void load(byte[] policy) throws IOException, ClassNotFoundException, JDOMException, PolicyFormatException {
        	
    	ByteArrayInputStream bais = new ByteArrayInputStream(policy);
    	SAXBuilder parser = new SAXBuilder();
        Document dom = parser.build(bais);
        loader(dom);
    }
ART*/
    /**
     * load a policy from an input stream
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws JDOMException
     * @throws PolicyFormatException
     */
/*ART
    public void load(InputStream is) throws IOException, ClassNotFoundException, JDOMException, PolicyFormatException {
    	SAXBuilder parser = new SAXBuilder();
        Document dom = parser.build(is);
        loader(dom);
    }
ART*/
/*ART
    private void loader(Document dom) throws IOException, JDOMException, PolicyFormatException {
            Element root = dom.getRootElement();
            attributes = root.getAttributes();
            List<Element> list = root.getChildren();
            ListIterator<Element> iter = list.listIterator();
            while(iter.hasNext()) {
            	Element next = iter.next();
            	if(next.getName().equals(Tag.maxint_tag))
            		maxint=Integer.parseInt(next.getTextTrim());
            	else if(next.getName().equals(Tag.maxlen_tag))
            		maxlen=Integer.parseInt(next.getTextTrim());
            	else if(next.getName().equals(Tag.scope_tag))
            		scope = new Scope(next);
            	else if(next.getName().equals(Tag.securitystate_tag)){
            		Iterator<Element> decl_iter = next.getChildren().iterator();
            		while(decl_iter.hasNext()){
            			this.add(new Declaration(decl_iter.next()));
            		}
            	}
            	else if(next.getName().equals(Tag.rule_tag)){
            		this.add(new Rule(next));
            	}            		
            	
            }
    }
ART*/
    /**
     * Saves the policy in @file
     *
     * @param file
     */
/*ART
    public void save(String file) throws IOException {

        Document dom = toDOM();
        FileOutputStream fos = new FileOutputStream(file);
        XMLOutputter serializer = new XMLOutputter(); 
        serializer.output(dom, fos);
        fos.flush();
        fos.close();
    }
ART*/
    
    /**
     * Saves the policy in a byte array
     *
     * @param file
     */
    /*ART
    public byte[] saveToByteArray() throws IOException {

        Document dom = toDOM();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(dom);
        byte[] ret = baos.toByteArray();
        baos.flush();
        oos.close();
        return ret;
    }

ART*/
  
    /**
     * Converts the current policy to a corresponding Document Object Model
     *
     * @return the policy DOM
     */

/*ART    
    protected Document toDOM() {

        Document dom = new Document();
        Element root = new Element(Tag.specification_tag);

        // attributes of Specification
        for(int i = 0; i<attributes.size(); i++) {
        	Attribute attrCopy=(Attribute)attributes.get(i).clone();
        	attrCopy.detach();
        	root.setAttribute(attrCopy);
        };        
        
        dom.addContent(root);
        Element max_int = new Element(Tag.maxint_tag);
        max_int.setText(""+maxint);
        root.addContent(max_int);
        
        Element max_len = new Element(Tag.maxlen_tag);
        max_len.setText(""+maxlen);
        root.addContent(max_len);
        
        root.addContent(scope.toElement());
        
        Element decl_type = new Element(Tag.securitystate_tag);
        for(int i = 0; i<security_state.size(); i++) {
            Declaration decl = (Declaration) security_state.elementAt(i);
            decl_type.addContent(decl.toElement());
        }
        root.addContent(decl_type);

        for(int i = 0; i<rules.size(); i++) {
            Rule rule = (Rule) rules.elementAt(i);
            Element rule_elm = rule.toElement();
            root.addContent(rule_elm);
        }

        return dom;
    }
ART*/
  
}
