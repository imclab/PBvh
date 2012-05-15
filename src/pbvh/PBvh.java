/**

Processing port of ofxBvh from perfume-dev:
https://github.com/perfume-dev/example-openFrameworks/tree/9563bdf21b9d0ffaa735624a2edc7ad6468ee7a3/ofxBvh

ported by Patrick Hebron and Greg Borenstein

**/

package pbvh;

import processing.core.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PBvh {	
	
	protected float play_head;
	protected ArrayList<Float> currentFrame; // looks like this should be a FrameData object
	protected boolean loop;
	protected boolean need_update;
	protected boolean frame_new;
	protected int num_frames;
	protected float rate;
	protected boolean playing;
	protected PBvhJoint root;
	protected int total_channels;
	protected PApplet parent;
	protected ArrayList<ArrayList<Float>> frames;
	protected float frame_time;
	protected ArrayList<PBvhJoint> joints;
	protected HashMap <String, PBvhJoint>jointMap;
	protected int frameCount;

	
	PBvh(PApplet p, String path) {
		root           = null;
		total_channels = 0; 
		rate           = 1; 
		loop           = false; 
		playing        = false; 
		play_head      = 0;
		need_update    = false;
		parent		   = p;
		frameCount	   = 0;
		
		joints = new ArrayList();
		frames = new ArrayList();
		currentFrame = new ArrayList();
		jointMap = new HashMap();
		
		load(path);
	}
	
	// TODO:
	void parseHierarchy(String data){}
	void parseMotion(String data){}
	void updateJoint(int index, ArrayList<Float>frame_data, PBvhJoint joint){}


	void load(String path) {
		File file = new File(path);
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            // repeat until all lines is read
            while ((text = reader.readLine()) != null) {
                contents.append(text)
                        .append(System.getProperty(
                                "line.separator"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

		String data = contents.toString();
		
		int HIERARCHY_BEGIN = data.indexOf("HIERARCHY");
		int MOTION_BEGIN    = data.indexOf("MOTION");

		if(HIERARCHY_BEGIN == -1 || MOTION_BEGIN == -1) {
			parent.println("PBvh: Invalid bvh format.");
			return;
		}
		
		parseHierarchy(data.substring(HIERARCHY_BEGIN, MOTION_BEGIN));
		parseMotion(data.substring(MOTION_BEGIN));

		// TODO: COME BACK TO THIS!
		currentFrame = frames.get(0); 

		int index = 0;
		updateJoint(index, currentFrame, root);

		frame_new = false;
	}
	
	
	
	void unload() {
		joints.clear();

		root = null;

		frames.clear();
		currentFrame.clear();

		num_frames = 0;
		frame_time = 0;

		rate = 1;
		play_head = 0;
		playing = false;
		loop = false;

		need_update = false;
	}
	
	void play() 					{playing = true;}
	void stop() 					{playing = false;}
	boolean isPlaying()				{return playing;}
	
	void setLoop(boolean yn)		{loop = yn;}
	boolean isLoop() 			    {return loop;}

	void setRate(float iRate) 		{rate = iRate;}
	
	boolean isFrameNew()   			{return frame_new;}

	void setFrame(int index) {
		if(index >= 0 && index < frames.size() && getFrame() != index) {
			currentFrame = frames.get(index);
			play_head = (float)index * frame_time;
			need_update = true;
		}
	}

	int getFrame()					{return parent.floor(play_head / frame_time);}

	void setPosition(float pos) 	{setFrame((int)((float)frames.size() * pos));}
	float getPosition() 			{return play_head / (float)frames.size();}

	float getDuration()				{return (float)frames.size() * frame_time;}
	
	int getNumJoints() 				{return joints.size();}
	PBvhJoint getJoint(int index) 	{return joints.get(index);}
	PBvhJoint getJoint(String name)	{return jointMap.get(name);}
	
	void update() {
		frame_new = false;
		
		// TODO: Replace ofGetLastFrameTime() with appropriate...
		//		 => not sure that exists. may need to keep track manually...
		if(playing && frameCount > 1) {
			int last_index = getFrame();

			// FIXME: this should be the P5 equivalent of ofGetLastFrameTime() 
			float lastFrameTime = (float)0.2;
			play_head += lastFrameTime * rate;
			int index = getFrame();

			if(index != last_index) {
				need_update = true;

				currentFrame = frames.get(index);

				if (index >= frames.size()) {
					if (loop)
						play_head = 0;
					else
						playing = false;
				}

				if (play_head < 0)
					play_head = 0;
			}
		}

		if(need_update) {
			need_update = false;
			frame_new = true;

			int index = 0;
			updateJoint(index, currentFrame, root);
		}
	}
	
	//FIXME
	// this OpenGL bizness needs to get translate to processing
	void billboard(){
		/*
		 	GLfloat m[16];
	glGetFloatv(GL_MODELVIEW_MATRIX, m);
	
	float inv_len;
	
	m[8] = -m[12];
	m[9] = -m[13];
	m[10] = -m[14];
	inv_len = 1. / sqrt(m[8] * m[8] + m[9] * m[9] + m[10] * m[10]);
	m[8] *= inv_len;
	m[9] *= inv_len;
	m[10] *= inv_len;
	
	m[0] = -m[14];
	m[1] = 0.0;
	m[2] = m[12];
	inv_len = 1. / sqrt(m[0] * m[0] + m[1] * m[1] + m[2] * m[2]);
	m[0] *= inv_len;
	m[1] *= inv_len;
	m[2] *= inv_len;
	
	m[4] = m[9] * m[2] - m[10] * m[1];
	m[5] = m[10] * m[0] - m[8] * m[2];
	m[6] = m[8] * m[1] - m[9] * m[0];
	
	glLoadMatrixf(m);
		 
		 */
		
	}
	
	// TODO
	void draw(){
		parent.pushStyle();
		
		
		for (int i = 0; i < joints.size(); i++){
			PBvhJoint o = joints.get(i);
			parent.pushMatrix();
		
			parent.applyMatrix(o.getGlobalMatrix());
			
			if (o.isSite()){
				parent.fill(255,255,0);
				billboard();
				parent.ellipse(0,0,6,6);
			}
			else if (o.getChildren().size() == 1){
				parent.fill(255);		
				billboard();
				parent.ellipse(0,0,2,2);
			}
			else if (o.getChildren().size() > 1){
				if (o.isRoot()){
					parent.fill(0,0,255);
				} else {
					parent.fill(0,255,0);
					
				}
				
				billboard();
				parent.ellipse(0,0,4,4);
			}
			
			
			parent.popMatrix();
		}
		
		parent.popStyle();
	}
}


	/*
protected:
	
	typedef vector<float> FrameData;
	
	int total_channels;
	
	PBvhJoint* root;
	vector<PBvhJoint*> joints;
	map<string, PBvhJoint*> jointMap;
	
	vector<FrameData> frames;
	FrameData currentFrame;
	
	int num_frames;
	float frame_time;
	
	float rate;
	
	boolean playing;
	float play_head;
	
	boolean loop;
	boolean need_update;
	boolean frame_new;
	
	// TODO
	void parseHierarchy(const string& data);// TODO
	PBvhJoint* parseJoint(int& index, vector<string> &tokens, PBvhJoint *parent);// TODO
	void updateJoint(int& index, const FrameData& frame_data, PBvhJoint *joint);// TODO
	void parseMotion(const string& data);// TODO
	
};
*/
