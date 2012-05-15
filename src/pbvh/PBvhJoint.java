package pbvh;

import java.util.ArrayList;

import processing.core.PMatrix3D;
import processing.core.PVector;

public class PBvhJoint {
	protected String name;
	protected PBvhJoint parent;
	protected PVector offset;
	protected ArrayList<PBvhJoint> children;
	protected PMatrix3D matrix;
	protected PMatrix3D global_matrix;
	
	PBvhJoint(String n, PBvhJoint p){
		name = n;
		parent = p;
		
		children = new ArrayList();
	}
	
	public String getName() 		           { return name; }
	public PVector getOffset() 		           { return offset; }
	public PBvhJoint getParent()  	           { return parent; }
	public boolean isRoot()  				   { return parent == null; }
	public ArrayList<PBvhJoint> getChildren()  { return children; }
	public boolean isSite()  				   { return children.size() == 0; }
	public PMatrix3D getMatrix()  			   { return matrix; }
	public PMatrix3D getGlobalMatrix() 	   	   { return global_matrix; }
	
	public PVector getPosition() {
		return new PVector(matrix.m30, matrix.m31, matrix.m32);
	}
	
	/*
	// TODO: Matrix methods that need some P5 equiv
	inline ofQuaternion getRotate() const { return global_matrix.getRotate(); }
	
	*/
	
//	protected PBvh bvh;
//	private PBvh getBvh() { return bvh; }



	
}
