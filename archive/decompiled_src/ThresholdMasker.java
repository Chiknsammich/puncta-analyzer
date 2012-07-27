/*     */ import ij.IJ;
/*     */ import ij.ImageJ;
/*     */ import ij.ImagePlus;
/*     */ import ij.ImageStack;
/*     */ import ij.Undo;
/*     */ import ij.WindowManager;
/*     */ import ij.gui.GUI;
/*     */ import ij.gui.GenericDialog;
/*     */ import ij.gui.Toolbar;
/*     */ import ij.gui.YesNoCancelDialog;
/*     */ import ij.measure.Calibration;
/*     */ import ij.measure.Measurements;
/*     */ import ij.plugin.PlugIn;
/*     */ import ij.plugin.frame.PasteController;
/*     */ import ij.plugin.frame.PlugInFrame;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import ij.process.ShortProcessor;
/*     */ import ij.process.StackProcessor;
/*     */ import java.awt.Button;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.Scrollbar;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ 
/*     */ public class ThresholdMasker extends PlugInFrame
/*     */   implements PlugIn, Measurements, Runnable, ActionListener, AdjustmentListener, MaskerIF
/*     */ {
/*     */   protected static final double defaultMinThreshold = 65.0D;
/*     */   protected static final double defaultMaxThreshold = 255.0D;
/*     */   protected static final String DONE = "Done";
/*  40 */   protected static boolean fill1 = true;
/*  41 */   protected static boolean fill2 = true;
/*  42 */   protected static boolean useBW = true;
/*     */   protected static Frame instance;
/*  45 */   protected ThresholdPlot plot = new ThresholdPlot();
/*     */   protected Thread thread;
/*  48 */   protected int minValue = -1;
/*  49 */   protected int maxValue = -1;
/*  50 */   protected int sliderRange = 256;
/*     */   protected boolean doAutoAdjust;
/*     */   protected boolean doReset;
/*     */   protected boolean doApplyLut;
/*     */   protected boolean doStateChange;
/*     */   protected boolean doSet;
/*     */   protected Panel panel;
/*     */   protected Button autoB;
/*     */   protected Button resetB;
/*     */   protected Button applyB;
/*     */   protected Button stateB;
/*     */   protected Button setB;
/*     */   protected int previousImageID;
/*     */   protected int previousImageType;
/*     */   protected double previousMin;
/*     */   protected double previousMax;
/*     */   protected ImageJ ij;
/*     */   protected double minThreshold;
/*     */   protected double maxThreshold;
/*     */   protected Scrollbar minSlider;
/*     */   protected Scrollbar maxSlider;
/*     */   protected Label label1;
/*     */   protected Label label2;
/*     */   protected boolean done;
/*     */   protected boolean invertedLut;
/*     */   protected boolean blackAndWhite;
/*  65 */   protected int lutColor = 0;
/*     */   static final int RESET = 0;
/*     */   static final int AUTO = 1;
/*     */   static final int HIST = 2;
/*     */   static final int APPLY = 3;
/*     */   static final int STATE_CHANGE = 4;
/*     */   static final int MIN_THRESHOLD = 5;
/*     */   static final int MAX_THRESHOLD = 6;
/*     */   static final int SET = 7;
/*     */ 
/*     */   public void mask()
/*     */   {
/*  74 */     this.thread.start();
/*  75 */     setVisible(true);
/*     */     try
/*     */     {
/*  78 */       syncAdjust();
/*     */     } catch (InterruptedException e) {
/*  80 */       IJ.error("Masker could not complete because an exception occured.");
/*     */     }
/*     */ 
/*  83 */     dispose();
/*     */   }
/*     */ 
/*     */   public ThresholdMasker() {
/*  87 */     super("Threshold");
/*  88 */     if (instance != null) {
/*  89 */       instance.toFront();
/*  90 */       return;
/*     */     }
/*  92 */     instance = this;
/*  93 */     IJ.register(PasteController.class);
/*     */ 
/*  95 */     this.ij = IJ.getInstance();
/*     */ 
/*  97 */     initGUI();
/*     */ 
/*  99 */     this.thread = new Thread(this, "ThresholdAdjuster");
/*     */ 
/* 102 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 103 */     if (imp != null) {
/* 104 */       setup(imp);
/*     */     }
/* 106 */     this.done = false;
/*     */   }
/*     */ 
/*     */   protected void initGUI() {
/* 110 */     Font font = new Font("SansSerif", 0, 10);
/* 111 */     GridBagLayout gridbag = new GridBagLayout();
/* 112 */     GridBagConstraints c = new GridBagConstraints();
/* 113 */     setLayout(gridbag);
/*     */ 
/* 116 */     int y = 0;
/* 117 */     c.gridx = 0;
/* 118 */     c.gridy = (y++);
/* 119 */     c.gridwidth = 2;
/* 120 */     c.fill = 1;
/* 121 */     c.anchor = 10;
/* 122 */     c.insets = new Insets(10, 10, 0, 10);
/* 123 */     add(this.plot, c);
/*     */ 
/* 126 */     this.minSlider = new Scrollbar(0, this.sliderRange / 3, 1, 0, this.sliderRange);
/* 127 */     c.gridx = 0;
/* 128 */     c.gridy = (y++);
/* 129 */     c.gridwidth = 1;
/* 130 */     c.weightx = (IJ.isMacintosh() ? 90.0D : 100.0D);
/* 131 */     c.fill = 2;
/* 132 */     c.insets = new Insets(5, 10, 0, 0);
/* 133 */     add(this.minSlider, c);
/* 134 */     this.minSlider.addAdjustmentListener(this);
/* 135 */     this.minSlider.setUnitIncrement(1);
/*     */ 
/* 138 */     c.gridx = 1;
/* 139 */     c.gridwidth = 1;
/* 140 */     c.weightx = (IJ.isMacintosh() ? 10.0D : 0.0D);
/* 141 */     c.insets = new Insets(5, 0, 0, 10);
/* 142 */     this.label1 = new Label("       ", 2);
/* 143 */     this.label1.setFont(font);
/* 144 */     add(this.label1, c);
/*     */ 
/* 147 */     this.maxSlider = new Scrollbar(0, this.sliderRange * 2 / 3, 1, 0, this.sliderRange);
/* 148 */     c.gridx = 0;
/* 149 */     c.gridy = (y++);
/* 150 */     c.gridwidth = 1;
/* 151 */     c.weightx = 100.0D;
/* 152 */     c.insets = new Insets(0, 10, 0, 0);
/* 153 */     add(this.maxSlider, c);
/* 154 */     this.maxSlider.addAdjustmentListener(this);
/* 155 */     this.maxSlider.setUnitIncrement(1);
/*     */ 
/* 158 */     c.gridx = 1;
/* 159 */     c.gridwidth = 1;
/* 160 */     c.weightx = 0.0D;
/* 161 */     c.insets = new Insets(0, 0, 0, 10);
/* 162 */     this.label2 = new Label("       ", 2);
/* 163 */     this.label2.setFont(font);
/* 164 */     add(this.label2, c);
/*     */ 
/* 167 */     this.panel = new Panel();
/*     */ 
/* 169 */     this.resetB = new Button("Reset");
/* 170 */     this.resetB.addActionListener(this);
/* 171 */     this.resetB.addKeyListener(this.ij);
/* 172 */     this.panel.add(this.resetB);
/* 173 */     Button doneB = new Button("Done");
/* 174 */     doneB.addActionListener(this);
/* 175 */     doneB.addKeyListener(this.ij);
/* 176 */     this.panel.add(doneB);
/*     */ 
/* 182 */     c.gridx = 0;
/* 183 */     c.gridy = (y++);
/* 184 */     c.gridwidth = 2;
/* 185 */     c.insets = new Insets(5, 5, 10, 5);
/* 186 */     add(this.panel, c);
/*     */ 
/* 188 */     addKeyListener(this.ij);
/* 189 */     pack();
/* 190 */     GUI.center(this);
/*     */   }
/*     */ 
/*     */   public synchronized void adjustmentValueChanged(AdjustmentEvent e)
/*     */   {
/* 196 */     if (e.getSource() == this.minSlider)
/* 197 */       this.minValue = this.minSlider.getValue();
/*     */     else
/* 199 */       this.maxValue = this.maxSlider.getValue();
/* 200 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public synchronized void actionPerformed(ActionEvent e) {
/* 204 */     Button b = (Button)e.getSource();
/* 205 */     if (b == null) return;
/* 206 */     if (b.getActionCommand().equals("Done")) {
/* 207 */       done();
/* 208 */       return;
/*     */     }
/* 210 */     if (b == this.resetB) {
/* 211 */       this.doReset = true;
/* 212 */     } else if (b == this.autoB) {
/* 213 */       this.doAutoAdjust = true;
/* 214 */     } else if (b == this.applyB) {
/* 215 */       this.doApplyLut = true;
/* 216 */     } else if (b == this.stateB) {
/* 217 */       this.blackAndWhite = this.stateB.getLabel().equals("B&W");
/* 218 */       if (this.blackAndWhite) {
/* 219 */         this.stateB.setLabel("Red");
/* 220 */         this.lutColor = 1;
/*     */       } else {
/* 222 */         this.stateB.setLabel("B&W");
/* 223 */         this.lutColor = 0;
/*     */       }
/* 225 */       this.doStateChange = true;
/* 226 */     } else if (b == this.setB) {
/* 227 */       this.doSet = true;
/* 228 */     }notifyAll();
/*     */   }
/*     */ 
/*     */   ImageProcessor setup(ImagePlus imp)
/*     */   {
/* 233 */     int type = imp.getType();
/* 234 */     if (type == 4)
/* 235 */       return null;
/* 236 */     ImageProcessor ip = imp.getProcessor();
/* 237 */     boolean minMaxChange = false;
/* 238 */     if ((type == 1) || (type == 2)) {
/* 239 */       if ((ip.getMin() != this.previousMin) || (ip.getMax() != this.previousMax))
/* 240 */         minMaxChange = true;
/* 241 */       this.previousMin = ip.getMin();
/* 242 */       this.previousMax = ip.getMax();
/*     */     }
/* 244 */     int id = imp.getID();
/* 245 */     if ((minMaxChange) || (id != this.previousImageID) || (type != this.previousImageType)) {
/* 246 */       this.invertedLut = imp.isInvertedLut();
/* 247 */       this.minThreshold = ip.getMinThreshold();
/* 248 */       this.maxThreshold = ip.getMaxThreshold();
/* 249 */       if (this.minThreshold == -808080.0D) {
/* 250 */         this.minThreshold = 65.0D;
/* 251 */         this.maxThreshold = 255.0D;
/*     */       }
/* 253 */       this.plot.setHistogram(imp);
/* 254 */       scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 255 */       updateLabels(imp, ip);
/* 256 */       updatePlot();
/* 257 */       updateScrollBars();
/* 258 */       imp.updateAndDraw();
/*     */     }
/* 260 */     this.previousImageID = id;
/* 261 */     this.previousImageType = type;
/* 262 */     return ip;
/*     */   }
/*     */ 
/*     */   void scaleUpAndSet(ImageProcessor ip, double minThreshold, double maxThreshold)
/*     */   {
/* 267 */     if ((!(ip instanceof ByteProcessor)) && (minThreshold != -808080.0D)) {
/* 268 */       double min = ip.getMin();
/* 269 */       double max = ip.getMax();
/* 270 */       if (max > min) {
/* 271 */         minThreshold = min + minThreshold / 255.0D * (max - min);
/* 272 */         maxThreshold = min + maxThreshold / 255.0D * (max - min);
/*     */       } else {
/* 274 */         minThreshold = -808080.0D;
/*     */       }
/*     */     }
/* 276 */     ip.setThreshold(minThreshold, maxThreshold, this.lutColor);
/*     */   }
/*     */ 
/*     */   double scaleDown(ImageProcessor ip, double threshold)
/*     */   {
/* 281 */     double min = ip.getMin();
/* 282 */     double max = ip.getMax();
/* 283 */     if (max > min) {
/* 284 */       return (threshold - min) / (max - min) * 255.0D;
/*     */     }
/* 286 */     return -808080.0D;
/*     */   }
/*     */ 
/*     */   double scaleUp(ImageProcessor ip, double threshold)
/*     */   {
/* 291 */     double min = ip.getMin();
/* 292 */     double max = ip.getMax();
/* 293 */     if (max > min) {
/* 294 */       return min + threshold / 255.0D * (max - min);
/*     */     }
/* 296 */     return -808080.0D;
/*     */   }
/*     */ 
/*     */   void updatePlot() {
/* 300 */     this.plot.minThreshold = this.minThreshold;
/* 301 */     this.plot.maxThreshold = this.maxThreshold;
/* 302 */     this.plot.blackAndWhite = this.blackAndWhite;
/* 303 */     this.plot.repaint();
/*     */   }
/*     */ 
/*     */   void updateLabels(ImagePlus imp, ImageProcessor ip) {
/* 307 */     double min = ip.getMinThreshold();
/* 308 */     double max = ip.getMaxThreshold();
/* 309 */     if (min == -808080.0D) {
/* 310 */       this.label1.setText("");
/* 311 */       this.label2.setText("");
/*     */     } else {
/* 313 */       Calibration cal = imp.getCalibration();
/* 314 */       if (cal.calibrated()) {
/* 315 */         min = cal.getCValue((int)min);
/* 316 */         max = cal.getCValue((int)max);
/*     */       }
/* 318 */       if ((((int)min == min) && ((int)max == max)) || ((ip instanceof ShortProcessor))) {
/* 319 */         this.label1.setText("" + (int)min);
/* 320 */         this.label2.setText("" + (int)max);
/*     */       } else {
/* 322 */         this.label1.setText("" + IJ.d2s(min, 2));
/* 323 */         this.label2.setText("" + IJ.d2s(max, 2));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void updateScrollBars() {
/* 329 */     this.minSlider.setValue((int)this.minThreshold);
/* 330 */     this.maxSlider.setValue((int)this.maxThreshold);
/*     */   }
/*     */ 
/*     */   void doMasking(ImagePlus imp, ImageProcessor ip)
/*     */   {
/* 335 */     int[] mask = imp.getMask();
/* 336 */     if (mask != null)
/* 337 */       ip.reset(mask);
/*     */   }
/*     */ 
/*     */   void adjustMinThreshold(ImagePlus imp, ImageProcessor ip, double value) {
/* 341 */     if (IJ.altKeyDown()) {
/* 342 */       double width = this.maxThreshold - this.minThreshold;
/* 343 */       if (width < 1.0D) width = 1.0D;
/* 344 */       this.minThreshold = value;
/* 345 */       this.maxThreshold = (this.minThreshold + width);
/* 346 */       if (this.minThreshold + width > 255.0D) {
/* 347 */         this.minThreshold = (255.0D - width);
/* 348 */         this.maxThreshold = (this.minThreshold + width);
/* 349 */         this.minSlider.setValue((int)this.minThreshold);
/*     */       }
/* 351 */       this.maxSlider.setValue((int)this.maxThreshold);
/* 352 */       scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 353 */       return;
/*     */     }
/* 355 */     this.minThreshold = value;
/* 356 */     if (this.maxThreshold < this.minThreshold) {
/* 357 */       this.maxThreshold = this.minThreshold;
/* 358 */       this.maxSlider.setValue((int)this.maxThreshold);
/*     */     }
/* 360 */     scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/*     */   }
/*     */ 
/*     */   void adjustMaxThreshold(ImagePlus imp, ImageProcessor ip, int cvalue) {
/* 364 */     this.maxThreshold = cvalue;
/* 365 */     if (this.minThreshold > this.maxThreshold) {
/* 366 */       this.minThreshold = this.maxThreshold;
/* 367 */       this.minSlider.setValue((int)this.minThreshold);
/*     */     }
/* 369 */     scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/*     */   }
/*     */ 
/*     */   void reset(ImagePlus imp, ImageProcessor ip) {
/* 373 */     this.plot.setHistogram(imp);
/* 374 */     ip.setThreshold(-808080.0D, 0.0D, 0);
/* 375 */     updateScrollBars();
/*     */   }
/*     */ 
/*     */   void doSet(ImagePlus imp, ImageProcessor ip) {
/* 379 */     double level1 = ip.getMinThreshold();
/* 380 */     double level2 = ip.getMaxThreshold();
/* 381 */     if (level1 == -808080.0D) {
/* 382 */       level1 = scaleUp(ip, 65.0D);
/* 383 */       level2 = scaleUp(ip, 255.0D);
/*     */     }
/* 385 */     GenericDialog gd = new GenericDialog("Set Threshold Levels");
/* 386 */     gd.addNumericField("Lower Threshold Level: ", level1, 0);
/* 387 */     gd.addNumericField("Upper Threshold Level: ", level2, 0);
/* 388 */     gd.showDialog();
/* 389 */     if (gd.wasCanceled())
/* 390 */       return;
/* 391 */     level1 = gd.getNextNumber();
/* 392 */     level2 = gd.getNextNumber();
/*     */ 
/* 394 */     if (level2 < level1)
/* 395 */       level2 = level1;
/* 396 */     double minDisplay = ip.getMin();
/* 397 */     double maxDisplay = ip.getMax();
/* 398 */     ip.resetMinAndMax();
/* 399 */     double minValue = ip.getMin();
/* 400 */     double maxValue = ip.getMax();
/* 401 */     if (level1 < minValue) level1 = minValue;
/* 402 */     if (level2 > maxValue) level2 = maxValue;
/* 403 */     boolean outOfRange = (level1 < minDisplay) || (level2 > maxDisplay);
/* 404 */     if (outOfRange)
/* 405 */       this.plot.setHistogram(imp);
/*     */     else {
/* 407 */       ip.setMinAndMax(minDisplay, maxDisplay);
/*     */     }
/* 409 */     this.minThreshold = scaleDown(ip, level1);
/* 410 */     this.maxThreshold = scaleDown(ip, level2);
/* 411 */     scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 412 */     updateScrollBars();
/*     */   }
/*     */ 
/*     */   void apply(ImagePlus imp, ImageProcessor ip) {
/* 416 */     boolean not8Bits = !(ip instanceof ByteProcessor);
/* 417 */     if (not8Bits) {
/* 418 */       double min = ip.getMin();
/* 419 */       double max = ip.getMax();
/* 420 */       ip.setMinAndMax(min, max);
/* 421 */       ip = new ByteProcessor(ip.createImage());
/*     */     }
/*     */ 
/* 424 */     boolean useBlackAndWhite = this.stateB.getLabel().equals("Red");
/* 425 */     if (!useBlackAndWhite) {
/* 426 */       GenericDialog gd = new GenericDialog("Apply Lut", this);
/* 427 */       gd.addCheckbox("Set thresholded pixels to foreground color", fill1);
/* 428 */       gd.addCheckbox("Set remaining pixels to background color", fill2);
/* 429 */       gd.addMessage("");
/* 430 */       gd.addCheckbox("Black forground, white background", useBW);
/* 431 */       gd.showDialog();
/* 432 */       if (gd.wasCanceled())
/* 433 */         return;
/* 434 */       fill1 = gd.getNextBoolean();
/* 435 */       fill2 = gd.getNextBoolean();
/* 436 */       useBW = useBlackAndWhite = gd.getNextBoolean();
/*     */     } else {
/* 438 */       fill1 = true;
/* 439 */       fill2 = true;
/*     */     }
/*     */ 
/* 442 */     Undo.setup(1, imp);
/* 443 */     ip.snapshot();
/* 444 */     reset(imp, ip);
/*     */ 
/* 446 */     int savePixel = ip.getPixel(0, 0);
/*     */ 
/* 448 */     if (useBlackAndWhite)
/* 449 */       ip.setColor(Color.black);
/*     */     else
/* 451 */       ip.setColor(Toolbar.getForegroundColor());
/* 452 */     ip.drawPixel(0, 0);
/* 453 */     int fcolor = ip.getPixel(0, 0);
/* 454 */     if (useBlackAndWhite)
/* 455 */       ip.setColor(Color.white);
/*     */     else
/* 457 */       ip.setColor(Toolbar.getBackgroundColor());
/* 458 */     ip.drawPixel(0, 0);
/* 459 */     int bcolor = ip.getPixel(0, 0);
/* 460 */     ip.setColor(Toolbar.getForegroundColor());
/* 461 */     ip.putPixel(0, 0, savePixel);
/*     */ 
/* 463 */     int[] lut = new int[256];
/* 464 */     for (int i = 0; i < 256; i++) {
/* 465 */       if ((i >= this.minThreshold) && (i <= this.maxThreshold))
/* 466 */         lut[i] = (fill1 ? fcolor : (byte)i);
/*     */       else {
/* 468 */         lut[i] = (fill2 ? bcolor : (byte)i);
/*     */       }
/*     */     }
/* 471 */     if (not8Bits) {
/* 472 */       ip.applyTable(lut);
/* 473 */       new ImagePlus(imp.getTitle(), ip).show();
/*     */     }
/* 475 */     if (imp.getStackSize() > 1) {
/* 476 */       ImageStack stack = imp.getStack();
/* 477 */       YesNoCancelDialog d = new YesNoCancelDialog(this, "Entire Stack?", "Apply threshold to all " + stack.getSize() + " slices in the stack?");
/*     */ 
/* 479 */       if (d.cancelPressed())
/* 480 */         return;
/* 481 */       if (d.yesPressed())
/* 482 */         new StackProcessor(stack, ip).applyTable(lut);
/*     */       else
/* 484 */         ip.applyTable(lut);
/*     */     } else {
/* 486 */       ip.applyTable(lut);
/* 487 */     }imp.changes = true;
/* 488 */     if (this.plot.histogram != null)
/* 489 */       this.plot.setHistogram(imp);
/*     */   }
/*     */ 
/*     */   void changeState(ImagePlus imp, ImageProcessor ip)
/*     */   {
/* 496 */     scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 497 */     updateScrollBars();
/*     */   }
/*     */ 
/*     */   void autoThreshold(ImagePlus imp, ImageProcessor ip) {
/* 501 */     if (!(ip instanceof ByteProcessor))
/* 502 */       return;
/* 503 */     ImageStatistics stats = imp.getStatistics(24);
/* 504 */     int threshold = ((ByteProcessor)ip).getAutoThreshold();
/* 505 */     if (stats.max - stats.mode < stats.mode - stats.min) {
/* 506 */       this.minThreshold = stats.min;
/* 507 */       this.maxThreshold = threshold;
/*     */     } else {
/* 509 */       this.minThreshold = threshold;
/* 510 */       this.maxThreshold = stats.max;
/*     */     }
/* 512 */     scaleUpAndSet(ip, this.minThreshold, this.maxThreshold);
/* 513 */     updateScrollBars();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 520 */     while (!this.done) {
/* 521 */       synchronized (this) {
/*     */         try { wait(); } catch (InterruptedException e) {
/*     */         }
/*     */       }
/* 525 */       doUpdate();
/*     */     }
/*     */   }
/*     */ 
/*     */   void doUpdate()
/*     */   {
/* 533 */     int min = this.minValue;
/* 534 */     int max = this.maxValue;
/*     */     int action;
/*     */     int action;
/* 535 */     if (this.doReset) { action = 0;
/*     */     }
/*     */     else
/*     */     {
/* 536 */       int action;
/* 536 */       if (this.doAutoAdjust) { action = 1;
/*     */       }
/*     */       else
/*     */       {
/* 537 */         int action;
/* 537 */         if (this.doApplyLut) { action = 3;
/*     */         }
/*     */         else
/*     */         {
/* 538 */           int action;
/* 538 */           if (this.doStateChange) { action = 4;
/*     */           }
/*     */           else
/*     */           {
/* 539 */             int action;
/* 539 */             if (this.doSet) { action = 7;
/*     */             }
/*     */             else
/*     */             {
/* 540 */               int action;
/* 540 */               if (this.minValue >= 0) action = 5;
/* 541 */               else if (this.maxValue >= 0) action = 6; else
/* 542 */                 return; 
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 543 */     this.minValue = -1;
/* 544 */     this.maxValue = -1;
/* 545 */     this.doReset = false;
/* 546 */     this.doAutoAdjust = false;
/* 547 */     this.doApplyLut = false;
/* 548 */     this.doStateChange = false;
/* 549 */     this.doSet = false;
/* 550 */     ImagePlus imp = WindowManager.getCurrentImage();
/* 551 */     if (imp == null) {
/* 552 */       IJ.beep();
/* 553 */       IJ.showStatus("No image");
/* 554 */       return;
/*     */     }
/* 556 */     if (!imp.lock()) {
/* 557 */       imp = null; return;
/* 558 */     }ImageProcessor ip = setup(imp);
/* 559 */     if (ip == null) {
/* 560 */       imp.unlock();
/* 561 */       IJ.beep();
/* 562 */       IJ.showStatus("RGB images cannot be thresolded");
/* 563 */       return;
/*     */     }
/*     */ 
/* 566 */     switch (action) { case 0:
/* 567 */       reset(imp, ip); break;
/*     */     case 1:
/* 568 */       autoThreshold(imp, ip); break;
/*     */     case 3:
/* 569 */       apply(imp, ip); break;
/*     */     case 4:
/* 570 */       changeState(imp, ip); break;
/*     */     case 7:
/* 571 */       doSet(imp, ip); break;
/*     */     case 5:
/* 572 */       adjustMinThreshold(imp, ip, min); break;
/*     */     case 6:
/* 573 */       adjustMaxThreshold(imp, ip, max);
/*     */     case 2: }
/* 575 */     updatePlot();
/* 576 */     updateLabels(imp, ip);
/* 577 */     ip.setLutAnimation(true);
/* 578 */     imp.updateAndDraw();
/* 579 */     imp.unlock();
/*     */   }
/*     */ 
/*     */   public void processWindowEvent(WindowEvent e) {
/* 583 */     super.processWindowEvent(e);
/* 584 */     if (e.getID() == 201)
/* 585 */       instance = null;
/*     */   }
/*     */ 
/*     */   protected synchronized void done()
/*     */   {
/* 590 */     this.done = true;
/*     */ 
/* 592 */     instance = null;
/* 593 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public synchronized void syncAdjust() throws InterruptedException {
/* 597 */     while (!this.done)
/* 598 */       wait();
/*     */   }
/*     */ }

/* Location:           /Users/todd/Projects/puncta-analyzer/v1/PunctaAnalyzer/
 * Qualified Name:     ThresholdMasker
 * JD-Core Version:    0.6.0
 */