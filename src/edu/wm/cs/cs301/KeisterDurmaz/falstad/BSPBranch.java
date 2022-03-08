package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * BSPNodes are used to build a binary tree, where internal nodes keep track of lower and upper bounds of (x,y) coordinates.
 * Leaf nodes carry a list of segments. Branch nodes are internal nodes of the tree.
 * A BSP tree is a data structure to search for a set of segments to put on display in the FirstPersonDrawer.
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class BSPBranch extends BSPNode {
	// left and right branches of the binary tree
    BSPNode lbranch, rbranch; 
    // (x,y) coordinates and (dx,dy) direction
    int x, y, dx, dy;
    
    /**
     * Constructor with values for all internal fields
     * @param px
     * @param py
     * @param pdx
     * @param pdy
     * @param l
     * @param r
     */
    BSPBranch(int px, int py, int pdx, int pdy, BSPNode l, BSPNode r) {
    	lbranch = l;
    	rbranch = r;
    	isleaf = false;
    	x = px; 
    	y = py; 
    	dx = pdx; 
    	dy = pdy;
    	xl = Math.min(l.xl, r.xl);
    	xu = Math.max(l.xu, r.xu);
    	yl = Math.min(l.yl, r.yl);
    	yu = Math.max(l.yu, r.yu);
    }

    public BSPNode getLeftBranch(){
    	return lbranch;
    }
    
    public BSPNode getRightBranch(){
    	return rbranch;
    }

	/**
	 * Store the content of a branch node, in particular its left and right children
	 * 
	 * The method recursively stores BSP nodes for left and right children.
	 * Note that the numbering schemes needs to match with the MazeFileReader class.
	 * 
	 * @param n is the node considered
	 * @param doc document to add data to
	 * @param mazeXML element to add data to
	 * @param number is an index number for this node in the XML format
	 * @return the highest used index number
	 */
    public int store(Document doc, Element mazeXML, int number) {
    	super.store(doc, mazeXML, number) ; //leaves number unchanged
		if (isleaf == true)
			System.out.println("WARNING: isleaf flag and class are inconsistent!");
		// store: x, y, dx, dy
		MazeFileWriter.appendChild(doc, mazeXML, "xBSPNode_" + number, x) ;
		MazeFileWriter.appendChild(doc, mazeXML, "yBSPNode_" + number, y) ;
		MazeFileWriter.appendChild(doc, mazeXML, "dxBSPNode_" + number, dx) ;
		MazeFileWriter.appendChild(doc, mazeXML, "dyBSPNode_" + number, dy) ;
		// recursively store left and right branches
		if (lbranch == null)
		{
			// this is likely to be dead code as BSPBranches seem to have always 2 children
			number++ ;
			MazeFileWriter.appendChild(doc, mazeXML, "xlBSPNode_" + number, Integer.MIN_VALUE) ;
		}
		else
		{
			// recursion
			number++ ;
			//number = MazeFileWriter.storeBSPNode(lbranch, doc, mazeXML, number) ;
			number = lbranch.store(doc, mazeXML, number) ;
		}
		// it is important that the recursion on the left branch updates the number value
		// such that for the nodes on the right branch we use new unique numbers
		if (rbranch == null)
		{
			// this is likely to be dead code as BSPBranches seem to have always 2 children
			number++ ;
			MazeFileWriter.appendChild(doc, mazeXML, "xlBSPNode_" + number, Integer.MAX_VALUE) ;
		}
		else
		{
			// recursion
			number++ ;
			//number = MazeFileWriter.storeBSPNode(rbranch, doc, mazeXML, number) ;
			number = rbranch.store(doc, mazeXML, number) ;
		}
		return number ; // return the last number that was used
    }

    
}
