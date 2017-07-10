// Copyright (c) 2015 D1SM.net

package net.fs.utils;

import java.util.Vector;


public class NetStatus {

	private Thread mainThread;

	private Vector<SpeedUnit> speedList;
	private SpeedUnit currentUnit;
	
	private int upSpeed=0;
	private int downSpeed=0;
	
	public NetStatus(){
		this(2);
	}
	
	private NetStatus(int averageTime){
		speedList= new Vector<>();
		for(int i=0;i<averageTime;i++){
			SpeedUnit unit=new SpeedUnit();
			if(i==0){
				currentUnit=unit;
			}
			speedList.add(unit);
		}
		mainThread= new Thread(() -> {
            long lastTime=System.currentTimeMillis();
            while(true){
                ////#MLog.println("xxxxxxxxxx");
                if(Math.abs(System.currentTimeMillis()-lastTime)>1000){
                    ////#MLog.println("yyyyyyyyyyy ");
                    lastTime=System.currentTimeMillis();
                    calcuSpeed();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                //	e.printStackTrace();
                    break;
                }
            }
        });
		mainThread.start();
	}


	public int getUpSpeed() {
		return upSpeed;
	}

	public int getDownSpeed() {
		return downSpeed;
	}


	private void calcuSpeed(){
		int ds = 0,us=0;
		for(SpeedUnit unit:speedList){
			ds+=unit.downSum;
			us+=unit.upSum;
		}
		upSpeed=(int) ((float)us/speedList.size());
		downSpeed=(int)(float)ds/speedList.size();
		
		speedList.remove(0);
		SpeedUnit unit=new SpeedUnit();
		currentUnit=unit;
		speedList.add(unit);
	}
	
	public void addDownload(int sum){
		currentUnit.addDown(sum);
	}
	
	public void addUpload(int sum){
		currentUnit.addUp(sum);
	}

}


class SpeedUnit{
	int downSum;
	int upSum;
	SpeedUnit(){
		
	}
	
	void addUp(int n){
		upSum+=n;
	}
	
	void addDown(int n){
		downSum+=n;
	}
}

