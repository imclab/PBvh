import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PBvh {	
	PBvh() {
		root           = NULL;
		total_channels = 0; 
		rate           = 1; 
		loop           = false; 
		playing        = false; 
		play_head      = 0;
		need_update    = false;
	}

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
			println("PBvh: Invalid bvh format.");
			return;
		}
		
		parseHierarchy(data.substring(HIERARCHY_BEGIN, MOTION_BEGIN));
		parseMotion(data.substring(MOTION_BEGIN));

		// TODO: COME BACK TO THIS!
		currentFrame = frames[0]; 

		int index = 0;
		updateJoint(index, currentFrame, root);

		frame_new = false;
	}
	
	void unload() {
		joints.clear();

		root = NULL;

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
	bool isPlaying()				{return playing;}
	
	void setLoop(boolean yn)		{loop = yn;}
	bool isLoop() 					{return loop;}

	void setRate(float iRate) 		{rate = iRate;}
	
	bool isFrameNew()   			{return frame_new;}

	void setFrame(int index) {
		if (ofInRange(index, 0, frames.size()) && getFrame() != index) {
			currentFrame = frames[index];
			play_head = (float)index * frame_time;
			need_update = true;
		}
	}

	int getFrame()					{return floor(play_head / frame_time);}

	void setPosition(float pos) 	{setFrame((float)frames.size() * pos);}
	float getPosition() 			{return play_head / (float)frames.size();}

	float getDuration()				{return (float)frames.size() * frame_time;}
	
	int getNumJoints() 				{return joints.size();}
	PBvhJoint getJoint(int index) 	{return joints.at(index);}
	PBvhJoint getJoint(string name)	{return jointMap[name];}
	
	void update() {
		frame_new = false;
		
		// TODO: Replace ofGetFrameNum() and ofGetLastFrameTime() with appropriate...

		if(playing && ofGetFrameNum() > 1) {
			int last_index = getFrame();

			play_head += ofGetLastFrameTime() * rate;
			int index = getFrame();

			if(index != last_index) {
				need_update = true;

				currentFrame = frames[index];

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
	
	
	// TODO
	void draw();// TODO
	
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