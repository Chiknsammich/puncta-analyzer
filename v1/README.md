puncta-analyzer v1
==================

Puncta Analyzer for ImageJ1

Instructions:
1. Puncta Analyzer v1 runs in ImageJ 1.29. To install ImageJ with Puncta Analyzer simply download the [puncta-analyzer-v1.zip](https://github.com/physion/puncta-analyzer/v1/puncta-analyzer-v1.zip) and uncompress. Double-click on ij.jar to launch.

2. Open the ins1.tif image located in samples directory. Use one of the selection tools in the ImageJ menu to determine the region of interest (ROI). We regularly use the circular selection tool to select a region approximately one-cell diameter radially around the soma of interest.

3. With your region of interest (ROI) selected, go to the plugins menu and select "Puncta Analyzer".

4. In the "Analysis Options" window that appears, select "Red Channel", "Green Channel", the first "Subtract Background" and "Set results file...." Click "OK". You will be asked to define a location to save your results in. These results can be exported to Excel for further analysis.

5. In the window that appears next, make sure a rolling ball radius of 50 is selected and uncheck the "White Background" option (this modification is not required but is often preferred by users of the application for ease of visualization). Click "OK".

6. A new window will appear alongside a mask corresponding with your red channel image. Adjust the threshold until you feel that the red mask corresponds as well as possible to as many discrete individual puncta without introducing too much noise. This is one of the most subjective steps of this protocol, so take care to develop a consistent approach. Click "Done". Set the minimum puncta size to 4 pixels and modify nothing else. Click "OK".

7. Repeat the previous step, this time for the green channel.

8. Once you complete the previous step, the plugin will provide quantification corresponding to puncta in each channel separately and to colocalized puncta between the two channels.

Note: A screencast of these instructions is available [here](https://github.com/physion/puncta-analyzer/v1/Puncta_Analyzer_Screencast.mp4).
-----
