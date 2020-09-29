package com.forecast.forecast;

import android.content.Context;
import android.util.Log;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//CLASE CAMERA. SE ENCARGA DE RECIBIR Y DEVOLVER LOS FRAMES, CARGAR LA CÁMARA Y LOS CLASIFICADORES
class Camera implements CameraBridgeViewBase.CvCameraViewListener2{


    private Context context;        //Almacena el contexto de la aplicación. Necesario para algunos métodos
    private CascadeClassifier faceClassifier;   //Almacena el clasificador de la cara
    private CascadeClassifier eyesClassifier;   //Almacena el clasificador de los ojos
    private static String TAG = "CameraTag"; //Variable que se va a utilizar para depurar. Va a contener mensajes de error o éxito
    private JavaCameraView cameraView;  //Se declara el objeto cameraView, que al crear el objeto, se tiene que indicar a qué cameraView corresponde
    private Mat mRGBA;  //los objetos Mat son matrices con la capacidad de contener imágenes enteras
    private int faceHeight = 0;
    private boolean eyesDetected = false;


    public Camera(Context context, JavaCameraView cameraView){
        this.cameraView = cameraView;
        this.context = context;
        cameraView.setCvCameraViewListener(this);   //Se le asigna un listener para poder usar el método onCameraFrame() y que le devuelva a la textura cameraView



    }

    public int getFaceHeight() {
        return faceHeight;
    }

    public boolean getEyesDetected() {
        return eyesDetected;
    }


    //BaseLoaderCallback se encarga de prepar las cosas para los métodos de CameraBridgeViewBase, la interfaz implementada en la actividad



    public BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(context) {
        @Override
        public void onManagerConnected(int status) throws IOException {
            if (status == BaseLoaderCallback.SUCCESS) {//Si se puedo conectar el manager correctamente, habilita la vista de la cámara y se muestra en cameraView

                cameraView.setCameraIndex(1);   //TODO Se usa para determinar que la cámara que se va a usar es la frontal. No poner esta línea indica cámara trasera
                cameraView.setMaxFrameSize(320, 240);
                cameraView.enableView();    //Muestra lo que devuelve onCameraFrame


                //CARGAR CLASIFICADOR DE CARA

                InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                File faceCascadeDir = context.getDir("faceCascade", Context.MODE_PRIVATE);
                File faceCascFile = new File(faceCascadeDir, "haarcascade_frontalface_alt2.xml");
                FileOutputStream fos = new FileOutputStream(faceCascFile);

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                is.close();
                fos.close();

                //CARGAR CLASIFICADOR DE OJO IZQUIERDO

                InputStream iser = context.getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                File eyesCascadeDir = context.getDir("eyesCascade", Context.MODE_PRIVATE);
                File eyesCascFile = new File(eyesCascadeDir, "haarcascade_lefteye_2splits.xml");
                FileOutputStream oser = new FileOutputStream(eyesCascFile);

                byte[] bufferER = new byte[4096];
                int bytesReadER;
                while ((bytesReadER = iser.read(bufferER)) != -1) {
                    oser.write(bufferER, 0, bytesReadER);
                }
                iser.close();
                oser.close();
                //----------------------------------------------------------------------------------

                faceClassifier = new CascadeClassifier(faceCascFile.getAbsolutePath());
                eyesClassifier = new CascadeClassifier(eyesCascFile.getAbsolutePath());


            } else {
                super.onManagerConnected(status);
            }
        }
    };



    //-----MÉTODOS DE LA INTERFAZ CameraBridgeViewBase----




    //Al iniciar la vista de cámara
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat(height, width, CvType.CV_8UC4);     //Se le asigna a mRGBA la cantidad de columnas, filas y el tipo (8bits, cuatro canales)
    }


    //Al cerrar la vista de cámara
    @Override
    public void onCameraViewStopped() {
        mRGBA.release();    //Se libera la matriz
    }


    //En cada frame de la cámara
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        System.gc();    //Limpia basura regularmente. Si se saca, se crashea la app

        mRGBA = inputFrame.rgba();    //Se le asigna el frame de la imagen en RGB al Mat mRGBA

        Core.rotate(mRGBA, mRGBA, Core.ROTATE_90_COUNTERCLOCKWISE); //Rotar la imagen para que quede en la misma orientación que el clasificador

        //DETECTAR CARAS
        MatOfRect faceDetections = new MatOfRect();
        faceClassifier.detectMultiScale(mRGBA,faceDetections);

        for (Rect rect: faceDetections.toArray()){

            Log.d(TAG, "Cara" + (rect.y + rect.height));  //Debug. Por cada cara, indica posición

            faceHeight = rect.y + rect.height;

            Imgproc.rectangle(mRGBA, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0,255,0));
        }

        //DETECTAR OJOS
        MatOfRect eyesDetections = new MatOfRect();
        eyesClassifier.detectMultiScale(mRGBA,eyesDetections);

        if (eyesDetections.toArray().length >0){
            eyesDetected = true;
        }

        for (Rect rect: eyesDetections.toArray()){
            Imgproc.rectangle(mRGBA, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(255,0,0));
        }

        Core.rotate(mRGBA, mRGBA, Core.ROTATE_90_CLOCKWISE); //Rotar la imagen para que quede en la misma orientación que el usuario
        Core.flip(mRGBA,mRGBA,1); //Este giro se realiza para que no esté en espejo

        //Imgproc.resize(mRGBA, mRGBA, mRGBA.size());   //Es necesario ajustar la imagen a exáctamente lo mismo que la original (sin transponer ni espejar) para que no se vea en negro la pantalla

        return mRGBA;  //Devuelve este frame. Es el que se muestra

    }

    public void resetIndicators(){
        faceHeight = 0;
        eyesDetected = false;
    }


    //-----FIN DE MÉTODOS DE LA INTERFAZ CameraBridgeViewBase----


}

