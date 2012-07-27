/*     */ import ij.IJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.Prefs;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.MessageDialog;
/*     */ import ij.gui.Roi;
/*     */ import ij.measure.Measurements;
/*     */ import ij.measure.ResultsTable;
/*     */ import ij.plugin.filter.ParticleAnalyzer;
/*     */ import ij.plugin.filter.PlugInFilter;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ColorProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Rectangle;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class Puncta_Analyzer
/*     */   implements PlugInFilter
/*     */ {
/*     */   public static final int RGB_RED_CHANNEL = 1;
/*     */   public static final int RGB_GREEN_CHANNEL = 2;
/*     */   public static final int RGB_BLUE_CHANNEL = 4;
/*     */   public static final int PUNCTA_RED_CHANNEL = 0;
/*     */   public static final int PUNCTA_GREEN_CHANNEL = 1;
/*     */   public static final int PUNCTA_BLUE_CHANNEL = 2;
/*     */   public static final int PUNCTA_COLOC = 3;
/*     */   static final String COLOC_RADIUS = "ap.colocRadius";
/*     */   static final String COLOR_CHANNELS = "ap.colorChannels";
/*     */   static final String PARTICLE_ANALYZER_OPTIONS = "ap.partAnalyzerOptions";
/*     */   static final String PARTICLE_ANALYZER_MEASUREMENTS = "ap.partAnalyzerMeasurements";
/*     */   static final String PARTICLE_ANALYZER_MIN_SIZE = "ap.partAnalyzerMinSize";
/*     */   static final String PARTICLE_ANALYZER_MAX_SIZE = "ap.partAnalyzerMaxSize";
/*     */   static final String SUB_CHANNEL_BACKGROUND = "ap.subChannelBackground";
/*     */   static final String RESULTS_FILE_HEADER_WRITTEN = "ap.resultsFileHeaderWritten";
/*     */   static final String RESULTS_FILE_NAME = "ap.resultsFileName";
/*     */   static final String RESULTS_DELIMETER = "ap.resultsDelimeter";
/*     */   static final String SAVE_RESULTS = "ap.saveResults";
/*     */   static final String CONDITION = "ap.condition";
/*  87 */   private static int staticColorChannels = Prefs.getInt("ap.colorChannels", 3);
/*  88 */   private static int staticPartAnalyzerOptions = Prefs.getInt("ap.partAnalyzerOptions", 4);
/*  89 */   private static int staticPartAnalyzerMeasurements = Prefs.getInt("ap.partAnalyzerMeasurements", 179);
/*  90 */   private static int staticPartAnalyzerMinSize = Prefs.getInt("ap.partAnalyzerMinSize", 1);
/*  91 */   private static int staticPartAnalyzerMaxSize = Prefs.getInt("ap.partAnalyzerMaxSize", 200);
/*  92 */   private static int staticSubtractChannelBackground = Prefs.getInt("ap.subChannelBackground", 1);
/*     */ 
/*  94 */   private static boolean staticReultsFileHeaderWritten = Prefs.getBoolean("ap.resultsFileHeaderWritten", false);
/*  95 */   private static boolean staticSaveResults = Prefs.getBoolean("ap.saveResults", false);
/*  96 */   private static String resultsFileName = Prefs.getString("ap.resultsFileName");
/*  97 */   private static String resultsDelimeter = "\t";
/*  98 */   private static String staticCondition = Prefs.getString("ap.condition");
/*  99 */   private static File resultsFile = null;
/*     */   protected String arg;
/*     */   protected ImagePlus imp;
/*     */   protected ParticleAnalyzer analyzer;
/*     */   protected List[] puncta;
/*     */ 
/*     */   public static void savePreferences(Properties prefs)
/*     */   {
/* 112 */     prefs.put("ap.colorChannels", Integer.toString(staticColorChannels));
/* 113 */     prefs.put("ap.partAnalyzerOptions", Integer.toString(staticPartAnalyzerOptions));
/* 114 */     prefs.put("ap.partAnalyzerMeasurements", Integer.toString(staticPartAnalyzerMeasurements));
/* 115 */     prefs.put("ap.partAnalyzerMinSize", Integer.toString(staticPartAnalyzerMinSize));
/* 116 */     prefs.put("ap.partAnalyzerMaxSize", Integer.toString(staticPartAnalyzerMaxSize));
/* 117 */     prefs.put("ap.subChannelBackground", Integer.toString(staticSubtractChannelBackground));
/* 118 */     prefs.put("ap.resultsFileHeaderWritten", new Boolean(staticReultsFileHeaderWritten).toString());
/* 119 */     prefs.put("ap.saveResults", new Boolean(staticSaveResults).toString());
/* 120 */     prefs.put("ap.resultsFileName", resultsFileName);
/* 121 */     prefs.put("ap.condition", staticCondition);
/*     */   }
/*     */ 
/*     */   public int setup(String arg, ImagePlus imp)
/*     */   {
/* 129 */     this.arg = arg;
/* 130 */     this.imp = imp;
/* 131 */     IJ.register(Puncta_Analyzer.class);
/*     */ 
/* 133 */     return 1424;
/*     */   }
/*     */ 
/*     */   protected boolean runPrefsDialog()
/*     */   {
/* 145 */     GenericDialog gd = new GenericDialog("Analysis Options", IJ.getInstance());
/* 146 */     gd.addStringField("Condition: ", staticCondition != null ? staticCondition : new String(""));
/* 147 */     gd.addCheckboxGroup(3, 2, new String[] { "Red Channel", "Green Channel", "Blue Channel", "Subtract Background", "Subtract Background", "Subtract Background" }, new boolean[] { (staticColorChannels & 0x1) != 0 ? 1 : 0, (staticColorChannels & 0x2) != 0 ? 1 : 0, (staticColorChannels & 0x4) != 0 ? 1 : 0, (staticSubtractChannelBackground & 0x1) != 0 ? 1 : 0, (staticSubtractChannelBackground & 0x2) != 0 ? 1 : 0, (staticSubtractChannelBackground & 0x4) != 0 ? 1 : 0 });
/* 148 */     gd.addCheckbox("Set results file...", !staticReultsFileHeaderWritten);
/* 149 */     gd.addCheckbox("Save results", staticReultsFileHeaderWritten);
/* 150 */     gd.addStringField("Current results file: ", resultsFile != null ? resultsFile.getPath() : "No results file selected", resultsFile != null ? resultsFile.getPath().length() : new String("No results file selected").length());
/* 151 */     gd.addMessage("(changing field does not change results file destination)");
/* 152 */     gd.showDialog();
/*     */ 
/* 154 */     if (gd.wasCanceled()) {
/* 155 */       return false;
/*     */     }
/* 157 */     if (gd.invalidNumber()) {
/* 158 */       IJ.error("Invalid number.");
/* 159 */       return false;
/*     */     }
/*     */ 
/* 162 */     staticCondition = gd.getNextString();
/*     */ 
/* 164 */     if (gd.getNextBoolean())
/* 165 */       staticColorChannels |= 1;
/*     */     else {
/* 167 */       staticColorChannels &= -2;
/*     */     }
/* 169 */     if (gd.getNextBoolean())
/* 170 */       staticColorChannels |= 2;
/*     */     else {
/* 172 */       staticColorChannels &= -3;
/*     */     }
/* 174 */     if (gd.getNextBoolean())
/* 175 */       staticColorChannels |= 4;
/*     */     else {
/* 177 */       staticColorChannels &= -5;
/*     */     }
/* 179 */     if (gd.getNextBoolean())
/* 180 */       staticSubtractChannelBackground |= 1;
/*     */     else {
/* 182 */       staticSubtractChannelBackground &= -2;
/*     */     }
/* 184 */     if (gd.getNextBoolean())
/* 185 */       staticSubtractChannelBackground |= 2;
/*     */     else {
/* 187 */       staticSubtractChannelBackground &= -3;
/*     */     }
/* 189 */     if (gd.getNextBoolean())
/* 190 */       staticSubtractChannelBackground |= 4;
/*     */     else {
/* 192 */       staticSubtractChannelBackground &= -5;
/*     */     }
/* 194 */     if (gd.getNextBoolean()) {
/* 195 */       changeResultsFile();
/*     */     }
/* 197 */     if (gd.getNextBoolean())
/* 198 */       staticSaveResults = true;
/*     */     else {
/* 200 */       staticSaveResults = false;
/*     */     }
/*     */ 
/* 205 */     return true;
/*     */   }
/*     */ 
/*     */   protected void changeResultsFile()
/*     */   {
/* 218 */     FileDialog fileD = new FileDialog(IJ.getInstance(), "Results File", 1);
/*     */ 
/* 220 */     fileD.show();
/*     */ 
/* 222 */     if (fileD.getFile() != null) {
/* 223 */       resultsFileName = fileD.getFile();
/* 224 */       resultsFile = new File(fileD.getDirectory(), resultsFileName);
/*     */       try {
/* 226 */         if (resultsFile.createNewFile()) {
/* 227 */           PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(resultsFile)));
/* 228 */           writeHeaderToStream(out);
/* 229 */           out.close();
/* 230 */           staticReultsFileHeaderWritten = true;
/*     */         }
/*     */         else {
/* 233 */           staticReultsFileHeaderWritten = true;
/*     */         }
/*     */       } catch (IOException e) {
/* 236 */         MessageDialog d = new MessageDialog(IJ.getInstance(), "Error", "Could not read/create results file. Results will not be saved.");
/* 237 */         d.show();
/* 238 */         resultsFile = null;
/*     */       }
/*     */     }
/*     */ 
/* 242 */     fileD.dispose();
/*     */   }
/*     */ 
/*     */   public void run(ImageProcessor ip)
/*     */   {
/* 251 */     ResultsTable[] rTables = null;
/* 252 */     this.puncta = new List[4];
/*     */ 
/* 255 */     if ((!IJ.versionLessThan("1.22")) && 
/* 256 */       (runPrefsDialog())) {
/* 257 */       this.imp.startTiming();
/* 258 */       rTables = locatePuncta(this.imp);
/*     */       MessageDialog d;
/* 260 */       if (rTables != null) {
/* 261 */         IJ.showStatus("Computing Colocalization...");
/* 262 */         analyzePuncta(rTables);
/* 263 */         displayColocalizedPuncta(this.imp);
/* 264 */         displayPunctaStatistics();
/* 265 */         if ((resultsFile != null) && (staticSaveResults) && 
/* 266 */           (userRequestsSaveData()))
/* 267 */           if (resultsFile.canWrite()) { MessageDialog d;
/*     */             try { writeResultsToFile();
/*     */             } catch (IOException e) {
/* 271 */               d = new MessageDialog(IJ.getInstance(), "Error", "Could not write to results file. Results were not saved.");
/*     */             }
/*     */           } else {
/* 274 */             d = new MessageDialog(IJ.getInstance(), "Error", "Could not write to results file. Results were not saved.");
/*     */           }
/*     */       }
/*     */       else
/*     */       {
/* 279 */         IJ.error("Analysis failed. Keep counting, sucker.");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean userRequestsSaveData()
/*     */   {
/* 292 */     return JOptionPane.showOptionDialog(null, "Save analysis results to file?", "", 0, 3, null, null, null) == 0;
/*     */   }
/*     */ 
/*     */   protected void displayPunctaStatistics()
/*     */   {
/* 301 */     if (this.puncta[0] != null) {
/* 302 */       IJ.write("Statistics for red channel puncta:");
/* 303 */       displayChannelPunctaStatistics(0);
/*     */     }
/*     */ 
/* 306 */     if (this.puncta[1] != null) {
/* 307 */       IJ.write("Statistics for green channel puncta:");
/* 308 */       displayChannelPunctaStatistics(1);
/*     */     }
/*     */ 
/* 311 */     if (this.puncta[2] != null) {
/* 312 */       IJ.write("Statistics for blue channel puncta:");
/* 313 */       displayChannelPunctaStatistics(2);
/*     */     }
/*     */ 
/* 316 */     if (this.puncta[3] != null) {
/* 317 */       IJ.write("Statistics for colocalized puncta:");
/* 318 */       displayChannelPunctaStatistics(3);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Puncta_Analyzer.ChannelSummaryStatistics channelSummaryStatistics(int index)
/*     */   {
/* 341 */     Iterator iter = this.puncta[index].iterator();
/*     */ 
/* 343 */     double areaSum = 0.0D;
/* 344 */     double minSum = 0.0D;
/* 345 */     double maxSum = 0.0D;
/* 346 */     double meanSum = 0.0D;
/* 347 */     while (iter.hasNext()) {
/* 348 */       Puncta curr = (Puncta)iter.next();
/*     */ 
/* 350 */       areaSum += curr.area();
/* 351 */       minSum += curr.min();
/* 352 */       maxSum += curr.max();
/* 353 */       meanSum += curr.mean();
/*     */     }
/*     */ 
/* 356 */     int size = this.puncta[index].size();
/*     */ 
/* 358 */     double areaAvg = areaSum / size;
/* 359 */     double minAvg = minSum / size;
/* 360 */     double maxAvg = maxSum / size;
/* 361 */     double meanAvg = meanSum / size;
/*     */ 
/* 363 */     return new Puncta_Analyzer.ChannelSummaryStatistics(size, areaAvg, minAvg, maxAvg, meanAvg);
/*     */   }
/*     */ 
/*     */   protected void displayChannelPunctaStatistics(int index)
/*     */   {
/* 375 */     Puncta_Analyzer.ChannelSummaryStatistics stat = channelSummaryStatistics(index);
/*     */ 
/* 377 */     BigDecimal bdAreaAvg = (Double.isNaN(stat.areaAvg)) || (Double.isInfinite(stat.areaAvg)) ? null : new BigDecimal(stat.areaAvg);
/* 378 */     BigDecimal bdMinAvg = (Double.isNaN(stat.minAvg)) || (Double.isInfinite(stat.minAvg)) ? null : new BigDecimal(stat.minAvg);
/* 379 */     BigDecimal bdMaxAvg = (Double.isNaN(stat.maxAvg)) || (Double.isInfinite(stat.maxAvg)) ? null : new BigDecimal(stat.maxAvg);
/* 380 */     BigDecimal bdMeanAvg = (Double.isNaN(stat.meanAvg)) || (Double.isInfinite(stat.meanAvg)) ? null : new BigDecimal(stat.meanAvg);
/*     */ 
/* 383 */     if (bdAreaAvg != null)
/* 384 */       bdAreaAvg = bdAreaAvg.setScale(1, 6);
/* 385 */     if (bdMinAvg != null)
/* 386 */       bdMinAvg = bdMinAvg.setScale(1, 6);
/* 387 */     if (bdMaxAvg != null)
/* 388 */       bdMaxAvg = bdMaxAvg.setScale(1, 6);
/* 389 */     if (bdMeanAvg != null) {
/* 390 */       bdMeanAvg = bdMeanAvg.setScale(1, 6);
/*     */     }
/*     */ 
/* 393 */     IJ.write("Number: " + stat.num);
/* 394 */     if (bdAreaAvg != null)
/* 395 */       IJ.write("Avg. Area: " + bdAreaAvg + " (" + stat.areaAvg + ")");
/*     */     else {
/* 397 */       IJ.write("Avg. Area: " + stat.areaAvg);
/*     */     }
/* 399 */     if (bdMinAvg != null)
/* 400 */       IJ.write("Avg. Min: " + bdMinAvg + " (" + stat.minAvg + ")");
/*     */     else {
/* 402 */       IJ.write("Avg. Min: " + stat.minAvg);
/*     */     }
/* 404 */     if (bdMaxAvg != null)
/* 405 */       IJ.write("Avg. Max: " + bdMaxAvg + " (" + stat.maxAvg + ")");
/*     */     else {
/* 407 */       IJ.write("Avg. Max: " + stat.maxAvg);
/*     */     }
/* 409 */     if (bdMeanAvg != null)
/* 410 */       IJ.write("Avg. Mean: " + bdMeanAvg + " (" + stat.meanAvg + ")");
/*     */     else
/* 412 */       IJ.write("Avg. Mean: " + stat.meanAvg);
/*     */   }
/*     */ 
/*     */   protected void writeResultsToFile()
/*     */     throws IOException
/*     */   {
/* 423 */     PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(resultsFile.getPath(), true)));
/*     */ 
/* 425 */     if (!staticReultsFileHeaderWritten) {
/* 426 */       writeHeaderToStream(out);
/*     */     }
/*     */ 
/* 429 */     writeImageDataToStream(out);
/*     */ 
/* 431 */     writeChannelResultsToStream(0, out);
/* 432 */     writeChannelResultsToStream(1, out);
/* 433 */     writeChannelResultsToStream(2, out);
/* 434 */     writeChannelResultsToStream(3, out);
/*     */ 
/* 436 */     out.println();
/* 437 */     out.close();
/*     */   }
/*     */ 
/*     */   protected void writeHeaderToStream(PrintWriter out)
/*     */   {
/* 448 */     out.print("Image name");
/* 449 */     out.print(resultsDelimeter);
/* 450 */     out.print("Condition");
/* 451 */     out.print(resultsDelimeter);
/* 452 */     out.print("Channel Name");
/* 453 */     out.print(resultsDelimeter);
/* 454 */     out.print("Num puncta");
/* 455 */     out.print(resultsDelimeter);
/* 456 */     out.print("Area average");
/* 457 */     out.print(resultsDelimeter);
/* 458 */     out.print("Min intensity average");
/* 459 */     out.print(resultsDelimeter);
/* 460 */     out.print("Max intensity average");
/* 461 */     out.print(resultsDelimeter);
/* 462 */     out.print("Mean intensity average");
/* 463 */     out.print(resultsDelimeter);
/* 464 */     out.print("...");
/* 465 */     out.println();
/*     */   }
/*     */ 
/*     */   protected void writeImageDataToStream(PrintWriter out)
/*     */   {
/* 475 */     out.print(this.imp.getTitle());
/* 476 */     out.print(resultsDelimeter);
/* 477 */     out.print(staticCondition);
/* 478 */     out.print(resultsDelimeter);
/*     */   }
/*     */ 
/*     */   protected void writeChannelResultsToStream(int index, PrintWriter out)
/*     */   {
/* 493 */     if (this.puncta[index] != null) {
/* 494 */       Puncta_Analyzer.ChannelSummaryStatistics stat = channelSummaryStatistics(index);
/*     */ 
/* 496 */       out.print(channelNameForPunctaIndex(index));
/* 497 */       out.print(resultsDelimeter);
/* 498 */       out.print(stat.num);
/* 499 */       out.print(resultsDelimeter);
/* 500 */       out.print(stat.areaAvg);
/* 501 */       out.print(resultsDelimeter);
/* 502 */       out.print(stat.minAvg);
/* 503 */       out.print(resultsDelimeter);
/* 504 */       out.print(stat.maxAvg);
/* 505 */       out.print(resultsDelimeter);
/* 506 */       out.print(stat.meanAvg);
/* 507 */       out.print(resultsDelimeter);
/*     */     } else {
/* 509 */       out.print(channelNameForPunctaIndex(index));
/* 510 */       out.print(resultsDelimeter);
/* 511 */       out.print("-");
/* 512 */       out.print(resultsDelimeter);
/* 513 */       out.print("-");
/* 514 */       out.print(resultsDelimeter);
/* 515 */       out.print("-");
/* 516 */       out.print(resultsDelimeter);
/* 517 */       out.print("-");
/* 518 */       out.print(resultsDelimeter);
/* 519 */       out.print("-");
/* 520 */       out.print(resultsDelimeter);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String channelNameForPunctaIndex(int index)
/*     */   {
/* 533 */     switch (index) {
/*     */     case 0:
/* 535 */       return "red channel puncta";
/*     */     case 1:
/* 537 */       return "green channel puncta";
/*     */     case 2:
/* 539 */       return "blue channel puncta";
/*     */     case 3:
/* 541 */       return "colocalized puncta";
/*     */     }
/*     */ 
/* 544 */     return "error: no channel for index";
/*     */   }
/*     */ 
/*     */   protected void analyzePuncta(ResultsTable[] rTables)
/*     */   {
/* 556 */     if ((staticColorChannels & 0x1) != 0) {
/* 557 */       IJ.showStatus("Analyzing Puncta from Red Channel...");
/* 558 */       if (rTables[1] == null)
/* 559 */         IJ.error("Data for Red Channel is null");
/*     */       else
/* 561 */         this.puncta[0] = toVector(rTables[1]);
/*     */     } else {
/* 563 */       this.puncta[0] = null;
/*     */     }
/*     */ 
/* 566 */     if ((staticColorChannels & 0x2) != 0) {
/* 567 */       IJ.showStatus("Analyzing Puncta from Green Channel...");
/* 568 */       if (rTables[2] == null)
/* 569 */         IJ.error("Data for Green Channel is null");
/*     */       else
/* 571 */         this.puncta[1] = toVector(rTables[2]);
/*     */     } else {
/* 573 */       this.puncta[1] = null;
/*     */     }
/*     */ 
/* 576 */     if ((staticColorChannels & 0x4) != 0) {
/* 577 */       IJ.showStatus("Analyzing Puncta from Blue Channel...");
/* 578 */       if (rTables[4] == null)
/* 579 */         IJ.error("Data for Blue Channel is null");
/*     */       else
/* 581 */         this.puncta[2] = toVector(rTables[4]);
/*     */     } else {
/* 583 */       this.puncta[2] = null;
/*     */     }
/*     */ 
/* 586 */     this.puncta[3] = computeColocalization();
/*     */   }
/*     */ 
/*     */   protected double computeRadius(Puncta p)
/*     */   {
/* 597 */     return Math.sqrt(p.area / 3.141592653589793D);
/*     */   }
/*     */ 
/*     */   protected Vector computeTwoChannelColocalization(List c1Puncta, List c2Puncta) {
/* 601 */     Vector coloc = new Vector();
/*     */ 
/* 606 */     Iterator c1Iterator = c1Puncta.iterator();
/*     */ 
/* 608 */     while (c1Iterator.hasNext()) {
/* 609 */       Puncta currPuncta = (Puncta)c1Iterator.next();
/* 610 */       double radius = computeRadius(currPuncta);
/*     */ 
/* 612 */       Iterator c2Iterator = c2Puncta.iterator();
/*     */ 
/* 614 */       while (c2Iterator.hasNext()) {
/* 615 */         Puncta targetPuncta = (Puncta)c2Iterator.next();
/*     */ 
/* 617 */         if (currPuncta.distanceTo(targetPuncta) > radius + computeRadius(targetPuncta))
/*     */         {
/*     */           continue;
/*     */         }
/* 621 */         double x = currPuncta.getX() + (targetPuncta.getX() - currPuncta.getX()) / 2.0D;
/* 622 */         double y = currPuncta.getY() + (targetPuncta.getY() - currPuncta.getY()) / 2.0D;
/*     */ 
/* 624 */         double area = (currPuncta.area() + targetPuncta.area()) / 2.0D;
/* 625 */         double perimeter = (currPuncta.perimeter() + targetPuncta.perimeter()) / 2.0D;
/* 626 */         double max = (currPuncta.max() + targetPuncta.max()) / 2.0D;
/* 627 */         double min = (currPuncta.min() + targetPuncta.min()) / 2.0D;
/* 628 */         double mean = (currPuncta.mean() + targetPuncta.mean()) / 2.0D;
/*     */ 
/* 630 */         coloc.add(new Puncta(x, y, area, perimeter, max, min, mean));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 637 */     return coloc;
/*     */   }
/*     */ 
/*     */   protected Vector computeColocalization()
/*     */   {
/* 647 */     Vector coloc = null;
/*     */ 
/* 650 */     switch (staticColorChannels) {
/*     */     case 3:
/* 652 */       coloc = computeTwoChannelColocalization(this.puncta[0], this.puncta[1]);
/* 653 */       break;
/*     */     case 5:
/* 656 */       coloc = computeTwoChannelColocalization(this.puncta[0], this.puncta[2]);
/* 657 */       break;
/*     */     case 6:
/* 660 */       coloc = computeTwoChannelColocalization(this.puncta[1], this.puncta[2]);
/* 661 */       break;
/*     */     case 7:
/* 664 */       coloc = computeTwoChannelColocalization(this.puncta[0], this.puncta[1]);
/* 665 */       coloc = computeTwoChannelColocalization(this.puncta[0], coloc);
/* 666 */       break;
/*     */     case 4:
/*     */     default:
/* 669 */       IJ.write("More than one color channel must be selected to compute colocalization.");
/*     */     }
/*     */ 
/* 673 */     return coloc;
/*     */   }
/*     */ 
/*     */   protected Vector toVector(ResultsTable table)
/*     */   {
/* 684 */     Vector vector = new Vector();
/*     */ 
/* 690 */     float[] x_centroid = table.getColumn(table.getColumnIndex("X"));
/* 691 */     float[] y_centroid = table.getColumn(table.getColumnIndex("Y"));
/* 692 */     float[] area = table.getColumn(table.getColumnIndex("Area"));
/* 693 */     float[] max = table.getColumn(table.getColumnIndex("Max"));
/* 694 */     float[] min = table.getColumn(table.getColumnIndex("Min"));
/* 695 */     float[] mean = table.getColumn(table.getColumnIndex("Mean"));
/*     */ 
/* 697 */     int numPuncta = table.getCounter();
/*     */ 
/* 699 */     Roi roi = this.imp.getRoi();
/*     */     Rectangle roiRect;
/*     */     Rectangle roiRect;
/* 700 */     if (roi != null)
/* 701 */       roiRect = roi.getBoundingRect();
/*     */     else {
/* 703 */       roiRect = null;
/*     */     }
/*     */ 
/* 706 */     for (int i = 0; i < numPuncta; i++) {
/* 707 */       if ((roi == null) || ((roi != null) && (roi.contains((int)x_centroid[i] + (int)roiRect.getX(), (int)y_centroid[i] + (int)roiRect.getY())))) {
/* 708 */         vector.add(new Puncta((int)x_centroid[i] + roiRect.getX(), (int)y_centroid[i] + roiRect.getY(), area[i], max[i], min[i], mean[i]));
/*     */       }
/*     */     }
/*     */ 
/* 712 */     return vector;
/*     */   }
/*     */ 
/*     */   protected void displayColocalizedPuncta(ImagePlus imp)
/*     */   {
/* 726 */     if (this.puncta[3] != null) {
/* 727 */       Iterator iter = this.puncta[3].iterator();
/*     */ 
/* 729 */       while (iter.hasNext())
/* 730 */         drawPuncta((Puncta)iter.next(), imp);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawPuncta(Puncta p, ImagePlus imp)
/*     */   {
/* 743 */     ImageProcessor ip = imp.getProcessor();
/*     */ 
/* 745 */     ip.setLineWidth(2);
/* 746 */     ip.drawDot((int)p.getX(), (int)p.getY());
/*     */ 
/* 749 */     imp.updateAndDraw();
/*     */   }
/*     */ 
/*     */   public ResultsTable[] locatePuncta(ImagePlus imp)
/*     */   {
/* 762 */     ResultsTable[] rTables = new ResultsTable[5];
/*     */ 
/* 764 */     if (imp.getType() != 4) {
/* 765 */       IJ.error("RGB image required.");
/* 766 */       return null;
/*     */     }
/*     */ 
/* 769 */     ColorProcessor cp = (ColorProcessor)imp.getProcessor();
/* 770 */     int arrayLength = imp.getHeight() * imp.getWidth();
/* 771 */     byte[] rBytes = new byte[arrayLength];
/* 772 */     byte[] gBytes = new byte[arrayLength];
/* 773 */     byte[] bBytes = new byte[arrayLength];
/*     */ 
/* 775 */     cp.getRGB(rBytes, gBytes, bBytes);
/*     */ 
/* 777 */     IJ.showStatus("Ready to analyze."); IJ.wait(1000);
/*     */ 
/* 780 */     if ((staticColorChannels & 0x1) != 0) {
/* 781 */       IJ.showStatus("Analyzing Red...");
/* 782 */       rTables[1] = locatePunctaInColorBand(imp, rBytes, "Red", (staticSubtractChannelBackground & 0x1) != 0 ? 1 : false);
/* 783 */       if (rTables[1] == null) {
/* 784 */         return null;
/*     */       }
/*     */     }
/* 787 */     if ((staticColorChannels & 0x2) != 0) {
/* 788 */       IJ.showStatus("Analyzing Green...");
/* 789 */       rTables[2] = locatePunctaInColorBand(imp, gBytes, "Green", (staticSubtractChannelBackground & 0x2) != 0 ? 1 : false);
/* 790 */       if (rTables[2] == null) {
/* 791 */         return null;
/*     */       }
/*     */     }
/* 794 */     if ((staticColorChannels & 0x4) != 0) {
/* 795 */       IJ.showStatus("Analyzing Blue...");
/* 796 */       rTables[4] = locatePunctaInColorBand(imp, bBytes, "Blue", (staticSubtractChannelBackground & 0x4) != 0 ? 1 : false);
/* 797 */       if (rTables[4] == null) {
/* 798 */         return null;
/*     */       }
/*     */     }
/* 801 */     return rTables;
/*     */   }
/*     */ 
/*     */   protected ResultsTable locatePunctaInColorBand(ImagePlus imp, byte[] bytes, String color, boolean subtractBackground)
/*     */   {
/* 817 */     Rectangle roi = imp.getProcessor().getRoi();
/* 818 */     ResultsTable rTable = new ResultsTable();
/*     */ 
/* 820 */     ByteProcessor bp = new ByteProcessor(imp.getWidth(), imp.getHeight());
/* 821 */     bp.setPixels(bytes);
/*     */ 
/* 823 */     if (roi != null) {
/* 824 */       bp.setRoi(roi);
/* 825 */       bp = (ByteProcessor)bp.crop();
/*     */     }
/*     */ 
/* 829 */     ImagePlus byteImp = new ImagePlus(color + " Channel Image", bp);
/*     */ 
/* 831 */     Puncta_Analyzer.ScopeImageUtils.contractHistogram(byteImp);
/*     */ 
/* 833 */     byteImp.show();
/* 834 */     byteImp.updateAndDraw();
/*     */ 
/* 836 */     if (subtractBackground) {
/* 837 */       IJ.run("Subtract Background...");
/*     */     }
/*     */ 
/* 840 */     PunctaMaskerFactory.getMasker().mask();
/*     */ 
/* 842 */     byteImp.updateAndDraw();
/*     */ 
/* 844 */     IJ.showStatus("Running Particle Analyzer on " + imp.getTitle());
/*     */ 
/* 846 */     this.analyzer = new ParticleAnalyzer(staticPartAnalyzerOptions, staticPartAnalyzerMeasurements, rTable, staticPartAnalyzerMinSize, staticPartAnalyzerMaxSize);
/*     */ 
/* 848 */     this.analyzer.setup(this.arg, byteImp);
/* 849 */     this.analyzer.run(bp);
/*     */ 
/* 851 */     byteImp.hide();
/*     */ 
/* 853 */     return rTable;
/*     */   }
/*     */ 
/*     */   protected class ChannelSummaryStatistics
/*     */   {
/*     */     public double areaAvg;
/*     */     public double minAvg;
/*     */     public double maxAvg;
/*     */     public double meanAvg;
/*     */     public int num;
/*     */ 
/*     */     public ChannelSummaryStatistics(int num, double areaAvg, double minAvg, double maxAvg, double meanAvg)
/*     */     {
/* 911 */       this.num = num;
/* 912 */       this.areaAvg = areaAvg;
/* 913 */       this.minAvg = minAvg;
/* 914 */       this.maxAvg = maxAvg;
/* 915 */       this.meanAvg = meanAvg;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static class ScopeImageUtils
/*     */     implements Measurements
/*     */   {
/*     */     public static void contractHistogram(ImagePlus imp)
/*     */     {
/* 861 */       ImageProcessor ip = imp.getProcessor();
/*     */ 
/* 863 */       int minUsedValue = 0;
/*     */ 
/* 866 */       ip.setMask(imp.getMask());
/*     */ 
/* 868 */       int[] histogram = imageHistogram(ip);
/*     */ 
/* 870 */       int maxUsedValue = histogram.length - 1;
/*     */ 
/* 872 */       while (histogram[minUsedValue] == 0) {
/* 873 */         minUsedValue++;
/*     */       }
/*     */ 
/* 876 */       while (histogram[maxUsedValue] == 0)
/* 877 */         maxUsedValue--;
/*     */     }
/*     */ 
/*     */     public static int[] imageHistogram(ImageProcessor ip)
/*     */     {
/* 890 */       if (!(ip instanceof ByteProcessor)) {
/* 891 */         double min = ip.getMin();
/* 892 */         double max = ip.getMax();
/* 893 */         ip.setMinAndMax(min, max);
/* 894 */         ip = new ByteProcessor(ip.createImage());
/*     */       }
/*     */ 
/* 897 */       return ip.getHistogram();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/todd/Projects/puncta-analyzer/v1/PunctaAnalyzer/
 * Qualified Name:     Puncta_Analyzer
 * JD-Core Version:    0.6.0
 */