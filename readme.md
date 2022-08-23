## Android Document Scanner

Android Document Scanner using [Dynamsoft Document Normalizer](https://www.dynamsoft.com/document-normalizer/docs/introduction/).

You can [apply for a trial license](https://www.dynamsoft.com/customer/license/trialLicense?product=ddn) and update [this line](https://github.com/xulihang/Android-Document-Scanner/blob/78ba04916bb395ae82ddd586bd6ab0c74def39ab/app/src/main/java/com/dynamsoft/documentscanner/MainActivity.java#L75) to use Dynamsoft Document Normalizer.


### Document Scanning Process

1. Start the camera using CameraX and analyse the frames to detect the boundary of documents. When the IOU of three consecutive detected polygons are over 90%, take a photo.
2. After the photo is taken, the users are directed to a cropping activity. They can drag the corner points to adjust the detected polygons.
3. If the user confirms that the polygon is correct, the app then runs perspective correction and cropping to get a normalized document image. Users can rotate the image and set the color mode (binary, grayscale and color) of the image.

A demo video of the whole process.

<video src="https://user-images.githubusercontent.com/5462205/186093735-b1622e5e-9c50-4fe3-974a-de29a881768f.mp4" data-canonical-src="https://user-images.githubusercontent.com/5462205/186093735-b1622e5e-9c50-4fe3-974a-de29a881768f.mp4" controls="controls" muted="muted" class="d-block rounded-bottom-2 border-top width-fit" style="max-width:100%;max-height:640px;">
</video>

### Features

1. live detection of documents
2. auto scan
3. edit detected polygons of documents
4. support three color mode: binary, grayscale and color

