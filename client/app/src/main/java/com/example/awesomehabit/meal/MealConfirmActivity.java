package com.example.awesomehabit.meal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Element;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awesomehabit.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class MealConfirmActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 111;
    ImageView imageViewFood;
    Button btnOk;
    Button btnCancel;
    EditText foodName;
    EditText foodCalo;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Meal meal;
            if(imageBitmap!=null) {
                meal = new Meal("No name",0 );
                meal.setBitmap(imageBitmap);
                imageViewFood.setImageBitmap(imageBitmap);
                //Log.d("TFLite",detectImage(imageBitmap)[0]) ;

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        meal.setName(foodName.getText().toString());
                        meal.setCalories(Integer.parseInt( foodCalo.getText().toString()));
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("meal",meal);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                });
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED){
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_confirm);
        imageViewFood = (ImageView) findViewById(R.id.imageViewMealConfirm);
        btnOk=findViewById(R.id.mealConfirmOk);

        foodName=findViewById(R.id.tvFoodName);
        foodCalo=findViewById(R.id.tvCalo);

        btnCancel=findViewById(R.id.mealConfirmCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
    Map<String,Float> floatMap;
    private String[] detectImage(Bitmap imageBitmap){
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(416, 416, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        TensorImage image0 = new TensorImage(DataType.UINT8);
        image0.load(imageBitmap);
        TensorImage tImage;
        tImage = imageProcessor.process(image0);
        try {
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(this, "yolov4-416.tflite");

            Interpreter tflite = new Interpreter(tfliteModel);
            TensorBuffer probabilityBuffer = TensorBuffer.createFixedSize(new int[]{1,11}, DataType.UINT8);

            //tflite.run(tImage.getBuffer(), probabilityBuffer);


            List<String> associatedAxisLabels = FileUtil.loadLabels(this, "label.txt");

            TensorProcessor probabilityProcessor =
                    new TensorProcessor.Builder().add(new NormalizeOp(0, 600)).build();

            TensorLabel labels = new TensorLabel(associatedAxisLabels,
                    probabilityProcessor.process(probabilityBuffer));
            //this is result of the detection
            floatMap = labels.getMapWithFloatValue();

            //sort the result
            //floatMap = sortByValue(floatMap);
            List<String> keys = Arrays.asList(floatMap.keySet().toArray(new String[0]));

            return new String[]{keys.get(0),keys.get(1),keys.get(2)};
        } catch (IOException e) {
            Log.e("TFLite", "run: result notfound.png loadmodel");
        }
        return null;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
