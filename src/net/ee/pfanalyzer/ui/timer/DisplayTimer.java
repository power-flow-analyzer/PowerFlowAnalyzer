/*******************************************************************************
 * Copyright 2019 Markus Gronau
 * 
 * This file is part of PowerFlowAnalyzer.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ee.pfanalyzer.ui.timer;

public class DisplayTimer implements Runnable {
	
	private int timeStep;
	
	private long timeStepSize = 1000 * 60 * 60;
	
	private long time;
	
	private long startTime = 2208985200000l; // "01.01.2040 00:00:00"
	
	private long endTime = 2240604000000l; // "31.12.2040 23:00:00"
	
	public DisplayTimer() {
		time = startTime;
	}
	
	public long getTime() {
		return time;
	}
	
	public int getTimeStep() {
		return timeStep;
	}
	
	public void setTimeStep(int newTimeStep) {
		timeStep = newTimeStep;
		time = startTime + timeStep * timeStepSize;
//		System.out.println("DisplayTimer: set step (new time step: " + timeStep + ")");
	}
	
	public void nextStep() {
		time += timeStepSize;
		if(time > endTime)
			resetTimer();
		else
			timeStep++;
//		System.out.println("DisplayTimer: next step (new time step: " + timeStep + ")");
	}
	
	public void previousStep() {
		time -= timeStepSize;
		if(time < startTime) {
			time = endTime;
			timeStep = (int) ((endTime - startTime) / timeStepSize);
		} else
			timeStep--;
//		System.out.println("DisplayTimer: previous step (new time step: " + timeStep + ")");
	}
	
	public void resetTimer() {
		time = startTime;
		timeStep = 0;
//		System.out.println("DisplayTimer: reset timer (new time step: " + timeStep + ")");
	}

	@Override
	public void run() {
		
	}

}
