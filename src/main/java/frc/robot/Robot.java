/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import frc.robot.GripPipeline;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.vision.VisionThread;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  
  private VisionThread visionThread;
  
  private final Object imgLock = new Object();
  public static final int cameraResX = 0;
  public static final int cameraResY = 0;
  private  static ArrayList<Rect> printTarget;
  private double centerX = 0;

  @Override
  public void robotInit() {
    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
      camera.setBrightness(0);
      camera.setFPS(0);
      camera.setResolution(cameraResX, cameraResY);
    CvSource outputStream = CameraServer.getInstance().putVideo("Stream", cameraResX, cameraResY);
    Mat frame = new Mat();
    visionThread = new VisionThread(camera, new GripPipeline(), pipeline-> {
      if (!pipeline.filterContoursOutput().isEmpty()) {
         synchronized (imgLock) {
              ArrayList<Rect> unknownTarget = new ArrayList<>();
              for (int i = 0; i < pipeline.filterContoursOutput().size(); i++) {
                Rect target = Imgproc.boundingRect(pipeline.filterContoursOutput().get(i));
                unknownTarget.add(target);
              } 
              printTarget = unknownTarget;
              ArrayList<Rect> filterRect = new ArrayList<>();
              for (int i = 0; i < unknownTarget.size(); i++) {
                double TargetPix = unknownTarget.get(i).width/unknownTarget.get(i).height;
                double FOVPix = cameraResX * cameraResY;
                double Targetft = 111; 
                double distanceAR = (Targetft * FOVPix) / (2 * TargetPix * 0.499);
                System.out.println("AR: "+ distanceAR + ": "+ i);
  
          }
        }
      }
     
    
    
    });      
    
  }
}

 