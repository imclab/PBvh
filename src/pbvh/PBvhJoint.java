package pbvh;

import java.util.ArrayList;

import processing.core.PVector;

public class PBvhJoint {
	protected String name;
	protected PBvhJoint parent;
	protected PVector offset;
	protected ArrayList<PBvhJoint> children;
	
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

	/*
	
	// TODO: Matrix methods that need some P5 equiv
	inline const ofMatrix4x4& getMatrix() const { return matrix; }
	inline const ofMatrix4x4& getGlobalMatrix() const { return global_matrix; }
	inline ofVec3f getPosition() const { return global_matrix.getTranslation(); }
	inline ofQuaternion getRotate() const { return global_matrix.getRotate(); }
	

	// WTF?
	inline ofxBvh* getBvh() const { return bvh; }
	*/
		
	
}
