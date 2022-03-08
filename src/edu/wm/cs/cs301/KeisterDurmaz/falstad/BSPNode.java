package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * BSPNodes are used to build a binary tree, where internal nodes keep track of lower and upper bounds of (x,y) coordinates.
 * Leaf nodes carry a list of segments. A BSP tree is a data structure to search for a set of segments to put on display in the FirstPersonDrawer.
 * 
 * Superclass for BSPBranch and Leaf nodes that carry further data. 
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 *  
 */
public class BSPNode {
	/* lower and upper bounds for (x.y) coordinates of segments carried in leaf nodes. */
    public int xl, yl, xu, yu;
    // boolean flag to recognize if node is a leaf node
    // TODO: this redundant and can be checked by looking into the class of an object
    public boolean isleaf;
    
    
	/**
	 * Store the content of a BSPNode including data of branches and leaves as special cases.
	 * @param root is the node considered
	 * @param doc document to add data to
	 * @param mazeXML element to add data to
	 * @param number is an index number for this node in the XML format
	 * @return the highest used index number, in this case the given number
	 */
    int store(Document doc, Element mazeXML, int number) {
    	// xlBSPNode elements
    	MazeFileWriter.appendChild(doc, mazeXML, "xlBSPNode_" + number, xl) ;
    	// ylBSPNode elements
    	MazeFileWriter.appendChild(doc, mazeXML, "ylBSPNode_" + number, yl) ;
    	// xuBSPNode elements
    	MazeFileWriter.appendChild(doc, mazeXML, "xuBSPNode_" + number, xu) ;
    	// yuBSPNode elements
    	MazeFileWriter.appendChild(doc, mazeXML, "yuBSPNode_" + number, yu) ;
    	// isleafBSPNode elements
    	MazeFileWriter.appendChild(doc, mazeXML, "isleafBSPNode_" + number, isleaf) ;
    	
    	return number ; // unchanged
    }
}
