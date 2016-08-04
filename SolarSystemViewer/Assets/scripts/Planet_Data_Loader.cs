using UnityEngine;
using System.Collections;
using System.Text;
using System.IO;
using System.Collections.Generic;
using System;

public class Planet_Data_Loader : MonoBehaviour {

	List<StreamReader> reader;
	List<GameObject> bodies;

	// bodies in the scene
	public GameObject sun;
	public GameObject mercury;
	public GameObject venus;
	public GameObject earth;
	public GameObject mars;
	public GameObject jupiter;
	public GameObject saturn;
	public GameObject uranus;
	public GameObject neptune;
	public GameObject pluto;

	// variables for keeping track of timestep
	private int currentFrame = 0; // index for keeping track of the current data timestep to use
	private int maxFrames = 0; // the max number of frames contained in the data

	// camera control variables
	private float yaw = 0.0f;
	private float pitch = 0.0f;
	private float moveSpeed = 0.1f;


	// Use this for initialization
	void Start () {
		init();
	}
	
	void init() {
		reader = new List<StreamReader>();
		// init list of bodies
		bodies = new List<GameObject>();
		bodies.Add(sun);
		bodies.Add(mercury);
		bodies.Add(venus);
		bodies.Add(earth);
		bodies.Add(mars);
		bodies.Add(jupiter);
		bodies.Add(saturn);
		bodies.Add(uranus);
		bodies.Add(neptune);
		bodies.Add(pluto);
		// init readers for reading the data files
		for (int i = 0; i < bodies.Count; i++) {
			string filename = "Assets/nbody_data/coord" + (i+1);
			StreamReader newReader = new StreamReader(filename, Encoding.Default);
			reader.Add(newReader);
		}
		// scale the planets properly
		scalePlanets();
		// get timestep info
		currentFrame = 0;
		maxFrames = getMaxFrames("Assets/nbody_data/coord1");
		print (maxFrames);
	}

	// Update is called once per frame
	void Update () {
		readNextTimeStep();
		handleCameraInput();
	}

	// properly deletes objects
	private void cleanup() {
		for (int i = 0; i < reader.Count; i++) {
			reader[i].Close();
		}
	}

	// returns the max number of timesteps in the simulation
	private int getMaxFrames(string fileName) {
		int count = 0;
		StreamReader reader = new StreamReader(fileName, Encoding.Default);
		while (reader.ReadLine() != null)
			count ++;
		return count;
	}

	// updates the simulation based on the next timestep
	private void readNextTimeStep() {
		try {
			string line;
			for (int i = 0; i < bodies.Count; i++) {
				for (int j = 0; j < 15; j++) // this for loop is for skipping a given number of data entries
					getNextPosition(reader[i]);
				line = getNextPosition(reader[i]);
				if (line != null) {
					string[] values = line.Split(' ');
					bodies[i].transform.position = new Vector3(float.Parse(values[0]), float.Parse(values[1]), float.Parse(values[2]));
				}
			}
		} catch (Exception e) {
			print (e.Message);
			print ("Simulation reinitialized because end of data was likely reached.");
		}
		
	}

	private String getNextPosition(StreamReader reader) {
		if (currentFrame <= maxFrames) {
			string line = reader.ReadLine();
			currentFrame++;
			return line;
		} else {
			init(); // reset simulation
			throw new Exception();
		}
	}

	// handle wasd and mouse inputs for moving in the simulation
	private void handleCameraInput() {
		if (Input.GetKey("w"))
			transform.Translate(0, 0, moveSpeed, Space.Self);
		if (Input.GetKey("a"))
			transform.Translate(-moveSpeed, 0, 0, Space.Self);
		if (Input.GetKey("s"))
			transform.Translate(0, 0, -moveSpeed, Space.Self);
		if (Input.GetKey("d"))
			transform.Translate(moveSpeed, 0, 0, Space.Self);
		// Update camera rotations 
		if (Input.GetMouseButton(0)) { // left mouse button
			yaw += Input.GetAxis("Mouse X");
			pitch -= Input.GetAxis("Mouse Y");
			transform.eulerAngles = new Vector3(pitch, yaw, 0.0f);
		}
	}

	// scales planet models to be accurate sizes relative to the sun
	private void scalePlanets() {
		// NEEDS TO BE IMPLEMENTED PROPERLY
//		for (int i = 0; i < bodies.Count; i++) {
//			bodies[i].transform.localScale = new Vector3(0.1f, 0.1f, 0.1f);
//		}
		float sunSize = 1.0f;
		// sun
		bodies[9].transform.localScale = new Vector3(sunSize, sunSize, sunSize);
		// mercury
		bodies[1].transform.localScale = new Vector3(sunSize * 1.0f/277.0f, sunSize * 1.0f/277.0f, sunSize * 1.0f/277.0f);
		// venus
		bodies[2].transform.localScale = new Vector3(sunSize  * 1.0f/113.0f, sunSize  * 1.0f/113.0f, sunSize  * 1.0f/113.0f);
		// earth
		bodies[3].transform.localScale = new Vector3(sunSize  * 1.0f/108.0f, sunSize  * 1.0f/108.0f, sunSize  * 1.0f/108.0f);
		// mars
		bodies[4].transform.localScale = new Vector3(sunSize * 1.0f/208.0f, sunSize * 1.0f/208.0f, sunSize * 1.0f/208.0f);
		// jupiter
		bodies[5].transform.localScale = new Vector3(sunSize * 1.0f/9.68f, sunSize * 1.0f/9.68f, sunSize * 1.0f/9.68f);
		// saturn
		bodies[6].transform.localScale = new Vector3(sunSize * 1.0f/11.4f, sunSize * 1.0f/11.4f, sunSize * 1.0f/11.4f);
		// uranus
		bodies[7].transform.localScale = new Vector3(sunSize * 1.0f/26.8f, sunSize * 1.0f/26.8f, sunSize * 1.0f/26.8f);
		// neptune 
		bodies[8].transform.localScale = new Vector3(sunSize * 1.0f/27.7f, sunSize * 1.0f/27.7f, sunSize * 1.0f/27.7f);
		// pluto
		bodies[9].transform.localScale = new Vector3(sunSize * 1.0f/625.0f, sunSize * 1.0f/625.0f, sunSize * 1.0f/625.0f);	
	}
}

