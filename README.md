# MathSnap Android App

MathSnap is an Android app that allows users to take a photo of a math problem and get the solution. The app uses the OCR.Space API to convert the image to text and solve the math problem. The app has two build flavors for themes (red and green) and two build flavors for image pickers (camera and gallery), resulting in four possible combinations: red camera, red gallery, green camera, and green gallery.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [API Key](#api-key)
- [Contributing](#contributing)
- [License](#license)

## Installation

To install the app, follow these steps:

1. Clone the repository or download the source code.
2. Obtain a free API key from [OCR.Space](https://ocr.space/ocrapi/freekey).
3. In the project, locate the app-level `build.gradle` file and add the following line:
```
buildConfigField "String", "API_KEY", "\"example\""
```
4. Choose build variant
5. Sync the project with Gradle.
6. Click Run/Build the app.


> This app use OCR engine 2 from ocr.space, you can see server status [here](https://status.ocr.space/).
> Also check ocr engine response time. ocr engine 2 is more precise but slower

## Usage

To use the app, open it on your Android device and follow these steps:

1. Tap the Floating action button.
2. It will show dropdown menu to choose, some button will be disabled based on build variant.
3. Take a photo of a math problem or select an existing image from your gallery.
4. Wait for the app to process the image and display the solution.

<img src="Screenshots/image1.png"/>

## API Key

To use the OCR.Space API, you need to obtain an API key by creating a free account. Once you have an API key, add it to the `build.gradle` file as described in the Installation section.

Keep in mind that the free version of the OCR.Space API has some limitations, such as a maximum of 25,000 conversions per month. If you need more conversions or additional features, you can upgrade to a paid plan.

## License
MathSnap is licensed under the MIT License.