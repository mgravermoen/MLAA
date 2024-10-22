import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AlgorithmMLAA {

    // stores a list of 8 2D arrays containing all changed pixels for each pattern
    private ArrayList<boolean[][]> totalChangedPixels = new ArrayList<boolean[][]>();
    // stores a list of 8 2D arrays containing all lengths for each pattern found
    private ArrayList<double[][]> totalPatternLengths = new ArrayList<double[][]>();

    // Controls the strength of the algorithm. Set between 1 and 254. The higher this number is,
    // the less patterns the algorithm will find.
    private int strength = 50; //default value is 50

    /*
     * Searches for all 8 "L" patterns and blends pictures accordingly
     * 
     * @return new image with MLAA
     */
    protected BufferedImage algorithm(BufferedImage image) throws Exception {

        locatingPattern1(image);
        locatingPattern2(image);
        locatingPattern3(image);
        locatingPattern4(image);
        locatingPattern5(image);
        locatingPattern6(image);
        locatingPattern7(image);
        locatingPattern8(image);

        return writingPatterns(image);
    }

    /*
    *   Pattern 1
    *
    *    Secondary Edge
    *       -------
    *              |
    *              |
    *              |   Primary
    *              |   Edge
    *              |
    *              |
    */
    private void locatingPattern1(BufferedImage image) {

        // stores all pixels that will be changes for pattern 1
        boolean[][] changedPixels = new boolean[image.getWidth()][image.getHeight()];
        // stores the length of each pattern found
        double[][] patternLengths = new double[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // make sure we stay in bounds: first compare luminance of secondary edge, then
                // compare luminance of primary edge
                if (y - 1 >= 0 && x + 1 < image.getWidth()) {
                    int currentPixel = image.getRGB(x, y);
                    Color currentColor = new Color(currentPixel, true);
                    double currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                            + (0.0722 * currentColor.getBlue());

                    int secondaryComparePixel = image.getRGB(x, y - 1);
                    Color secondaryCompareColor = new Color(secondaryComparePixel, true);
                    double secondaryCompareLuminance = (0.2126 * secondaryCompareColor.getRed())
                            + (0.7152 * secondaryCompareColor.getGreen()) + (0.0722 * secondaryCompareColor.getBlue());

                    int primaryComparePixel = image.getRGB(x + 1, y);
                    Color primaryCompareColor = new Color(primaryComparePixel, true);
                    double primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                            + (0.7152 * primaryCompareColor.getGreen()) + (0.0722 * primaryCompareColor.getBlue());

            
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // this is comparing dark pixels against light pixels.
                    if (currentLuminance - secondaryCompareLuminance < -strength
                            && currentLuminance - primaryCompareLuminance < -strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        // we're going to be changing the value of y; to not throw off the main loop, we make a copy
                        // to use instead.
                        int tempY = y;
                        // a pattern can technically be 1 pixel; each length found starts at 1
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        // make sure we stay in bounds: we're now comparing pixels on both sides of the
                        // primary edge
                        while (findingPattern == true && tempY + 1 < image.getHeight() && x + 1 < image.getWidth()) {
                            currentPixel = image.getRGB(x, tempY + 1);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(x + 1, tempY + 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            // 1 aditional unit of length found
                            if (currentLuminance - primaryCompareLuminance < -strength) {
                                changedPixels[x][tempY + 1] = true;
                                tempY += 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // very similiar to what is happening above, except now we're comparing 
                    //light pixels against dark pixels.
                    } else if (currentLuminance - secondaryCompareLuminance > strength
                            && currentLuminance - primaryCompareLuminance > strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        int tempY = y;
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        while (findingPattern == true && tempY + 1 < image.getHeight() && x + 1 < image.getWidth()) {
                            currentPixel = image.getRGB(x, tempY + 1);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(x + 1, tempY + 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            if (currentLuminance - primaryCompareLuminance > strength) {
                                changedPixels[x][tempY + 1] = true;
                                tempY += 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    }

                }
            }
        }
        totalChangedPixels.add(changedPixels);
        totalPatternLengths.add(patternLengths);
    }

    /*
    *   Pattern 2
    *
    *         Secondary Edge
    *            -------
    *           |
    *  Primary  |
    *    Edge   |
    *           |
    *           |
    *           |
    */
    private void locatingPattern2(BufferedImage image) {

        // stores all pixels that will be changes for pattern 2
        boolean[][] changedPixels = new boolean[image.getWidth()][image.getHeight()];
        // stores the length of each pattern found
        double[][] patternLengths = new double[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // make sure we stay in bounds: first compare luminance of secondary edge, then
                // compare luminance of primary edge
                if (y - 1 >= 0 && x - 1 >= 0) {
                    int currentPixel = image.getRGB(x, y);
                    Color currentColor = new Color(currentPixel, true);
                    double currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                            + (0.0722 * currentColor.getBlue());

                    int secondaryComparePixel = image.getRGB(x, y - 1);
                    Color secondaryCompareColor = new Color(secondaryComparePixel, true);
                    double secondaryCompareLuminance = (0.2126 * secondaryCompareColor.getRed())
                            + (0.7152 * secondaryCompareColor.getGreen()) + (0.0722 * secondaryCompareColor.getBlue());

                    int primaryComparePixel = image.getRGB(x - 1, y);
                    Color primaryCompareColor = new Color(primaryComparePixel, true);
                    double primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                            + (0.7152 * primaryCompareColor.getGreen()) + (0.0722 * primaryCompareColor.getBlue());

                    if (currentLuminance - secondaryCompareLuminance < -strength
                            && currentLuminance - primaryCompareLuminance < -strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        // we're going to be changing the value of y; to not throw off the main loop, we make a copy
                        // to use instead.
                        int tempY = y;
                        // a pattern can technically be 1 pixel; each length found starts at 1
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        // make sure we stay in bounds: we're now comparing pixels on both sides of the
                        // primary edge
                        while (findingPattern == true && tempY + 1 < image.getHeight() && x - 1 >= 0) {
                            currentPixel = image.getRGB(x, tempY + 1);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(x - 1, tempY + 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            // 1 aditional unit of length found
                            if (currentLuminance - primaryCompareLuminance < -strength) {
                                changedPixels[x][tempY + 1] = true;
                                tempY += 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // very similiar to what is happening above, except now we're comparing 
                    //light pixels against dark pixels.
                    } else if (currentLuminance - secondaryCompareLuminance > strength
                            && currentLuminance - primaryCompareLuminance > strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        int tempY = y;
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        while (findingPattern == true && tempY + 1 < image.getHeight() && x - 1 >= 0) {
                            currentPixel = image.getRGB(x, tempY + 1);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(x - 1, tempY + 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            if (currentLuminance - primaryCompareLuminance > strength) {
                                changedPixels[x][tempY + 1] = true;
                                tempY += 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    }
                }
            }
        }
        totalChangedPixels.add(changedPixels);
        totalPatternLengths.add(patternLengths);
    }

    /*
    *   Pattern 3
    *
    *         Primary Edge
    *       ------------------
    *                         |
    *                         |  Secondary
    *                         |    Edge
    */
    private void locatingPattern3(BufferedImage image) {

        // stores all pixels that will be changes for pattern 3
        boolean[][] changedPixels = new boolean[image.getWidth()][image.getHeight()];
        // stores the length of each pattern found
        double[][] patternLengths = new double[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // make sure we stay in bounds: first compare luminance of secondary edge, then
                // compare luminance of primary edge
                if (y - 1 >= 0 && x + 1 < image.getWidth()) {
                    int currentPixel = image.getRGB(x, y);
                    Color currentColor = new Color(currentPixel, true);
                    double currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                            + (0.0722 * currentColor.getBlue());

                    int secondaryComparePixel = image.getRGB(x + 1, y);
                    Color secondaryCompareColor = new Color(secondaryComparePixel, true);
                    double secondaryCompareLuminance = (0.2126 * secondaryCompareColor.getRed())
                            + (0.7152 * secondaryCompareColor.getGreen()) + (0.0722 * secondaryCompareColor.getBlue());

                    int primaryComparePixel = image.getRGB(x, y - 1);
                    Color primaryCompareColor = new Color(primaryComparePixel, true);
                    double primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                            + (0.7152 * primaryCompareColor.getGreen()) + (0.0722 * primaryCompareColor.getBlue());

                    if (currentLuminance - secondaryCompareLuminance < -strength
                            && currentLuminance - primaryCompareLuminance < -strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        // we're going to be changing the value of x; to not throw off the main loop, we make a copy
                        // to use instead.
                        int tempX = x;
                        // a pattern can technically be 1 pixel; each length found starts at 1
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        // make sure we stay in bounds: we're now comparing pixels on both sides of the
                        // primary edge
                        while (findingPattern == true && tempX - 1 >= 0 && y - 1 >= 0) {
                            currentPixel = image.getRGB(tempX - 1, y);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(tempX - 1, y - 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            // 1 aditional unit of length found
                            if (currentLuminance - primaryCompareLuminance < -strength) {
                                changedPixels[tempX - 1][y] = true;
                                tempX -= 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // very similiar to what is happening above, except now we're comparing 
                    //light pixels against dark pixels.
                    } else if (currentLuminance - secondaryCompareLuminance > strength
                            && currentLuminance - primaryCompareLuminance > strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        int tempX = x;
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        while (findingPattern == true && tempX - 1 >= 0 && y - 1 >= 0) {
                            currentPixel = image.getRGB(tempX - 1, y);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(tempX - 1, y - 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            if (currentLuminance - primaryCompareLuminance > strength) {
                                changedPixels[tempX - 1][y] = true;
                                tempX -= 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    }

                }
            }
        }
        totalChangedPixels.add(changedPixels);
        totalPatternLengths.add(patternLengths);
    }

    /*
    *   Pattern 4
    *
    *                Primary Edge
    *              ------------------
    *  Secondary  |
    *    Edge     |
    *             |
    */
    private void locatingPattern4(BufferedImage image) {

        // stores all pixels that will be changes for pattern 4
        boolean[][] changedPixels = new boolean[image.getWidth()][image.getHeight()];
        // stores the length of each pattern found
        double[][] patternLengths = new double[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // make sure we stay in bounds: first compare luminance of secondary edge, then
                // compare luminance of primary edge
                if (x - 1 >= 0 && y - 1 >= 0) {
                    int currentPixel = image.getRGB(x, y);
                    Color currentColor = new Color(currentPixel, true);
                    double currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                            + (0.0722 * currentColor.getBlue());

                    int secondaryComparePixel = image.getRGB(x - 1, y);
                    Color secondaryCompareColor = new Color(secondaryComparePixel, true);
                    double secondaryCompareLuminance = (0.2126 * secondaryCompareColor.getRed())
                            + (0.7152 * secondaryCompareColor.getGreen()) + (0.0722 * secondaryCompareColor.getBlue());

                    int primaryComparePixel = image.getRGB(x, y - 1);
                    Color primaryCompareColor = new Color(primaryComparePixel, true);
                    double primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                            + (0.7152 * primaryCompareColor.getGreen()) + (0.0722 * primaryCompareColor.getBlue());

                    if (currentLuminance - secondaryCompareLuminance < -strength
                            && currentLuminance - primaryCompareLuminance < -strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        // we're going to be changing the value of x; to not throw off the main loop, we make a copy
                        // to use instead.
                        int tempX = x;
                        // a pattern can technically be 1 pixel; each length found starts at 1
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        // make sure we stay in bounds: we're now comparing pixels on both sides of the
                        // primary edge
                        while (findingPattern == true && tempX + 1 < image.getWidth() && y - 1 >= 0) {
                            currentPixel = image.getRGB(tempX + 1, y);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(tempX + 1, y - 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            // 1 aditional unit of length found
                            if (currentLuminance - primaryCompareLuminance < -strength) {
                                changedPixels[tempX + 1][y] = true;
                                tempX += 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // very similiar to what is happening above, except now we're comparing 
                    //light pixels against dark pixels.
                    } else if (currentLuminance - secondaryCompareLuminance > strength
                            && currentLuminance - primaryCompareLuminance > strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        int tempX = x;
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        while (findingPattern == true && tempX + 1 < image.getWidth() && y - 1 >= 0) {
                            currentPixel = image.getRGB(tempX + 1, y);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(tempX + 1, y - 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            if (currentLuminance - primaryCompareLuminance > strength) {
                                changedPixels[tempX + 1][y] = true;
                                tempX += 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    }

                }
            }
        }
        totalChangedPixels.add(changedPixels);
        totalPatternLengths.add(patternLengths);
    }

    /*
    *   Pattern 5
    *
    *             |
    *             |
    *             |   Primary
    *             |    Edge
    *             |
    *             |
    *      -------
    *    Secondary Edge
    *
    */
    private void locatingPattern5(BufferedImage image) {

        // stores all pixels that will be changes for pattern 5
        boolean[][] changedPixels = new boolean[image.getWidth()][image.getHeight()];
        // stores the length of each pattern found
        double[][] patternLengths = new double[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // make sure we stay in bounds: first compare luminance of secondary edge, then
                // compare luminance of primary edge
                if (y + 1 < image.getHeight() && x + 1 < image.getWidth()) {
                    int currentPixel = image.getRGB(x, y);
                    Color currentColor = new Color(currentPixel, true);
                    double currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                            + (0.0722 * currentColor.getBlue());

                    int secondaryComparePixel = image.getRGB(x, y + 1);
                    Color secondaryCompareColor = new Color(secondaryComparePixel, true);
                    double secondaryCompareLuminance = (0.2126 * secondaryCompareColor.getRed())
                            + (0.7152 * secondaryCompareColor.getGreen()) + (0.0722 * secondaryCompareColor.getBlue());

                    int primaryComparePixel = image.getRGB(x + 1, y);
                    Color primaryCompareColor = new Color(primaryComparePixel, true);
                    double primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                            + (0.7152 * primaryCompareColor.getGreen()) + (0.0722 * primaryCompareColor.getBlue());

                    if (currentLuminance - secondaryCompareLuminance < -strength
                            && currentLuminance - primaryCompareLuminance < -strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        // we're going to be changing the value of y; to not throw off the main loop, we make a copy
                        // to use instead.
                        int tempY = y;
                        // a pattern can technically be 1 pixel; each length found starts at 1
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        // make sure we stay in bounds: we're now comparing pixels on both sides of the
                        // primary edge
                        while (findingPattern == true && tempY - 1 >= 0 && x + 1 < image.getWidth()) {
                            currentPixel = image.getRGB(x, tempY - 1);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(x + 1, tempY - 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            // 1 aditional unit of length found 
                            if (currentLuminance - primaryCompareLuminance < -strength) {
                                changedPixels[x][tempY - 1] = true;
                                tempY -= 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // very similiar to what is happening above, except now we're comparing 
                    //light pixels against dark pixels.
                    } else if (currentLuminance - secondaryCompareLuminance > strength
                            && currentLuminance - primaryCompareLuminance > strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        int tempY = y;
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        while (findingPattern == true && tempY - 1 >= 0 && x + 1 < image.getWidth()) {
                            currentPixel = image.getRGB(x, tempY - 1);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(x + 1, tempY - 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            if (currentLuminance - primaryCompareLuminance > strength) {
                                changedPixels[x][tempY - 1] = true;
                                tempY -= 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    }
                }
            }
        }
        totalChangedPixels.add(changedPixels);
        totalPatternLengths.add(patternLengths);
    }

    /*
    *   Pattern 6
    *
    *           |
    *           |
    *  Primary  |
    *    Edge   |
    *           |
    *           |
    *            -------
    *         Secondary Edge
    *
    */
    private void locatingPattern6(BufferedImage image) {

        // stores all pixels that will be changes for pattern 6
        boolean[][] changedPixels = new boolean[image.getWidth()][image.getHeight()];
        // stores the length of each pattern found
        double[][] patternLengths = new double[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // make sure we stay in bounds: first compare luminance of secondary edge, then
                // compare luminance of primary edge
                if (y + 1 < image.getHeight() && x - 1 >= 0) {
                    int currentPixel = image.getRGB(x, y);
                    Color currentColor = new Color(currentPixel, true);
                    double currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                            + (0.0722 * currentColor.getBlue());

                    int secondaryComparePixel = image.getRGB(x, y + 1);
                    Color secondaryCompareColor = new Color(secondaryComparePixel, true);
                    double secondaryCompareLuminance = (0.2126 * secondaryCompareColor.getRed())
                            + (0.7152 * secondaryCompareColor.getGreen()) + (0.0722 * secondaryCompareColor.getBlue());

                    int primaryComparePixel = image.getRGB(x - 1, y);
                    Color primaryCompareColor = new Color(primaryComparePixel, true);
                    double primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                            + (0.7152 * primaryCompareColor.getGreen()) + (0.0722 * primaryCompareColor.getBlue());

                    if (currentLuminance - secondaryCompareLuminance < -strength
                            && currentLuminance - primaryCompareLuminance < -strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        // we're going to be changing the value of y; to not throw off the main loop, we make a copy
                        // to use instead.
                        int tempY = y;
                        // a pattern can technically be 1 pixel; each length found starts at 1
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        // make sure we stay in bounds: we're now comparing pixels on both sides of the
                        // primary edge
                        while (findingPattern == true && tempY - 1 >= 0 && x - 1 >= 0) {
                            currentPixel = image.getRGB(x, tempY - 1);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(x - 1, tempY - 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            // 1 aditional unit of length found   
                            if (currentLuminance - primaryCompareLuminance < -strength) {
                                changedPixels[x][tempY - 1] = true;
                                tempY -= 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // very similiar to what is happening above, except now we're comparing 
                    //light pixels against dark pixels.
                    } else if (currentLuminance - secondaryCompareLuminance > strength
                            && currentLuminance - primaryCompareLuminance > strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        int tempY = y;
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        while (findingPattern == true && tempY - 1 >= 0 && x - 1 >= 0) {
                            currentPixel = image.getRGB(x, tempY - 1);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(x - 1, tempY - 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            if (currentLuminance - primaryCompareLuminance > strength) {
                                changedPixels[x][tempY - 1] = true;
                                tempY -= 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    }
                }
            }
        }
        totalChangedPixels.add(changedPixels);
        totalPatternLengths.add(patternLengths);
    }

    /*
    *   Pattern 7
    *
    *                         |
    *                         |  Secondary
    *                         |    Edge
    *       ------------------
    *         Primary Edge
    */
    private void locatingPattern7(BufferedImage image) {

        // stores all pixels that will be changes for pattern 7
        boolean[][] changedPixels = new boolean[image.getWidth()][image.getHeight()];
        // stores the length of each pattern found
        double[][] patternLengths = new double[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // make sure we stay in bounds: first compare luminance of secondary edge, then
                // compare luminance of primary edge
                if (y + 1 < image.getHeight() && x + 1 < image.getWidth()) {
                    int currentPixel = image.getRGB(x, y);
                    Color currentColor = new Color(currentPixel, true);
                    double currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                            + (0.0722 * currentColor.getBlue());

                    int secondaryComparePixel = image.getRGB(x + 1, y);
                    Color secondaryCompareColor = new Color(secondaryComparePixel, true);
                    double secondaryCompareLuminance = (0.2126 * secondaryCompareColor.getRed())
                            + (0.7152 * secondaryCompareColor.getGreen()) + (0.0722 * secondaryCompareColor.getBlue());

                    int primaryComparePixel = image.getRGB(x, y + 1);
                    Color primaryCompareColor = new Color(primaryComparePixel, true);
                    double primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                            + (0.7152 * primaryCompareColor.getGreen()) + (0.0722 * primaryCompareColor.getBlue());

                    if (currentLuminance - secondaryCompareLuminance < -strength
                            && currentLuminance - primaryCompareLuminance < -strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        // we're going to be changing the value of x; to not throw off the main loop, we make a copy
                        // to use instead.
                        int tempX = x;
                        // a pattern can technically be 1 pixel; each length found starts at 1
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        // make sure we stay in bounds: we're now comparing pixels on both sides of the
                        // primary edge
                        while (findingPattern == true && tempX - 1 >= 0 && y + 1 < image.getHeight()) {
                            currentPixel = image.getRGB(tempX - 1, y);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(tempX - 1, y + 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            // 1 aditional unit of length found
                            if (currentLuminance - primaryCompareLuminance < -strength) {
                                changedPixels[tempX - 1][y] = true;
                                tempX -= 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // very similiar to what is happening above, except now we're comparing 
                    //light pixels against dark pixels.
                    } else if (currentLuminance - secondaryCompareLuminance > strength
                            && currentLuminance - primaryCompareLuminance > strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        int tempX = x;
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        while (findingPattern == true && tempX - 1 >= 0 && y + 1 < image.getHeight()) {
                            currentPixel = image.getRGB(tempX - 1, y);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(tempX - 1, y + 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            if (currentLuminance - primaryCompareLuminance > strength) {
                                changedPixels[tempX - 1][y] = true;
                                tempX -= 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    }

                }
            }
        }
        totalChangedPixels.add(changedPixels);
        totalPatternLengths.add(patternLengths);
    }

    /*
    *   Pattern 8
    *
    *             |
    *  Secondary  |
    *    Edge     |
    *              ------------------
    *                Primary Edge
    */
    private void locatingPattern8(BufferedImage image) {

        // stores all pixels that will be changes for pattern 8
        boolean[][] changedPixels = new boolean[image.getWidth()][image.getHeight()];
        // stores the length of each pattern found
        double[][] patternLengths = new double[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                // make sure we stay in bounds: first compare luminance of secondary edge, then
                // compare luminance of primary edge
                if (x - 1 >= 0 && y + 1 < image.getHeight()) {
                    int currentPixel = image.getRGB(x, y);
                    Color currentColor = new Color(currentPixel, true);
                    double currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                            + (0.0722 * currentColor.getBlue());

                    int secondaryComparePixel = image.getRGB(x - 1, y);
                    Color secondaryCompareColor = new Color(secondaryComparePixel, true);
                    double secondaryCompareLuminance = (0.2126 * secondaryCompareColor.getRed())
                            + (0.7152 * secondaryCompareColor.getGreen()) + (0.0722 * secondaryCompareColor.getBlue());

                    int primaryComparePixel = image.getRGB(x, y + 1);
                    Color primaryCompareColor = new Color(primaryComparePixel, true);
                    double primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                            + (0.7152 * primaryCompareColor.getGreen()) + (0.0722 * primaryCompareColor.getBlue());

                    if (currentLuminance - secondaryCompareLuminance < -strength
                            && currentLuminance - primaryCompareLuminance < -strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        // we're going to be changing the value of x; to not throw off the main loop, we make a copy
                        // to use instead.
                        int tempX = x;
                        // a pattern can technically be 1 pixel; each length found starts at 1
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        // make sure we stay in bounds: we're now comparing pixels on both sides of the
                        // primary edge
                        while (findingPattern == true && tempX + 1 < image.getWidth() && y + 1 < image.getHeight()) {
                            currentPixel = image.getRGB(tempX + 1, y);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(tempX + 1, y + 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            // 1 aditional unit of length found
                            if (currentLuminance - primaryCompareLuminance < -strength) {
                                changedPixels[tempX + 1][y] = true;
                                tempX += 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    // pattern found: continue to see how many pixels this pattern stretches;
                    // very similiar to what is happening above, except now we're comparing 
                    //light pixels against dark pixels.
                    } else if (currentLuminance - secondaryCompareLuminance > strength
                            && currentLuminance - primaryCompareLuminance > strength) {
                        changedPixels[x][y] = true;

                        boolean findingPattern = true;
                        int tempX = x;
                        int patternLength = 1;
                        patternLengths[x][y] = 1;

                        while (findingPattern == true && tempX + 1 < image.getWidth() && y + 1 < image.getHeight()) {
                            currentPixel = image.getRGB(tempX + 1, y);
                            currentColor = new Color(currentPixel, true);
                            currentLuminance = (0.2126 * currentColor.getRed()) + (0.7152 * currentColor.getGreen())
                                    + (0.0722 * currentColor.getBlue());

                            primaryComparePixel = image.getRGB(tempX + 1, y + 1);
                            primaryCompareColor = new Color(primaryComparePixel, true);
                            primaryCompareLuminance = (0.2126 * primaryCompareColor.getRed())
                                    + (0.7152 * primaryCompareColor.getGreen())
                                    + (0.0722 * primaryCompareColor.getBlue());

                            if (currentLuminance - primaryCompareLuminance > strength) {
                                changedPixels[tempX + 1][y] = true;
                                tempX += 1;
                                patternLength += 1;
                            } else {
                                findingPattern = false;
                            }
                        }

                        patternLengths[x][y] = patternLength;
                    }

                }
            }
        }
        totalChangedPixels.add(changedPixels);
        totalPatternLengths.add(patternLengths);
    }

    /*
     * Blends pixels in regard to all eight "L" patterns found
     * 
     * @return new image with MLAA
     */
    private BufferedImage writingPatterns(BufferedImage image) throws Exception {

        boolean[][] changedPixels;
        double[][] patternLengths;

        // assign what pattern we'll be using for this iteration
        for (int i = 0; i < 8; i++) {
            changedPixels = totalChangedPixels.get(i);
            patternLengths = totalPatternLengths.get(i);

            // now we need to change each pixel that was previously marked
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (changedPixels[x][y] == true && patternLengths[x][y] != 0) {
                        // to calculate weight, we're drawing trapezoids by connecting a line
                        // through the midpoint of the secondary edge and the midpoint of the
                        // primary edge
                        double trapezoidA = 1.0 / 2.0;
                        int lengthCounter = 0;
                        // we're going to be changing the values of x and y; to prevent the main loop from
                        // being thrown off, we're make some copies
                        int tempX = x;
                        int tempY = y;
                        while (patternLengths[x][y] / 2.0 - lengthCounter > 0) {

                            double a;

                            // this algorithm doesn't work for patterns with a length of 1 or 2;
                            // since there are only two of these cases, just hardcode the weights in
                            if (patternLengths[x][y] == 1.0) {
                                a = 1.0 / 8.0;
                            } else if (patternLengths[x][y] == 2.0) {
                                a = 1.0 / 4.0;
                            } else {
                                double trapezoidB = trapezoidA - (1.0 / patternLengths[x][y]);

                                // if we reach this point, our trapezoid has gotten so small that
                                // it is no longer a trapezoid, but a triangle. compute the area of
                                // the triangle instead
                                if (trapezoidB == 0.0) {
                                    if (patternLengths[x][y] % 2 == 0) {
                                        a = trapezoidA * 1.0 / 2.0;
                                    } else {
                                        a = trapezoidA * (1.0 / 2.0) / 2.0;
                                    }
                                } else {
                                    a = trapezoidA + trapezoidB / 2.0 * 1.0;
                                    trapezoidA = trapezoidB;
                                }
                            }

                            // depending on what pattern we're looking at, we'll need to
                            // adjust which pixel we're looking at
                            int currentPixel;
                            if (i == 0 || i == 1 || i == 4 || i == 5) {
                                currentPixel = image.getRGB(x, tempY);
                            } else {
                                currentPixel = image.getRGB(tempX, y);
                            }
                            Color currentColor = new Color(currentPixel, true);
                            int currentRed = currentColor.getRed();
                            int currentGreen = currentColor.getGreen();
                            int currentBlue = currentColor.getBlue();

                            // depending on what pattern we're looking at, we'll need to
                            // adjust what pixel we're comparing to
                            int comparePixel;
                            if (i == 0 || i == 4) {
                                comparePixel = image.getRGB(x + 1, tempY);
                            } else if (i == 1 || i == 5) {
                                comparePixel = image.getRGB(x - 1, tempY);
                            } else if (i == 2 || i == 3) {
                                comparePixel = image.getRGB(tempX, y - 1);
                            } else {
                                comparePixel = image.getRGB(tempX, y + 1);
                            }
                            Color compareColor = new Color(comparePixel, true);
                            int compareRed = compareColor.getRed();
                            int compareGreen = compareColor.getGreen();
                            int compareBlue = compareColor.getBlue();

                            currentRed = (int) Math.round(((1.0 - a) * currentRed) + (a * compareRed));
                            currentGreen = (int) Math.round(((1.0 - a) * currentGreen) + (a * compareGreen));
                            currentBlue = (int) Math.round(((1.0 - a) * currentBlue) + (a * compareBlue));

                            currentColor = new Color(currentRed, currentGreen, currentBlue);
                            if (i == 0 || i == 1 || i == 4 || i == 5) {
                                image.setRGB(x, tempY, currentColor.getRGB());
                            } else {
                                image.setRGB(tempX, y, currentColor.getRGB());
                            }

                            // we now need to either decrease or increase x or y depending
                            // on what pattern we're looking at
                            if (i == 2 || i == 6) {
                                tempX -= 1;
                            } else {
                                tempX += 1;
                            }

                            if (i == 4 || i == 5) {
                                tempY -= 1;
                            } else {
                                tempY += 1;
                            }

                            lengthCounter += 1;
                        }
                    }
                }
            }
        }

        return image;
    }
}