package constants;

import java.util.Locale;

public class S3Constants {
	public static final String ACCESS_KEY_ID = "AKIAJQR7T44WRW3CA4OA";
	public static final String SECRET_KEY = "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";
	
	public static final String PICTURE_BUCKET = "picture-bucket";
	public static final String PICTURE_NAME = "NameOfThePicture";
	
	
	public static String getPictureBucket() {
		return ("my-unique-name" + ACCESS_KEY_ID + PICTURE_BUCKET).toLowerCase(Locale.US);
	}
}
