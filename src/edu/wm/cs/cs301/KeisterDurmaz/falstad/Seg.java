package edu.wm.cs.cs301.KeisterDurmaz.falstad;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A segment is a continuous sequence of walls in the maze.
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class Seg {
	public int x, y, dx, dy, dist;
	public int[] col;
	public int colVal;
	public boolean partition, seen;
	MazePanel panel;

	/**
	 * Constructor
	 * @param psx
	 * @param psys
	 * @param pdx
	 * @param pdy
	 * @param cl
	 * @param cc
	 */
	Seg(MazePanel panel, int psx, int psy, int pdx, int pdy, int cl, int cc) {
		this.panel = panel;
		x = psx;
		y = psy;
		dx = pdx;
		dy = pdy;
		dist = cl;
		seen = false;
		cl /= 4;
		int add = (dx != 0) ? 1 : 0;
		int part1 = cl & 7;
		int part2 = ((cl >> 3) ^ cc) % 6;
		colVal = ((part1 + 2 + add) * 70)/8 + 80;
		
		switch (part2) {
		case 0: col = new int[]{colVal, 20, 20}; break;
		case 1: col = new int[]{20, colVal, 20}; break;
		case 2: col = new int[]{20, 20, colVal}; break;
		case 3: col = new int[]{colVal, colVal, 20}; break;
		case 4: col = new int[]{20, colVal, colVal}; break;
		case 5: col = new int[]{colVal, 20, colVal}; break;
		default: col = new int[]{20, 20, 20}; break;
		}
	}

	int getDir() {
		if (dx != 0)
			return (dx < 0) ? 1 : -1;
		return (dy < 0) ? 2 : -2;
	}
	
	void storeSeg(Document doc, Element mazeXML, int number, int i) {
		MazeFileWriter.appendChild(doc, mazeXML, "distSeg_" + number+ "_" + i, dist) ;
		MazeFileWriter.appendChild(doc, mazeXML, "dxSeg_" + number+ "_" + i, dx) ;
		MazeFileWriter.appendChild(doc, mazeXML, "dySeg_" + number+ "_" + i, dy) ;
		MazeFileWriter.appendChild(doc, mazeXML, "partitionSeg_" + number+ "_" + i, partition) ;
		MazeFileWriter.appendChild(doc, mazeXML, "seenSeg_" + number+ "_" + i, seen) ;
		MazeFileWriter.appendChild(doc, mazeXML, "xSeg_" + number+ "_" + i, x) ;
		MazeFileWriter.appendChild(doc, mazeXML, "ySeg_" + number+ "_" + i, y) ;
		MazeFileWriter.appendChild(doc, mazeXML, "colSeg_" + number+ "_" + i, panel.getRGB(panel.col)) ;
	}
}
